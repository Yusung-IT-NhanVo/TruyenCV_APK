# 📋 DANH SÁCH KIỂM THỬ THỦ CÔNG (Manual Test Checklist)
# Ứng Dụng Đọc Truyện — TruyenCV_APK

> **Phiên bản**: 1.0  
> **Ngày tạo**: 2026-11-21  
> **Tài liệu gốc**: [SRS_REQUIREMENTS.md](file:///d:/LearnBlazor/TruyenCV_APK/SRS_REQUIREMENTS.md) | [API_INTEGRATION_SPEC.md](file:///d:/LearnBlazor/TruyenCV_APK/API_INTEGRATION_SPEC.md)  
> **Quy ước**: ✅ Pass | ❌ Fail | ⚠️ Partial | ⏭️ Not Tested | N/A Không áp dụng

---

## Mục Lục

1. [Khởi Chạy & Điều Hướng Chung](#1-khởi-chạy--điều-hướng-chung)
2. [Trang Khám Phá (Explore / Home)](#2-trang-khám-phá-explore--home)
3. [Bảng Xếp Hạng (BXH / Ranking)](#3-bảng-xếp-hạng-bxh--ranking)
4. [Tìm Kiếm & Bộ Lọc (Search & Filter)](#4-tìm-kiếm--bộ-lọc-search--filter)
5. [Chi Tiết Truyện (Novel Detail)](#5-chi-tiết-truyện-novel-detail)
6. [Màn Hình Đọc (Reader Screen)](#6-màn-hình-đọc-reader-screen)
7. [Auto-Scroll (Tự Động Cuộn)](#7-auto-scroll-tự-động-cuộn)
8. [Text-to-Speech (TTS)](#8-text-to-speech-tts)
9. [Tủ Truyện (Library)](#9-tủ-truyện-library)
10. [Tải Truyện Offline (Download Engine)](#10-tải-truyện-offline-download-engine)
11. [Cài Đặt (Settings)](#11-cài-đặt-settings)
12. [Sao Lưu Google Drive (Backup & Restore)](#12-sao-lưu-google-drive-backup--restore)
13. [Xử Lý Lỗi & Mạng (Error & Network Handling)](#13-xử-lý-lỗi--mạng-error--network-handling)
14. [Hiệu Năng & Phi Chức Năng (Performance & NFR)](#14-hiệu-năng--phi-chức-năng-performance--nfr)
15. [Tích Hợp Dữ Liệu Đa Nguồn (Unified Source)](#15-tích-hợp-dữ-liệu-đa-nguồn-unified-source)

---

## 1. Khởi Chạy & Điều Hướng Chung

### 1.1. Cold Start (Khởi động lạnh)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 1.1.1 | App khởi chạy thành công, không crash, không ANR | | |
| 1.1.2 | Thời gian từ tap icon đến hiển thị nội dung < **3 giây** | | |
| 1.1.3 | Splash screen (nếu có) hiển thị đúng branding | | |
| 1.1.4 | Tab mặc định sau khi mở là **Khám Phá** | | |

### 1.2. Bottom Navigation Bar (4 Tab)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 1.2.1 | 4 tab hiển thị đúng: 🔍 Khám Phá · 🏆 BXH · 📚 Tủ Truyện · ⚙️ Cài Đặt | | |
| 1.2.2 | Tap từng tab → chuyển đúng nội dung, tab active được highlight | | |
| 1.2.3 | Chuyển tab không reset state của tab trước (ví dụ: vị trí cuộn) | | |
| 1.2.4 | Double-tap tab hiện tại → cuộn về đầu danh sách | | |

### 1.3. Navigation Stack (Push Screens)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 1.3.1 | Từ bất kỳ danh sách → tap truyện → mở Chi Tiết Truyện | | |
| 1.3.2 | Từ Chi Tiết → tap chương → mở Reader | | |
| 1.3.3 | Nút Back (phần cứng + phần mềm) quay về đúng màn hình trước | | |
| 1.3.4 | Deep navigation (Khám Phá → Detail → Reader → Back → Back) không lỗi | | |
| 1.3.5 | System Back Gesture (vuốt cạnh) hoạt động đúng | | |

### 1.4. Theme & Giao Diện Chung

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 1.4.1 | Light Theme hiển thị đúng (text đọc rõ, contrast đủ) | | |
| 1.4.2 | Dark Theme hiển thị đúng (không text trắng trên nền trắng) | | |
| 1.4.3 | Chế độ "Theo hệ thống" tự động đổi theo system Dark/Light | | |
| 1.4.4 | Material 3 Dynamic Color áp dụng đúng (nếu hỗ trợ) | | |
| 1.4.5 | Chữ tiếng Việt (dấu, Unicode) hiển thị đúng toàn bộ app | | |

### 1.5. Xoay Màn Hình & Responsive

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 1.5.1 | Xoay ngang → giao diện không vỡ, không crash | | |
| 1.5.2 | Xoay lại dọc → state giữ nguyên (vị trí cuộn, dữ liệu đã tải) | | |
| 1.5.3 | Multi-window / Split-screen không crash (nếu hỗ trợ) | | |

---

## 2. Trang Khám Phá (Explore / Home)

### 2.1. Bố Cục & Hiển Thị

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 2.1.1 | Thanh tìm kiếm sticky ở trên cùng, tap mở màn hình tìm kiếm | | |
| 2.1.2 | Bộ chọn nguồn hiện 3 chip: TruyenCV · MotTruyen · Tất cả | | |
| 2.1.3 | Chip mặc định ban đầu (xác nhận theo spec) | | |
| 2.1.4 | Banner Hot — hiện carousel ngang 5–10 truyện hot, ảnh bìa lớn | | |
| 2.1.5 | Banner Hot — auto-scroll hoạt động, không giật | | |
| 2.1.6 | Section "Mới cập nhật" hiển thị danh sách truyện: bìa, tên, tác giả, số chương, nguồn | | |
| 2.1.7 | Section "Đề cử" hiển thị đúng (carousel ngang hoặc grid) | | |
| 2.1.8 | Section "Thể loại" hiển thị grid chip/card nhanh | | |
| 2.1.9 | Tap chip thể loại → chuyển đến danh sách truyện theo thể loại đó | | |

### 2.2. Tải Dữ Liệu & Loading State

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 2.2.1 | Lần đầu mở: Shimmer/Skeleton hiện đúng vị trí khi đang tải | | |
| 2.2.2 | Dữ liệu tải xong → shimmer biến mất, nội dung hiện | | |
| 2.2.3 | Pull-to-Refresh kéo xuống → tải lại toàn bộ dữ liệu | | |
| 2.2.4 | Lazy loading khi cuộn đến cuối Section "Mới cập nhật" | | |

### 2.3. Chuyển Nguồn Dữ Liệu

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 2.3.1 | Chọn chip "TruyenCV" → chỉ hiển thị truyện từ TruyenCV | | |
| 2.3.2 | Chọn chip "MotTruyen" → chỉ hiển thị truyện từ MotTruyen | | |
| 2.3.3 | Chọn chip "Tất cả" → hiển thị truyện xen kẽ từ cả 2 nguồn (interleave) | | |
| 2.3.4 | Chuyển nguồn → shimmer loading → dữ liệu mới thay thế | | |
| 2.3.5 | Badge nguồn trên mỗi item truyện đúng (TruyenCV / MotTruyen) | | |

---

## 3. Bảng Xếp Hạng (BXH / Ranking)

### 3.1. Tabs & Hiển Thị

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 3.1.1 | 4 tab nội bộ hiển thị: Ngày · Tuần · Tháng · Tổng | | |
| 3.1.2 | Tab mặc định khi mở BXH (xác nhận) | | |
| 3.1.3 | Mỗi item hiện: vị trí, bìa, tên, tác giả, lượt xem, nguồn | | |
| 3.1.4 | Vị trí xếp hạng (1, 2, 3…) hiển thị rõ ràng, có style đặc biệt cho top 3 | | |

### 3.2. Lọc & Phân Trang

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 3.2.1 | Chọn nguồn TruyenCV → gọi `/initmanga/v1/ranking`, hiển thị đúng | | |
| 3.2.2 | Chọn nguồn MotTruyen → gọi `/filter?sort=...`, hiển thị đúng | | |
| 3.2.3 | Chọn nguồn "Tất cả" → gộp kết quả xen kẽ từ cả 2 | | |
| 3.2.4 | Cuộn xuống cuối → phân trang tự động tải thêm (pagination) | | |
| 3.2.5 | Chuyển tab (Ngày → Tuần) → tải lại dữ liệu tương ứng | | |

### 3.3. Fallback

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 3.3.1 | Nếu `/initmanga/v1/ranking` timeout/lỗi → fallback sang WordPress standard API (`orderby=views`) | | |
| 3.3.2 | Fallback hoạt động trong suốt, người dùng không thấy lỗi | | |

---

## 4. Tìm Kiếm & Bộ Lọc (Search & Filter)

### 4.1. Tìm Kiếm Theo Từ Khóa

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 4.1.1 | Tap thanh tìm kiếm → mở màn hình tìm kiếm, bàn phím hiện | | |
| 4.1.2 | Gõ từ khóa → kết quả hiện sau khi debounce hoặc nhấn tìm | | |
| 4.1.3 | Từ khóa tiếng Việt có dấu → kết quả chính xác | | |
| 4.1.4 | Từ khóa rỗng → không gọi API / hiện gợi ý | | |
| 4.1.5 | Từ khóa không khớp → empty state hiển thị | | |

### 4.2. Bộ Lọc Nâng Cao

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 4.2.1 | Lọc theo Thể loại → danh sách thể loại hiển thị, chọn được | | |
| 4.2.2 | Lọc theo Trạng thái: Tất cả / Đang ra / Hoàn thành | | |
| 4.2.3 | Lọc theo Số chương: < 100 · 100–300 · 300–600 · 600–1000 · > 1000 | | |
| 4.2.4 | Sắp xếp: Mới cập nhật / Mới đăng / Lượt đọc / Đánh giá | | |
| 4.2.5 | Lọc theo Nguồn: TruyenCV / MotTruyen / Tất cả | | |
| 4.2.6 | Kết hợp nhiều bộ lọc đồng thời → kết quả đúng | | |
| 4.2.7 | Reset/Xóa bộ lọc → quay về trạng thái mặc định | | |

### 4.3. Nguồn "Tất Cả" Khi Tìm Kiếm

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 4.3.1 | Chọn "Tất cả" → gọi song song 2 API | | |
| 4.3.2 | Kết quả xen kẽ (interleave) giữa TruyenCV và MotTruyen | | |
| 4.3.3 | Nếu 1 nguồn trả chậm → hiển thị kết quả nguồn kia trước (nếu UX cho phép) | | |

---

## 5. Chi Tiết Truyện (Novel Detail)

### 5.1. Hiển Thị Thông Tin

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 5.1.1 | Ảnh bìa hiển thị đúng (tải từ URL) | | |
| 5.1.2 | URL bìa bị lỗi double-slash (`//` trong path) → fallback hiện bìa mặc định | | |
| 5.1.3 | Tên truyện hiển thị đầy đủ (không bị cắt ngắn sai) | | |
| 5.1.4 | Tác giả hiển thị đúng (phân giải từ `_embedded['wp:term']` cho TruyenCV) | | |
| 5.1.5 | Tác giả fallback → `class_list` → `author_name` → "Chưa rõ" | | |
| 5.1.6 | Thể loại hiện dạng chips, đúng thể loại | | |
| 5.1.7 | Trạng thái: "Đang ra" hoặc "Hoàn thành" hiển thị đúng | | |
| 5.1.8 | Số chương hiển thị chính xác | | |
| 5.1.9 | Nguồn (badge TruyenCV / MotTruyen) hiển thị | | |
| 5.1.10 | Mô tả: hiển thị đã strip HTML, expandable (rút gọn / xem thêm) | | |

### 5.2. Hành Động

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 5.2.1 | Nút "Đọc" (chưa có lịch sử) → mở chương đầu tiên | | |
| 5.2.2 | Nút "Đọc" (đã đọc dở) → mở chương đọc dở, đúng vị trí | | |
| 5.2.3 | Nút "Theo dõi" lần 1 → thêm vào tủ truyện, icon thay đổi | | |
| 5.2.4 | Nút "Theo dõi" lần 2 → xóa khỏi tủ truyện, icon quay lại | | |
| 5.2.5 | Nút "Tải xuống" → bắt đầu tải truyện offline | | |

### 5.3. Danh Sách Chương

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 5.3.1 | Danh sách chương hiển thị phân trang | | |
| 5.3.2 | Cuộn → tải thêm chương (lazy load / pagination) | | |
| 5.3.3 | Nút đảo chiều sắp xếp: ASC ↔ DESC hoạt động | | |
| 5.3.4 | Chương đã đọc → hiển thị màu khác so với chương chưa đọc | | |
| 5.3.5 | Tap vào chương → mở Reader tại chương đó | | |
| 5.3.6 | Truyện nhiều chương (> 500) → phân trang mượt, không OOM | | |

---

## 6. Màn Hình Đọc (Reader Screen)

> [!IMPORTANT]
> Đây là màn hình cốt lõi nhất, cần kiểm thử kỹ nhất.

### 6.1. Chế Độ Cuộn Dọc (Scroll Mode)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 6.1.1 | Nội dung chương hiển thị đầy đủ, text rõ ràng | | |
| 6.1.2 | Cuộn mượt 60fps, không giật/đơ | | |
| 6.1.3 | Chạm giữa màn hình → hiện/ẩn thanh điều khiển (overlay) | | |
| 6.1.4 | Cuộn tới cuối chương → tự động tải chương tiếp theo (infinite load) | | |
| 6.1.5 | Cuộn ngược lên đầu → tự động tải chương trước | | |
| 6.1.6 | Nội dung chương tiếp nối liền mạch (không bị giật vị trí cuộn) | | |

### 6.2. Chế Độ Lật Trang (Pager Mode)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 6.2.1 | Vuốt trái/phải để lật trang hoạt động | | |
| 6.2.2 | Lật trang animation mượt mà | | |
| 6.2.3 | Lật hết trang cuối → chuyển sang chương tiếp | | |
| 6.2.4 | Lật ngược trang đầu → chuyển sang chương trước | | |
| 6.2.5 | Tap để hiện/ẩn overlay hoạt động đúng trong chế độ pager | | |

### 6.3. Infinite Chapter Loading (Buffer ±6 chương)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 6.3.1 | Đọc liên tục qua 6+ chương → tải trước mượt, không phải chờ | | |
| 6.3.2 | Buffer giới hạn 12 chương (6 trước + 6 sau) | | |
| 6.3.3 | Vượt buffer → chương xa nhất bị cắt khỏi bộ nhớ | | |
| 6.3.4 | Quay lại chương đã cắt → tải lại từ API/DB, không crash | | |
| 6.3.5 | Áp dụng cho cả chế độ cuộn dọc lẫn lật trang | | |
| 6.3.6 | Đọc chương đầu tiên (chương 1) → không lỗi khi tải chương trước | | |
| 6.3.7 | Đọc chương cuối cùng → không lỗi khi tải chương tiếp | | |

### 6.4. Thanh Điều Khiển (Reader Overlay)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 6.4.1 | **Thanh trên**: Tên chương hiển thị, nút Quay lại, nút Danh sách chương | | |
| 6.4.2 | Nút "Quay lại" → về Chi Tiết Truyện | | |
| 6.4.3 | Nút "Danh sách chương" → mở danh sách chương | | |
| 6.4.4 | **Thanh dưới**: Nút chương trước/sau hoạt động | | |
| 6.4.5 | Slider chương — kéo để nhảy đến chương bất kỳ | | |
| 6.4.6 | Nút Cài đặt đọc → mở panel settings | | |
| 6.4.7 | Nút TTS → kích hoạt TTS | | |
| 6.4.8 | Nút Auto-Scroll → kích hoạt auto-scroll | | |

### 6.5. Tùy Chỉnh Giao Diện Đọc (Reader Settings)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 6.5.1 | Cỡ chữ: Slider/+/- từ 12sp → 32sp, **áp dụng ngay** | | |
| 6.5.2 | Font chữ: chuyển giữa Roboto / Noto Serif / Georgia / System default | | |
| 6.5.3 | Màu nền: Trắng / Kem / Tối / AMOLED Đen / E-ink, **áp dụng ngay** | | |
| 6.5.4 | Độ sáng: Slider 0%–100%, override system brightness | | |
| 6.5.5 | Chế độ đọc: Toggle cuộn dọc ↔ lật trang, **chuyển ngay** | | |
| 6.5.6 | Tất cả settings lưu DataStore, mở lại app vẫn giữ | | |
| 6.5.7 | Keep Screen On: màn hình không tắt khi đang ở reader | | |

### 6.6. Làm Sạch Nội Dung

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 6.6.1 | Nội dung TruyenCV: escape slashes đã xử lý (`\"` → `"`, `\'` → `'`) | | |
| 6.6.2 | Nội dung TruyenCV: dòng lặp tiêu đề chương đầu bài đã bị xóa | | |
| 6.6.3 | HTML tags đã strip, chỉ còn plain text dễ đọc | | |
| 6.6.4 | Ký tự đặc biệt tiếng Việt hiển thị đúng | | |

### 6.7. Lưu Vị Trí Đọc

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 6.7.1 | Rời khỏi Reader → vị trí cuộn/trang được lưu | | |
| 6.7.2 | Quay lại truyện → resume đúng chương + vị trí cuộn | | |
| 6.7.3 | Kill app → mở lại → vẫn resume đúng vị trí | | |

---

## 7. Auto-Scroll (Tự Động Cuộn)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 7.1 | Bật Auto-Scroll → trang tự động cuộn xuống | | |
| 7.2 | Slider tốc độ: điều chỉnh chậm / vừa / nhanh, thay đổi ngay | | |
| 7.3 | Chạm màn hình → tạm dừng; chạm lại → tiếp tục cuộn | | |
| 7.4 | Cuộn hết chương → tự động chuyển sang chương kế | | |
| 7.5 | Keep Screen On khi Auto-Scroll hoạt động (không tắt màn) | | |
| 7.6 | Khi TTS đang phát → tốc độ cuộn sync theo tốc độ đọc giọng nói | | |
| 7.7 | Tắt Auto-Scroll → cuộn dừng, có thể cuộn thủ công bình thường | | |

---

## 8. Text-to-Speech (TTS)

### 8.1. Điều Khiển Cơ Bản

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 8.1.1 | Nhấn Play → TTS bắt đầu đọc từ vị trí hiện tại | | |
| 8.1.2 | Nhấn Pause → dừng đọc, nhấn lại → tiếp tục | | |
| 8.1.3 | Tốc độ đọc: Slider 0.5x → 2.0x (mặc định 1.0x), thay đổi ngay | | |
| 8.1.4 | Giọng đọc tiếng Việt rõ ràng (nếu engine hỗ trợ) | | |

### 8.2. Highlight & Auto-Scroll Đồng Bộ

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 8.2.1 | Câu đang đọc được highlight (tô sáng) trên màn hình | | |
| 8.2.2 | Trang tự scroll theo câu đang đọc | | |
| 8.2.3 | Chuyển câu → highlight cập nhật đúng câu kế | | |

### 8.3. Foreground Service & Media Notification

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 8.3.1 | Tắt màn hình → TTS tiếp tục phát | | |
| 8.3.2 | Chuyển sang app khác → TTS tiếp tục phát nền | | |
| 8.3.3 | Media Notification hiện trên thanh thông báo: Play/Pause, Next/Prev Chapter | | |
| 8.3.4 | Điều khiển TTS từ lock screen | | |
| 8.3.5 | Nhấn nút Next Chapter trên notification → chuyển chương, tiếp tục đọc | | |
| 8.3.6 | Nhấn nút Prev Chapter trên notification → quay chương trước, tiếp tục đọc | | |

### 8.4. Chuyển Chương & Sleep Timer

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 8.4.1 | Đọc hết chương → tự động tải + đọc chương tiếp theo | | |
| 8.4.2 | Sleep Timer: đặt 15 / 30 / 45 / 60 phút / Cuối chương | | |
| 8.4.3 | Sleep Timer hết giờ → TTS dừng + lưu vị trí đọc | | |
| 8.4.4 | Sleep Timer "Cuối chương" → đọc hết chương hiện tại mới dừng | | |

### 8.5. Keep Screen On & Pin

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 8.5.1 | TTS đang phát + user xem reader → giữ sáng màn hình | | |
| 8.5.2 | TTS phát nền (tắt màn) → không giữ sáng (tiết kiệm pin) | | |
| 8.5.3 | Dừng TTS → trở lại cơ chế timeout màn hình bình thường | | |

---

## 9. Tủ Truyện (Library)

### 9.1. Tab "Theo Dõi"

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 9.1.1 | Hiển thị danh sách truyện đã follow/bookmark | | |
| 9.1.2 | Mỗi item: bìa, tên, chương đọc tới, số chương, nguồn | | |
| 9.1.3 | Tap → mở Chi Tiết Truyện | | |
| 9.1.4 | Vuốt để xóa (swipe-to-delete) hoạt động | | |
| 9.1.5 | Long-press → hiện menu (xóa, v.v.) | | |
| 9.1.6 | Truyện từ cả 2 nguồn (TruyenCV + MotTruyen) hiển thị cùng danh sách | | |

### 9.2. Tab "Lịch Sử"

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 9.2.1 | Hiển thị truyện đã đọc, sắp theo thời gian gần nhất | | |
| 9.2.2 | Ghi nhớ chương đã đọc + vị trí cuộn chính xác | | |
| 9.2.3 | Tap → mở Reader tại đúng vị trí đọc dở | | |
| 9.2.4 | Đọc truyện mới → lịch sử cập nhật ngay | | |
| 9.2.5 | Vuốt/xóa lịch sử hoạt động | | |

### 9.3. Tab "Offline"

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 9.3.1 | Hiển thị truyện đã tải xong hoàn toàn | | |
| 9.3.2 | Ảnh bìa hiện offline (cached Base64) | | |
| 9.3.3 | Tắt mạng → vẫn đọc được truyện offline | | |
| 9.3.4 | Tap → mở Reader, nội dung load từ DB local | | |
| 9.3.5 | Vuốt xóa → xóa truyện offline (nội dung + metadata) | | |

### 9.4. Tab "Đang Tải"

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 9.4.1 | Hiển thị truyện đang trong tiến trình download | | |
| 9.4.2 | Progress bar cập nhật realtime (X/Y chương) | | |
| 9.4.3 | Nút Tạm dừng → dừng tải, trạng thái chuyển sang paused | | |
| 9.4.4 | Nút Tiếp tục → resume tải từ vị trí dừng | | |
| 9.4.5 | Nút Hủy → dừng tải, hỏi xác nhận (nếu cần) | | |

---

## 10. Tải Truyện Offline (Download Engine)

### 10.1. Luồng Tải Chính

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 10.1.1 | Nhấn "Tải xuống" từ Chi Tiết → bắt đầu tải, notification hiện | | |
| 10.1.2 | Concurrency: tối đa 4 request đồng thời | | |
| 10.1.3 | Batch size: 50 chương/batch | | |
| 10.1.4 | Cooldown 300–500ms giữa các batch (không gọi liên tục) | | |

### 10.2. Idempotent & Resume

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 10.2.1 | Chương đã tải thành công → không tải lại (skip) | | |
| 10.2.2 | Kill app giữa chừng → mở lại → trạng thái chuyển thành `paused` | | |
| 10.2.3 | Nhấn Tiếp tục → resume đúng chương đang dở, không tải lại từ đầu | | |

### 10.3. Retry & Pass 2

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 10.3.1 | Chương lỗi → retry tối đa 3 lần, exponential backoff (300ms × attempt) | | |
| 10.3.2 | Sau đợt tải chính, chương vẫn lỗi → chạy Pass 2 (vét) với delay 400ms | | |
| 10.3.3 | Pass 2 thành công → truyện chuyển sang trạng thái hoàn tất | | |

### 10.4. Cache Bìa Offline

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 10.4.1 | Khi tải truyện → fetch ảnh bìa → convert Base64 → lưu vào Room DB | | |
| 10.4.2 | Tắt mạng → ảnh bìa vẫn hiển thị từ Base64 cached | | |

### 10.5. Notification Foreground Service

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 10.5.1 | Notification hiện progress tải (X/Y chương) | | |
| 10.5.2 | Tải nền khi tắt màn hình / chuyển app → tiếp tục tải | | |
| 10.5.3 | Tải hoàn tất → notification cập nhật "Hoàn tất" | | |

---

## 11. Cài Đặt (Settings)

### 11.1. Nhóm Giao Diện

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 11.1.1 | Theme: Toggle Theo hệ thống / Sáng / Tối → áp dụng ngay | | |
| 11.1.2 | Đổi theme → quay về app → giao diện đúng theme đã chọn | | |

### 11.2. Nhóm Reader Mặc Định

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 11.2.1 | Đổi Font mặc định → mở reader mới → font đúng | | |
| 11.2.2 | Đổi cỡ chữ mặc định → reader áp dụng đúng | | |
| 11.2.3 | Đổi màu nền reader mặc định → reader áp dụng đúng | | |
| 11.2.4 | Đổi chế độ đọc mặc định (cuộn/lật) → reader áp dụng đúng | | |

### 11.3. Nhóm TTS

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 11.3.1 | Đổi tốc độ TTS mặc định → TTS lần sau dùng tốc độ này | | |
| 11.3.2 | Đổi Sleep Timer mặc định → TTS lần sau dùng timer này | | |

### 11.4. Nhóm Dữ Liệu

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 11.4.1 | Xóa cache → cache API/ảnh bị xóa, app tải lại dữ liệu | | |
| 11.4.2 | Xóa lịch sử đọc → tab Lịch Sử trống, hỏi xác nhận trước | | |
| 11.4.3 | Xóa tất cả truyện offline → tab Offline trống, dung lượng giảm | | |

### 11.5. Nhóm Thông Tin

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 11.5.1 | Phiên bản app hiển thị đúng | | |
| 11.5.2 | Thông tin dự án hiển thị đúng | | |

---

## 12. Sao Lưu Google Drive (Backup & Restore)

### 12.1. Đăng Nhập Google

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 12.1.1 | Chưa đăng nhập → nhấn Sao lưu → yêu cầu đăng nhập Google | | |
| 12.1.2 | Google Sign-In flow hoàn tất thành công | | |
| 12.1.3 | Chỉ yêu cầu scope `Drive.appdata` (không xin quyền thừa) | | |
| 12.1.4 | Không lưu token lên log / console | | |

### 12.2. Sao Lưu (Backup)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 12.2.1 | Nhấn "Sao lưu" → tạo file `truyencv_backup_YYYYMMDD_HHmmss.json.gz` | | |
| 12.2.2 | File lưu vào `appDataFolder` trên Google Drive (thư mục ẩn) | | |
| 12.2.3 | Dữ liệu backup gồm: Bookmark, Lịch sử đọc, Cài đặt reader, Metadata offline | | |
| 12.2.4 | Dữ liệu KHÔNG backup: Nội dung chương offline (quá nặng) | | |
| 12.2.5 | Hiển thị thời gian backup gần nhất sau khi sao lưu xong | | |
| 12.2.6 | Loading/progress indicator trong quá trình backup | | |

### 12.3. Khôi Phục (Restore)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 12.3.1 | Nhấn "Khôi phục" → liệt kê bản backup có sẵn trên Drive | | |
| 12.3.2 | Chọn bản backup → tải về, giải nén, khôi phục dữ liệu | | |
| 12.3.3 | Sau khôi phục: Bookmark đúng, Lịch sử đọc đúng, Settings đúng | | |
| 12.3.4 | Hỏi xác nhận trước khi ghi đè dữ liệu hiện tại | | |
| 12.3.5 | Metadata offline → hiển thị trong tab Offline (sẵn sàng tải lại) | | |

---

## 13. Xử Lý Lỗi & Mạng (Error & Network Handling)

### 13.1. Trạng Thái Tải (Loading States)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 13.1.1 | Shimmer/Skeleton placeholder hiện đúng vị trí khi tải dữ liệu | | |
| 13.1.2 | Shimmer biến mất khi dữ liệu tải xong | | |
| 13.1.3 | Shimmer áp dụng cho: Khám Phá, BXH, Tìm kiếm, Chi tiết, Reader | | |

### 13.2. Empty State

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 13.2.1 | Không có dữ liệu → empty state với illustration + text mô tả | | |
| 13.2.2 | Tủ Truyện trống → "Chưa có truyện nào" hoặc tương tự | | |
| 13.2.3 | Tìm kiếm không khớp → "Không tìm thấy kết quả" | | |

### 13.3. Lỗi Mạng (Error State)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 13.3.1 | Tắt mạng → Error state + nút "Thử lại" (Retry) | | |
| 13.3.2 | Nhấn "Thử lại" → gọi lại API | | |
| 13.3.3 | Mất kết nối đang dùng → banner thông báo ở trên cùng | | |
| 13.3.4 | Có mạng lại → banner tự ẩn (auto-dismiss) | | |

### 13.4. Pull-to-Refresh

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 13.4.1 | Pull-to-Refresh hoạt động ở tất cả danh sách | | |
| 13.4.2 | Kéo → spinner hiện → dữ liệu tải lại → spinner ẩn | | |

### 13.5. API Cache

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 13.5.1 | Gọi lại trang vừa xem → dữ liệu từ cache (nhanh hơn), không gọi API lại | | |
| 13.5.2 | Pull-to-Refresh → bypass cache, lấy dữ liệu mới | | |

---

## 14. Hiệu Năng & Phi Chức Năng (Performance & NFR)

### 14.1. Hiệu Năng

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 14.1.1 | App cold start < **3 giây** | | |
| 14.1.2 | Tải chương < **2 giây** | | |
| 14.1.3 | Reader cuộn mượt **60fps** (không jank) | | |
| 14.1.4 | Chuyển tab nhanh, không delay đáng kể | | |
| 14.1.5 | Tìm kiếm → kết quả hiện trong < 3s | | |

### 14.2. Bộ Nhớ & Pin

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 14.2.1 | Đọc 20+ chương liên tục → không OOM crash | | |
| 14.2.2 | Buffer chương giữ ≤ 12, cắt bớt chương xa | | |
| 14.2.3 | TTS Foreground Service không wake-lock khi tắt màn hình đọc | | |
| 14.2.4 | Dùng app 1 giờ → không tiêu hao pin bất thường | | |

### 14.3. Offline Toàn Phần

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 14.3.1 | Bật chế độ máy bay → truyện đã tải đọc bình thường | | |
| 14.3.2 | Ảnh bìa offline hiển thị (Base64 cached) | | |
| 14.3.3 | Tủ truyện (Theo dõi, Lịch sử) hiện từ local DB | | |
| 14.3.4 | Tab Khám Phá/BXH → error state + retry (vì cần mạng) | | |

### 14.4. Khả Năng Tương Thích

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 14.4.1 | Chạy trên Android 7.0 (API 24) — min SDK | | |
| 14.4.2 | Chạy trên Android 15 (API 35) — target SDK | | |
| 14.4.3 | Chạy trên thiết bị phổ thông (RAM 3–4GB) không lag | | |
| 14.4.4 | Chạy trên tablet (màn lớn) → giao diện không vỡ | | |

---

## 15. Tích Hợp Dữ Liệu Đa Nguồn (Unified Source)

### 15.1. ID Prefixing

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 15.1.1 | Truyện từ TruyenCV có ID prefix `tcv-` (vd: `tcv-1234`) | | |
| 15.1.2 | Truyện từ MotTruyen có ID prefix `mt-` (vd: `mt-5678`) | | |
| 15.1.3 | Bookmark truyện từ cả 2 nguồn → lưu đúng prefix, không trùng | | |

### 15.2. Ánh Xạ Thể Loại Chéo (Genre Cross-Mapping)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 15.2.1 | Lọc "Tiên Hiệp" (Tất cả nguồn) → TruyenCV genre=16 + MotTruyen cat=14 | | |
| 15.2.2 | Lọc "Ngôn Tình" → đúng mapping ID cho cả 2 nguồn | | |
| 15.2.3 | Thể loại chỉ có 1 nguồn → chỉ hiện kết quả nguồn đó | | |

### 15.3. Interleave Kết Quả

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 15.3.1 | Chế độ "Tất cả": kết quả xen kẽ TCV-1, MT-1, TCV-2, MT-2... | | |
| 15.3.2 | Nguồn A nhiều hơn nguồn B → phần dư hiện ở cuối | | |
| 15.3.3 | 1 nguồn lỗi → chỉ hiện kết quả nguồn còn lại (graceful) | | |

### 15.4. Chuyển Nguồn Cross-Source

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 15.4.1 | Mở chi tiết truyện TruyenCV → dữ liệu, endpoint, response đúng | | |
| 15.4.2 | Mở chi tiết truyện MotTruyen → dữ liệu, endpoint, response đúng | | |
| 15.4.3 | Reader load chương TruyenCV (endpoint `/initmanga/v1/chapter/{id}`) | | |
| 15.4.4 | Reader load chương MotTruyen (endpoint `/chapter/?chapter_id={id}`) | | |

---

## Phụ Lục: Ma Trận Kiểm Thử Nhanh Theo Màn Hình

| Màn hình | Chức năng | Trải nghiệm (UX) | Hiệu năng | Offline | Lỗi/Mạng |
|---|:---:|:---:|:---:|:---:|:---:|
| **Khám Phá** | §2.1–2.3 | §2.2 | §14.1 | §14.3.4 | §13 |
| **BXH** | §3.1–3.2 | §3.1 | §14.1 | §14.3.4 | §3.3, §13 |
| **Tìm kiếm** | §4.1–4.3 | §4.1 | §14.1.5 | N/A | §13 |
| **Chi tiết truyện** | §5.1–5.3 | §5.1 | §14.1.2 | §14.3 | §13 |
| **Reader** | §6.1–6.7 | §6.5 | §14.1.3, §14.2 | §9.3.3 | §13 |
| **Auto-Scroll** | §7 | §7 | §14.2 | N/A | N/A |
| **TTS** | §8.1–8.5 | §8.2 | §14.2.3 | N/A | N/A |
| **Tủ Truyện** | §9.1–9.4 | §9 | §14.1 | §14.3.3 | §13 |
| **Download** | §10.1–10.5 | §10.1 | §10.1.2–4 | §10.4 | §10.3 |
| **Cài đặt** | §11.1–11.5 | §11 | N/A | N/A | N/A |
| **Google Drive** | §12.1–12.3 | §12 | N/A | N/A | §13.3 |

---

> **Tổng cộng: ~170+ test cases** bao phủ toàn bộ chức năng, trải nghiệm người dùng, hiệu năng, offline, và xử lý lỗi theo đặc tả SRS v1.0.
