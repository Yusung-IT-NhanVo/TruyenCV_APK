# CODEGRAPH RULES FOR ANDROID KOTLIN PROJECT

## 1. ƯU TIÊN SỬ DỤNG CODEGRAPH TRƯỚC TIÊN (GRAPH-FIRST)
- **Truy vấn Graph trước khi đọc file/grep:** Khi cần tìm kiếm định nghĩa class, hàm, biến hoặc phân tích luồng gọi (call graph / dependency tree), BẮT BUỘC phải sử dụng các công cụ MCP của CodeGraph trước (`codegraph_explore` hoặc CLI `codegraph explore`). Chỉ dùng `grep`, tìm kiếm văn bản thuần túy, hoặc đọc toàn bộ file khi CodeGraph không trả về kết quả hoặc cần xem chi tiết logic bên trong một khối code đã xác định.
- **Khoanh vùng tác động trước khi sửa code:** Trước khi chỉnh sửa bất kỳ `Repository`, `UseCase`, `ViewModel` hoặc hàm `@Composable` nào, luôn dùng CodeGraph để kiểm tra danh sách các thành phần đang gọi (callers) và phụ thuộc (dependents) nhằm tránh gây lỗi dây chuyền.

## 2. XỬ LÝ ĐIỂM MÙ DEPENDENCY INJECTION (HILT / DAGGER / KOIN)
- Do CodeGraph phân tích cú pháp tĩnh (AST), đồ thị gọi hàm có thể bị ngắt quãng giữa `Interface` và class thực thi (`Implementation`) khi dự án sử dụng Dependency Injection.
- **Quy tắc truy vết Implementation:** Khi tra cứu một `Interface` (ví dụ: `AuthRepository`) mà CodeGraph chỉ dừng lại ở mức khai báo interface và không tự nối được tới class triển khai (`AuthRepositoryImpl`), Agent BẮT BUỘC phải:
  1. Tìm kiếm các module cấu hình DI chứa annotation `@Binds` hoặc `@Provides` (đối với Hilt/Dagger) hoặc các khai báo `singleOf`, `factoryOf`, `bind` (đối với Koin) có tham chiếu tới `Interface` đó.
  2. Đọc file DI module tìm được để xác định chính xác tên class triển khai, sau đó quay lại dùng CodeGraph để tiếp tục phân tích class thực thi đó.

## 3. KIỂM TRA CÁC LIÊN KẾT NGOÀI PHẠM VI KOTLIN AST
- CodeGraph lập chỉ mục tốt nhất cho mã nguồn Kotlin/Java. Với các lỗi hoặc thay đổi liên quan đến cấu hình ứng dụng, tài nguyên UI hoặc build system, Agent cần kiểm tra bổ sung:
  - `AndroidManifest.xml` (khai báo Activity, Service, Permission).
  - Thư mục `res/` (XML layouts, navigation graphs, strings, themes).
  - Các file cấu hình Gradle (`build.gradle.kts`, `settings.gradle.kts`, `libs.versions.toml`).
