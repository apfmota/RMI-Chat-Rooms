import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerChat  implements IServerChat {

    private Map<String, IRoomChat> roomList = new HashMap<>();

    @Override
    public ArrayList<String> getRooms() {
        return new ArrayList<>(roomList.keySet());
    }

    @Override
    public void createRoom(String roomName) {
        RoomChat newRoom = new RoomChat();
        newRoom.setRoomName(roomName);
        roomList.put(roomName, newRoom);
    }
}
