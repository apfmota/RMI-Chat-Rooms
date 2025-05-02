import java.util.HashMap;
import java.util.Map;

public class RoomChat implements IRoomChat {

    private String roomName;
    private Map<String, IUserChat> users = new HashMap<>();

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public void sendMsg(String usrName, String msg) {
        for (IUserChat user: users.values()) {
            user.deliverMsg(usrName, msg);
        }
    }

    @Override
    public void joinRoom(String userName, IUserChat user) {
        users.put(userName, user);
    }

    @Override
    public void leaveRoom(String usrName) {
        users.remove(usrName);
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
