package group6.it.ou.sportfacilitybooking.scheduler;

import group6.it.ou.sportfacilitybooking.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookingScheduler Unit Tests")
class BookingSchedulerTest {

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingScheduler bookingScheduler;

    @Test
    @DisplayName("Should auto confirm bookings")
    void testAutoConfirmBookings() {
        bookingScheduler.autoConfirmBookings();
        verify(bookingService).autoConfirmPendingBookingsForNextDay();
    }
}
