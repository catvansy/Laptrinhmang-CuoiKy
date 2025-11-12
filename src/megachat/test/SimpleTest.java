package megachat.test;

import java.io.*;
import java.net.Socket;
import megachat.utils.Message;

/**
 * SimpleTest - Test đơn giản: gửi 1 tin rồi thoát
 */
public class SimpleTest {
    public static void main(String[] args) throws Exception {
        System.out.println(">>> Kết nối đến server...");
        Socket socket = new Socket("localhost", 5000);
        System.out.println("✓ Kết nối thành công!");
        
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        
        // Gửi tin nhắn
        String username = args.length > 0 ? args[0] : "TestUser";
        String text = args.length > 1 ? args[1] : "Hello from " + username;
        
        Message msg = new Message(username, text, "chat");
        out.writeObject(msg);
        out.flush();
        System.out.println("✓ Đã gửi: [" + username + "] " + text);
        
        // Chờ nhận tin từ server trong 2 giây
        socket.setSoTimeout(2000);
        try {
            Object obj = in.readObject();
            if (obj instanceof Message) {
                Message received = (Message) obj;
                System.out.println("✓ Nhận lại: [" + received.getSender() + "] " + received.getContent());
            }
        } catch (java.net.SocketTimeoutException e) {
            System.out.println("(Timeout - không nhận tin trong 2s)");
        }
        
        socket.close();
        System.out.println("✓ Đóng kết nối");
    }
}
