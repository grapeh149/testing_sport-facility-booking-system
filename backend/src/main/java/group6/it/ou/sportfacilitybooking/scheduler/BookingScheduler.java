package group6.it.ou.sportfacilitybooking.scheduler;

import group6.it.ou.sportfacilitybooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class BookingScheduler {

    @Autowired
    private BookingService bookingService;

    // Run every day at 00:01 AM
    @Scheduled(cron = "0 1 0 * * *")
    public void autoConfirmBookings() {
        System.out.println("[BookingScheduler] Running auto confirm job...");
        bookingService.autoConfirmPendingBookingsForNextDay();
    }
}
