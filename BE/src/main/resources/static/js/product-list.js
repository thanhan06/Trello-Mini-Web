const PAGE_SIZE = 5;

function getAccessToken() {
  return localStorage.getItem('accessToken') || '';
}

function setStatus(message) {
  const el = document.getElementById('statusText');
  if (el) el.textContent = message || '';
}

function decodeJwt(token) {
  try {
    const parts = String(token || '').split('.');
    if (parts.length !== 3) return null;
    const payload = parts[1].replace(/-/g, '+').replace(/_/g, '/');
    const json = atob(payload);
    return JSON.parse(json);
  } catch {
    return null;
  }
}

function normalize(str) {
  return String(str || '').trim().toLowerCase();
}

function matchesQuery(product, q) {
  const nameOk = !q.name || normalize(product.productName).includes(q.name);
  const descOk = !q.desc || normalize(product.description).includes(q.desc);
  const typeOk = !q.typeId || String(product.producttypeId) === String(q.typeId);
  return nameOk && descOk && typeOk;
}

function renderRows(items, pageIndex, tbody) {
  tbody.innerHTML = '';

  if (!items.length) {
    const tr = document.createElement('tr');
    const td = document.createElement('td');
    td.colSpan = 5;
    td.className = 'empty-cell';
    td.textContent = 'Không có dữ liệu';
    tr.appendChild(td);
    tbody.appendChild(tr);
    return;
  }

  const start = pageIndex * PAGE_SIZE;
  const slice = items.slice(start, start + PAGE_SIZE);
  slice.forEach((p, idx) => {
    const tr = document.createElement('tr');

    const tdNo = document.createElement('td');
    tdNo.textContent = String(start + idx + 1);
    tr.appendChild(tdNo);

    const tdImg = document.createElement('td');
    tdImg.className = 'img-cell';
    const img = document.createElement('img');
    img.className = 'product-img';
    img.alt = p.productName || '';
    img.src = p.productImg || '';
    img.onerror = () => {
      img.src = '';
      img.classList.add('img-missing');
    };
    tdImg.appendChild(img);
    tr.appendChild(tdImg);

    const tdName = document.createElement('td');
    tdName.textContent = p.productName || '';
    tr.appendChild(tdName);

    const tdDesc = document.createElement('td');
    tdDesc.textContent = p.description || '';
    tr.appendChild(tdDesc);

    const tdQty = document.createElement('td');
    tdQty.textContent = p.productAmount != null ? String(p.productAmount) : '';
    tr.appendChild(tdQty);

    tbody.appendChild(tr);
  });
}

document.addEventListener('DOMContentLoaded', async () => {
  const tbody = document.getElementById('productTbody');
  const qName = document.getElementById('qName');
  const qType = document.getElementById('qType');
  const qDesc = document.getElementById('qDesc');
  const btnSearch = document.getElementById('btnSearch');

  // const btnFirst = document.getElementById('btnFirst');
  // const btnPrev = document.getElementById('btnPrev');
  // const btnNext = document.getElementById('btnNext');
  // const btnLast = document.getElementById('btnLast');

  const helloUser = document.getElementById('helloUser');
  const displayName = document.getElementById('displayName');
  const logoutLink = document.getElementById('logoutLink');

  // if (!tbody || !qName || !qType || !qDesc || !btnSearch || !btnFirst || !btnPrev || !btnNext || !btnLast) return;

  const token = getAccessToken();
  const claims = decodeJwt(token);
  if (claims && helloUser) helloUser.textContent = claims.preferred_username || claims.email || claims.sub || 'user';
  if (claims && displayName) displayName.textContent = claims.name || displayName.textContent;

  if (logoutLink) {
    logoutLink.addEventListener('click', (e) => {
      e.preventDefault();
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      window.location.href = '/';
    });
  }

  let allProducts = [];
  let filtered = [];
  let pageIndex = 0;

  function applyFilterAndRender() {
    const query = {
      name: normalize(qName.value),
      typeId: String(qType.value || '').trim(),
      desc: normalize(qDesc.value)
    };

    filtered = allProducts.filter((p) => matchesQuery(p, query));
    const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
    if (pageIndex > totalPages - 1) pageIndex = totalPages - 1;

    renderRows(filtered, pageIndex, tbody);
    setStatus(`Trang ${pageIndex + 1}/${totalPages} - Tổng ${filtered.length} sản phẩm`);

    btnFirst.disabled = pageIndex === 0;
    btnPrev.disabled = pageIndex === 0;
    btnNext.disabled = pageIndex >= totalPages - 1;
    btnLast.disabled = pageIndex >= totalPages - 1;
  }

  btnSearch.addEventListener('click', () => {
    pageIndex = 0;
    applyFilterAndRender();
  });

  btnFirst.addEventListener('click', () => {
    pageIndex = 0;
    applyFilterAndRender();
  });
  btnPrev.addEventListener('click', () => {
    pageIndex = Math.max(0, pageIndex - 1);
    applyFilterAndRender();
  });
  btnNext.addEventListener('click', () => {
    pageIndex = pageIndex + 1;
    applyFilterAndRender();
  });
  btnLast.addEventListener('click', () => {
    const totalPages = Math.max(1, Math.ceil(filtered.length / PAGE_SIZE));
    pageIndex = totalPages - 1;
    applyFilterAndRender();
  });

  try {
    setStatus('Đang tải dữ liệu...');

    const initialTypes = window.__INITIAL_PRODUCT_TYPES__;
    // const types = Array.isArray(initialTypes) ? initialTypes : await apiGet('/shop/product-types');
    const types = Array.isArray(initialTypes) ? initialTypes : [];
    if (Array.isArray(types)) {
      types.forEach((t) => {
        const opt = document.createElement('option');
        opt.value = String(t.producttypeId);
        opt.textContent = t.name || String(t.producttypeId);
        if (qType) qType.appendChild(opt);
      });
    }

    const initialProducts = window.__INITIAL_PRODUCTS__;
    // const products = Array.isArray(initialProducts) ? initialProducts : await apiGet('/shop/products');
    // allProducts = Array.isArray(products) ? products : [];
    // filtered = allProducts;

    // applyFilterAndRender();
  } catch (err) {
    tbody.innerHTML = '';
    const tr = document.createElement('tr');
    const td = document.createElement('td');
    td.colSpan = 5;
    td.className = 'empty-cell';
    td.textContent = err instanceof Error ? err.message : 'Không thể tải dữ liệu';
    tr.appendChild(td);
    tbody.appendChild(tr);

    setStatus('');
  }
});
