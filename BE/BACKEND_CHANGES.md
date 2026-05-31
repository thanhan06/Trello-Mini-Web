# Tóm tắt các thay đổi Backend (từ đầu tới giờ)

Dưới đây là danh sách các chỉnh sửa tôi đã thực hiện trên phần backend để hỗ trợ màn hình `product-order` và API `POST /shop/orders/create-batch`.

## Mục tiêu
- Cho phép frontend gửi payload batch order và nhận về thông báo lỗi rõ ràng khi có sản phẩm thiếu tồn.
- Trả về thông báo lỗi tuỳ chỉnh từ service để hiển thị popup đỏ ở frontend.

## File đã thay đổi

- `src/main/java/com/example/Trello_Mini/common/ApiException.java`
  - Thêm constructor để ném `ApiException` với message tuỳ chỉnh.
  - Snippet đã thêm:

```java
public ApiException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
}
```

- `src/main/java/com/example/Trello_Mini/common/GlobalExceptionHandler.java`
  - Sửa handler của `ApiException` để ưu tiên `ex.getMessage()` nếu có, thay vì luôn dùng `ErrorCode.message()`.
  - Snippet đã thay đổi:

```java
@ExceptionHandler(ApiException.class)
public ResponseEntity<ApiError> handleApi(ApiException ex, HttpServletRequest req) {
    ErrorCode ec = ex.getErrorCode();
    String message = ex.getMessage();
    if (message == null || message.isBlank()) {
        message = ec.message();
    }
    return ResponseEntity.status(ec.status())
            .body(new ApiError(Instant.now(), ec.status().value(), ec.code(), message, req.getRequestURI()));
}
```

- `src/main/java/com/example/Trello_Mini/service/Shop/OrderServiceImpl.java`
  - Khi kiểm tra số lượng yêu cầu vượt quá tồn kho, thay vì ném lỗi chung `ErrorCode.ORDER_ITEM_NOT_ENOUGH`, bổ sung thông báo mô tả tên sản phẩm và lượng tối đa có thể đặt.
  - Snippet đã thay đổi:

```java
if (entry.getValue() > available) {
    String name = product.getProductName() != null ? product.getProductName() : String.valueOf(product.getProductId());
    throw new ApiException(ErrorCode.ORDER_ITEM_NOT_ENOUGH,
            "Số lượng của " + name + " không đủ, hãy nhập số lượng " + available);
}
```

## Lý do và hiệu quả
- Trước kia `ApiException` chỉ chứa `ErrorCode` với thông điệp mặc định; frontend chỉ nhận được thông báo chung (ví dụ: "Số lượng sản phẩm không đủ").
- Sau thay đổi, service có thể cung cấp thông báo cụ thể (ví dụ: "Số lượng của Bút bi không đủ, hãy nhập số lượng 3"), và `GlobalExceptionHandler` sẽ trả JSON với trường `message` chứa chuỗi này. Frontend sẽ lấy `message` và hiển thị popup đỏ.

## Ghi chú quan trọng
- Có thể bạn đã hoàn nguyên (undo) một số thay đổi này; hãy kiểm tra lại nội dung hiện tại của các file nếu cần áp lại.
- Nếu muốn, tôi có thể:
  - Áp lại các thay đổi (patch) nếu file đã bị hoàn nguyên.
  - Mở rộng kiểm tra đồng thời (pessimistic lock / cập nhật tồn kho) để tránh race-condition khi nhiều yêu cầu đặt hàng.

---
Tôi đã lưu file: `BE/BACKEND_CHANGES.md`.
Bạn muốn tôi tiếp tục: (1) áp lại các sửa đổi nếu bị revert, (2) thêm test unit/integration cho luồng này, hay (3) build và chạy server để kiểm thử trực tiếp?

## Những điều cần làm để có API cho `product-order` (chi tiết bước-by-bước)

Dưới đây là danh sách các việc cần làm trên backend (file cụ thể, đoạn mã mẫu, và giải thích ngắn) để đảm bảo API `POST /shop/orders/create-batch` hoạt động an toàn và trả lỗi có ý nghĩa cho frontend `product-order.html`.

1) Xác nhận / chỉnh DTO (server-side validation)
  - File: `src/main/java/com/example/Trello_Mini/dto/request/Shop/OrderBatchCreationRequest.java`
  - Yêu cầu: các trường phải khớp với payload frontend gửi:
    - `customName` (String) - `@NotBlank`
    - `orderDeliveryAddress` (String) - `@NotBlank`
    - `orderDeliveryDate` (LocalDate) - `@JsonFormat(pattern = "yyyy/MM/dd")` + `@NotNull`
    - `items` (List<OrderBatchItemRequest>) - `@NotEmpty` + `@Valid`
    - `actorUsername` (String) - optional
  - Item DTO: `orderProductId` (Long) và `orderProductAmount` (Integer, `@Min(1)`, `@NotNull`).

  Snippet (hiện tại dự án đã có; kiểm tra lại nếu cần):
```java
@NotBlank String customName;
@NotBlank String orderDeliveryAddress;
@NotNull @JsonFormat(pattern = "yyyy/MM/dd") LocalDate orderDeliveryDate;
@NotEmpty @Valid List<OrderBatchItemRequest> items;
String actorUsername;
```

2) Controller mapping (đã tồn tại nhưng kiểm tra)
  - File: `src/main/java/com/example/Trello_Mini/controller/Shop/OrderController.java`
  - Phương thức cần nhận JSON và trả `ApiResponse<List<OrderResponse>>`.
  - Signature mẫu (dự án có sẵn):
```java
@PostMapping("/create-batch")
public ResponseEntity<ApiResponse<List<OrderResponse>>> createBatch(
        @Valid @RequestBody OrderBatchCreationRequest request, HttpServletRequest httpReq) {
    return ApiResponses.created(httpReq, orderService.createBatch(request));
}
```

3) Service (`createBatch`) — triển khai giao dịch và kiểm tra tồn kho
  - File: `src/main/java/com/example/Trello_Mini/service/Shop/OrderServiceImpl.java`
  - Yêu cầu chính:
    a. Làm gộp số lượng theo `orderProductId` (nếu frontend gửi trùng id nhiều lần).
    b. Tải tất cả sản phẩm cần kiểm tra bằng `productRepository.findAllById(...)`.
    c. Với từng sản phẩm: tính `available = product.getProductAmount() - product.getOrderProductAmount()` (hoặc sử dụng trường tương ứng trong DB).
    d. Nếu `requested > available` → ném `ApiException` với **thông điệp cụ thể** (ví dụ: "Số lượng của Bút bi không đủ, hãy nhập số lượng 3").
    e. Nếu đủ: tạo các `TrProductOrderEntity` tương ứng và lưu (dùng `saveAll` hoặc `save` trong vòng lặp) trong transaction.
    f. (Tùy chọn/khuyến nghị) Giữ kho an toàn khi đồng thời: sử dụng lock (PESSIMISTIC_WRITE) khi lấy `MstProductEntity` hoặc cập nhật `productAmount` ngay trong transaction.

  - Ví dụ throw lỗi có thông điệp (mẫu):
```java
if (requested > available) {
    String name = product.getProductName() != null ? product.getProductName() : String.valueOf(product.getProductId());
    throw new ApiException(ErrorCode.ORDER_ITEM_NOT_ENOUGH,
            "Số lượng của " + name + " không đủ, hãy nhập số lượng " + available);
}
```

4) Repository: lock / helper methods
  - File(s): `MstProductRepository`, `TrProductOrderRepository`
  - Thêm (nếu cần) method để load product với lock:
```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select p from MstProductEntity p where p.productId = :id")
Optional<MstProductEntity> findByIdForUpdate(@Param("id") Long id);
```
  - Hoặc, nếu bạn cần một `orderId` nhóm (nếu schema dùng orderId chung), thêm helper:
```java
@Query("SELECT COALESCE(MAX(t.orderId), 0) FROM TrProductOrderEntity t")
Long findMaxOrderId();
```

5) Exception và Global handler
  - File: `src/main/java/com/example/Trello_Mini/common/ApiException.java`
  - Để service có thể truyền thông điệp tuỳ chỉnh, thêm constructor:
```java
public ApiException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
}
```
  - File: `src/main/java/com/example/Trello_Mini/common/GlobalExceptionHandler.java`
  - Sửa `handleApi` để ưu tiên `ex.getMessage()` khi trả JSON, ví dụ:
```java
String message = ex.getMessage();
if (message == null || message.isBlank()) message = ec.message();
return ResponseEntity.status(ec.status())
    .body(new ApiError(..., message, req.getRequestURI()));
```

6) Response format cho frontend
  - Frontend `product-order.html` mong đợi JSON lỗi với trường `message`. GlobalExceptionHandler hiện trả `ApiError` gồm `message` — đảm bảo trường đó chứa thông báo tiếng Việt ngắn gọn.

7) Validation bổ sung (server-side)
  - Kiểm tra `orderDeliveryDate >= today` (nếu business rule yêu cầu). Nếu invalid, ném `new ApiException(ErrorCode.COMMON_VALIDATION_FAILED, "Ngày giao hàng không hợp lệ")` hoặc cấu hình annotation/custom validator.
  - Kiểm tra `customName`/`orderDeliveryAddress` độ dài tối đa nếu cần.

8) Tests
  - Viết integration test (MockMvc) gửi payload batch:
    - Case thành công → 201 + danh sách `OrderResponse`.
    - Case thiếu tồn → 409 (ErrorCode.ORDER_ITEM_NOT_ENOUGH.status) và JSON `message` chứa thông tin sản phẩm và số lượng tối đa.

9) Build & Run (kiểm thử thủ công)
  - Build và chạy (BE folder):
```bash
./mvnw clean package
./mvnw spring-boot:run
```
  - Gửi request test với curl / Postman:
```bash
curl -X POST http://localhost:8080/shop/orders/create-batch \
  -H "Content-Type: application/json" \
  -d '{"customName":"Khách A","orderDeliveryAddress":"Hanoi","orderDeliveryDate":"2026/05/31","actorUsername":"admin","items":[{"orderProductId":1,"orderProductAmount":5}] }'
```

10) Optional: cải thiện nghiệp vụ kho
  - Nếu yêu cầu là giảm `productAmount` tại thời điểm đặt hàng, hãy cập nhật `MstProductEntity.productAmount = productAmount - requested` trong cùng transaction, và lưu `productRepository.save(product)` sau khi kiểm tra.
  - Đảm bảo locking để tránh race-condition (PESSIMISTIC_WRITE) hoặc áp dụng optimistic versioning nếu DB schema hỗ trợ.

---
Tóm tắt nhanh (Checklist)
- [ ] Kiểm tra/hoàn thiện DTO (`OrderBatchCreationRequest`, `OrderBatchItemRequest`).
- [ ] Đảm bảo `OrderController.createBatch(...)` nhận `@Valid @RequestBody` và trả `ApiResponse<List<OrderResponse>>`.
- [ ] Implement `OrderService.createBatch(...)` transactional: aggregate quantities, check stock, lock rows nếu cần, save orders.
- [ ] Thêm/kiểm tra repository helper (lock query, findMaxOrderId nếu schema cần).
- [ ] Thêm `ApiException(ErrorCode, String)` và sửa `GlobalExceptionHandler` để ưu tiên `ex.getMessage()`.
- [ ] Viết integration tests cho flow batch order.
- [ ] Build và test end-to-end từ `product-order.html`.

Nếu bạn đồng ý, tôi sẽ áp lại 3 thay đổi code mẫu ngay (ApiException overload, GlobalExceptionHandler update, và throw lỗi chi tiết trong `OrderServiceImpl`) và chạy build. Hãy trả lời "Áp lại" nếu muốn tôi làm việc đó bây giờ.