package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.entity.FacilityImage;
import group6.it.ou.sportfacilitybooking.dto.FacilityDTO;

@Component
public class FacilityImageMapper {

    public void toFacilityDTO(FacilityImage image, FacilityDTO dto) {
        if (image != null && image.getIsPrimary() != null && image.getIsPrimary()) {
            dto.setCoverImageUrl(image.getImageUrl());
        }
    }
}
