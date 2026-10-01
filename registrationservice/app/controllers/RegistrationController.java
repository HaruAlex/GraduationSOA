package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Registration;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.RegistrationService;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class RegistrationController extends Controller {

    private final RegistrationService registrationService;

    @Inject
    public RegistrationController(
            RegistrationService registrationService
    ) {
        this.registrationService = registrationService;
    }

    // GET /api/registrations
    public Result getAll() {

        System.out.println(
                "[Registration] GET /api/registrations"
        );

        try {

            List<Registration> registrations =
                    registrationService.getAll();

            return ok(Json.toJson(registrations));

        } catch (SQLException e) {

            System.err.println(
                    "[Registration] Database error: "
                            + e.getMessage()
            );

            return internalServerError(
                    createJson(
                            "error",
                            "Lỗi cơ sở dữ liệu"
                    )
            );
        }
    }

    // POST /api/registrations
    public Result create(Http.Request request) {

        System.out.println(
                "[Registration] POST /api/registrations"
        );

        JsonNode json =
                request.body().asJson();

        // 400 - JSON không hợp lệ
        if (json == null) {

            return badRequest(
                    createJson(
                            "error",
                            "Dữ liệu JSON không hợp lệ"
                    )
            );
        }

        String maSV =
                json.path("maSV").asText(null);

        String maDT =
                json.path("maDT").asText(null);

        // 400 - thiếu dữ liệu
        if (maSV == null ||
                maSV.trim().isEmpty() ||
                maDT == null ||
                maDT.trim().isEmpty()) {

            return badRequest(
                    createJson(
                            "error",
                            "MaSV và MaDT không được để trống"
                    )
            );
        }

        try {

            String result =
                    registrationService.create(
                            maSV.trim(),
                            maDT.trim()
                    );

            switch (result) {

                case "STUDENT_NOT_FOUND":

                    return notFound(
                            createJson(
                                    "error",
                                    "Sinh viên không tồn tại"
                            )
                    );

                case "THESIS_NOT_FOUND":

                    return notFound(
                            createJson(
                                    "error",
                                    "Đề tài không tồn tại"
                            )
                    );

                case "DUPLICATE":

                    return status(
                            CONFLICT,
                            createJson(
                                    "error",
                                    "Sinh viên đã đăng ký đề tài này"
                            )
                    );

                case "THESIS_FULL":

                    return status(
                            CONFLICT,
                            createJson(
                                    "error",
                                    "Đề tài đã đủ số lượng sinh viên"
                            )
                    );

                case "SUCCESS":

                    ObjectNode success =
                            Json.newObject();

                    success.put(
                            "message",
                            "Đăng ký đề tài thành công"
                    );

                    success.put(
                            "maSV",
                            maSV
                    );

                    success.put(
                            "maDT",
                            maDT
                    );

                    return created(success);

                default:

                    return internalServerError(
                            createJson(
                                    "error",
                                    "Lỗi không xác định"
                            )
                    );
            }

        } catch (IOException |
                 InterruptedException e) {

            System.err.println(
                    "[Registration] Service-to-service error: "
                            + e.getMessage()
            );

            return internalServerError(
                    createJson(
                            "error",
                            "Không thể kết nối Student/Thesis Service"
                    )
            );

        } catch (SQLException e) {

            System.err.println(
                    "[Registration] Database error: "
                            + e.getMessage()
            );

            return internalServerError(
                    createJson(
                            "error",
                            "Lỗi cơ sở dữ liệu"
                    )
            );
        }
    }

    // DELETE /api/registrations/:maSV/:maDT
    public Result delete(
            String maSV,
            String maDT
    ) {

        System.out.println(
                "[Registration] DELETE /api/registrations/"
                        + maSV + "/" + maDT
        );

        if (maSV == null ||
                maSV.trim().isEmpty() ||
                maDT == null ||
                maDT.trim().isEmpty()) {

            return badRequest(
                    createJson(
                            "error",
                            "MaSV và MaDT không được để trống"
                    )
            );
        }

        try {

            boolean deleted =
                    registrationService.delete(
                            maSV,
                            maDT
                    );

            if (!deleted) {

                return notFound(
                        createJson(
                                "error",
                                "Không tìm thấy đăng ký"
                        )
                );
            }

            return ok(
                    createJson(
                            "message",
                            "Đã xóa đăng ký thành công"
                    )
            );

        } catch (SQLException e) {

            System.err.println(
                    "[Registration] Database error: "
                            + e.getMessage()
            );

            return internalServerError(
                    createJson(
                            "error",
                            "Lỗi cơ sở dữ liệu"
                    )
            );
        }
    }

    private ObjectNode createJson(
            String key,
            String value
    ) {

        ObjectNode json =
                Json.newObject();

        json.put(key, value);

        return json;
    }
}