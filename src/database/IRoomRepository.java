package database;

import model.Room;
import java.util.List;

public interface IRoomRepository {
    List<Room> getAllRooms();
    boolean addRoom(Room room);
    boolean updateRoomStatus(int roomId, String status);
    boolean deleteRoom(int roomId);
    
    int getTotalRoomsCount();
    int getAvailableRoomsCount();
}