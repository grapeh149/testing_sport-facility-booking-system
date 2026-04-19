package group6.it.ou.sportfacilitybooking.request;

public class ReviewCreateRequest {
    private Long bookingId;
    private Long facilityId;
    private Long userId;
    private String comment;
    private Integer rating;

    public ReviewCreateRequest() {}

    public ReviewCreateRequest(Long bookingId, Long facilityId, Long userId, 
                              String comment, Integer rating) {
        this.bookingId = bookingId;
        this.facilityId = facilityId;
        this.userId = userId;
        this.comment = comment;
        this.rating = rating;
    }

    // Getters & Setters
    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Long getFacilityId() { return facilityId; }
    public void setFacilityId(Long facilityId) { this.facilityId = facilityId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}
