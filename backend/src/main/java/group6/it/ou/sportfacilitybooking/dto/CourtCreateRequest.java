package group6.it.ou.sportfacilitybooking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CourtCreateRequest {
    @NotNull(message = "ID sân vận động không được để trống")
    private Long facilityId;

    @NotNull(message = "Loại thể thao không được để trống")
    private Integer sportTypeId;

    @NotEmpty(message = "Tên sân không được để trống")
    private String name;

    private String description;

    @NotEmpty(message = "Loại bề mặt không được để trống")
    private String surfaceType;

    @NotNull(message = "Vị trí trong nhà/ngoài trời không được để trống")
    private Boolean isIndoor;

    private Boolean isActive;

    public CourtCreateRequest() {}

    // Getters & Setters
    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public Integer getSportTypeId() { return sportTypeId; }
    public void setSportTypeId(Integer sportTypeId) { this.sportTypeId = sportTypeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSurfaceType() { return surfaceType; }
    public void setSurfaceType(String surfaceType) { this.surfaceType = surfaceType; }

    public Boolean getIsIndoor() { return isIndoor; }
    public void setIsIndoor(Boolean isIndoor) { this.isIndoor = isIndoor; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
