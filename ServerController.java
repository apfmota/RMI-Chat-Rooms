import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class ServerController {

    private final ServerChat serverChat;
    private final JFrame mainFrame;

    public static void main(String[] args) throws MalformedURLException, RemoteException {
        ServerController controller = new ServerController();
    }

    private ServerController() throws MalformedURLException, RemoteException {
        this.serverChat = new ServerChat();
        LocateRegistry.createRegistry(2020);
        Naming.rebind("rmi://localhost:2020/Servidor", UnicastRemoteObject.exportObject(serverChat, 0));
        this.mainFrame = new JFrame();
        this.mainFrame.setSize(800, 600);
        this.mainFrame.setTitle("Server Chat");
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        showRooms();

        mainFrame.setVisible(true);
    }

    private void showRooms() throws RemoteException {
        this.mainFrame.getContentPane().removeAll();
        JPanel mainPanel = new JPanel();

        JPanel roomsPanel = new JPanel();
        roomsPanel.setLayout(new BorderLayout());
        roomsPanel.setSize(new Dimension(400, 400));

        JPanel selectRoomsPanel = new JPanel();
        selectRoomsPanel.setLayout(new BoxLayout(selectRoomsPanel, BoxLayout.Y_AXIS));
        roomsPanel.add(selectRoomsPanel);

        for (String roomName: serverChat.getRooms()) {
            JPanel roomPanel = new JPanel();
            roomPanel.setLayout(new BorderLayout());
            roomsPanel.add(roomPanel);
            JLabel roomNameLabel = new JLabel(roomName);
            roomPanel.add(roomNameLabel, BorderLayout.WEST);
            JButton closeButton = new JButton("Close");
            roomPanel.add(closeButton, BorderLayout.EAST);
            closeButton.addActionListener(e -> {
                closeRoom(roomName);
            });
        }
        this.mainFrame.add(roomsPanel);

        mainPanel.add(roomsPanel, BorderLayout.CENTER);
        mainFrame.add(mainPanel, BorderLayout.CENTER);
        this.mainFrame.revalidate();
        this.mainFrame.repaint();
    }

    private void closeRoom(String roomName) {
        try {
            IRoomChat roomChat = serverChat.getRoomList().get(roomName);
        } catch (Exception e) {
            e.printStackTrace();
            //trata depois
        }
    }
}
