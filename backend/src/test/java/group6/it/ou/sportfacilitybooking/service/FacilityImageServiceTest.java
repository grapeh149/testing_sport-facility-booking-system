package group6.it.ou.sportfacilitybooking.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import group6.it.ou.sportfacilitybooking.entity.Facility;
import group6.it.ou.sportfacilitybooking.entity.FacilityImage;
import group6.it.ou.sportfacilitybooking.repository.FacilityImageRepository;
import group6.it.ou.sportfacilitybooking.repository.FacilityRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("FacilityImageService Unit Tests")
class FacilityImageServiceTest {

    @Mock
    private FacilityImageRepository facilityImageRepository;

    @Mock
    private FacilityRepository facilityRepository;

    @InjectMocks
    private FacilityImageService facilityImageService;

    private Facility facility;
    private FacilityImage image;

    @BeforeEach
    void setUp() {
        facility = new Facility();
        facility.setId(1L);

        image = new FacilityImage();
        image.setId(1L);
        image.setFacility(facility);
        image.setImageUrl("http://image.url/1.jpg");
        image.setSortOrder(1);
    }

    @Test
    @DisplayName("Should get images by facility ID")
    void testGetImagesByFacilityId() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(facilityImageRepository.findByFacilityIdOrderBySortOrder(1L)).thenReturn(List.of(image));

        List<FacilityImage> result = facilityImageService.getImagesByFacilityId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should add image to facility")
    void testAddImage() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(facilityImageRepository.save(any(FacilityImage.class))).thenReturn(image);

        FacilityImage result = facilityImageService.addImage(1L, "http://image.url/1.jpg", 1);

        assertNotNull(result);
        assertEquals("http://image.url/1.jpg", result.getImageUrl());
        verify(facilityImageRepository, times(1)).save(any(FacilityImage.class));
    }

    @Test
    @DisplayName("Should add multiple images to facility")
    void testAddImages() {
        when(facilityRepository.findById(1L)).thenReturn(Optional.of(facility));
        when(facilityImageRepository.findByFacilityId(1L)).thenReturn(List.of(image)); // already has 1
        when(facilityImageRepository.save(any(FacilityImage.class))).thenReturn(new FacilityImage());
        when(facilityImageRepository.findByFacilityIdOrderBySortOrder(1L))
                .thenReturn(List.of(image, new FacilityImage(), new FacilityImage()));

        List<FacilityImage> result = facilityImageService.addImages(1L, List.of("url2.jpg", "url3.jpg"));

        assertEquals(3, result.size());
        verify(facilityImageRepository, times(2)).save(any(FacilityImage.class)); // called 2 times
    }

    @Test
    @DisplayName("Should throw exception when facility not found")
    void testAddImage_FacilityNotFound() {
        when(facilityRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> facilityImageService.addImage(999L, "url", 1));
        assertEquals("Facility not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete image by id")
    void testDeleteImage() {
        when(facilityImageRepository.findById(1L)).thenReturn(Optional.of(image));

        facilityImageService.deleteImage(1L);

        verify(facilityImageRepository, times(1)).delete(image);
    }

    @Test
    @DisplayName("Should delete all images by facility")
    void testDeleteAllImagesByFacility() {
        when(facilityImageRepository.findByFacilityId(1L)).thenReturn(List.of(image));

        facilityImageService.deleteAllImagesByFacility(1L);

        verify(facilityImageRepository, times(1)).deleteAll(List.of(image));
    }

    @Test
    @DisplayName("Should update image sort order")
    void testUpdateImageSortOrder() {
        when(facilityImageRepository.findById(1L)).thenReturn(Optional.of(image));
        when(facilityImageRepository.save(image)).thenReturn(image);

        FacilityImage result = facilityImageService.updateImageSortOrder(1L, 5);

        assertEquals(5, result.getSortOrder());
        verify(facilityImageRepository, times(1)).save(image);
    }
}