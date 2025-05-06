import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface IRoomChat extends Remote {
    void sendMsg(String usrName, String msg) throws RemoteException;
    void joinRoom(String userName, IUserChat user) throws RemoteException;
    void leaveRoom(String usrName) throws RemoteException;
    String getRoomName() throws RemoteException;
    void closeRoom() throws RemoteException;
    Map<String, IUserChat> getUsers() throws RemoteException;
}