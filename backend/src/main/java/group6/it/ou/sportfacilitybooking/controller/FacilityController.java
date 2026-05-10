package group6.it.ou.sportfacilitybooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import group6.it.ou.sportfacilitybooking.dto.FacilityDTO;
import group6.it.ou.sportfacilitybooking.dto.FacilityCreateRequest;
import group6.it.ou.sportfacilitybooking.entity.FacilityStatus;
import group6.it.ou.sportfacilitybooking.service.FacilityService;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facilities")
@CrossOrigin(origins = "*")
public class FacilityController {

    @Autowired
    private FacilityService facilityService;

    @GetMapping
    public ApiResponse<Map<String, Object>> searchFacilities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String search) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<FacilityDTO> result = facilityService.searchFacilitiesWithPagination(search, pageable);

            Map<String, Object> responseData = new LinkedHashMap<>();
            responseData.put("content", result.getContent());
            responseData.put("currentPage", result.getNumber());
            responseData.put("totalPages", result.getTotalPages());
            responseData.put("totalElements", result.getTotalElements());
            responseData.put("hasNextPage", result.hasNext());
            responseData.put("hasPreviousPage", result.hasPrevious());

            return new ApiResponse<>(true, responseData, "Tìm kiếm sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<FacilityDTO> getFacilityDetails(@PathVariable Long id) {
        try {
            FacilityDTO result = facilityService.getFacilityDetails(id);
            return new ApiResponse<>(true, result, "Lấy thông tin sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<FacilityDTO>> getFacilitiesByOwner(@PathVariable Long ownerId) {
        try {
            List<FacilityDTO> result = facilityService.getFacilitiesByOwner(ownerId);
            return new ApiResponse<>(true, result, "Lấy danh sách sân của chủ sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @GetMapping("/admin/all")
    public ApiResponse<Map<String, Object>> getFacilitiesForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            FacilityStatus statusFilter = null;
            if (status != null && !status.trim().isEmpty()) {
                statusFilter = FacilityStatus.valueOf(status.trim().toUpperCase());
            }

            Page<FacilityDTO> result = facilityService.getFacilitiesForAdmin(pageable, statusFilter);

            Map<String, Object> responseData = new LinkedHashMap<>();
            responseData.put("content", result.getContent());
            responseData.put("currentPage", result.getNumber());
            responseData.put("totalPages", result.getTotalPages());
            responseData.put("totalElements", result.getTotalElements());
            responseData.put("hasNextPage", result.hasNext());
            responseData.put("hasPreviousPage", result.hasPrevious());

            return new ApiResponse<>(true, responseData, "Lấy danh sách sân cho admin thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<FacilityDTO> createFacility(@Valid @RequestBody FacilityCreateRequest request,
            @RequestParam Long ownerId) {
        try {
            FacilityDTO result = facilityService.createFacility(request, ownerId);
            return new ApiResponse<>(true, result, "Tạo sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<FacilityDTO> updateFacility(@PathVariable Long id,
            @Valid @RequestBody FacilityCreateRequest request) {
        try {
            FacilityDTO result = facilityService.updateFacility(id, request);
            return new ApiResponse<>(true, result, "Cập nhật sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PatchMapping("/{id}/cover-image")
    public ApiResponse<FacilityDTO> updateFacilityCoverImage(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String coverImageUrl = request.get("coverImageUrl");
            if (coverImageUrl == null || coverImageUrl.trim().isEmpty()) {
                return new ApiResponse<>(false, null, "URL ảnh bìa không được để trống");
            }
            FacilityDTO result = facilityService.updateFacilityCoverImage(id, coverImageUrl);
            return new ApiResponse<>(true, result, "Cập nhật ảnh bìa thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteFacility(@PathVariable Long id) {
        try {
            facilityService.deleteFacility(id);
            return new ApiResponse<>(true, null, "Xóa cơ sở thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<?> approveFacility(@PathVariable Long id, @RequestParam Long adminId) {
        try {
            facilityService.approveFacility(id, adminId);
            return new ApiResponse<>(true, null, "Phê duyệt sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PostMapping("/{id}/reject")
    public ApiResponse<?> rejectFacility(@PathVariable Long id, @RequestParam Long adminId,
            @RequestParam String reason) {
        try {
            facilityService.rejectFacility(id, adminId, reason);
            return new ApiResponse<>(true, null, "Từ chối sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
}
