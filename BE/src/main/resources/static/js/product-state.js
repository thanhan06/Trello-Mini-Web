// ============================================================================
// 1. XỬ LÝ KHI NGƯỜI DÙNG RELOAD TRANG (BẤM F5 HOẶC TẢI LẠI)
// ============================================================================
// Kiểm tra API Performance của trình duyệt để biết nguyên nhân tải trang
if (window.performance) {
  // Lấy danh sách các sự kiện điều hướng trang (cách trang được load)
  const navEntries = window.performance.getEntriesByType("navigation");
  // Nếu trang được load qua hành động "reload" (người dùng ấn F5 hoặc Ctrl+R)
  if (navEntries.length > 0 && navEntries[0].type === "reload") {
    // Xóa toàn bộ dữ liệu giỏ hàng lưu tạm trong phiên làm việc hiện tại
    sessionStorage.removeItem('cartQty');
    // Nếu URL có chứa các tiêu chí tìm kiếm (query parameters như ?name=...&page=...)
    if (window.location.search !== "") {
      // Chuyển hướng người dùng về lại trang danh sách gốc (URL không chứa parameter)
      // Việc này giúp xóa trắng các ô điều kiện tìm kiếm.
      window.location.href = window.location.pathname;
    }
  }
}

// ============================================================================
// 2. KHÔI PHỤC DỮ LIỆU KHI TRANG VỪA TẢI XONG (Bao gồm chuyển trang, back/forward)
// ============================================================================
document.addEventListener('DOMContentLoaded', function () {
  
  // --- A.1 KHÔI PHỤC GIÁ TRỊ TÌM KIẾM TỪ URL QUERRY (CHO JS XỬ LÝ CHÍNH XÁC 100%) ---
  const urlParams = new URLSearchParams(window.location.search);
  
  const qNameInput = document.getElementById('qName');
  if (qNameInput && urlParams.has('name')) {
    qNameInput.value = urlParams.get('name');
  }
  
  const qDescInput = document.getElementById('qDesc');
  if (qDescInput && urlParams.has('desc')) {
    qDescInput.value = urlParams.get('desc');
  }

  const qTypeHidden = document.getElementById('qTypeHidden');
  if (qTypeHidden && urlParams.has('type')) {
    qTypeHidden.value = urlParams.get('type');
  }

  // --- A.2 XỬ LÝ SỰ KIỆN VÀ HIỂN THỊ DROPDOWN LOẠI SẢN PHẨM ---
  const qTypeLabel = document.getElementById('qTypeLabel');   // Chữ hiển thị trên dropdown
  const drpItems = document.querySelectorAll('#qTypeDropdown + .dropdown-menu .dropdown-item'); // Các item trong menu sổ xuống

  let initialMatched = false; // Biến cờ kiểm tra xem type đang tìm kiếm có trùng mục nào trong danh sách không
  drpItems.forEach(item => {
    // Khi mới load, nếu value của item nào trong menu trùng với giá trị đang lưu trong hidden input
    if (qTypeHidden && item.getAttribute('data-value') === qTypeHidden.value) {
       // Cập nhật nhãn hiển thị thành tên loại sản phẩm (nếu nội dung là trắng \xa0 thì hiển thị chuỗi rỗng)
       qTypeLabel.textContent = item.textContent === '\xa0' ? '' : item.textContent;
       initialMatched = true; // Đánh dấu là đã tìm thấy
    }
    
    // Lắng nghe sự kiện click trên từng item của menu dropdown
    item.addEventListener('click', function(e) {
      e.preventDefault(); // Ngăn hành vi cuộn mặc định của thẻ <a>
      const val = this.getAttribute('data-value'); // Lấy giá trị ID của Loại SP
      if (qTypeHidden) qTypeHidden.value = val; // Gán ID đó vào thẻ input ẩn để chuẩn bị submit form
      // Thay đổi chữ trên nút bấm cho khớp với Loại bạn vừa chọn
      qTypeLabel.textContent = this.textContent === '\xa0' ? '' : this.textContent;
    });
  });
  // Nếu không có bất kì value nào khớp (hoặc searchType rỗng), để trắng nhãn Dropdown
  if(!initialMatched && qTypeLabel) qTypeLabel.textContent = ''; 

  // --- A.3 ĐỒNG BỘ PHÂN TRANG (CHUYỂN TRANG KHÔNG MẤT INPUT ĐANG NHẬP) ---
  const pagBtns = document.querySelectorAll('.pag-btn');
  const searchForm = document.querySelector('form.search-bar');
  const pageInput = document.querySelector('input[name="page"]');

  pagBtns.forEach(btn => {
    btn.addEventListener('click', function(e) {
      if (this.classList.contains('disabled')) return;
      e.preventDefault(); // Chặn hành vi chuyển link gốc

      const href = this.getAttribute('href');
      const urlMatch = href.match(/[?&]page=(\d+)/); // Rút trích số trang muốn tới

      if (urlMatch && searchForm && pageInput) {
         pageInput.value = urlMatch[1]; // Sửa parameter page ẩn trong Form
         searchForm.submit(); // Submit toàn bộ Form hiện tại bao gồm các Input (tên, loại, mô tả)
      } else {
         window.location.href = href; // Phương án dự phòng an toàn
      }
    });
  });

  // --- B. KHÔI PHỤC DỮ LIỆU CÁC Ô NHẬP SỐ LƯỢNG SẢN PHẨM ---
  // Lấy chuỗi JSON giỏ hàng từ Session. Nếu không có thì fallback về chuỗi gán object rỗng '{}'
  const storedCart = JSON.parse(sessionStorage.getItem('cartQty') || '{}');
  const inputs = document.querySelectorAll('.qty-input'); // Lấy tất cả ô nhập liệu số lượng trên form
  
  inputs.forEach(function (input) {
    const productId = input.dataset.id; // Mã SP lưu trong th:data-id
    
    // Kiểm tra xem SP này đã có dữ liệu nhập trong session chưa
    if (storedCart[productId]) {
      // Nếu có, tự động điền lại giá trị vào UI cho người dùng
      input.value = storedCart[productId];
      // Đồng thời validate lại (đề phòng ai đó sửa bậy bạ biến session bằng F12)
      const numVal = Number(input.value);
      // Nếu dữ liệu khôi phục không phải là số hợp lệ -> đánh dấu ô hiển thị viền đỏ
      if (isNaN(numVal) || !Number.isInteger(numVal) || numVal < 0) {
        input.classList.add('is-invalid');
      }
    }
  });

  // ============================================================================
  // 3. VALIDATE LOGIC NGAY KHI NGƯỜI DÙNG ĐANG GÕ VÀO Ô SỐ LƯỢNG
  // ============================================================================
  const statusText = document.getElementById('statusText'); // Chỗ hiển thị chữ lỗi
  const qtyInputs = document.querySelectorAll('.qty-input');
  
  qtyInputs.forEach(function (input) {
    input.addEventListener('input', function () {
      // Mỗi phím gõ xuống thì reset lại trạng thái lỗi trước đó
      statusText.textContent = '';
      input.classList.remove('is-invalid');
      input.removeAttribute('title'); // Gỡ tooltip báo lỗi
      
      const rawValue = input.value; // Lấy chuỗi thô người dùng gõ
      const numVal = Number(rawValue); // Ép kiểu thử sang dạng Số
      const max = parseInt(input.dataset.max, 10); // Lấy giới hạn tồn kho max từ th:data-max
      const productId = input.dataset.id; 
      const name = input.dataset.name;

      // Đọc giỏ session hiện tại lên (để add vào hoặc xóa đi)
      const storedCart = JSON.parse(sessionStorage.getItem('cartQty') || '{}');

      // TÌNH HUỐNG 1: Người dùng xóa trắng ô input -> Xóa SP này khỏi giỏ session
      if (!rawValue) {
         delete storedCart[productId];
      } 
      // TÌNH HUỐNG 2: Nhập bậy (Chữ cái, số thập phân, số âm) -> Báo lỗi & xóa khỏi session
      else if (isNaN(numVal) || !Number.isInteger(numVal) || numVal < 0) {
         statusText.textContent = 'Số lượng nhập của ' + name + ' phải là số nguyên dương (không chứa dấu thập phân, âm).';
         input.classList.add('is-invalid'); // Tô viền đỏ ô input
         input.title = 'Vui lòng nhập số nguyên dương';
         delete storedCart[productId]; // Xóa dữ liệu rác này để không ghim vào Giỏ
      } 
      // TÌNH HUỐNG 3: Nhập Số nguyên hợp lệ
      else {
         // Nếu số lớn hơn tồn kho thực tế -> Tô cảnh báo nhưng VẪN cho vào Session để người dùng xem lại
         if (numVal > max) {
           statusText.textContent = 'Số lượng nhập của ' + name + ' vượt quá ' + max;
           input.classList.add('is-invalid');
           input.title = 'Tồn kho chỉ còn ' + max;
         }
         
         // Nếu nhập số 0 hệt như chưa nhập -> Xóa đi luôn
         if (numVal === 0) {
           delete storedCart[productId];
         } else {
           // Còn không thì chính thức ghi đè/lưu số lượng của SP này vào bộ Session chung
           storedCart[productId] = numVal;
         }
      }
      
      // Ghi ngược trở lại JSON object vào RAM SessionStorage 
      // -> Nếu lật sang trang 2, session này vẫn sống.
      sessionStorage.setItem('cartQty', JSON.stringify(storedCart));
    });
  });

  // ============================================================================
  // 4. KIỂM TRA CHỐT CHẶN CUỐI CÙNG KHI BẤM NÚT "ĐẶT HÀNG"
  // ============================================================================
  const btnOrder = document.getElementById('btnOrder');
  if (btnOrder) {
    btnOrder.addEventListener('click', function () {
      const statusText = document.getElementById('statusText');
      const inputs = document.querySelectorAll('.qty-input');
      const errors = []; // Mảng chứa các dòng báo lỗi nếu có

      // Lọc tất cả ô input chỉ lấy các sản phẩm chưa bị vô hiệu hóa (status === false)
      const enabledInputs = Array.from(inputs).filter(function (input) {
        return input.dataset.status === 'false';
      });

      // Kéo giỏ hàng toàn cục hiện đang có trong máy lên
      const storedCart = JSON.parse(sessionStorage.getItem('cartQty') || '{}');
      // Chỉ lấy ra danh sách các ProductId có số lượng lớn hơn 0
      const productIdsInCart = Object.keys(storedCart).filter(id => storedCart[id] > 0);

      // B1: CHECK LỖI TYPO
      let hasInvalidInput = false;
      enabledInputs.forEach(function(input) {
         const numVal = Number(input.value);
         // Kiểm tra xem hiện trên màn hình UI có ô nào đang chứa chữ cái hay phẩy/âm không
         const isInvalidNumber = input.value && (isNaN(numVal) || !Number.isInteger(numVal) || numVal < 0);

         // Nếu ô đó đang bị class 'is-invalid' từ lúc gõ, hoặc vừa phát hiện là number hỏng dở
         if (input.classList.contains('is-invalid') || isInvalidNumber) {
             hasInvalidInput = true;
             input.classList.add('is-invalid');
             if (isInvalidNumber) {
               errors.push('"' + input.dataset.name + '": Số lượng phải là số nguyên hợp lệ (không chứa phần thập phân hoặc âm).');
             }
         }
      });

      // Nếu kẹt ở B1 (Lỗi type) thì văng ngay không cho qua
      if (hasInvalidInput) {
         if (errors.length === 0) errors.push('Vui lòng sửa các lỗi nhập liệu (được tô đỏ).');
         statusText.innerHTML = 'Lỗi nhập liệu:<br>' + errors.join('<br>');
         return;
      }

      // B2: CHECK GIỎ CÓ TRỐNG KHÔNG
      // Nếu Session ghi nhận mọi thứ sạch trơn (người dùng chưa nhập bất cứ số nào > 0 ở trang này cũng như trang trước)
      if (productIdsInCart.length === 0) {
        statusText.textContent = 'Vui lòng nhập số lượng hợp lệ cho ít nhất một sản phẩm.';
        return;
      }

      // B3: CHECK LỖI TỒN KHO OVER (VƯỢT TRẦN)
      enabledInputs.forEach(function (input) {
        const rawValue = input.value;
        if (!rawValue) return;
        
        const val = parseInt(rawValue, 10);
        const max = parseInt(input.dataset.max, 10);
        const name = input.dataset.name;

        // Nếu phát hiện trên giao diện chừa lại 1 ô vượt Max, add message vào và chặn
        if (val > max) {
          errors.push('"' + name + '": đã nhập ' + val + ', nhưng tồn kho khả dụng chỉ còn ' + max);
          input.classList.add('is-invalid');
        }
      });

      // TỔNG KẾT
      if (errors.length > 0) {
        // Bể kèo, In danh sách lỗi bằng tag <br> xuống dòng
        statusText.innerHTML = 'Lỗi đặt hàng:<br>' + errors.join('<br>');
      } else {
        // Okie qua tọt 
        statusText.textContent = '';
        
        // Mẹo chuyển trang: Lưu lại đoạn String thông số đường dẫn trên thanh URL hiện tại (VD: ?page=2&name=abc)
        // Để lát nữa nếu user Cancel trên màn Order thì chèn ngược String này lại vào để về đúng y vị trí cũ.
        sessionStorage.setItem('lastSearchUrl', window.location.search);
        
        // Điều hướng chuyển trang Order để tiến hành xem thông tin đặt
        window.location.href = '/shop/product-order';
      }
    });
  }
});
