import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.util.HashSet;
import java.net.Socket;

/** This class represents the connection thread that allows clients to connect
 *  to an IrcServer.
 */
public class ClientThread implements Runnable {

    private IrcServer server;
    private Socket connection;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BufferedReader reader;
    private PrintWriter writer;

    private String nickName;
    private String userName;
    private String realName;
    private HashSet<Channel> channels;

    /** Creates a new ClientThread, with a server and socket.
     * @param server     The IrcServer that this ClientThread will connect to
     * @param connection The socket which will be used to connect to the IrcServer
     */
    public ClientThread(IrcServer server, Socket connection) {
        this.server = server;
        this.connection = connection;
        server.addClientThread(this);
        channels = new HashSet<Channel>();
    }

    /** Runs a thread for this ClientThread, allowing
     *  the client to interact with the IrcServer.
     */
    public void run() {
        try {
            inputStream = connection.getInputStream();
            outputStream = connection.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            writer = new PrintWriter(outputStream, true);

            for (;;) {
                String userInput = reader.readLine();
                Command command = new Command(userInput, this);
                command.process();
            }
        }
        catch (IOException ioe) {
            System.out.println("ioe1");
        }

    }

    /** Prints a message to the client's terminal.
     * @param message The messsage to write to the client's terminal
     */
    public void printMessage(String message) {
        writer.println(message);
    }

    /** Returns the writer object.
     *  @return This ClientThread's writer
     */
    public PrintWriter getWriter() {
        return writer;
    }

    /** Returns the reader object.
     *  @return This ClientThread's reader.
     */
    public BufferedReader getReader() {
        return reader;
    }

    /** Sets the client's nickname to the provided nickname.
     *  @param nickName The nickname to be set
     */
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    /** Returns the client's nickname.
     * @return This client's nickname
     */
    public String getNickName() {
        return nickName;
    }

    /** Returns the IrcServer that this ClientThread is connected to.
     * @return This ClientThread's IrcServer
     */
    public IrcServer getIrcServer() {
        return server;
    }

    /** Returns the name of the IrcServer that this ClientThread is connected to.
     *  @return IrcServer's name
     */
    public String getIrcServerName() {
        return server.getName();
    }

    /** Sets the client's real name.
     *  @param realName The client's real name
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /** Sets the client's user name.
     *  @param userName The client's chosen user name.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /** Gets the client's user name.
     *  @return The client's user name
     */
    public String getUserName() {
        return userName;
    }

    /** Determines if this client is registered with the IrcServer.
     *  @return True if the client's user name is set, False otherwise.
     */
    public boolean isRegistered() {
        return (userName != null);
    }

    /** Gets the socket connection of this ClientThread.
     *  @return This ClientThread's socket
     */
    public Socket getSocket() {
        return connection;
    }

    /** Adds a channel to this client's set of channels.
     *  @param channel The channel to be added for this client
     */
    public void addToChannel(Channel channel) {
        channels.add(channel);
    }

    /** Removes a channel from this client's set of channels.
     *  @param channel The channel to be removed from this client's set of channels
     */
    public void removeFromChannel(Channel channel) {
        channels.remove(channel);
    }

    /** Gets the set of channels that a client is connected to.
     *  @return The set of channels this client is connected to
     */
    public HashSet<Channel> getChannels() {
        return channels;
    }

    /** Closes all streams, and the reader, writer, and socket of this ClientThread. */
    public void closeAll() throws IOException {
        inputStream.close();
        outputStream.close();
        reader.close();
        writer.close();
        connection.close();
    }

}
