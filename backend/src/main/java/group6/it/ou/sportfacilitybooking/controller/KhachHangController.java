package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import group6.it.ou.sportfacilitybooking.dto.KhachHangDTO;
import group6.it.ou.sportfacilitybooking.service.KhachHangService;

@RestController
@RequestMapping("/api/khachhang")
public class KhachHangController {

    @Autowired
    private KhachHangService service;

    @GetMapping
    public List<KhachHangDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public KhachHangDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public KhachHangDTO create(@RequestBody KhachHangDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public KhachHangDTO update(@PathVariable Integer id, @RequestBody KhachHangDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
