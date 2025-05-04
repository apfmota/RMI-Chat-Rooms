import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientController {

    private final JFrame mainFrame;
    private JPanel roomsPanel;
    private IServerChat serverChat;
    private String serverAddress;
    private UserChat userChat;
    private IRoomChat roomChat;
    private JPanel roomMessages;

    public JPanel getRoomMessages() {
        return roomMessages;
    }

    public static void main(String[] args) {
        ClientController clientController = new ClientController();
    }

    public ClientController() {
        this.mainFrame = new JFrame();
        this.mainFrame.setTitle("RMI Chat - Client GUI");
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainFrame.setSize(650, 750);
        JPanel mainPanel = new JPanel();
        this.mainFrame.add(mainPanel);

        JPanel serverConnectionPanel = new JPanel();
        serverConnectionPanel.setLayout(new BorderLayout());
        serverConnectionPanel.setSize(new Dimension(200, 100));

        JPanel serverNamePanel = new JPanel();
        serverNamePanel.setLayout(new BoxLayout(serverNamePanel, BoxLayout.Y_AXIS));
        JTextField serverNameField = new JTextField(15);
        JLabel serverNameLabel = new JLabel("Server address");
        serverNamePanel.add(serverNameLabel);
        serverNamePanel.add(serverNameField);
        serverConnectionPanel.add(serverNamePanel, BorderLayout.CENTER);

        JPanel userNamePanel = new JPanel();
        userNamePanel.setLayout(new BoxLayout(userNamePanel, BoxLayout.Y_AXIS));
        JLabel userNameLabel = new JLabel("User name");
        userNamePanel.add(userNameLabel);
        JTextField userNameField = new JTextField(15);
        userNamePanel.add(userNameField);
        serverConnectionPanel.add(userNamePanel, BorderLayout.WEST);

        JLabel connectionFailMessage = new JLabel("A conexão falhou");
        serverConnectionPanel.add(connectionFailMessage, BorderLayout.SOUTH);
        connectionFailMessage.setVisible(false);
        JButton connectButton = new JButton("Connect");
        connectButton.addActionListener(e -> {
            connectToServer(serverNameField.getText(), userNameField.getText(), connectionFailMessage);
        });
        serverConnectionPanel.add(connectButton, BorderLayout.EAST);
        mainPanel.add(serverConnectionPanel, BorderLayout.CENTER);
        this.mainFrame.setVisible(true);
    }

    private void connectToServer(String serverAdress, String userName, JLabel connectionFailedMessage) {
        try {
            this.serverChat = (IServerChat) Naming.lookup("rmi://" + serverAdress + ":2020/Servidor");
            this.userChat = new UserChat(userName, this);
            this.serverAddress = serverAdress;
            showRooms();
        } catch (Exception e) {
            connectionFailedMessage.setVisible(true);
        }
    }

    private void showRooms() throws RemoteException {
        this.mainFrame.getContentPane().removeAll();
        JPanel mainPanel = new JPanel();

        this.roomsPanel = new JPanel();
        this.roomsPanel.setLayout(new BorderLayout());
        this.roomsPanel.setSize(new Dimension(400, 400));

        JPanel selectRoomsPanel = new JPanel();
        selectRoomsPanel.setLayout(new BoxLayout(selectRoomsPanel, BoxLayout.Y_AXIS));
        roomsPanel.add(selectRoomsPanel);

        for (String roomName: serverChat.getRooms()) {
            JPanel roomPanel = new JPanel();
            roomPanel.setLayout(new BorderLayout());
            roomsPanel.add(roomPanel);
            JLabel roomNameLabel = new JLabel(roomName);
            roomPanel.add(roomNameLabel, BorderLayout.WEST);
            JButton joinButton = new JButton("Join");
            roomPanel.add(joinButton, BorderLayout.EAST);
            joinButton.addActionListener(e -> {
                joinRoom(roomName);
            });
        }
        this.mainFrame.add(roomsPanel);

        JPanel newRoomPanel = new JPanel();
        newRoomPanel.setLayout(new BorderLayout());

        JPanel roomNamePanel = new JPanel();
        roomNamePanel.setLayout(new BoxLayout(roomNamePanel, BoxLayout.Y_AXIS));
        JLabel roomNameLabel = new JLabel("Room name");
        roomNamePanel.add(roomNameLabel);
        JTextField roomNameField = new JTextField(15);
        roomNamePanel.add(roomNameField);
        newRoomPanel.add(roomNamePanel, BorderLayout.WEST);

        JButton newRoomButton = new JButton("New Room");

        newRoomButton.addActionListener(e -> {
            createRoom(roomNameField.getText());
        });
        this.roomsPanel.add(newRoomPanel, BorderLayout.SOUTH);
        newRoomPanel.add(newRoomButton, BorderLayout.EAST);
        mainPanel.add(roomsPanel, BorderLayout.CENTER);
        mainFrame.add(mainPanel, BorderLayout.CENTER);
        this.mainFrame.revalidate();
        this.mainFrame.repaint();
    }

    private void showRoom() throws RemoteException {
        this.mainFrame.getContentPane().removeAll();
        this.roomsPanel = new JPanel();
        this.roomsPanel.setLayout(new BorderLayout());
        this.roomsPanel.add(new JLabel(roomChat.getRoomName()), BorderLayout.NORTH);
        this.roomMessages = new JPanel();
        this.roomMessages.setLayout(new BoxLayout(roomMessages, BoxLayout.Y_AXIS));
        this.roomsPanel.add(roomMessages, BorderLayout.CENTER);

        JPanel newMessagePanel = new JPanel();
        newMessagePanel.setLayout(new BorderLayout());
        JTextArea textArea = new JTextArea();
        newMessagePanel.add(textArea, BorderLayout.NORTH);
        JButton sendButton = new JButton("Send");
        newMessagePanel.add(sendButton, BorderLayout.EAST);
        sendButton.addActionListener(e -> {
             sendMessage(textArea.getText());
        });
        JButton closeButton = new JButton("Leave");
        newMessagePanel.add(closeButton, BorderLayout.WEST);
        closeButton.addActionListener(e -> {
            leaveRoom();
        });
        this.roomsPanel.add(newMessagePanel, BorderLayout.SOUTH);

        this.mainFrame.add(roomsPanel, BorderLayout.CENTER);
        this.mainFrame.revalidate();
        this.mainFrame.repaint();
    }

    private void createRoom(String roomName) {
        try {
            if (!serverChat.getRooms().contains(roomName)) {
                serverChat.createRoom(roomName);
                joinRoom(roomName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            //tratar erro depois
        }
    }

    private void joinRoom(String roomName) {
        try {
            this.roomChat = (IRoomChat) Naming.lookup("rmi://" + this.serverAddress + ":2020/" + roomName);
            showRoom();
            this.roomChat.joinRoom(userChat.getUserName(), (IUserChat) UnicastRemoteObject.exportObject(userChat, 0));
        } catch (Exception e) {
            e.printStackTrace();
            //tratar melhor depois
        }
    }

    private void leaveRoom() {
        try {
            this.roomChat.leaveRoom(this.userChat.getUserName());
            showRooms();
        } catch (Exception e) {
            e.printStackTrace();
            //tratar melhor depois
        }
    }

    private void sendMessage(String message) {
        try {
            this.roomChat.sendMsg(this.userChat.getUserName(), message);
        } catch (Exception e) {
            e.printStackTrace();
            //tratar melhor depois
        }
    }

    public void receiveMessage(String senderName, String message) {
        String formattedMsg = senderName + ": " + message;
        getRoomMessages().add(new JLabel(formattedMsg));
        getRoomMessages() .revalidate();
        getRoomMessages().repaint();
    }
}
