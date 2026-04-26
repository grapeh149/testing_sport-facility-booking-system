package group6.it.ou.sportfacilitybooking.dto;

import java.time.LocalDateTime;

public class CourtDTO {
    private Long id;
    private Long facilityId;
    private Integer sportTypeId;
    private String name;
    private String description;
    private String surfaceType;
    private Boolean isIndoor;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private FacilityDTO facility;
    private SportTypeDTO sportType;

    public CourtDTO() {}

    public CourtDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public FacilityDTO getFacility() { return facility; }
    public void setFacility(FacilityDTO facility) { this.facility = facility; }

    public SportTypeDTO getSportType() { return sportType; }
    public void setSportType(SportTypeDTO sportType) { this.sportType = sportType; }
}
