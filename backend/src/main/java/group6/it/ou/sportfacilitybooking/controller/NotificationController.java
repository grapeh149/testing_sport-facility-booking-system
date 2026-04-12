package group6.it.ou.sportfacilitybooking.controller;

import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import group6.it.ou.sportfacilitybooking.dto.NotificationDTO;
import group6.it.ou.sportfacilitybooking.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    private Long extractUserId(HttpServletRequest request) {
        Object userIdAttr = request.getAttribute("userId");
        if (userIdAttr instanceof Long userId) {
            return userId;
        }
        throw new RuntimeException("Unauthorized: User ID not found");
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getNotifications(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        HttpServletRequest request
    ) {
        try {
            Long userId = extractUserId(request);
            Pageable pageable = PageRequest.of(page, size);
            Page<NotificationDTO> result = notificationService.getNotifications(userId, pageable);

            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("content", result.getContent());
            payload.put("currentPage", result.getNumber());
            payload.put("totalPages", result.getTotalPages());
            payload.put("totalElements", result.getTotalElements());
            return new ApiResponse<>(true, payload, "Lấy danh sách thông báo thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @GetMapping("/latest")
    public ApiResponse<List<NotificationDTO>> getLatestNotifications(HttpServletRequest request) {
        try {
            Long userId = extractUserId(request);
            List<NotificationDTO> result = notificationService.getLatestNotifications(userId);
            return new ApiResponse<>(true, result, "Lấy thông báo mới nhất thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount(HttpServletRequest request) {
        try {
            Long userId = extractUserId(request);
            long count = notificationService.getUnreadCount(userId);
            return new ApiResponse<>(true, count, "Lấy số thông báo chưa đọc thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, 0L, e.getMessage());
        }
    }

    @PutMapping("/{id}/read")
    public ApiResponse<?> markAsRead(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = extractUserId(request);
            notificationService.markAsRead(id, userId);
            return new ApiResponse<>(true, null, "Đã đánh dấu thông báo là đã đọc");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PutMapping("/read-all")
    public ApiResponse<?> markAllAsRead(HttpServletRequest request) {
        try {
            Long userId = extractUserId(request);
            notificationService.markAllAsRead(userId);
            return new ApiResponse<>(true, null, "Đã đánh dấu tất cả thông báo là đã đọc");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
}
