package group6.it.ou.sportfacilitybooking.dto.DatSan.CalendarDTO;

public class SlotStatusDTO {
    private Integer maGio;
    private String status; // "PENDING" | "CONFIRMED | CANCELLED "

    public SlotStatusDTO() {}

    public SlotStatusDTO(Integer maGio, String status) {
        this.maGio = maGio;
        this.status = status;
    }

    public Integer getMaGio() { return maGio; }
    public void setMaGio(Integer maGio) { this.maGio = maGio; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
