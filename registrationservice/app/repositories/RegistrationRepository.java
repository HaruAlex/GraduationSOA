package repositories;

import models.Registration;

import javax.inject.Singleton;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class RegistrationRepository {

   private static final String URL =
    "jdbc:sqlserver://localhost:1433;" +
    "databaseName=GraduationSOA;" +
    "user=sa;" +
    "password=123456;" +
    "encrypt=true;" +
    "trustServerCertificate=true";

    public RegistrationRepository() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "Không tìm thấy SQL Server JDBC Driver", e
            );
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public List<Registration> findAll() throws SQLException {

        List<Registration> registrations = new ArrayList<>();

        String sql = """
                SELECT MaSV, MaDT, NgayDangKy
                FROM DANGKY
                """;

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                Registration registration = new Registration();

                registration.setMaSV(resultSet.getString("MaSV"));
                registration.setMaDT(resultSet.getString("MaDT"));

                Timestamp timestamp =
                        resultSet.getTimestamp("NgayDangKy");

                if (timestamp != null) {
                    registration.setNgayDangKy(timestamp.toString());
                }

                registrations.add(registration);
            }
        }

        return registrations;
    }

    public boolean exists(String maSV, String maDT) throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM DANGKY
                WHERE MaSV = ? AND MaDT = ?
                """;

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, maSV);
            statement.setString(2, maDT);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public int countByThesis(String maDT) throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM DANGKY
                WHERE MaDT = ?
                """;

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, maDT);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        return 0;
    }

    public void create(String maSV, String maDT) throws SQLException {

        String sql = """
                INSERT INTO DANGKY(MaSV, MaDT, NgayDangKy)
                VALUES (?, ?, GETDATE())
                """;

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, maSV);
            statement.setString(2, maDT);

            statement.executeUpdate();
        }
    }

    public boolean delete(String maSV, String maDT) throws SQLException {

        String sql = """
                DELETE FROM DANGKY
                WHERE MaSV = ? AND MaDT = ?
                """;

        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, maSV);
            statement.setString(2, maDT);

            return statement.executeUpdate() > 0;
        }
    }
}