package group6.it.ou.sportfacilitybooking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import group6.it.ou.sportfacilitybooking.dto.ApiResponse;
import group6.it.ou.sportfacilitybooking.dto.SportTypeDTO;
import group6.it.ou.sportfacilitybooking.service.SportTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/sport-types")
@CrossOrigin(origins = "*")
public class SportTypeController {
    
    @Autowired
    private SportTypeService sportTypeService;
    
    @GetMapping
    public ApiResponse<List<SportTypeDTO>> getAllActiveSportTypes() {
        try {
            List<SportTypeDTO> result = sportTypeService.getAllActiveSportTypes();
            return new ApiResponse<>(true, result, "Lấy danh sách loại thể thao thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    @GetMapping("/all")
    public ApiResponse<List<SportTypeDTO>> getAllSportTypes() {
        try {
            List<SportTypeDTO> result = sportTypeService.getAllSportTypes();
            return new ApiResponse<>(true, result, "Lấy danh sách loại thể thao thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public ApiResponse<SportTypeDTO> getSportTypeById(@PathVariable Integer id) {
        try {
            SportTypeDTO result = sportTypeService.getSportTypeById(id);
            return new ApiResponse<>(true, result, "Lấy thông tin loại thể thao thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    @PostMapping
    public ApiResponse<SportTypeDTO> createSportType(@RequestBody SportTypeDTO sportTypeDTO) {
        try {
            SportTypeDTO result = sportTypeService.createSportType(sportTypeDTO);
            return new ApiResponse<>(true, result, "Tạo loại thể thao thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
    
    @PutMapping("/{id}")
    public ApiResponse<SportTypeDTO> updateSportType(@PathVariable Integer id, @RequestBody SportTypeDTO sportTypeDTO) {
        try {
            SportTypeDTO result = sportTypeService.updateSportType(id, sportTypeDTO);
            return new ApiResponse<>(true, result, "Cập nhật loại thể thao thành công");
        } catch (Exception e) {
            return new ApiResponse<>(false, null, e.getMessage());
        }
    }
}
