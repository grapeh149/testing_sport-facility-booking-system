package group6.it.ou.sportfacilitybooking;

import group6.it.ou.sportfacilitybooking.entity.*;
import group6.it.ou.sportfacilitybooking.repository.PaymentRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

// ----File này chưa thêm Denpendcy cho @DataJpaTest---
//@DataJpaTest(properties = {
//        "spring.flyway.enabled=false",
//        "spring.jpa.hibernate.ddl-auto=create-drop",
//        "spring.datasource.url=jdbc:h2:mem:payment_repository_test;MODE=MSSQLServer;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false",
//        "spring.datasource.driver-class-name=org.h2.Driver",
//        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
//        "spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl"
//})
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void findByVnpayTxnRef_existingTxnRef_returnPayment() {
        // ARRANGE
        Booking booking = persistBooking("BK_FIND_EXIST");
        persistPayment(booking, "TXN_EXIST", PaymentStatus.PENDING, LocalDateTime.now());
        flushAndClear();

        // ACT
        Optional<Payment> result = paymentRepository.findByVnpayTxnRef("TXN_EXIST");

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals("TXN_EXIST", result.get().getVnpayTxnRef());
        assertEquals(PaymentStatus.PENDING, result.get().getStatus());
    }

    @Test
    void findByVnpayTxnRef_missingTxnRef_returnEmpty() {
        // ARRANGE
        Booking booking = persistBooking("BK_FIND_MISSING");
        persistPayment(booking, "TXN_EXIST", PaymentStatus.PENDING, LocalDateTime.now());
        flushAndClear();

        // ACT
        Optional<Payment> result = paymentRepository.findByVnpayTxnRef("TXN_MISSING");

        // ASSERT
        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({
            "TXN_EXIST, true",
            "TXN_MISSING, false"
    })
    void existsByVnpayTxnRef_txnRef_returnExpectedResult(String txnRef, boolean expected) {
        // ARRANGE
        Booking booking = persistBooking("BK_EXISTS");
        persistPayment(booking, "TXN_EXIST", PaymentStatus.PENDING, LocalDateTime.now());
        flushAndClear();

        // ACT
        boolean result = paymentRepository.existsByVnpayTxnRef(txnRef);

        // ASSERT
        assertEquals(expected, result);
    }

    @Test
    void findByBookingId_bookingHasTwoPayments_returnTwoPayments() {
        // ARRANGE
        Booking booking = persistBooking("BK_PAYMENTS");
        persistPayment(booking, "TXN_1", PaymentStatus.SUCCESS, LocalDateTime.now().minusMinutes(2));
        persistPayment(booking, "TXN_2", PaymentStatus.FAILED, LocalDateTime.now().minusMinutes(1));
        flushAndClear();

        // ACT
        List<Payment> result = paymentRepository.findByBookingId(booking.getId());

        // ASSERT
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(payment -> "TXN_1".equals(payment.getVnpayTxnRef())));
        assertTrue(result.stream().anyMatch(payment -> "TXN_2".equals(payment.getVnpayTxnRef())));
    }

    @Test
    void findByBookingId_bookingHasNoPayment_returnEmptyList() {
        // ARRANGE
        Booking booking = persistBooking("BK_EMPTY");
        flushAndClear();

        // ACT
        List<Payment> result = paymentRepository.findByBookingId(booking.getId());

        // ASSERT
        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @EnumSource(PaymentStatus.class)
    void findByStatusAndCreatedAtBetween_matchingStatusAndDateRange_returnOnlyMatchingPayments(PaymentStatus status) {
        // ARRANGE
        LocalDateTime startDate = LocalDateTime.of(2026, 5, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 5, 31, 23, 59);
        PaymentStatus otherStatus = status == PaymentStatus.PENDING ? PaymentStatus.SUCCESS : PaymentStatus.PENDING;

        Booking booking = persistBooking("BK_STATUS_" + status.name());
        persistPayment(booking, "TXN_MATCH_START_" + status.name(), status, startDate);
        persistPayment(booking, "TXN_MATCH_END_" + status.name(), status, endDate);
        persistPayment(booking, "TXN_BEFORE_" + status.name(), status, startDate.minusSeconds(1));
        persistPayment(booking, "TXN_AFTER_" + status.name(), status, endDate.plusSeconds(1));
        persistPayment(booking, "TXN_OTHER_STATUS_" + status.name(), otherStatus, startDate.plusDays(1));
        flushAndClear();

        // ACT
        List<Payment> result = paymentRepository.findByStatusAndCreatedAtBetween(status, startDate, endDate);

        // ASSERT
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(payment -> payment.getStatus() == status));
        assertTrue(result.stream().anyMatch(payment -> payment.getVnpayTxnRef().equals("TXN_MATCH_START_" + status.name())));
        assertTrue(result.stream().anyMatch(payment -> payment.getVnpayTxnRef().equals("TXN_MATCH_END_" + status.name())));
    }

    @Test
    void findByStatusAndCreatedAtBetween_noPaymentInDateRange_returnEmptyList() {
        // ARRANGE
        LocalDateTime startDate = LocalDateTime.of(2026, 5, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2026, 5, 31, 23, 59);

        Booking booking = persistBooking("BK_NO_RANGE");
        persistPayment(booking, "TXN_BEFORE", PaymentStatus.SUCCESS, startDate.minusSeconds(1));
        persistPayment(booking, "TXN_AFTER", PaymentStatus.SUCCESS, endDate.plusSeconds(1));
        persistPayment(booking, "TXN_OTHER_STATUS", PaymentStatus.FAILED, startDate.plusDays(1));
        flushAndClear();

        // ACT
        List<Payment> result = paymentRepository.findByStatusAndCreatedAtBetween(
                PaymentStatus.SUCCESS,
                startDate,
                endDate
        );

        // ASSERT
        assertTrue(result.isEmpty());
    }

    private Payment persistPayment(Booking booking, String txnRef, PaymentStatus status, LocalDateTime createdAt) {
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setVnpayTxnRef(txnRef);
        payment.setAmount(BigDecimal.valueOf(100000));
        payment.setPaymentType(PaymentType.DEPOSIT);
        payment.setStatus(status);
        payment.setPaymentMethod("VNPAY");
        payment.setBankCode("NCB");
        payment.setCreatedAt(createdAt);
        entityManager.persist(payment);
        return payment;
    }

    private Booking persistBooking(String bookingCode) {
        User customer = persistUser("customer_" + bookingCode, UserRole.CUSTOMER);
        User owner = persistUser("owner_" + bookingCode, UserRole.OWNER);
        SportType sportType = persistSportType("sport_" + bookingCode);
        Facility facility = persistFacility(owner, "facility_" + bookingCode);
        Court court = persistCourt(facility, sportType, "court_" + bookingCode);
        TimeSlot timeSlot = persistTimeSlot(court);

        Booking booking = new Booking();
        booking.setBookingCode(bookingCode);
        booking.setCustomer(customer);
        booking.setCourt(court);
        booking.setOwnerId(owner.getId());
        booking.setTimeSlot(timeSlot);
        booking.setBookingDate(LocalDate.now().plusDays(1));
        booking.setStartTime(LocalTime.of(8, 0));
        booking.setEndTime(LocalTime.of(9, 0));
        booking.setTotalPrice(BigDecimal.valueOf(200000));
        booking.setDepositAmount(BigDecimal.valueOf(100000));
        booking.setCommissionAmount(BigDecimal.valueOf(10000));
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        entityManager.persist(booking);
        return booking;
    }

    private User persistUser(String prefix, UserRole role) {
        User user = new User();
        user.setFullName(prefix);
        user.setUsername(prefix);
        user.setEmail(prefix + "@test.com");
        user.setPasswordHash("password_hash");
        user.setRole(role);
        user.setIsActive(true);
        entityManager.persist(user);
        return user;
    }

    private SportType persistSportType(String name) {
        SportType sportType = new SportType();
        sportType.setName(name);
        sportType.setIsActive(true);
        entityManager.persist(sportType);
        return sportType;
    }

    private Facility persistFacility(User owner, String name) {
        Facility facility = new Facility();
        facility.setOwner(owner);
        facility.setName(name);
        facility.setDistrict("District 1");
        facility.setCity("Ho Chi Minh");
        facility.setStatus(FacilityStatus.APPROVED);
        facility.setCommissionRate(BigDecimal.valueOf(5));
        facility.setCancelBeforeHours(24);
        facility.setAutoConfirm(false);
        facility.setTotalReviews(0);
        entityManager.persist(facility);
        return facility;
    }

    private Court persistCourt(Facility facility, SportType sportType, String name) {
        Court court = new Court();
        court.setFacility(facility);
        court.setSportType(sportType);
        court.setName(name);
        court.setIsIndoor(false);
        court.setIsActive(true);
        entityManager.persist(court);
        return court;
    }

    private TimeSlot persistTimeSlot(Court court) {
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setCourt(court);
        timeSlot.setStartTime(LocalTime.of(8, 0));
        timeSlot.setEndTime(LocalTime.of(9, 0));
        timeSlot.setPrice(BigDecimal.valueOf(200000));
        timeSlot.setDepositRate(BigDecimal.valueOf(50));
        timeSlot.setIsActive(true);
        entityManager.persist(timeSlot);
        return timeSlot;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}