// ===== STUDENTS MODULE =====

const API = 'http://localhost:9001/api/students';
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
            showToast(`Đã tải ${result.data.length} sinh viên`, 'success');
        }
    } catch (err) {
        console.error(err);
    }
}

// ===== FILTER & RENDER =====
function filterData() {
    const keyword = document.getElementById('searchInput').value;
    filteredData = filterByKeyword(allData, keyword, ['maSV', 'hoTen', 'email', 'lop']);
    currentPage = 1;
    renderTable();
}

function renderTable() {
    const { pageData, totalPages } = calculatePagination(filteredData, currentPage, rowsPerPage);
    const tbody = document.getElementById('tableBody');
    
    updateCountLabel('countLabel', filteredData.length, 'sinh viên');

    if (pageData.length === 0) {
        tbody.innerHTML = renderEmptyRow(6);
        document.getElementById('pagination').innerHTML = '';
        return;
    }

    tbody.innerHTML = pageData.map(sv => `
        <tr>
            <td class="center"><input type="checkbox" class="row-check" value="${sv.maSV}"></td>
            <td>${escHtml(sv.maSV)}</td>
            <td>${escHtml(sv.hoTen)}</td>
            <td>${escHtml(sv.email || '')}</td>
            <td>${escHtml(sv.lop || '')}</td>
            <td class="center">
                <button class="btn-link-blue" onclick="editRow('${sv.maSV}')">Sửa</button>
                <button class="btn-link-red" onclick="deleteRow('${sv.maSV}')">Xóa</button>
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
    document.getElementById('modalTitle').textContent = 'Thêm sinh viên';
    clearModalInputs(['modalMaSV', 'modalHoTen', 'modalEmail', 'modalLop']);
    disableInput('modalMaSV', false);
    openModal();
}

function openEditModal() {
    const checked = getCheckedIds();
    if (checked.length === 0) {
        showToast('Vui lòng chọn 1 sinh viên để sửa', 'error');
        return;
    }
    if (checked.length > 1) {
        showToast('Chỉ được chọn 1 sinh viên để sửa', 'error');
        return;
    }
    editRow(checked[0]);
}

function editRow(maSV) {
    const sv = allData.find(x => x.maSV === maSV);
    if (!sv) return;

    modalMode = 'edit';
    editingId = maSV;
    document.getElementById('modalTitle').textContent = 'Sửa sinh viên';
    
    setModalInputs({
        modalMaSV: sv.maSV,
        modalHoTen: sv.hoTen,
        modalEmail: sv.email || '',
        modalLop: sv.lop || ''
    });
    
    disableInput('modalMaSV', true);
    openModal();
}

async function submitModal() {
    const maSV  = document.getElementById('modalMaSV').value.trim();
    const hoTen = document.getElementById('modalHoTen').value.trim();
    const email = document.getElementById('modalEmail').value.trim();
    const lop   = document.getElementById('modalLop').value.trim();

    if (!validateRequired([
        {value: maSV, name: 'Mã sinh viên'},
        {value: hoTen, name: 'Họ tên'}
    ])) return;

    const body = { maSV, hoTen, email, lop };

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
        const result = await apiRequest(`${API}/${editingId}`, 'PUT', {hoTen, email, lop});
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
async function deleteRow(maSV) {
    if (await deleteOne(API, maSV, 'sinh viên')) {
        loadData();
    }
}

async function deleteSelected() {
    const ids = getCheckedIds();
    if (await deleteMultiple(API, ids, 'sinh viên')) {
        clearAllCheckboxes();
        loadData();
    }
}

// ===== EXPORT =====
function exportData() {
    const rows = filteredData.map(sv => [
        sv.maSV,
        sv.hoTen,
        sv.email || '',
        sv.lop || ''
    ]);
    exportToCSV('danh-sach-sinh-vien.csv', ['Mã SV', 'Họ tên', 'Email', 'Lớp'], rows);
}

// ===== INIT =====
document.getElementById('searchInput').addEventListener('input', filterData);
loadData();
