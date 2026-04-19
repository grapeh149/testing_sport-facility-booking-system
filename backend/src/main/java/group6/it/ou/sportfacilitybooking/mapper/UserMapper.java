package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.dto.UserDTO;
import group6.it.ou.sportfacilitybooking.entity.User;
import group6.it.ou.sportfacilitybooking.entity.UserRole;

@Component
public class UserMapper {
    // Entity → DTO
    public UserDTO toDTO(User entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setUsername(entity.getUsername());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());
        if (entity.getRole() != null) {
            dto.setRole(entity.getRole().name());
        }
        dto.setIsActive(entity.getIsActive());
        dto.setAvatarUrl(entity.getAvatarUrl());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }

    // DTO → Entity
    public User toEntity(UserDTO dto) {
        User entity = new User();
        entity.setId(dto.getId());
        entity.setFullName(dto.getFullName());
        entity.setUsername(dto.getUsername());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());
        if (dto.getRole() != null) {
            entity.setRole(UserRole.valueOf(dto.getRole()));
        }
        entity.setIsActive(dto.getIsActive());
        entity.setAvatarUrl(dto.getAvatarUrl());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());
        return entity;
    }
}
