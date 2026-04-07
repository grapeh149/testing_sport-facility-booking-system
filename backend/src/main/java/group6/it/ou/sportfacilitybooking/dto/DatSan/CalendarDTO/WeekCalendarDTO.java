package group6.it.ou.sportfacilitybooking.dto.DatSan.CalendarDTO;

import java.time.LocalDate;
import java.util.List;

public class WeekCalendarDTO {
    private Integer week;
    private LocalDate from;
    private LocalDate to;
    private List<DayCalendarDTO> days;

    public WeekCalendarDTO() {}

    public WeekCalendarDTO(Integer week, LocalDate from, LocalDate to, List<DayCalendarDTO> days) {
        this.week = week;
        this.from = from;
        this.to = to;
        this.days = days;
    }

    public Integer getWeek() { return week; }
    public void setWeek(Integer week) { this.week = week; }

    public LocalDate getFrom() { return from; }
    public void setFrom(LocalDate from) { this.from = from; }

    public LocalDate getTo() { return to; }
    public void setTo(LocalDate to) { this.to = to; }

    public List<DayCalendarDTO> getDays() { return days; }
    public void setDays(List<DayCalendarDTO> days) { this.days = days; }
}
