// ===== THESES MODULE =====

const API = 'http://localhost:9002/api/theses';
let allData = [];
let filteredData = [];
let currentPage = 1;
const rowsPerPage = 15;
let modalMode = 'add';
let editingId = null;

// ===== LOAD DATA =====
async function loadData() {
    try {
        const result = await apiRequest(API, 'GET');
        if (result.ok) {
            allData = result.data;
            filterData();
            showToast(`Đã tải ${result.data.length} đề tài`, 'success');
        }
    } catch (err) {
        console.error(err);
    }
}

// ===== FILTER & RENDER =====
function filterData() {
    const keyword = document.getElementById('searchInput').value;
    filteredData = filterByKeyword(allData, keyword, ['maDT', 'tenDT', 'giangVien']);
    currentPage = 1;
    renderTable();
}

function renderTable() {
    const { pageData, totalPages } = calculatePagination(filteredData, currentPage, rowsPerPage);
    const tbody = document.getElementById('tableBody');
    
    updateCountLabel('countLabel', filteredData.length, 'đề tài');

    if (pageData.length === 0) {
        tbody.innerHTML = renderEmptyRow(6);
        document.getElementById('pagination').innerHTML = '';
        return;
    }

    tbody.innerHTML = pageData.map(dt => `
        <tr>
            <td class="center"><input type="checkbox" class="row-check" value="${dt.maDT}"></td>
            <td>${escHtml(dt.maDT)}</td>
            <td>${escHtml(dt.tenDT)}</td>
            <td>${escHtml(dt.giangVien || '')}</td>
            <td class="center">${dt.soLuongToiDa}</td>
            <td class="center">
                <button class="btn-link-blue" onclick="editRow('${dt.maDT}')">Sửa</button>
                <button class="btn-link-red" onclick="deleteRow('${dt.maDT}')">Xóa</button>
            </td>
        </tr>
    `).join('');

    renderPagination(currentPage, totalPages, 'goToPage');
}

function goToPage(page) {
    currentPage = page;
    renderTable();
}

// ===== MODAL =====
function openAddModal() {
    modalMode = 'add';
    editingId = null;
    document.getElementById('modalTitle').textContent = 'Thêm đề tài';
    clearModalInputs(['modalMaDT', 'modalTenDT', 'modalGiangVien', 'modalSoLuong']);
    disableInput('modalMaDT', false);
    openModal();
}

function openEditModal() {
    const checked = getCheckedIds();
    if (checked.length === 0) {
        showToast('Vui lòng chọn 1 đề tài để sửa', 'error');
        return;
    }
    if (checked.length > 1) {
        showToast('Chỉ được chọn 1 đề tài để sửa', 'error');
        return;
    }
    editRow(checked[0]);
}

function editRow(maDT) {
    const dt = allData.find(x => x.maDT === maDT);
    if (!dt) return;

    modalMode = 'edit';
    editingId = maDT;
    document.getElementById('modalTitle').textContent = 'Sửa đề tài';
    
    setModalInputs({
        modalMaDT: dt.maDT,
        modalTenDT: dt.tenDT,
        modalGiangVien: dt.giangVien || '',
        modalSoLuong: dt.soLuongToiDa
    });
    
    disableInput('modalMaDT', true);
    openModal();
}

async function submitModal() {
    const maDT         = document.getElementById('modalMaDT').value.trim();
    const tenDT        = document.getElementById('modalTenDT').value.trim();
    const giangVien    = document.getElementById('modalGiangVien').value.trim();
    const soLuongToiDa = parseInt(document.getElementById('modalSoLuong').value);

    if (!validateRequired([
        {value: maDT, name: 'Mã đề tài'},
        {value: tenDT, name: 'Tên đề tài'}
    ])) return;

    if (!soLuongToiDa || soLuongToiDa <= 0) {
        showToast('Số lượng tối đa phải lớn hơn 0', 'error');
        return;
    }

    const body = { maDT, tenDT, giangVien, soLuongToiDa };

    if (modalMode === 'add') {
        const result = await apiRequest(API, 'POST', body);
        if (result.status === 201) {
            showToast('Thêm thành công', 'success');
            closeModal();
            loadData();
        } else {
            showToast(result.data?.message || 'Lỗi: ' + result.status, 'error');
        }
    } else {
        const result = await apiRequest(`${API}/${editingId}`, 'PUT', {tenDT, giangVien, soLuongToiDa});
        if (result.ok) {
            showToast('Cập nhật thành công', 'success');
            closeModal();
            loadData();
        } else {
            showToast(result.data?.message || 'Lỗi: ' + result.status, 'error');
        }
    }
}

// ===== DELETE =====
async function deleteRow(maDT) {
    if (await deleteOne(API, maDT, 'đề tài')) {
        loadData();
    }
}

async function deleteSelected() {
    const ids = getCheckedIds();
    if (await deleteMultiple(API, ids, 'đề tài')) {
        clearAllCheckboxes();
        loadData();
    }
}

// ===== EXPORT =====
function exportData() {
    const rows = filteredData.map(dt => [
        dt.maDT,
        dt.tenDT,
        dt.giangVien || '',
        dt.soLuongToiDa
    ]);
    exportToCSV('danh-sach-de-tai.csv', ['Mã ĐT', 'Tên đề tài', 'Giảng viên', 'Số lượng tối đa'], rows);
}

// ===== INIT =====
document.getElementById('searchInput').addEventListener('input', filterData);
loadData();
