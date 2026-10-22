import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final ScheduledExecutorService scheduler;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.scheduler = Executors.newScheduledThreadPool(2);
    }

    @Override
    public void run() {
        try {
            setupStreams();
            listenForNotifications();
        } catch (Exception e) {
            System.out.println("Klient rozłączony: " + e.getMessage());
        } finally {
            cleanUpResources();
        }
    }

    private void setupStreams() throws IOException {
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    private void listenForNotifications() throws IOException, ClassNotFoundException {
        while (!socket.isClosed()) {
            Notification notif = (Notification) in.readObject();
            System.out.println("Odebrano nową notyfikację do kolejki.");
            scheduleNotification(notif);
        }
    }

    private void scheduleNotification(Notification notif) {
        long delay = ChronoUnit.SECONDS.between(LocalDateTime.now(), notif.getSendTime());
        
        if (delay < 0) {
            sendResponse("Odrzucono: Czas powiadomienia jest z przeszłości!");
            return;
        }
        
        scheduler.schedule(() -> sendResponse("PRZYPOMNIENIE: " + notif.getText()), delay, TimeUnit.SECONDS);
    }

    private void sendResponse(String message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.err.println("Nie udało się wysłać notyfikacji do klienta.");
        }
    }

    private void cleanUpResources() {
        try {
            scheduler.shutdownNow();
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Błąd zamykania zasobów: " + e.getMessage());
        }
    }
}
