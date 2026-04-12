package group6.it.ou.sportfacilitybooking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import group6.it.ou.sportfacilitybooking.entity.Facility;
import group6.it.ou.sportfacilitybooking.entity.FacilityImage;
import group6.it.ou.sportfacilitybooking.repository.FacilityRepository;
import group6.it.ou.sportfacilitybooking.repository.FacilityImageRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class FacilityImageService {
    
    @Autowired
    private FacilityImageRepository facilityImageRepository;
    
    @Autowired
    private FacilityRepository facilityRepository;
    
    /**
     * Get all images for a facility ordered by sort order
     */
    public List<FacilityImage> getImagesByFacilityId(Long facilityId) {
        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> new RuntimeException("Facility not found"));
        return facilityImageRepository.findByFacilityIdOrderBySortOrder(facilityId);
    }
    
    /**
     * Add an image to facility
     */
    public FacilityImage addImage(Long facilityId, String imageUrl, Integer sortOrder) {
        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        FacilityImage image = new FacilityImage();
        image.setFacility(facility);
        image.setImageUrl(imageUrl);
        image.setSortOrder(sortOrder != null ? sortOrder : 0);
        image.setIsPrimary(false);
        image.setCreatedAt(LocalDateTime.now());
        
        return facilityImageRepository.save(image);
    }
    
    /**
     * Add multiple images to facility
     */
    public List<FacilityImage> addImages(Long facilityId, List<String> imageUrls) {
        Facility facility = facilityRepository.findById(facilityId)
            .orElseThrow(() -> new RuntimeException("Facility not found"));
        
        List<FacilityImage> images = facilityImageRepository.findByFacilityId(facilityId);
        int startIndex = images.size();
        
        for (int i = 0; i < imageUrls.size(); i++) {
            FacilityImage image = new FacilityImage();
            image.setFacility(facility);
            image.setImageUrl(imageUrls.get(i));
            image.setSortOrder(startIndex + i);
            image.setIsPrimary(false);
            image.setCreatedAt(LocalDateTime.now());
            
            facilityImageRepository.save(image);
        }
        
        return facilityImageRepository.findByFacilityIdOrderBySortOrder(facilityId);
    }
    
    /**
     * Delete image by id
     */
    public void deleteImage(Long imageId) {
        FacilityImage image = facilityImageRepository.findById(imageId)
            .orElseThrow(() -> new RuntimeException("Image not found"));
        facilityImageRepository.delete(image);
    }
    
    /**
     * Delete all images for a facility
     */
    public void deleteAllImagesByFacility(Long facilityId) {
        List<FacilityImage> images = facilityImageRepository.findByFacilityId(facilityId);
        facilityImageRepository.deleteAll(images);
    }
    
    /**
     * Update image sort order
     */
    public FacilityImage updateImageSortOrder(Long imageId, Integer sortOrder) {
        FacilityImage image = facilityImageRepository.findById(imageId)
            .orElseThrow(() -> new RuntimeException("Image not found"));
        image.setSortOrder(sortOrder);
        return facilityImageRepository.save(image);
    }
}
