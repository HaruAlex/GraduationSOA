package models;

public class Student {
    private String maSV;
    private String hoTen;
    private String email;
    private String lop;

    public Student() {}

    public Student(String maSV, String hoTen, String email, String lop) {
        this.maSV = maSV;
        this.hoTen = hoTen;
        this.email = email;
        this.lop = lop;
    }

    public String getMaSV() { return maSV; }
    public void setMaSV(String maSV) { this.maSV = maSV; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLop() { return lop; }
    public void setLop(String lop) { this.lop = lop; }
}