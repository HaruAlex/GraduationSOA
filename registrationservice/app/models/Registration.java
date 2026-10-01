package models;

public class Registration {

    private String maSV;
    private String maDT;
    private String ngayDangKy;

    public Registration() {
    }

    public Registration(String maSV, String maDT, String ngayDangKy) {
        this.maSV = maSV;
        this.maDT = maDT;
        this.ngayDangKy = ngayDangKy;
    }

    public String getMaSV() {
        return maSV;
    }

    public void setMaSV(String maSV) {
        this.maSV = maSV;
    }

    public String getMaDT() {
        return maDT;
    }

    public void setMaDT(String maDT) {
        this.maDT = maDT;
    }

    public String getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(String ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }
}