package group6.it.ou.sportfacilitybooking.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import group6.it.ou.sportfacilitybooking.entity.User;

@Repository
public interface TaiKhoanRepository extends JpaRepository<User, Integer> {
    User findByTenTaiKhoan(String tenTaiKhoan);
    List<User> findByRole(String role);
}