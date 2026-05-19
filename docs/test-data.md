# Test Data

**Project:** Sport Facility Booking System  
**Updated:** May 17, 2026

---

# 1. Users

```json
[
  {
    "id": 1,
    "email": "duc@gmail.com",
    "password": "buivanduc",
    "role": "ADMIN"
  },
  {
    "id": 2,
    "email": "duc1@gmail.com",
    "password": "buivanduc",
    "role": "OWNER"
  },
  {
    "id": 3,
    "email": "duc2@gmail.com",
    "password": "buivanduc",
    "role": "CUSTOMER"
  },
  {
    "id": 4,
    "email": "duc3@gmail.com",
    "password": "buivanduc",
    "role": "CUSTOMER"
  }
]
```

---

# 2. Sport Types

```json
[
  {
    "id": 1,
    "name": "Bóng đá 5 người"
  },
  {
    "id": 2,
    "name": "Bóng đá 7 người"
  },
  {
    "id": 3,
    "name": "Cầu lông"
  }
]
```

---

# 3. Facilities

```json
[
  {
    "id": 1,
    "name": "Sân cầu lông Nhà Bè",
    "city": "Hồ Chí Minh",
    "status": "APPROVED",
    "rating": 4.67
  },
  {
    "id": 2,
    "name": "Sân bóng đá Bình Thạnh",
    "city": "Hồ Chí Minh",
    "status": "APPROVED"
  },
  {
    "id": 5,
    "name": "Sân cầu lông Đầm Sen",
    "city": "Hồ Chí Minh",
    "status": "REJECTED"
  },
  {
    "id": 11,
    "name": "Sân bóng đá Quận 10",
    "city": "Hồ Chí Minh",
    "status": "SUSPENDED"
  }
]
```

---

# 4. Courts

```json
[
  {
    "id": 1,
    "facility_id": 1,
    "name": "Sân cầu lông số 1",
    "sport_type_id": 3,
    "is_indoor": true,
    "surface_type": "Thảm xanh",
    "is_active": false
  },
  {
    "id": 2,
    "facility_id": 1,
    "name": "Sân cầu lông số 2",
    "sport_type_id": 3,
    "is_indoor": true,
    "surface_type": "Thảm tím",
    "is_active": true
  },
  {
    "id": 3,
    "facility_id": 2,
    "name": "Sân bóng đá 5 người",
    "sport_type_id": 1,
    "is_indoor": false,
    "surface_type": "Sân cỏ nhân tạo",
    "is_active": true
  }
]
```

---

# 5. Bookings

```json
[
  {
    "id": 1,
    "booking_code": "SB-1775049614769-C3E27",
    "customer_id": 3,
    "court_id": 1,
    "status": "CANCELLED"
  },
  {
    "id": 3,
    "booking_code": "SB-1775050056660-E505D",
    "customer_id": 3,
    "court_id": 1,
    "status": "CHECKED_IN"
  },
  {
    "id": 6,
    "booking_code": "SB-1775049614769-C3E27",
    "customer_id": 2,
    "court_id": 1,
    "status": "PENDING_PAYMENT"
  }
]
```

---

# 6. Payments

```json
[
  {
    "id": 1,
    "booking_id": 2,
    "amount": 75000,
    "status": "PENDING"
  },
  {
    "id": 2,
    "booking_id": 3,
    "amount": 60000,
    "status": "SUCCESS"
  },
  {
    "id": 27,
    "booking_id": 38,
    "amount": 195,
    "status": "FAILED"
  }
]
```

---

# 7. Reviews

```json
[
  {
    "id": 1,
    "booking_id": 3,
    "customer_id": 3,
    "facility_id": 1,
    "rating": 4,
    "comment": "Sân đẹp"
  },
  {
    "id": 2,
    "booking_id": 7,
    "customer_id": 2,
    "facility_id": 1,
    "rating": 5,
    "comment": "Sân nhỏ, rẻ"
  }
]
```

---

# 8. Check-ins

```json
[
  {
    "id": 1,
    "booking_id": 5,
    "checked_by": 2,
    "note": "Owner check-in"
  },
  {
    "id": 2,
    "booking_id": 3,
    "checked_by": 2,
    "note": "Owner check-in"
  }
]
```

---

# 9. Time Slots

```json
[
  {
    "id": 1,
    "court_id": 1,
    "day_of_week": null,
    "start_time": "07:00:00",
    "end_time": "08:00:00",
    "price": 200000,
    "deposit_rate": 30.00,
    "is_active": true
  },
  {
    "id": 2,
    "court_id": 1,
    "day_of_week": null,
    "start_time": "08:00:00",
    "end_time": "09:00:00",
    "price": 250000,
    "deposit_rate": 30.00,
    "is_active": true
  },
  {
    "id": 3,
    "court_id": 1,
    "day_of_week": 1,
    "start_time": "09:00:00",
    "end_time": "10:00:00",
    "price": 300000,
    "deposit_rate": 30.00,
    "is_active": true
  }
]
```

---

# 10. Edge Cases

```json
[
  {
    "email": "invalid-email",
    "password": "short"
  },
  {
    "startTime": "15:00",
    "endTime": "14:00"
  },
  {
    "pricePerHour": -50000
  },
  {
    "rating": 10
  }
]
```
