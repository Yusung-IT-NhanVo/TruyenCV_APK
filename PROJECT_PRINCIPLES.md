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

## 6. Quy Tắc Bắt Buộc Về Git & GitHub (Git & GitHub Automation Discipline)

- **Cam kết hoàn tất Task**: Khi bất kỳ một Task nào được xác nhận hoàn tất (Code xong + Kiểm tra thành công), **bắt buộc** phải ghi nhận vào Git và đẩy mã nguồn lên GitHub tương ứng.
- **Quy trình chuẩn**:
  1. Ghi chép cập nhật công việc vào tài liệu trí nhớ `PROGRESS_LOG.md`.
  2. Gom thay đổi: `git add .`
  3. Tạo commit nguyên tử (Atomic Conventional Commit): `git commit -m "<type>(<scope>): <mô tả rõ ràng>"`
  4. Đẩy lên nhánh tương ứng: `git push origin <branch>`
- **Đường dẫn Repository**: Đường dẫn GitHub Repository chính thức phải được cấu hình và ghi nhận rõ trong tài liệu kỹ thuật (`PROGRESS_LOG.md` và `API_INTEGRATION_SPEC.md`) để AI Agent và các công cụ tự động có thể tự đọc và làm việc lại.

---

## 7. Mẫu Báo Cáo Định Kỳ Sau Mỗi Giai Đoạn (Progress Report Template)

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
