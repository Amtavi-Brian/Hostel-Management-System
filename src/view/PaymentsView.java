package view;

import database.IPaymentRepository;
import database.PostgresPaymentRepository;
import model.Payment;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class PaymentsView extends BorderPane {

    private final IPaymentRepository paymentRepository = new PostgresPaymentRepository();
    private TableView<Payment> table;
    private ObservableList<Payment> observablePaymentList;

    public PaymentsView() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #F5F7FA;");

        Label title = new Label("💳 Financial Payments Ledger");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #111111;");

        table = new TableView<>();
        table.setPlaceholder(new Label("No transactional payment database profiles fetched."));
        table.setPrefHeight(500);

        TableColumn<Payment, Integer> colId = new TableColumn<>("Payment ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("paymentId"));
        colId.setPrefWidth(100);

        TableColumn<Payment, Integer> colBookingId = new TableColumn<>("Booking ID");
        colBookingId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colBookingId.setPrefWidth(120);

        TableColumn<Payment, Double> colAmount = new TableColumn<>("Amount (KES)");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setPrefWidth(140);

        TableColumn<Payment, LocalDate> colDate = new TableColumn<>("Payment Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        colDate.setPrefWidth(140);

        TableColumn<Payment, String> colMethod = new TableColumn<>("Payment Method");
        colMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        colMethod.setPrefWidth(150);

        table.getColumns().addAll(colId, colBookingId, colAmount, colDate, colMethod);

        Button add = new Button("➕ New Payment");
        add.setStyle("-fx-background-color: #082C6C; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        add.setOnAction(e -> openAddPaymentDialog());

        VBox content = new VBox(15, title, add, table);
        setCenter(content);

        loadDataFromDatabase();
    }

    public void loadDataFromDatabase() {
        observablePaymentList = FXCollections.observableArrayList(paymentRepository.getAllPayments());
        table.setItems(observablePaymentList);
    }

    private void openAddPaymentDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Process Transaction - New Payment");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        Label lblBookingId = new Label("Booking ID:");
        TextField txtBookingId = new TextField();

        Label lblAmount = new Label("Amount (KES):");
        TextField txtAmount = new TextField();

        Label lblMethod = new Label("Payment Method:");
        ComboBox<String> comboMethod = new ComboBox<>();
        comboMethod.getItems().addAll("M-Pesa", "Bank Transfer", "Cash", "Credit Card");
        comboMethod.setValue("M-Pesa");
        comboMethod.setPrefWidth(150);

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");

        grid.add(lblBookingId, 0, 0);
        grid.add(txtBookingId, 1, 0);
        grid.add(lblAmount, 0, 1);
        grid.add(txtAmount, 1, 1);
        grid.add(lblMethod, 0, 2);
        grid.add(comboMethod, 1, 2);
        grid.add(lblError, 1, 3);

        Button btnSave = new Button("Post Payment");
        btnSave.setStyle("-fx-background-color: #082C6C; -fx-text-fill: white; -fx-font-weight: bold;");

        btnSave.setOnAction(e -> {
            String bIdStr = txtBookingId.getText().trim();
            String amtStr = txtAmount.getText().trim();
            String method = comboMethod.getValue();

            if (bIdStr.isEmpty() || amtStr.isEmpty()) {
                lblError.setText("Fields cannot be empty!");
                return;
            }

            try {
                int bookingId = Integer.parseInt(bIdStr);
                double amount = Double.parseDouble(amtStr);

                Payment newPayment = new Payment(0, bookingId, amount, LocalDate.now(), method);
                boolean success = paymentRepository.addPayment(newPayment);

                if (success) {
                    loadDataFromDatabase();
                    dialog.close();
                } else {
                    lblError.setText("Save failure. Verify Booking ID.");
                }
            } catch (NumberFormatException ex) {
                lblError.setText("Invalid input types entered.");
            }
        });

        grid.add(btnSave, 1, 4);

        Scene scene = new Scene(grid, 380, 250);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}