package group6.it.ou.sportfacilitybooking.mapper;

import group6.it.ou.sportfacilitybooking.dto.*;
import group6.it.ou.sportfacilitybooking.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("All Mappers Unit Tests")
class MapperTest {

    private BookingMapper bookingMapper = new BookingMapper();
    private CheckInMapper checkInMapper = new CheckInMapper();
    private CourtMapper courtMapper = new CourtMapper();
    private FacilityApprovalLogMapper facilityApprovalLogMapper = new FacilityApprovalLogMapper();
    private FacilityImageMapper facilityImageMapper = new FacilityImageMapper();
    private FacilityMapper facilityMapper = new FacilityMapper();
    private NotificationMapper notificationMapper = new NotificationMapper();
    private PaymentMapper paymentMapper = new PaymentMapper();
    private ReviewMapper reviewMapper = new ReviewMapper();
    private SportTypeMapper sportTypeMapper = new SportTypeMapper();
    private TimeSlotMapper timeSlotMapper = new TimeSlotMapper();
    private UserMapper userMapper = new UserMapper();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(courtMapper, "facilityMapper", facilityMapper);
        ReflectionTestUtils.setField(courtMapper, "sportTypeMapper", sportTypeMapper);
    }

    @Test
    @DisplayName("BookingMapper tests")
    void testBookingMapper() {
        assertNull(bookingMapper.toDTO(null));
        assertNull(bookingMapper.toEntity((BookingDTO) null));

        Booking entity = new Booking();
        entity.setId(1L);
        entity.setBookingCode("B-123");
        User customer = new User();
        customer.setId(2L);
        customer.setFullName("John Doe");
        entity.setCustomer(customer);
        Court court = new Court();
        court.setId(3L);
        court.setName("Court 1");
        Facility facility = new Facility();
        facility.setName("Facility 1");
        court.setFacility(facility);
        entity.setCourt(court);
        entity.setBookingDate(LocalDate.now());
        entity.setStartTime(LocalTime.now());
        entity.setEndTime(LocalTime.now().plusHours(1));
        entity.setTotalPrice(BigDecimal.valueOf(100));
        entity.setDepositAmount(BigDecimal.valueOf(20));
        entity.setStatus(BookingStatus.CONFIRMED);
        entity.setNote("Test note");

        BookingDTO dto = bookingMapper.toDTO(entity);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("B-123", dto.getBookingCode());
        assertEquals("John Doe", dto.getCustomerName());

        Booking mappedEntity = bookingMapper.toEntity(dto);
        assertNotNull(mappedEntity);
        assertEquals(1L, mappedEntity.getId());
    }

    @Test
    @DisplayName("CheckInMapper tests")
    void testCheckInMapper() {
        assertNull(checkInMapper.toDTO(null));

        CheckIn entity = new CheckIn();
        entity.setId(1L);
        Booking booking = new Booking();
        booking.setId(2L);
        entity.setBooking(booking);
        User checkedBy = new User();
        checkedBy.setId(3L);
        entity.setCheckedByUser(checkedBy);
        entity.setCheckedInAt(LocalDateTime.now());

        CheckInDTO dto = checkInMapper.toDTO(entity);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getBookingId());
    }

    @Test
    @DisplayName("CourtMapper tests")
    void testCourtMapper() {
        assertNull(courtMapper.toDTO(null));
        assertNull(courtMapper.toEntity((CourtDTO) null));
        assertNull(courtMapper.toEntity((CourtCreateRequest) null));

        Court entity = new Court();
        entity.setId(1L);
        Facility facility = new Facility();
        facility.setId(2L);
        entity.setFacility(facility);
        SportType sportType = new SportType();
        sportType.setId(3);
        entity.setSportType(sportType);
        entity.setName("Court 1");
        entity.setIsActive(true);

        CourtDTO dto = courtMapper.toDTO(entity);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(1L, dto.getId());

        Court mappedEntity = courtMapper.toEntity(dto);
        assertNotNull(mappedEntity);
        assertEquals(1L, mappedEntity.getId());

        CourtCreateRequest request = new CourtCreateRequest();
        request.setName("Court req");
        Court mappedFromReq = courtMapper.toEntity(request);
        assertNotNull(mappedFromReq);
        assertEquals("Court req", mappedFromReq.getName());
    }

    @Test
    @DisplayName("FacilityApprovalLogMapper tests")
    void testFacilityApprovalLogMapper() {
        FacilityApprovalLog entity = new FacilityApprovalLog();
        User admin = new User();
        admin.setId(1L);
        admin.setEmail("admin@example.com");
        entity.setAdmin(admin);
        UserDTO adminDTO = new UserDTO();
        facilityApprovalLogMapper.toApprovalSummary(entity, adminDTO);
        assertNotNull(adminDTO.getId());
    }

    @Test
    @DisplayName("FacilityImageMapper tests")
    void testFacilityImageMapper() {
        FacilityImage entity = new FacilityImage();
        entity.setIsPrimary(true);
        entity.setImageUrl("url");
        FacilityDTO dto = new FacilityDTO();
        facilityImageMapper.toFacilityDTO(entity, dto);
        assertEquals("url", dto.getCoverImageUrl());
    }

    @Test
    @DisplayName("FacilityMapper tests")
    void testFacilityMapper() {
        assertNull(facilityMapper.toDTO(null));
        assertNull(facilityMapper.toEntity((FacilityDTO) null));
        assertNull(facilityMapper.toEntity((FacilityCreateRequest) null));

        Facility entity = new Facility();
        entity.setId(1L);
        User owner = new User();
        owner.setId(2L);
        entity.setOwner(owner);
        entity.setName("Fac 1");

        FacilityDTO dto = facilityMapper.toDTO(entity);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getOwnerId());

        Facility mappedEntity = facilityMapper.toEntity(dto);
        assertNotNull(mappedEntity);
        assertEquals(1L, mappedEntity.getId());

        FacilityCreateRequest request = new FacilityCreateRequest();
        request.setName("Fac req");
        Facility mappedFromReq = facilityMapper.toEntity(request);
        assertNotNull(mappedFromReq);
        assertEquals("Fac req", mappedFromReq.getName());
    }

    @Test
    @DisplayName("NotificationMapper tests")
    void testNotificationMapper() {
        assertNull(notificationMapper.toDTO(null));
        Notification entity = new Notification();
        entity.setId(1L);
        User user = new User();
        user.setId(2L);
        entity.setUser(user);
        entity.setType(group6.it.ou.sportfacilitybooking.entity.NotificationType.BOOKING_CREATED);

        assertNotNull(notificationMapper.toDTO(entity));
    }

    @Test
    @DisplayName("PaymentMapper tests")
    void testPaymentMapper() {
        assertNull(paymentMapper.toDTO(null));
        Payment entity = new Payment();
        entity.setId(1L);
        Booking booking = new Booking();
        booking.setId(2L);
        entity.setBooking(booking);

        assertNotNull(paymentMapper.toDTO(entity));
    }

    @Test
    @DisplayName("ReviewMapper tests")
    void testReviewMapper() {
        assertNull(reviewMapper.toDTO(null));
        assertNull(reviewMapper.toEntity((ReviewDTO) null));

        Review entity = new Review();
        entity.setId(1L);
        User customer = new User();
        customer.setId(2L);
        customer.setFullName("Cus");
        entity.setCustomer(customer);
        Facility facility = new Facility();
        facility.setId(3L);
        entity.setFacility(facility);
        Booking booking = new Booking();
        booking.setId(4L);
        entity.setBooking(booking);

        ReviewDTO dto = reviewMapper.toDTO(entity);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());

        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setRating((byte) 5);
        Review mappedFromReq = reviewMapper.toEntity(request);
        assertNotNull(mappedFromReq);
    }

    @Test
    @DisplayName("SportTypeMapper tests")
    void testSportTypeMapper() {
        assertNull(sportTypeMapper.toDTO(null));
        assertNull(sportTypeMapper.toEntity((SportTypeDTO) null));

        SportType entity = new SportType();
        entity.setId(1);
        entity.setName("Soccer");

        SportTypeDTO dto = sportTypeMapper.toDTO(entity);
        assertNotNull(dto);
        assertEquals(1, dto.getId());

        SportType mappedEntity = sportTypeMapper.toEntity(dto);
        assertNotNull(mappedEntity);
        assertEquals("Soccer", mappedEntity.getName());
    }

    @Test
    @DisplayName("TimeSlotMapper tests")
    void testTimeSlotMapper() {
        assertNull(timeSlotMapper.toDTO(null));
        assertNull(timeSlotMapper.toEntity((TimeSlotDTO) null));

        TimeSlot entity = new TimeSlot();
        entity.setId(1L);
        Court court = new Court();
        court.setId(2L);
        entity.setCourt(court);

        TimeSlotDTO dto = timeSlotMapper.toDTO(entity);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());

        TimeSlot mappedEntity = timeSlotMapper.toEntity(dto);
        assertNotNull(mappedEntity);
        assertEquals(1L, mappedEntity.getId());
    }

    @Test
    @DisplayName("UserMapper tests")
    void testUserMapper() {
        assertNull(userMapper.toDTO(null));
        assertNull(userMapper.toEntity((UserRegistrationRequest) null));

        User entity = new User();
        entity.setId(1L);
        entity.setFullName("User 1");
        entity.setRole(UserRole.CUSTOMER);

        UserDTO dto = userMapper.toDTO(entity);
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("CUSTOMER", dto.getRole());

        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setFullName("User req");
        request.setRole("OWNER");
        User mappedFromReq = userMapper.toEntity(request);
        assertNotNull(mappedFromReq);
        assertEquals("User req", mappedFromReq.getFullName());
        // assertEquals(UserRole.OWNER, mappedFromReq.getRole());
    }
}
