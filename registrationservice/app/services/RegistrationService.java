package services;

import clients.StudentClient;
import clients.ThesisClient;
import com.fasterxml.jackson.databind.JsonNode;
import models.Registration;
import repositories.RegistrationRepository;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final StudentClient studentClient;
    private final ThesisClient thesisClient;

    @Inject
    public RegistrationService(
            RegistrationRepository registrationRepository,
            StudentClient studentClient,
            ThesisClient thesisClient
    ) {

        this.registrationRepository = registrationRepository;
        this.studentClient = studentClient;
        this.thesisClient = thesisClient;
    }

    public List<Registration> getAll()
            throws SQLException {

        return registrationRepository.findAll();
    }

    public String create(String maSV, String maDT)
            throws IOException,
            InterruptedException,
            SQLException {

        System.out.println(
                "[Registration] Create registration: "
                        + maSV + " -> " + maDT
        );

        // 1. Kiểm tra sinh viên tồn tại
        JsonNode student =
                studentClient.getStudent(maSV);

        if (student == null) {

            System.out.println(
                    "[Registration] Student not found: "
                            + maSV
            );

            return "STUDENT_NOT_FOUND";
        }

        // 2. Kiểm tra đề tài tồn tại
        JsonNode thesis =
                thesisClient.getThesis(maDT);

        if (thesis == null) {

            System.out.println(
                    "[Registration] Thesis not found: "
                            + maDT
            );

            return "THESIS_NOT_FOUND";
        }

        // 3. Kiểm tra đăng ký trùng
        boolean exists =
                registrationRepository.exists(maSV, maDT);

        if (exists) {

            System.out.println(
                    "[Registration] Duplicate registration"
            );

            return "DUPLICATE";
        }

        // 4. Lấy số lượng tối đa của đề tài
        int soLuongToiDa =
                thesis.path("soLuongToiDa").asInt();

        // 5. Đếm số sinh viên đã đăng ký
        int currentCount =
                registrationRepository.countByThesis(maDT);

        System.out.println(
                "[Registration] Current: "
                        + currentCount
                        + " / Max: "
                        + soLuongToiDa
        );

        // 6. Kiểm tra đề tài đã đầy
        if (currentCount >= soLuongToiDa) {

            System.out.println(
                    "[Registration] Thesis is full"
            );

            return "THESIS_FULL";
        }

        // 7. Thực hiện đăng ký
        registrationRepository.create(
                maSV,
                maDT
        );

        System.out.println(
                "[Registration] Registration successful"
        );

        return "SUCCESS";
    }

    public boolean delete(
            String maSV,
            String maDT
    ) throws SQLException {

        System.out.println(
                "[Registration] Delete registration: "
                        + maSV + " -> " + maDT
        );

        return registrationRepository.delete(
                maSV,
                maDT
        );
    }
}