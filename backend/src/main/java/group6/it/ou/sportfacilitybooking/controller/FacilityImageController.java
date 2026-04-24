package group6.it.ou.sportfacilitybooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import group6.it.ou.sportfacilitybooking.entity.FacilityImage;
import group6.it.ou.sportfacilitybooking.service.FacilityImageService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facility-images")
@CrossOrigin(origins = "*")
public class FacilityImageController {
    
    @Autowired
    private FacilityImageService facilityImageService;
    
    /**
     * Get all images for a facility
     */
    @GetMapping("/facility/{facilityId}")
    public ApiResponse<List<FacilityImage>> getImagesByFacility(@PathVariable Long facilityId) {
        try {
            List<FacilityImage> images = facilityImageService.getImagesByFacilityId(facilityId);
            return new ApiResponse<>(true, images, "Lấy danh sách ảnh thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    /**
     * Add a single image to facility
     */
    @PostMapping("/facility/{facilityId}")
    public ApiResponse<FacilityImage> addImage(
            @PathVariable Long facilityId,
            @RequestBody Map<String, Object> request) {
        try {
            String imageUrl = (String) request.get("imageUrl");
            Integer sortOrder = (Integer) request.get("sortOrder");
            
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                return new ApiResponse<>(false, null, "URL ảnh không được để trống");
            }
            
            FacilityImage image = facilityImageService.addImage(facilityId, imageUrl, sortOrder);
            return new ApiResponse<>(true, image, "Thêm ảnh thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    /**
     * Add multiple images to facility
     */
    @PostMapping("/facility/{facilityId}/bulk")
    public ApiResponse<List<FacilityImage>> addImages(
            @PathVariable Long facilityId,
            @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<String> imageUrls = (List<String>) request.get("imageUrls");
            
            if (imageUrls == null || imageUrls.isEmpty()) {
                return new ApiResponse<>(false, null, "Danh sách ảnh không được trống");
            }
            
            if (imageUrls.size() > 5) {
                return new ApiResponse<>(false, null, "Tối đa 5 ảnh mô tả");
            }
            
            List<FacilityImage> images = facilityImageService.addImages(facilityId, imageUrls);
            return new ApiResponse<>(true, images, "Thêm các ảnh thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    /**
     * Delete an image
     */
    @DeleteMapping("/{imageId}")
    public ApiResponse<?> deleteImage(@PathVariable Long imageId) {
        try {
            facilityImageService.deleteImage(imageId);
            return new ApiResponse<>(true, null, "Xóa ảnh thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    /**
     * Delete all images for a facility
     */
    @DeleteMapping("/facility/{facilityId}/all")
    public ApiResponse<?> deleteAllImages(@PathVariable Long facilityId) {
        try {
            facilityImageService.deleteAllImagesByFacility(facilityId);
            return new ApiResponse<>(true, null, "Xóa tất cả ảnh thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    /**
     * Update image sort order
     */
    @PutMapping("/{imageId}/sort-order")
    public ApiResponse<FacilityImage> updateSortOrder(
            @PathVariable Long imageId,
            @RequestBody Map<String, Integer> request) {
        try {
            Integer sortOrder = request.get("sortOrder");
            if (sortOrder == null) {
                return new ApiResponse<>(false, null, "Sort order không được để trống");
            }
            
            FacilityImage image = facilityImageService.updateImageSortOrder(imageId, sortOrder);
            return new ApiResponse<>(true, image, "Cập nhật thứ tự ảnh thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
}
