package view;

import database.IBookingRepository;
import database.PostgresBookingRepository;
import model.Booking;
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
import java.time.LocalDate;

public class BookingsView extends BorderPane {

    private final IBookingRepository bookingRepository = new PostgresBookingRepository();
    private TableView<Booking> table;
    private ObservableList<Booking> observableBookingList;

    public BookingsView() {
        initUI();
        loadDataFromDatabase();
    }

    private void initUI() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #F5F7FA;");

        Label title = new Label("📅 Room Allocations & Bookings");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #111111;");

        table = new TableView<>();
        table.setPlaceholder(new Label("No booking database profiles fetched."));
        table.setPrefHeight(500);

        TableColumn<Booking, Integer> colId = new TableColumn<>("Booking ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colId.setPrefWidth(90);

        TableColumn<Booking, String> colName = new TableColumn<>("Student Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colName.setPrefWidth(180);

        TableColumn<Booking, String> colStudId = new TableColumn<>("Student ID");
        colStudId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colStudId.setPrefWidth(110);

        TableColumn<Booking, Integer> colRoom = new TableColumn<>("Room ID");
        colRoom.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        colRoom.setPrefWidth(90);

        TableColumn<Booking, LocalDate> colCheckIn = new TableColumn<>("Check-In Date");
        colCheckIn.setCellValueFactory(new PropertyValueFactory<>("checkInDate"));
        colCheckIn.setPrefWidth(130);

        TableColumn<Booking, LocalDate> colCheckOut = new TableColumn<>("Check-Out Date");
        colCheckOut.setCellValueFactory(new PropertyValueFactory<>("checkOutDate"));
        colCheckOut.setPrefWidth(130);

        TableColumn<Booking, Double> colAmount = new TableColumn<>("Total Cost (KES)");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colAmount.setPrefWidth(130);

        TableColumn<Booking, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(110);

        table.getColumns().addAll(colId, colName, colStudId, colRoom, colCheckIn, colCheckOut, colAmount, colStatus);

        Button add = new Button("➕ Create Booking");
        add.setStyle("-fx-background-color: #082C6C; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        add.setOnAction(e -> openAddBookingDialog());

        Button cancel = new Button("❌ Cancel Booking");
        cancel.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        cancel.setOnAction(e -> processCancellationAction());

        VBox content = new VBox(15,
                title,
                new HBox(10, add, cancel),
                table
        );

        setCenter(content);
    }

    public void loadDataFromDatabase() {
        observableBookingList = FXCollections.observableArrayList(bookingRepository.getAllBookings());
        table.setItems(observableBookingList);
    }

    private void openAddBookingDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Allocation Desk - New Allocation");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        Label lblName = new Label("Student Name:");
        TextField txtName = new TextField();

        Label lblStudId = new Label("Student ID:");
        TextField txtStudId = new TextField();

        Label lblRoom = new Label("Room ID:");
        TextField txtRoom = new TextField();

        Label lblIn = new Label("Check-In Date:");
        DatePicker pickerIn = new DatePicker(LocalDate.now());

        Label lblOut = new Label("Check-Out Date:");
        DatePicker pickerOut = new DatePicker(LocalDate.now().plusMonths(3));

        Label lblCost = new Label("Cost (KES):");
        TextField txtCost = new TextField();

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 12px;");

        grid.add(lblName, 0, 0);
        grid.add(txtName, 1, 0);
        grid.add(lblStudId, 0, 1);
        grid.add(txtStudId, 1, 1);
        grid.add(lblRoom, 0, 2);
        grid.add(txtRoom, 1, 2);
        grid.add(lblIn, 0, 3);
        grid.add(pickerIn, 1, 3);
        grid.add(lblOut, 0, 4);
        grid.add(pickerOut, 1, 4);
        grid.add(lblCost, 0, 5);
        grid.add(txtCost, 1, 5);
        grid.add(lblError, 1, 6);

        Button btnSave = new Button("Commit Allocation");
        btnSave.setStyle("-fx-background-color: #082C6C; -fx-text-fill: white; -fx-font-weight: bold;");
        
        btnSave.setOnAction(e -> {
            String name = txtName.getText().trim();
            String sId = txtStudId.getText().trim();
            String roomStr = txtRoom.getText().trim();
            String costStr = txtCost.getText().trim();
            LocalDate inDate = pickerIn.getValue();
            LocalDate outDate = pickerOut.getValue();

            if (name.isEmpty() || sId.isEmpty() || roomStr.isEmpty() || costStr.isEmpty() || inDate == null || outDate == null) {
                lblError.setText("All entry parameters are required.");
                return;
            }

            try {
                int roomId = Integer.parseInt(roomStr);
                double cost = Double.parseDouble(costStr);

                Booking newBooking = new Booking(0, name, sId, roomId, inDate, outDate, cost, "Confirmed");
                boolean success = bookingRepository.addBooking(newBooking);

                if (success) {
                    loadDataFromDatabase();
                    dialog.close();
                } else {
                    lblError.setText("Save failure. Check relational constraints.");
                }
            } catch (NumberFormatException ex) {
                lblError.setText("Verify type parsing bounds rules.");
            }
        });

        grid.add(btnSave, 1, 7);

        Scene scene = new Scene(grid, 420, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void processCancellationAction() {
        Booking selected = table.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Selection Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a target active booking item to cancel from the table grid layout list.");
            alert.showAndWait();
            return;
        }

        if ("Cancelled".equals(selected.getStatus())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("State Notice");
            alert.setHeaderText(null);
            alert.setContentText("The target profile item allocation is already flagged completely as Cancelled.");
            alert.showAndWait();
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel this booking allocation?", ButtonType.YES, ButtonType.NO);
        confirmAlert.setTitle("Confirm Cancellation");
        confirmAlert.setHeaderText(null);
        confirmAlert.showAndWait();

        if (confirmAlert.getResult() == ButtonType.YES) {
            boolean success = bookingRepository.cancelBooking(selected.getBookingId());
            if (success) {
                loadDataFromDatabase();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Database Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Failed to update the cancellation state in the database. Please verify relational constraints.");
                errorAlert.showAndWait();
            }
        }
    }
}