import javax.swing.*;
import javax.swing.text.DateFormatter;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.util.Date;

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
        String formattedMsg = senderName + ": " + msg;
        clientController.getRoomMessages().add(new JLabel(formattedMsg));
        clientController.getRoomMessages() .revalidate();
        clientController.getRoomMessages().repaint();
    }
}
