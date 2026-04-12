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

import group6.it.ou.sportfacilitybooking.dto.CourtDTO;
import group6.it.ou.sportfacilitybooking.service.CourtService;

@RestController
@RequestMapping("/api/santhethao")
public class CourtController {

    @Autowired
    private CourtService service;

    @GetMapping
    public List<CourtDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public CourtDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public CourtDTO create(@RequestBody CourtDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public CourtDTO update(@PathVariable Integer id, @RequestBody CourtDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
