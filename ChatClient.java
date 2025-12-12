import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private JTextArea messageArea;
    private JTextField textField;
    private PrintWriter out;
    private JFrame frame;
    private String username;

    private final Color COLOR_BG = new Color(30, 30, 30);
    private final Color COLOR_FG = new Color(220, 220, 220);
    private final Color COLOR_INPUT = new Color(50, 50, 50);
    private final Color COLOR_ACCENT = new Color(0, 150, 136);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClient().createAndShowGUI());
    }

    private void createAndShowGUI() {
        username = JOptionPane.showInputDialog(
            null, 
            "Enter your username:", 
            "Chat Login", 
            JOptionPane.PLAIN_MESSAGE
        );
        if (username == null || username.trim().isEmpty()) {
            System.exit(0);
        }

        frame = new JFrame("Chat Client - " + username);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 700);
        
        setupDarkMode();
        
        connectToServer();
        
        frame.setVisible(true);
    }

    private void setupDarkMode() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setBackground(COLOR_BG);
        messageArea.setForeground(COLOR_FG);
        messageArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        messageArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_INPUT));
        scrollPane.getViewport().setBackground(COLOR_BG);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(COLOR_BG);

        textField = new JTextField();
        textField.setBackground(COLOR_INPUT);
        textField.setForeground(COLOR_FG);
        textField.setCaretColor(COLOR_FG);
        textField.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        textField.addActionListener(e -> sendMessage());

        JButton sendButton = new JButton("Send");
        sendButton.setBackground(COLOR_ACCENT);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.addActionListener(e -> sendMessage());

        inputPanel.add(textField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        frame.add(mainPanel);
    }

    private void connectToServer() {
        new Thread(() -> {
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {
                
                out = new PrintWriter(socket.getOutputStream(), true);
                
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                out.println(username);

                String incomingMessage;
                while ((incomingMessage = in.readLine()) != null) {
                    appendToChat(incomingMessage);
                }

            } catch (ConnectException e) {
                appendToChat("ERROR: Connection refused. Server may be offline.");
            } catch (IOException e) {
                appendToChat("ERROR: Lost connection to server.");
            }
        }).start();
    }

    private void sendMessage() {
        String message = textField.getText();
        if (message != null && !message.trim().isEmpty()) {
            if (out != null) {
                out.println(message); 
                textField.setText("");
            } else {
                appendToChat("System: Not connected to the server.");
            }
        }
    }

    private void appendToChat(String message) {
        SwingUtilities.invokeLater(() -> {
            messageArea.append(message + "\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength());
        });
    }
}