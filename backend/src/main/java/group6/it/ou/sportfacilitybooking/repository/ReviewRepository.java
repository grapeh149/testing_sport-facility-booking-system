package group6.it.ou.sportfacilitybooking.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import group6.it.ou.sportfacilitybooking.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByKhachHang_MaKH(Integer maKH);
    List<Review> findBySanTheThao_MaSan(Integer maSan);
}
