package database;

import model.Booking;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgresBookingRepository implements IBookingRepository {

    private final DBConnection dbConnectionProvider = new DBConnection();

    @Override
    public boolean cancelBooking(int bookingId) {
        String query = "UPDATE bookings SET status = 'Cancelled' WHERE booking_id = ?";
        
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            
            pstmt.setInt(1, bookingId);
            int rowsUpdated = pstmt.executeUpdate();
            return rowsUpdated > 0;
            
        } catch (SQLException e) {
            System.err.println("Error executing cancelBooking: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int getTotalBookingsCount() {
        String query = "SELECT COUNT(*) FROM bookings";
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error executing getTotalBookingsCount: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public double getTotalRevenue() {
        String query = "SELECT COALESCE(SUM(total_amount), 0.0) FROM bookings WHERE status != 'Cancelled'";
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Error executing getTotalRevenue: " + e.getMessage());
        }
        return 0.0;
    }

    @Override
    public List<Booking> getAllBookings() {
        List<Booking> list = new ArrayList<>();
        String query = "SELECT booking_id, student_name, student_id, room_id, check_in_date, check_out_date, total_amount, status FROM bookings ORDER BY booking_id DESC";
        
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Booking booking = new Booking(
                    rs.getInt("booking_id"),
                    rs.getString("student_name"),
                    rs.getString("student_id"),
                    rs.getInt("room_id"),
                    rs.getDate("check_in_date").toLocalDate(),
                    rs.getDate("check_out_date").toLocalDate(),
                    rs.getDouble("total_amount"),
                    rs.getString("status")
                );
                list.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Error executing getAllBookings: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Map<String, Integer> getOccupancyBreakdown() {
        Map<String, Integer> stats = new HashMap<>();
        String query = "SELECT status, COUNT(*) as count FROM rooms GROUP BY status";
        
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                stats.put(rs.getString("status"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Error executing getOccupancyBreakdown: " + e.getMessage());
        }
        return stats;
    }

    @Override
    public Map<String, Double> getRevenueAnalysis() {
        Map<String, Double> financials = new HashMap<>();
        String query = "SELECT COUNT(*), COALESCE(SUM(total_amount), 0.0), COALESCE(AVG(total_amount), 0.0) FROM bookings WHERE status != 'Cancelled'";
        
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            if (rs.next()) {
                financials.put("TotalBookings", (double) rs.getInt(1));
                financials.put("GrossRevenue", rs.getDouble(2));
                financials.put("AverageValue", rs.getDouble(3));
            }
        } catch (SQLException e) {
            System.err.println("Error executing getRevenueAnalysis: " + e.getMessage());
        }
        return financials;
    }

    @Override
    public boolean addBooking(Booking booking) {
        String query = "INSERT INTO bookings (student_name, student_id, room_id, check_in_date, check_out_date, total_amount, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            
            pstmt.setString(1, booking.getStudentName());
            pstmt.setString(2, booking.getStudentId());
            pstmt.setInt(3, booking.getRoomId());
            pstmt.setDate(4, java.sql.Date.valueOf(booking.getCheckInDate()));
            pstmt.setDate(5, java.sql.Date.valueOf(booking.getCheckOutDate()));
            pstmt.setDouble(6, booking.getTotalAmount());
            pstmt.setString(7, booking.getStatus());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error executing addBooking: " + e.getMessage());
            return false;
        }
    }
}