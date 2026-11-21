# NHẬT KÝ TIẾN ĐỘ & TRÍ NHỚ DỰ ÁN (PROJECT PROGRESS & MEMORY LOG)

Tài liệu này lưu trữ toàn bộ lịch sử công việc, tiến trình phát triển, các vấn đề đã gặp phải, giải pháp khắc phục và bài học kinh nghiệm.
Mục tiêu: Đảm bảo tính liên tục, không lặp lại công việc đã làm, và giữ nguyên vẹn ngữ cảnh (Context Memory) cho các phiên làm việc tiếp theo của AI và Developer.

---

## 1. Thông Tin Chung Dự Án

- **Tên dự án**: `TruyenCV_APK` (Tái thiết kế & xây dựng mới ứng dụng đọc truyện TruyenCV & MotTruyen).
- **Thư mục làm việc**: `D:\LearnBlazor\TruyenCV_APK`
- **Dự án tiền nhiệm (tham khảo API & luồng nghiệp vụ)**: `D:\LearnBlazor\TruyenCv\TruyenCVApp`
- **Kho lưu trữ GitHub (Repository)**: [https://github.com/Yusung-IT-NhanVo/TruyenCV_APK](https://github.com/Yusung-IT-NhanVo/TruyenCV_APK)
- **Clone URL**: `https://github.com/Yusung-IT-NhanVo/TruyenCV_APK.git`
- **Nhánh chính**: `main`

---

## 2. Quy Định Ghi Chép Tiến Độ (Logging Guidelines)

Sau khi hoàn thành mỗi Task hoặc khi xử lý xong một vấn đề kỹ thuật quan trọng:
1. **Ghi nhận ngay**: Cập nhật mục `Lịch Sử Công Việc Chi Tiết` theo mẫu bên dưới.
2. **Commit & Push Git**: Thực hiện commit với message chuẩn hóa (Conventional Commits) và `git push origin <branch>`.
3. **Cập nhật trạng thái**: Đánh dấu trạng thái tương ứng trong bảng theo dõi tổng quan.

---

## 3. Bảng Theo Dõi Tiến Độ Tổng Quan (Roadmap Status)

| Mã Task | Hạng Mục Công Việc | Trạng Thái | Ngày Cập Nhật | Ghi Chú |
| :--- | :--- | :---: | :---: | :--- |
| `TASK-001` | Trích xuất API từ dự án cũ TruyenCVApp | ✅ HOÀN THÀNH | 2026-11-20 | Đã lập file `API_INTEGRATION_SPEC.md` |
| `TASK-002` | Thiết lập bộ nguyên tắc cốt lõi dự án | ✅ HOÀN THÀNH | 2026-11-20 | Đã lập file `PROJECT_PRINCIPLES.md` |
| `TASK-003` | Cấu hình Git, nhật ký tiến độ & đồng bộ quy tắc | ✅ HOÀN THÀNH | 2026-11-20 | Khởi tạo repo git local, lập `.gitignore`, `PROGRESS_LOG.md` |
| `TASK-004` | Cấu hình GitHub Remote Repo | ✅ HOÀN THÀNH | 2026-11-20 | Đã tạo repo `Yusung-IT-NhanVo/TruyenCV_APK`, push nhánh `main` thành công |
| `TASK-005` | Bổ sung quy tắc bắt buộc Unit Test & Gate Rule | ✅ HOÀN THÀNH | 2026-11-21 | Đã tích hợp quy tắc Unit Test, Gate Rule 8 bước & Android Device Hub vào `PROJECT_PRINCIPLES.md` |
| `TASK-006` | Khởi tạo nền tảng ứng dụng Android native (Phase 0) | ✅ HOÀN THÀNH | 2026-11-21 | Kotlin, Compose, Material 3, Room, Retrofit, Coil 3, KSP, Nav 3, 4 tab shell, AppContainer DI |
| `TASK-007` | Triển khai Core Layers (Phase 1: Module A, B, C) | ⚪ CHƯA BẮT ĐẦU | - | Sẵn sàng phân bổ Swarm Agents độc lập |

*Ghi chú trạng thái: ⚪ Chưa bắt đầu | 🔄 Đang thực hiện | ⏳ Chờ xác nhận | ✅ Hoàn thành | ❌ Tạm dừng/Hủy*

---

## 4. Lịch Sử Công Việc Chi Tiết & Giải Pháp Kỹ Thuật (Memory Log)

### Ngày 2026-11-20

#### [TASK-001] Trích xuất và đặc tả API cốt lõi
- **Mục tiêu**: Đọc toàn bộ code dịch vụ tại `D:\LearnBlazor\TruyenCv\TruyenCVApp` và trích xuất cách gọi, tham số và xử lý dữ liệu API của TruyenCV và MotTruyen.
- **Công việc đã làm**:
  - Khảo sát các service: `truyenCvService.js`, `motTruyenService.js`, `unifiedSource.js`, `downloadService.js`, `sqliteDb.js`, `chapterFormat.js`, `coverUrl.js`.
  - Soạn thảo và lưu trữ tài liệu đặc tả toàn diện tại [API_INTEGRATION_SPEC.md](file:///d:/LearnBlazor/TruyenCV_APK/API_INTEGRATION_SPEC.md).
- **Vấn đề đã khắc phục & bài học ghi nhớ**:
  - *Vấn đề 1*: Tác giả trong API TruyenCV `/wp/v2/manga` trả về ID bot (vd: 1123). Khắc phục: Lấy tác giả từ `_embedded['wp:term']` với taxonomy `author_tax` hoặc từ `class_list`.
  - *Vấn đề 2*: URL bìa bị lỗi double-slash (`https://.../poster//150.jpg`) gây lỗi 404. Khắc phục: Regex bắt lỗi `//` sau scheme để fallback về bìa mặc định.
  - *Vấn đề 3*: Nội dung chương của TruyenCV bị lỗi gạch chéo escape JSON/MySQL và lặp tiêu đề chương ở đầu bài viết. Đã có regex làm sạch tương ứng.

#### [TASK-002] Thiết lập bộ nguyên tắc phát triển dự án
- **Mục tiêu**: Xây dựng bộ quy tắc làm việc nghiêm ngặt đảm bảo chất lượng và tính ổn định.
- **Công việc đã làm**:
  - Tạo tài liệu chuẩn tại [PROJECT_PRINCIPLES.md](file:///d:/LearnBlazor/TruyenCV_APK/PROJECT_PRINCIPLES.md).
  - Chuẩn hóa 5 nguyên tắc cốt lõi: Không làm vượt bước, Không tự ý quyết định thay người dùng, Không sửa code mù quáng, Không tuyên bố hoàn thành khi chưa kiểm chứng, Không lạm dụng công nghệ.
  - Tích hợp biểu mẫu báo cáo tiến độ chuẩn sau mỗi giai đoạn.

#### [TASK-003] Thiết lập Git & Hệ thống lưu trữ trí nhớ dự án
- **Mục tiêu**: Khởi tạo Git repository local, tạo `.gitignore`, lập tài liệu nhật ký trí nhớ `PROGRESS_LOG.md` và tích hợp quy tắc tự động hóa Git vào quy trình.
- **Công việc đã làm**:
  - Khởi tạo Git local repository (`git init`, nhánh chính `main`).
  - Thiết lập `.gitignore` loại trừ file hệ điều hành, IDE, build artifacts.
  - Tạo file [PROGRESS_LOG.md](file:///d:/LearnBlazor/TruyenCV_APK/PROGRESS_LOG.md).
  - Bổ sung quy tắc bắt buộc commit & push Git sau mỗi task vào `PROJECT_PRINCIPLES.md`.

---

### Ngày 2026-11-21

#### [TASK-005] Bổ sung quy tắc bắt buộc Unit Test & Self-Healing Loop
- **Mục tiêu**: Bổ sung quy tắc kiểm thử tự động bắt buộc vào nguyên tắc phát triển dự án.
- **Công việc đã làm**:
  - Cập nhật [PROJECT_PRINCIPLES.md](file:///d:/LearnBlazor/TruyenCV_APK/PROJECT_PRINCIPLES.md) thêm mục **6. Bắt Buộc Viết Unit Test & Tự Động Xác Minh (Mandatory Unit Testing & Self-Healing Loop)**.
  - Quy định mọi hàm/logic nghiệp vụ mới phải đi kèm Unit Test (Vitest/Jest/xUnit/Pytest tùy stack).
  - Quy định vòng lặp tự động chạy test trong terminal và sửa code cho tới khi toàn bộ test cases đều PASS trước khi sang bước tiếp theo.

#### [TASK-006] Khởi tạo nền tảng ứng dụng Android native (Phase 0 Foundation)
- **Mục tiêu**: Xây dựng khung xương ứng dụng Android native chuẩn bị cho các Swarm Agents chạy độc lập ở Phase 1.
- **Công việc đã làm**:
  - Khởi tạo project Kotlin + Jetpack Compose + Material 3 (minSdk 24, compileSdk/targetSdk 36).
  - Cấu hình Gradle dependencies qua Version Catalog (`libs.versions.toml`): Room 2.7.2, KSP 2.3.14, Retrofit 2.11.0, OkHttp 4.12.0, Coil 3.0.4, DataStore Preferences 1.2.0, Kotlinx Serialization 1.8.0, Navigation 3.
  - Xây dựng Domain Models: `Novel`, `Chapter`, `Source`, `NovelStatus`, `Genre`, `ReadingProgress`, `RankingPeriod`, `SearchQuery`.
  - Định nghĩa Repository Interfaces: `NovelRepository`, `ChapterRepository`, `BookmarkRepository`, `HistoryRepository`.
  - Khởi tạo Room Database stub (`TruyenCVDatabase`, `NovelEntity`, `NovelDao`).
  - Khởi tạo Retrofit API Service stubs (`TruyenCvApiService`, `MotTruyenApiService`).
  - Thiết lập App Shell & Bottom Navigation Bar 4 tabs (🔍 Khám Phá, 🏆 BXH, 📚 Tủ Truyện, ⚙️ Cài Đặt) với `MainScreen` và `NavDisplay`.
  - Thiết lập AppContainer DI và `TruyenCVApp` application class.
- **Kiểm thử & Xác minh đầy đủ (Gate Rule)**:
  - *Build*: `./gradlew assembleDebug` -> PASS (Exit code 0, APK 14.5MB).
  - *Lint*: `./gradlew lintDebug` -> PASS (0 error, HTML report generated).
  - *Unit Test*: `./gradlew testDebugUnitTest` -> PASS (100% 6/6 test cases trong `Phase0DomainModelTest`).
  - *Deploy*: `adb install -r app-debug.apk` lên giả lập Android `medium_phone` (`emulator-5554`) thành công.
  - *Manual Test (Android Device Hub)*: Sử dụng `android layout --device emulator-5554 --pretty` xác nhận 4 tab hiển thị đúng, tap chuyển tab qua `adb shell input tap` cập nhật đúng màn hình tương ứng.
  - *Bằng chứng*: Lưu ảnh chụp màn hình kiểm thử tại `screenshots/phase0_explore_tab.png` và `screenshots/phase0_settings_tab.png`.

---

## 5. Danh Sách Vấn Đề Tồn Tại & Cần Giải Quyết Tiếp Theo
1. **Tiến hành Phase 1 (Core Layers)**: Phân bổ 3 Module độc lập cho Swarm Agents chạy song song:
   - **Module A**: Data Layer & Network (Room DAOs, Retrofit API implementation, Mappers, Repositories).
   - **Module B**: UI Components & Theme (NovelCard, Shimmer, EmptyState, Carousel, Custom Theme).
   - **Module C**: Navigation & App Shell wiring.
