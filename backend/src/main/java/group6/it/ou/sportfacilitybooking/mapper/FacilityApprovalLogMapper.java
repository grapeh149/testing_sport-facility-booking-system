package group6.it.ou.sportfacilitybooking.mapper;

import org.springframework.stereotype.Component;
import group6.it.ou.sportfacilitybooking.entity.FacilityApprovalLog;
import group6.it.ou.sportfacilitybooking.dto.UserDTO;

@Component
public class FacilityApprovalLogMapper {

    public void toApprovalSummary(FacilityApprovalLog log, UserDTO adminDTO) {
        // Maps approval log to admin DTO for response
        if (log != null && adminDTO != null) {
            if (log.getAdmin() != null) {
                adminDTO.setId(log.getAdmin().getId());
                adminDTO.setEmail(log.getAdmin().getEmail());
            }
        }
    }
}
