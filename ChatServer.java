package fop.w11pchat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class ChatServer {
    private static long nextId;
    private static List<ClientHandler> clientHandlers;
    private int port;
    private boolean online = true;


    public ChatServer(int port) {
        this.port = port;
        clientHandlers = Collections.synchronizedList(new ArrayList<>());
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);

            while (online) {
                System.out.println(LocalTime.now() + " Server is waiting on port " + port);
                Socket socket = serverSocket.accept();

                if (online == false) break;

                ClientHandler ct = new ClientHandler(socket);
                clientHandlers.add(ct);

                ct.start();
            }
            serverSocket.close();

            int i = 0;
            while (i < clientHandlers.size()) {
                try {
                    clientHandlers.get(i).out.close();
                    clientHandlers.get(i).in.close();
                    clientHandlers.get(i).socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        int portNr = 7896;
        ChatServer cs = new ChatServer(portNr);
        cs.start();
    }

    static class ClientHandler extends Thread {
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
        long id;
        String userName;
        EnumMessage mess;
        String date;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            id = nextId++;

            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                userName = (String) in.readObject();
                System.out.println(LocalTime.now() + "  *** " + userName + " has joined the chat room.  *** ");
                messageToEveryone(LocalTime.now() + "  *** " + userName + " has joined the chat room.  *** ");

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            date = LocalTime.now().toString();
        }


        @Override
        public void run() {
            boolean online = true;

            while (online) {
                try {
                    mess = (EnumMessage) in.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }


                String message = mess.getMsg();
                // String[] splited = message.split(" ");


                if (mess.getType() == MessType.MESSAGE) {
                    message = mess.getMsg();
                    String[] splited = message.split(" ");
                    String userToDM = splited[0].substring(1);


                    //DM message
                    if (splited[0].charAt(0) == '@') {
                        for (ClientHandler ch : clientHandlers) {
                            if (userToDM.equalsIgnoreCase(ch.userName)) {
                                try {
                                    ch.out.writeObject(LocalTime.now() + " " + message);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                    // normal message
                    else {
                        try {
                            System.out.print(LocalTime.now() + " " + userName + ": " + message + "\n");
                            messageToEveryone(message);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }


                if (mess.getType() == MessType.WHOIS) {

                    try {
                        out.writeObject("List of the users connected at " + LocalTime.now() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    int i = 0;
                    while (i < clientHandlers.size()) {
                        try {
                            out.writeObject((i + 1) + ") " + clientHandlers.get(i).userName + " since " + clientHandlers.get(i).date);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        i++;
                    }


                }
                if (mess.getType() == MessType.LOGOUT) {
                    System.out.println(LocalTime.now() + userName + " disconnected with a LOGOUT message.");
                    online = false;

                }
                if (mess.getType() == MessType.PINGU) {
                    int j = 0;
                    while (j < clientHandlers.size()) {
                        try {
                            clientHandlers.get(j).out.writeObject("PinguFacts: We all hate Penguins... especially Emperor one - Mr. Helmut ");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        j++;
                    }
                }
            }


            String lost = "";
            int k = 0;
            while (k < clientHandlers.size()) {
                if (clientHandlers.get(k).id == id) {
                    lost = clientHandlers.get(k).getUserName();
                    clientHandlers.remove(k);
                    break;
                }
                k++;
            }

            try {
                messageToEveryone(LocalTime.now() + " *** " + lost + " has left the chat room. *** ");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        public void messageToEveryone(String message) throws IOException {
            if (message != null) {
                for (ClientHandler clientHandler : clientHandlers) {
                    clientHandler.out.writeObject(userName + ": " + message);
                    clientHandler.out.flush();
                }
            }
        }

        public String getUserName() {
            return userName;
        }
    }
}


    
