# ⚡ Performance Optimizations & Image Optimization

## 📋 Tổng Quan
Tài liệu này mô tả các tối ưu performance và image optimization đã được implement trong MegaChat.

---

## ✅ Đã Implement

### 1. **Image Optimization** 🖼️

#### Lazy Loading
- ✅ Native `loading="lazy"` attribute cho tất cả images
- ✅ Intersection Observer API cho advanced lazy loading
- ✅ Images chỉ load khi sắp vào viewport (50px margin)

#### Image Attributes
- ✅ `decoding="async"` - Decode images asynchronously
- ✅ `loading="lazy"` - Native browser lazy loading
- ✅ Responsive sizing với `width: 100%; height: auto`
- ✅ `object-fit: contain` để maintain aspect ratio

#### Error Handling
- ✅ Fallback SVG placeholder khi image load fail
- ✅ Graceful degradation

**Code Example:**
```html
<img src="${imageUrl}" 
     alt="${imageName}"
     loading="lazy"
     decoding="async"
     style="max-width: 300px; max-height: 300px; ..."
     onerror="this.onerror=null; this.src='data:image/svg+xml,...';">
```

---

### 2. **Search Debouncing** 🔍

**Trước:** Search ngay khi user gõ (300ms delay)
**Sau:** Debounce 500ms để giảm API calls

**Lợi ích:**
- Giảm số lượng API requests
- Giảm server load
- Cải thiện UX (không search quá nhiều lần)

**Code:**
```javascript
// Debounce search (wait 500ms after user stops typing)
state.searchTimeout = setTimeout(() => {
    searchUsers(keyword);
}, 500);
```

---

### 3. **Scroll Optimization** 📜

#### Throttling
- ✅ Throttle function để limit scroll event frequency
- ✅ Throttle limit: 100ms

#### requestAnimationFrame
- ✅ Sử dụng `requestAnimationFrame` cho smooth scrolling
- ✅ Batch DOM updates

**Code:**
```javascript
function scrollMessagesToBottom() {
    requestAnimationFrame(() => {
        messagesContainer.scrollTop = messagesContainer.scrollHeight;
    });
}
```

---

### 4. **Rendering Optimization** 🎨

#### Fragment & Batch Updates
- ✅ Sử dụng `DocumentFragment` để batch DOM updates
- ✅ `requestAnimationFrame` cho smooth rendering
- ✅ Giảm reflows và repaints

**Code:**
```javascript
const fragment = document.createDocumentFragment();
messages.forEach(msg => {
    // Build elements
    fragment.appendChild(wrapper);
});

requestAnimationFrame(() => {
    messagesContainer.innerHTML = '';
    messagesContainer.appendChild(fragment);
    scrollMessagesToBottom();
    initImageLazyLoading();
});
```

---

### 5. **Intersection Observer** 👁️

**Mục đích:** Advanced lazy loading cho images

**Features:**
- Observe images khi vào viewport
- Root margin: 50px (load trước khi vào viewport)
- Auto unobserve sau khi load xong

**Code:**
```javascript
const imageObserver = new IntersectionObserver((entries, observer) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            const img = entry.target;
            if (img.dataset.src && !img.src) {
                img.src = img.dataset.src;
                img.removeAttribute('data-src');
            }
            observer.unobserve(img);
        }
    });
}, {
    rootMargin: '50px'
});
```

---

### 6. **Performance Monitoring** 📊

**Function:** `measurePerformance(name, fn)`

**Features:**
- Measure execution time của functions
- Warning nếu > 100ms
- Sử dụng Performance API

**Usage:**
```javascript
measurePerformance('renderMessages', () => {
    renderMessages(friendId);
});
```

---

### 7. **Resource Hints** 🔗

**Landing Page:**
- ✅ `preconnect` cho Google Fonts
- ✅ `dns-prefetch` cho external resources
- ✅ `preload` cho critical CSS

**Code:**
```html
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="dns-prefetch" href="https://fonts.googleapis.com">
<link rel="preload" href="..." as="style">
```

---

## 📈 Performance Metrics

### Before Optimization
- Image loading: Eager (load tất cả ngay)
- Search: 300ms debounce
- Scroll: No throttling
- Rendering: Direct DOM updates

### After Optimization
- Image loading: Lazy (chỉ load khi cần)
- Search: 500ms debounce
- Scroll: Throttled + requestAnimationFrame
- Rendering: Batched với Fragment

### Expected Improvements
- ⚡ **Faster Initial Load:** 30-50% faster
- 📉 **Reduced Bandwidth:** 40-60% less data
- 🎯 **Better UX:** Smoother scrolling, faster search
- 💾 **Lower Memory:** Images chỉ load khi cần

---

## 🎯 Best Practices Applied

1. **Lazy Loading**
   - Native `loading="lazy"` cho browser support
   - Intersection Observer cho advanced control

2. **Debouncing/Throttling**
   - Debounce cho user input (search)
   - Throttle cho scroll events

3. **Batch DOM Updates**
   - Fragment để batch updates
   - requestAnimationFrame cho smooth rendering

4. **Resource Hints**
   - Preconnect cho external domains
   - Preload cho critical resources

5. **Error Handling**
   - Fallback images khi load fail
   - Graceful degradation

---

## 🔄 Future Optimizations (Optional)

### 1. **WebP Format Support**
```javascript
// Check WebP support
function supportsWebP() {
    const canvas = document.createElement('canvas');
    return canvas.toDataURL('image/webp').indexOf('data:image/webp') === 0;
}

// Use WebP if supported
const imageUrl = supportsWebP() ? `${url}.webp` : `${url}.jpg`;
```

### 2. **Responsive Images**
```html
<picture>
    <source srcset="image-small.webp" media="(max-width: 600px)" type="image/webp">
    <source srcset="image-small.jpg" media="(max-width: 600px)">
    <source srcset="image-large.webp" type="image/webp">
    <img src="image-large.jpg" alt="..." loading="lazy">
</picture>
```

### 3. **Virtual Scrolling**
- Chỉ render messages trong viewport
- Giảm DOM nodes cho conversations dài

### 4. **Service Worker Caching**
- Cache images và static assets
- Offline support

### 5. **Code Splitting**
- Tách CSS/JS ra file riêng
- Lazy load components

---

## 📚 Resources

- [Web.dev - Image Optimization](https://web.dev/fast/#optimize-your-images)
- [MDN - Intersection Observer](https://developer.mozilla.org/en-US/docs/Web/API/Intersection_Observer_API)
- [Web.dev - Lazy Loading Images](https://web.dev/lazy-loading-images/)
- [MDN - requestAnimationFrame](https://developer.mozilla.org/en-US/docs/Web/API/window/requestAnimationFrame)

---

## ✅ Checklist

- [x] Lazy loading cho images
- [x] Debounce search
- [x] Throttle scroll
- [x] Batch DOM updates
- [x] Intersection Observer
- [x] Performance monitoring
- [x] Resource hints
- [x] Error handling
- [ ] WebP support (optional)
- [ ] Responsive images (optional)
- [ ] Virtual scrolling (optional)

---

**Lưu ý:** Các optimizations này đã được implement và test. Monitor performance metrics để đảm bảo improvements.

