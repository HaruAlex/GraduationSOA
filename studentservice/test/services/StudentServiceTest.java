package services;

import models.Student;
import org.junit.Before;
import org.junit.Test;
import repositories.StudentRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

// Giả lập Repository lưu dữ liệu trên RAM để test độc lập, không phụ thuộc SQL Server
class FakeStudentRepository extends StudentRepository {
    private final Map<String, Student> db = new HashMap<>();

    public FakeStudentRepository() {
        super(null); // Không khởi tạo kết nối CSDL thật
    }

    @Override
    public List<Student> findAll() {
        return new ArrayList<>(db.values());
    }

    @Override
    public Optional<Student> findById(String maSV) {
        return Optional.ofNullable(db.get(maSV));
    }

    @Override
    public boolean create(Student student) {
        if (db.containsKey(student.getMaSV())) return false;
        db.put(student.getMaSV(), student);
        return true;
    }

    @Override
    public boolean update(String maSV, Student student) {
        if (!db.containsKey(maSV)) return false;
        db.put(maSV, student);
        return true;
    }

    @Override
    public boolean delete(String maSV) {
        return db.remove(maSV) != null;
    }
}

public class StudentServiceTest {

    private FakeStudentRepository fakeRepo;
    private StudentService studentService;

    @Before
    public void setUp() {
        fakeRepo = new FakeStudentRepository();
        studentService = new StudentService(fakeRepo);
    }

    // 1. Test lấy danh sách sinh viên
    @Test
    public void testGetAllStudents() {
        fakeRepo.create(new Student("SV01", "Nguyễn Văn An", "sv01@gmail.com", "CNTT01"));
        List<Student> list = studentService.getAllStudents();
        assertEquals(1, list.size());
        assertEquals("SV01", list.get(0).getMaSV());
    }

    // 2. Test lấy sinh viên theo mã (Tồn tại)
    @Test
    public void testGetStudentById_WhenExists() {
        fakeRepo.create(new Student("SV01", "Nguyễn Văn An", "sv01@gmail.com", "CNTT01"));
        Optional<Student> result = studentService.getStudentById("SV01");
        assertTrue(result.isPresent());
        assertEquals("Nguyễn Văn An", result.get().getHoTen());
    }

    // 3. Test lấy sinh viên theo mã (Không tồn tại)
    @Test
    public void testGetStudentById_WhenNotFound() {
        Optional<Student> result = studentService.getStudentById("SV99");
        assertFalse(result.isPresent());
    }

    // 4. Test thêm sinh viên thành công
    @Test
    public void testCreateStudent_Success() {
        Student st = new Student("SV02", "Trần Thị Bình", "sv02@gmail.com", "CNTT01");
        String result = studentService.createStudent(st);
        assertEquals("SUCCESS", result);
    }

    // 5. Test validation thêm sinh viên (Để trống Mã SV)
    @Test
    public void testCreateStudent_EmptyMaSV() {
        Student st = new Student("", "Trần Thị Bình", "sv02@gmail.com", "CNTT01");
        String result = studentService.createStudent(st);
        assertEquals("Mã sinh viên không được để trống!", result);
    }

    // 6. Test validation trùng Mã SV
    @Test
    public void testCreateStudent_DuplicateKey() {
        fakeRepo.create(new Student("SV01", "Nguyễn Văn An", "sv01@gmail.com", "CNTT01"));
        Student duplicate = new Student("SV01", "Nguyễn Văn An", "sv01@gmail.com", "CNTT01");
        String result = studentService.createStudent(duplicate);
        assertEquals("DUPLICATE_KEY", result);
    }

    // 7. Test cập nhật sinh viên
    @Test
    public void testUpdateStudent_Success() {
        fakeRepo.create(new Student("SV01", "Nguyễn Văn An", "sv01@gmail.com", "CNTT01"));
        Student updateInfo = new Student("SV01", "Nguyễn Văn An Mới", "an.new@gmail.com", "CNTT02");
        boolean updated = studentService.updateStudent("SV01", updateInfo);
        assertTrue(updated);
    }

    // 8. Test xóa sinh viên
    @Test
    public void testDeleteStudent_Success() {
        fakeRepo.create(new Student("SV01", "Nguyễn Văn An", "sv01@gmail.com", "CNTT01"));
        boolean deleted = studentService.deleteStudent("SV01");
        assertTrue(deleted);
        assertFalse(fakeRepo.findById("SV01").isPresent());
    }
}