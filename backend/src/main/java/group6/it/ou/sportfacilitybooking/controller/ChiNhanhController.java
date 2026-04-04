package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import group6.it.ou.sportfacilitybooking.dto.ChiNhanhDTO;
import group6.it.ou.sportfacilitybooking.service.ChiNhanhService;

@RestController
@RequestMapping("/api/facilities")
public class ChiNhanhController {

    @Autowired
    private ChiNhanhService service;

    @GetMapping
    public List<ChiNhanhDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ChiNhanhDTO getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public ChiNhanhDTO create(@RequestBody ChiNhanhDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public ChiNhanhDTO update(@PathVariable Integer id, @RequestBody ChiNhanhDTO dto) {
        return service.update(id, dto);
    }
}
