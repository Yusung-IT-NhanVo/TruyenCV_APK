# Kiến Trúc & Đặc Tả Triển Khai API Hệ Thống Đọc Truyện (TruyenCV & MotTruyen)

Tài liệu này trích xuất toàn bộ phần cốt lõi của việc gọi và thực thi API từ dự án gốc (`TruyenCv/TruyenCVApp`), bao gồm cấu trúc Endpoint, cơ chế xử lý tham số, kỹ thuật mapping dữ liệu chuẩn hóa, xử lý ngoại lệ và luồng tải truyện.

---

## 1. Tổng Quan Kiến Trúc Nguồn Truyện

Hệ thống hỗ trợ 2 nguồn truyện chính và 1 lớp Adapter gộp (Unified Adapter):
1. **TruyenCV.io**: WordPress REST API kết hợp Custom Endpoints (`/initmanga/v1/`).
2. **MotTruyen**: Custom Backend REST API (`http://api.mottruyen.com`).
3. **Unified Source**: Điều phối, ánh xạ chéo thể loại và gộp kết quả xen kẽ (interleave).

### Định Danh Dữ Liệu (ID Prefixing)
Để phân biệt nguồn dữ liệu trên toàn hệ thống (UI, Database, Bookmark, History):
- Nguồn **TruyenCV**: prefix `tcv-` (Ví dụ truyện: `tcv-1234`, chương: `tcv-56789`).
- Nguồn **MotTruyen**: prefix `mt-` (Ví dụ truyện: `mt-987`, chương: `mt-112233`).

---

## 2. Nguồn 1: TruyenCV.io API

### 2.1. Cấu hình cơ bản
- **Base URL**: `https://truyencv.io/wp-json`
- **Proxy Dev (Vite)**: `/truyencv-api` -> `https://truyencv.io/wp-json`
- **Headers bắt buộc**:
  ```http
  User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36
  Accept: application/json, text/plain, */*
  ```

### 2.2. Danh sách Endpoints & Tham số

| Chức năng | Method | Endpoint & Parameters | Mô tả & Lưu ý |
| :--- | :---: | :--- | :--- |
| **Truyện Hot / Mới** | `GET` | `/wp/v2/manga?page={page}&per_page={perPage}&_embed=1` | Lấy danh sách truyện mới nhất hoặc hot. Cần `_embed=1` để lấy taxonomy và thumbnail. |
| **Truyện Đề Cử** | `GET` | `/wp/v2/manga?genre=639&page={page}&per_page={perPage}&_embed=wp:featuredmedia,author,wp:term` | Genre `639` là thể loại quy ước dành cho "Đề Cử". |
| **Bảng Xếp Hạng** | `GET` | `/initmanga/v1/ranking?type={period}&per_page={perPage}&paged={page}` | `type`: `day`, `week`, `month`, `all`. |
| **Fallback Xếp Hạng** | `GET` | `/wp/v2/manga?page={page}&per_page={perPage}&orderby={views/modified}&order=desc&_embed=wp:featuredmedia,author,wp:term` | Dự phòng tự phục hồi nếu `/initmanga/v1/ranking` không phản hồi. |
| **Danh Mục Thể Loại** | `GET` | `/wp/v2/genre?per_page=100` | Trả về danh sách thể loại: id, name, slug, count. |
| **Lọc Theo Số Chương** | `GET` | `/initmanga/v1/manga-by-chapters?min={min}&max={max}` | Trả về JSON `{ success: true, ids: [...] }`. Sau đó query `/wp/v2/manga?include={ids}`. (Khuyến nghị timeout 4s). |
| **Tìm Kiếm / Lọc Tổng Hợp** | `GET` | `/wp/v2/manga?page={page}&per_page={perPage}&search={kw}&genre={genreId}&team={team}&manga_status={completed/ongoing}&orderby={sort}&order=desc&_embed=...` | Hỗ trợ lọc kết hợp nhiều điều kiện. |
| **Chi Tiết Truyện** | `GET` | `/wp/v2/manga/{id}?_embed=1` | Thông tin chi tiết truyện theo ID số (đã bỏ prefix `tcv-`). |
| **Danh Sách Chương (Phân Trang)** | `GET` | `/initmanga/v1/chapters?manga_id={id}&per_page=50&paged={page}` | Trả về `{ items: [...], total_pages: N }`. Cần quét song song các trang còn lại theo batch. |
| **Nội Dung Chương** | `GET` | `/initmanga/v1/chapter/{chapterId}` | Trả về `{ id, number, slug, title, content }`. |

### 2.3. Bảng Tham Số Sắp Xếp (Sort / Orderby)
- Mới cập nhật: `orderby=modified&order=desc`
- Đăng mới: `orderby=date&order=desc`
- Lượt đọc tổng: `orderby=views&order=desc`
- Lượt đọc tháng: `orderby=views_month&order=desc`
- Lượt đọc tuần: `orderby=views_week&order=desc`
- Đánh giá: `orderby=rating&order=desc`

### 2.4. Danh Sách Tính Cách Nhân Vật Độc Quyền (Character Personalities)
TruyenCV phân loại truyện theo taxonomy đặc thù (dùng filter theo `genre` ID):
- `1192`: Vô Sỉ (`vo-si`)
- `1046`: Cơ Trí (`co-tri`)
- `1034`: Lãnh Khốc (`lanh-khoc`)
- `857`: Không Thánh Mẫu (`khong-thanh-mau`)
- `858`: Phàm Nhân (`pham-nhan`)
- `155`: Phản Phái (`phan-phai`)
- `197`: Hài Hước (`hai-huoc`)
- `1114`: Sảng Văn (`sang-van`)
- `1112`: Vả Mặt (`va-mat`)
- `196`: Sau Màn (`sau-man`)
- `1194`: Xây Dựng Thế Lực (`xay-dung-the-luc`)
- `639`: Đề Cử (`de-cu`)

### 2.5. Xử Lý Dữ Liệu Đặc Thù Của TruyenCV
1. **Phân giải Tác Giả (Author)**:
   - Trường `item.author` trong WP REST API thường chỉ là User ID của bot crawler (ví dụ: `1123`).
   - Tác giả thực nằm trong `_embedded['wp:term']` với taxonomy `author_tax`.
   - Fallback 1: Trích xuất từ `item.class_list` dạng chuỗi `author_tax-{slug}`.
   - Fallback 2: `item.author_name` hoặc mặc định `'Chưa rõ'`.
2. **Phân giải Thể Loại (Genres)**:
   - Trích xuất từ `_embedded['wp:term']` với taxonomy `genre`.
   - Fallback: Lấy từ `item.class_list` dạng `genre-{slug}`.
3. **Phân giải Số Chương (Enrichment)**:
   - Danh sách `/wp/v2/manga` không có sẵn số chương chính xác.
   - Cần gọi kiểm tra `/initmanga/v1/chapters?manga_id={id}&per_page=1&paged=1` -> đọc `total_pages`. Dùng `Map` in-memory cache để giảm số lượng request.
4. **Làm Sạch Nội Dung Chương**:
   - Escape slashes: Gặp lỗi ký tự JSON/MySQL gạch chéo (`\"` -> `"`, `\'` -> `'`, `\\` -> `\`).
   - Xóa bỏ dòng lặp tiêu đề chương ở đầu bài viết (Regex: `/^(?:<p>)?[\s\r\n]*Chương\s+\d+[^<\r\n]*(?:<\/p>|<br\s*\/?>)+/i`).

---

## 3. Nguồn 2: MotTruyen API

### 3.1. Cấu hình cơ bản
- **Base URL**: `http://api.mottruyen.com`
- **Proxy Dev (Vite)**: `/mottruyen-api` -> `http://api.mottruyen.com`
- **Default Query Params**: `deviceid=antigravity&os=android&app_version=1.0.6`

### 3.2. Danh sách Endpoints & Tham số

| Chức năng | Method | Endpoint & Parameters | Mô tả & Lưu ý |
| :--- | :---: | :--- | :--- |
| **Bộ Lọc Truyện Đa Năng** | `GET` | `/filter?kind={kind}&cat={cat}&status={status}&totalchapter={tc}&date={date}&sort={sort}&page={page}&...` | Endpoint chính cho Hot, Đề cử, Bảng xếp hạng và Tìm kiếm theo danh mục. |
| **Tìm Kiếm Theo Tên** | `GET` | `/tim-kiem/?k={keyword}&page_number={page}` | Tìm kiếm truyện theo từ khóa. |
| **Danh Mục Thể Loại** | `GET` | `/getListCategory` | Trả về `{ data: [{ ID, NAME, SLUG }] }`. |
| **Chi Tiết Truyện** | `GET` | `/story/?story_id={id}&page_numer=1&page_size=1` | Chi tiết một bộ truyện theo `ID` (đã bỏ prefix `mt-`). |
| **Danh Sách Toàn Bộ Chương** | `GET` | `/listchap?story_id={id}` | **Rất nhanh:** Trả về toàn bộ danh sách chương trong 1 request duy nhất `{ data: [{ ID, NAME, ORDER }] }`. |
| **Nội Dung Chương** | `GET` | `/chapter/?chapter_id={chapterId}` | Trả về `{ data: { ID, NAME, ORDER, CONTENT } }`. |

### 3.3. Bảng Mã Hóa Tham Số Filter của MotTruyen
- **`kind` (Loại truyện)**:
  - `0`: Tất cả
  - `1`: Convert
  - `2`: Sáng tác
- **`status` (Trạng thái)**:
  - `0`: Tất cả
  - `1`: Đang ra (ongoing)
  - `2`: Hoàn thành (completed)
- **`totalchapter` (Số lượng chương)**:
  - `0`: Tất cả
  - `1`: Dưới 100 chương
  - `2`: 100 - 300 chương
  - `3`: 300 - 600 chương
  - `4`: 600 - 1000 chương
  - `5`: Trên 1000 chương
- **`sort` (Sắp xếp)**:
  - `0`: Mới cập nhật (Default)
  - `1`: Truyện mới đăng
  - `2`: Lượt đọc All-time
  - `3`: Lượt đọc Tháng
  - `4`: Lượt đọc Tuần
  - `5`: Lượt đọc Ngày
  - `6`: Đề cử All-time
  - `7`: Đề cử Tháng
  - `8`: Điểm đánh giá (Rating)
  - `9`: Số lượng chương

---

## 4. Unified Source & Bảng Ánh Xạ Thể Loại Chéo (Genre Cross-Mapping)

Để gộp 2 nguồn thành một giao diện đồng nhất, hệ thống chuẩn hóa thể loại qua một Slug trung gian:

| Canonical Slug | Tên Thể Loại | ID TruyenCV (`genre`) | ID MotTruyen (`cat`) |
| :--- | :--- | :---: | :---: |
| `tien-hiep` | Tiên Hiệp | 16 | 14 |
| `kiem-hiep` | Kiếm Hiệp | 86 | 16 |
| `do-thi` | Đô Thị | 23 | 15 |
| `huyen-huyen` | Huyền Huyễn | 72 | 17 |
| `huyen-ao` | Huyền Ảo | 1148 | 17 |
| `khoa-huyen` | Khoa Huyễn | 39 | 22 |
| `vong-du` | Võng Du | 167 | 20 |
| `dong-nhan` | Đồng Nhân | 792 | 28 |
| `lich-su` | Lịch Sử | 145 | 24 |
| `quan-su` | Quân Sự | 1121 | 23 |
| `di-nang` | Dị Năng | 134 | 19 |
| `mat-the` | Mạt Thế | 40 | 32 |
| `ngon-tinh` | Ngôn Tình | 19 | 18 |
| `xuyen-khong` | Xuyên Không | 34 | 25 |
| `trung-sinh` | Trùng Sinh | 1244 | 30 |
| `canh-ky` | Cạnh Kỹ | 1292 | 27 |
| `linh-di` | Linh Dị | 82 | 31 |
| `he-thong` | Hệ Thống | 57 | 19 |
| `de-cu` | Đề Cử | 639 | 0 |
| `convert` | Convert | 972 | 1 |

### Thuật toán Xen Kẽ Kết Quả (Interleave)
Khi người dùng chọn chế độ `Tất cả nguồn`:
```javascript
function interleave(arr1 = [], arr2 = []) {
  const result = [];
  const maxLen = Math.max(arr1.length, arr2.length);
  for (let i = 0; i < maxLen; i++) {
    if (i < arr1.length) result.push(arr1[i]);
    if (i < arr2.length) result.push(arr2[i]);
  }
  return result;
}
```

---

## 5. Cấu Trúc Thực Thể Dữ Liệu Chuẩn Hóa (Standard Models)

### 5.1. Story / Novel Model
```typescript
interface Novel {
  id: string;               // "tcv-1234" hoặc "mt-5678"
  raw_id: number | string;  // 1234 hoặc 5678
  title: string;            // Tên truyện
  author: string;           // Tác giả
  thumb: string;            // URL ảnh bìa
  coverUrl: string;         // Alias cho thumb
  desc: string;             // Mô tả truyện (đã strip HTML)
  genres: string[];         // Mảng thể loại ['Tiên Hiệp', 'Dị Giới']
  cat: string;              // Chuỗi thể loại nối bằng dấu phẩy
  total_chapters: number;   // Tổng số chương
  views: number;            // Lượt xem
  status: 'ongoing' | 'completed';
  source: 'truyencv' | 'mottruyen';
  source_label: string;     // "TruyenCV.io" | "MotTruyen"
}
```

### 5.2. Chapter Model
```typescript
interface Chapter {
  id: string;               // "tcv-9999" hoặc "mt-8888"
  raw_id: number | string;  // ID gốc từ nguồn
  story_id: string;         // "tcv-1234"
  chapter_order: number;    // Thứ tự chương (1, 2, 3...)
  title: string;            // Chuẩn hóa dạng "Chương 1: Tiêu đề"
  raw_title: string;        // Tiêu đề gốc
  content?: string;         // Nội dung chương (HTML / Plain text)
  prev_id: string;          // ID chương trước
  next_id: string;          // ID chương kế tiếp
  source: 'truyencv' | 'mottruyen';
}
```

---

## 6. Quy Trình & Kỹ Thuật Tải Truyện Offline (Download Engine)

Kỹ thuật tải truyện được đúc kết từ `downloadService.js`:

1. **Kiểm soát Tốc Độ và Đồng Thời (Concurrency Control)**:
   - Sử dụng hàng đợi với `concurrency: 4` (tối đa 4 request đồng thời).
   - Chia danh sách chương cần tải theo các batch (mỗi đợt 50 chương).
   - Nghỉ làm mát (cooldown) `300ms - 500ms` giữa các batch để tránh bị chặn IP / rate-limit.
2. **Cơ Chế Bỏ Qua Chương Đã Tải (Idempotency)**:
   - Trước khi tải, truy vấn SQLite: `SELECT id FROM chapters WHERE novelId = ? AND isDownloaded = 1`.
   - Lọc bỏ các chương đã có nội dung hợp lệ, chỉ tải những chương còn thiếu.
3. **Cơ Chế Tự Động Thử Lại (Exponential Backoff Retry)**:
   - Mỗi chương tải lỗi sẽ thử lại tối đa 3 lần với thời gian chờ tăng dần (`300ms * attempt`).
   - Nếu kết thúc đợt tải vẫn còn chương lỗi: Chạy tự động **Pass 2 (Vét lại)** với độ trễ `400ms`.
4. **Offline Cover Image Caching**:
   - Khi bắt đầu tải một bộ truyện, fetch URL ảnh bìa -> convert sang `data:image/jpeg;base64,...` và lưu trực tiếp vào trường `coverUrl` trong database SQLite để đảm bảo truyện hiển thị đầy đủ bìa ngay cả khi ngắt kết nối mạng hoàn toàn.
5. **Khả Năng Tạm Dừng / Tiếp Tục (Resumable)**:
   - Toàn bộ tiến trình tải (novelId, chapters list, downloaded count) được lưu bền vững vào `localStorage` hoặc DB. Khi khởi động lại app, tác vụ chưa xong tự động chuyển sang trạng thái `paused` để người dùng nhấn tải tiếp mà không phải tải lại từ đầu.

---

## 7. Các Lưu Ý Kỹ Thuật Quan Trọng Khi Xây Dựng Ứng Dụng Mới

1. **Xử lý URL ảnh bìa lỗi (Malformed Double-Slash)**:
   - Các API truyện thi thoảng trả về URL ảnh bị lỗi 2 dấu gạch chéo ở path: `https://static.cdnno.com/poster//150.jpg`. Request vào URL này luôn trả về HTTP 404.
   - Cần kiểm tra: nếu `url.slice(url.indexOf('://') + 3).includes('//')` thì coi như không có bìa và gán bìa mặc định `/default-cover.svg`.
2. **CORS trên Môi Trường Web / Dev**:
   - Cả 2 server `api.mottruyen.com` và `truyencv.io` đều chặn CORS khi gọi từ trình duyệt. Bắt buộc phải cấu hình Reverse Proxy trên máy chủ phát triển (Vite Proxy, Next.js Rewrite hoặc Nginx) hoặc dùng Native HTTP Client (như `CapacitorHttp` trên mobile).
3. **Đồng Bộ Dữ Liệu SQLite**:
   - Để hỗ trợ cả ứng dụng di động native lẫn web preview, nên áp dụng kiến trúc Fallback: Chạy SQLite trên mobile và IndexedDB trên trình duyệt.
