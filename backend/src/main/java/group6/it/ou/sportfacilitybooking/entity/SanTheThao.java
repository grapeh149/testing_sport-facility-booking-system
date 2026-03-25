package group6.it.ou.sportfacilitybooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "SanTheThao")
public class SanTheThao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaSan")
    private Integer maSan;

    @Column(name = "MaLoaiSan")
    private String maLoaiSan;

    @Column(name = "MaChiNhanh")
    private Integer maChiNhanh;

    @Column(name = "SoSan")
    private Integer soSan;

    @Column(name = "GhiChu")
    private String ghiChu;

    @Column(name = "HinhAnh")
    private String hinhAnh;

    // Getters & Setters
    public Integer getMaSan() { return maSan; }
    public void setMaSan(Integer maSan) { this.maSan = maSan; }

    public String getMaLoaiSan() { return maLoaiSan; }
    public void setMaLoaiSan(String maLoaiSan) { this.maLoaiSan = maLoaiSan; }

    public Integer getMaChiNhanh() { return maChiNhanh; }
    public void setMaChiNhanh(Integer maChiNhanh) { this.maChiNhanh = maChiNhanh; }

    public Integer getSoSan() { return soSan; }
    public void setSoSan(Integer soSan) { this.soSan = soSan; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
}
