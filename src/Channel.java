import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.Set;

/** The Channel class represents each CHannel on the IRC Server,
 *  which clients can send messages to, that will be recieved by every
 *  user in the channel.
 */
public class Channel {
    /** Maps each Channel to a Set of all Clients connected to a Channel. */
    private static final LinkedHashMap<Channel, LinkedHashSet<ClientThread>> CHANNEL_CLIENTS
        = new LinkedHashMap<Channel, LinkedHashSet<ClientThread>>();
    /** Maps channel names to channel objects. */
    public static final LinkedHashMap<String, Channel> CHANNEL_NAMES
        = new LinkedHashMap<String, Channel>();
    /** Name of a channel. */
    private String name;
    /** A set of all clients connected to an instance of Channel. */
    private LinkedHashSet<ClientThread> clients;

    /** Creates a new channel, taking the name of the CHannel to be created.
     *  @param name Name of the channel
     */
    public Channel(String name) {
        this.name = name;
        CHANNEL_NAMES.put(name, this);
        clients = new LinkedHashSet<ClientThread>();
        CHANNEL_CLIENTS.put(this, clients);
    }

    /** Adds a client to a channel.
     *  @param client The client to be added to this channel
     */
    public void addClient(ClientThread client) {
        clients.add(client);
    }

    /** Removes a client from this channel.
     *  @param client The client to be removed from this channel
     */
    public void removeClient(ClientThread client) {
        clients.remove(client);
    }
    /** Notifies all clients in a channel of a message, used as a helper function
     *  by commands JOIN and PART.
     *  @param client  The client leaving or joining a channel
     *  @param command The command that will be shown in the message to each
     *                 connected client, will be either JOIN or PART
     */
    public void notifyAll(ClientThread client, String command) {
        LinkedHashSet<ClientThread> clients = CHANNEL_CLIENTS.get(this);
        for (ClientThread cl : clients) {
            cl.printMessage(":" + client.getNickName() + " " + command + " " + name);
        }
    }

    /** Sends a message to every client in this channel.
     *  @param message The message to be sent to all clients in this channel
     */
    public void sendMessage(String message) {
        for (ClientThread cl : CHANNEL_CLIENTS.get(this)) {
            cl.printMessage(message);
        }
    }

    /** Determines if a channel exists by checking its name.
     *  @param  channelName The name of the channel that may exist
     *  @return True if the provided name corresponds to a channel, False otherwise
     */
    public static boolean channelExists(String channelName) {
        return CHANNEL_NAMES.containsKey(channelName);
    }

    /** Determines if a client is in a channel.
     *  @param client       The client to whose presence will be checked
     *  @param channelName  The name of a channel, which will be validated
     *  @return True if a channel corresponds to the given name, and the
     *          client is in this channel, False otherwise
     */
    public static boolean clientInChannel(ClientThread client, String channelName) {
        if (!channelExists(channelName)) {
            return false;
        }
        Channel channel = CHANNEL_NAMES.get(channelName);
        LinkedHashSet<ClientThread> clients = CHANNEL_CLIENTS.get(channel);
        return clients.contains(client);
    }

    /** Returns the name of a channel.
     *  @return Name of a channel
     */
    public String getName() {
        return name;
    }
    /** Returns the names of all channels.
     *  @return A set of all channel names
     */
    public static Set<String> getChannelNames() {
        return CHANNEL_NAMES.keySet();
    }
    /** Gets a channel from its name.
     *  @param channelName The name of the channel to be retrieved
     *  @return The Channel with the provided name
     */
    public static Channel getChannelByName(String channelName) {
        return CHANNEL_NAMES.get(channelName);
    }

    /** Gets the Set of all clients connected to a channel.
     *  @param channel The channel to get all clients from
     *  @return The Set of clients connected to the given channel
     */
    public static LinkedHashSet<ClientThread> getClients(Channel channel) {
        return CHANNEL_CLIENTS.get(channel);
    }

}
