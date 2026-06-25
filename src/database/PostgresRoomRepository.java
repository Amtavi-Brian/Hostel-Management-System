package database;

import model.Room;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresRoomRepository implements IRoomRepository {

    private final DBConnection dbConnectionProvider = new DBConnection();

    @Override
    public List<Room> getAllRooms() {
        List<Room> list = new ArrayList<>();
        String query = "SELECT room_id, room_type, building, floor, price_per_week, status FROM rooms ORDER BY building ASC, room_id ASC";
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Room room = new Room(
                    rs.getInt("room_id"),
                    rs.getString("room_type"),
                    rs.getString("building"),
                    rs.getInt("floor"),
                    rs.getDouble("price_per_week"),
                    rs.getString("status")
                );
                list.add(room);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return list;
    }

    @Override
    public boolean addRoom(Room room) {
        String query = "INSERT INTO rooms (room_type, building, floor, price_per_week, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, room.getRoomType());
            pstmt.setString(2, room.getBuilding());
            pstmt.setInt(3, room.getFloor());
            pstmt.setDouble(4, room.getPricePerWeek());
            pstmt.setString(5, room.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateRoomStatus(int roomId, String status) {
        String query = "UPDATE rooms SET status = ? WHERE room_id = ?";
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, roomId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteRoom(int roomId) {
        String query = "DELETE FROM rooms WHERE room_id = ?";
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, roomId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }
    @Override
    public int getTotalRoomsCount() {
        String query = "SELECT COUNT(*) FROM rooms";
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }

    @Override
    public int getAvailableRoomsCount() {
        String query = "SELECT COUNT(*) FROM rooms WHERE status = 'Available'";
        try (Connection con = dbConnectionProvider.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }
}