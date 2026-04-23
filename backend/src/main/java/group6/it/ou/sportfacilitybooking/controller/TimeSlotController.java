package group6.it.ou.sportfacilitybooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import group6.it.ou.sportfacilitybooking.dto.TimeSlotDTO;
import group6.it.ou.sportfacilitybooking.service.TimeSlotService;
import java.util.List;

@RestController
@RequestMapping("/api/timeslots")
@CrossOrigin(origins = "*")
public class TimeSlotController {

    @Autowired
    private TimeSlotService timeSlotService;

    @GetMapping
    public ApiResponse<List<TimeSlotDTO>> getAllTimeSlots() {
        try {
            List<TimeSlotDTO> result = timeSlotService.getAllTimeSlots();
            return new ApiResponse<>(true, result, "Lấy danh sách giờ hoạt động thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @GetMapping("/court/{courtId}")
    public ApiResponse<List<TimeSlotDTO>> getTimeSlotsByCourtId(@PathVariable Long courtId) {
        try {
            List<TimeSlotDTO> result = timeSlotService.getTimeSlotsByCourtId(courtId);
            return new ApiResponse<>(true, result, "Lấy giờ hoạt động thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<TimeSlotDTO> createTimeSlot(@RequestBody TimeSlotDTO dto) {
        try {
            TimeSlotDTO result = timeSlotService.createTimeSlot(dto);
            return new ApiResponse<>(true, result, "Tạo giờ hoạt động thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<TimeSlotDTO> updateTimeSlot(@PathVariable Long id, @RequestBody TimeSlotDTO dto) {
        try {
            TimeSlotDTO result = timeSlotService.updateTimeSlot(id, dto);
            return new ApiResponse<>(true, result, "Cập nhật giờ hoạt động thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteTimeSlot(@PathVariable Long id) {
        try {
            timeSlotService.deleteTimeSlot(id);
            return new ApiResponse<>(true, null, "Xóa giờ hoạt động thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
}
