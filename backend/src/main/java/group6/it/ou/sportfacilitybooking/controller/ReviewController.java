package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import group6.it.ou.sportfacilitybooking.dto.ReviewDTO;
import group6.it.ou.sportfacilitybooking.service.ReviewService;

@RestController
@RequestMapping("/api/review")
public class ReviewController {

    @Autowired
    private ReviewService service;

    @GetMapping
    public List<ReviewDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ReviewDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @GetMapping("/san/{maSan}")
    public List<ReviewDTO> getBySan(@PathVariable Integer maSan) {
        return service.getBySan(maSan);
    }

    @GetMapping("/khach-hang/{maKH}")
    public List<ReviewDTO> getByKhachHang(@PathVariable Integer maKH) {
        return service.getByKhachHang(maKH);
    }

    @PostMapping
    public ReviewDTO create(@RequestBody ReviewDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ReviewDTO update(@PathVariable Integer id, @RequestBody ReviewDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}