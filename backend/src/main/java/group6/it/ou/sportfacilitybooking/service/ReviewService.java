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

    // SELECT * FROM Review
    public List<ReviewDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SELECT WHERE MaReview = ?
    public ReviewDTO getById(Integer id) {
        Review entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Review với MaReview: " + id));
        return mapper.toDTO(entity);
    }

    // SELECT WHERE MaSan = ?
    public List<ReviewDTO> getBySan(Integer maSan) {
        return repository.findBySanTheThao_MaSan(maSan)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // SELECT WHERE MaKH = ?
    public List<ReviewDTO> getByKhachHang(Integer maKH) {
        return repository.findByKhachHang_MaKH(maKH)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    // INSERT
    public ReviewDTO create(ReviewDTO dto) {
        Review entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    // UPDATE
    public ReviewDTO update(Integer id, ReviewDTO dto) {
        Review entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Review với MaReview: " + id));
        entity.setComment(dto.getComment());
        entity.setRating(dto.getRating());
        entity.setReviewDate(dto.getReviewDate());
        return mapper.toDTO(repository.save(entity));
    }

    // DELETE
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
