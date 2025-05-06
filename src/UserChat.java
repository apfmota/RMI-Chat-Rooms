import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.formdev.flatlaf.FlatDarkLaf;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class UserChat extends UnicastRemoteObject implements IUserChat {

    private String userName;
    private String serverAddress;
    private IServerChat server;
    private IRoomChat currentRoom;
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private JList<String> roomListDisplay;
    private DefaultListModel<String> roomListModel;
    private JButton joinButton;
    private JButton leaveButton;
    private JButton sendButton;
    private JButton createRoomButton;
    private JTextField newRoomNameField;

    public UserChat(String name, String serverAddress) throws RemoteException, UnsupportedLookAndFeelException {
        super();
        this.userName = name;
        this.serverAddress = serverAddress;
        initializeGUI();
        connectToServer(serverAddress);
        loadRoomList();
    }

    private void connectToServer(String serverAdddress) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(serverAdddress, 2020);
            server = (IServerChat) registry.lookup("Servidor");
            appendToChat("[Sistema]", "Conectado ao servidor.");
        } catch (NotBoundException | RemoteException e) {
            appendToChat("[Erro]", "Não foi possível conectar ao servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadRoomList() throws RemoteException {
        if (server != null) {
            try {
                ArrayList<String> rooms = server.getRooms();
                roomListModel.clear();
                for (String roomName : rooms) {
                    roomListModel.addElement(roomName);
                }
            } catch (RemoteException e) {
                appendToChat("[Erro]", "Erro ao obter a lista de salas: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void joinRoom(String roomName) throws RemoteException {
        if (server != null) {
            try {
                Registry registry = LocateRegistry.getRegistry(serverAddress, 2020);
                IRoomChat room = (IRoomChat) registry.lookup(roomName);
                if (currentRoom != null) {
                        currentRoom.leaveRoom(userName);
                    }
                room.joinRoom(userName, this);
                currentRoom = room;
                appendToChat("[Sistema]", "Entrou na sala '" + roomName + "'.");
            } catch (NotBoundException | RemoteException e) {
                appendToChat("[Erro]", "Não foi possível entrar na sala '" + roomName + "': " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            appendToChat("[Erro]", "Não conectado ao servidor.");
        }
    }

    private void createNewRoom(String roomName) throws RemoteException {
        if (server != null) {
            try {
                server.createRoom(roomName);
                loadRoomList(); // Atualiza a lista de salas após a criação
                JOptionPane.showMessageDialog(frame, "Sala '" + roomName + "' criada. Agora você pode entrar nela.",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (RemoteException e) {
                appendToChat("[Erro]", "Erro ao criar a sala '" + roomName + "': " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            appendToChat("[Erro]", "Não conectado ao servidor.");
        }
    }

    public void leaveCurrentRoom() throws RemoteException {
        if (currentRoom != null) {
            try {
                currentRoom.leaveRoom(userName);
                appendToChat("[Sistema]", "Saiu da sala '" + currentRoom.getRoomName() + "'.");
                currentRoom = null;
            } catch (RemoteException e) {
                appendToChat("[Erro]", "Erro ao sair da sala: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            appendToChat("[Sistema]", "Não está em nenhuma sala.");
        }
    }

    public void sendMessage(String msg) throws RemoteException {
        if (currentRoom != null) {
            try {
                currentRoom.sendMsg(userName, msg);
                messageField.setText("");
            } catch (RemoteException e) {
                appendToChat("[Erro]", "Erro ao enviar mensagem: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            appendToChat("[Sistema]", "Você precisa entrar em uma sala para enviar mensagens.");
        }
    }

    @Override
    public void deliverMsg(String senderName, String msg) throws RemoteException {
        System.out.println("Mensagem recebida por " + userName + " de " + senderName + ": " + msg);
        final String sender = senderName;
        final String message = msg;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    appendToChat(sender, message);
                    if (message.trim().toLowerCase().equals("Sala fechada pelo servidor.")) {
                        appendToChat("[Sistema]", "Esta sala foi fechada pelo servidor.");
                        currentRoom = null;
                        loadRoomList();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void appendToChat(String sender, String message) throws RemoteException {
        System.out.println("Vai adicionar à chatArea: " + sender + ": " + message);
        chatArea.append(sender + ": " + message + "\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
        chatArea.repaint();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                chatScrollPane.revalidate();
            }
        });
    }

    private JScrollPane chatScrollPane;

    private void initializeGUI() throws RemoteException {
        frame = new JFrame("Chat - " + userName);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());
        
        JPanel chatPanel = new JPanel(new BorderLayout());
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatScrollPane = new JScrollPane(chatArea);
        chatPanel.add(chatScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        inputPanel.add(messageField, BorderLayout.CENTER);

        sendButton = new JButton("Enviar");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                sendMessage(messageField.getText());
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
        inputPanel.add(sendButton, BorderLayout.EAST);
        chatPanel.add(inputPanel, BorderLayout.SOUTH);
        frame.add(chatPanel, BorderLayout.CENTER);

        JPanel roomPanel = new JPanel();
        roomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        roomPanel.setLayout(new BoxLayout(roomPanel, BoxLayout.Y_AXIS));
        roomPanel.setSize(new Dimension(200, 200));
        roomListModel = new DefaultListModel<>();
        roomListDisplay = new JList<>(roomListModel);
        JScrollPane roomListScrollPane = new JScrollPane(roomListDisplay);
        roomPanel.add(new JLabel("Salas:"));
        roomPanel.add(roomListScrollPane);

        JPanel joinLeaveButtons = new JPanel(new BorderLayout());
        joinLeaveButtons.setBorder(new EmptyBorder(5, 5, 5, 5));
        joinLeaveButtons.setMaximumSize(new Dimension(200, 40));
        roomPanel.add(joinLeaveButtons);

        JButton refreshButton = new JButton("Att");
        joinLeaveButtons.add(refreshButton, BorderLayout.CENTER);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    loadRoomList();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        joinButton = new JButton("Entrar");
        joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedRoom = roomListDisplay.getSelectedValue();
                if (selectedRoom != null) {
                    try {
                        joinRoom(selectedRoom);
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Selecione uma sala para entrar.", "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        joinLeaveButtons.add(joinButton, BorderLayout.WEST);

        leaveButton = new JButton("Sair");
        leaveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {                
                    leaveCurrentRoom();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
        joinLeaveButtons.add(leaveButton, BorderLayout.EAST);

        JPanel createRoomPanel = new JPanel(new BorderLayout());
        createRoomPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        newRoomNameField = new JTextField(10);
        createRoomPanel.add(new JLabel("Nova Sala:"), BorderLayout.NORTH);
        createRoomPanel.add(newRoomNameField, BorderLayout.WEST);
        createRoomButton = new JButton("Criar Sala");
        createRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newRoomName = newRoomNameField.getText().trim();
                if (!newRoomName.isEmpty()) {
                    try {
                        if (server.getRooms().contains(newRoomName)) {
                            JOptionPane.showMessageDialog(frame, "Essa sala já existe.", "Aviso",
                                JOptionPane.WARNING_MESSAGE);
                        } else {
                            createNewRoom(newRoomName);
                            newRoomNameField.setText("");
                        }
                    } catch (RemoteException ex) {
                        try {
                        appendToChat("[Erro]", "Erro ao verificar a sala: " + ex.getMessage());
                        ex.printStackTrace();
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Digite o nome da nova sala.", "Aviso",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        createRoomPanel.add(createRoomButton, BorderLayout.EAST);
        createRoomPanel.setMaximumSize(new Dimension(200, 40));
        roomPanel.add(createRoomPanel);
        
        // espacamento
        JPanel blankPanel = new JPanel();
        blankPanel.setPreferredSize(new Dimension(200, 50)); 
        roomPanel.add(blankPanel);

        frame.add(roomPanel, BorderLayout.WEST);

        frame.setVisible(true);

        // Carregar a lista de salas ao iniciar a GUI
        loadRoomList();
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, RemoteException {
        UIManager.setLookAndFeel(new FlatDarkLaf());
        String userName = JOptionPane.showInputDialog(null, "Digite seu nome:", "Nome de Usuário", JOptionPane.PLAIN_MESSAGE);
        String serverAdress = JOptionPane.showInputDialog(null, "Digite o endereço do servidor:", "Endereço", JOptionPane.PLAIN_MESSAGE, null, null, "localhost").toString();
        if (userName != null && !userName.trim().isEmpty()) {
            if (serverAdress != null && !serverAdress.trim().isEmpty()) {
                try {
                    UserChat client = new UserChat(userName.trim(), serverAdress.trim());
                } catch (RemoteException | UnsupportedLookAndFeelException e) {
                    System.err.println("Erro ao iniciar o cliente: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Endereço inválido.");
            }
        } else {
            System.out.println("Nome de usuário inválido.");
        }
    }
}