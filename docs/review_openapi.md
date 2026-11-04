# Review Hợp đồng OpenAPI - catalog-service (TH2-1)

## 1. Bảng kiểm tra phương thức HTTP & Mã phản hồi
| Thao tác | Mã thành công | Mã lỗi đã khai báo | An toàn? | Idempotent? |
| :--- | :--- | :--- | :--- | :--- |
| `GET /api/v1/books` | 200 | 400 | có | có |
| `POST /api/v1/books` | 201 + Location + ETag | 400, 409 | không | không |
| `GET /api/v1/books/{isbn}` | 200 + ETag / 304 | 404 | có | có |
| `PUT /api/v1/books/{isbn}` | 200 + ETag mới | 400, 404, 412, 428 | không | có |
| `DELETE /api/v1/books/{isbn}` | 204 | 404 | không | có |

---

## 2. Trả lời chi tiết 4 câu hỏi trong hợp đồng

### Câu 1: Mã trạng thái đã đúng ngữ nghĩa RFC 9110 chưa?
Đã đúng ngữ nghĩa RFC 9110:
- **201 Created**: Trả về khi tạo thành công tài nguyên mới kèm vị trí (`Location`) và `ETag`.
- **204 No Content**: Trả về khi xóa thành công và không cần thân phản hồi (body).
- **304 Not Modified**: Trả về khi ETag phía Client gửi lên trùng với ETag hiện tại của Server.
- **412 Precondition Failed**: Trả về khi ETag trong header `If-Match` bị lệch (dữ liệu đã thay đổi).
- **428 Precondition Required**: Trả về khi thực hiện cập nhật (PUT) mà thiếu header `If-Match`.

### Câu 2: Ràng buộc Isbn13 và Money có khớp XSD ở TH1 không?
- **Isbn13**: Chuỗi `^97[89][0-9]{10}$` đảm bảo chuẩn 13 chữ số, khớp hoàn toàn với ràng buộc XSD.
- **Money**: Khai báo `minimum: 0` và `multipleOf: 0.01` đảm bảo số tiền không âm và có 2 chữ số thập phân, tương thích hoàn toàn với kiểu dữ liệu tiền tệ trong XSD.

### Câu 3: Thao tác nào an toàn / idempotent? Vì sao PUT cần If-Match?
- **GET**: An toàn & Idempotent (chỉ đọc dữ liệu, gọi nhiều lần kết quả không đổi).
- **POST**: Không an toàn & Không idempotent (mỗi lần gọi tạo ra 1 bản ghi mới).
- **PUT / DELETE**: Không an toàn (có chỉnh sửa dữ liệu), nhưng Idempotent (gọi nhiều lần với cùng tham số cho ra cùng kết quả).
- **Lý do PUT cần If-Match**: Để áp dụng Khóa lạc quan (Optimistic Locking). Header `If-Match` chứa ETag phiên bản cũ, giúp phát hiện nếu có client khác vừa sửa dữ liệu trước đó, tránh lỗi ghi đè dữ liệu bất hợp lệ (Lost Update).

### Câu 4: Thêm trường bắt buộc mới vào BookCreateRequest có phá vỡ tương thích ngược không?
- **Có phá vỡ tương thích ngược (Breaking Change)**.
- **Giải thích**: Các Client phiên bản cũ chưa cập nhật sẽ không gửi trường bắt buộc mới này, dẫn đến Server từ chối request và trả về lỗi `400 Bad Request` (Validation Error).
