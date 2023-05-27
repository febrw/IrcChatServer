# IRC Chat Server

This project implements a simplified version of the Internet Relay Chat (IRC) protocol. Users can create channels, join other channels, broadcast messages to a channel, or privately message other users.

## Compiling and Running

Within the src directory run:

```bash
javac *.java
```

To start the server with the name MyServer, on port 12345:

```bash
java IrcServerMain MyServer 12345
```


To connect from a client terminal, run:
```bash
telnet localhost 21345
```


## Commands Implemented

####  NICK

Usage: 
```bash
NICK <nickname>
```

This message is sent by the client in order to declare what nickname the user wants to be known
by.

#### USER

Usage:
```bash
USER <username> 0 * :<real_name>
```

This command can be used once a user has set their nickname. A user may then enter their real name and become registered in the system. A registered client can send and recieve private messages, and join and leave channels.


#### QUIT

This command takes no arguments, and is used to disconnect from the server. If the user is registered, all other users are notified of this user disconnecting. The user will be removed from any channels they are in.

#### JOIN

Usage:
```bash
JOIN <channel_name>
````

This command allows a user to JOIN a channel, all users in this channel will be notified that a new user has joined. If the channel does not yet exist, it is created, and the creator is added. A channel name must be a single \# symbol followed by the channel name.


#### PART

Usage:
```bash
PART <channel_name>
```

This message is sent by a client when they wish to leave a channel they are in. All other users in the channel will be notified that this user has left the channnel. If the channel is now empty, it will be deleted.

#### PRIVMSG

Usage:
```bash
PRIVMSG <target> :<message>
```

This command allows a user to send a private message to other users. If the \<lttarget> argument is a user's nickname, this user will be the sole recipient of the message. If the \<lttarget> argument is a channel name, all users in that channel will recieve the message.

#### NAMES

Usage:
```bash
NAMES <channel_name>
```
If the channel name provided is valid, the server will reply with the list of nicknames of all registered users in that channel.

#### LIST

This command takes no arguments. It allows registered users to view the full list of channel names.

#### TIME

This command takes no arguments. It simply responds with the current date and time in ISO 8601 format.

#### INFO

This command takes no arguments. It responds with a message about the server, and who wrote it.

#### PING

Usage:
```bash
PING <text>
```

This command will prompt a server response: PONG \<lttext>. This can be used for clients to ensure their connection is still active.
