import java.io.IOException;
import java.io.ObjectInputStream;

public class ServerListener implements Runnable {
    private final ObjectInputStream in;

    public ServerListener(ObjectInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        try {
            listenLoop();
        } catch (Exception e) {
            System.out.println("\nUtracono połączenie z serwerem nasłuchującym.");
        }
    }

    private void listenLoop() throws IOException, ClassNotFoundException {
        while (true) {
            String serverMessage = (String) in.readObject();
            displayNotification(serverMessage);
        }
    }

    private void displayNotification(String message) {
        System.out.println("\nOTRZYMANO: " + message);
        System.out.print("Wpisz treść wiadomości: ");
    }
}
