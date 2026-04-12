package group6.it.ou.sportfacilitybooking.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TaiKhoan")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maTaiKhoan;
    private String tenTaiKhoan;
    private String matKhau;
    private String role;

    @ManyToOne
    @JoinColumn(name = "maNhanVien")
    private PaymentStatus nhanVien;

    @ManyToOne
    @JoinColumn(name = "maKH")
    private Payment khachHang;

    public Integer getMaTaiKhoan() { return maTaiKhoan; }
    public void setMaTaiKhoan(Integer maTaiKhoan) { this.maTaiKhoan = maTaiKhoan; }

    public String getTenTaiKhoan() { return tenTaiKhoan; }
    public void setTenTaiKhoan(String tenTaiKhoan) { this.tenTaiKhoan = tenTaiKhoan; }

    public String getMatKhau() { return matKhau; }
    public void setMatKhau(String matKhau) { this.matKhau = matKhau; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public PaymentStatus getNhanVien() { return nhanVien; }
    public void setNhanVien(PaymentStatus nhanVien) { this.nhanVien = nhanVien; }

    public Payment getKhachHang() { return khachHang; }
    public void setKhachHang(Payment khachHang) { this.khachHang = khachHang; }
}