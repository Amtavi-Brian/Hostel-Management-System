package model;

import java.time.LocalDate;

public class Booking {
    private int bookingId;
    private String studentName;
    private String studentId;
    private int roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalAmount;
    private String status;

    public Booking(int bookingId, String studentName, String studentId, int roomId, 
                   LocalDate checkInDate, LocalDate checkOutDate, double totalAmount, String status) {
        this.bookingId = bookingId;
        this.studentName = studentName;
        this.studentId = studentId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getBookingId() { return bookingId; }
    public String getStudentName() { return studentName; }
    public String getStudentId() { return studentId; }
    public int getRoomId() { return roomId; }
    public LocalDate getCheckInDate() { return checkInDate; }
    public LocalDate getCheckOutDate() { return checkOutDate; }
    public double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
}