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
| `TASK-007` | Triển khai Core Layers (Phase 1: Module A, B, C) | ✅ HOÀN THÀNH | 2026-11-21 | 3 Swarm Agents song song: Room (6 DAOs), 2 APIs, Mappers, Repos, Theme 5 Reader, Components, Nav 3 |
| `TASK-008` | Tích hợp CodeGraph Rules cho Android Kotlin | ✅ HOÀN THÀNH | 2026-11-21 | Bổ sung quy tắc Graph-First, xử lý DI blind spots và Kotlin AST scope vào `PROJECT_PRINCIPLES.md` và `.codegraph/rules.md` |
| `TASK-009` | Triển khai Feature Screens (Phase 2: Module D, E, F) | ⚪ CHƯA BẮT ĐẦU | - | Explore, Search, Ranking, Detail, Library (Tủ Truyện 4 tabs) |

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

#### [TASK-007] Triển khai Core Layers (Phase 1: Module A, B, C)
- **Mục tiêu**: Xây dựng toàn bộ các tầng cốt lõi (Data Layer, Shared UI Components & Theme, Navigation & App Shell) bằng 3 Swarm Agents chạy song song độc lập, không xung đột file.
- **Công việc đã làm**:
  - **Module A (Data Layer)**:
    + 6 Room Entities & DAOs: `NovelEntity`/`NovelDao`, `ChapterEntity`/`ChapterDao`, `BookmarkEntity`/`BookmarkDao`, `ReadingHistoryEntity`/`ReadingHistoryDao`, `GenreEntity`/`GenreDao`, `DownloadTaskEntity`/`DownloadTaskDao`.
    + 2 Remote API services: `TruyenCvApiService` (WP REST API & custom endpoints) và `MotTruyenApiService` (Filter, Search, Listchap, Chapter).
    + OkHttp Interceptors: `UserAgentInterceptor` và `MotTruyenDefaultParamsInterceptor` (`deviceid=antigravity&os=android&app_version=1.0.6`).
    + Mappers & Utilities: `GenreMappingTable` (20 canonical slugs), `InterleaveUtil` (xen kẽ kết quả đa nguồn), `TextSanitizer` (làm sạch HTML, xử lý double-slash cover URL, unescape, xóa tiêu đề lặp), `TruyenCvMapper`, `MotTruyenMapper`.
    + Repositories: `NovelRepositoryImpl`, `ChapterRepositoryImpl`, `BookmarkRepositoryImpl`, `HistoryRepositoryImpl`.
    + DataStore Preferences: `ReaderPreferences`, `AppPreferences`.
  - **Module B (UI Components & Theme)**:
    + Theme Material 3 hoàn chỉnh với 5 bảng màu Reader chuyên biệt: Trắng (Light), Giấy cũ (Sepia), Tối (Dark), Đen sâu (AMOLED), Bảo vệ mắt (Green/E-ink).
    + 11 Shared UI Components Compose có `@Preview`: `NovelCard`, `NovelListItem`, `ChapterListItem`, `SourceChipGroup`, `GenreChip`, `GenreGrid`, `ShimmerPlaceholder`, `EmptyStateView`, `ErrorStateView`, `NetworkBanner`, `HorizontalCarousel`, `SearchBarField`.
  - **Module C (Navigation & App Shell)**:
    + 6 type-safe Navigation 3 `NavKey`: `NavMain`, `NavNovelDetail`, `NavReader`, `NavSearch`, `NavGenreList`, `NavChapterList`.
    + Điều phối màn hình qua `NavigationActions`, `LocalNavigationActions`, `PlaceholderScreens`, hỗ trợ Edge-to-Edge và WindowInsets an toàn.
- **Kiểm thử & Xác minh đầy đủ (Gate Rule 8 Bước)**:
  - *Build*: `./gradlew assembleDebug` -> **PASS** (Exit code 0).
  - *Lint*: `./gradlew lintDebug` -> **PASS** (0 errors).
  - *Unit Test*: `./gradlew testDebugUnitTest` -> **PASS 100%** (35/35 test cases: `GenreMappingTableTest` 5/5, `InterleaveUtilTest` 6/6, `TextSanitizerTest` 6/6, `MapperTest` 11/11, `Phase0DomainModelTest` 7/7).
  - *Deploy*: `adb install -r app-debug.apk` lên Android Emulator `medium_phone` (`emulator-5554`) thành công.
  - *Manual Test (Android Device Hub)*: Trích xuất `android layout` xác nhận giao diện hiển thị chuẩn xác với Edge-to-Edge, Safe Insets padding, 4 tabs chuyển đổi mượt mà.
  - *Bằng chứng*: Lưu ảnh chụp màn hình tại `screenshots/phase1_core_layers.png`.

#### [TASK-008] Tích hợp CodeGraph Rules cho Android Kotlin
- **Mục tiêu**: Bổ sung bộ quy tắc khai thác và sử dụng CodeGraph vào hệ thống nguyên tắc dự án và thư mục `.codegraph/`.
- **Công việc đã làm**:
  - Cập nhật [PROJECT_PRINCIPLES.md](file:///d:/LearnBlazor/TruyenCV_APK/PROJECT_PRINCIPLES.md) thêm mục **8. Quy Tắc CodeGraph Cho Dự Án Android Kotlin (CodeGraph Rules)**.
  - Tạo file [.codegraph/rules.md](file:///d:/LearnBlazor/TruyenCV_APK/.codegraph/rules.md) lưu trữ quy tắc riêng biệt phục vụ công cụ CodeGraph.
  - Định nghĩa 3 nguyên tắc trọng yếu:
    1. **Graph-First**: Ưu tiên MCP tool / CLI `codegraph explore` để phân tích định nghĩa, call graph và dependency tree trước khi dùng `grep` hay đọc cả file; khoanh vùng tác động trước khi sửa code.
    2. **Xử lý điểm mù DI (Dependency Injection)**: Hướng dẫn truy vết từ Interface sang class Implementation thông qua module DI chứa `@Binds`/`@Provides` hoặc container declarations (`AppContainer`).
    3. **Kiểm tra liên kết ngoài phạm vi Kotlin AST**: Hướng dẫn đối chiếu thêm `AndroidManifest.xml`, tài nguyên `res/` và Gradle files (`build.gradle.kts`, `libs.versions.toml`).

---

#### [TASK-009] Triển khai Feature Screens (Phase 2: Module D, E, F)
- **Mục tiêu**: Xây dựng toàn bộ các màn hình tính năng người dùng (Khám Phá, Tìm Kiếm & Lọc Nâng Cao, Danh Mục Thể Loại, Bảng Xếp Hạng 4 Kỳ, Chi Tiết Truyện, Danh Sách Chương, Tủ Truyện 4 Tabs) theo SRS §5.1 - §5.8.
- **Công việc đã làm**:
  - **Module D (Khám Phá & Tìm Kiếm)**:
    + `ExploreViewModel.kt` & `ExploreScreen.kt`: Feed cuộn dọc với Pull-to-Refresh, Banner Carousel tự động chuyển, mục Đề cử nổi bật, Lọc nguồn (Tất cả, TruyenCV, MotTruyen), và phân trang Lazy Loading.
    + `SearchViewModel.kt` & `SearchScreen.kt`: Thanh tìm kiếm tích hợp debounce 400ms, bộ lọc nguồn, bộ lọc trạng thái, ModalBottomSheet lọc nâng cao (thể loại, sắp xếp, nguồn, trạng thái).
    + `GenreListScreen.kt`: Lưới 3 cột 20 thể loại canonical chuẩn SRS §4.3.
    + Unit tests: `ExploreViewModelTest.kt` (6 tests), `SearchViewModelTest.kt` (7 tests).
  - **Module E (Bảng Xếp Hạng & Chi Tiết Truyện)**:
    + `RankingViewModel.kt` & `RankingScreen.kt`: 4 kỳ xếp hạng (Ngày, Tuần, Tháng, Tổng), huy hiệu xếp hạng Top 1 (vàng), Top 2 (bạc), Top 3 (đồng).
    + `NovelDetailViewModel.kt` & `NovelDetailScreen.kt`: Bìa truyện, thông tin tác giả, badges nguồn và trạng thái, nút Đọc từ đầu, nút Theo dõi (Bookmark Room DB), nút Tải về, Card Giới thiệu truyện có rút gọn/mở rộng, 3 chương mới nhất.
    + `ChapterListScreen.kt`: Hiển thị toàn bộ danh sách chương (vd: 7400+ chương), tìm kiếm theo số chương/tên chương, đảo thứ tự Cũ -> Mới / Mới -> Cũ.
    + Unit tests: `RankingViewModelTest.kt` (6 tests), `NovelDetailViewModelTest.kt` (5 tests).
  - **Module F (Tủ Truyện 4 Tabs)**:
    + `LibraryViewModel.kt` & `LibraryScreen.kt`: Tích hợp 4 Tab nội bộ: 📚 Theo dõi (Bookmarks), 🕒 Lịch sử (Reading History), 💾 Offline (Offline Novels), ⏳ Đang tải (Download Tasks).
    + `FollowTabContent.kt`, `HistoryTabContent.kt`, `OfflineTabContent.kt`, `DownloadingTabContent.kt`.
    + Badge đếm số lượng realtime trên tiêu đề tab; EmptyStateView sinh động khi danh sách trống.
    + Unit tests: `LibraryViewModelTest.kt` (14 tests).
  - **Tích hợp Tầng Điều Phối**:
    + Nối toàn bộ màn hình thực tế vào `MainScreen.kt` và `Navigation.kt` (Navigation 3).
- **Kiểm thử & Xác minh đầy đủ (Gate Rule 8 Bước)**:
  - *Build*: `./gradlew assembleDebug` -> **PASS** (Exit code 0, APK build thành công).
  - *Unit Test*: `./gradlew testDebugUnitTest` -> **PASS 100% (74/74 test cases)**:
    + Phase 0 Models & Phase 1 Core tests: 35/35 PASS.
    + Module D (Explore + Search): 13/13 PASS.
    + Module E (Ranking + Detail): 11/11 PASS.
    + Module F (Library): 14/14 PASS.
    + Domain Models: 1/1 PASS.
  - *Deploy*: `adb install -r app-debug.apk` lên giả lập Android `medium_phone` (`emulator-5554`) thành công.
  - *Manual Test (Android Device Hub)*:
    + Màn hình Khám Phá: Feed, Carousel Banner, Đề Cử, Nguồn lọc, Search Bar hoạt động mượt mà.
    + Màn hình BXH: Tab 4 kỳ, badge Top 1/2/3, load dữ liệu API thật từ MotTruyen.
    + Màn hình Chi Tiết Truyện: Thông tin truyện, nút Đọc, nút Theo dõi, danh sách chương mới.
    + Màn hình Danh Sách Chương: Tìm kiếm số chương, đảo thứ tự Cũ/Mới, hiển thị 7391 chương thật.
    + Màn hình Tủ Truyện: Sau khi bấm "Theo dõi" ở Chi tiết, truyện lập tức hiển thị realtime trong tab "Theo dõi" với badge số 1 trên header.
    + Màn hình Tìm Kiếm: Gõ từ khóa tự động debounce 400ms và hiển thị 40 kết quả interleave từ cả 2 nguồn.
    + Màn hình Danh Mục Thể Loại: Lưới 3 cột 20 thể loại canonical.
  - *Bằng chứng screenshots lưu tại*:
    + `screenshots/phase2_explore.png`
    + `screenshots/phase2_ranking.png`
    + `screenshots/phase2_library.png`
    + `screenshots/phase2_detail.png`
    + `screenshots/phase2_chapters.png`
    + `screenshots/phase2_library_bookmarked.png`
    + `screenshots/phase2_search_results.png`
    + `screenshots/phase2_genre_list.png`

---

#### [TASK-010] Triển khai Reader Engine & Background Services (Phase 3: Module G, H)
- **Mục tiêu**: Xây dựng toàn bộ giao diện và logic màn hình Đọc truyện (Reader Engine) và 2 Android Foreground Services (TTS đọc truyện chạy nền và Downloader tải offline hàng loạt) theo SRS §5.4, §5.5, §5.7.
- **Công việc đã làm**:
  - **Module G (Reader Engine & UI)**:
    + `ReaderViewModel.kt`: Quản lý `ReaderUiState`, tự động tải nội dung chương, prefetch buffer ±6 chương vào SQLite Room, tự động lưu tiến độ đọc (Reading Progress) vào `HistoryRepository`, điều hướng chuyển chương, cập nhật cấu hình đọc.
    + `ReaderGestures.kt`: Triển khai cử chỉ chạm 3 vùng ngang (0-25% lùi trang/cuộn lên, 25-75% bật tắt thanh công cụ, 75-100% tiến trang/cuộn xuống).
    + `ReaderTextContent.kt`: Hỗ trợ 2 chế độ đọc: Cuộn liên tục (`SCROLL`) và Lật trang (`PAGE` với `HorizontalPager`), làm sạch HTML, áp dụng font, cỡ chữ, line height, màu nền.
    + `ReaderMenuBottomSheet.kt`: Tùy chỉnh trực tiếp: Slider cỡ chữ (12sp - 36sp), 4 font chữ (Roboto, Serif, Sans-Serif, Monospace), 5 themes màu đọc (Trắng, Sepia/Giấy cũ, Tối, AMOLED, Bảo vệ mắt), chuyển chế độ đọc, chỉnh giãn dòng (1.2x - 2.0x), nút kích hoạt TTS.
    + `ReaderScreen.kt`: Tích hợp Gestures, TextContent, TopBar/BottomBar ẩn hiện mượt mà, Slider cuộn chương, `FLAG_KEEP_SCREEN_ON`.
    + Unit tests: `ReaderViewModelTest.kt` (6/6 tests PASS).
  - **Module H (Services & Background)**:
    + `TtsForegroundService.kt`: Android Foreground Service (`mediaPlayback`) tích hợp `android.speech.tts.TextToSpeech`, phân tách câu mượt mà qua `TtsTextSplitter`, tự động đọc tiếp câu/chương sau, expose `serviceState` StateFlow.
    + `TtsNotificationHelper.kt`: MediaStyle Notification kèm 4 nút điều khiển (Prev, Play/Pause toggle, Next, Stop).
    + `TtsPlayerState.kt`: Mô hình trạng thái phát TTS, chuyển đổi trạng thái, hằng số Actions & Extras.
    + `NovelDownloadService.kt`: Android Foreground Service (`dataSync`) tải offline hàng loạt, mã hóa ảnh bìa Base64 lưu vào SQLite để xem offline 100%, exponential backoff retry 3 lần, cooldown 250ms tránh chặn IP.
    + `DownloadNotificationHelper.kt`: Progress Notification thanh tiến độ %, nút Pause/Resume/Cancel, Completed Notification.
    + Cập nhật `AndroidManifest.xml` đăng ký quyền `FOREGROUND_SERVICE_MEDIA_PLAYBACK`, `FOREGROUND_SERVICE_DATA_SYNC` và 2 Service components.
    + Unit tests: `TtsPlayerStateTest.kt` (6/6 tests PASS), `NovelDownloadServiceTest.kt` (5/5 tests PASS).
  - **Tích hợp Tầng Điều Phối**:
    + Nối `ReaderScreen` thực tế vào `Navigation.kt` (Navigation 3).
- **Kiểm thử & Xác minh đầy đủ (Gate Rule 8 Bước)**:
  - *Build*: `./gradlew assembleDebug` -> **PASS** (Exit code 0, APK hoàn chỉnh).
  - *Unit Test*: `./gradlew testDebugUnitTest` -> **PASS 100% (93/93 test cases, 0 failures)**.
  - *Deploy*: `adb install -r app-debug.apk` lên giả lập Android `medium_phone` (`emulator-5554`) thành công.
  - *Manual Test (Android Device Hub)*:
    + Màn hình Reader: Tải và hiển thị chương 1 "Vũ Luyện Điên Phong" từ API MotTruyen thật, text format căn lề đẹp mắt.
    + Cử chỉ 3 vùng chạm: Chạm vùng giữa lập tức hiện TopBar (Quay lại, Tên chương, TTS, Cài đặt) và BottomBar (Prev, Next, Slider Chương 1/6028, A-, A+, Lật trang, Cài đặt).
    + Reader Menu BottomSheet: Tùy chỉnh cỡ chữ, chọn font, đổi màu theme sang Sepia (Giấy cũ) đổi nền và chữ tức thì.
    + Lịch sử đọc tự động đồng bộ: Sau khi đọc chương 1, quay lại Tủ Truyện -> Tab "Lịch sử" tự động có badge số `1` hiển thị "Đã đọc: Chương 1: Vũ Luyện Điên Phong (1 phút trước)" kèm nút "Đọc tiếp" và nút xóa.
  - *Bằng chứng screenshots lưu tại*:
    + `screenshots/phase3_reader.png`
    + `screenshots/phase3_reader_bars.png`
    + `screenshots/phase3_reader_menu.png`
    + `screenshots/phase3_reader_sepia.png`
    + `screenshots/phase3_history.png`

---

## 5. Danh Sách Vấn Đề Tồn Tại & Cần Giải Quyết Tiếp Theo
1. **Tiến hành Phase 4 (Settings Screen, Backup & Restore, Data Management)**:
   - **Màn hình Cài đặt (`ui/settings/`)**:
     + Cài đặt chung: Tự động tải trước (Prefetch buffer), Dung lượng lưu trữ cache, Xóa bộ nhớ đệm (Clear Cache), Tự động kiểm tra cập nhật.
     + Quản lý nguồn truyện (Bật/tắt hoặc sắp xếp thứ tự ưu tiên nguồn TruyenCV / MotTruyen).
   - **Sao lưu & Phục hồi (Backup & Restore theo SRS §5.8)**:
     + Xuất dữ liệu Tủ truyện (Bookmarks, Reading History, Settings) ra file JSON kèm SHA-256 checksum.
     + Nhập và phục hồi dữ liệu từ file JSON, kiểm tra tính toàn vẹn dữ liệu trước khi khôi phục vào Room DB.
   - **Tối ưu hóa & Đóng gói phát hành (Polishing & Release Build)**:
     + ProGuard/R8 rules.
     + Tối ưu kích thước APK & kiểm thử hiệu năng khởi động.
