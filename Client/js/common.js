// ===== COMMON UTILITIES =====

// ===== STRING & FORMAT =====
function escHtml(str) {
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;');
}

function formatDate(dateStr) {
    if (!dateStr) return '';
    try {
        const d = new Date(dateStr);
        if (isNaN(d)) return dateStr;
        
        const day = String(d.getDate()).padStart(2, '0');
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const year = d.getFullYear();
        const hours = String(d.getHours()).padStart(2, '0');
        const mins = String(d.getMinutes()).padStart(2, '0');
        
        return `${day}/${month}/${year} ${hours}:${mins}`;
    } catch {
        return dateStr;
    }
}

// ===== TOAST NOTIFICATION =====
function showToast(msg, type = 'success') {
    const toast = document.getElementById('toast');
    if (!toast) return;
    
    toast.textContent = msg;
    toast.className = 'toast ' + type + ' show';
    setTimeout(() => toast.classList.remove('show'), 3000);
}

// ===== API REQUEST =====
async function apiRequest(url, method = 'GET', body = null) {
    const options = { method };
    
    if (body) {
        options.headers = { 'Content-Type': 'application/json' };
        options.body = JSON.stringify(body);
    }
    
    try {
        const res = await fetch(url, options);
        
        // DELETE có thể trả về 204 No Content
        if (res.status === 204) {
            return { ok: true, status: 204, data: null };
        }
        
        const data = await res.json();
        return { ok: res.ok, status: res.status, data };
        
    } catch (err) {
        showToast('Lỗi kết nối: ' + err.message, 'error');
        throw err;
    }
}

// ===== MODAL =====
function openModal(modalId = 'modalOverlay') {
    document.getElementById(modalId).classList.add('open');
}

function closeModal(modalId = 'modalOverlay') {
    document.getElementById(modalId).classList.remove('open');
}

function clearModalInputs(inputIds) {
    inputIds.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
}

function setModalInputs(data) {
    Object.keys(data).forEach(key => {
        const el = document.getElementById(key);
        if (el) el.value = data[key] || '';
    });
}

function disableInput(id, disabled = true) {
    const el = document.getElementById(id);
    if (el) el.disabled = disabled;
}

// ===== CHECKBOX =====
function toggleAllCheckboxes(checkAllId = 'checkAll', rowCheckClass = 'row-check') {
    const checkAll = document.getElementById(checkAllId);
    const checked = checkAll.checked;
    document.querySelectorAll('.' + rowCheckClass).forEach(cb => cb.checked = checked);
}

function getCheckedIds(rowCheckClass = 'row-check') {
    return Array.from(document.querySelectorAll('.' + rowCheckClass + ':checked')).map(cb => cb.value);
}

function clearAllCheckboxes(checkAllId = 'checkAll', rowCheckClass = 'row-check') {
    const checkAll = document.getElementById(checkAllId);
    if (checkAll) checkAll.checked = false;
    document.querySelectorAll('.' + rowCheckClass).forEach(cb => cb.checked = false);
}

// ===== PAGINATION =====
function calculatePagination(filteredData, currentPage, rowsPerPage) {
    const start = (currentPage - 1) * rowsPerPage;
    const end = start + rowsPerPage;
    const pageData = filteredData.slice(start, end);
    const totalPages = Math.ceil(filteredData.length / rowsPerPage);
    
    return { pageData, totalPages, start, end };
}

function renderPagination(currentPage, totalPages, onPageChangeFn) {
    const paginationEl = document.getElementById('pagination');
    if (!paginationEl) return;
    
    if (totalPages <= 1) {
        paginationEl.innerHTML = '';
        return;
    }

    let html = `<button class="page-btn" ${currentPage === 1 ? 'disabled' : ''} onclick="${onPageChangeFn}(${currentPage - 1})">‹</button>`;
    
    for (let i = 1; i <= totalPages; i++) {
        if (i === 1 || i === totalPages || (i >= currentPage - 1 && i <= currentPage + 1)) {
            html += `<button class="page-btn ${i === currentPage ? 'active' : ''}" onclick="${onPageChangeFn}(${i})">${i}</button>`;
        } else if (i === currentPage - 2 || i === currentPage + 2) {
            html += `<span style="padding:0 4px">...</span>`;
        }
    }

    html += `<button class="page-btn" ${currentPage === totalPages ? 'disabled' : ''} onclick="${onPageChangeFn}(${currentPage + 1})">›</button>`;
    paginationEl.innerHTML = html;
}

// ===== EXPORT CSV =====
function exportToCSV(filename, headers, dataRows) {
    const csv = [
        headers,
        ...dataRows
    ].map(row => row.join(',')).join('\n');

    const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.download = filename;
    link.click();
    showToast('Đã tải xuống file CSV', 'success');
}

// ===== TABLE RENDER =====
function renderEmptyRow(colspan, message = 'Không có dữ liệu') {
    return `<tr><td colspan="${colspan}" class="empty-row">${message}</td></tr>`;
}

function updateCountLabel(labelId, count, entityName) {
    const el = document.getElementById(labelId);
    if (el) el.textContent = `Đã tải ${count} ${entityName}`;
}

// ===== SEARCH/FILTER =====
function filterByKeyword(data, keyword, searchFields) {
    const kw = keyword.toLowerCase().trim();
    if (!kw) return data;
    
    return data.filter(item => 
        searchFields.some(field => {
            const value = item[field];
            return value && String(value).toLowerCase().includes(kw);
        })
    );
}

// ===== CRUD HELPERS =====
async function deleteMultiple(api, ids, entityName) {
    if (ids.length === 0) {
        showToast(`Vui lòng chọn ${entityName} cần xóa`, 'error');
        return false;
    }
    
    if (!confirm(`Xác nhận xóa ${ids.length} ${entityName}?`)) {
        return false;
    }

    let success = 0, failed = 0;
    
    for (const id of ids) {
        try {
            const result = await apiRequest(`${api}/${id}`, 'DELETE');
            if (result.ok || result.status === 204) success++;
            else failed++;
        } catch {
            failed++;
        }
    }
    
    const msg = failed === 0 
        ? `Xóa thành công ${success} ${entityName}`
        : `Xóa: ${success} thành công, ${failed} thất bại`;
    
    showToast(msg, success > 0 ? 'success' : 'error');
    return success > 0;
}

async function deleteOne(api, id, entityName) {
    if (!confirm(`Xác nhận xóa ${entityName} ${id}?`)) return false;
    
    try {
        const result = await apiRequest(`${api}/${id}`, 'DELETE');
        if (result.ok || result.status === 204) {
            showToast(`Đã xóa ${entityName} ${id}`, 'success');
            return true;
        } else {
            showToast(result.data?.message || 'Lỗi: ' + result.status, 'error');
            return false;
        }
    } catch (err) {
        return false;
    }
}

// ===== VALIDATION =====
function validateRequired(fields) {
    for (const {value, name} of fields) {
        if (!value || !value.trim()) {
            showToast(`${name} không được để trống`, 'error');
            return false;
        }
    }
    return true;
}
