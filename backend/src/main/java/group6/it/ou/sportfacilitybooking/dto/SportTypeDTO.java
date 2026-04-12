package group6.it.ou.sportfacilitybooking.dto;

public class SportTypeDTO {
    private Integer id;
    private String name;
    private String description;
    private String iconUrl;
    private Boolean isActive;

    public SportTypeDTO() {}

    public SportTypeDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
