package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import group6.it.ou.sportfacilitybooking.dto.NhanVienDTO;
import group6.it.ou.sportfacilitybooking.service.PaymentService;

@RestController
@RequestMapping("/api/nhanvien")
public class CheckInController {

    @Autowired
    private PaymentService service;

    @GetMapping
    public List<NhanVienDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public NhanVienDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public NhanVienDTO create(@RequestBody NhanVienDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public NhanVienDTO update(@PathVariable Integer id, @RequestBody NhanVienDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}