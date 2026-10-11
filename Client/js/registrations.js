// ===== REGISTRATIONS MODULE =====

const API = 'http://localhost:9003/api/registrations';
let allData = [];
let filteredData = [];
let currentPage = 1;
const rowsPerPage = 15;

// ===== LOAD DATA =====
async function loadData() {
    try {
        const result = await apiRequest(API, 'GET');
        if (result.ok) {
            allData = result.data;
            filterData();
            showToast(`Đã tải ${result.data.length} đăng ký`, 'success');
        }
    } catch (err) {
        console.error(err);
    }
}

// ===== FILTER & RENDER =====
function filterData() {
    const keyword = document.getElementById('searchInput').value;
    filteredData = filterByKeyword(allData, keyword, ['maSV', 'maDT', 'ngayDangKy']);
    currentPage = 1;
    renderTable();
}

function renderTable() {
    const { pageData, totalPages } = calculatePagination(filteredData, currentPage, rowsPerPage);
    const tbody = document.getElementById('tableBody');
    
    updateCountLabel('countLabel', filteredData.length, 'đăng ký');

    if (pageData.length === 0) {
        tbody.innerHTML = renderEmptyRow(4);
        document.getElementById('pagination').innerHTML = '';
        return;
    }

    tbody.innerHTML = pageData.map(r => `
        <tr>
            <td>${escHtml(r.maSV)}</td>
            <td>${escHtml(r.maDT)}</td>
            <td>${formatDate(r.ngayDangKy)}</td>
            <td class="center">
                <button class="btn-link-red" onclick="deleteRow('${r.maSV}','${r.maDT}')">Xóa</button>
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
    document.getElementById('modalTitle').textContent = 'Đăng ký đề tài';
    clearModalInputs(['modalMaSV', 'modalMaDT']);
    disableInput('modalMaSV', false);
    disableInput('modalMaDT', false);
    openModal();
}

function openEditModal() {
    showToast('Chức năng sửa không áp dụng cho đăng ký (chỉ có thể thêm hoặc xóa)', 'info');
}

async function submitModal() {
    const maSV = document.getElementById('modalMaSV').value.trim();
    const maDT = document.getElementById('modalMaDT').value.trim();

    if (!validateRequired([
        {value: maSV, name: 'Mã sinh viên'},
        {value: maDT, name: 'Mã đề tài'}
    ])) return;

    const result = await apiRequest(API, 'POST', { maSV, maDT });
    
    if (result.status === 201) {
        showToast('Đăng ký thành công', 'success');
        closeModal();
        loadData();
    } else {
        const errorMsg = result.data?.error || result.data?.message || 'Lỗi: ' + result.status;
        showToast(errorMsg, 'error');
    }
}

// ===== DELETE =====
async function deleteRow(maSV, maDT) {
    if (!confirm(`Xác nhận xóa đăng ký: ${maSV} → ${maDT}?`)) return;
    
    try {
        const result = await apiRequest(`${API}/${maSV}/${maDT}`, 'DELETE');
        if (result.ok) {
            showToast('Đã xóa đăng ký', 'success');
            loadData();
        } else {
            showToast(result.data?.error || result.data?.message || 'Lỗi: ' + result.status, 'error');
        }
    } catch (err) {
        console.error(err);
    }
}

// ===== EXPORT =====
function exportData() {
    const rows = filteredData.map(r => [
        r.maSV,
        r.maDT,
        formatDate(r.ngayDangKy)
    ]);
    exportToCSV('danh-sach-dang-ky.csv', ['Mã SV', 'Mã ĐT', 'Ngày đăng ký'], rows);
}

// ===== INIT =====
document.getElementById('searchInput').addEventListener('input', filterData);
loadData();
