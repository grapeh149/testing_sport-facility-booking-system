# Test Data

**Project:** Sport Facility Booking System | **Updated:** May 15, 2026

---

## 1. Users

```json
{"id": "ADMIN", "email": "admin@sportfacility.com", "password": "Admin@123456", "role": "ADMIN"}
{"id": "FO001", "email": "owner1@badminton.com", "password": "Owner@123456", "role": "FACILITY_OWNER"}
{"id": "USER001", "email": "user1@example.com", "password": "User@123456", "role": "USER"}
{"id": "USER002", "email": "user2@example.com", "password": "User@123456", "role": "USER"}
```

---

## 2. Sport Types

```json
[
  {"id": "SPORT001", "name": "Badminton"},
  {"id": "SPORT002", "name": "Tennis"},
  {"id": "SPORT003", "name": "Basketball"}
]
```

---

## 3. Facilities

```json
{"id": "FAC001", "name": "Badminton Center HCM", "city": "Ho Chi Minh", "status": "APPROVED", "rating": 4.5}
{"id": "FAC002", "name": "Tennis Court Hanoi", "city": "Hanoi", "status": "APPROVED", "rating": 4.7}
{"id": "FAC003", "name": "Basketball Arena Da Nang", "city": "Da Nang", "status": "PENDING"}
```

---

## 4. Courts

```json
{"id": "COURT001", "facilityId": "FAC001", "name": "Court A1", "type": "Indoor", "price": 150000, "status": "AVAILABLE"}
{"id": "COURT002", "facilityId": "FAC001", "name": "Court A2", "type": "Indoor", "price": 150000, "status": "AVAILABLE"}
{"id": "COURT201", "facilityId": "FAC002", "name": "Tennis Court 1", "type": "Outdoor", "price": 200000, "status": "AVAILABLE"}
```

---

## 5. Bookings

```json
{"id": "BOOK001", "userId": "USER001", "courtId": "COURT001", "date": "2026-05-20", "status": "COMPLETED", "paymentStatus": "PAID"}
{"id": "BOOK002", "userId": "USER002", "courtId": "COURT002", "date": "2026-05-22", "status": "PENDING", "paymentStatus": "PENDING"}
{"id": "BOOK003", "userId": "USER001", "courtId": "COURT201", "date": "2026-05-21", "status": "CONFIRMED", "paymentStatus": "PAID"}
```

---

## 6. Payments

```json
{"id": "PAY001", "bookingId": "BOOK001", "amount": 150000, "status": "COMPLETED"}
{"id": "PAY002", "bookingId": "BOOK002", "amount": 150000, "status": "FAILED", "error": "Card declined"}
```

---

## 7. Reviews

```json
{"id": "REV001", "bookingId": "BOOK001", "facilityId": "FAC001", "rating": 5, "comment": "Great facility!"}
{"id": "REV002", "bookingId": "BOOK003", "facilityId": "FAC002", "rating": 4, "comment": "Good facility"}
```

---

## 8. Check-ins

```json
{"id": "CHECK001", "bookingId": "BOOK001", "userId": "USER001", "status": "CHECKED_IN"}
{"id": "CHECK002", "bookingId": "BOOK003", "userId": "USER001", "status": "CHECKED_IN"}
```

---

## 9. Edge Cases

```json
{"email": "invalid-email", "password": "short"}
{"startTime": "15:00", "endTime": "14:00"}
{"pricePerHour": -50000}
{"rating": 10}
```

---

**Notes:** 
- Phone numbers are placeholders
- Dates relative to May 15, 2026
- Test credentials only (not for production)
