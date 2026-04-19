package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import group6.it.ou.sportfacilitybooking.dto.ReviewDTO;
import group6.it.ou.sportfacilitybooking.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    @Autowired
    private ReviewService service;

    @GetMapping
    public List<ReviewDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ReviewDTO getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/facility/{facilityId}")
    public List<ReviewDTO> getByFacility(@PathVariable Long facilityId) {
        return service.getByFacility(facilityId);
    }

    @GetMapping("/user/{userId}")
    public List<ReviewDTO> getByUser(@PathVariable Long userId) {
        return service.getByUser(userId);
    }

    @GetMapping("/booking/{bookingId}")
    public List<ReviewDTO> getByBooking(@PathVariable Long bookingId) {
        return service.getByBooking(bookingId);
    }

    @PostMapping
    public ReviewDTO create(@RequestBody ReviewDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ReviewDTO update(@PathVariable Long id, @RequestBody ReviewDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
