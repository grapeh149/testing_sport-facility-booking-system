package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import group6.it.ou.sportfacilitybooking.dto.SanTheThaoDTO;
import group6.it.ou.sportfacilitybooking.service.SanTheThaoService;

@RestController
public class SanTheThaoController {

    @Autowired
    private SanTheThaoService service;

    @GetMapping("/api/facilities/{facilityId}/courts")
    public List<SanTheThaoDTO> getByFacility(@PathVariable Integer facilityId) {
        return service.getByFacilityId(facilityId);
    }

    @PostMapping("/api/facilities/{facilityId}/courts")
    public SanTheThaoDTO create(@PathVariable Integer facilityId, @RequestBody SanTheThaoDTO dto) {
        return service.createForFacility(facilityId, dto);
    }

    @PutMapping("/api/courts/{id}")
    public SanTheThaoDTO update(@PathVariable Integer id, @RequestBody SanTheThaoDTO dto) {
        return service.update(id, dto);
    }
}
