package database;

import model.Booking;
import java.util.List;
import java.util.Map;

public interface IBookingRepository {
    List<Booking> getAllBookings();
    boolean addBooking(Booking booking);
    boolean cancelBooking(int bookingId);
    
    int getTotalBookingsCount();
    double getTotalRevenue();

    public Map<String, Double> getRevenueAnalysis();

    public Map<String, Integer> getOccupancyBreakdown();
}