import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class ChatServer {
    private static final int PORT = 12345;
    
    private static Set<PrintWriter> clientWriters = ConcurrentHashMap.newKeySet();
    
    private static List<String> chatHistory = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        System.out.println("Chat Server (Multi-threaded, History enabled) is running on port " + PORT + "...");
        
        ExecutorService pool = Executors.newFixedThreadPool(50); 
        
        try (ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = listener.accept();
                System.out.println("New client connected: " + clientSocket);
                
                pool.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                username = in.readLine(); 
                if (username == null) return;

                System.out.println(username + " has joined.");
                
                out.println("--- HISTORY START ---");
                for (String oldMessage : chatHistory) {
                    out.println(oldMessage);
                }
                out.println("--- HISTORY END ---");

                clientWriters.add(out);
                broadcast(username + " has joined the chat.");
                
                String clientMessage;
                while ((clientMessage = in.readLine()) != null) {
                    String fullMessage = "[" + username + "]: " + clientMessage;
                    System.out.println("Broadcast: " + fullMessage);
                    broadcast(fullMessage);
                }
            } catch (IOException e) {
                System.out.println(username + " disconnected with error: " + e.getMessage());
            } finally {
                if (out != null) {
                    clientWriters.remove(out);
                }
                if (username != null) {
                    broadcast(username + " has left the chat.");
                }
                try { socket.close(); } catch (IOException e) {}
            }
        }

        private void broadcast(String message) {
            if (!message.contains("has joined") && !message.contains("has left")) {
                 chatHistory.add(message);
                 if (chatHistory.size() > 100) {
                     chatHistory.remove(0);
                 }
            }

            for (PrintWriter writer : clientWriters) {
                writer.println(message);
            }
        }
    }
}