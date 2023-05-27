import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

/** This class represents the commands that will be sent by a client to an IrcServer. */
public class Command {

    private String command;
    private String messageArgs;
    private ClientThread client;

    /** Creates a new Command, with the client's input string, and their ClientThread object.
     *  @param userInput The command and arguments issued by the user
     *  @param client    The ClientThread object that sent the command and arguments
     */
    public Command(String userInput, ClientThread client) {
        String[] separatedMessage = userInput.split(" ", 2);
        command = separatedMessage[0];
        messageArgs = (separatedMessage.length > 1) ? separatedMessage[1] : "";
        this.client = client;
    }

    /** Process the command, based on the command string that occurs at the beginning
     *  of the client's input.
     */
    public void process() {
        switch (command) {
            case "NICK":
                nick();
                break;
            case "USER":
                user();
                break;
            case "QUIT":
                quit();
                break;
            case "JOIN":
                join();
                break;
            case "PART":
                part();
                break;
            case "NAMES":
                names();
                break;
            case "LIST":
                list();
                break;
            case "PRIVMSG":
                privateMessage();
                break;
            case "TIME":
                time();
                break;
            case "INFO":
                info();
                break;
            case "PING":
                ping();
                break;
            default:
                client.printMessage("Invalid command, try again.");
                break;
        }
    }

    private void ping() {
        client.printMessage("PONG " + messageArgs);
    }
    private void nick() {
        if (Pattern.matches("[a-zA-z_]{1}\\w{0,8}", messageArgs)) {
            client.setNickName(messageArgs);
            client.getIrcServer().addNamedClient(client.getNickName(), client);
        }
        else {
            String nickError = createErrorMessage("Invalid nickname");
            client.printMessage(nickError);
        }
    }

    private void user() {
        // Nickname not yet set
        if (client.getNickName() == null) {
            String noNickNameError = createErrorMessage("You must have a nickname before you can register.");
            client.printMessage(noNickNameError);
            return;
        }

        String args = messageArgs.split(":", 2)[0];
        String realName = messageArgs.split(":", 2)[1];

        // real name is empty, or no colon supplied before it
        if (realName == "") {
            client.printMessage(createErrorMessage("Invalid arguments to USER command"));
            return;
        }
        // incorrect number of arguments given
        String[] userNameArgs = args.split(" ");
        final int correctNumberOfArguments = 3;
        if (userNameArgs.length != correctNumberOfArguments) {
            client.printMessage(createErrorMessage("Not enough arguments"));
            return;
        }

        String userName = userNameArgs[0];
        // check username not already set
        if (client.getUserName() != null) {
            client.printMessage(createErrorMessage("You are already registered"));
            return;
        }

        // check redundant arguments match
        boolean middleArgsMatch = userNameArgs[1].equals("0") && userNameArgs[2].equals("*");
        //client.printMessage("test1: " + middleArgsMatch);
        //client.printMessage("test2: " + Pattern.matches("^[\\s]", userName));
        if (Pattern.matches("[^\\s]+", userName) && middleArgsMatch) {
            client.setUserName(userName);
            client.setRealName(realName);
            String registrationReply = ":" + client.getIrcServerName() + " 001 " + client.getNickName()
                + " :Welcome to the IRC network, " + client.getNickName();
            client.printMessage(registrationReply);
        }
    }

    private void quit() {
        try {
            if (client.isRegistered()) {
            // TODO remove from channels
                HashSet<ClientThread> connectedClients = client.getIrcServer().getConnectedClients();
                final String quitMessage = ":" + client.getNickName() + " QUIT";

                for (ClientThread cc : connectedClients) {
                    cc.printMessage(quitMessage);
                }

                for (Channel myChannel : client.getChannels()) {
                    client.removeFromChannel(myChannel);
                    myChannel.removeClient(client);
                }
            }
            client.closeAll();
            client.getIrcServer().removeClientThread(client);
        } catch (IOException ioe) {
            System.out.println("Connections and streams could not be closed.");
        }
    }

    private void join() {
        final String channelName = messageArgs;
        final String channelNameRegex = "^#[\\w]+";

        if (!client.isRegistered()) {
            client.printMessage(createErrorMessage("You need to register first"));
            return;
        }

        if (!Pattern.matches(channelNameRegex, channelName)) {
            client.printMessage(createErrorMessage("Invalid channel name"));
            return;
        }
        // create channel if it does not exist
        Channel channel;
        if (!Channel.channelExists(channelName)) {
            channel = new Channel(channelName);
        } else {
            channel = Channel.CHANNEL_NAMES.get(channelName);
        }

        channel.addClient(client);
        client.addToChannel(channel);
        channel.notifyAll(client, command);
    }

    private void part() {
        final String channelName = messageArgs;
        // check channel exists
        if (!Channel.channelExists(channelName)) {
            client.printMessage(createErrorMessage("No channel exists with that name"));
            return;
        }
        // check registration
        if (!client.isRegistered()) {
            client.printMessage(createErrorMessage("You need to register first"));
            return;
        }

        if (Channel.clientInChannel(client, channelName)) {
            Channel channel = Channel.CHANNEL_NAMES.get(channelName);
            channel.notifyAll(client, command);
            channel.removeClient(client);
        }
    }

    private void privateMessage() {

        if (!client.isRegistered()) {
            client.printMessage(createErrorMessage("You need to register first"));
            return;
        }
        if (!messageArgs.contains(":")) {
            client.printMessage(createErrorMessage("Invalid arguments to PRIVMSG command"));
            return;
        }

        String[] args = messageArgs.split(":", 2);
        String target = args[0].trim();
        String message = args[1];

        // check channels
        final String channelNameRegex = "^#[\\w]+";
        if (Pattern.matches(channelNameRegex, target)) {
            Channel channel = Channel.CHANNEL_NAMES.get(target);
            if (channel != null) {
                channel.sendMessage(":" + client.getNickName() + " " + command
                + " " + channel.getName() + " :" + message);
            }
            else {
                client.printMessage(createErrorMessage("No channel exists with that name"));
                return;
            }
        }

        // check users
        ClientThread recipient = client.getIrcServer().getNamedClient(target);
        System.out.println(target);
        if (recipient == null) {
            client.printMessage(createErrorMessage("No user exists with that name"));
            return;
        }
        else {
            recipient.printMessage(":" + client.getNickName() + " "
                + command + " " + recipient.getNickName() + " :" + message);
        }

    }

    private void list() {
        for (String channelName : Channel.getChannelNames()) {
            client.printMessage(":" + client.getIrcServerName() + " 322 "
                + client.getNickName() + " " + channelName);
        }
        client.printMessage(":" + client.getIrcServerName() + " 323 "
                + client.getNickName() + " :End of LIST");
    }

    private void names() {
        String channelName = messageArgs;
        Channel channel = Channel.getChannelByName(channelName);
        LinkedHashSet<ClientThread> clients = Channel.getClients(channel);

        ArrayList<String> clientNickNames = new ArrayList<String>();

        for (ClientThread cl: clients) {
            clientNickNames.add(cl.getNickName());
        }

        final String nickNames = clientNickNames.stream().collect(Collectors.joining(" "));
        client.printMessage(":" + client.getIrcServerName() + " 353 "
            + client.getNickName() + " = " + channelName + " :" + nickNames);
    }

    private void time() {
        LocalDateTime now = LocalDateTime.now();
        client.printMessage(now.toString());
        System.out.println(now.toString());
        String nickName = (client.getNickName() == null) ? "*" : client.getNickName();
        client.printMessage(":" + client.getIrcServerName() + " 391 "
            + nickName + " :" + now);
    }

    private void info() {
        final String infoMessage = "This server is the largest program I've ever witten - 210025499";
        String nickName = (client.getNickName() == null) ? "*" : client.getNickName();
        client.printMessage(":" + client.getIrcServerName() + " 371 " + nickName + " :" + infoMessage);
    }

    private String createErrorMessage(String errorText) {
        String errorMessage = ":" + client.getIrcServerName() + " 400" + " * :" + errorText;
        if (client.getNickName() != null) {
            errorMessage = errorMessage.replace("*", client.getNickName());
        }
        return errorMessage;
    }

}
