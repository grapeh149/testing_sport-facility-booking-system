package group6.it.ou.sportfacilitybooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import group6.it.ou.sportfacilitybooking.dto.CheckInDTO;
import group6.it.ou.sportfacilitybooking.dto.CheckInRequest;
import group6.it.ou.sportfacilitybooking.service.CheckInService;

@RestController
@RequestMapping("/api/checkins")
@CrossOrigin(origins = "*")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @PostMapping
    public ApiResponse<CheckInDTO> checkIn(@Valid @RequestBody CheckInRequest request,
                                           @RequestParam Long bookingId,
                                           @RequestParam Long checkedByUserId) {
        try {
            CheckInDTO result = checkInService.checkIn(bookingId, checkedByUserId, request);
            return new ApiResponse<>(true, result, "Check-in thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @GetMapping("/booking/{bookingId}")
    public ApiResponse<CheckInDTO> getCheckInRecord(@PathVariable Long bookingId) {
        try {
            CheckInDTO result = checkInService.getCheckInRecord(bookingId);
            return new ApiResponse<>(true, result, "Lấy thông tin check-in thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
}
