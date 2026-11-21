# ĐẶC TẢ YÊU CẦU PHẦN MỀM (Software Requirements Specification)
# Ứng Dụng Đọc Truyện Chữ Android — TruyenCV_APK

> **Phiên bản**: 1.0  
> **Ngày tạo**: 2026-11-20  
> **Tác giả**: AI Agent + Người dùng (phỏng vấn yêu cầu)  
> **Tài liệu tham chiếu**: [API_INTEGRATION_SPEC.md](file:///d:/LearnBlazor/TruyenCV_APK/API_INTEGRATION_SPEC.md) | [PROJECT_PRINCIPLES.md](file:///d:/LearnBlazor/TruyenCV_APK/PROJECT_PRINCIPLES.md)

---

## 1. Tổng Quan Dự Án

### 1.1. Mục Tiêu
Xây dựng ứng dụng đọc truyện chữ Android native, tổng hợp 2 nguồn truyện (TruyenCV.io + MotTruyen), hỗ trợ đọc online/offline, Text-to-Speech chạy nền, và sao lưu dữ liệu qua Google Drive.

### 1.2. Đối Tượng Người Dùng
Người đọc truyện chữ tiếng Việt (tiên hiệp, ngôn tình, huyền huyễn, đô thị...) sử dụng thiết bị Android.

### 1.3. Phạm Vi
- **Nền tảng**: Android only (không iOS).
- **Phân phối**: APK tự phân phối (không qua Google Play Store).
- **Ngôn ngữ giao diện**: Tiếng Việt.
- **Không bao gồm** (v1): Đăng nhập/tài khoản (trừ Google Sign-In cho backup), bình luận, push notification, widget, mini-game, hệ thống xu, auto-update.

---

## 2. Nền Tảng Công Nghệ (Tech Stack)

| Thành phần | Công nghệ | Ghi chú |
| :--- | :--- | :--- |
| **Ngôn ngữ** | Kotlin | Ngôn ngữ chính thức Android |
| **UI Framework** | Jetpack Compose | Declarative UI, Material 3 |
| **Design System** | Material 3 (Material You) | Dynamic Color, Dark/Light theme |
| **Min SDK** | API 24 (Android 7.0) | Bao phủ ~97% thiết bị |
| **Target SDK** | API 35 (Android 15) | Mới nhất |
| **Kiến trúc** | MVVM + Repository Pattern | ViewModel → Repository → DataSource |
| **DI** | Hilt | Compile-time DI, Jetpack integration |
| **HTTP Client** | Retrofit + OkHttp | Interceptor cho User-Agent, default params |
| **JSON** | Kotlinx Serialization | Nhanh, compile-time, no reflection |
| **Image Loading** | Coil | Kotlin-first, Compose-native |
| **Database** | Room (SQLite) | Compile-time safety, Flow support |
| **Preferences** | DataStore (Preferences) | Cho settings đơn giản (font, theme, speed) |
| **Async** | Kotlin Coroutines + Flow | Reactive streams cho UI |
| **TTS** | Android TextToSpeech Engine | Built-in, offline, miễn phí |
| **Background** | Foreground Service | Cho TTS chạy nền |
| **Google Drive** | Google Drive API v3 | Sao lưu/khôi phục thủ công |
| **Navigation** | Jetpack Navigation Compose | Type-safe navigation |

---

## 3. Nguồn Dữ Liệu API

Hệ thống tích hợp **2 nguồn truyện** ngay từ phiên bản đầu tiên:

### 3.1. TruyenCV.io
- Base URL: `https://truyencv.io/wp-json`
- WordPress REST API + Custom Endpoints (`/initmanga/v1/`)
- Prefix ID: `tcv-`
- Chi tiết endpoints: Xem [API_INTEGRATION_SPEC.md §2](file:///d:/LearnBlazor/TruyenCV_APK/API_INTEGRATION_SPEC.md#L22)

### 3.2. MotTruyen
- Base URL: `http://api.mottruyen.com`
- Custom REST API
- Prefix ID: `mt-`
- Default params: `deviceid=antigravity&os=android&app_version=1.0.6`
- Chi tiết endpoints: Xem [API_INTEGRATION_SPEC.md §3](file:///d:/LearnBlazor/TruyenCV_APK/API_INTEGRATION_SPEC.md#L88)

### 3.3. Unified Source
- Ánh xạ thể loại chéo qua Canonical Slug (xem [bảng mapping](file:///d:/LearnBlazor/TruyenCV_APK/API_INTEGRATION_SPEC.md#L136))
- Thuật toán xen kẽ kết quả (interleave) khi chọn "Tất cả nguồn"

---

## 4. Cấu Trúc Điều Hướng (Navigation Architecture)

### 4.1. Bottom Navigation Bar — 4 Tab

```
┌──────────────────────────────────────────────────────┐
│                    [Nội dung tab]                     │
├──────────┬──────────┬──────────────┬─────────────────┤
│ 🔍 Khám  │ 🏆 BXH   │ 📚 Tủ Truyện │ ⚙️ Cài Đặt     │
│   Phá    │          │              │                 │
└──────────┴──────────┴──────────────┴─────────────────┘
```

### 4.2. Chi Tiết Từng Tab

| Tab | Nội dung | Mô tả |
| :--- | :--- | :--- |
| **Khám Phá** | Feed cuộn dọc | Thanh tìm kiếm + Chọn nguồn + Banner Hot + Mới cập nhật + Đề cử + Thể loại nhanh |
| **BXH** | Bảng xếp hạng | Xếp hạng theo Ngày/Tuần/Tháng/Tổng, lọc theo nguồn |
| **Tủ Truyện** | Tabs nội bộ | Theo dõi / Lịch sử đọc / Offline / Đang tải |
| **Cài Đặt** | Cấu hình app | Theme, reader defaults, TTS, Google Drive backup, about |

### 4.3. Các Màn Hình Phụ (Push Screens)

| Màn hình | Truy cập từ | Nội dung |
| :--- | :--- | :--- |
| **Tìm kiếm & Lọc** | Thanh tìm kiếm Khám Phá | Tìm theo tên + Lọc: thể loại, trạng thái, số chương, sắp xếp |
| **Danh mục thể loại** | Nút "Thể loại" Khám Phá | Grid hiển thị tất cả thể loại |
| **Chi tiết truyện** | Tap vào truyện bất kỳ | Bìa, tên, tác giả, thể loại, mô tả, trạng thái, số chương, nút Đọc/Tải/Theo dõi, danh sách chương |
| **Đọc chương (Reader)** | Tap vào chương | Nội dung chương + thanh điều khiển reader |
| **Danh sách chương đầy đủ** | Chi tiết truyện | Danh sách toàn bộ chương, hỗ trợ sắp xếp ASC/DESC |

---

## 5. Đặc Tả Tính Năng Chi Tiết

### 5.1. Trang Khám Phá (Home / Explore)

**Bố cục feed cuộn dọc:**

1. **Thanh tìm kiếm** — sticky ở trên cùng, tap để mở màn hình tìm kiếm.
2. **Bộ chọn nguồn** — Chip group: TruyenCV | MotTruyen | Tất cả.
3. **Banner truyện Hot** — Carousel ngang (auto-scroll), hiển thị 5-10 truyện hot với ảnh bìa lớn.
4. **Section "Mới cập nhật"** — Danh sách dọc/grid, lazy loading khi cuộn, hiển thị: bìa, tên, tác giả, số chương, nguồn.
5. **Section "Đề cử"** — Carousel ngang hoặc grid nhỏ.
6. **Section "Thể loại"** — Grid chip/card nhanh để nhảy vào từng thể loại.

**Hành vi:**
- Pull-to-Refresh để tải lại dữ liệu.
- Shimmer/Skeleton loading khi tải.
- Empty state khi không có dữ liệu.
- API cache tạm thời để giảm request lặp.

---

### 5.2. Bảng Xếp Hạng (BXH / Ranking)

- Tabs nội bộ: **Ngày** | **Tuần** | **Tháng** | **Tổng**.
- Hiển thị danh sách xếp hạng: vị trí, bìa, tên truyện, tác giả, lượt xem, nguồn.
- Hỗ trợ chọn nguồn (TruyenCV / MotTruyen / Tất cả).
- Phân trang (pagination) khi cuộn.
- Dùng `/initmanga/v1/ranking` cho TruyenCV, `/filter?sort=...` cho MotTruyen.
- Fallback sang WordPress standard API nếu ranking endpoint lỗi.

---

### 5.3. Tìm Kiếm & Bộ Lọc (Search & Filter)

**Các tiêu chí lọc:**

| Tiêu chí | Giá trị | Áp dụng nguồn |
| :--- | :--- | :--- |
| Từ khóa (tên truyện) | Text input | Cả 2 |
| Thể loại | Danh sách thể loại (ánh xạ chéo) | Cả 2 |
| Trạng thái | Tất cả / Đang ra / Hoàn thành | Cả 2 |
| Số chương | Ranges (< 100, 100-300, 300-600, 600-1000, > 1000) | Cả 2 |
| Sắp xếp | Mới cập nhật / Mới đăng / Lượt đọc / Đánh giá | Cả 2 |
| Nguồn | TruyenCV / MotTruyen / Tất cả | — |

**Khi chọn "Tất cả nguồn":** Gọi song song cả 2 API, dùng thuật toán interleave để xen kẽ kết quả.

---

### 5.4. Chi Tiết Truyện (Novel Detail)

**Hiển thị:**
- Ảnh bìa (xử lý URL double-slash lỗi → fallback default cover).
- Tên truyện, tác giả (phân giải theo đặc tả TruyenCV: `_embedded['wp:term']` → `class_list` → fallback).
- Thể loại (chips).
- Trạng thái (Đang ra / Hoàn thành).
- Số chương.
- Nguồn (badge: TruyenCV / MotTruyen).
- Mô tả (expandable, đã strip HTML).

**Hành động:**
- **Nút "Đọc"**: Mở chương đầu tiên (hoặc chương đọc dở nếu có lịch sử).
- **Nút "Theo dõi"** (toggle): Thêm/xóa khỏi tủ truyện.
- **Nút "Tải xuống"**: Bắt đầu tải truyện offline.

**Danh sách chương:**
- Hiển thị danh sách chương phân trang.
- Nút đảo chiều sắp xếp (ASC ↔ DESC).
- Đánh dấu chương đã đọc (màu khác).
- Tap để mở reader tại chương đó.

---

### 5.5. Màn Hình Đọc (Reader Screen)

> [!IMPORTANT]
> Đây là màn hình cốt lõi nhất, cần trải nghiệm mượt mà tuyệt đối.

#### 5.5.1. Hai Chế Độ Đọc

| Chế độ | Mô tả | Chuyển chương |
| :--- | :--- | :--- |
| **Cuộn dọc (Scroll)** | Cuộn liên tục, chạm giữa màn hình để hiện/ẩn thanh điều khiển | Cuộn tới cuối → tự động tải chương tiếp; cuộn lên đầu → tự động tải chương trước |
| **Lật trang (Pager)** | Vuốt trái/phải để lật trang | Lật hết trang cuối → chương tiếp; lật ngược trang đầu → chương trước |

#### 5.5.2. Infinite Chapter Loading (Lazy Load Chương Liền Mạch)

Cơ chế tải chương liền mạch 100%:

```
Chương N-6 ← ... ← Chương N-1 ← [CHƯƠNG N ĐANG ĐỌC] → Chương N+1 → ... → Chương N+6
                                        ↑
                              Buffer: ±6 chương (tổng 12)
```

- Khi người dùng cuộn/lật tới **cuối chương hiện tại**, tự động tải và nối chương tiếp theo.
- Khi cuộn/lật **ngược về đầu**, tự động tải và nối chương trước đó.
- **Giới hạn buffer: 12 chương** (6 trước + 6 sau chương đang đọc).
- Khi vượt ngưỡng → **cắt bớt chương xa nhất** khỏi bộ nhớ để giữ hiệu năng.
- Áp dụng cho **cả 2 chế độ** (cuộn dọc và lật trang).

#### 5.5.3. Thanh Điều Khiển Reader (Overlay)

Hiện khi chạm giữa màn hình (hoặc tap nhẹ ở chế độ lật trang):

- **Thanh trên**: Tên chương, nút Quay lại, nút Danh sách chương.
- **Thanh dưới**: Nút chương trước/sau, slider chương, nút Cài đặt đọc, nút TTS, nút Auto-Scroll.

#### 5.5.4. Tùy Chỉnh Giao Diện Đọc (Reader Settings Panel)

| Tùy chỉnh | Control | Range / Options |
| :--- | :--- | :--- |
| Cỡ chữ | Slider hoặc +/- | 12sp → 32sp |
| Font chữ | Dropdown / List | Roboto, Noto Serif, Georgia, System default |
| Màu nền | Chips | Trắng / Kem / Tối / AMOLED Đen / E-ink |
| Độ sáng | Slider riêng | 0% → 100% (override system brightness) |
| Chế độ đọc | Toggle | Cuộn dọc ↔ Lật trang |

- Tất cả settings lưu vào DataStore, áp dụng ngay lập tức (không cần restart).
- Keep Screen On khi đang ở reader (trừ TTS chạy nền tắt màn hình).

---

### 5.6. Auto-Scroll (Tự Động Cuộn)

| Tính năng | Mô tả |
| :--- | :--- |
| Điều chỉnh tốc độ | Slider (chậm / vừa / nhanh) |
| Tạm dừng / tiếp tục | Chạm màn hình để toggle |
| Chuyển chương tự động | Khi cuộn hết chương → tiếp tục sang chương kế |
| Đồng bộ TTS | Khi TTS đang phát, tốc độ cuộn sync theo tốc độ đọc giọng nói |
| Keep Screen On | Luôn giữ sáng màn hình khi auto-scroll đang hoạt động |

---

### 5.7. Text-to-Speech (TTS)

#### 5.7.1. Engine
- Sử dụng `android.speech.tts.TextToSpeech` (built-in).
- Offline, miễn phí, hỗ trợ tiếng Việt.

#### 5.7.2. Tính Năng TTS Đầy Đủ

| Tính năng | Mô tả |
| :--- | :--- |
| **Play / Pause** | Nút phát/dừng trên thanh reader overlay |
| **Foreground Service** | Tiếp tục phát khi tắt màn hình, chạy dưới nền |
| **Media Notification** | Điều khiển từ thanh thông báo / lock screen (Play, Pause, Next/Prev Chapter) |
| **Highlight câu** | Tô sáng (highlight) câu đang được đọc trên màn hình reader |
| **Tốc độ đọc** | Slider: 0.5x → 2.0x (mặc định 1.0x) |
| **Chuyển chương tự động** | Đọc hết chương → tự động sang chương tiếp theo |
| **Sleep Timer** | Hẹn giờ: 15 / 30 / 45 / 60 phút / Cuối chương hiện tại |
| **Keep Screen On** | Khi TTS phát + người dùng đang xem → giữ sáng. Khi TTS phát nền (tắt màn) → không giữ sáng |

#### 5.7.3. Luồng TTS

```
Nhấn Play → Parse nội dung chương thành danh sách câu
           → Phát câu 1 → Highlight câu 1 trên UI + Auto-scroll tới câu
           → Phát câu 2 → Highlight câu 2 ...
           → Hết chương → Load chương tiếp → Tiếp tục phát
           → Sleep Timer hết giờ → Dừng + Lưu vị trí đọc
```

---

### 5.8. Tủ Truyện (Library)

**4 Tab nội bộ:**

| Tab | Nội dung | Nguồn dữ liệu |
| :--- | :--- | :--- |
| **Theo dõi** | Truyện đã bookmark/follow | Room DB (bảng `bookmarks`) |
| **Lịch sử** | Truyện đã đọc, sắp theo thời gian gần nhất | Room DB (bảng `reading_history`) |
| **Offline** | Truyện đã tải xong, đọc được khi mất mạng | Room DB (bảng `novels` + `chapters` where `isDownloaded = 1`) |
| **Đang tải** | Truyện đang trong tiến trình download | Room DB / in-memory state |

**Hành vi:**
- Hiển thị: bìa (cached offline nếu đã tải), tên, chương đọc tới, số chương, nguồn, tiến độ tải.
- Vuốt để xóa (swipe-to-delete) hoặc long-press menu.
- Lịch sử: Ghi nhớ chương đã đọc + vị trí cuộn chính xác.

---

### 5.9. Tải Truyện Offline (Download Engine)

Kỹ thuật tải dựa trên đặc tả từ dự án cũ ([API_INTEGRATION_SPEC.md §6](file:///d:/LearnBlazor/TruyenCV_APK/API_INTEGRATION_SPEC.md#L219)):

| Tham số | Giá trị | Ghi chú |
| :--- | :--- | :--- |
| Concurrency | 4 request đồng thời | Hàng đợi có giới hạn |
| Batch size | 50 chương/batch | Nghỉ cooldown giữa các batch |
| Cooldown | 300ms - 500ms | Tránh rate-limit / block IP |
| Retry | 3 lần, exponential backoff (300ms × attempt) | — |
| Pass 2 (vét) | Tự động chạy lại chương lỗi với delay 400ms | — |
| Idempotent | Skip chương đã tải thành công | Query Room DB trước khi tải |
| Cache bìa | Fetch ảnh bìa → Base64 → lưu vào Room DB | Hiển thị offline |
| Resumable | Lưu tiến trình tải vào DB, resume khi mở lại app | Chuyển sang trạng thái `paused` khi app restart |

**Giao diện tải:**
- Progress bar tổng thể (X/Y chương).
- Nút Tạm dừng / Tiếp tục / Hủy.
- Notification hiển thị tiến trình tải (Foreground Service).

---

### 5.10. Cài Đặt (Settings)

| Nhóm | Mục cài đặt |
| :--- | :--- |
| **Giao diện** | Theme toàn app: Theo hệ thống / Sáng / Tối (toggle thủ công) |
| **Reader mặc định** | Font, cỡ chữ, màu nền reader, chế độ đọc (cuộn/lật) |
| **TTS** | Tốc độ mặc định, Sleep timer mặc định |
| **Google Drive** | Đăng nhập Google, nút Sao lưu, nút Khôi phục, thời gian backup gần nhất |
| **Dữ liệu** | Xóa cache, xóa lịch sử đọc, xóa tất cả truyện offline |
| **Thông tin** | Phiên bản app, thông tin dự án |

---

### 5.11. Sao Lưu Google Drive (Backup & Restore)

#### 5.11.1. Phương thức
- **Thủ công**: Người dùng bấm nút trong Cài đặt.
- **Xác thực**: Google Sign-In (chỉ cần đăng nhập khi thực hiện backup/restore, không cần đăng nhập để dùng app).

#### 5.11.2. Dữ Liệu Được Sao Lưu

| Dữ liệu | Có backup | Ghi chú |
| :--- | :---: | :--- |
| Tủ truyện (Bookmark/Follow) | ✅ | Danh sách truyện theo dõi |
| Lịch sử đọc | ✅ | Truyện, chương, vị trí cuộn |
| Cài đặt reader | ✅ | Font, cỡ chữ, theme, tốc độ TTS |
| Metadata truyện offline | ✅ | Danh sách truyện đã tải + vị trí đọc tới (để tải lại) |
| Nội dung chương offline | ❌ | Quá nặng, không backup nội dung |

#### 5.11.3. Định Dạng Backup
- File JSON nén (gzip) lưu vào thư mục ẩn trên Google Drive (`appDataFolder`).
- Tên file: `truyencv_backup_YYYYMMDD_HHmmss.json.gz`

---

## 6. Xử Lý Lỗi & Trải Nghiệm Mạng

| Tình huống | Xử lý |
| :--- | :--- |
| Đang tải dữ liệu | Shimmer / Skeleton placeholder |
| Không có dữ liệu | Empty state với illustration + text mô tả |
| Lỗi mạng | Error state + nút "Thử lại" (Retry) |
| Mất kết nối | Banner thông báo ở trên cùng (auto-dismiss khi có mạng lại) |
| Làm mới | Pull-to-Refresh ở tất cả danh sách |
| API cache | Cache response tạm thời (OkHttp Cache / in-memory) để giảm request lặp |

---

## 7. Mô Hình Dữ Liệu (Data Models)

### 7.1. Room Database Schema

```
┌─────────────────────────────────────────────────────────┐
│ Database: TruyenCVDatabase                              │
├─────────────────────────────────────────────────────────┤
│ novels         → Thông tin truyện (cache + offline)     │
│ chapters       → Danh sách & nội dung chương            │
│ bookmarks      → Truyện theo dõi / tủ truyện            │
│ reading_history→ Lịch sử đọc (truyện, chương, vị trí)  │
│ genres         → Cache danh sách thể loại               │
│ download_tasks → Trạng thái tải offline                 │
└─────────────────────────────────────────────────────────┘
```

### 7.2. Standard Models (Kotlin)

Ánh xạ từ [API_INTEGRATION_SPEC.md §5](file:///d:/LearnBlazor/TruyenCV_APK/API_INTEGRATION_SPEC.md#L179):

```kotlin
// Novel / Story
data class Novel(
    val id: String,              // "tcv-1234" hoặc "mt-5678"
    val rawId: Long,             // ID gốc từ nguồn
    val title: String,
    val author: String,
    val thumbUrl: String,        // URL ảnh bìa (đã xử lý double-slash)
    val coverBase64: String?,    // Base64 cached cho offline
    val description: String,     // Đã strip HTML
    val genres: List<String>,
    val totalChapters: Int,
    val views: Long,
    val status: NovelStatus,     // ONGOING | COMPLETED
    val source: Source,          // TRUYENCV | MOTTRUYEN
)

// Chapter
data class Chapter(
    val id: String,              // "tcv-9999" hoặc "mt-8888"
    val rawId: Long,
    val novelId: String,
    val chapterOrder: Int,
    val title: String,           // "Chương 1: Tiêu đề"
    val rawTitle: String,
    val content: String?,        // HTML/text (null nếu chưa tải)
    val prevId: String?,
    val nextId: String?,
    val source: Source,
    val isDownloaded: Boolean,
)

enum class Source { TRUYENCV, MOTTRUYEN }
enum class NovelStatus { ONGOING, COMPLETED }
```

---

## 8. Cấu Trúc Package Dự Án (Đề Xuất)

```
com.truyencv.app/
├── di/                          # Hilt modules
├── data/
│   ├── local/
│   │   ├── db/                  # Room Database, DAOs, Entities
│   │   └── datastore/           # DataStore preferences
│   ├── remote/
│   │   ├── truyencv/            # TruyenCV API service + DTOs
│   │   ├── mottruyen/           # MotTruyen API service + DTOs
│   │   └── interceptor/        # OkHttp interceptors (User-Agent, default params)
│   ├── repository/              # Repository implementations
│   └── mapper/                  # DTO ↔ Domain model mappers
├── domain/
│   ├── model/                   # Domain models (Novel, Chapter, etc.)
│   └── repository/              # Repository interfaces
├── ui/
│   ├── theme/                   # Material 3 theme, colors, typography
│   ├── navigation/              # NavHost, routes
│   ├── explore/                 # Trang Khám Phá
│   ├── ranking/                 # Bảng Xếp Hạng
│   ├── library/                 # Tủ Truyện (tabs: theo dõi, lịch sử, offline, đang tải)
│   ├── settings/                # Cài Đặt
│   ├── search/                  # Tìm Kiếm & Lọc
│   ├── detail/                  # Chi Tiết Truyện
│   ├── reader/                  # Màn Hình Đọc (scroll + pager)
│   └── components/              # Shared UI components
├── service/
│   ├── tts/                     # TTS Foreground Service
│   └── download/                # Download Foreground Service
└── util/                        # Extensions, helpers, constants
```

---

## 9. Ràng Buộc Phi Chức Năng (Non-Functional Requirements)

| Tiêu chí | Yêu cầu |
| :--- | :--- |
| **Hiệu năng** | Reader cuộn mượt 60fps, tải chương < 2s, mở app < 3s |
| **Bộ nhớ** | Buffer chương tải trước giới hạn 12 chương, cắt bớt khi vượt |
| **Offline** | Đọc truyện đã tải hoàn toàn offline (bao gồm ảnh bìa) |
| **Pin** | TTS Foreground Service tối ưu, không wake-lock khi tắt màn hình đọc |
| **Dung lượng APK** | Tối thiểu hóa, không bundle font/asset không cần thiết |
| **Bảo mật** | Google Sign-In chỉ scope Drive.appdata, không lưu token lên log |

---

## 10. Lộ Trình Phát Triển Đề Xuất (Development Phases)

### Phase 1 — Nền Tảng (Foundation)
- Khởi tạo project Kotlin + Compose + Hilt + Room.
- Thiết lập kiến trúc MVVM + Repository.
- Cấu hình Retrofit cho 2 nguồn API (TruyenCV + MotTruyen).
- Navigation Compose + Bottom Nav 4 tab.
- Material 3 Theme + Dark Mode.

### Phase 2 — Tính Năng Đọc Cốt Lõi
- Trang Khám Phá (Home feed).
- Tìm kiếm & Lọc.
- Chi tiết truyện.
- Reader Screen (cuộn dọc + lật trang).
- Infinite Chapter Loading (buffer 12 chương).
- Reader Settings (font, cỡ chữ, theme, brightness).

### Phase 3 — Bảng Xếp Hạng & Tủ Truyện
- BXH (Ngày/Tuần/Tháng/Tổng).
- Tủ truyện (Theo dõi / Lịch sử đọc).
- Lưu vị trí đọc chính xác.

### Phase 4 — Offline & Download
- Download Engine (concurrency, batch, retry, resume).
- Tab Offline + Đang tải trong Tủ truyện.
- Cache ảnh bìa Base64.

### Phase 5 — TTS & Auto-Scroll
- TTS integration (Play/Pause, speed, highlight câu).
- Foreground Service + Media Notification.
- Sleep Timer.
- Auto-Scroll (tốc độ, sync TTS, keep screen on).

### Phase 6 — Backup & Polish
- Google Drive Backup/Restore.
- Error handling hoàn chỉnh (shimmer, empty states, network banner).
- Performance optimization.
- Bug fix & polish UI.

---

## 11. Tham Chiếu Tài Liệu

| Tài liệu | Đường dẫn |
| :--- | :--- |
| Đặc tả API | [API_INTEGRATION_SPEC.md](file:///d:/LearnBlazor/TruyenCV_APK/API_INTEGRATION_SPEC.md) |
| Nguyên tắc dự án | [PROJECT_PRINCIPLES.md](file:///d:/LearnBlazor/TruyenCV_APK/PROJECT_PRINCIPLES.md) |
| Nhật ký tiến độ | [PROGRESS_LOG.md](file:///d:/LearnBlazor/TruyenCV_APK/PROGRESS_LOG.md) |
| GitHub Repo | [Yusung-IT-NhanVo/TruyenCV_APK](https://github.com/Yusung-IT-NhanVo/TruyenCV_APK) |
