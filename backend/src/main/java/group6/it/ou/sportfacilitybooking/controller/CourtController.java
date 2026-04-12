package group6.it.ou.sportfacilitybooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import group6.it.ou.sportfacilitybooking.dto.CourtDTO;
import group6.it.ou.sportfacilitybooking.dto.CourtCreateRequest;
import group6.it.ou.sportfacilitybooking.service.CourtService;
import java.util.List;

@RestController
@RequestMapping("/api/courts")
@CrossOrigin(origins = "*")
public class CourtController {
    
    @Autowired
    private CourtService courtService;
    
    @GetMapping("/facility/{facilityId}")
    public ApiResponse<List<CourtDTO>> getCourtsByFacility(@PathVariable Long facilityId) {
        try {
            List<CourtDTO> result = courtService.getCourtsByFacility(facilityId);
            return new ApiResponse<>(true, result, "Lấy danh sách sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ApiResponse<CourtDTO> getCourtDetails(@PathVariable Long id) {
        try {
            CourtDTO result = courtService.getCourtDetails(id);
            return new ApiResponse<>(true, result, "Lấy thông tin sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    @PostMapping
    public ApiResponse<CourtDTO> createCourt(@Valid @RequestBody CourtCreateRequest request) {
        try {
            CourtDTO result = courtService.createCourt(request);
            return new ApiResponse<>(true, result, "Tạo sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ApiResponse<CourtDTO> updateCourt(@PathVariable Long id, @RequestBody CourtCreateRequest request) {
        try {
            CourtDTO result = courtService.updateCourt(id, request);
            return new ApiResponse<>(true, result, "Cập nhật sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteCourt(@PathVariable Long id) {
        try {
            courtService.deleteCourt(id);
            return new ApiResponse<>(true, null, "Xóa sân chơi thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    // Search courts with filters
    @GetMapping("/search")
    public ApiResponse<List<CourtDTO>> searchCourts(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) Long sportTypeId) {
        try {
            List<CourtDTO> result = courtService.searchCourts(address, sportTypeId);
            return new ApiResponse<>(true, result, "Tìm kiếm sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
}
