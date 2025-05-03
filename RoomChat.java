import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

public class RoomChat implements IRoomChat {

    private String roomName;
    private Map<String, IUserChat> users = new HashMap<>();

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public void sendMsg(String usrName, String msg) throws RemoteException {
        for (IUserChat user: users.values()) {
            user.deliverMsg(usrName, msg);
        }
    }

    @Override
    public void joinRoom(String userName, IUserChat user) throws RemoteException {
        users.put(userName, user);
        sendMsg("Servidor", userName + " joined");
    }

    @Override
    public void leaveRoom(String usrName) throws RemoteException {
        users.remove(usrName);
        sendMsg("Servidor", usrName + " left");
    }

    @Override
    public String getRoomName() {
        return roomName;
    }

    @Override
    public void closeRoom() {
        //nao sei o que fazer aqui por enquanto
    }
}
