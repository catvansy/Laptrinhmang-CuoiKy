package megachat.server;

import java.io.*;
import java.net.Socket;
import megachat.utils.Message;

/**
 * ClientHandler - Luồng xử lý cho mỗi client kết nối
 * Nhiệm vụ: Lắng nghe tin nhắn từ client, phát sóng cho toàn bộ
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String clientName = "Anonymous";
    
    public ClientHandler(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void run() {
        try {
            // Khởi tạo stream để đọc/ghi object qua socket
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            
            // Lắng nghe tin nhắn từ client
            while (true) {
                Object obj = in.readObject();
                if (obj instanceof Message) {
                    Message message = (Message) obj;
                    this.clientName = message.getSender();
                    
                    System.out.println("📨 Nhận từ [" + clientName + "]: " + message.getContent());
                    
                    // Phát sóng cho toàn bộ client
                    ChatServer.broadcastMessage(message);
                }
            }
        } catch (EOFException e) {
            System.out.println("✗ Client đã ngắt kết nối: " + clientName);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("✗ Lỗi kết nối: " + e.getMessage());
        } finally {
            cleanup();
        }
    }
    
    /**
     * Gửi tin nhắn cho client này
     */
    public void sendMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
            System.out.println("✗ Lỗi gửi tin: " + e.getMessage());
        }
    }
    
    /**
     * Dọn dẹp: đóng socket, loại bỏ khỏi danh sách
     */
    private void cleanup() {
        try {
            ChatServer.removeClient(this);
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
