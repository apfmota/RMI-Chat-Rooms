import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerChat  implements IServerChat {

    private Map<String, IRoomChat> roomList = new HashMap<>();

    public Map<String, IRoomChat> getRoomList() throws RemoteException {
        return roomList;
    }

    @Override
    public ArrayList<String> getRooms() {
        return new ArrayList<>(roomList.keySet());
    }

    @Override
    public void createRoom(String roomName) {
        try {
            RoomChat newRoom = new RoomChat();
            newRoom.setRoomName(roomName);
            roomList.put(roomName, newRoom);
            Naming.rebind("rmi://localhost:2020/" + roomName, UnicastRemoteObject.exportObject(newRoom, 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
