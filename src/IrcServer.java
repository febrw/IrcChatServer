import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;

/** This class represents an IrcServer which will allow clients to send message and join channels. */
public class IrcServer {
    private String name;
    private int portNumber;
    private ServerSocket serverSocket;

    private HashSet<ClientThread> connectedClients;
    private HashMap<String, ClientThread> namedClients;

    /** Creates a new IrcServer instance with a name and port number
     *  on which the server accepts connections.
     *  @param name         The name of the IrcServer
     *  @param portNumber   The port on which this IrcServer will listen for connections
     */
    public IrcServer(String name, int portNumber) {
        this.name = name;
        this.portNumber = portNumber;
        connectedClients = new HashSet<ClientThread>();
        namedClients = new HashMap<String, ClientThread>();
    }
    /** Starts running the IrcServer's connection, listening for clients. */
    public void start() {
        try {
            serverSocket = new ServerSocket(portNumber);
            for (;;) {
                Socket connection = serverSocket.accept();

                ClientThread client = new ClientThread(this, connection);
                Thread thread = new Thread(client);
                thread.start();
            }
        }

        catch (IOException ioe) {
            System.out.println("Server socket could not be established.");
        }
    }
    /** Gets the name of the IrcServer.
     *  @return The name of this IrcServer
     */
    public String getName() {
        return name;
    }
    /** Removes a client from this IrcServer.
     *  @param client The client to be forgotten
     */
    public void removeClientThread(ClientThread client) {
        connectedClients.remove(client);
    }

    /** Adds a client to this IrcServer.
     *  @param client The client to be added to this IrcServer
     */
    public void addClientThread(ClientThread client) {
        connectedClients.add(client);
    }
    /** Adds a client to this server, along with their nickname.
     *  @param nickName The assigned nickName this client chose
     *  @param client   The client to be added to the IrcServer
     */
    public void addNamedClient(String nickName, ClientThread client) {
        namedClients.put(nickName, client);
    }

    /** Gets a client by their name.
     *  @param  clientName The name of the client to be retrieved
     *  @return The client who's name matches the provided name
     */
    public ClientThread getNamedClient(String clientName) {
        return namedClients.get(clientName);
    }

    /** Gets the Set of all clients connected to this IrcServer.
     *  @return All clients connected to this IrcServer
     */
    public HashSet<ClientThread> getConnectedClients() {
        return connectedClients;
    }
}
