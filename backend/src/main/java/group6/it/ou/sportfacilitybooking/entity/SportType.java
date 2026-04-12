package group6.it.ou.sportfacilitybooking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "LoaiSanTheThao")
public class SportType {

    @Id
    @Column(name = "MaLoaiSan")
    private String maLoaiSan;

    @Column(name = "TenLoaiSan")
    private String tenLoaiSan;

    @Column(name = "HinhAnh")
    private String hinhAnh;

    // Getters & Setters
    public String getMaLoaiSan() { return maLoaiSan; }
    public void setMaLoaiSan(String maLoaiSan) { this.maLoaiSan = maLoaiSan; }

    public String getTenLoaiSan() { return tenLoaiSan; }
    public void setTenLoaiSan(String tenLoaiSan) { this.tenLoaiSan = tenLoaiSan; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }
}
