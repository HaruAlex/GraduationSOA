package services;

import models.Student;
import repositories.StudentRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;

@Singleton
public class StudentService {

    private final StudentRepository studentRepository;

    @Inject
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(String maSV) {
        return studentRepository.findById(maSV);
    }

    // Thêm sinh viên kèm Validation
    public String createStudent(Student student) {
        // 1. Validation cơ bản
        if (student.getMaSV() == null || student.getMaSV().trim().isEmpty()) {
            return "Mã sinh viên không được để trống!";
        }
        if (student.getHoTen() == null || student.getHoTen().trim().isEmpty()) {
            return "Họ tên sinh viên không được để trống!";
        }
        if (student.getEmail() == null || !student.getEmail().contains("@")) {
            return "Email không đúng định dạng!";
        }

        // 2. Validation trùng Mã SV
        if (studentRepository.findById(student.getMaSV()).isPresent()) {
            return "DUPLICATE_KEY";
        }

        boolean created = studentRepository.create(student);
        return created ? "SUCCESS" : "Lỗi hệ thống khi thêm sinh viên!";
    }

    public boolean updateStudent(String maSV, Student student) {
        if (!studentRepository.findById(maSV).isPresent()) {
            return false;
        }
        return studentRepository.update(maSV, student);
    }

    public boolean deleteStudent(String maSV) {
        if (!studentRepository.findById(maSV).isPresent()) {
            return false;
        }
        return studentRepository.delete(maSV);
    }
}