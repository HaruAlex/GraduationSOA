package controllers;

import models.Thesis;
import org.junit.Before;
import org.junit.Test;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import repositories.ThesisRepository;
import services.ThesisService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.CONFLICT;
import static play.mvc.Http.Status.CREATED;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.NO_CONTENT;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.fakeRequest;

public class ThesisControllerTest {

    private FakeThesisRepository repository;
    private ThesisController controller;

    @Before
    public void setUp() {
        repository = new FakeThesisRepository();
        controller = new ThesisController(new ThesisService(repository));
    }

    @Test
    public void getAllReturnsOkAndThesisList() throws Exception {
        repository.data.add(new Thesis("DT01", "He thong quan ly", "Nguyen Van A", 4));

        Result result = controller.getAll();

        assertEquals(OK, result.status());
        assertEquals(1, Json.parse(contentAsString(result)).size());
    }

    @Test
    public void getByIdReturnsOkWhenThesisExists() throws Exception {
        repository.data.add(new Thesis("DT01", "He thong quan ly", "Nguyen Van A", 4));

        Result result = controller.getById("DT01");

        assertEquals(OK, result.status());
        assertEquals("DT01", Json.parse(contentAsString(result)).get("maDT").asText());
    }

    @Test
    public void getByIdReturnsNotFoundWhenThesisDoesNotExist() throws Exception {
        Result result = controller.getById("KHONG_CO");

        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void createReturnsBadRequestWhenMaDTIsEmpty() {
        Result result = controller.create(jsonRequest("maDT", " ", "tenDT", "De tai mau",
                "giangVien", "Nguyen Van A", "soLuongToiDa", 3));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void createReturnsBadRequestWhenTenDTIsEmpty() {
        Result result = controller.create(jsonRequest("maDT", "DT01", "tenDT", " ",
                "giangVien", "Nguyen Van A", "soLuongToiDa", 3));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void createReturnsBadRequestWhenSoLuongToiDaIsNotPositive() {
        Result result = controller.create(jsonRequest("maDT", "DT01", "tenDT", "De tai mau",
                "giangVien", "Nguyen Van A", "soLuongToiDa", 0));

        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void createReturnsConflictWhenMaDTAlreadyExists() throws Exception {
        repository.data.add(new Thesis("DT01", "De tai cu", "Nguyen Van A", 3));

        Result result = controller.create(jsonRequest("maDT", "DT01", "tenDT", "De tai moi",
                "giangVien", "Nguyen Van B", "soLuongToiDa", 4));

        assertEquals(CONFLICT, result.status());
    }

    @Test
    public void createReturnsCreatedWhenInputIsValid() throws Exception {
        Result result = controller.create(jsonRequest("maDT", "DT01", "tenDT", "He thong quan ly",
                "giangVien", "Nguyen Van A", "soLuongToiDa", 4));

        assertEquals(CREATED, result.status());
        assertNotNull(repository.findById("DT01"));
    }

    @Test
    public void updateReturnsNotFoundWhenThesisDoesNotExist() {
        Result result = controller.update("KHONG_CO",
                jsonRequest("tenDT", "De tai moi", "giangVien", "Nguyen Van B",
                        "soLuongToiDa", 3));

        assertEquals(NOT_FOUND, result.status());
    }

    @Test
    public void updateReturnsOkWhenThesisExists() throws Exception {
        repository.data.add(new Thesis("DT01", "De tai cu", "Nguyen Van A", 3));

        Result result = controller.update("DT01",
                jsonRequest("tenDT", "De tai moi", "giangVien", "Nguyen Van B",
                        "soLuongToiDa", 5));

        assertEquals(OK, result.status());
        assertEquals("De tai moi", Json.parse(contentAsString(result)).get("tenDT").asText());
    }

    @Test
    public void deleteReturnsNoContentWhenThesisExists() throws Exception {
        repository.data.add(new Thesis("DT01", "De tai mau", "Nguyen Van A", 3));

        Result result = controller.delete("DT01");

        assertEquals(NO_CONTENT, result.status());
        assertEquals(null, repository.findById("DT01"));
    }

    private Http.Request jsonRequest(Object... fields) {
        com.fasterxml.jackson.databind.node.ObjectNode json = Json.newObject();
        for (int i = 0; i < fields.length; i += 2) {
            String key = (String) fields[i];
            Object value = fields[i + 1];
            if (value instanceof String) {
                json.put(key, (String) value);
            } else if (value instanceof Integer) {
                json.put(key, (Integer) value);
            }
        }
        return fakeRequest().bodyJson(json).build();
    }

    /**
     * Repository giả lưu dữ liệu trong RAM để test không cần SQL Server.
     */
    private static class FakeThesisRepository extends ThesisRepository {
        private final List<Thesis> data = new ArrayList<>();

        FakeThesisRepository() {
            super(null);
        }

        @Override
        public List<Thesis> findAll() throws SQLException {
            return new ArrayList<>(data);
        }

        @Override
        public Thesis findById(String maDT) throws SQLException {
            for (Thesis thesis : data) {
                if (thesis.getMaDT().equals(maDT)) {
                    return thesis;
                }
            }
            return null;
        }

        @Override
        public boolean exists(String maDT) throws SQLException {
            return findById(maDT) != null;
        }

        @Override
        public void create(Thesis thesis) throws SQLException {
            data.add(thesis);
        }

        @Override
        public boolean update(String maDT, Thesis thesis) throws SQLException {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getMaDT().equals(maDT)) {
                    data.set(i, thesis);
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean delete(String maDT) throws SQLException {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getMaDT().equals(maDT)) {
                    data.remove(i);
                    return true;
                }
            }
            return false;
        }
    }
}
