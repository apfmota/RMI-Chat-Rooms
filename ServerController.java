import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class ServerController {

    private final ServerChat serverChat;
    private final JFrame mainFrame;

    public static void main(String[] args) throws MalformedURLException, RemoteException {
        ServerController controller = new ServerController();
    }

    private ServerController() throws MalformedURLException, RemoteException {
        this.serverChat = new ServerChat();
        LocateRegistry.createRegistry(2020);
        Naming.rebind("rmi://localhost:2020/Servidor", UnicastRemoteObject.exportObject(serverChat, 0));
        this.mainFrame = new JFrame();
        this.mainFrame.setTitle("Server Chat");
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

}
