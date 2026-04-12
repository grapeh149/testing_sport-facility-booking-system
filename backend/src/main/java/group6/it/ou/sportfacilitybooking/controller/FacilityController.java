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

import group6.it.ou.sportfacilitybooking.dto.CourtCreateRequest;
import group6.it.ou.sportfacilitybooking.service.FacilityService;

@RestController
@RequestMapping("/api/chinhanh")
public class FacilityController {

    @Autowired
    private FacilityService service;

    @GetMapping
    public List<CourtCreateRequest> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CourtCreateRequest getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public CourtCreateRequest create(@RequestBody CourtCreateRequest dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CourtCreateRequest update(@PathVariable Integer id, @RequestBody CourtCreateRequest dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
