package view;

import database.IBookingRepository;
import database.IRoomRepository;
import database.PostgresBookingRepository;
import database.PostgresRoomRepository;
import model.Booking;
import model.Room;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class DashboardView extends Application {

    private StackPane centerPane;
    private VBox defaultDashboardContent;

    private final IRoomRepository roomRepository = new PostgresRoomRepository();
    private final IBookingRepository bookingRepository = new PostgresBookingRepository();

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();

        VBox sidebar = new VBox(15);
        sidebar.setPrefWidth(250);
        sidebar.setStyle("-fx-background-color:#082C6C;");
        sidebar.setPadding(new Insets(20));

        Label logo = new Label("🏨 Campus Crib");
        logo.setStyle("-fx-text-fill:white; -fx-font-size:24px; -fx-font-weight:bold;");

        Label subtitle = new Label("Hostel Booking System");
        subtitle.setStyle("-fx-text-fill:white; -fx-font-size:12px;");

        Button dashboardBtn = createMenuButton("📊 Dashboard");
        Button roomsBtn = createMenuButton("🛏️ Rooms");
        Button bookingsBtn = createMenuButton("📅 Bookings");
        Button studentsBtn = createMenuButton("🎓 Students");
        Button paymentsBtn = createMenuButton("💳 Payments");
        Button reportsBtn = createMenuButton("📈 Reports");
        Button usersBtn = createMenuButton("👥 Users");
        Button settingsBtn = createMenuButton("⚙️ Settings");

        centerPane = new StackPane();

        dashboardBtn.setOnAction(e -> {
            centerPane.getChildren().clear();
            refreshDashboardData();
            centerPane.getChildren().add(defaultDashboardContent);
        });

        roomsBtn.setOnAction(e -> {
            centerPane.getChildren().clear();
            centerPane.getChildren().add(new RoomsView());
        });

        studentsBtn.setOnAction(e -> {
            centerPane.getChildren().clear();
            centerPane.getChildren().add(new StudentsView());
        });

        bookingsBtn.setOnAction(e -> {
            centerPane.getChildren().clear();
            centerPane.getChildren().add(new BookingsView());
        });

        paymentsBtn.setOnAction(e -> {
            centerPane.getChildren().clear();
            centerPane.getChildren().add(new PaymentsView());
        });

        reportsBtn.setOnAction(e -> {
            centerPane.getChildren().clear();
            centerPane.getChildren().add(new ReportsView());
        });

        usersBtn.setOnAction(e -> {
            centerPane.getChildren().clear();
            centerPane.getChildren().add(new UsersView());
        });

        settingsBtn.setOnAction(e -> {
            centerPane.getChildren().clear();
            centerPane.getChildren().add(new SettingView()); 
        });

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = createMenuButton("🚪 Logout");
        logoutBtn.setOnAction(e -> stage.close());

        sidebar.getChildren().addAll(
                logo,
                subtitle,
                new Separator(),
                dashboardBtn,
                roomsBtn,
                bookingsBtn,
                studentsBtn,
                paymentsBtn,
                reportsBtn,
                usersBtn,
                settingsBtn,
                spacer,
                logoutBtn
        );

        root.setLeft(sidebar);

        refreshDashboardData();

        root.setCenter(centerPane);

        Scene scene = new Scene(root, 1400, 850);
        stage.setTitle("Campus Crib - Hostel Management Dashboard");
        stage.setScene(scene);
        stage.show();
    }

    private void refreshDashboardData() {
        int totalRoomsFromDb = roomRepository.getTotalRoomsCount();
        int availableRoomsFromDb = roomRepository.getAvailableRoomsCount();
        int totalBookingsFromDb = bookingRepository.getTotalBookingsCount();
        double revenueFromDb = bookingRepository.getTotalRevenue();

        defaultDashboardContent = new VBox(20);
        defaultDashboardContent.setPadding(new Insets(20));
        defaultDashboardContent.setStyle("-fx-background-color:#F5F7FA;");

        Label title = new Label("Dashboard Summary");
        title.setStyle("-fx-font-size:32px; -fx-font-weight:bold; -fx-text-fill: #111111;");

        Label welcome = new Label("Welcome back, Admin!");
        welcome.setStyle("-fx-text-fill:gray; -fx-font-size:14px;");

        VBox header = new VBox(5);
        header.getChildren().addAll(title, welcome);

        HBox cards = new HBox(20);
        cards.getChildren().addAll(
                createCard("Total Rooms", String.valueOf(totalRoomsFromDb)),
                createCard("Available Rooms", String.valueOf(availableRoomsFromDb)),
                createCard("Total Bookings", String.valueOf(totalBookingsFromDb)),
                createCard("Revenue Logged", String.format("KES %,.2f", revenueFromDb))
        );

        VBox searchPanel = new VBox(15);
        searchPanel.setPadding(new Insets(15));
        searchPanel.setStyle("-fx-background-color:white; -fx-background-radius:10;");

        Label searchTitle = new Label("Quick Room Lookup");
        searchTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333; -fx-font-size:15px;");

        HBox searchFields = new HBox(15);
        searchFields.setAlignment(Pos.CENTER_LEFT);

        DatePicker checkIn = new DatePicker();
        checkIn.setPromptText("Check In");

        DatePicker checkOut = new DatePicker();
        checkOut.setPromptText("Check Out");

        ComboBox<String> roomType = new ComboBox<>();
        roomType.setPromptText("Room Type");
        roomType.getItems().addAll("Single Occupancy", "Double Share", "Four Share");

        ComboBox<String> capacity = new ComboBox<>();
        capacity.setPromptText("Floor");
        capacity.getItems().addAll("0", "1", "2", "3");

        Button searchBtn = new Button("Search Availability");
        searchBtn.setStyle("-fx-background-color: #082C6C; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        searchFields.getChildren().addAll(checkIn, checkOut, roomType, capacity, searchBtn);
        searchPanel.getChildren().addAll(searchTitle, searchFields);

        HBox tablesSection = new HBox(20);
        VBox roomsPanel = createRoomsTablePreview();
        VBox bookingsPanel = createBookingsTablePreview();
        tablesSection.getChildren().addAll(roomsPanel, bookingsPanel);

        defaultDashboardContent.getChildren().addAll(header, cards, searchPanel, tablesSection);

        centerPane.getChildren().clear();
        centerPane.getChildren().add(defaultDashboardContent);
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(220);
        btn.setPrefHeight(45);
        btn.setStyle(
                "-fx-background-color:transparent;" +
                "-fx-text-fill:white;" +
                "-fx-font-size:15px;" +
                "-fx-alignment:center-left;" +
                "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color:#1e40af; -fx-text-fill:white; -fx-font-size:15px; -fx-alignment:center-left; -fx-cursor:hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color:transparent; -fx-text-fill:white; -fx-font-size:15px; -fx-alignment:center-left; -fx-cursor:hand;"));
        return btn;
    }

    private VBox createCard(String title, String value) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefWidth(260);
        card.setStyle(
                "-fx-background-color:white;" +
                "-fx-background-radius:10;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 8, 0, 0, 4);"
        );

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-text-fill: #666666; -fx-font-size: 14px;");

        Label lblValue = new Label(value);
        lblValue.setStyle("-fx-font-size:28px; -fx-font-weight:bold; -fx-text-fill: #082C6C;");

        card.getChildren().addAll(lblTitle, lblValue);
        return card;
    }

    private VBox createRoomsTablePreview() {
        VBox panel = new VBox(10);
        panel.setPrefWidth(560);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color:white; -fx-background-radius:10;");

        Label title = new Label("Live Room Status Preview");
        title.setStyle("-fx-font-weight: bold; -fx-text-fill:#444444;");

        TableView<Room> table = new TableView<>();
        table.setPlaceholder(new Label("No available inventory records."));
        table.setPrefHeight(300);

        TableColumn<Room, Integer> colId = new TableColumn<>("Room ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        colId.setPrefWidth(100);

        TableColumn<Room, String> colType = new TableColumn<>("Classification");
        colType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colType.setPrefWidth(180);

        TableColumn<Room, String> colBuilding = new TableColumn<>("Building");
        colBuilding.setCellValueFactory(new PropertyValueFactory<>("building"));
        colBuilding.setPrefWidth(140);

        TableColumn<Room, Double> colPrice = new TableColumn<>("Rate/Wk (KES)");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerWeek"));
        colPrice.setPrefWidth(130);

        table.getColumns().addAll(colId, colType, colBuilding, colPrice);
        table.setItems(FXCollections.observableArrayList(roomRepository.getAllRooms()));

        panel.getChildren().addAll(title, table);
        return panel;
    }

    private VBox createBookingsTablePreview() {
        VBox panel = new VBox(10);
        panel.setPrefWidth(560);
        panel.setPadding(new Insets(15));
        panel.setStyle("-fx-background-color:white; -fx-background-radius:10;");

        Label title = new Label("Recent Occupancy Allocations");
        title.setStyle("-fx-font-weight: bold; -fx-text-fill:#444444;");

        TableView<Booking> table = new TableView<>();
        table.setPlaceholder(new Label("No recent transaction logs."));
        table.setPrefHeight(300);

        TableColumn<Booking, String> student = new TableColumn<>("Student");
        student.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        student.setPrefWidth(220);

        TableColumn<Booking, Integer> room = new TableColumn<>("Room ID");
        room.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        room.setPrefWidth(140);

        TableColumn<Booking, String> status = new TableColumn<>("Status");
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        status.setPrefWidth(190);

        table.getColumns().addAll(student, room, status);
        table.setItems(FXCollections.observableArrayList(bookingRepository.getAllBookings()));

        panel.getChildren().addAll(title, table);
        return panel;
    }

    public static void main(String[] args) {
        launch(args);
    }
}