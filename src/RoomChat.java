import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class RoomChat extends UnicastRemoteObject implements IRoomChat {

    private String roomName;
    private Map<String, IUserChat> userList;

    public RoomChat(String name) throws RemoteException {
        super();
        this.roomName = name;
        this.userList = new HashMap<>();
        System.out.println("Sala '" + roomName + "' criada.");
    }

    @Override
    public String getRoomName() throws RemoteException {
        return roomName;
    }

    @Override
    public synchronized void joinRoom(String userName, IUserChat user) throws RemoteException {
        if (!userList.containsKey(userName)) {
            userList.put(userName, user);
            System.out.println("Usuário '" + userName + "' entrou na sala '" + roomName + "'.");
            sendMsg("[Servidor]", "'" + userName + "' entrou na sala.");
        } else {
            System.out.println("Usuário '" + userName + "' já está na sala '" + roomName + "'.");
        }
    }

    @Override
    public synchronized void leaveRoom(String usrName) throws RemoteException {
        if (userList.containsKey(usrName)) {
            System.out.println("Usuário '" + usrName + "' saiu da sala '" + roomName + "'.");
            sendMsg("[Servidor]", "'" + usrName + "' saiu da sala.");
            userList.remove(usrName);
        } else {
            System.out.println("Usuário '" + usrName + "' não está na sala '" + roomName + "'.");
        }
    }

    @Override
    public synchronized void sendMsg(String usrName, String msg) throws RemoteException {
        for (Map.Entry<String, IUserChat> entry : userList.entrySet()) {
            String userName = entry.getKey();
            IUserChat user = entry.getValue();
            try {
                user.deliverMsg(usrName, msg);
            } catch (RemoteException e) {
                System.err.println(
                        "Erro ao entregar mensagem para " + userName + " na sala " + roomName + ": " + e.getMessage());
                // Pode ser que o cliente desconectou
            }
        }
        System.out.println("Sala '" + roomName + "': " + usrName + " -> " + msg);
    }

    @Override
    public synchronized void closeRoom() throws RemoteException {
        String closeMessage = "Sala fechada pelo servidor.";
        for (IUserChat user : userList.values()) {
            try {
                user.deliverMsg("[Servidor]", closeMessage);
            } catch (RemoteException e) {
                System.err.println("Erro ao enviar mensagem de fechamento para um usuário: " + e.getMessage());
                // Pode ser que o cliente já tenha desconectado
            }
        }
        System.out.println("Sala '" + roomName + "' sendo fechada. Notificando usuários.");
        userList.clear();
        // A remoção da sala da lista do servidor será feita pelo ServerChat.
    }

    @Override
    public Map<String, IUserChat> getUsers() throws RemoteException {
        return userList;
    }
}