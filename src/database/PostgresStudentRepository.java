package database;

import model.Student;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresStudentRepository implements IStudentRepository {
    
    private final DBConnection dbConnectionProvider = new DBConnection();

    @Override
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String query = "SELECT student_id, full_name, email, phone_number, course FROM students ORDER BY full_name ASC";
        
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Student student = new Student(
                    rs.getString("student_id"),
                    rs.getString("full_name"),
                    rs.getString("email"),
                    rs.getString("phone_number"),
                    rs.getString("course")
                );
                list.add(student);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    @Override
    public boolean addStudent(Student student) {
        String query = "INSERT INTO students (student_id, full_name, email, phone_number, course) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getEmail());
            pstmt.setString(4, student.getPhoneNumber());
            pstmt.setString(5, student.getCourse());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
}