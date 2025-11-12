package megachat.client;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import javax.swing.*;
import megachat.utils.Message;

/**
 * ChatClient - Ứng dụng client chat với giao diện Swing
 * Kết nối đến server, gửi/nhận tin nhắn
 */
public class ChatClient extends JFrame {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    // GUI Components
    private JTextArea chatArea;
    private JTextField messageInput;
    private JButton sendButton;
    private JLabel statusLabel;
    private String username;
    
    public ChatClient(String host, int port, String username) {
        this.username = username;
        
        // Cấu hình cửa sổ
        setTitle("🎯 MegaChat - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 700);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Tạo giao diện
        initComponents();
        
        // Kết nối đến server
        connectToServer(host, port);
        
        // Hiển thị cửa sổ
        setVisible(true);
    }
    
    /**
     * Khởi tạo các component giao diện
     */
    private void initComponents() {
        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Panel trạng thái (header)
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setBackground(new Color(100, 150, 200));
        statusLabel = new JLabel("Đang kết nối...");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusPanel.add(statusLabel);
        mainPanel.add(statusPanel, BorderLayout.NORTH);
        
        // Khu vực hiển thị tin nhắn
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 13));
        chatArea.setMargin(new Insets(5, 5, 5, 5));
        chatArea.setBackground(new Color(245, 245, 245));
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel nhập tin nhắn
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBackground(Color.WHITE);
        
        messageInput = new JTextField();
        messageInput.setFont(new Font("Arial", Font.PLAIN, 13));
        messageInput.setMargin(new Insets(5, 5, 5, 5));
        
        sendButton = new JButton("📤 Gửi");
        sendButton.setFont(new Font("Arial", Font.BOLD, 12));
        sendButton.setBackground(new Color(100, 150, 200));
        sendButton.setForeground(Color.WHITE);
        sendButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendButton.addActionListener(e -> sendMessage());
        
        // Bấm Enter để gửi
        messageInput.addActionListener(e -> sendMessage());
        
        inputPanel.add(messageInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    /**
     * Kết nối đến server
     */
    private void connectToServer(String host, int port) {
        new Thread(() -> {
            try {
                socket = new Socket(host, port);
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());
                
                statusLabel.setText("✓ Đã kết nối (" + username + ")");
                statusLabel.setForeground(Color.GREEN);
                chatArea.append("--- Kết nối đến server thành công ---\n");
                
                // Lắng nghe tin nhắn từ server
                listenForMessages();
                
            } catch (IOException e) {
                chatArea.append("✗ Lỗi kết nối: " + e.getMessage() + "\n");
                statusLabel.setText("✗ Kết nối thất bại");
                statusLabel.setForeground(Color.RED);
            }
        }).start();
    }
    
    /**
     * Gửi tin nhắn
     */
    private void sendMessage() {
        String text = messageInput.getText().trim();
        if (text.isEmpty()) return;
        
        try {
            Message message = new Message(username, text, "chat");
            out.writeObject(message);
            out.flush();
            
            messageInput.setText("");
        } catch (IOException e) {
            chatArea.append("✗ Lỗi gửi tin: " + e.getMessage() + "\n");
        }
    }
    
    /**
     * Lắng nghe tin nhắn từ server
     */
    private void listenForMessages() {
        try {
            while (true) {
                Object obj = in.readObject();
                if (obj instanceof Message) {
                    Message message = (Message) obj;
                    chatArea.append(message.getSender() + ": " + message.getContent() + "\n");
                    chatArea.setCaretPosition(chatArea.getDocument().getLength());
                }
            }
        } catch (EOFException e) {
            chatArea.append("\n--- Ngắt kết nối ---\n");
        } catch (IOException | ClassNotFoundException e) {
            chatArea.append("\n✗ Lỗi: " + e.getMessage() + "\n");
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String username;
            if (args.length > 0 && !args[0].isEmpty()) {
                username = args[0];
            } else {
                username = JOptionPane.showInputDialog("Nhập tên người dùng:");
                if (username == null || username.isEmpty()) {
                    username = "User" + System.currentTimeMillis() % 1000;
                }
            }
            new ChatClient("localhost", 5000, username);
        });
    }
}
