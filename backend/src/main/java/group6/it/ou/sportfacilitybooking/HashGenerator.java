import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String password = "Password123!";
        String hash = encoder.encode(password);
        
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("");
        System.out.println("SQL INSERT template:");
        System.out.println("INSERT INTO users (id, full_name, email, password_hash, phone, role, is_active, avatar_url, created_at)");
        System.out.println("VALUES (1, N'Admin Hệ Thống', 'admin@sportbook.vn', '" + hash + "', '0800000001', 'ADMIN', 1, NULL, '2025-01-01 00:00:00');");
    }
}
