package database;

import model.Payment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresPaymentRepository implements IPaymentRepository {

    private final DBConnection dbConnectionProvider = new DBConnection();

    @Override
    public List<Payment> getAllPayments() {
        List<Payment> list = new ArrayList<>();
        String query = "SELECT payment_id, booking_id, amount, payment_date, payment_method FROM payments ORDER BY payment_id DESC";
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Payment payment = new Payment(
                    rs.getInt("payment_id"),
                    rs.getInt("booking_id"),
                    rs.getDouble("amount"),
                    rs.getDate("payment_date").toLocalDate(),
                    rs.getString("payment_method")
                );
                list.add(payment);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    @Override
    public boolean addPayment(Payment payment) {
        String query = "INSERT INTO payments (booking_id, amount, payment_date, payment_method) VALUES (?, ?, ?, ?)";
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, payment.getBookingId());
            pstmt.setDouble(2, payment.getAmount());
            pstmt.setDate(3, java.sql.Date.valueOf(payment.getPaymentDate()));
            pstmt.setString(4, payment.getPaymentMethod());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}