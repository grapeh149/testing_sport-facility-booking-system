package group6.it.ou.sportfacilitybooking.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db")
    public Map<String, Object> checkDb() {
        String result = jdbcTemplate.queryForObject(
            "SELECT 'Connected to: ' + DB_NAME() AS status",
            String.class
        );
        return Map.of("status", "OK", "message", result);
    }

    @GetMapping("/khachhang")
    public List<Map<String, Object>> getKhachHang() {
        return jdbcTemplate.queryForList("SELECT * FROM KhachHang");
    }
}