package model;

import java.time.LocalDate;

public class Payment {
    private int paymentId;
    private int bookingId;
    private double amount;
    private LocalDate paymentDate;
    private String paymentMethod;

    public Payment(int paymentId, int bookingId, double amount, LocalDate paymentDate, String paymentMethod) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
    }

    public int getPaymentId() { return paymentId; }
    public int getBookingId() { return bookingId; }
    public double getAmount() { return amount; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public String getPaymentMethod() { return paymentMethod; }
}