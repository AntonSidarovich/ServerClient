import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class NotificationServer {
    private static final int PORT = 8080;

    public static void main(String[] args) {
        new NotificationServer().startServer();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Serwer uruchomiony, wpisz exit żeby zakonczyc pracę.");
            startConsoleListener(serverSocket);
            acceptClientsLoop(serverSocket);
        } catch (IOException e) {
            System.err.println("Serwer został zamknęty.");
        }
    }
    
    private void startConsoleListener(ServerSocket serverSocket) {
    	new Thread(() -> {
    	     Scanner scanner = new Scanner(System.in);
    	     while (scanner.hasNextLine()) {
    	     	String command = scanner.nextLine();
    	     	if ("exit".equalsIgnoreCase(command.trim())) {
    	    		System.out.println("Zamykanie serwera...");
    	    		closeServerSocket(serverSocket);
    	    		System.exit(0);
    		}
    	     }
    	}).start();
    } 
    
    private void closeServerSocket (ServerSocket serverSocket) {
    	try {
    	    serverSocket.close();    
    	} catch (IOException e) {
    	    System.err.println("Błąd podczas zamykania serwera: " + e.getMessage());
    	}
    }

    private void acceptClientsLoop(ServerSocket serverSocket) {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                handleNewClient(clientSocket);
            } catch (IOException e) {
                System.err.println("Błąd podczas łączenia z klientem: " + e.getMessage());
            }
        }
    }

    private void handleNewClient(Socket clientSocket) {
        System.out.println("Nowy klient połączony: " + clientSocket.getInetAddress());
        ClientHandler handler = new ClientHandler(clientSocket);
        new Thread(handler).start();
    }
}
