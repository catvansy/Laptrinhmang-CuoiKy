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
- **MySQL 8.0** - Database
- **Maven** - Build tool

### Frontend
- **HTML5/CSS3** - Markup và styling
- **JavaScript (Vanilla)** - Logic và interactions
- **WebSocket API** - Real-time messaging
- **Intersection Observer** - Lazy loading
- **Fetch API** - HTTP requests

### Tools & Libraries
- **Google Fonts (Inter)** - Typography
- **Emoji** - Icons và visual elements

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
│       │   ├── MegaChatApplication.java      # Main class
│       │   ├── controller/
│       │   │   ├── HomeController.java       # Routing & static files
│       │   │   ├── AuthController.java       # Authentication
│       │   │   ├── FriendController.java     # Friend management
│       │   │   └── MessageController.java    # Message API
│       │   ├── config/
│       │   │   ├── AuthFilter.java           # Authentication filter
│       │   │   ├── WebSocketConfig.java      # WebSocket config
│       │   │   ├── ContextPathRedirectFilter.java  # 404 fix
│       │   │   ├── TomcatConfig.java         # Tomcat config
│       │   │   └── WebMvcConfig.java         # MVC config
│       │   ├── model/                        # Entity models
│       │   ├── repository/                   # JPA repositories
│       │   ├── service/                      # Business logic
│       │   └── websocket/
│       │       └── ChatEndpoint.java         # WebSocket endpoint
│       └── resources/
│           ├── static/
│           │   ├── index.html                # Redirect page
│           │   ├── landing.html              # Landing page
│           │   ├── login.html                # Login page
│           │   ├── chat.html                 # Chat interface
│           │   └── forgot-password.html      # Password reset
│           └── application.properties        # Configuration
├── pom.xml                                   # Maven config
├── start-megachat.bat                       # Startup script (Windows)
└── README.md                                 # File này
```

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
# Server
server.port=8080
server.servlet.context-path=/megachat

# Database
spring.datasource.url=jdbc:mysql://localhost:3307/megachat
spring.datasource.username=root
spring.datasource.password=

# JPA
spring.jpa.hibernate.ddl-auto=update
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

---

## 📈 Roadmap & Cải thiện tương lai

### Đã hoàn thành ✅
- [x] Real-time chat với WebSocket
- [x] Friend management
- [x] Message status indicators
- [x] Copy & Reply messages
- [x] Landing page với animations
- [x] Performance optimizations
- [x] Accessibility improvements
- [x] Mobile responsive design
- [x] Error handling với retry
- [x] Connection status indicator

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

## 👨‍💻 Tác giả

**MegaChat Team**

---

## 🙏 Lời cảm ơn

- Spring Boot team
- Discord (inspiration cho UI/UX)
- Tất cả contributors

---

**⭐ Nếu bạn thấy project này hữu ích, hãy cho một star! ⭐**

---

**Phiên bản:** 1.0.0  
**Cập nhật:** 2024  
**Trạng thái:** ✅ Đang phát triển tích cực
