package repositories;

import models.Student;
import play.db.Database;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class StudentRepository {

    private final Database db;

    @Inject
    public StudentRepository(Database db) {
        this.db = db;
    }

    // 1. Lấy toàn bộ danh sách sinh viên
    public List<Student> findAll() {
        return db.withConnection(conn -> {
            List<Student> students = new ArrayList<>();
            String sql = "SELECT MaSV, HoTen, Email, Lop FROM SINHVIEN";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(new Student(
                        rs.getString("MaSV"),
                        rs.getString("HoTen"),
                        rs.getString("Email"),
                        rs.getString("Lop")
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return students;
        });
    }

    // 2. Tìm sinh viên theo MaSV
    public Optional<Student> findById(String maSV) {
        return db.withConnection(conn -> {
            String sql = "SELECT MaSV, HoTen, Email, Lop FROM SINHVIEN WHERE MaSV = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maSV);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return Optional.of(new Student(
                        rs.getString("MaSV"),
                        rs.getString("HoTen"),
                        rs.getString("Email"),
                        rs.getString("Lop")
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Optional.empty();
        });
    }

    // 3. Thêm sinh viên mới
    public boolean create(Student student) {
        return db.withConnection(conn -> {
            String sql = "INSERT INTO SINHVIEN (MaSV, HoTen, Email, Lop) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, student.getMaSV());
                stmt.setString(2, student.getHoTen());
                stmt.setString(3, student.getEmail());
                stmt.setString(4, student.getLop());
                return stmt.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    // 4. Cập nhật sinh viên
    public boolean update(String maSV, Student student) {
        return db.withConnection(conn -> {
            String sql = "UPDATE SINHVIEN SET HoTen = ?, Email = ?, Lop = ? WHERE MaSV = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, student.getHoTen());
                stmt.setString(2, student.getEmail());
                stmt.setString(3, student.getLop());
                stmt.setString(4, maSV);
                return stmt.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    // 5. Xóa sinh viên
    public boolean delete(String maSV) {
        return db.withConnection(conn -> {
            String sql = "DELETE FROM SINHVIEN WHERE MaSV = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maSV);
                return stmt.executeUpdate() > 0;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }
}