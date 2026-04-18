package group6.it.ou.sportfacilitybooking.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.repository.UserRepository;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("[UserDetailsService] Loading user details for email: {}", email);
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> {
                logger.warn("[UserDetailsService] User not found with email: {}", email);
                return new UsernameNotFoundException("User not found with email: " + email);
            });
        
        logger.debug("[UserDetailsService] User found: {} (ID: {}, Active: {})", 
            email, user.getId(), user.getIsActive());
        
        if (!user.getIsActive()) {
            logger.warn("[UserDetailsService] Account is disabled for user: {}", email);
            throw new UsernameNotFoundException("Account is disabled");
        }
        
        // Create authority from role
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(Collections.singletonList(authority))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.getIsActive())
                .build();
    }
}
