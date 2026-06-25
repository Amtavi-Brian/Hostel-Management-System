package view;

import database.IBookingRepository;
import database.PostgresBookingRepository;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Map;

public class ReportsView extends BorderPane {

    private final IBookingRepository bookingRepository = new PostgresBookingRepository();

    public ReportsView() {
        initUI();
    }

    private void initUI() {
        this.setPadding(new Insets(25));
        this.setStyle("-fx-background-color: #F5F7FA;");

        Label title = new Label("📈 Management Reports & Insights");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #111111;");

        Label description = new Label("Generate real-time analytical summaries pulled straight from active server data nodes.");
        description.setStyle("-fx-text-fill: #555555; -fx-font-size: 14px;");

        Button occupancyBtn = new Button("🛏️ Generate Occupancy Report");
        occupancyBtn.setPrefHeight(50);
        occupancyBtn.setStyle("-fx-background-color: #082C6C; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");

        Button revenueBtn = new Button("💳 Generate Financial Report");
        revenueBtn.setPrefHeight(50);
        revenueBtn.setStyle("-fx-background-color: #10B981; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");

        occupancyBtn.setOnAction(e -> {
            Map<String, Integer> data = bookingRepository.getOccupancyBreakdown();
            
            int available = 0;
            int occupied = 0;
            int maintenance = 0;
            
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                if (entry.getKey() != null) {
                    String statusKey = entry.getKey().trim();
                    if (statusKey.equalsIgnoreCase("Available")) {
                        available = entry.getValue();
                    } else if (statusKey.equalsIgnoreCase("Occupied")) {
                        occupied = entry.getValue();
                    } else if (statusKey.equalsIgnoreCase("Maintenance")) {
                        maintenance = entry.getValue();
                    }
                }
            }
            
            int total = available + occupied + maintenance;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Campus Crib - Occupancy Report");
            alert.setHeaderText("Active Hostel Allocation Distributions");
            alert.setContentText(
                "Total Rooms Logged: " + total + "\n\n" +
                " - Available Rooms (Vacant): " + available + "\n" +
                " - Occupied Rooms (Allocated): " + occupied + "\n" +
                " - Under Maintenance (Locked): " + maintenance
            );
            alert.getDialogPane().setMinWidth(400);
            alert.showAndWait();
        });

        revenueBtn.setOnAction(e -> {
            Map<String, Double> data = bookingRepository.getRevenueAnalysis();
            
            // Defensive Check: Fallback if the database repository returns a null Map object
            if (data == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("System Notice");
                alert.setHeaderText("No Data Found");
                alert.setContentText("The financial query returned no records. Please ensure you have active bookings in your database.");
                alert.showAndWait();
                return;
            }
            
            // Safe extraction using robust getOrDefault rules
            double totalBookings = data.getOrDefault("TotalBookings", 0.0);
            double grossRevenue = data.getOrDefault("GrossRevenue", 0.0);
            double averageValue = data.getOrDefault("AverageValue", 0.0);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Campus Crib - Financial Performance Audit");
            alert.setHeaderText("Revenue & Billing Operational Analysis");
            alert.setContentText(String.format(
                "Active Booking Logs Evaluated: %.0f\n\n" +
                " - Total Collected Revenue: KES %,.2f\n" +
                " - Average Transaction Invoice Value: KES %,.2f", 
                totalBookings, grossRevenue, averageValue
            ));
            alert.getDialogPane().setMinWidth(450);
            alert.showAndWait();
        });

        HBox actionsRow = new HBox(15, occupancyBtn, revenueBtn);
        
        VBox content = new VBox(20,
                title,
                description,
                new Separator(),
                actionsRow
        );

        setCenter(content);
    }
}