// ===== STUDENTS MODULE =====

const API = 'http://localhost:9001/api/students';
let allData = [];
let filteredData = [];
let currentPage = 1;
const rowsPerPage = 15;
let modalMode = 'add';
let editingId = null;

// ===== LOAD DATA =====
async function loadData(showNotify = true) {
    try {
        const result = await apiRequest(API, 'GET');
        if (result.ok) {
            allData = result.data;
            filterData();
            if (showNotify) showToast(`Đã tải ${result.data.length} sinh viên`, 'success');
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

// ===== IMPORT CSV =====
// Định dạng CSV: MaSV,HoTen,Email,Lop
// Dòng đầu là header, bỏ qua khi import

function triggerImport() {
    const input = document.getElementById('importFileInput');
    input.value = ''; // reset để chọn lại cùng file vẫn trigger
    input.click();
}

async function handleImport(event) {
    const file = event.target.files[0];
    if (!file) return;

    let items;
    try {
        items = await parseSpreadsheet(file, cols => {
            // cols: [MaSV, HoTen, Email, Lop]
            const maSV  = (cols[0] || '').trim();
            const hoTen = (cols[1] || '').trim();
            const email = (cols[2] || '').trim();
            const lop   = (cols[3] || '').trim();

            if (!maSV || !hoTen) return null; // bỏ qua dòng thiếu bắt buộc
            return { maSV, hoTen, email, lop };
        });
    } catch (err) {
        showToast('Lỗi đọc file: ' + err, 'error');
        return;
    }

    if (items.length === 0) {
        showToast('Không có dòng dữ liệu hợp lệ trong file CSV', 'error');
        return;
    }

    showToast(`Đang import ${items.length} sinh viên...`, 'info');

    const { success, failed, errors } = await importToAPI(API, items);

    await loadData(false);

    if (failed === 0) {
        showToast(`Import thành công ${success} sinh viên`, 'success');
        showImportErrors([], 'sinh viên');
    } else {
        showToast(`Import: ${success} thành công, ${failed} thất bại`, 'error');
        showImportErrors(errors, 'sinh viên');
        const detail = errors.map(({ index, item, message }) =>
            `Dòng ${index} [${item.maSV} - ${item.hoTen}]: ${message}`
        ).join('\n');
        alert(`Kết quả import sinh viên:\n✔ Thành công: ${success}\n✘ Thất bại: ${failed}\n\nChi tiết lỗi:\n${detail}`);
    }
}
