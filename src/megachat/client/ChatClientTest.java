package megachat.client;

import java.io.*;
import java.net.Socket;
import megachat.utils.Message;

/**
 * ChatClientTest - Client test console (không GUI) để test Socket
 */
public class ChatClientTest {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;
    
    public ChatClientTest(String host, int port, String username) {
        this.username = username;
        System.out.println("[" + username + "] Đang kết nối đến " + host + ":" + port);
        
        try {
            socket = new Socket(host, port);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            
            System.out.println("[" + username + "] ✓ Kết nối thành công!");
            
            // Thread lắng nghe tin từ server
            new Thread(this::listenMessages).start();
            
            // Thread gửi tin từ console
            new Thread(this::sendMessagesFromConsole).start();
            
        } catch (IOException e) {
            System.out.println("[" + username + "] ✗ Lỗi kết nối: " + e.getMessage());
        }
    }
    
    private void listenMessages() {
        try {
            while (true) {
                Object obj = in.readObject();
                if (obj instanceof Message) {
                    Message message = (Message) obj;
                    System.out.println("\n📨 [" + message.getSender() + "]: " + message.getContent());
                    System.out.print("[" + username + "] Nhập: ");
                }
            }
        } catch (EOFException e) {
            System.out.println("\n[" + username + "] Ngắt kết nối từ server");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("\n[" + username + "] Lỗi: " + e.getMessage());
        }
    }
    
    private void sendMessagesFromConsole() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("[" + username + "] Nhập: ");
            
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    Message message = new Message(username, line, "chat");
                    out.writeObject(message);
                    out.flush();
                    System.out.print("[" + username + "] Nhập: ");
                }
            }
        } catch (IOException e) {
            System.out.println("[" + username + "] Lỗi gửi: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        String username = (args.length > 0) ? args[0] : "User1";
        new ChatClientTest("localhost", 5000, username);
        
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
