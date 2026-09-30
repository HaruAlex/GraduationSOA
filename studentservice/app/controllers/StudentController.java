package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Student;
import play.libs.Json;
import play.mvc.*;
import services.StudentService;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

public class StudentController extends Controller {

    private final StudentService studentService;

    @Inject
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // GET /api/students
    public Result getAll() {
        List<Student> students = studentService.getAllStudents();
        return ok(Json.toJson(students));
    }

    // GET /api/students/:id
    public Result getById(String id) {
        Optional<Student> studentOpt = studentService.getStudentById(id);
        if (studentOpt.isPresent()) {
            return ok(Json.toJson(studentOpt.get()));
        }
        return notFound(Json.newObject().put("message", "Không tìm thấy sinh viên có mã: " + id));
    }

    // POST /api/students
    public Result create(Http.Request request) {
        JsonNode json = request.body().asJson();
        if (json == null) {
            return badRequest(Json.newObject().put("message", "Dữ liệu phải là định dạng JSON!"));
        }

        Student student = Json.fromJson(json, Student.class);
        String result = studentService.createStudent(student);

        if ("SUCCESS".equals(result)) {
            return created(Json.toJson(student)); // 201 Created
        } else if ("DUPLICATE_KEY".equals(result)) {
            return status(409, Json.newObject().put("message", "Mã sinh viên đã tồn tại trong hệ thống!")); // 409 Conflict
        }

        return badRequest(Json.newObject().put("message", result)); // 400 Bad Request
    }

    // PUT /api/students/:id
    public Result update(String id, Http.Request request) {
        JsonNode json = request.body().asJson();
        if (json == null) {
            return badRequest(Json.newObject().put("message", "Dữ liệu phải là định dạng JSON!"));
        }

        Student student = Json.fromJson(json, Student.class);
        boolean updated = studentService.updateStudent(id, student);

        if (updated) {
            student.setMaSV(id);
            return ok(Json.toJson(student)); // 200 OK
        }
        return notFound(Json.newObject().put("message", "Không tìm thấy sinh viên có mã: " + id)); // 404 Not Found
    }

    // DELETE /api/students/:id
    public Result delete(String id) {
        boolean deleted = studentService.deleteStudent(id);
        if (deleted) {
            return ok(Json.newObject().put("message", "Đã xóa thành công sinh viên mã: " + id)); // 200 OK
        }
        return notFound(Json.newObject().put("message", "Không tìm thấy sinh viên có mã: " + id)); // 404 Not Found
    }
}