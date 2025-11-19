# 💬 MegaChat - Ứng dụng Chat Web Hiện Đại

Ứng dụng chat web hiện đại được xây dựng với **Spring Boot**, **WebSocket**, và **MySQL**. Giao diện đẹp mắt theo phong cách Discord với nhiều tính năng tiên tiến.

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.15-brightgreen)
![Java](https://img.shields.io/badge/Java-11-orange)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![WebSocket](https://img.shields.io/badge/WebSocket-Enabled-yellow)

---

## ✨ Tính năng chính

### 💬 Chat & Messaging
- ✅ **Chat real-time** - Gửi/nhận tin nhắn tức thời qua WebSocket
- ✅ **Quản lý bạn bè** - Tìm kiếm, thêm, xóa bạn bè dễ dàng
- ✅ **Trạng thái tin nhắn** - Hiển thị đang gửi, đã gửi, đã đọc
- ✅ **Reply tin nhắn** - Trả lời tin nhắn cụ thể với preview
- ✅ **Copy tin nhắn** - Sao chép nội dung tin nhắn nhanh chóng
- ✅ **Gửi file** - Upload và chia sẻ hình ảnh, file
- ✅ **Timestamps thông minh** - Hiển thị thời gian tương đối ("2 phút trước", "Hôm qua")

### 🎨 Giao diện & UX
- ✅ **Giao diện Discord-like** - Thiết kế hiện đại, đẹp mắt
- ✅ **Landing page ấn tượng** - Nhiều animations và visual effects
- ✅ **Responsive design** - Tối ưu cho mobile và desktop
- ✅ **Dark theme** - Giao diện tối dễ nhìn
- ✅ **Loading skeletons** - Hiển thị khi đang tải dữ liệu
- ✅ **Toast notifications** - Thông báo đẹp mắt
- ✅ **Keyboard shortcuts** - Phím tắt để tăng tốc độ sử dụng

### ⚡ Performance & Optimization
- ✅ **Lazy loading images** - Tải ảnh khi cần
- ✅ **Debounce search** - Tối ưu tìm kiếm
- ✅ **Throttle scroll** - Tối ưu scroll performance
- ✅ **Image optimization** - Async decoding, proper sizing
- ✅ **Connection status** - Hiển thị trạng thái kết nối

### 🔒 Bảo mật & Authentication
- ✅ **Đăng nhập/Đăng ký** - Xác thực người dùng
- ✅ **Session management** - Quản lý phiên đăng nhập
- ✅ **Password reset** - Quên mật khẩu
- ✅ **Protected routes** - Bảo vệ các trang cần đăng nhập

### ♿ Accessibility
- ✅ **ARIA labels** - Hỗ trợ screen readers
- ✅ **Keyboard navigation** - Điều hướng bằng bàn phím
- ✅ **Focus management** - Quản lý focus hợp lý
- ✅ **Semantic HTML** - HTML có ý nghĩa

---

## 🛠️ Công nghệ sử dụng

### Backend
- **Spring Boot 2.7.15** - Framework chính
- **Spring WebSocket** - Real-time communication
- **Spring Data JPA** - Database ORM
- **Spring Security Crypto** - Password encoding
- **MySQL 8.0** - Database (Driver 8.0.33)
- **Maven** - Build tool
- **Lombok** - Reduce boilerplate code
- **Spring Boot DevTools** - Auto reload (development)

### Frontend
- **HTML5/CSS3** - Markup và styling
- **JavaScript (Vanilla)** - Logic và interactions
- **WebSocket API** - Real-time messaging
- **Intersection Observer** - Lazy loading images
- **Fetch API** - HTTP requests

### Tools & Libraries
- **Google Fonts (Inter)** - Typography
- **Emoji** - Icons và visual elements
- **Cloudflared** - Public tunnel (optional)

---

## 📋 Yêu cầu hệ thống

- **Java**: JDK 11 trở lên
- **Maven**: 3.6 trở lên
- **MySQL**: 8.0 trở lên (chạy trên port 3307)
- **RAM**: 512MB tối thiểu
- **OS**: Windows, Linux, macOS

---

## 🚀 Cài đặt và Chạy

### 1. Clone repository
```bash
git clone https://github.com/catvansy/Laptrinhmang-CuoiKy.git
cd Laptrinhmang-CuoiKy
```

### 2. Cấu hình MySQL
Đảm bảo MySQL đang chạy trên port **3307** với:
- **Username**: `root`
- **Password**: (trống)
- **Database**: `megachat` (sẽ tự động tạo nếu chưa có)

### 3. Khởi động ứng dụng

**Windows:**
```bash
# Cách 1: Sử dụng script tự động (Khuyến nghị)
start-megachat.bat

# Cách 2: Chạy thủ công
mvn spring-boot:run
```

**Linux/macOS:**
```bash
mvn spring-boot:run
```

### 4. Truy cập ứng dụng

Sau khi server khởi động (khoảng 20-30 giây), truy cập:

- **Trang chủ**: http://localhost:8080/megachat
- **Landing page**: http://localhost:8080/megachat/landing.html
- **Đăng nhập**: http://localhost:8080/megachat/login.html
- **Chat**: http://localhost:8080/megachat/chat.html

⚠️ **Lưu ý**: Phải có `/megachat` ở cuối URL!

---

## 📁 Cấu trúc dự án

```
Laptrinhmang-CuoiKy/
├── src/
│   └── main/
│       ├── java/com/megachat/
│       │   ├── MegaChatApplication.java          # Main application class
│       │   ├── controller/
│       │   │   ├── HomeController.java           # Static file routing
│       │   │   ├── AuthController.java           # Authentication API (/api/auth)
│       │   │   ├── FriendshipController.java     # Friend management API (/api/friends)
│       │   │   └── ChatController.java           # Message API (/api/messages)
│       │   ├── config/
│       │   │   ├── AuthFilter.java               # Authentication filter
│       │   │   ├── WebSocketConfig.java          # WebSocket configuration
│       │   │   ├── ContextPathRedirectFilter.java  # Context path handler
│       │   │   ├── DatabaseInitializer.java      # Database initialization
│       │   │   ├── TomcatConfig.java             # Tomcat configuration
│       │   │   └── WebMvcConfig.java             # MVC configuration
│       │   ├── model/
│       │   │   ├── User.java                     # User entity
│       │   │   ├── ChatMessage.java              # Message entity
│       │   │   ├── Friendship.java               # Friendship entity
│       │   │   └── FriendshipStatus.java         # Friendship status enum
│       │   ├── repository/
│       │   │   ├── UserRepository.java           # User repository
│       │   │   ├── ChatMessageRepository.java    # Message repository
│       │   │   └── FriendshipRepository.java     # Friendship repository
│       │   ├── service/
│       │   │   ├── UserService.java              # User business logic
│       │   │   ├── ChatMessageService.java       # Message business logic
│       │   │   ├── FriendshipService.java        # Friendship business logic
│       │   │   └── FileStorageService.java       # File upload service
│       │   ├── dto/
│       │   │   └── FriendRequestDto.java         # Friend request DTO
│       │   └── websocket/
│       │       └── ChatEndpoint.java             # WebSocket endpoint
│       └── resources/
│           ├── static/
│           │   ├── index.html                    # Redirect page
│           │   ├── landing.html                  # Landing page
│           │   ├── login.html                    # Login/Register page
│           │   ├── chat.html                     # Chat interface
│           │   └── forgot-password.html          # Password reset page
│           └── application.properties            # Application configuration
├── pom.xml                                       # Maven configuration
├── start-megachat.bat                           # Startup script (Windows)
├── start-megachat with cloudflared.bat          # Startup with Cloudflare tunnel
├── cloudflared.exe                               # Cloudflare tunnel binary
├── init.sql                                      # Database initialization script
└── README.md                                     # Documentation
```

---

## 🔌 API Endpoints

### Authentication (`/api/auth`)
- `POST /api/auth/register` - Đăng ký tài khoản mới
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/logout` - Đăng xuất
- `POST /api/auth/forgot-password` - Gửi email reset mật khẩu
- `POST /api/auth/reset-password` - Reset mật khẩu với token

### Friends (`/api/friends`)
- `GET /api/friends` - Lấy danh sách bạn bè
- `POST /api/friends/requests` - Gửi lời mời kết bạn
- `PUT /api/friends/requests/{requestId}/accept` - Chấp nhận lời mời
- `PUT /api/friends/requests/{requestId}/reject` - Từ chối lời mời
- `DELETE /api/friends/{friendId}` - Xóa bạn bè
- `GET /api/friends/search?q={username}` - Tìm kiếm người dùng

### Messages (`/api/messages`)
- `GET /api/messages?friendId={id}` - Lấy lịch sử tin nhắn
- `POST /api/messages` - Gửi tin nhắn mới
- `POST /api/messages/upload` - Upload file/ảnh
- `GET /api/messages/files/{filename}` - Tải file đã upload

### WebSocket
- `ws://localhost:8080/megachat/chat` - Kết nối WebSocket cho chat real-time

---

## 🎯 Các tính năng chi tiết

### Chat Interface
- **Danh sách bạn bè** - Sidebar bên trái với search
- **Chat area** - Hiển thị tin nhắn với timestamps
- **Input area** - Gửi tin nhắn và file
- **Message actions** - Copy, Reply, xem timestamp

### Landing Page
- **Hero section** - Giới thiệu với animations
- **Trust indicators** - Badges tin cậy
- **How It Works** - 3 bước đơn giản
- **Features** - Giới thiệu tính năng
- **Testimonials** - Phản hồi người dùng
- **FAQ** - Câu hỏi thường gặp
- **CTA** - Call to action

### Keyboard Shortcuts
- `Ctrl+K` hoặc `/` - Tìm kiếm bạn bè
- `Ctrl+Enter` - Gửi tin nhắn
- `Esc` - Đóng modal/dropdown
- `Ctrl+/` - Hiển thị danh sách shortcuts

---

## 🔧 Cấu hình

### application.properties
```properties
# Server Configuration
server.port=8080
server.address=0.0.0.0
server.servlet.context-path=/megachat
server.servlet.session.timeout=30m

# MySQL Database
spring.datasource.url=jdbc:mysql://localhost:3307/megachat?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.hikari.maximum-pool-size=5

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# File Upload
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# Static Resources
spring.web.resources.static-locations=classpath:/static/
spring.web.resources.cache.period=3600
```

---

## 🐛 Xử lý lỗi thường gặp

### Lỗi 404 khi truy cập `/megachat`
**Nguyên nhân**: Context path không được xử lý đúng  
**Giải pháp**: Đã được fix vĩnh viễn với `ContextPathRedirectFilter` và `TomcatConfig`. Nếu vẫn gặp:
1. Dừng server: `taskkill /F /IM java.exe`
2. Rebuild: `mvn clean install`
3. Khởi động lại: `start-megachat.bat`

### Port 8080 đã được sử dụng
**Giải pháp**: Script `start-megachat.bat` tự động xử lý. Hoặc thủ công:
```bash
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### MySQL connection error
**Giải pháp**: 
1. Kiểm tra MySQL đang chạy trên port 3307
2. Kiểm tra username/password trong `application.properties`
3. Đảm bảo database `megachat` tồn tại hoặc để JPA tự tạo
4. Kiểm tra MySQL service: `net start MySQL` (Windows) hoặc `sudo systemctl start mysql` (Linux)

### Maven không tìm thấy dependencies
**Giải pháp**:
1. Kiểm tra kết nối internet
2. Xóa cache: `mvn clean`
3. Tải lại dependencies: `mvn dependency:resolve`
4. Rebuild project: `mvn clean install`

### File upload không hoạt động
**Giải pháp**:
1. Kiểm tra thư mục `uploads/` có tồn tại trong project root
2. Đảm bảo quyền ghi file cho thư mục `uploads/`
3. Kiểm tra `spring.servlet.multipart` configuration trong `application.properties`

### WebSocket không kết nối được
**Giải pháp**:
1. Kiểm tra WebSocket config trong `WebSocketConfig.java`
2. Đảm bảo URL kết nối đúng: `ws://localhost:8080/megachat/chat`
3. Kiểm tra CORS settings nếu chạy frontend từ domain khác
4. Xem console browser để kiểm tra lỗi WebSocket

---

## 📈 Roadmap & Cải thiện tương lai

### Đã hoàn thành ✅
- [x] Real-time chat với WebSocket
- [x] Friend management (thêm, xóa, tìm kiếm)
- [x] Message status indicators (đang gửi, đã gửi, đã đọc)
- [x] Copy & Reply messages
- [x] File upload và chia sẻ
- [x] Landing page với animations
- [x] Performance optimizations (lazy loading, debounce, throttle)
- [x] Accessibility improvements (ARIA, keyboard navigation)
- [x] Mobile responsive design
- [x] Error handling với retry mechanism
- [x] Connection status indicator
- [x] Password reset functionality
- [x] Session management
- [x] Protected routes với AuthFilter

### Đang phát triển 🚧
- [ ] PWA support (Service Worker, offline access)
- [ ] Tách CSS/JS ra file riêng
- [ ] Drag & drop files
- [ ] Better file preview (PDF, video, audio)
- [ ] Message reactions (emoji)
- [ ] Typing indicators
- [ ] Read receipts chi tiết hơn

### Kế hoạch 📋
- [ ] Group chat / Channels
- [ ] Voice/Video call
- [ ] File sharing improvements
- [ ] Theme customization
- [ ] Notification system
- [ ] Message search
- [ ] Message pinning

---

## 📚 Tài liệu tham khảo

- [FRONTEND-IMPROVEMENTS.md](./FRONTEND-IMPROVEMENTS.md) - Đề xuất cải thiện frontend
- [FRONTEND-NEXT-STEPS.md](./FRONTEND-NEXT-STEPS.md) - Các bước tiếp theo
- [PERFORMANCE-OPTIMIZATIONS.md](./PERFORMANCE-OPTIMIZATIONS.md) - Tối ưu hiệu năng

---

## 🤝 Đóng góp

Mọi đóng góp đều được chào đón! Vui lòng:
1. Fork project
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

---

## 📄 License

MIT License - Tự do sử dụng, chỉnh sửa và phân phối

---

## 🗄️ Cơ sở dữ liệu

### Bảng chính

#### User
- `id` - Primary key (BIGINT)
- `username` - Tên người dùng (VARCHAR, UNIQUE)
- `email` - Email (VARCHAR, UNIQUE)
- `phone` - Số điện thoại (VARCHAR)
- `password` - Mật khẩu đã hash (VARCHAR)
- `reset_token` - Token reset mật khẩu (VARCHAR)
- `reset_token_expiry` - Thời gian hết hạn token (TIMESTAMP)
- `created_at` - Thời gian tạo (TIMESTAMP)

#### ChatMessage
- `id` - Primary key (BIGINT)
- `sender_id` - ID người gửi (BIGINT, FK -> User)
- `receiver_id` - ID người nhận (BIGINT, FK -> User)
- `content` - Nội dung tin nhắn (TEXT)
- `reply_to_id` - ID tin nhắn được reply (BIGINT, FK -> ChatMessage, nullable)
- `file_path` - Đường dẫn file (VARCHAR, nullable)
- `timestamp` - Thời gian gửi (TIMESTAMP)
- `read` - Đã đọc chưa (BOOLEAN)

#### Friendship
- `id` - Primary key (BIGINT)
- `requester_id` - ID người gửi lời mời (BIGINT, FK -> User)
- `addressee_id` - ID người nhận lời mời (BIGINT, FK -> User)
- `status` - Trạng thái (ENUM: PENDING, ACCEPTED, REJECTED)
- `request_message` - Lời nhắn kèm theo (TEXT, nullable)
- `created_at` - Thời gian tạo (TIMESTAMP)
- `updated_at` - Thời gian cập nhật (TIMESTAMP)

Database sẽ tự động được tạo bởi JPA với `ddl-auto=update` khi ứng dụng khởi động lần đầu.

---

## 📊 Luồng hoạt động

### Chat Flow
1. Người dùng đăng nhập → Session được tạo
2. Kết nối WebSocket → Join chat room
3. Gửi tin nhắn → Frontend gửi qua WebSocket
4. Server nhận → Lưu vào database → Broadcast đến receiver
5. Receiver nhận → Cập nhật UI real-time
6. Đọc tin nhắn → Gửi ACK → Cập nhật trạng thái đã đọc

### Friend Management Flow
1. Tìm kiếm người dùng → API `/api/friends/search`
2. Gửi lời mời → POST `/api/friends/requests`
3. Nhận lời mời → Danh sách pending requests
4. Chấp nhận/Từ chối → PUT `/api/friends/requests/{id}/accept|reject`
5. Khi chấp nhận → Friendship status = ACCEPTED
6. Xóa bạn bè → DELETE `/api/friends/{id}`

---

## 👨‍💻 Tác giả

**MegaChat Team**

---

## 🙏 Lời cảm ơn

- Spring Boot team - Framework tuyệt vời
- Discord - Inspiration cho UI/UX design
- Tất cả contributors đã đóng góp cho project

---

**⭐ Nếu bạn thấy project này hữu ích, hãy cho một star! ⭐**

---

**Phiên bản:** 1.0.0  
**Cập nhật:** 2024  
**Trạng thái:** ✅ Đang phát triển tích cực  
**License:** MIT License
