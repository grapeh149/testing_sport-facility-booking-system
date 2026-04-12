package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import group6.it.ou.sportfacilitybooking.dto.SportTypeDTO;
import group6.it.ou.sportfacilitybooking.service.SportTypeService;

@RestController
@RequestMapping("/api/loaisanthethao")
public class SportTypeController {

    @Autowired
    private SportTypeService service;

    @GetMapping
    public List<SportTypeDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SportTypeDTO getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public SportTypeDTO create(@RequestBody SportTypeDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public SportTypeDTO update(@PathVariable String id, @RequestBody SportTypeDTO dto) {
        return service.update(id, dto);
    }
}
