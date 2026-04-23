package group6.it.ou.sportfacilitybooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import group6.it.ou.sportfacilitybooking.dto.BookingDTO;
import group6.it.ou.sportfacilitybooking.dto.BookingCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.BookingCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.CheckInDTO;
import group6.it.ou.sportfacilitybooking.dto.CheckInRequest;
import group6.it.ou.sportfacilitybooking.service.BookingService;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingService bookingService;

    // Helper method to extract userId from request attributes (set by JwtAuthenticationFilter)
    private Long extractUserIdFromRequest(HttpServletRequest request) {
        Object userIdAttr = request.getAttribute("userId");
        if (userIdAttr != null) {
            return Long.parseLong(userIdAttr.toString());
        }
        return null;
    }

    @GetMapping
    public ApiResponse<String> getAllBookings() {
        logger.info("[API] GET /api/bookings - Getting all bookings");
        return new ApiResponse<>(true, "Use /api/bookings/my-bookings to get your bookings",
                "Available endpoints: /my-bookings, /{id}");
    }

    @PostMapping
    public ApiResponse<BookingDTO> createBooking(@Valid @RequestBody BookingCreateRequest request,
                                                 HttpServletRequest httpRequest) {
        try {
            Long customerId = extractUserIdFromRequest(httpRequest);
            logger.info("[API] POST /api/bookings - Creating booking for customer: {}", customerId);
            BookingDTO result = bookingService.createBooking(request, customerId);
            logger.info("[API] Booking created successfully: {}", result);
            return new ApiResponse<>(true, result, "Đặt sân thành công");
        } catch (Exception e) {
            logger.error("[API] Failed to create booking: {}", e.getMessage(), e);
            return new ApiResponse<>(false, null, "Đặt sân thất bại: " + e.getMessage());
        }
    }

    @GetMapping("/code/{bookingCode}")
    public ApiResponse<BookingDTO> getBookingDetails(@PathVariable String bookingCode) {
        try {
            BookingDTO result = bookingService.getBookingDetails(bookingCode);
            return new ApiResponse<>(true, result, "Lấy thông tin đặt sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @GetMapping("/my-bookings")
    public ApiResponse<Map<String, Object>> getMyBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            Long customerId = extractUserIdFromRequest(request);
            logger.info("[API] GET /api/bookings/my-bookings - Customer ID: {}, Page: {}, Size: {}", customerId, page, size);

            Pageable pageable = PageRequest.of(page, size);
            Page<BookingDTO> result = bookingService.getCustomerBookingHistory(customerId, pageable);

            Map<String, Object> responseData = new LinkedHashMap<>();
            responseData.put("content", result.getContent());
            responseData.put("currentPage", result.getNumber());
            responseData.put("totalPages", result.getTotalPages());
            responseData.put("totalElements", result.getTotalElements());
            responseData.put("hasNextPage", result.hasNext());
            responseData.put("hasPreviousPage", result.hasPrevious());

            return new ApiResponse<>(true, responseData, "Lấy lịch sử đặt sân thành công");
        } catch (Exception e) {
            logger.error("[API] Failed to get bookings: {}", e.getMessage());
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @GetMapping("/owner/pending-bookings")
    public ApiResponse<Map<String, Object>> getOwnerPendingBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            Long ownerId = extractUserIdFromRequest(request);
            logger.info("[API] GET /api/bookings/owner/pending-bookings - Owner ID: {}, Page: {}, Size: {}", ownerId, page, size);

            Pageable pageable = PageRequest.of(page, size);
            Page<BookingDTO> result = bookingService.getOwnerPendingBookings(ownerId, pageable);

            Map<String, Object> responseData = new LinkedHashMap<>();
            responseData.put("content", result.getContent());
            responseData.put("currentPage", result.getNumber());
            responseData.put("totalPages", result.getTotalPages());
            responseData.put("totalElements", result.getTotalElements());
            responseData.put("hasNextPage", result.hasNext());
            responseData.put("hasPreviousPage", result.hasPrevious());

            return new ApiResponse<>(true, responseData, "Lấy danh sách đơn cần duyệt thành công");
        } catch (Exception e) {
            logger.error("[API] Failed to get pending bookings: {}", e.getMessage());
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @GetMapping("/owner/all-bookings")
    public ApiResponse<Map<String, Object>> getOwnerAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            HttpServletRequest request) {
        try {
            Long ownerId = extractUserIdFromRequest(request);
            logger.info("[API] GET /api/bookings/owner/all-bookings - Owner ID: {}, Page: {}, Size: {}", ownerId, page, size);

            Pageable pageable = PageRequest.of(page, size);
            Page<BookingDTO> result = bookingService.getOwnerAllBookings(ownerId, pageable);

            Map<String, Object> responseData = new LinkedHashMap<>();
            responseData.put("content", result.getContent());
            responseData.put("currentPage", result.getNumber());
            responseData.put("totalPages", result.getTotalPages());
            responseData.put("totalElements", result.getTotalElements());
            responseData.put("hasNextPage", result.hasNext());
            responseData.put("hasPreviousPage", result.hasPrevious());

            return new ApiResponse<>(true, responseData, "Lấy danh sách tất cả đơn đặt thành công");
        } catch (Exception e) {
            logger.error("[API] Failed to get all owner bookings: {}", e.getMessage());
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<BookingDTO> getBookingById(@PathVariable Long id) {
        try {
            BookingDTO result = bookingService.getBookingById(id);
            return new ApiResponse<>(true, result, "Lấy thông tin đặt sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PostMapping("/{id}/confirm")
    public ApiResponse<BookingDTO> confirmBooking(@PathVariable Long id, @RequestParam Long ownerId) {
        try {
            BookingDTO result = bookingService.confirmBooking(id, ownerId);
            return new ApiResponse<>(true, result, "Xác nhận đặt sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<BookingDTO> cancelBookingPost(@PathVariable Long id, @RequestParam(required = false) String reason) {
        try {
            BookingDTO result = bookingService.cancelBooking(id, reason != null ? reason : "");
            return new ApiResponse<>(true, result, "Hủy đặt sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<BookingDTO> cancelBooking(@PathVariable Long id) {
        try {
            BookingDTO result = bookingService.cancelBooking(id, "");
            return new ApiResponse<>(true, result, "Hủy đặt sân thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
}
