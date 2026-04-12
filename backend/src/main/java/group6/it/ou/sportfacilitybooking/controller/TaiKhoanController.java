package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import group6.it.ou.sportfacilitybooking.dto.TaiKhoanDTO;
import group6.it.ou.sportfacilitybooking.service.TaiKhoanService;

@RestController
@RequestMapping("/api/taikhoan")
public class TaiKhoanController {

    @Autowired
    private TaiKhoanService service;

    @GetMapping
    public List<TaiKhoanDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public TaiKhoanDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping("/username/{tenTaiKhoan}")
    public TaiKhoanDTO getByTenTaiKhoan(@PathVariable String tenTaiKhoan) {
        return service.getByTenTaiKhoan(tenTaiKhoan);
    }

    @GetMapping("/role/{role}")
    public List<TaiKhoanDTO> getByRole(@PathVariable String role) {
        return service.getByRole(role);
    }

    @PostMapping
    public TaiKhoanDTO create(@RequestBody TaiKhoanDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public TaiKhoanDTO update(@PathVariable Integer id, @RequestBody TaiKhoanDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}