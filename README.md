# GroupChat
# The Kutaisi International University assignment of Fundamentals of programming (FoP) by Nikoloz Aneli
Requirements
General
  1. you are going to realize a multi-user chat by means of sockets allows an arbitrary (but not too large, i.e., less than 50) number of clients to exchange messages.
	2. your server may be started in two possible ways: java ChatServer and java ChatServer <port> where in the first case, the port number is set to 3000 by default.
	3. the clients may as well be started in two ways: java ChatClient and java ChatClient [portNumber] [serverAddress] where in the first case by default, localhost:3000 is used.
	4. The first message after a client has connected to the server, only consists of a String representing the username of this participant.
	5. altogether there are five kinds of messages:
			@username<blank>message sends a DM to the respective client and only this client. When there is no client with this name known to the server, the sender just receives a corresponding error message by the server.
			If the client sends WHOIS, (s)he and only (s)he, receives a list of all currently connected clients and since when they are connected.
			If a client sends LOGOUT, the connection of this client is closed and all streams and of both sides are also closed.
			If a client sends PINGU, all currently connected clients receive an important fact about penguins (what ever that might be :)).
			All other messages are considered as ordinary messages and sent to all connected clients.
You are invited to introduce further functionality! If you do this, you may send every client, once (s)he has connected send a welcome message listing all available functionality.
Since you have significantly more freedom here, we will also correct significantly more liberal here.
Server
		You should implement a ChatServer which accepts connections via Sockets.
For each established connection, create an independent Thread which is responsible for the communication with the given client.
For each established connection, a message is sent to all client that a new user has joined the chat.
Your server should maintain a data-structure of all currently connected clients (do not omit synchronization here!).
If the Socket of a client is closed or if an IO error occurred when sending a message, the corresponding client is removed from the data-structure.
Client
		You should implement a ChatClient who by means of a Socket establishes a connection to the ChatServer.
The client is meant to prompt all messages on the console, and at the same time should allow the user to write own messages.
The client may choose a fresh username for each session herself.
![image](https://user-images.githubusercontent.com/77580098/218210458-81b94208-b667-41e7-a7c5-5bf4d7bb3f8d.png)
