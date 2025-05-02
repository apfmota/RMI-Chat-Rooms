import java.rmi.Naming;

public class ServerController {

    public static void main(String[] args) {
        try {
            ServerChat serverChat = new ServerChat();
            Naming.rebind("RMI://127.0.0.1/Servidor", serverChat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
