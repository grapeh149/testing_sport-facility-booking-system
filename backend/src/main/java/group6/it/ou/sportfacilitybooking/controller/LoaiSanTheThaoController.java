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

import group6.it.ou.sportfacilitybooking.dto.LoaiSanTheThaoDTO;
import group6.it.ou.sportfacilitybooking.service.LoaiSanTheThaoService;

@RestController
@RequestMapping("/api/loaisanthethao")
public class LoaiSanTheThaoController {

    @Autowired
    private LoaiSanTheThaoService service;

    @GetMapping
    public List<LoaiSanTheThaoDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public LoaiSanTheThaoDTO getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public LoaiSanTheThaoDTO create(@RequestBody LoaiSanTheThaoDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    public LoaiSanTheThaoDTO update(@PathVariable String id, @RequestBody LoaiSanTheThaoDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
