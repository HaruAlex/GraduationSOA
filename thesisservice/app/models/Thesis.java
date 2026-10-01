package models;

public class Thesis {

    private String maDT;
    private String tenDT;
    private String giangVien;
    private int soLuongToiDa;

    public Thesis() {
    }

    public Thesis(String maDT, String tenDT, String giangVien, int soLuongToiDa) {
        this.maDT = maDT;
        this.tenDT = tenDT;
        this.giangVien = giangVien;
        this.soLuongToiDa = soLuongToiDa;
    }

    public String getMaDT() {
        return maDT;
    }

    public void setMaDT(String maDT) {
        this.maDT = maDT;
    }

    public String getTenDT() {
        return tenDT;
    }

    public void setTenDT(String tenDT) {
        this.tenDT = tenDT;
    }

    public String getGiangVien() {
        return giangVien;
    }

    public void setGiangVien(String giangVien) {
        this.giangVien = giangVien;
    }

    public int getSoLuongToiDa() {
        return soLuongToiDa;
    }

    public void setSoLuongToiDa(int soLuongToiDa) {
        this.soLuongToiDa = soLuongToiDa;
    }
}