# 🎯 MegaChat - Ứng dụng Chat Online

Ứng dụng chat đơn giản, hiệu quả sử dụng **Java Socket** với mô hình **Multi Client-Server**. Cho phép nhiều người dùng chat trực tuyến trong thời gian thực.

---

## 📋 Tính năng

✅ **Chat trực tuyến** - Gửi/nhận tin nhắn realtime  
✅ **Multi Client-Server** - Hỗ trợ nhiều client kết nối đồng thời  
✅ **Giao diện Swing** - GUI đơn giản, dễ sử dụng  
✅ **Phát sóng tin nhắn** - Broadcast tin cho tất cả user  
✅ **Kết nối ổn định** - Xử lý lỗi kết nối tự động  

---

## 🔧 Yêu cầu hệ thống

- **Java**: JDK 8 trở lên
- **OS**: Windows, Linux, macOS
- **RAM**: 256MB tối thiểu
- **Cổng**: 5000 (lắng nghe Server)

---

## 📁 Cấu trúc dự án

```
MegaChat/
├── src/
│   └── megachat/
│       ├── utils/
│       │   └── Message.java           # Lớp tin nhắn chung
│       ├── server/
│       │   ├── ChatServer.java        # Server chính
│       │   └── ClientHandler.java     # Xử lý client
│       ├── client/
│       │   ├── ChatClient.java        # Client GUI
│       │   └── ChatClientTest.java    # Client test
│       └── test/
│           └── SimpleTest.java        # Test đơn giản
├── bin/                               # Thư mục compile (tự tạo)
├── compile.bat                        # Script compile Windows
├── README.md                          # File này
└── Readme.docx                        # Yêu cầu ban đầu
```

---

## 🚀 Cách sử dụng

### 1️⃣ **Biên dịch (Compile)**

**Windows:**
```bash
cd d:\CurseForge\MegaChat
javac -d bin src\megachat\utils\Message.java src\megachat\server\ChatServer.java src\megachat\server\ClientHandler.java src\megachat\client\ChatClient.java
```

**Linux/macOS:**
```bash
cd ~/CurseForge/MegaChat
javac -d bin src/megachat/utils/Message.java src/megachat/server/ChatServer.java src/megachat/server/ClientHandler.java src/megachat/client/ChatClient.java
```

### 2️⃣ **Chạy Server**

**Windows:**
```bash
java -cp bin megachat.server.ChatServer
```

**Output:**
```
=== MegaChat Server khởi động ===
Lắng nghe trên cổng: 5000
```

Để server chạy liên tục. Không đóng terminal này!

### 3️⃣ **Chạy Client (mở terminal mới)**

**Window 1 - User1:**
```bash
java -cp bin megachat.client.ChatClient User1
```

**Window 2 - User2:**
```bash
java -cp bin megachat.client.ChatClient User2
```

**Window 3 - User3 (tùy chọn):**
```bash
java -cp bin megachat.client.ChatClient User3
```

Mỗi cửa sổ GUI sẽ hiện lên. Nhập tin nhắn và bấm **"Gửi"** hoặc **Enter**.

---

## 📝 Cách hoạt động

### **Server (ChatServer.java)**

```
PORT: 5000
├── Chấp nhận kết nối từ client
├── Tạo ClientHandler cho mỗi client
├── Lắng nghe tin nhắn từ client
└── Phát sóng (broadcast) cho tất cả client
```

**Server Log ví dụ:**
```
✓ Client kết nối: /127.0.0.1
📨 Nhận từ [User1]: Xin chào
✓ Client kết nối: /127.0.0.1
📨 Nhận từ [User2]: Hello
✗ Client ngắt kết nối. Còn: 1
```

### **Client (ChatClient.java)**

```
┌─────────────────────────────┐
│  MegaChat - User1            │
├─────────────────────────────┤
│ ✓ Đã kết nối (User1)        │
│                              │
│ [User1]: Xin chào            │
│ [User2]: Hi User1!           │
│ [User1]: Bạn khỏe không?     │
│ [User2]: Khỏe, cảm ơn       │
│                              │
├─────────────────────────────┤
│ [Input: ] Gửi               │
└─────────────────────────────┘
```

---

## 🔌 Kiến trúc Socket

### **Multi Client-Server Model**

```
         ┌──────────────────┐
         │  ChatServer      │
         │  Port: 5000      │
         └────────┬─────────┘
                  │
        ┌─────────┼─────────┐
        │         │         │
     Client1   Client2   Client3
     (User1)   (User2)   (User3)
```

### **Quy trình gửi/nhận tin**

```
1. Client gửi tin → Socket OutputStream
2. Server nhận tin → ClientHandler.readObject()
3. Server phát sóng → broadcast() loop
4. Tất cả Client nhận → Socket InputStream
5. Client hiển thị GUI → JTextArea.append()
```

---

## 💾 Lớp chính

### **Message.java** (Tin nhắn)
```java
- sender: String        // Người gửi
- content: String       // Nội dung
- type: String          // Loại: "chat", "login", "register"
- timestamp: long       // Thời gian gửi
```

### **ChatServer.java** (Server)
```java
- PORT = 5000
- clientHandlers: Set  // Danh sách client kết nối
- main()              // Chương trình chính
- broadcastMessage()  // Phát sóng tin nhắn
- removeClient()      // Loại bỏ client
```

### **ClientHandler.java** (Xử lý Client)
```java
- socket: Socket
- in/out: Streams
- run()              // Lắng nghe tin từ client
- sendMessage()      // Gửi tin cho client
- cleanup()          // Dọn dẹp
```

### **ChatClient.java** (Client GUI)
```java
- username: String
- chatArea: JTextArea      // Hiển thị tin
- messageInput: JTextField // Ô nhập tin
- connectToServer()        // Kết nối server
- sendMessage()            // Gửi tin
- listenForMessages()      // Lắng nghe server
```

---

## 🧪 Test

### **Test 1: Compile OK**
```bash
javac -d bin src\megachat\...java
# Nếu không có lỗi → OK
```

### **Test 2: Server hoạt động**
```bash
java -cp bin megachat.server.ChatServer
# Nếu in "Lắng nghe trên cổng: 5000" → OK
```

### **Test 3: Client kết nối**
```bash
java -cp bin megachat.client.ChatClient User1
# Nếu cửa sổ GUI hiện + "Đã kết nối (User1)" → OK
```

### **Test 4: Chat hoạt động**
```
1. Mở 2 Client khác nhau
2. User1 gửi: "Hello User2"
3. User2 nhận và trả lời: "Hi User1"
4. Kiểm tra tin nhắn xuất hiện trên cả 2 cửa sổ
```

---

## 🐛 Xử lý lỗi

| Lỗi | Nguyên nhân | Giải pháp |
|-----|-----------|----------|
| `Connection refused` | Server chưa chạy | Chạy `ChatServer` trước |
| `Port already in use` | Cổng 5000 đã dùng | `netstat -ano \| findstr :5000` rồi kill |
| `Cannot find symbol` | Thiếu file compile | Compile lại tất cả file |
| `UI không hiện` | Cần GUI display | Chạy trên máy có desktop |

---

## 📈 Mở rộng tương lai

- [ ] **MySQL** - Đăng nhập/đăng ký user
- [ ] **Chat riêng tư** - Tin nhắn 1-1
- [ ] **Lịch sử tin** - Lưu trữ tin nhắn
- [ ] **Avatar/Emoji** - Giao diện phong phú
- [ ] **Nhóm chat** - Tạo room chat
- [ ] **Web version** - JavaWeb + JS/HTML/CSS

---

## 📞 Liên hệ

**Dự án:** MegaChat  
**Phiên bản:** 1.0  
**Ngôn ngữ:** Java  
**Mô hình:** Multi Client-Server Socket  
**Trạng thái:** ✅ Hoạt động tốt

---

## 📄 License

MIT License - Tự do sử dụng, chỉnh sửa

---

**Chúc bạn sử dụng MegaChat vui vẻ! 🎉**
