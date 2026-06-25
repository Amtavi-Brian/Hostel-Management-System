package model;

public class Student {
    private String studentId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String course;

    public Student(String studentId, String fullName, String email, String phoneNumber, String course) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.course = course;
    }

    public String getStudentId() { return studentId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getCourse() { return course; }
}