import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultListModel;

import com.formdev.flatlaf.FlatDarkLaf;

public class ServerChat extends UnicastRemoteObject implements IServerChat {

    private Map<String, IRoomChat> roomList;
    private JFrame frame;
    private JList<String> roomListDisplay;
    private DefaultListModel<String> roomListModel;
    private JButton closeRoomButton;

    public ServerChat() throws RemoteException, UnsupportedLookAndFeelException {
        super();
        roomList = new HashMap<>();
        initializeGUI();
        System.out.println("Servidor de Chat iniciado.");
    }

    private void initializeGUI() throws UnsupportedLookAndFeelException, RemoteException {
        UIManager.setLookAndFeel(new FlatDarkLaf());
        frame = new JFrame("Servidor de Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 400);
        frame.setLayout(new BorderLayout());

        roomListModel = new DefaultListModel<>();
        roomListDisplay = new JList<>(roomListModel);
        JScrollPane roomListScrollPane = new JScrollPane(roomListDisplay);
        frame.add(roomListScrollPane, BorderLayout.CENTER);

        closeRoomButton = new JButton("Fechar Sala");
        closeRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRoom = roomListDisplay.getSelectedValue();
                if (selectedRoom != null) {
                    try {
                        closeRoomServer(selectedRoom);
                    } catch (RemoteException ex) {
                        JOptionPane.showMessageDialog(frame, "Erro ao fechar a sala: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Selecione uma sala para fechar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeRoomButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
        updateRoomList();
    }

    private void updateRoomList() throws RemoteException {
        roomListModel.clear();
        for (String roomName : roomList.keySet()) {
            roomListModel.addElement(roomName);
        }
    }

    @Override
    public ArrayList<String> getRooms() throws RemoteException {
        return new ArrayList<>(roomList.keySet());
    }

    @Override
    public void createRoom(String roomName) throws RemoteException {
        if (!roomList.containsKey(roomName)) {
            RoomChat newRoom = new RoomChat(roomName);
            roomList.put(roomName, newRoom);
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 2020);
                registry.bind(roomName, newRoom);
                System.out.println("Sala '" + roomName + "' criada e registrada.");
                updateRoomList(); // Atualiza a lista na GUI do servidor
            } catch (Exception e) {
                System.err.println("Erro ao registrar a sala '" + roomName + "': " + e.toString());
                e.printStackTrace();
            }
        } else {
            System.out.println("Sala '" + roomName + "' já existe.");
        }
    }

    @Override
    public void closeRoomServer(String roomName) throws RemoteException {
        IRoomChat roomToClose = roomList.get(roomName);
        if (roomToClose != null) {
            roomToClose.closeRoom();
            roomList.remove(roomName);
            try {
                Registry registry = LocateRegistry.getRegistry("localhost", 2020);
                registry.unbind(roomName);
                System.out.println("Sala '" + roomName + "' fechada e removida pelo servidor.");
                updateRoomList(); // Atualiza a lista na GUI do servidor
            } catch (NotBoundException e) {
                System.err.println("Erro ao remover sala '" + roomName + "' do registry: " + e.toString());
            }
        } else {
            System.out.println("Sala '" + roomName + "' não encontrada para fechar.");
        }
    }

    public static void main(String[] args) throws RemoteException {
        try {
            ServerChat server = new ServerChat();
            Registry registry = LocateRegistry.createRegistry(2020);
            registry.bind("Servidor", server);
            System.out.println("Servidor registrado no RMI Registry na porta 2020.");
            System.out.println("Servidor de Chat pronto.");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar o servidor: " + e.toString());
            e.printStackTrace();
        }
    }
}