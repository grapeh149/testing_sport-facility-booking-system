package group6.it.ou.sportfacilitybooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import group6.it.ou.sportfacilitybooking.dto.ReviewDTO;
import group6.it.ou.sportfacilitybooking.dto.ReviewCreateRequest;
import group6.it.ou.sportfacilitybooking.service.ReviewService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    private Long extractUserIdFromRequest(HttpServletRequest request) {
        Object userIdAttr = request.getAttribute("userId");
        if (userIdAttr != null) {
            return Long.parseLong(userIdAttr.toString());
        }
        return null;
    }
    
    @GetMapping
    public ApiResponse<List<ReviewDTO>> getAllReviews() {
        try {
            List<ReviewDTO> result = reviewService.getAllReviews();
            return new ApiResponse<>(true, result, "Lấy danh sách đánh giá thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    @PostMapping
    public ApiResponse<ReviewDTO> createReview(@Valid @RequestBody ReviewCreateRequest request,
                                               @RequestParam Long bookingId,
                                               HttpServletRequest httpRequest) {
        try {
            Long customerId = extractUserIdFromRequest(httpRequest);
            if (customerId == null) {
                return new ApiResponse<>(false, null, "Không xác định được người dùng");
            }
            ReviewDTO result = reviewService.createReview(bookingId, customerId, request);
            return new ApiResponse<>(true, result, "Tạo đánh giá thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    @GetMapping("/facility/{facilityId}")
    public ApiResponse<Map<String, Object>> getReviewsByFacility(
            @PathVariable Long facilityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ReviewDTO> result = reviewService.getReviewsByFacility(facilityId, pageable);
            
            Map<String, Object> responseData = new LinkedHashMap<>();
            responseData.put("content", result.getContent());
            responseData.put("currentPage", result.getNumber());
            responseData.put("totalPages", result.getTotalPages());
            responseData.put("totalElements", result.getTotalElements());
            responseData.put("hasNextPage", result.hasNext());
            responseData.put("hasPreviousPage", result.hasPrevious());
            
            return new ApiResponse<>(true, responseData, "Lấy danh sách đánh giá thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    @PutMapping("/{id}/reply")
    public ApiResponse<ReviewDTO> replyToReview(@PathVariable Long id, @RequestParam String reply) {
        try {
            ReviewDTO result = reviewService.replyToReview(id, reply);
            return new ApiResponse<>(true, result, "Phản hồi đánh giá thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
}
