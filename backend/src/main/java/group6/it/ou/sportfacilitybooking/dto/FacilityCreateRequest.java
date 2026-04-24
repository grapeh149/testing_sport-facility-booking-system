package group6.it.ou.sportfacilitybooking.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class FacilityCreateRequest {
    @NotEmpty(message = "Tên sân không được để trống")
    private String name;

    @NotEmpty(message = "Địa chỉ không được để trống")
    private String address;

    @NotEmpty(message = "Quận/huyện không được để trống")
    private String district;

    @NotEmpty(message = "Tỉnh/thành phố không được để trống")
    private String city;

    @NotEmpty(message = "Số điện thoại không được để trống")
    private String phone;

    private String description;

    @NotEmpty(message = "Giờ mở cửa không được để trống")
    private String openTime;

    @NotEmpty(message = "Giờ đóng cửa không được để trống")
    private String closeTime;

    @DecimalMin(value = "0.0", inclusive = false, message = "Tỷ lệ hoa hồng phải lớn hơn 0")
    private BigDecimal commissionRate;

    @Min(value = 1, message = "Số giờ hủy phải >= 1")
    private Integer cancelBeforeHours;

    private Boolean autoConfirm;

    private String coverImageUrl;

    public FacilityCreateRequest() {}

    // Getters & Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOpenTime() { return openTime; }
    public void setOpenTime(String openTime) { this.openTime = openTime; }

    public String getCloseTime() { return closeTime; }
    public void setCloseTime(String closeTime) { this.closeTime = closeTime; }

    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public Integer getCancelBeforeHours() { return cancelBeforeHours; }
    public void setCancelBeforeHours(Integer cancelBeforeHours) { this.cancelBeforeHours = cancelBeforeHours; }

    public Boolean getAutoConfirm() { return autoConfirm; }
    public void setAutoConfirm(Boolean autoConfirm) { this.autoConfirm = autoConfirm; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }
}
