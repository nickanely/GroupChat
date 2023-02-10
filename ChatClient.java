package fop.w11pchat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Scanner;

public class ChatClient {
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;

    private String username;
    private int port;


    public ChatClient(String username, int port) {
        this.username = username;
        this.port = port;
    }
    public static void main(String[] args) {
        int portNum = 7896;

        Scanner scan = new Scanner(System.in);

        System.out.println("Enter the username: ");
        String userName = scan.nextLine();


        ChatClient client = new ChatClient(userName, portNum);
        client.start();

        System.out.println("\nHello.! Welcome to the chatroom. \n" +
                "Instructions: \n" +
                "1. Simply type the message to send broadcast to all active clients \n" +
                "2. Type @username<space>yourmessage without quotes to send message to desired client \n" +
                "3. Type WHOIS without quotes to see list of active clients \n" +
                "4. Type LOGOUT without quotes to logoff from server \n" +
                "5. Type PINGU without quotes to request a random penguin fact \n");

        while (true) {
            System.out.println("> ");
            String message = scan.nextLine();
            try {
                if (message.equalsIgnoreCase("LOGOUT")) {

                    client.out.writeObject( new EnumMessage(MessType.LOGOUT,""));
                    break;

                } else if (message.equalsIgnoreCase("WHOIS"))

                    client.out.writeObject(new EnumMessage(MessType.WHOIS, ""));

                else if (message.equalsIgnoreCase("PINGU"))

                    client.out.writeObject(new EnumMessage(MessType.PINGU, ""));

                else {

                    client.out.writeObject(new EnumMessage(MessType.MESSAGE, message));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        scan.close();
        client.disconnect();
    }
    public void start() {
        try {
            socket = new Socket("localhost", port);

            System.out.println(LocalTime.now() + " Connection accepted " + socket.getInetAddress() + " : " + socket.getPort());

            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());


            listen();


            out.writeObject(username);

        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }
    public void listen() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {

                        String message = (String) in.readObject();

                        System.out.println(message);
                        System.out.print("> ");

                    } catch (IOException | ClassNotFoundException e) {
                        disconnect();
                        break;
                    }
                }
            }
        }).start();
    }
    private void disconnect() {
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

}
