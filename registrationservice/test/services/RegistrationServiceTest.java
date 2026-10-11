package services;

import clients.StudentClient;
import clients.ThesisClient;
import com.fasterxml.jackson.databind.JsonNode;
import models.Registration;
import org.junit.Before;
import org.junit.Test;
import repositories.RegistrationRepository;
import play.libs.Json;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;


/*
 * ============================================================
 * FAKE REGISTRATION REPOSITORY
 * ============================================================
 *
 * Dùng Map để lưu dữ liệu trên RAM.
 * Không kết nối SQL Server khi chạy unit test.
 */
class FakeRegistrationRepository extends RegistrationRepository {

    private final Map<String, Registration> db =
            new HashMap<>();

    public FakeRegistrationRepository() {
        super();
    }

    private String createKey(
            String maSV,
            String maDT) {

        return maSV + "-" + maDT;
    }

    @Override
    public List<Registration> findAll()
            throws SQLException {

        return new ArrayList<>(db.values());
    }

    @Override
    public boolean exists(
            String maSV,
            String maDT)
            throws SQLException {

        return db.containsKey(
                createKey(maSV, maDT)
        );
    }

    @Override
    public int countByThesis(
            String maDT)
            throws SQLException {

        int count = 0;

        for (Registration registration : db.values()) {

            if (maDT.equals(
                    registration.getMaDT())) {

                count++;
            }
        }

        return count;
    }

    @Override
    public void create(
            String maSV,
            String maDT)
            throws SQLException {

        Registration registration =
                new Registration(
                        maSV,
                        maDT,
                        "2026-10-04"
                );

        db.put(
                createKey(maSV, maDT),
                registration
        );
    }

    @Override
    public boolean delete(
            String maSV,
            String maDT)
            throws SQLException {

        return db.remove(
                createKey(maSV, maDT)
        ) != null;
    }

    public void add(
            String maSV,
            String maDT) {

        Registration registration =
                new Registration(
                        maSV,
                        maDT,
                        "2026-10-04"
                );

        db.put(
                createKey(maSV, maDT),
                registration
        );
    }

    public void clear() {
        db.clear();
    }
}


/*
 * ============================================================
 * FAKE STUDENT CLIENT
 * ============================================================
 *
 * Không gọi HTTP thật tới Student Service :9001.
 */
class FakeStudentClient extends StudentClient {

    private boolean studentExists = true;

    public void setStudentExists(
            boolean studentExists) {

        this.studentExists = studentExists;
    }

    @Override
    public JsonNode getStudent(
            String maSV)
            throws IOException,
            InterruptedException {

        if (!studentExists) {
            return null;
        }

        return Json.parse(
                "{"
                        + "\"maSV\":\"" + maSV + "\","
                        + "\"hoTen\":\"Nguyen Van A\","
                        + "\"email\":\"student@gmail.com\","
                        + "\"lop\":\"CNTT01\""
                        + "}"
        );
    }
}


/*
 * ============================================================
 * FAKE THESIS CLIENT
 * ============================================================
 *
 * Không gọi HTTP thật tới Thesis Service :9002.
 */
class FakeThesisClient extends ThesisClient {

    private boolean thesisExists = true;

    private int soLuongToiDa = 2;

    public void setThesisExists(
            boolean thesisExists) {

        this.thesisExists = thesisExists;
    }

    public void setSoLuongToiDa(
            int soLuongToiDa) {

        this.soLuongToiDa =
                soLuongToiDa;
    }

    @Override
    public JsonNode getThesis(
            String maDT)
            throws IOException,
            InterruptedException {

        if (!thesisExists) {
            return null;
        }

        return Json.parse(
                "{"
                        + "\"maDT\":\"" + maDT + "\","
                        + "\"tenDT\":\"He thong quan ly do an\","
                        + "\"giangVien\":\"Nguyen Van A\","
                        + "\"soLuongToiDa\":"
                        + soLuongToiDa
                        + "}"
        );
    }
}


/*
 * ============================================================
 * REGISTRATION SERVICE TEST
 * ============================================================
 */
public class RegistrationServiceTest {

    private FakeRegistrationRepository fakeRepository;

    private FakeStudentClient fakeStudentClient;

    private FakeThesisClient fakeThesisClient;

    private RegistrationService registrationService;


    /*
     * Chạy trước mỗi test.
     */
    @Before
    public void setUp() {

        fakeRepository =
                new FakeRegistrationRepository();

        fakeStudentClient =
                new FakeStudentClient();

        fakeThesisClient =
                new FakeThesisClient();

        registrationService =
                new RegistrationService(
                        fakeRepository,
                        fakeStudentClient,
                        fakeThesisClient
                );
    }


    /*
     * ========================================================
     * TEST 1
     * Lấy danh sách đăng ký.
     * ========================================================
     */
    @Test
    public void testGetAllRegistrations()
            throws SQLException {

        fakeRepository.add(
                "SV01",
                "DT01"
        );

        fakeRepository.add(
                "SV02",
                "DT02"
        );

        List<Registration> result =
                registrationService.getAll();

        assertEquals(
                2,
                result.size()
        );
    }


    /*
     * ========================================================
     * TEST 2
     * Tạo đăng ký thành công.
     * ========================================================
     */
    @Test
    public void testCreateRegistration_Success()
            throws IOException,
            InterruptedException,
            SQLException {

        String result =
                registrationService.create(
                        "SV01",
                        "DT01"
                );

        assertEquals(
                "SUCCESS",
                result
        );

        assertTrue(
                fakeRepository.exists(
                        "SV01",
                        "DT01"
                )
        );
    }


    /*
     * ========================================================
     * TEST 3
     * Sinh viên không tồn tại.
     * ========================================================
     */
    @Test
    public void testCreateRegistration_StudentNotFound()
            throws IOException,
            InterruptedException,
            SQLException {

        fakeStudentClient.setStudentExists(
                false
        );

        String result =
                registrationService.create(
                        "SV99",
                        "DT01"
                );

        assertEquals(
                "STUDENT_NOT_FOUND",
                result
        );

        assertFalse(
                fakeRepository.exists(
                        "SV99",
                        "DT01"
                )
        );
    }


    /*
     * ========================================================
     * TEST 4
     * Đề tài không tồn tại.
     * ========================================================
     */
    @Test
    public void testCreateRegistration_ThesisNotFound()
            throws IOException,
            InterruptedException,
            SQLException {

        fakeThesisClient.setThesisExists(
                false
        );

        String result =
                registrationService.create(
                        "SV01",
                        "DT99"
                );

        assertEquals(
                "THESIS_NOT_FOUND",
                result
        );

        assertFalse(
                fakeRepository.exists(
                        "SV01",
                        "DT99"
                )
        );
    }


    /*
     * ========================================================
     * TEST 5
     * Đăng ký bị trùng.
     * ========================================================
     */
    @Test
    public void testCreateRegistration_Duplicate()
            throws IOException,
            InterruptedException,
            SQLException {

        fakeRepository.add(
                "SV01",
                "DT01"
        );

        String result =
                registrationService.create(
                        "SV01",
                        "DT01"
                );

        assertEquals(
                "DUPLICATE",
                result
        );
    }


    /*
     * ========================================================
     * TEST 6
     * Đề tài đã đủ số lượng.
     * ========================================================
     */
    @Test
    public void testCreateRegistration_ThesisFull()
            throws IOException,
            InterruptedException,
            SQLException {

        /*
         * DT01 tối đa 2 sinh viên.
         */

        fakeThesisClient.setSoLuongToiDa(
                2
        );

        fakeRepository.add(
                "SV01",
                "DT01"
        );

        fakeRepository.add(
                "SV02",
                "DT01"
        );

        String result =
                registrationService.create(
                        "SV03",
                        "DT01"
                );

        assertEquals(
                "THESIS_FULL",
                result
        );

        assertFalse(
                fakeRepository.exists(
                        "SV03",
                        "DT01"
                )
        );
    }


    /*
     * ========================================================
     * TEST 7
     * Đăng ký khi đề tài còn chỗ.
     * ========================================================
     */
    @Test
    public void testCreateRegistration_WhenThesisHasSpace()
            throws IOException,
            InterruptedException,
            SQLException {

        fakeThesisClient.setSoLuongToiDa(
                2
        );

        fakeRepository.add(
                "SV01",
                "DT01"
        );

        String result =
                registrationService.create(
                        "SV02",
                        "DT01"
                );

        assertEquals(
                "SUCCESS",
                result
        );

        assertTrue(
                fakeRepository.exists(
                        "SV02",
                        "DT01"
                )
        );
    }


    /*
     * ========================================================
     * TEST 8
     * Xóa đăng ký thành công.
     * ========================================================
     */
    @Test
    public void testDeleteRegistration_Success()
            throws SQLException {

        fakeRepository.add(
                "SV01",
                "DT01"
        );

        boolean result =
                registrationService.delete(
                        "SV01",
                        "DT01"
                );

        assertTrue(result);

        assertFalse(
                fakeRepository.exists(
                        "SV01",
                        "DT01"
                )
        );
    }


    /*
     * ========================================================
     * TEST 9
     * Xóa đăng ký không tồn tại.
     * ========================================================
     */
    @Test
    public void testDeleteRegistration_NotFound()
            throws SQLException {

        boolean result =
                registrationService.delete(
                        "SV99",
                        "DT99"
                );

        assertFalse(result);
    }


    /*
     * ========================================================
     * TEST 10
     * Kiểm tra số lượng đăng ký của đề tài.
     *
     * Đây là test gián tiếp thông qua logic create().
     * ========================================================
     */
    @Test
    public void testRegistrationCapacity()
            throws IOException,
            InterruptedException,
            SQLException {

        fakeThesisClient.setSoLuongToiDa(
                3
        );

        fakeRepository.add(
                "SV01",
                "DT01"
        );

        fakeRepository.add(
                "SV02",
                "DT01"
        );

        String result =
                registrationService.create(
                        "SV03",
                        "DT01"
                );

        assertEquals(
                "SUCCESS",
                result
        );

        assertTrue(
                fakeRepository.exists(
                        "SV03",
                        "DT01"
                )
        );
    }
}