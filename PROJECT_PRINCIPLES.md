# NGUYÊN TẮC CỐT LÕI DỰ ÁN (PROJECT CORE PRINCIPLES)

Tài liệu này định nghĩa bộ quy tắc kỷ luật bắt buộc phải tuân thủ trong toàn bộ quá trình lập kế hoạch, phát triển, sửa lỗi và vận hành dự án. Tất cả thành viên tham gia phát triển (con người và AI Agent) phải tuyệt đối tuân theo.

---

## 1. Không làm vượt bước (No Step Skipping)

- **Định vị giai đoạn**: Luôn xác định rõ ràng dự án hiện đang ở giai đoạn nào trong quy trình phát triển.
- **Chứng thực trước khi tiến tới**: Không mặc định rằng các bước trước đó đã hoàn thành nếu chưa có bằng chứng xác thực (logs, test results, output thực tế) hoặc sự phê duyệt từ người dùng.
- **Tuân thủ quy trình tuần tự**:
  $$\text{Hiểu Đúng} \longrightarrow \text{Thiết Kế Đúng} \longrightarrow \text{Làm Từng Phần} \longrightarrow \text{Kiểm Tra} \longrightarrow \text{Triển Khai} \longrightarrow \text{Bảo Trì}$$

---

## 2. Không tự ý quyết định thay người dùng (No Unauthorized Decisions)

Bắt buộc phải trao đổi, giải thích rõ rủi ro và xin ý kiến xác nhận từ người dùng trước khi thực hiện các hành động:

- Thay đổi cấu trúc hoặc kiến trúc hệ thống quan trọng.
- Xóa, dọn dẹp hoặc ghi đè dữ liệu, tệp tin hiện hữu.
- Thay đổi cấu trúc cơ sở dữ liệu (migration, alter schema, drop table/column) có nguy cơ gây mất hoặc lỗi dữ liệu.
- Kích hoạt, cài đặt hoặc sử dụng các dịch vụ bên ngoài có khả năng phát sinh chi phí.
- Triển khai (deploy) sản phẩm hoặc mã nguồn lên môi trường Production.
- Thực hiện bất kỳ thay đổi lớn nào có thể gây gián đoạn hoạt động của hệ thống.

---

## 3. Không sửa code một cách mù quáng (No Blind Coding / Patching)

Tuyệt đối không vá lỗi dựa trên phỏng đoán triệu chứng bề nổi. Mọi thao tác sửa mã nguồn phải tuân thủ quy trình 6 bước:

1. **Đọc & thấu hiểu**: Khảo sát và đọc kỹ toàn bộ đoạn mã nguồn liên quan cùng các luồng gọi (call-sites).
2. **Xác định nguyên nhân gốc rễ (Root Cause)**: Tìm đúng điểm gây lỗi cốt lõi, không chỉ dập tắt lỗi bề mặt.
3. **Đánh giá phạm vi ảnh hưởng (Impact Analysis)**: Đo lường xem thay đổi có tác động phụ (side-effects) đến các module khác hay không.
4. **Đề xuất giải pháp**: Trình bày phương án giải quyết tối ưu nhất trước khi thao tác lớn.
5. **Thực hiện thay đổi nhỏ nhất hợp lý (Minimal Working Diff)**: Ưu tiên can thiệp gọn gàng, súc tích, tránh tái cấu trúc lan man không cần thiết.
6. **Kiểm thử lại**: Xác minh lại tính năng vừa sửa và đảm bảo không phá vỡ các chức năng cũ (regression test).

---

## 4. Không tuyên bố hoàn thành khi chưa kiểm chứng (No Unverified Completion)

Phân biệt rạch ròi các trạng thái công việc, không đánh đồng hay kết luận vội vàng:

- **Đã viết code**: Chỉ mới hoàn thành việc gõ mã nguồn.
- **Đã kiểm tra cú pháp**: Mã nguồn không có lỗi biên dịch/linter cơ bản.
- **Đã chạy thử**: Đã chạy thực thi trong môi trường chạy thử.
- **Đã kiểm thử thành công**: Đã xác nhận kết quả đầu ra khớp với yêu cầu mong muốn bằng bằng chứng rõ ràng.
- **Đã triển khai**: Đã đưa vào môi trường hoạt động thực tế.
- **Đã được người dùng nghiệm thu**: Người dùng trực tiếp kiểm tra và đồng ý hoàn tất.

> **Quy định bắt buộc**: Nếu chưa thể chạy thử hoặc thiếu công cụ kiểm thử tại môi trường hiện tại, phải nói rõ và minh bạch cho người dùng biết là *chưa thể xác minh* thay vì khẳng định "đã hoàn thành".

---

## 5. Không lạm dụng công nghệ (No Over-Engineering)

Mọi giải pháp thiết kế và công nghệ áp dụng vào dự án phải phục vụ mục tiêu thực tế:

- **Ưu tiên sự đơn giản**: Code tốt nhất là phần code không cần phải viết. Đơn giản để ai cũng có thể đọc hiểu và bảo trì.
- **Chi phí hợp lý**: Tiết kiệm tài nguyên xử lý, bộ nhớ và chi phí duy trì.
- **Dễ sử dụng & Dễ bảo trì**: Giảm thiểu sự phức tạp không cần thiết; tránh tạo ra các lớp trừu tượng (abstractions) khi chưa có nhu cầu mở rộng thực sự.
- **Phù hợp nhu cầu thực tế**: Tập trung giải quyết bài toán cốt lõi của người dùng trước mắt.
- **Khả năng mở rộng vừa đủ**: Kiến trúc rõ ràng để sẵn sàng mở rộng khi cần, nhưng không tạo sẵn boilerplate "để dành cho tương lai".

> **Nguyên tắc vàng**: Tuyệt đối không xây dựng một hệ thống quá đồ sộ, phức tạp chỉ để phục vụ cho một tính năng nhỏ.

---

## 6. Bắt Buộc Kiểm Thử Từng Task Trước Khi Báo Cáo & Commit (Mandatory Per-Task Testing & Gate Rule)

### 6.1. Nguyên tắc cốt lõi

- **Mỗi task phải có test riêng**: Mọi tính năng mới, hàm xử lý logic, API adapter, data mapper, parser, formatter, download helper, state management, ViewModel... **bắt buộc phải có unit test đi kèm** trước khi được coi là hoàn thành.
- **Test framework**: Dùng test runner phù hợp với stack hiện tại (JUnit 5 / Kotlin Test cho Android, Turbine cho Flow, MockK cho mocking).
- **Test phải có ý nghĩa**: Mỗi test phải kiểm tra behavior thực tế (input → expected output), không phải chỉ kiểm tra code chạy không throw exception.

### 6.2. Vòng lặp bắt buộc: Code → Test → Fix → Pass 100% (Self-Healing Loop)

```
┌─────────────────────────────────────────────────────────────────┐
│  1. Viết code cho task                                          │
│  2. Viết unit test tương ứng cho task đó                        │
│  3. Chạy test: ./gradlew testDebugUnitTest                      │
│  4. Nếu FAIL → đọc log → sửa code/test → quay lại bước 3       │
│  5. Lặp cho đến khi 100% test PASS                              │
│  6. Chạy kiểm tra thủ công (manual test) trên emulator/device   │
│  7. Nếu manual test FAIL → sửa code → quay lại bước 3           │
│  8. Lặp cho đến khi manual test PASS                             │
│  9. CHỈ KHI CẢ UNIT TEST + MANUAL TEST ĐỀU PASS →              │
│     mới được báo cáo kết quả và commit vào Git/GitHub            │
└─────────────────────────────────────────────────────────────────┘
```

### 6.3. Các hành vi NGHIÊM CẤM

| ❌ Hành vi bị cấm | Lý do |
|---|---|
| Bypass, skip (`@Ignore`, `@Disabled`) bài test đang fail | Giấu lỗi, không giải quyết root cause |
| Comment out assertion để test pass giả | Vô nghĩa hóa kiểm thử |
| Giảm bớt assertion hoặc hạ thấp tiêu chí pass | Hạ thấp chất lượng |
| Báo cáo "đã hoàn thành" khi test đang fail | Vi phạm nguyên tắc §4 (No Unverified Completion) |
| Commit code lên Git/GitHub khi test chưa pass hoàn toàn | Vi phạm nguyên tắc §7 (Git Discipline) |
| Chuyển sang task tiếp theo khi task hiện tại chưa pass test | Vi phạm nguyên tắc §1 (No Step Skipping) |

### 6.4. Quy trình Gate trước mỗi commit (6 bước bắt buộc)

| Bước | Lệnh / Hành động | Tiêu chí PASS | Nếu FAIL |
|------|-------------------|---------------|----------|
| **1. Build** | `./gradlew assembleDebug` | Exit code 0 | Sửa → chạy lại |
| **2. Lint** | `./gradlew lintDebug` | Không error | Sửa → chạy lại |
| **3. Unit Test** | `./gradlew testDebugUnitTest` | 100% pass, 0 failures, 0 skipped | Sửa code/test → chạy lại, KHÔNG ĐƯỢC skip |
| **4. Manual Test** | Chạy app trên emulator/device **qua Android Device Hub** (xem §6.5) | Tính năng task vừa làm hoạt động đúng đặc tả, có screenshot/layout làm bằng chứng | Sửa → quay lại bước 1 |
| **5. Ghi nhật ký** | Cập nhật `PROGRESS_LOG.md` | Ghi rõ: task đã làm, test đã chạy, kết quả, bằng chứng | — |
| **6. Commit + Push** | `git add . && git commit && git push` | Atomic conventional commit | — |

> **Quy tắc tuyệt đối**: Nếu bất kỳ bước nào từ 1-4 FAIL, **CẤM chuyển sang bước tiếp theo**. Phải sửa và lặp lại cho đến khi PASS rồi mới được tiến tiếp. Không có ngoại lệ.

### 6.5. Kiểm Thử Thủ Công Qua Android Device Hub (Mandatory Manual Testing)

Mọi kiểm tra thủ công (Manual Test) **bắt buộc** thực hiện thông qua hệ thống **Android Device Hub** (Antigravity plugin `android-cli`). Quy trình như sau:

#### Bước 1: Chuẩn bị emulator/device
```bash
# Liệt kê thiết bị hiện có
android emulator list

# Nếu chưa có, tạo emulator mới
android emulator create

# Khởi chạy emulator
android emulator start <avd-id>

# Chờ boot hoàn tất
adb -s <serial> wait-for-device shell 'while [ -z "$(getprop sys.boot_completed)" ]; do sleep 1; done'
```

#### Bước 2: Build & Deploy APK lên emulator
```bash
# Build APK debug
./gradlew assembleDebug

# Cài đặt lên emulator (android install nhanh hơn adb install)
android install --apks=app/build/outputs/apk/debug/app-debug.apk --device <serial>

# Hoặc build + deploy + chạy 1 lệnh
android run --device <serial>
```

#### Bước 3: Kiểm tra UI bằng lệnh inspect
```bash
# Đọc cấu trúc UI (text-based, nhanh)
android layout --device <serial> --pretty

# Chụp screenshot có đánh số phần tử
android screen capture --device <serial> --annotate --output <screenshot_path>.png

# Chụp screenshot thường (không annotate)
android screen capture --device <serial> --output <screenshot_path>.png
```

#### Bước 4: Thao tác trên device nếu cần
```bash
# Tap vào phần tử #N từ annotated screenshot
adb -s <serial> shell $(android screen resolve --screenshot <screenshot_path>.png --string "input tap #7")

# Nhập text
adb -s <serial> shell input text '<text>'

# Nhấn nút Back
adb -s <serial> shell input keyevent KEYCODE_BACK

# Vuốt (swipe)
adb -s <serial> shell input swipe x1 y1 x2 y2
```

#### Bước 5: Thu thập bằng chứng
Sau khi kiểm tra, **bắt buộc lưu bằng chứng** gồm:
- **Screenshot** chức năng hoạt động đúng (file `.png`)
- **Layout dump** xác nhận UI elements hiển thị đúng (output `android layout`)
- Ghi nhận vào `PROGRESS_LOG.md`: tên task, lệnh đã chạy, kết quả, đường dẫn screenshot

> **Lưu ý**: Người dùng có thể mở panel **Android Device Hub** trong Antigravity IDE để xem mirror live màn hình emulator/device và tương tác trực tiếp (chạm, gõ, vuốt). Agent sử dụng `android layout` / `android screen` / `adb shell input` để kiểm tra tự động.

---

## 7. Quy Tắc Bắt Buộc Về Git & GitHub (Git & GitHub Automation Discipline)

- **Cam kết hoàn tất Task**: Khi bất kỳ một Task nào được xác nhận hoàn tất (Code xong + Kiểm tra thành công), **bắt buộc** phải ghi nhận vào Git và đẩy mã nguồn lên GitHub tương ứng.
- **Quy trình chuẩn**:
  1. Ghi chép cập nhật công việc vào tài liệu trí nhớ `PROGRESS_LOG.md`.
  2. Gom thay đổi: `git add .`
  3. Tạo commit nguyên tử (Atomic Conventional Commit): `git commit -m "<type>(<scope>): <mô tả rõ ràng>"`
  4. Đẩy lên nhánh tương ứng: `git push origin <branch>`
- **Đường dẫn Repository**: Đường dẫn GitHub Repository chính thức phải được cấu hình và ghi nhận rõ trong tài liệu kỹ thuật (`PROGRESS_LOG.md` và `API_INTEGRATION_SPEC.md`) để AI Agent và các công cụ tự động có thể tự đọc và làm việc lại.

---

## 8. Mẫu Báo Cáo Định Kỳ Sau Mỗi Giai Đoạn (Progress Report Template)

Khi kết thúc một giai đoạn công việc, báo cáo theo mẫu chuẩn:

```markdown
GIAI ĐOẠN: [Tên giai đoạn / Tính năng]

1. Mục tiêu: [Cần đạt được điều gì?]
2. Đã thực hiện: [Những công việc cụ thể đã hoàn thành]
3. Kết quả kiểm tra: [Những gì đã được xác minh thực tế và bằng chứng/cách thức]
4. Vấn đề còn tồn tại: [Lỗi, rủi ro, dữ liệu thiếu hoặc điểm chưa chắc chắn]
5. Điều kiện chuyển bước: [Yêu cầu cần đạt được trước khi sang bước kế tiếp]
6. Bước tiếp theo: [Đề xuất công việc cụ thể tiếp theo]
7. Trạng thái: CHƯA BẮT ĐẦU / ĐANG THỰC HIỆN / CHỜ XÁC NHẬN / ĐÃ HOÀN THÀNH
```
