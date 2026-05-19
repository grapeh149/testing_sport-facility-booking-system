package group6.it.ou.sportfacilitybooking.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = 
            http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                // Public endpoints - no authentication required
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/payments/vnpay/ipn").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/payments/vnpay/return").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/admin/health").permitAll()
                
                // Facility search endpoint - public read
                .requestMatchers(HttpMethod.GET, "/api/facilities").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/facilities/**").permitAll()
                
                // Court endpoints - public read
                .requestMatchers(HttpMethod.GET, "/api/courts/**").permitAll()

                // Booking schedule endpoint - public read for court detail calendar
                .requestMatchers(HttpMethod.GET, "/api/bookings/court/**").permitAll()

                // Sport type endpoints - public read for search filter
                .requestMatchers(HttpMethod.GET, "/api/sport-types/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/sport-types").permitAll()
                
                // TimeSlot endpoints - public read
                .requestMatchers(HttpMethod.GET, "/api/timeslots/**").permitAll()

                // Review endpoints - public read
                .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                
                // Facility images endpoints - public read
                .requestMatchers(HttpMethod.GET, "/api/facility-images/**").permitAll()
                

                //Only owner cancel/confirm booking
                .requestMatchers(HttpMethod.POST, "/api/bookings/*/cancel").hasRole("OWNER")
                .requestMatchers(HttpMethod.POST, "/api/bookings/*/cancel").hasRole("CUSTOMER")
                
                // Only owner can check in booking
                .requestMatchers(HttpMethod.POST, "/api/checkins").hasRole("OWNER")

                .requestMatchers(HttpMethod.POST, "/api/bookings/*/confirm").hasRole("OWNER")

                // Chỉ ADMIN mới được approve/reject facility
                .requestMatchers(HttpMethod.POST, "/api/facilities/*/approve").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/facilities/*/reject").hasRole("ADMIN")
                // All other endpoints require authentication
                .anyRequest().authenticated()
            );

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
