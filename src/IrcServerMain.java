/** Main class which will serve as the entry point to the program. */
public class IrcServerMain {
    /** Checks if the program arguments provided are valid for starting the program.
     *  @param  args Command line arguments provided by the user
     *  @throws IllegalArgumentException if the provided arguments are invalid
     */
    private static void validateArguments(String[] args) throws IllegalArgumentException {
        if (args.length != 2) {
            throw new IllegalArgumentException();
        }

        final int minPortNumber = 1025;
        final int maxPortNumber = 65536;

        int portNumber = Integer.parseInt(args[1]);
        if ((portNumber < minPortNumber && portNumber > maxPortNumber)) {
            throw new IllegalArgumentException();
        }

    }

    /** Entry point to the program, where agruments are verified and an
     *  IrcServer is instantiated.
     *  @param args Command line arguments passed to the program by the user
     */
    public static void main(String[] args) {
        try {
            validateArguments(args);
            String name = args[0];
            int portNumber = Integer.parseInt(args[1]);
            IrcServer server = new IrcServer(name, portNumber);
            server.start();

        } catch (IllegalArgumentException e) {
            System.out.println("Usage: java IrcServerMain <server_name> <port>");
        }
    }

}
