package group6.it.ou.sportfacilitybooking.dto.DatSan.CalendarDTO;

import java.time.LocalDate;
import java.util.List;

public class DayCalendarDTO {
    private LocalDate date;
    private List<SlotStatusDTO> bookedSlots;

    public DayCalendarDTO() {}

    public DayCalendarDTO(LocalDate date, List<SlotStatusDTO> bookedSlots) {
        this.date = date;
        this.bookedSlots = bookedSlots;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public List<SlotStatusDTO> getBookedSlots() { return bookedSlots; }
    public void setBookedSlots(List<SlotStatusDTO> bookedSlots) { this.bookedSlots = bookedSlots; }
}
