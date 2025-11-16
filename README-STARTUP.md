# Hướng dẫn khởi động MegaChat

## Vấn đề thường gặp

Nếu bạn gặp lỗi **404 Not Found** khi truy cập `http://localhost:8080/megachat`, có thể do:
1. Ứng dụng chưa được khởi động
2. Port 8080 đang bị chiếm bởi process khác
3. MySQL chưa được khởi động

## Cách khởi động ứng dụng

### Cách 1: Sử dụng script tự động (Khuyến nghị)

**Windows (Batch):**
- Double-click vào file `start-megachat.bat`
- Script sẽ tự động:
  - Kiểm tra và dừng process cũ trên port 8080
  - Khởi động ứng dụng Spring Boot
  - Hiển thị log trong cửa sổ console

**Windows (PowerShell):**
- Right-click vào file `start-megachat.ps1` → "Run with PowerShell"
- Hoặc mở PowerShell và chạy: `.\start-megachat.ps1`

### Cách 2: Khởi động thủ công

1. **Mở Terminal/Command Prompt** tại thư mục dự án

2. **Kiểm tra port 8080:**
   ```bash
   netstat -ano | findstr :8080
   ```
   Nếu có process đang chạy, dừng nó:
   ```bash
   taskkill /PID <PID> /F
   ```

3. **Khởi động ứng dụng:**
   ```bash
   mvn spring-boot:run
   ```

4. **Đợi ứng dụng khởi động** (thường mất 30-60 giây)

5. **Truy cập:** `http://localhost:8080/megachat`

## Kiểm tra ứng dụng đã chạy

Sau khi khởi động, bạn sẽ thấy log như:
```
Started MegaChatApplication in X.XXX seconds
```

Và có thể truy cập:
- Trang chủ: `http://localhost:8080/megachat`
- Landing page: `http://localhost:8080/megachat/landing.html`
- Login: `http://localhost:8080/megachat/login.html`

## Lưu ý quan trọng

1. **MySQL phải được khởi động trước:**
   - Đảm bảo MySQL đang chạy trên port 3307
   - Database `megachat` đã được tạo

2. **Port 8080:**
   - Nếu port 8080 đang bị sử dụng, script sẽ tự động dừng process cũ
   - Hoặc bạn có thể đổi port trong `application.properties`

3. **Sau khi tắt máy:**
   - Ứng dụng sẽ không tự động khởi động lại
   - Bạn cần chạy script `start-megachat.bat` hoặc `start-megachat.ps1` mỗi lần khởi động máy

## Troubleshooting

### Lỗi 404 Not Found
- ✅ Kiểm tra ứng dụng đã khởi động chưa
- ✅ Kiểm tra port 8080 có đang được sử dụng không
- ✅ Đảm bảo truy cập đúng URL: `http://localhost:8080/megachat` (có `/megachat`)

### Lỗi kết nối database
- ✅ Kiểm tra MySQL đang chạy
- ✅ Kiểm tra port MySQL (mặc định 3307)
- ✅ Kiểm tra username/password trong `application.properties`

### Port đã được sử dụng
- Chạy script sẽ tự động dừng process cũ
- Hoặc dừng thủ công:
  ```bash
  netstat -ano | findstr :8080
  taskkill /PID <PID> /F
  ```

## Liên hệ

Nếu vẫn gặp vấn đề, vui lòng kiểm tra:
1. Log trong console để xem lỗi cụ thể
2. File `application.properties` có đúng cấu hình không
3. MySQL và các dependencies đã được cài đặt đúng chưa

