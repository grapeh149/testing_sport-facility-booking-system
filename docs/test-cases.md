# Test Cases

**Project:** Sport Facility Booking System | **Total Cases:** 85 | **Updated:** May 15, 2026

---

## 1. Authentication & Authorization Test Cases

### 1.1 User Registration

| Test ID | Scenario | Input | Expected Result | Status |
|---------|----------|-------|-----------------|--------|
| TC_AUTH_001 | Valid registration | Valid email, strong password, all required fields | User created, confirmation email sent | ⏳ |
| TC_AUTH_002 | Duplicate email | Email already exists | Error message: "Email already registered" | ⏳ |
| TC_AUTH_003 | Invalid email format | Invalid email pattern | Error: "Invalid email format" | ⏳ |
| TC_AUTH_004 | Weak password | Password < 6 chars | Error: "Password too weak" | ⏳ |
| TC_AUTH_005 | Missing required field | Missing name/phone | Error: "Required field missing" | ⏳ |

### 1.2 User Login & Access Control

| Test ID | Scenario | Expected Result | Status |
|---------|----------|-----------------|--------|
| TC_AUTH_006 | Valid credentials | JWT token generated | ⏳ |
| TC_AUTH_007 | Invalid email | Error: "User not found" | ⏳ |
| TC_AUTH_008 | Invalid password | Error: "Invalid credentials" | ⏳ |
| TC_AUTH_009 | Token expiration | Error: "Token expired" | ⏳ |
| TC_AUTH_010 | Admin access to /admin | Access granted | ⏳ |
| TC_AUTH_011 | User access to /admin | Access denied (403) | ⏳ |

---

## 2. Facility Management Test Cases

### 2.1 Facility Creation

| Test ID | Scenario | Input | Expected Result | Status |
|---------|----------|-------|-----------------|--------|
| TC_FAC_001 | Create valid facility | All required fields filled | Facility created with PENDING status | ⏳ |
| TC_FAC_002 | Missing facility name | Name field empty | Error: "Facility name required" | ⏳ |
| TC_FAC_003 | Invalid location | Invalid address/coordinates | Error: "Invalid location" | ⏳ |
| TC_FAC_004 | Upload facility images | 3 valid images (.jpg, .png) | Images uploaded to Cloudinary | ⏳ |
| TC_FAC_005 | Invalid image format | .gif, .bmp files | Error: "Only JPG/PNG allowed" | ⏳ |

### 2.2 Approval & Search

| Test ID | Scenario | Expected Result | Status |
|---------|----------|-----------------|--------|
| TC_FAC_006 | Admin approves facility | Status: APPROVED, owner notified | ⏳ |
| TC_FAC_007 | Admin rejects facility | Status: REJECTED, reason sent | ⏳ |
| TC_FAC_008 | Search by name | Matching facilities returned | ⏳ |
| TC_FAC_009 | Filter by sport/location | Filtered results displayed | ⏳ |

---

## 3. Court & Time Slot Test Cases

### 3.1 Court Management

| Test ID | Scenario | Input | Expected Result | Status |
|---------|----------|-------|-----------------|--------|
| TC_COURT_001 | Add court to facility | Court name, type | Court created and assigned | ⏳ |
| TC_COURT_002 | Set court price | Price: 100,000 VND | Price saved per hour | ⏳ |
| TC_COURT_003 | Update court status | Mark court as maintenance | Court unavailable for booking | ⏳ |
| TC_COURT_004 | Delete court | Court with no active bookings | Court removed | ⏳ |

### 3.2 Time Slot Availability

| Test ID | Scenario | Expected Result | Status |
|---------|----------|-----------------|--------|
| TC_SLOT_001 | View available slots | All free slots displayed | ⏳ |
| TC_SLOT_002 | Slot conflict detection | Error: "Slot already booked" | ⏳ |
| TC_SLOT_003 | Opening hours validation | Error: "Outside operating hours" | ⏳ |

---

## 4. Booking System Test Cases

### 4.1 Booking Creation

| Test ID | Scenario | Input | Expected Result | Status |
|---------|----------|-------|-----------------|--------|
| TC_BOOK_001 | Valid booking | Court ID, date, time, user | Booking created with PENDING status | ⏳ |
| TC_BOOK_002 | Past date booking | Date in the past | Error: "Cannot book past date" | ⏳ |
| TC_BOOK_003 | Slot already booked | Overlapping time slot | Error: "Time slot unavailable" | ⏳ |
| TC_BOOK_004 | Double booking prevention | Same user, same slot | Error: "Already booked this slot" | ⏳ |
| TC_BOOK_005 | Booking confirmation | Valid booking | Confirmation email sent to user | ⏳ |

### 4.2 Booking Cancellation & Status

| Test ID | Scenario | Expected Result | Status |
|---------|----------|-----------------|--------|
| TC_BOOK_006 | Cancel < 24h | Penalty applied (20%) | ⏳ |
| TC_BOOK_007 | Cancel > 24h | Full refund | ⏳ |
| TC_BOOK_009 | Auto-confirm booking | Status: CONFIRMED | ⏳ |

---

## 5. Payment Test Cases

### 5.1 Payment Processing

| Test ID | Scenario | Input | Expected Result | Status |
|---------|----------|-------|-----------------|--------|
| TC_PAY_001 | Initiate payment | Valid booking, amount | Payment gateway redirected | ⏳ |
| TC_PAY_002 | Successful payment | Valid payment credentials | Transaction confirmed, booking CONFIRMED | ⏳ |
| TC_PAY_003 | Failed payment | Declined card | Error: "Payment declined", booking PENDING | ⏳ |
| TC_PAY_004 | IPN webhook | Payment gateway callback | System records payment status | ⏳ |

### 5.2 Refunds

| Test ID | Scenario | Expected Result | Status |
|---------|----------|-----------------|--------|
| TC_PAY_005 | Process refund | Refund initiated 3-5 days | ⏳ |
| TC_PAY_006 | Partial refund | Penalty deducted | ⏳ |

---

## 6. Review & Rating Test Cases


| Test ID | Scenario | Expected Result | Status |
|---------|----------|-----------------|--------|
| TC_REV_001 | Submit review | Review saved | ⏳ |
| TC_REV_002 | Invalid rating | Error: "Rating must be 1-5" | ⏳ |
| TC_REV_005 | Calculate facility rating | Average rating
---

## 7. Check-in Test Cases

### 7.1 Check-in Process

| Test ID | Scenario | Input | Expected Result | Status |
|---------|----------|-------|-----------------|--------|
| TC_CHECK_001 | Valid check-in | QR code/booking ID on arrival day | Check-in confirmed, access granted | ⏳ |
| TC_CHECK_002 | Early check-in | Check-in > 30 min before slot | Error: "Early check-in, wait until slot time" | ⏳ |
| TC_CHECK_003 | Late check-in | Check-in after slot ended | Warning: "Slot time passed" | ⏳ |
| TC_CHECK_004 | Invalid booking | Non-existent booking ID | Error: "Booking not found" | ⏳ |

---

| Test ID | Scenario | Expected Result | Status |
|---------|----------|-----------------|--------|
| TC_CHECK_001 | Valid check-in | Check-in confirmed, access granted | ⏳ |
| TC_CHECK_002 | Early check-in | Error: "Early check-in" | ⏳ |
| TC_CHECK_003 | Invalidsword correct | Password updated | ⏳ |
| TC_USER_003 | Invalid old password | Wrong old password | Error: "Incorrect password" | ⏳ |
| TC_USER_004 | Upload avatar | Valid image file | Avatar uploaded to Cloudinary | ⏳ |

### 8.2 Notification Preferences

| Test ID | Scenario | Input | Expected Result | Status |
|---------|-------

| Test ID | Scenario | Expected Result | Status |
|---------|----------|-----------------|--------|
| TC_USER_001 | Update profile | Profile updated | ⏳ |
| TC_USER_002 | Change password | Password updated | ⏳ |
| TC_USER_004 | Upload avatar | Avatar uploaded
### 9.2 User Management

| Test ID | Scenario 

| Test ID | Scenario | Expected Result | Status |
|---------|----------|-----------------|--------|
| TC_ADMIN_001 | View pending facilities | Pending facilities list | ⏳ |
| TC_ADMIN_002 | Approve facility | Status: APPROVED | ⏳ |
| TC_ADMIN_005 | View all users | Paginated user list | ⏳ |
| TC_ADMIN_009 | Revenue report | Total revenue displayed

## 11. Security Test Cases

| Test ID | Scenario | Expected Result | Status |
|---------|----------|-----------------|--------|
| TC_SEC_001 | SQL injection attempt | Input validation | Request blocked | ⏳ |
| TC_SEC_002 | XSS attack prevention | Script in input | Script sanitized/escaped | ⏳ |
| TC_SEC_003 | CSRF token validation | Missing token | Request rejected | ⏳ |
| TC_SEC_004 | JWT token tampering | Modified token | Token invalid, user logged out | ⏳ |

---

## Test Case Status Legend
- ⏳ Not Started
- 🔄 In Progress
- ✅ Passed
- ❌ Failed
## 10. Performance & Security

| Test ID | Scenario | Expected Result | Status |
|---------|----------|-----------------|--------|
| TC_PERF_001 | Response time | < 2 seconds | ⏳ |
| TC_PERF_002 | Concurrent users (100) | No errors | ⏳ |
| TC_SEC_001 | SQL injection | Request blocked | ⏳ |
| TC_SEC_002 | XSS prevention | Script sanitized | ⏳ |
| TC_SEC_003 | CSRF validation | Request rejected | ⏳ |

---

**Status Legend:** ⏳ Not Started | 🔄 In Progress | ✅ Passed | ❌ Failed