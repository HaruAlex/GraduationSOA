package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Thesis;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.ThesisService;

import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

public class ThesisController extends Controller {

	private final ThesisService service;
	@Inject
    public ThesisController(ThesisService service) {
        this.service = service;
    }

    public Result getAll() {
        try {
            List<Thesis> theses = service.getAll();
            return ok(Json.toJson(theses));
        } catch (SQLException e) {
            return internalServerError(
                    Json.newObject()
                            .put("message", "Lỗi database")
                            .put("error", e.getMessage())
            );
        }
    }

    public Result getById(String maDT) {
        try {
            Thesis thesis = service.getById(maDT);

            if (thesis == null) {
                return notFound(
                        Json.newObject()
                                .put("message", "Không tìm thấy đề tài")
                );
            }

            return ok(Json.toJson(thesis));

        } catch (SQLException e) {
            return internalServerError(
                    Json.newObject()
                            .put("message", "Lỗi database")
                            .put("error", e.getMessage())
            );
        }
    }

    public Result create(Http.Request request) {

        JsonNode json = request.body().asJson();

        if (json == null) {
            return badRequest(
                    Json.newObject()
                            .put("message", "Request body phải là JSON")
            );
        }

        String maDT = json.path("maDT").asText("").trim();
        String tenDT = json.path("tenDT").asText("").trim();
        String giangVien = json.path("giangVien").asText("").trim();

        if (maDT.isEmpty()) {
            return badRequest(
                    Json.newObject()
                            .put("message", "MaDT không được để trống")
            );
        }

        if (tenDT.isEmpty()) {
            return badRequest(
                    Json.newObject()
                            .put("message", "TenDT không được để trống")
            );
        }

        int soLuongToiDa = json.path("soLuongToiDa").asInt(0);

        if (soLuongToiDa <= 0) {
            return badRequest(
                    Json.newObject()
                            .put("message", "SoLuongToiDa phải lớn hơn 0")
            );
        }

        try {

            if (service.exists(maDT)) {
                return status(
                        CONFLICT,
                        Json.newObject()
                                .put("message", "MaDT đã tồn tại")
                );
            }

            Thesis thesis = new Thesis(
                    maDT,
                    tenDT,
                    giangVien,
                    soLuongToiDa
            );

            service.create(thesis);

            return created(Json.toJson(thesis));

        } catch (SQLException e) {

            return internalServerError(
                    Json.newObject()
                            .put("message", "Lỗi database")
                            .put("error", e.getMessage())
            );
        }
    }

    public Result update(String maDT, Http.Request request) {

        JsonNode json = request.body().asJson();

        if (json == null) {
            return badRequest(
                    Json.newObject()
                            .put("message", "Request body phải là JSON")
            );
        }

        String tenDT = json.path("tenDT").asText("").trim();
        String giangVien = json.path("giangVien").asText("").trim();

        if (tenDT.isEmpty()) {
            return badRequest(
                    Json.newObject()
                            .put("message", "TenDT không được để trống")
            );
        }

        int soLuongToiDa = json.path("soLuongToiDa").asInt(0);

        if (soLuongToiDa <= 0) {
            return badRequest(
                    Json.newObject()
                            .put("message", "SoLuongToiDa phải lớn hơn 0")
            );
        }

        try {

            if (!service.exists(maDT)) {
                return notFound(
                        Json.newObject()
                                .put("message", "Không tìm thấy đề tài")
                );
            }

            Thesis thesis = new Thesis(
                    maDT,
                    tenDT,
                    giangVien,
                    soLuongToiDa
            );

            boolean updated = service.update(maDT, thesis);

            if (!updated) {
                return notFound(
                        Json.newObject()
                                .put("message", "Không thể cập nhật đề tài")
                );
            }

            return ok(Json.toJson(thesis));

        } catch (SQLException e) {

            return internalServerError(
                    Json.newObject()
                            .put("message", "Lỗi database")
                            .put("error", e.getMessage())
            );
        }
    }

    public Result delete(String maDT) {

        try {

            if (!service.exists(maDT)) {
                return notFound(
                        Json.newObject()
                                .put("message", "Không tìm thấy đề tài")
                );
            }

            boolean deleted = service.delete(maDT);

            if (!deleted) {
                return notFound(
                        Json.newObject()
                                .put("message", "Không thể xóa đề tài")
                );
            }

            return noContent();

        } catch (SQLException e) {

            return internalServerError(
                    Json.newObject()
                            .put("message", "Lỗi database")
                            .put("error", e.getMessage())
            );
        }
    }
}