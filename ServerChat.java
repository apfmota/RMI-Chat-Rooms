import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerChat  implements IServerChat {

    private Map<String, IRoomChat> roomList = new HashMap<>();
    private final ServerController controller;

    public ServerChat(ServerController controller) {
        this.controller = controller;
    }

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
            controller.showRooms();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
