package model;

public class Room {
    private int roomId;
    private String roomType;
    private String building;
    private int floor;
    private double pricePerWeek;
    private String status;

    public Room(int roomId, String roomType, String building, int floor, double pricePerWeek, String status) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.building = building;
        this.floor = floor;
        this.pricePerWeek = pricePerWeek;
        this.status = status;
    }

    public int getRoomId() { return roomId; }
    public String getRoomType() { return roomType; }
    public String getBuilding() { return building; }
    public int getFloor() { return floor; }
    public double getPricePerWeek() { return pricePerWeek; }
    public String getStatus() { return status; }
}