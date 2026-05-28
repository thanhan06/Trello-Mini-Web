// ============================================================================
// 1. XỬ LÝ KHI NGƯỜI DÙNG RELOAD TRANG (BẤM F5 HOẶC TẢI LẠI)
// ============================================================================
if (window.performance) {
  const navEntries = window.performance.getEntriesByType("navigation");
  if (navEntries.length > 0 && navEntries[0].type === "reload") {
    sessionStorage.removeItem('cartQty');
    if (window.location.search !== "") {
      window.location.href = window.location.pathname;
    }
  }
}

// ============================================================================
// 2. KHÔI PHỤC DỮ LIỆU KHI TRANG VỪA TẢI XONG
// ============================================================================
document.addEventListener('DOMContentLoaded', function () {
  
  // [ĐIỂM SỬA 1] --- A.0 LƯU TRỮ GIÁ TRỊ NHÁP KHI CLICK CHUYỂN TRANG MÀ CHƯA TÌM KIẾM ---
  // Lắng nghe sự kiện click trên các nút phân trang. Nếu click thì lưu luôn dữ liệu đang nhập dở vào Session
  const pagBtns = document.querySelectorAll('.pag-btn');
  pagBtns.forEach(btn => {
    btn.addEventListener('click', function() {
      if (!this.classList.contains('disabled')) {
        // Lưu nháp ngay trước khi trình duyệt chuyển trang qua đường link phân trang
        sessionStorage.setItem('draftSearch', JSON.stringify({
          name: document.getElementById('qName')?.value || '',
          desc: document.getElementById('qDesc')?.value || '',
          type: document.getElementById('qTypeHidden')?.value || ''
        }));
      }
    });
  });

  // [ĐIỂM SỬA 2] --- A.1 KHÔI PHỤC GIÁ TRỊ TÌM KIẾM TỪ BẢN NHÁP (DRAFT) HOẶC TỪ URL ---
  // Đọc đồng thời cả giá trị URL và bản nháp (nếu có)
  const urlParams = new URLSearchParams(window.location.search);
  const draftSearchStr = sessionStorage.getItem('draftSearch');
  let draftSearch = null;
  if (draftSearchStr) {
    try { draftSearch = JSON.parse(draftSearchStr); } catch(e) {}
    sessionStorage.removeItem('draftSearch'); // Dùng xong xoá ngay để không ảnh hưởng trang khác
  }
  
  const qNameInput = document.getElementById('qName');
  if (qNameInput) {
    // [ĐIỂM SỬA 3] Ưu tiên lấy giá trị từ bản nháp trước, nếu không có mới lấy từ URL
    if (draftSearch) qNameInput.value = draftSearch.name;
    else if (urlParams.has('name')) qNameInput.value = urlParams.get('name');
  }
  
  const qDescInput = document.getElementById('qDesc');
  if (qDescInput) {
    // [ĐIỂM SỬA 3] Tương tự với ô Mô tả
    if (draftSearch) qDescInput.value = draftSearch.desc;
    else if (urlParams.has('desc')) qDescInput.value = urlParams.get('desc');
  }

  const qTypeHidden = document.getElementById('qTypeHidden');
  if (qTypeHidden) {
    // [ĐIỂM SỬA 3] Tương tự với Loại sản phẩm
    if (draftSearch) qTypeHidden.value = draftSearch.type;
    else if (urlParams.has('type')) qTypeHidden.value = urlParams.get('type');
  }

  // --- A.2 XỬ LÝ SỰ KIỆN VÀ HIỂN THỊ DROPDOWN LOẠI SẢN PHẨM ---
  const qTypeLabel = document.getElementById('qTypeLabel');
  const drpItems = document.querySelectorAll('#qTypeDropdown + .dropdown-menu .dropdown-item');

  let initialMatched = false;
  drpItems.forEach(item => {
    if (qTypeHidden && item.getAttribute('data-value') === qTypeHidden.value) {
       qTypeLabel.textContent = item.textContent === '\xa0' ? '' : item.textContent;
       initialMatched = true;
    }
    
    item.addEventListener('click', function(e) {
      e.preventDefault();
      const val = this.getAttribute('data-value');
      if (qTypeHidden) qTypeHidden.value = val;
      qTypeLabel.textContent = this.textContent === '\xa0' ? '' : this.textContent;
    });
  });
  if(!initialMatched && qTypeLabel) qTypeLabel.textContent = ''; 

  // (Đã loại bỏ phần A.3 can thiệp form submit khi bấm chuyển trang. 
  // Nút phân trang sẽ hoạt động theo href mặc định của Thymeleaf, 
  // nghĩa là sẽ áp dụng đúng điều kiện của lần bấm nút "Tìm kiếm" gần nhất).

  // --- B. KHÔI PHỤC DỮ LIỆU CÁC Ô NHẬP SỐ LƯỢNG SẢN PHẨM ---
  const storedCart = JSON.parse(sessionStorage.getItem('cartQty') || '{}');
  const inputs = document.querySelectorAll('.qty-input');
  
  inputs.forEach(function (input) {
    const productId = input.dataset.id;
    if (storedCart[productId]) {
      input.value = storedCart[productId];
      const numVal = Number(input.value);
      if (isNaN(numVal) || !Number.isInteger(numVal) || numVal < 0) {
        input.classList.add('is-invalid');
      }
    }
  });

  // ============================================================================
  // 3. VALIDATE LOGIC NGAY KHI NGƯỜI DÙNG ĐANG GÕ VÀO Ô SỐ LƯỢNG
  // ============================================================================
  const statusText = document.getElementById('statusText');
  const qtyInputs = document.querySelectorAll('.qty-input');
  
  qtyInputs.forEach(function (input) {
    input.addEventListener('input', function () {
      statusText.textContent = '';
      input.classList.remove('is-invalid');
      input.removeAttribute('title');
      
      const rawValue = input.value;
      const numVal = Number(rawValue);
      const max = parseInt(input.dataset.max, 10);
      const productId = input.dataset.id; 
      const name = input.dataset.name;

      const storedCart = JSON.parse(sessionStorage.getItem('cartQty') || '{}');

      if (!rawValue) {
         delete storedCart[productId];
      } else if (isNaN(numVal) || !Number.isInteger(numVal) || numVal < 0) {
         statusText.textContent = 'Số lượng nhập của ' + name + ' phải là số nguyên dương.';
         input.classList.add('is-invalid');
         input.title = 'Vui lòng nhập số nguyên dương';
         delete storedCart[productId];
      } else {
         if (numVal > max) {
           statusText.textContent = 'Số lượng nhập của ' + name + ' vượt quá ' + max;
           input.classList.add('is-invalid');
           input.title = 'Tồn kho chỉ còn ' + max;
         }
         if (numVal === 0) {
           delete storedCart[productId];
         } else {
           storedCart[productId] = numVal;
         }
      }
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
      const errors = [];

      const enabledInputs = Array.from(inputs).filter(function (input) {
        return input.dataset.status === 'false';
      });

      const storedCart = JSON.parse(sessionStorage.getItem('cartQty') || '{}');
      const productIdsInCart = Object.keys(storedCart).filter(id => storedCart[id] > 0);

      let hasInvalidInput = false;
      enabledInputs.forEach(function(input) {
         const numVal = Number(input.value);
         const isInvalidNumber = input.value && (isNaN(numVal) || !Number.isInteger(numVal) || numVal < 0);

         if (input.classList.contains('is-invalid') || isInvalidNumber) {
             hasInvalidInput = true;
             input.classList.add('is-invalid');
             if (isInvalidNumber) {
               errors.push('"' + input.dataset.name + '": Số lượng phải là số nguyên hợp lệ.');
             }
         }
      });

      if (hasInvalidInput) {
         if (errors.length === 0) errors.push('Vui lòng sửa các lỗi nhập liệu (được tô đỏ).');
         statusText.innerHTML = 'Lỗi nhập liệu:<br>' + errors.join('<br>');
         return;
      }

      if (productIdsInCart.length === 0) {
        statusText.textContent = 'Vui lòng nhập số lượng hợp lệ cho ít nhất một sản phẩm.';
        return;
      }

      enabledInputs.forEach(function (input) {
        const rawValue = input.value;
        if (!rawValue) return;
        
        const val = parseInt(rawValue, 10);
        const max = parseInt(input.dataset.max, 10);
        const name = input.dataset.name;

        if (val > max) {
          errors.push('"' + name + '": đã nhập ' + val + ', nhưng tồn kho khả dụng chỉ còn ' + max);
          input.classList.add('is-invalid');
        }
      });

      if (errors.length > 0) {
        statusText.innerHTML = 'Lỗi đặt hàng:<br>' + errors.join('<br>');
      } else {
        statusText.textContent = '';
        sessionStorage.setItem('lastSearchUrl', window.location.search);
        window.location.href = '/shop/product-order';
      }
    });
  }
});