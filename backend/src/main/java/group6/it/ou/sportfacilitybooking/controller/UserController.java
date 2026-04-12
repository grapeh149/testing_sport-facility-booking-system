package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.service.UserService;

@RestController
@RequestMapping("/api/taikhoan")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public List<UserDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping("/username/{tenTaiKhoan}")
    public UserDTO getByTenTaiKhoan(@PathVariable String tenTaiKhoan) {
        return service.getByTenTaiKhoan(tenTaiKhoan);
    }

    @GetMapping("/role/{role}")
    public List<UserDTO> getByRole(@PathVariable String role) {
        return service.getByRole(role);
    }

    @PostMapping
    public UserDTO create(@RequestBody UserDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public UserDTO update(@PathVariable Integer id, @RequestBody UserDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
        @PostMapping("/dang-ky")
    public UserDTO dangKy(@RequestBody UserDTO dto) {
        return service.dangKy(dto);
    }

    @PostMapping("/dang-nhap")
    public UserDTO dangNhap(@RequestBody UserDTO dto) {
        return service.dangNhap(dto);
    }
}
