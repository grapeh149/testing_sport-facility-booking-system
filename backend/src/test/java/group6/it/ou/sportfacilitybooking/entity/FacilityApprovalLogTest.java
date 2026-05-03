package group6.it.ou.sportfacilitybooking.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FacilityApprovalLog Entity Tests")
class FacilityApprovalLogTest {

    private FacilityApprovalLog approvalLog;
    private Facility facility;
    private User admin;

    @BeforeEach
    void setUp() {
        approvalLog = new FacilityApprovalLog();

        facility = new Facility();
        facility.setId(1L);
        facility.setName("Test Facility");

        admin = new User();
        admin.setId(2L);
        admin.setFullName("Admin User");
    }

    @Test
    @DisplayName("Test FacilityApprovalLog creation and default values")
    void testApprovalLogDefaults() {
        FacilityApprovalLog newLog = new FacilityApprovalLog();
        assertNotNull(newLog.getCreatedAt());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog id getter and setter")
    void testIdGetterSetter() {
        approvalLog.setId(100L);
        assertEquals(100L, approvalLog.getId());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog facility getter and setter")
    void testFacilityGetterSetter() {
        approvalLog.setFacility(facility);
        assertNotNull(approvalLog.getFacility());
        assertEquals(facility, approvalLog.getFacility());
        assertEquals("Test Facility", approvalLog.getFacility().getName());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog admin getter and setter")
    void testAdminGetterSetter() {
        approvalLog.setAdmin(admin);
        assertNotNull(approvalLog.getAdmin());
        assertEquals(admin, approvalLog.getAdmin());
        assertEquals("Admin User", approvalLog.getAdmin().getFullName());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog action getter and setter for APPROVED")
    void testActionApproved() {
        approvalLog.setAction("APPROVED");
        assertEquals("APPROVED", approvalLog.getAction());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog action getter and setter for REJECTED")
    void testActionRejected() {
        approvalLog.setAction("REJECTED");
        assertEquals("REJECTED", approvalLog.getAction());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog action getter and setter for SUSPENDED")
    void testActionSuspended() {
        approvalLog.setAction("SUSPENDED");
        assertEquals("SUSPENDED", approvalLog.getAction());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog action getter and setter for REACTIVATED")
    void testActionReactivated() {
        approvalLog.setAction("REACTIVATED");
        assertEquals("REACTIVATED", approvalLog.getAction());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog reason getter and setter")
    void testReasonGetterSetter() {
        approvalLog.setReason("Facility meets all requirements");
        assertEquals("Facility meets all requirements", approvalLog.getReason());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog reason null value")
    void testReasonNull() {
        approvalLog.setReason(null);
        assertNull(approvalLog.getReason());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog createdAt getter and setter")
    void testCreatedAtGetterSetter() {
        LocalDateTime now = LocalDateTime.now();
        approvalLog.setCreatedAt(now);
        assertEquals(now, approvalLog.getCreatedAt());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog with approval action")
    void testApprovalLogWithApproval() {
        LocalDateTime approvalTime = LocalDateTime.now();

        approvalLog.setId(1L);
        approvalLog.setFacility(facility);
        approvalLog.setAdmin(admin);
        approvalLog.setAction("APPROVED");
        approvalLog.setReason("All documents verified");
        approvalLog.setCreatedAt(approvalTime);

        assertEquals(1L, approvalLog.getId());
        assertEquals(facility, approvalLog.getFacility());
        assertEquals(admin, approvalLog.getAdmin());
        assertEquals("APPROVED", approvalLog.getAction());
        assertEquals("All documents verified", approvalLog.getReason());
        assertEquals(approvalTime, approvalLog.getCreatedAt());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog with rejection action")
    void testApprovalLogWithRejection() {
        LocalDateTime rejectionTime = LocalDateTime.now();

        approvalLog.setId(2L);
        approvalLog.setFacility(facility);
        approvalLog.setAdmin(admin);
        approvalLog.setAction("REJECTED");
        approvalLog.setReason("Insufficient documentation provided");
        approvalLog.setCreatedAt(rejectionTime);

        assertEquals(2L, approvalLog.getId());
        assertEquals("REJECTED", approvalLog.getAction());
        assertEquals("Insufficient documentation provided", approvalLog.getReason());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog with suspension action")
    void testApprovalLogWithSuspension() {
        LocalDateTime suspensionTime = LocalDateTime.now();

        approvalLog.setId(3L);
        approvalLog.setFacility(facility);
        approvalLog.setAdmin(admin);
        approvalLog.setAction("SUSPENDED");
        approvalLog.setReason("Violation of terms and conditions");
        approvalLog.setCreatedAt(suspensionTime);

        assertEquals("SUSPENDED", approvalLog.getAction());
        assertEquals("Violation of terms and conditions", approvalLog.getReason());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog with reactivation action")
    void testApprovalLogWithReactivation() {
        LocalDateTime reactivationTime = LocalDateTime.now();

        approvalLog.setId(4L);
        approvalLog.setFacility(facility);
        approvalLog.setAdmin(admin);
        approvalLog.setAction("REACTIVATED");
        approvalLog.setReason("Issues resolved, facility can resume operations");
        approvalLog.setCreatedAt(reactivationTime);

        assertEquals("REACTIVATED", approvalLog.getAction());
        assertEquals("Issues resolved, facility can resume operations", approvalLog.getReason());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog with empty reason")
    void testApprovalLogWithEmptyReason() {
        approvalLog.setId(5L);
        approvalLog.setFacility(facility);
        approvalLog.setAdmin(admin);
        approvalLog.setAction("APPROVED");
        approvalLog.setReason("");

        assertEquals(5L, approvalLog.getId());
        assertEquals("APPROVED", approvalLog.getAction());
        assertEquals("", approvalLog.getReason());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog null facility")
    void testNullFacility() {
        approvalLog.setFacility(null);
        assertNull(approvalLog.getFacility());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog null admin")
    void testNullAdmin() {
        approvalLog.setAdmin(null);
        assertNull(approvalLog.getAdmin());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog null action")
    void testNullAction() {
        approvalLog.setAction(null);
        assertNull(approvalLog.getAction());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog multiple sequential updates")
    void testMultipleSequentialUpdates() {
        approvalLog.setFacility(facility);
        approvalLog.setAdmin(admin);
        approvalLog.setAction("APPROVED");
        approvalLog.setReason("Initial approval");

        assertEquals("APPROVED", approvalLog.getAction());

        // Update to suspension
        approvalLog.setAction("SUSPENDED");
        approvalLog.setReason("Violation found");
        assertEquals("SUSPENDED", approvalLog.getAction());

        // Update to reactivation
        approvalLog.setAction("REACTIVATED");
        approvalLog.setReason("Issues fixed");
        assertEquals("REACTIVATED", approvalLog.getAction());
    }

    @Test
    @DisplayName("Test FacilityApprovalLog with very long reason")
    void testVeryLongReason() {
        String longReason = "A".repeat(500);
        approvalLog.setReason(longReason);
        assertEquals(longReason, approvalLog.getReason());
    }
}
