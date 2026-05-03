package group6.it.ou.sportfacilitybooking.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Facility Entity Tests")
class FacilityTest {

    private Facility facility;
    private User owner;
    private Court court;
    private FacilityImage image;

    @BeforeEach
    void setUp() {
        facility = new Facility();
        owner = new User();
        owner.setId(1L);
        owner.setFullName("Owner Name");

        court = new Court();
        court.setId(1L);

        image = new FacilityImage();
        image.setId(1L);
    }

    @Test
    @DisplayName("Test Facility creation and default values")
    void testFacilityDefaults() {
        Facility newFacility = new Facility();

        assertEquals(FacilityStatus.PENDING, newFacility.getStatus());
        assertEquals(BigDecimal.valueOf(5.00), newFacility.getCommissionRate());
        assertEquals(24, newFacility.getCancelBeforeHours());
        assertEquals(false, newFacility.getAutoConfirm());
        assertEquals(0, newFacility.getTotalReviews());
        assertNotNull(newFacility.getCreatedAt());
    }

    @Test
    @DisplayName("Test Facility id getter and setter")
    void testIdGetterSetter() {
        facility.setId(100L);
        assertEquals(100L, facility.getId());
    }

    @Test
    @DisplayName("Test Facility owner getter and setter")
    void testOwnerGetterSetter() {
        facility.setOwner(owner);
        assertNotNull(facility.getOwner());
        assertEquals(owner, facility.getOwner());
        assertEquals("Owner Name", facility.getOwner().getFullName());
    }

    @Test
    @DisplayName("Test Facility name getter and setter")
    void testNameGetterSetter() {
        facility.setName("Test Facility");
        assertEquals("Test Facility", facility.getName());
    }

    @Test
    @DisplayName("Test Facility address getter and setter")
    void testAddressGetterSetter() {
        facility.setAddress("123 Main Street");
        assertEquals("123 Main Street", facility.getAddress());
    }

    @Test
    @DisplayName("Test Facility district getter and setter")
    void testDistrictGetterSetter() {
        facility.setDistrict("District 1");
        assertEquals("District 1", facility.getDistrict());
    }

    @Test
    @DisplayName("Test Facility city getter and setter")
    void testCityGetterSetter() {
        facility.setCity("Ho Chi Minh");
        assertEquals("Ho Chi Minh", facility.getCity());
    }

    @Test
    @DisplayName("Test Facility latitude getter and setter")
    void testLatitudeGetterSetter() {
        Double latitude = 10.7769;
        facility.setLatitude(latitude);
        assertEquals(latitude, facility.getLatitude());
    }

    @Test
    @DisplayName("Test Facility longitude getter and setter")
    void testLongitudeGetterSetter() {
        Double longitude = 106.7009;
        facility.setLongitude(longitude);
        assertEquals(longitude, facility.getLongitude());
    }

    @Test
    @DisplayName("Test Facility phone getter and setter")
    void testPhoneGetterSetter() {
        facility.setPhone("0123456789");
        assertEquals("0123456789", facility.getPhone());
    }

    @Test
    @DisplayName("Test Facility description getter and setter")
    void testDescriptionGetterSetter() {
        facility.setDescription("A great sports facility");
        assertEquals("A great sports facility", facility.getDescription());
    }

    @Test
    @DisplayName("Test Facility openTime getter and setter")
    void testOpenTimeGetterSetter() {
        LocalTime openTime = LocalTime.of(6, 0);
        facility.setOpenTime(openTime);
        assertEquals(openTime, facility.getOpenTime());
    }

    @Test
    @DisplayName("Test Facility closeTime getter and setter")
    void testCloseTimeGetterSetter() {
        LocalTime closeTime = LocalTime.of(22, 0);
        facility.setCloseTime(closeTime);
        assertEquals(closeTime, facility.getCloseTime());
    }

    @Test
    @DisplayName("Test Facility status getter and setter")
    void testStatusGetterSetter() {
        facility.setStatus(FacilityStatus.APPROVED);
        assertEquals(FacilityStatus.APPROVED, facility.getStatus());

        facility.setStatus(FacilityStatus.SUSPENDED);
        assertEquals(FacilityStatus.SUSPENDED, facility.getStatus());

        facility.setStatus(FacilityStatus.REJECTED);
        assertEquals(FacilityStatus.REJECTED, facility.getStatus());
    }

    @Test
    @DisplayName("Test Facility commissionRate getter and setter")
    void testCommissionRateGetterSetter() {
        BigDecimal rate = BigDecimal.valueOf(10.00);
        facility.setCommissionRate(rate);
        assertEquals(rate, facility.getCommissionRate());
    }

    @Test
    @DisplayName("Test Facility cancelBeforeHours getter and setter")
    void testCancelBeforeHoursGetterSetter() {
        facility.setCancelBeforeHours(48);
        assertEquals(48, facility.getCancelBeforeHours());
    }

    @Test
    @DisplayName("Test Facility autoConfirm getter and setter")
    void testAutoConfirmGetterSetter() {
        facility.setAutoConfirm(true);
        assertEquals(true, facility.getAutoConfirm());

        facility.setAutoConfirm(false);
        assertEquals(false, facility.getAutoConfirm());
    }

    @Test
    @DisplayName("Test Facility avgRating getter and setter")
    void testAvgRatingGetterSetter() {
        BigDecimal rating = BigDecimal.valueOf(4.50);
        facility.setAvgRating(rating);
        assertEquals(rating, facility.getAvgRating());

        facility.setAvgRating(null);
        assertNull(facility.getAvgRating());
    }

    @Test
    @DisplayName("Test Facility totalReviews getter and setter")
    void testTotalReviewsGetterSetter() {
        facility.setTotalReviews(50);
        assertEquals(50, facility.getTotalReviews());
    }

    @Test
    @DisplayName("Test Facility coverImageUrl getter and setter")
    void testCoverImageUrlGetterSetter() {
        facility.setCoverImageUrl("https://example.com/image.jpg");
        assertEquals("https://example.com/image.jpg", facility.getCoverImageUrl());
    }

    @Test
    @DisplayName("Test Facility createdAt getter and setter")
    void testCreatedAtGetterSetter() {
        LocalDateTime now = LocalDateTime.now();
        facility.setCreatedAt(now);
        assertEquals(now, facility.getCreatedAt());
    }

    @Test
    @DisplayName("Test Facility updatedAt getter and setter")
    void testUpdatedAtGetterSetter() {
        LocalDateTime now = LocalDateTime.now();
        facility.setUpdatedAt(now);
        assertEquals(now, facility.getUpdatedAt());

        facility.setUpdatedAt(null);
        assertNull(facility.getUpdatedAt());
    }

    @Test
    @DisplayName("Test Facility courts getter and setter")
    void testCourtsGetterSetter() {
        List<Court> courts = new ArrayList<>();
        courts.add(court);
        facility.setCourts(courts);

        assertNotNull(facility.getCourts());
        assertEquals(1, facility.getCourts().size());
        assertEquals(court, facility.getCourts().get(0));
    }

    @Test
    @DisplayName("Test Facility images getter and setter")
    void testImagesGetterSetter() {
        List<FacilityImage> images = new ArrayList<>();
        images.add(image);
        facility.setImages(images);

        assertNotNull(facility.getImages());
        assertEquals(1, facility.getImages().size());
        assertEquals(image, facility.getImages().get(0));
    }

    @Test
    @DisplayName("Test Facility with all fields set")
    void testFacilityWithAllFields() {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now().plusDays(1);

        facility.setId(1L);
        facility.setOwner(owner);
        facility.setName("Complete Facility");
        facility.setAddress("456 Oak Ave");
        facility.setDistrict("District 5");
        facility.setCity("Ho Chi Minh");
        facility.setLatitude(10.8000);
        facility.setLongitude(106.7000);
        facility.setPhone("0987654321");
        facility.setDescription("Complete test facility");
        facility.setOpenTime(LocalTime.of(7, 0));
        facility.setCloseTime(LocalTime.of(23, 0));
        facility.setStatus(FacilityStatus.APPROVED);
        facility.setCommissionRate(BigDecimal.valueOf(7.50));
        facility.setCancelBeforeHours(36);
        facility.setAutoConfirm(true);
        facility.setAvgRating(BigDecimal.valueOf(4.75));
        facility.setTotalReviews(100);
        facility.setCoverImageUrl("https://example.com/cover.jpg");
        facility.setCreatedAt(createdAt);
        facility.setUpdatedAt(updatedAt);

        List<Court> courts = new ArrayList<>();
        courts.add(court);
        facility.setCourts(courts);

        List<FacilityImage> images = new ArrayList<>();
        images.add(image);
        facility.setImages(images);

        assertEquals(1L, facility.getId());
        assertEquals(owner, facility.getOwner());
        assertEquals("Complete Facility", facility.getName());
        assertEquals("456 Oak Ave", facility.getAddress());
        assertEquals("District 5", facility.getDistrict());
        assertEquals("Ho Chi Minh", facility.getCity());
        assertEquals(10.8000, facility.getLatitude());
        assertEquals(106.7000, facility.getLongitude());
        assertEquals("0987654321", facility.getPhone());
        assertEquals("Complete test facility", facility.getDescription());
        assertEquals(LocalTime.of(7, 0), facility.getOpenTime());
        assertEquals(LocalTime.of(23, 0), facility.getCloseTime());
        assertEquals(FacilityStatus.APPROVED, facility.getStatus());
        assertEquals(BigDecimal.valueOf(7.50), facility.getCommissionRate());
        assertEquals(36, facility.getCancelBeforeHours());
        assertEquals(true, facility.getAutoConfirm());
        assertEquals(BigDecimal.valueOf(4.75), facility.getAvgRating());
        assertEquals(100, facility.getTotalReviews());
        assertEquals("https://example.com/cover.jpg", facility.getCoverImageUrl());
        assertEquals(createdAt, facility.getCreatedAt());
        assertEquals(updatedAt, facility.getUpdatedAt());
        assertEquals(1, facility.getCourts().size());
        assertEquals(1, facility.getImages().size());
    }

    @Test
    @DisplayName("Test Facility null values handling")
    void testNullValuesHandling() {
        facility.setOwner(null);
        assertNull(facility.getOwner());

        facility.setAddress(null);
        assertNull(facility.getAddress());

        facility.setLatitude(null);
        assertNull(facility.getLatitude());

        facility.setUpdatedAt(null);
        assertNull(facility.getUpdatedAt());

        facility.setCourts(null);
        assertNull(facility.getCourts());

        facility.setImages(null);
        assertNull(facility.getImages());
    }
}
