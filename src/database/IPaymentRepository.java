package database;

import model.Payment;
import java.util.List;

public interface IPaymentRepository {
    List<Payment> getAllPayments();
    boolean addPayment(Payment payment);
}