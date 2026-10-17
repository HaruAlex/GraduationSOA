// ===== REGISTRATIONS MODULE =====

const API = 'http://localhost:9003/api/registrations';
let allData = [];
let filteredData = [];
let currentPage = 1;
const rowsPerPage = 15;

// ===== LOAD DATA =====
async function loadData(showNotify = true) {
    try {
        const result = await apiRequest(API, 'GET');
        if (result.ok) {
            allData = result.data;
            filterData();
            if (showNotify) showToast(`Đã tải ${result.data.length} đăng ký`, 'success');
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

// ===== IMPORT CSV =====
// Định dạng CSV: MaSV,MaDT
// Dòng đầu là header, bỏ qua khi import

function triggerImport() {
    const input = document.getElementById('importFileInput');
    input.value = '';
    input.click();
}

async function handleImport(event) {
    const file = event.target.files[0];
    if (!file) return;

    let items;
    try {
        items = await parseSpreadsheet(file, cols => {
            // cols: [MaSV, MaDT]
            const maSV = (cols[0] || '').trim();
            const maDT = (cols[1] || '').trim();

            if (!maSV || !maDT) return null;
            return { maSV, maDT };
        });
    } catch (err) {
        showToast('Lỗi đọc file: ' + err, 'error');
        return;
    }

    if (items.length === 0) {
        showToast('Không có dòng dữ liệu hợp lệ trong file CSV', 'error');
        return;
    }

    showToast(`Đang import ${items.length} đăng ký...`, 'info');

    const { success, failed, errors } = await importToAPI(API, items);

    console.log('[Import debug] success:', success, 'failed:', failed, 'errors:', errors);

    await loadData(false);

    if (failed === 0) {
        showToast(`Import thành công ${success} đăng ký`, 'success');
        showImportErrors([], 'đăng ký');
    } else {
        showToast(`Import: ${success} thành công, ${failed} thất bại`, 'error');
        showImportErrors(errors, 'đăng ký');
        const detail = errors.map(({ index, item, message }) =>
            `Dòng ${index} [SV: ${item.maSV} → ĐT: ${item.maDT}]: ${message}`
        ).join('\n');
        alert(`Kết quả import đăng ký:\n✔ Thành công: ${success}\n✘ Thất bại: ${failed}\n\nChi tiết lỗi:\n${detail}`);
    }
}
