import java.rmi.RemoteException;

public class UserChat implements IUserChat {

    private final String userName;
    private final ClientController clientController;

    public UserChat(String userName, ClientController clientController) {
        this.userName = userName;
        this.clientController = clientController;
    }

    public String getUserName() throws RemoteException {
        return userName;
    }

    @Override
    public void deliverMsg(String senderName, String msg) {
        clientController.receiveMessage(senderName, msg);
    }
}
