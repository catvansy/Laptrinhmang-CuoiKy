package megachat.server;

import java.io.*;
import java.net.*;
import java.util.*;
import megachat.utils.Message;

/**
 * ChatServer - Server quản lý kết nối từ nhiều client
 * Lắng nghe cổng 5000, chấp nhận client kết nối
 * Phát sóng (broadcast) tin nhắn cho toàn bộ client
 */
public class ChatServer {
    private static final int PORT = 5000;
    private static Set<ClientHandler> clientHandlers = Collections.synchronizedSet(new HashSet<>());
    
    public static void main(String[] args) {
        System.out.println("=== MegaChat Server khởi động ===");
        System.out.println("Lắng nghe trên cổng: " + PORT);
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                // Chấp nhận client kết nối mới
                Socket clientSocket = serverSocket.accept();
                System.out.println("✓ Client kết nối: " + clientSocket.getInetAddress());
                
                // Tạo luồng xử lý cho client này
                ClientHandler handler = new ClientHandler(clientSocket);
                clientHandlers.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Phát sóng tin nhắn cho toàn bộ client
     */
    public static void broadcastMessage(Message message) {
        for (ClientHandler handler : clientHandlers) {
            handler.sendMessage(message);
        }
    }
    
    /**
     * Loại bỏ client khỏi danh sách khi ngắt kết nối
     */
    public static void removeClient(ClientHandler handler) {
        clientHandlers.remove(handler);
        System.out.println("✗ Client ngắt kết nối. Còn: " + clientHandlers.size());
    }
}
