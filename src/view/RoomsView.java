package view;

import database.IRoomRepository;
import database.PostgresRoomRepository;
import model.Room;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class RoomsView extends BorderPane {

    private final IRoomRepository roomRepository = new PostgresRoomRepository();
    private TableView<Room> table;
    private ObservableList<Room> observableRoomList;

    public RoomsView() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #F5F7FA;");

        Label title = new Label("🏢 Hostel Room Inventory");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #111111;");

        table = new TableView<>();
        table.setPlaceholder(new Label("No inventory records found."));
        table.setPrefHeight(500);

        TableColumn<Room, Integer> colId = new TableColumn<>("Room ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        colId.setPrefWidth(90);

        TableColumn<Room, String> colType = new TableColumn<>("Room Type");
        colType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colType.setPrefWidth(160);

        TableColumn<Room, String> colBuilding = new TableColumn<>("Building Block");
        colBuilding.setCellValueFactory(new PropertyValueFactory<>("building"));
        colBuilding.setPrefWidth(140);

        TableColumn<Room, Integer> colFloor = new TableColumn<>("Floor Level");
        colFloor.setCellValueFactory(new PropertyValueFactory<>("floor"));
        colFloor.setPrefWidth(100);

        TableColumn<Room, Double> colPrice = new TableColumn<>("Price / Week (KES)");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerWeek"));
        colPrice.setPrefWidth(150);

        TableColumn<Room, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(120);

        table.getColumns().addAll(colId, colType, colBuilding, colFloor, colPrice, colStatus);

        Button addBtn = new Button("➕ Add Room");
        addBtn.setStyle("-fx-background-color: #082C6C; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        addBtn.setOnAction(e -> openAddRoomDialog());

        Button updateBtn = new Button("✏️ Change Status");
        updateBtn.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        updateBtn.setOnAction(e -> processStatusUpdate());

        Button deleteBtn = new Button("❌ Delete");
        deleteBtn.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        deleteBtn.setOnAction(e -> processDeletion());

        HBox actions = new HBox(10, addBtn, updateBtn, deleteBtn);

        VBox content = new VBox(15, title, actions, table);
        setCenter(content);

        loadDataFromDatabase();
    }

    public void loadDataFromDatabase() {
        observableRoomList = FXCollections.observableArrayList(roomRepository.getAllRooms());
        table.setItems(observableRoomList);
    }

    private void openAddRoomDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Inventory - New Room Entry");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        Label lblType = new Label("Room Type:");
        ComboBox<String> comboType = new ComboBox<>();
        comboType.getItems().addAll("Single Occupancy", "Double Share", "Four Share");
        comboType.setValue("Single Occupancy");

        Label lblBuilding = new Label("Building:");
        TextField txtBuilding = new TextField();
        txtBuilding.setPromptText("e.g. Block A");

        Label lblFloor = new Label("Floor Level:");
        TextField txtFloor = new TextField();
        txtFloor.setPromptText("e.g. 1");

        Label lblPrice = new Label("Price / Week:");
        TextField txtPrice = new TextField();

        Label lblStatus = new Label("Status:");
        ComboBox<String> comboStatus = new ComboBox<>();
        comboStatus.getItems().addAll("Available", "Occupied", "Maintenance");
        comboStatus.setValue("Available");

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");

        grid.add(lblType, 0, 0);
        grid.add(comboType, 1, 0);
        grid.add(lblBuilding, 0, 1);
        grid.add(txtBuilding, 1, 1);
        grid.add(lblFloor, 0, 2);
        grid.add(txtFloor, 1, 2);
        grid.add(lblPrice, 0, 3);
        grid.add(txtPrice, 1, 3);
        grid.add(lblStatus, 0, 4);
        grid.add(comboStatus, 1, 4);
        grid.add(lblError, 1, 5);

        Button btnSave = new Button("Register Room");
        btnSave.setStyle("-fx-background-color: #082C6C; -fx-text-fill: white; -fx-font-weight: bold;");

        btnSave.setOnAction(e -> {
            String type = comboType.getValue();
            String building = txtBuilding.getText().trim();
            String floorStr = txtFloor.getText().trim();
            String priceStr = txtPrice.getText().trim();
            String status = comboStatus.getValue();

            if (building.isEmpty() || floorStr.isEmpty() || priceStr.isEmpty()) {
                lblError.setText("All text parameters are required.");
                return;
            }

            try {
                int floor = Integer.parseInt(floorStr);
                double price = Double.parseDouble(priceStr);
                
                Room newRoom = new Room(0, type, building, floor, price, status);
                boolean success = roomRepository.addRoom(newRoom);

                if (success) {
                    loadDataFromDatabase();
                    dialog.close();
                } else {
                    lblError.setText("Database insertion transaction failed.");
                }
            } catch (NumberFormatException ex) {
                lblError.setText("Verify numeric parsing parameters.");
            }
        });

        grid.add(btnSave, 1, 6);

        Scene scene = new Scene(grid, 400, 320);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void processStatusUpdate() {
        Room selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a target room row.");
            alert.showAndWait();
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(selected.getStatus(), "Available", "Occupied", "Maintenance");
        dialog.setTitle("Operational Update");
        dialog.setHeaderText(null);
        dialog.setContentText("Select new state status assignment:");
        
        dialog.showAndWait().ifPresent(newStatus -> {
            boolean success = roomRepository.updateRoomStatus(selected.getRoomId(), newStatus);
            if (success) {
                loadDataFromDatabase();
            }
        });
    }

    private void processDeletion() {
        Room selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a target room row.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Purge selected room inventory record permanently?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait();

        if (confirm.getResult() == ButtonType.YES) {
            boolean success = roomRepository.deleteRoom(selected.getRoomId());
            if (success) {
                loadDataFromDatabase();
            } else {
                Alert error = new Alert(Alert.AlertType.ERROR, "Purge failed. Relational integrity constraints block deletion.");
                error.showAndWait();
            }
        }
    }
}