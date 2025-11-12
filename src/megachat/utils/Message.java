package megachat.utils;

import java.io.Serializable;

/**
 * Lớp Message để gửi nhận tin nhắn qua mạng
 * Serializable để có thể convert thành byte và gửi qua socket
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String sender;        // Người gửi
    private String content;       // Nội dung tin nhắn
    private String type;          // Loại: "chat", "login", "register", "notification"
    private long timestamp;       // Thời gian gửi
    
    // Constructor
    public Message(String sender, String content, String type) {
        this.sender = sender;
        this.content = content;
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getter, Setter
    public String getSender() {
        return sender;
    }
    
    public void setSender(String sender) {
        this.sender = sender;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return "[" + sender + "] " + content;
    }
}
