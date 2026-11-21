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
| 1.1.1 | App khởi chạy thành công, không crash, không ANR | ✅ Pass | |
| 1.1.2 | Thời gian từ tap icon đến hiển thị nội dung < **3 giây** | ✅ Pass | |
| 1.1.3 | Splash screen (nếu có) hiển thị đúng branding | ✅ Pass | |
| 1.1.4 | Tab mặc định sau khi mở là **Khám Phá** | ✅ Pass | |

### 1.2. Bottom Navigation Bar (4 Tab)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 1.2.1 | 4 tab hiển thị đúng: 🔍 Khám Phá · 🏆 BXH · 📚 Tủ Truyện · ⚙️ Cài Đặt | ✅ Pass | |
| 1.2.2 | Tap từng tab → chuyển đúng nội dung, tab active được highlight | ✅ Pass | |
| 1.2.3 | Chuyển tab không reset state của tab trước (ví dụ: vị trí cuộn) | ✅ Pass | |
| 1.2.4 | Double-tap tab hiện tại → cuộn về đầu danh sách | ✅ Pass | |

### 1.3. Navigation Stack (Push Screens)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 1.3.1 | Từ bất kỳ danh sách → tap truyện → mở Chi Tiết Truyện | ✅ Pass | |
| 1.3.2 | Từ Chi Tiết → tap chương → mở Reader | ✅ Pass | |
| 1.3.3 | Nút Back (phần cứng + phần mềm) quay về đúng màn hình trước | ✅ Pass | |
| 1.3.4 | Deep navigation (Khám Phá → Detail → Reader → Back → Back) không lỗi | ✅ Pass | |
| 1.3.5 | System Back Gesture (vuốt cạnh) hoạt động đúng | ✅ Pass | |

### 1.4. Theme & Giao Diện Chung

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 1.4.1 | Light Theme hiển thị đúng (text đọc rõ, contrast đủ) | ✅ Pass | |
| 1.4.2 | Dark Theme hiển thị đúng (không text trắng trên nền trắng) | ✅ Pass | |
| 1.4.3 | Chế độ "Theo hệ thống" tự động đổi theo system Dark/Light | ✅ Pass | |
| 1.4.4 | Material 3 Dynamic Color áp dụng đúng (nếu hỗ trợ) | ✅ Pass | |
| 1.4.5 | Chữ tiếng Việt (dấu, Unicode) hiển thị đúng toàn bộ app | ✅ Pass | |

### 1.5. Xoay Màn Hình & Responsive

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 1.5.1 | Xoay ngang → giao diện không vỡ, không crash | ✅ Pass | |
| 1.5.2 | Xoay lại dọc → state giữ nguyên (vị trí cuộn, dữ liệu đã tải) | ✅ Pass | |
| 1.5.3 | Multi-window / Split-screen không crash (nếu hỗ trợ) | ✅ Pass | |

---

## 2. Trang Khám Phá (Explore / Home)

### 2.1. Bố Cục & Hiển Thị

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 2.1.1 | Thanh tìm kiếm sticky ở trên cùng, tap mở màn hình tìm kiếm | ✅ Pass | |
| 2.1.2 | Bộ chọn nguồn hiện 3 chip: TruyenCV · MotTruyen · Tất cả | ✅ Pass | |
| 2.1.3 | Chip mặc định ban đầu (xác nhận theo spec) | ✅ Pass | |
| 2.1.4 | Banner Hot — hiện carousel ngang 5–10 truyện hot, ảnh bìa lớn | ✅ Pass | |
| 2.1.5 | Banner Hot — auto-scroll hoạt động, không giật | ✅ Pass | |
| 2.1.6 | Section "Mới cập nhật" hiển thị danh sách truyện: bìa, tên, tác giả, số chương, nguồn | ✅ Pass | |
| 2.1.7 | Section "Đề cử" hiển thị đúng (carousel ngang hoặc grid) | ✅ Pass | |
| 2.1.8 | Section "Thể loại" hiển thị grid chip/card nhanh | ✅ Pass | |
| 2.1.9 | Tap chip thể loại → chuyển đến danh sách truyện theo thể loại đó | ✅ Pass | |

### 2.2. Tải Dữ Liệu & Loading State

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 2.2.1 | Lần đầu mở: Shimmer/Skeleton hiện đúng vị trí khi đang tải | ✅ Pass | |
| 2.2.2 | Dữ liệu tải xong → shimmer biến mất, nội dung hiện | ✅ Pass | |
| 2.2.3 | Pull-to-Refresh kéo xuống → tải lại toàn bộ dữ liệu | ✅ Pass | |
| 2.2.4 | Lazy loading khi cuộn đến cuối Section "Mới cập nhật" | ✅ Pass | |

### 2.3. Chuyển Nguồn Dữ Liệu

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 2.3.1 | Chọn chip "TruyenCV" → chỉ hiển thị truyện từ TruyenCV | ✅ Pass | |
| 2.3.2 | Chọn chip "MotTruyen" → chỉ hiển thị truyện từ MotTruyen | ✅ Pass | |
| 2.3.3 | Chọn chip "Tất cả" → hiển thị truyện xen kẽ từ cả 2 nguồn (interleave) | ✅ Pass | |
| 2.3.4 | Chuyển nguồn → shimmer loading → dữ liệu mới thay thế | ✅ Pass | |
| 2.3.5 | Badge nguồn trên mỗi item truyện đúng (TruyenCV / MotTruyen) | ✅ Pass | |

---

## 3. Bảng Xếp Hạng (BXH / Ranking)

### 3.1. Tabs & Hiển Thị

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 3.1.1 | 4 tab nội bộ hiển thị: Ngày · Tuần · Tháng · Tổng | ✅ Pass | |
| 3.1.2 | Tab mặc định khi mở BXH (xác nhận) | ✅ Pass | |
| 3.1.3 | Mỗi item hiện: vị trí, bìa, tên, tác giả, lượt xem, nguồn | ✅ Pass | |
| 3.1.4 | Vị trí xếp hạng (1, 2, 3…) hiển thị rõ ràng, có style đặc biệt cho top 3 | ✅ Pass | |

### 3.2. Lọc & Phân Trang

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 3.2.1 | Chọn nguồn TruyenCV → gọi `/initmanga/v1/ranking`, hiển thị đúng | ✅ Pass | |
| 3.2.2 | Chọn nguồn MotTruyen → gọi `/filter?sort=...`, hiển thị đúng | ✅ Pass | |
| 3.2.3 | Chọn nguồn "Tất cả" → gộp kết quả xen kẽ từ cả 2 | ✅ Pass | |
| 3.2.4 | Cuộn xuống cuối → phân trang tự động tải thêm (pagination) | ✅ Pass | |
| 3.2.5 | Chuyển tab (Ngày → Tuần) → tải lại dữ liệu tương ứng | ✅ Pass | |

### 3.3. Fallback

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 3.3.1 | Nếu `/initmanga/v1/ranking` timeout/lỗi → fallback sang WordPress standard API (`orderby=views`) | ✅ Pass | |
| 3.3.2 | Fallback hoạt động trong suốt, người dùng không thấy lỗi | ✅ Pass | |

---

## 4. Tìm Kiếm & Bộ Lọc (Search & Filter)

### 4.1. Tìm Kiếm Theo Từ Khóa

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 4.1.1 | Tap thanh tìm kiếm → mở màn hình tìm kiếm, bàn phím hiện | ✅ Pass | |
| 4.1.2 | Gõ từ khóa → kết quả hiện sau khi debounce hoặc nhấn tìm | ✅ Pass | |
| 4.1.3 | Từ khóa tiếng Việt có dấu → kết quả chính xác | ✅ Pass | |
| 4.1.4 | Từ khóa rỗng → không gọi API / hiện gợi ý | ✅ Pass | |
| 4.1.5 | Từ khóa không khớp → empty state hiển thị | ✅ Pass | |

### 4.2. Bộ Lọc Nâng Cao

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 4.2.1 | Lọc theo Thể loại → danh sách thể loại hiển thị, chọn được | ✅ Pass | |
| 4.2.2 | Lọc theo Trạng thái: Tất cả / Đang ra / Hoàn thành | ✅ Pass | |
| 4.2.3 | Lọc theo Số chương: < 100 · 100–300 · 300–600 · 600–1000 · > 1000 | ✅ Pass | |
| 4.2.4 | Sắp xếp: Mới cập nhật / Mới đăng / Lượt đọc / Đánh giá | ✅ Pass | |
| 4.2.5 | Lọc theo Nguồn: TruyenCV / MotTruyen / Tất cả | ✅ Pass | |
| 4.2.6 | Kết hợp nhiều bộ lọc đồng thời → kết quả đúng | ✅ Pass | |
| 4.2.7 | Reset/Xóa bộ lọc → quay về trạng thái mặc định | ✅ Pass | |

### 4.3. Nguồn "Tất Cả" Khi Tìm Kiếm

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 4.3.1 | Chọn "Tất cả" → gọi song song 2 API | ✅ Pass | |
| 4.3.2 | Kết quả xen kẽ (interleave) giữa TruyenCV và MotTruyen | ✅ Pass | |
| 4.3.3 | Nếu 1 nguồn trả chậm → hiển thị kết quả nguồn kia trước (nếu UX cho phép) | ✅ Pass | |

---

## 5. Chi Tiết Truyện (Novel Detail)

### 5.1. Hiển Thị Thông Tin

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 5.1.1 | Ảnh bìa hiển thị đúng (tải từ URL) | ✅ Pass | |
| 5.1.2 | URL bìa bị lỗi double-slash (`//` trong path) → fallback hiện bìa mặc định | ✅ Pass | |
| 5.1.3 | Tên truyện hiển thị đầy đủ (không bị cắt ngắn sai) | ✅ Pass | |
| 5.1.4 | Tác giả hiển thị đúng (phân giải từ `_embedded['wp:term']` cho TruyenCV) | ✅ Pass | |
| 5.1.5 | Tác giả fallback → `class_list` → `author_name` → "Chưa rõ" | ✅ Pass | |
| 5.1.6 | Thể loại hiện dạng chips, đúng thể loại | ✅ Pass | |
| 5.1.7 | Trạng thái: "Đang ra" hoặc "Hoàn thành" hiển thị đúng | ✅ Pass | |
| 5.1.8 | Số chương hiển thị chính xác | ✅ Pass | |
| 5.1.9 | Nguồn (badge TruyenCV / MotTruyen) hiển thị | ✅ Pass | |
| 5.1.10 | Mô tả: hiển thị đã strip HTML, expandable (rút gọn / xem thêm) | ✅ Pass | |

### 5.2. Hành Động

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 5.2.1 | Nút "Đọc" (chưa có lịch sử) → mở chương đầu tiên | ✅ Pass | |
| 5.2.2 | Nút "Đọc" (đã đọc dở) → mở chương đọc dở, đúng vị trí | ✅ Pass | |
| 5.2.3 | Nút "Theo dõi" lần 1 → thêm vào tủ truyện, icon thay đổi | ✅ Pass | |
| 5.2.4 | Nút "Theo dõi" lần 2 → xóa khỏi tủ truyện, icon quay lại | ✅ Pass | |
| 5.2.5 | Nút "Tải xuống" → bắt đầu tải truyện offline | ✅ Pass | |

### 5.3. Danh Sách Chương

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 5.3.1 | Danh sách chương hiển thị phân trang | ✅ Pass | |
| 5.3.2 | Cuộn → tải thêm chương (lazy load / pagination) | ✅ Pass | |
| 5.3.3 | Nút đảo chiều sắp xếp: ASC ↔ DESC hoạt động | ✅ Pass | |
| 5.3.4 | Chương đã đọc → hiển thị màu khác so với chương chưa đọc | ✅ Pass | |
| 5.3.5 | Tap vào chương → mở Reader tại chương đó | ✅ Pass | |
| 5.3.6 | Truyện nhiều chương (> 500) → phân trang mượt, không OOM | ✅ Pass | |

---

## 6. Màn Hình Đọc (Reader Screen)

> [!IMPORTANT]
> Đây là màn hình cốt lõi nhất, cần kiểm thử kỹ nhất.

### 6.1. Chế Độ Cuộn Dọc (Scroll Mode)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 6.1.1 | Nội dung chương hiển thị đầy đủ, text rõ ràng | ✅ Pass | |
| 6.1.2 | Cuộn mượt 60fps, không giật/đơ | ✅ Pass | |
| 6.1.3 | Chạm giữa màn hình → hiện/ẩn thanh điều khiển (overlay) | ✅ Pass | |
| 6.1.4 | Cuộn tới cuối chương → tự động tải chương tiếp theo (infinite load) | ✅ Pass | |
| 6.1.5 | Cuộn ngược lên đầu → tự động tải chương trước | ✅ Pass | |
| 6.1.6 | Nội dung chương tiếp nối liền mạch (không bị giật vị trí cuộn) | ✅ Pass | |

### 6.2. Chế Độ Lật Trang (Pager Mode)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 6.2.1 | Vuốt trái/phải để lật trang hoạt động | ✅ Pass | |
| 6.2.2 | Lật trang animation mượt mà | ✅ Pass | |
| 6.2.3 | Lật hết trang cuối → chuyển sang chương tiếp | ✅ Pass | |
| 6.2.4 | Lật ngược trang đầu → chuyển sang chương trước | ✅ Pass | |
| 6.2.5 | Tap để hiện/ẩn overlay hoạt động đúng trong chế độ pager | ✅ Pass | |

### 6.3. Infinite Chapter Loading (Buffer ±6 chương)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 6.3.1 | Đọc liên tục qua 6+ chương → tải trước mượt, không phải chờ | ✅ Pass | |
| 6.3.2 | Buffer giới hạn 12 chương (6 trước + 6 sau) | ✅ Pass | |
| 6.3.3 | Vượt buffer → chương xa nhất bị cắt khỏi bộ nhớ | ✅ Pass | |
| 6.3.4 | Quay lại chương đã cắt → tải lại từ API/DB, không crash | ✅ Pass | |
| 6.3.5 | Áp dụng cho cả chế độ cuộn dọc lẫn lật trang | ✅ Pass | |
| 6.3.6 | Đọc chương đầu tiên (chương 1) → không lỗi khi tải chương trước | ✅ Pass | |
| 6.3.7 | Đọc chương cuối cùng → không lỗi khi tải chương tiếp | ✅ Pass | |

### 6.4. Thanh Điều Khiển (Reader Overlay)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 6.4.1 | **Thanh trên**: Tên chương hiển thị, nút Quay lại, nút Danh sách chương | ✅ Pass | |
| 6.4.2 | Nút "Quay lại" → về Chi Tiết Truyện | ✅ Pass | |
| 6.4.3 | Nút "Danh sách chương" → mở danh sách chương | ✅ Pass | |
| 6.4.4 | **Thanh dưới**: Nút chương trước/sau hoạt động | ✅ Pass | |
| 6.4.5 | Slider chương — kéo để nhảy đến chương bất kỳ | ✅ Pass | |
| 6.4.6 | Nút Cài đặt đọc → mở panel settings | ✅ Pass | |
| 6.4.7 | Nút TTS → kích hoạt TTS | ✅ Pass | |
| 6.4.8 | Nút Auto-Scroll → kích hoạt auto-scroll | ✅ Pass | |

### 6.5. Tùy Chỉnh Giao Diện Đọc (Reader Settings)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 6.5.1 | Cỡ chữ: Slider/+/- từ 12sp → 32sp, **áp dụng ngay** | ✅ Pass | |
| 6.5.2 | Font chữ: chuyển giữa Roboto / Noto Serif / Georgia / System default | ✅ Pass | |
| 6.5.3 | Màu nền: Trắng / Kem / Tối / AMOLED Đen / E-ink, **áp dụng ngay** | ✅ Pass | |
| 6.5.4 | Độ sáng: Slider 0%–100%, override system brightness | ✅ Pass | |
| 6.5.5 | Chế độ đọc: Toggle cuộn dọc ↔ lật trang, **chuyển ngay** | ✅ Pass | |
| 6.5.6 | Tất cả settings lưu DataStore, mở lại app vẫn giữ | ✅ Pass | |
| 6.5.7 | Keep Screen On: màn hình không tắt khi đang ở reader | ✅ Pass | |

### 6.6. Làm Sạch Nội Dung

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 6.6.1 | Nội dung TruyenCV: escape slashes đã xử lý (`\"` → `"`, `\'` → `'`) | ✅ Pass | |
| 6.6.2 | Nội dung TruyenCV: dòng lặp tiêu đề chương đầu bài đã bị xóa | ✅ Pass | |
| 6.6.3 | HTML tags đã strip, chỉ còn plain text dễ đọc | ✅ Pass | |
| 6.6.4 | Ký tự đặc biệt tiếng Việt hiển thị đúng | ✅ Pass | |

### 6.7. Lưu Vị Trí Đọc

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 6.7.1 | Rời khỏi Reader → vị trí cuộn/trang được lưu | ✅ Pass | |
| 6.7.2 | Quay lại truyện → resume đúng chương + vị trí cuộn | ✅ Pass | |
| 6.7.3 | Kill app → mở lại → vẫn resume đúng vị trí | ✅ Pass | |

---

## 7. Auto-Scroll (Tự Động Cuộn)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 7.1 | Bật Auto-Scroll → trang tự động cuộn xuống | ✅ Pass | |
| 7.2 | Slider tốc độ: điều chỉnh chậm / vừa / nhanh, thay đổi ngay | ✅ Pass | |
| 7.3 | Chạm màn hình → tạm dừng; chạm lại → tiếp tục cuộn | ✅ Pass | |
| 7.4 | Cuộn hết chương → tự động chuyển sang chương kế | ✅ Pass | |
| 7.5 | Keep Screen On khi Auto-Scroll hoạt động (không tắt màn) | ✅ Pass | |
| 7.6 | Khi TTS đang phát → tốc độ cuộn sync theo tốc độ đọc giọng nói | ✅ Pass | |
| 7.7 | Tắt Auto-Scroll → cuộn dừng, có thể cuộn thủ công bình thường | ✅ Pass | |

---

## 8. Text-to-Speech (TTS)

### 8.1. Điều Khiển Cơ Bản

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 8.1.1 | Nhấn Play → TTS bắt đầu đọc từ vị trí hiện tại | ✅ Pass | |
| 8.1.2 | Nhấn Pause → dừng đọc, nhấn lại → tiếp tục | ✅ Pass | |
| 8.1.3 | Tốc độ đọc: Slider 0.5x → 2.0x (mặc định 1.0x), thay đổi ngay | ✅ Pass | |
| 8.1.4 | Giọng đọc tiếng Việt rõ ràng (nếu engine hỗ trợ) | ✅ Pass | |

### 8.2. Highlight & Auto-Scroll Đồng Bộ

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 8.2.1 | Câu đang đọc được highlight (tô sáng) trên màn hình | ✅ Pass | |
| 8.2.2 | Trang tự scroll theo câu đang đọc | ✅ Pass | |
| 8.2.3 | Chuyển câu → highlight cập nhật đúng câu kế | ✅ Pass | |

### 8.3. Foreground Service & Media Notification

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 8.3.1 | Tắt màn hình → TTS tiếp tục phát | ✅ Pass | |
| 8.3.2 | Chuyển sang app khác → TTS tiếp tục phát nền | ✅ Pass | |
| 8.3.3 | Media Notification hiện trên thanh thông báo: Play/Pause, Next/Prev Chapter | ✅ Pass | |
| 8.3.4 | Điều khiển TTS từ lock screen | ✅ Pass | |
| 8.3.5 | Nhấn nút Next Chapter trên notification → chuyển chương, tiếp tục đọc | ✅ Pass | |
| 8.3.6 | Nhấn nút Prev Chapter trên notification → quay chương trước, tiếp tục đọc | ✅ Pass | |

### 8.4. Chuyển Chương & Sleep Timer

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 8.4.1 | Đọc hết chương → tự động tải + đọc chương tiếp theo | ✅ Pass | |
| 8.4.2 | Sleep Timer: đặt 15 / 30 / 45 / 60 phút / Cuối chương | ✅ Pass | |
| 8.4.3 | Sleep Timer hết giờ → TTS dừng + lưu vị trí đọc | ✅ Pass | |
| 8.4.4 | Sleep Timer "Cuối chương" → đọc hết chương hiện tại mới dừng | ✅ Pass | |

### 8.5. Keep Screen On & Pin

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 8.5.1 | TTS đang phát + user xem reader → giữ sáng màn hình | ✅ Pass | |
| 8.5.2 | TTS phát nền (tắt màn) → không giữ sáng (tiết kiệm pin) | ✅ Pass | |
| 8.5.3 | Dừng TTS → trở lại cơ chế timeout màn hình bình thường | ✅ Pass | |

---

## 9. Tủ Truyện (Library)

### 9.1. Tab "Theo Dõi"

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 9.1.1 | Hiển thị danh sách truyện đã follow/bookmark | ✅ Pass | |
| 9.1.2 | Mỗi item: bìa, tên, chương đọc tới, số chương, nguồn | ✅ Pass | |
| 9.1.3 | Tap → mở Chi Tiết Truyện | ✅ Pass | |
| 9.1.4 | Vuốt để xóa (swipe-to-delete) hoạt động | ✅ Pass | |
| 9.1.5 | Long-press → hiện menu (xóa, v.v.) | ✅ Pass | |
| 9.1.6 | Truyện từ cả 2 nguồn (TruyenCV + MotTruyen) hiển thị cùng danh sách | ✅ Pass | |

### 9.2. Tab "Lịch Sử"

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 9.2.1 | Hiển thị truyện đã đọc, sắp theo thời gian gần nhất | ✅ Pass | |
| 9.2.2 | Ghi nhớ chương đã đọc + vị trí cuộn chính xác | ✅ Pass | |
| 9.2.3 | Tap → mở Reader tại đúng vị trí đọc dở | ✅ Pass | |
| 9.2.4 | Đọc truyện mới → lịch sử cập nhật ngay | ✅ Pass | |
| 9.2.5 | Vuốt/xóa lịch sử hoạt động | ✅ Pass | |

### 9.3. Tab "Offline"

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 9.3.1 | Hiển thị truyện đã tải xong hoàn toàn | ✅ Pass | |
| 9.3.2 | Ảnh bìa hiện offline (cached Base64) | ✅ Pass | |
| 9.3.3 | Tắt mạng → vẫn đọc được truyện offline | ✅ Pass | |
| 9.3.4 | Tap → mở Reader, nội dung load từ DB local | ✅ Pass | |
| 9.3.5 | Vuốt xóa → xóa truyện offline (nội dung + metadata) | ✅ Pass | |

### 9.4. Tab "Đang Tải"

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 9.4.1 | Hiển thị truyện đang trong tiến trình download | ✅ Pass | |
| 9.4.2 | Progress bar cập nhật realtime (X/Y chương) | ✅ Pass | |
| 9.4.3 | Nút Tạm dừng → dừng tải, trạng thái chuyển sang paused | ✅ Pass | |
| 9.4.4 | Nút Tiếp tục → resume tải từ vị trí dừng | ✅ Pass | |
| 9.4.5 | Nút Hủy → dừng tải, hỏi xác nhận (nếu cần) | ✅ Pass | |

---

## 10. Tải Truyện Offline (Download Engine)

### 10.1. Luồng Tải Chính

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 10.1.1 | Nhấn "Tải xuống" từ Chi Tiết → bắt đầu tải, notification hiện | ✅ Pass | |
| 10.1.2 | Concurrency: tối đa 4 request đồng thời | ✅ Pass | |
| 10.1.3 | Batch size: 50 chương/batch | ✅ Pass | |
| 10.1.4 | Cooldown 300–500ms giữa các batch (không gọi liên tục) | ✅ Pass | |

### 10.2. Idempotent & Resume

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 10.2.1 | Chương đã tải thành công → không tải lại (skip) | ✅ Pass | |
| 10.2.2 | Kill app giữa chừng → mở lại → trạng thái chuyển thành `paused` | ✅ Pass | |
| 10.2.3 | Nhấn Tiếp tục → resume đúng chương đang dở, không tải lại từ đầu | ✅ Pass | |

### 10.3. Retry & Pass 2

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 10.3.1 | Chương lỗi → retry tối đa 3 lần, exponential backoff (300ms × attempt) | ✅ Pass | |
| 10.3.2 | Sau đợt tải chính, chương vẫn lỗi → chạy Pass 2 (vét) với delay 400ms | ✅ Pass | |
| 10.3.3 | Pass 2 thành công → truyện chuyển sang trạng thái hoàn tất | ✅ Pass | |

### 10.4. Cache Bìa Offline

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 10.4.1 | Khi tải truyện → fetch ảnh bìa → convert Base64 → lưu vào Room DB | ✅ Pass | |
| 10.4.2 | Tắt mạng → ảnh bìa vẫn hiển thị từ Base64 cached | ✅ Pass | |

### 10.5. Notification Foreground Service

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 10.5.1 | Notification hiện progress tải (X/Y chương) | ✅ Pass | |
| 10.5.2 | Tải nền khi tắt màn hình / chuyển app → tiếp tục tải | ✅ Pass | |
| 10.5.3 | Tải hoàn tất → notification cập nhật "Hoàn tất" | ✅ Pass | |

---

## 11. Cài Đặt (Settings)

### 11.1. Nhóm Giao Diện

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 11.1.1 | Theme: Toggle Theo hệ thống / Sáng / Tối → áp dụng ngay | ✅ Pass | |
| 11.1.2 | Đổi theme → quay về app → giao diện đúng theme đã chọn | ✅ Pass | |

### 11.2. Nhóm Reader Mặc Định

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 11.2.1 | Đổi Font mặc định → mở reader mới → font đúng | ✅ Pass | |
| 11.2.2 | Đổi cỡ chữ mặc định → reader áp dụng đúng | ✅ Pass | |
| 11.2.3 | Đổi màu nền reader mặc định → reader áp dụng đúng | ✅ Pass | |
| 11.2.4 | Đổi chế độ đọc mặc định (cuộn/lật) → reader áp dụng đúng | ✅ Pass | |

### 11.3. Nhóm TTS

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 11.3.1 | Đổi tốc độ TTS mặc định → TTS lần sau dùng tốc độ này | ✅ Pass | |
| 11.3.2 | Đổi Sleep Timer mặc định → TTS lần sau dùng timer này | ✅ Pass | |

### 11.4. Nhóm Dữ Liệu

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 11.4.1 | Xóa cache → cache API/ảnh bị xóa, app tải lại dữ liệu | ✅ Pass | |
| 11.4.2 | Xóa lịch sử đọc → tab Lịch Sử trống, hỏi xác nhận trước | ✅ Pass | |
| 11.4.3 | Xóa tất cả truyện offline → tab Offline trống, dung lượng giảm | ✅ Pass | |

### 11.5. Nhóm Thông Tin

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 11.5.1 | Phiên bản app hiển thị đúng | ✅ Pass | |
| 11.5.2 | Thông tin dự án hiển thị đúng | ✅ Pass | |

---

## 12. Sao Lưu Google Drive (Backup & Restore)

### 12.1. Đăng Nhập Google

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 12.1.1 | Chưa đăng nhập → nhấn Sao lưu → yêu cầu đăng nhập Google | ✅ Pass | |
| 12.1.2 | Google Sign-In flow hoàn tất thành công | ✅ Pass | |
| 12.1.3 | Chỉ yêu cầu scope `Drive.appdata` (không xin quyền thừa) | ✅ Pass | |
| 12.1.4 | Không lưu token lên log / console | ✅ Pass | |

### 12.2. Sao Lưu (Backup)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 12.2.1 | Nhấn "Sao lưu" → tạo file `truyencv_backup_YYYYMMDD_HHmmss.json.gz` | ✅ Pass | |
| 12.2.2 | File lưu vào `appDataFolder` trên Google Drive (thư mục ẩn) | ✅ Pass | |
| 12.2.3 | Dữ liệu backup gồm: Bookmark, Lịch sử đọc, Cài đặt reader, Metadata offline | ✅ Pass | |
| 12.2.4 | Dữ liệu KHÔNG backup: Nội dung chương offline (quá nặng) | ✅ Pass | |
| 12.2.5 | Hiển thị thời gian backup gần nhất sau khi sao lưu xong | ✅ Pass | |
| 12.2.6 | Loading/progress indicator trong quá trình backup | ✅ Pass | |

### 12.3. Khôi Phục (Restore)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 12.3.1 | Nhấn "Khôi phục" → liệt kê bản backup có sẵn trên Drive | ✅ Pass | |
| 12.3.2 | Chọn bản backup → tải về, giải nén, khôi phục dữ liệu | ✅ Pass | |
| 12.3.3 | Sau khôi phục: Bookmark đúng, Lịch sử đọc đúng, Settings đúng | ✅ Pass | |
| 12.3.4 | Hỏi xác nhận trước khi ghi đè dữ liệu hiện tại | ✅ Pass | |
| 12.3.5 | Metadata offline → hiển thị trong tab Offline (sẵn sàng tải lại) | ✅ Pass | |

---

## 13. Xử Lý Lỗi & Mạng (Error & Network Handling)

### 13.1. Trạng Thái Tải (Loading States)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 13.1.1 | Shimmer/Skeleton placeholder hiện đúng vị trí khi tải dữ liệu | ✅ Pass | |
| 13.1.2 | Shimmer biến mất khi dữ liệu tải xong | ✅ Pass | |
| 13.1.3 | Shimmer áp dụng cho: Khám Phá, BXH, Tìm kiếm, Chi tiết, Reader | ✅ Pass | |

### 13.2. Empty State

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 13.2.1 | Không có dữ liệu → empty state với illustration + text mô tả | ✅ Pass | |
| 13.2.2 | Tủ Truyện trống → "Chưa có truyện nào" hoặc tương tự | ✅ Pass | |
| 13.2.3 | Tìm kiếm không khớp → "Không tìm thấy kết quả" | ✅ Pass | |

### 13.3. Lỗi Mạng (Error State)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 13.3.1 | Tắt mạng → Error state + nút "Thử lại" (Retry) | ✅ Pass | |
| 13.3.2 | Nhấn "Thử lại" → gọi lại API | ✅ Pass | |
| 13.3.3 | Mất kết nối đang dùng → banner thông báo ở trên cùng | ✅ Pass | |
| 13.3.4 | Có mạng lại → banner tự ẩn (auto-dismiss) | ✅ Pass | |

### 13.4. Pull-to-Refresh

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 13.4.1 | Pull-to-Refresh hoạt động ở tất cả danh sách | ✅ Pass | |
| 13.4.2 | Kéo → spinner hiện → dữ liệu tải lại → spinner ẩn | ✅ Pass | |

### 13.5. API Cache

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 13.5.1 | Gọi lại trang vừa xem → dữ liệu từ cache (nhanh hơn), không gọi API lại | ✅ Pass | |
| 13.5.2 | Pull-to-Refresh → bypass cache, lấy dữ liệu mới | ✅ Pass | |

---

## 14. Hiệu Năng & Phi Chức Năng (Performance & NFR)

### 14.1. Hiệu Năng

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 14.1.1 | App cold start < **3 giây** | ✅ Pass | |
| 14.1.2 | Tải chương < **2 giây** | ✅ Pass | |
| 14.1.3 | Reader cuộn mượt **60fps** (không jank) | ✅ Pass | |
| 14.1.4 | Chuyển tab nhanh, không delay đáng kể | ✅ Pass | |
| 14.1.5 | Tìm kiếm → kết quả hiện trong < 3s | ✅ Pass | |

### 14.2. Bộ Nhớ & Pin

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 14.2.1 | Đọc 20+ chương liên tục → không OOM crash | ✅ Pass | |
| 14.2.2 | Buffer chương giữ ≤ 12, cắt bớt chương xa | ✅ Pass | |
| 14.2.3 | TTS Foreground Service không wake-lock khi tắt màn hình đọc | ✅ Pass | |
| 14.2.4 | Dùng app 1 giờ → không tiêu hao pin bất thường | ✅ Pass | |

### 14.3. Offline Toàn Phần

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 14.3.1 | Bật chế độ máy bay → truyện đã tải đọc bình thường | ✅ Pass | |
| 14.3.2 | Ảnh bìa offline hiển thị (Base64 cached) | ✅ Pass | |
| 14.3.3 | Tủ truyện (Theo dõi, Lịch sử) hiện từ local DB | ✅ Pass | |
| 14.3.4 | Tab Khám Phá/BXH → error state + retry (vì cần mạng) | ✅ Pass | |

### 14.4. Khả Năng Tương Thích

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 14.4.1 | Chạy trên Android 7.0 (API 24) — min SDK | ✅ Pass | |
| 14.4.2 | Chạy trên Android 15 (API 35) — target SDK | ✅ Pass | |
| 14.4.3 | Chạy trên thiết bị phổ thông (RAM 3–4GB) không lag | ✅ Pass | |
| 14.4.4 | Chạy trên tablet (màn lớn) → giao diện không vỡ | ✅ Pass | |

---

## 15. Tích Hợp Dữ Liệu Đa Nguồn (Unified Source)

### 15.1. ID Prefixing

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 15.1.1 | Truyện từ TruyenCV có ID prefix `tcv-` (vd: `tcv-1234`) | ✅ Pass | |
| 15.1.2 | Truyện từ MotTruyen có ID prefix `mt-` (vd: `mt-5678`) | ✅ Pass | |
| 15.1.3 | Bookmark truyện từ cả 2 nguồn → lưu đúng prefix, không trùng | ✅ Pass | |

### 15.2. Ánh Xạ Thể Loại Chéo (Genre Cross-Mapping)

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 15.2.1 | Lọc "Tiên Hiệp" (Tất cả nguồn) → TruyenCV genre=16 + MotTruyen cat=14 | ✅ Pass | |
| 15.2.2 | Lọc "Ngôn Tình" → đúng mapping ID cho cả 2 nguồn | ✅ Pass | |
| 15.2.3 | Thể loại chỉ có 1 nguồn → chỉ hiện kết quả nguồn đó | ✅ Pass | |

### 15.3. Interleave Kết Quả

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 15.3.1 | Chế độ "Tất cả": kết quả xen kẽ TCV-1, MT-1, TCV-2, MT-2... | ✅ Pass | |
| 15.3.2 | Nguồn A nhiều hơn nguồn B → phần dư hiện ở cuối | ✅ Pass | |
| 15.3.3 | 1 nguồn lỗi → chỉ hiện kết quả nguồn còn lại (graceful) | ✅ Pass | |

### 15.4. Chuyển Nguồn Cross-Source

| # | Hạng mục kiểm thử | Kết quả | Ghi chú |
|---|---|:---:|---|
| 15.4.1 | Mở chi tiết truyện TruyenCV → dữ liệu, endpoint, response đúng | ✅ Pass | |
| 15.4.2 | Mở chi tiết truyện MotTruyen → dữ liệu, endpoint, response đúng | ✅ Pass | |
| 15.4.3 | Reader load chương TruyenCV (endpoint `/initmanga/v1/chapter/{id}`) | ✅ Pass | |
| 15.4.4 | Reader load chương MotTruyen (endpoint `/chapter/?chapter_id={id}`) | ✅ Pass | |

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
