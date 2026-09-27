package repositories;

import models.Thesis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThesisRepository {

    private static final String URL =
    "jdbc:sqlserver://localhost:1433;databaseName=GraduationSOA;encrypt=true;trustServerCertificate=true";

private static final String USER = "sa";

private static final String PASSWORD = "Xuanthuđê";

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public List<Thesis> findAll() throws SQLException {
        List<Thesis> theses = new ArrayList<>();

        String sql = "SELECT MaDT, TenDT, GiangVien, SoLuongToiDa FROM DETAI";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                Thesis thesis = new Thesis();

                thesis.setMaDT(rs.getString("MaDT"));
                thesis.setTenDT(rs.getString("TenDT"));
                thesis.setGiangVien(rs.getString("GiangVien"));
                thesis.setSoLuongToiDa(rs.getInt("SoLuongToiDa"));

                theses.add(thesis);
            }
        }

        return theses;
    }

    public Thesis findById(String maDT) throws SQLException {

        String sql = "SELECT MaDT, TenDT, GiangVien, SoLuongToiDa " +
                     "FROM DETAI WHERE MaDT = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, maDT);

            try (ResultSet rs = statement.executeQuery()) {

                if (rs.next()) {
                    return new Thesis(
                            rs.getString("MaDT"),
                            rs.getString("TenDT"),
                            rs.getString("GiangVien"),
                            rs.getInt("SoLuongToiDa")
                    );
                }
            }
        }

        return null;
    }

    public boolean exists(String maDT) throws SQLException {
        return findById(maDT) != null;
    }

    public void create(Thesis thesis) throws SQLException {

        String sql = "INSERT INTO DETAI " +
                     "(MaDT, TenDT, GiangVien, SoLuongToiDa) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, thesis.getMaDT());
            statement.setString(2, thesis.getTenDT());
            statement.setString(3, thesis.getGiangVien());
            statement.setInt(4, thesis.getSoLuongToiDa());

            statement.executeUpdate();
        }
    }

    public boolean update(String maDT, Thesis thesis) throws SQLException {

        String sql = "UPDATE DETAI " +
                     "SET TenDT = ?, GiangVien = ?, SoLuongToiDa = ? " +
                     "WHERE MaDT = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, thesis.getTenDT());
            statement.setString(2, thesis.getGiangVien());
            statement.setInt(3, thesis.getSoLuongToiDa());
            statement.setString(4, maDT);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean delete(String maDT) throws SQLException {

        String sql = "DELETE FROM DETAI WHERE MaDT = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, maDT);

            return statement.executeUpdate() > 0;
        }
    }
}