import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class NotificationClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        new NotificationClient().startClient();
    }

    public void startClient() {
        try (Socket socket = new Socket(HOST, PORT)) {
            System.out.println("Pomyślnie połączono z serwerem.");
            setupClientLogic(socket);
        } catch (IOException e) {
            System.err.println("Błąd połączenia z serwerem: " + e.getMessage());
        }
    }

    private void setupClientLogic(Socket socket) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

        new Thread(new ServerListener(in)).start();
        handleUserInputLoop(out);
    }
    
    private void checkExitCommand (String input) {
    	if ("exit".equalsIgnoreCase(input.trim())) {
    	    System.out.println("Zamykanie klenta...");
    	    System.exit(0);
    	}
    }

    private void handleUserInputLoop(ObjectOutputStream out) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Wpisz widomosc lub exit, aby zakończyć pracę: ");
            String msg = scanner.nextLine();
            checkExitCommand(msg);
            
            Notification notif = createNotificationFromInput(scanner, msg);
            if (notif != null) {
                sendNotificationToServer(notif, out);
            }
        }
    }

    private Notification createNotificationFromInput(Scanner scanner, String msg) {
        System.out.print("Wpisz czas (format yyyy-MM-dd HH:mm:ss): ");
        String timeStr = scanner.nextLine();
        
        checkExitCommand(timeStr);
        
        return parseInput(msg, timeStr);
    }

    private Notification parseInput(String msg, String timeStr) {
        try {
            LocalDateTime time = LocalDateTime.parse(timeStr, formatter);
            return new Notification(msg, time);
        } catch (DateTimeParseException e) {
            System.out.println("Błąd: Nieprawidłowy format czasu! Spróbuj ponownie.");
            return null;
        }
    }

    private void sendNotificationToServer(Notification notif, ObjectOutputStream out) {
        try {
            out.writeObject(notif);
            out.flush();
            System.out.println("Wysłano do kolejki serwera.");
        } catch (IOException e) {
            System.err.println("Błąd wysyłania do serwera: " + e.getMessage());
        }
    }
}
