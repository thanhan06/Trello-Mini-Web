# Responsive notes cho trang product-list

## Mục tiêu đã thực hiện
- Giữ toàn bộ nội dung trang nằm trong 1 màn hình (viewport), không xuất hiện cuộn dọc toàn trang.
- Footer luôn hiển thị ở đáy màn hình.
- Bố cục không bị vỡ khi thay đổi kích thước màn hình.

## Các chỉnh sửa đã làm trong `listproduct.html`
1. Khóa chiều cao viewport và tắt cuộn toàn trang
- Thêm `html, body { height: 100%; }`.
- Thêm `body { margin: 0; overflow: hidden; }`.

2. Chuyển khối nội dung chính sang layout flex dọc
- Thêm class `.page-content` với:
	- `height: calc(100vh - 44px);`
	- `display: flex;`
	- `flex-direction: column;`
	- `overflow: hidden;`
- Gán class này cho container chính để chia layout theo chiều dọc ổn định.

3. Cố định vùng bảng để không làm tràn màn hình
- Bọc bảng vào `.table-fixed-wrap` với:
	- `flex: 1 1 auto;`
	- `min-height: 0;`
	- `overflow: hidden;`
- Đặt `.table-fixed-wrap .table { margin-bottom: 0 !important; }` để tránh phát sinh khoảng dư gây cuộn.

4. Neo footer xuống cuối màn hình
- Chỉnh `.order-footer` dùng `margin-top: auto;` để tự đẩy footer xuống cuối trong layout flex.
- Bổ sung `padding-bottom: 8px;` để footer thoáng hơn và không dính sát mép dưới.

## Kết quả
- Trang product-list hiển thị gọn trong khung nhìn.
- Footer luôn xuất hiện ở phần đáy màn hình.
- Không còn thanh cuộn dọc ở mức trang.

## Ghi chú thêm
- Nếu màn hình quá thấp (đặc biệt mobile ngang), nội dung có thể bị chật. Khi đó nên bổ sung media query để giảm kích thước chữ, giảm padding và rút chiều cao các thành phần để giữ trải nghiệm tốt hơn.
