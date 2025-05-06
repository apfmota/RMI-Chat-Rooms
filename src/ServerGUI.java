import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class ServerGUI extends JFrame {

    private IServerChat server;
    private JList<String> roomListDisplay;
    private DefaultListModel<String> roomListModel;
    private JButton closeRoomButton;

    public ServerGUI() {
        super("Servidor de Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLayout(new BorderLayout());

        roomListModel = new DefaultListModel<>();
        roomListDisplay = new JList<>(roomListModel);
        JScrollPane roomListScrollPane = new JScrollPane(roomListDisplay);
        add(roomListScrollPane, BorderLayout.CENTER);

        closeRoomButton = new JButton("Fechar Sala");
        closeRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRoom = roomListDisplay.getSelectedValue();
                if (selectedRoom != null) {
                    try {
                        server.closeRoomServer(selectedRoom);
                        updateRoomList();
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(ServerGUI.this, "Erro ao fechar a sala: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(ServerGUI.this, "Selecione uma sala para fechar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeRoomButton);
        add(buttonPanel, BorderLayout.SOUTH);

        connectToServer();
        updateRoomList();

        setVisible(true);
    }

    private void connectToServer() {
        try {
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);
            server = (IServerChat) registry.lookup("Servidor");
            System.out.println("Conectado ao servidor RMI.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao conectar ao servidor RMI: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateRoomList() {
        if (server != null) {
            try {
                ArrayList<String> rooms = server.getRooms();
                roomListModel.clear();
                for (String room : rooms) {
                    roomListModel.addElement(room);
                }
            } catch (RemoteException e) {
                JOptionPane.showMessageDialog(this, "Erro ao obter lista de salas: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ServerGUI::new);
    }
}