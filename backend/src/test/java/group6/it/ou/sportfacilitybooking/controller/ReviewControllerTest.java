package group6.it.ou.sportfacilitybooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import group6.it.ou.sportfacilitybooking.dto.ReviewCreateRequest;
import group6.it.ou.sportfacilitybooking.dto.ReviewDTO;
import group6.it.ou.sportfacilitybooking.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewController Unit Tests")
class ReviewControllerTest {

    @InjectMocks
    private ReviewController reviewController;

    private MockMvc mockMvc;

    @Mock
    private ReviewService reviewService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private ReviewDTO reviewDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).setValidator(new org.springframework.validation.Validator() { public boolean supports(Class<?> c) { return true; } public void validate(Object o, org.springframework.validation.Errors e) {} }).setCustomArgumentResolvers(new org.springframework.data.web.PageableHandlerMethodArgumentResolver()).build();
        reviewDTO = new ReviewDTO();
        reviewDTO.setId(1L);
        reviewDTO.setComment("Great place!");
    }

    @Test
    @DisplayName("Should get all reviews")
    void testGetAllReviews() throws Exception {
        when(reviewService.getAllReviews()).thenReturn(List.of(reviewDTO));

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get all reviews")
    void testGetAllReviews_Exception() throws Exception {
        when(reviewService.getAllReviews()).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should create review")
    void testCreateReview() throws Exception {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setRating((byte) 5);
        request.setComment("Great place!");

        when(reviewService.createReview(eq(1L), eq(1L), any(ReviewCreateRequest.class))).thenReturn(reviewDTO);

        mockMvc.perform(post("/api/reviews")
                .requestAttr("userId", 1L)
                .param("bookingId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when create review")
    void testCreateReview_Exception() throws Exception {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setRating((byte) 5);
        request.setComment("Great place!");

        when(reviewService.createReview(eq(1L), eq(1L), any(ReviewCreateRequest.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(post("/api/reviews")
                .requestAttr("userId", 1L)
                .param("bookingId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should return error if userId not found when creating review")
    void testCreateReviewNoUserId() throws Exception {
        ReviewCreateRequest request = new ReviewCreateRequest();
        request.setRating((byte) 5);
        request.setComment("Great place!");

        mockMvc.perform(post("/api/reviews")
                .param("bookingId", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should get reviews by facility")
    void testGetReviewsByFacility() throws Exception {
        Page<ReviewDTO> page = new PageImpl<>(List.of(reviewDTO));
        when(reviewService.getReviewsByFacility(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/reviews/facility/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when get reviews by facility")
    void testGetReviewsByFacility_Exception() throws Exception {
        when(reviewService.getReviewsByFacility(eq(1L), any(Pageable.class))).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(get("/api/reviews/facility/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should reply to review")
    void testReplyToReview() throws Exception {
        when(reviewService.replyToReview(1L, "Thanks!")).thenReturn(reviewDTO);

        mockMvc.perform(put("/api/reviews/1/reply")
                .param("reply", "Thanks!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("Should handle exception when reply to review")
    void testReplyToReview_Exception() throws Exception {
        when(reviewService.replyToReview(1L, "Thanks!")).thenThrow(new RuntimeException("Error"));

        mockMvc.perform(put("/api/reviews/1/reply")
                .param("reply", "Thanks!"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }
}
