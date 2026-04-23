package group6.it.ou.sportfacilitybooking.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import group6.it.ou.sportfacilitybooking.dto.ReviewDTO;
import group6.it.ou.sportfacilitybooking.entity.Review;
import group6.it.ou.sportfacilitybooking.mapper.ReviewMapper;
import group6.it.ou.sportfacilitybooking.repository.ReviewRepository;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository repository;

    @Autowired
    private ReviewMapper mapper;

    public List<ReviewDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public ReviewDTO getById(Long id) {
        Review entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Review với id: " + id));
        return mapper.toDTO(entity);
    }

    public List<ReviewDTO> getByFacility(Long facilityId) {
        return repository.findByFacility_Id(facilityId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getByUser(Long userId) {
        return repository.findByUser_Id(userId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReviewDTO> getByBooking(Long bookingId) {
        return repository.findByBooking_Id(bookingId)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    public ReviewDTO create(ReviewDTO dto) {
        Review entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    public ReviewDTO update(Long id, ReviewDTO dto) {
        Review entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Review với id: " + id));
        entity.setComment(dto.getComment());
        entity.setRating(dto.getRating());
        return mapper.toDTO(repository.save(entity));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
