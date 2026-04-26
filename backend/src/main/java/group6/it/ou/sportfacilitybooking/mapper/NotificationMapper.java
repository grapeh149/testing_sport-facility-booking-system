package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.entity.Notification;
import group6.it.ou.sportfacilitybooking.dto.NotificationDTO;

@Component
public class NotificationMapper {

    public NotificationDTO toDTO(Notification entity) {
        if (entity == null) return null;
        
        NotificationDTO dto = new NotificationDTO();
        dto.setId(entity.getId());
        dto.setUserId(entity.getUser().getId());
        dto.setType(entity.getType().toString());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setRefId(entity.getRefId());
        dto.setRefType(entity.getRefType());
        dto.setIsRead(entity.getIsRead());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    public Notification toEntity(NotificationDTO dto) {
        if (dto == null) return null;
        
        Notification entity = new Notification();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setMessage(dto.getMessage());
        entity.setRefId(dto.getRefId());
        entity.setRefType(dto.getRefType());
        entity.setIsRead(dto.getIsRead());
        return entity;
    }
}
