package view;

import database.IUserRepository;
import database.PostgresUserRepository;
import model.User;
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

public class UsersView extends BorderPane {

    private final IUserRepository userRepository = new PostgresUserRepository();
    private TableView<User> table;
    private ObservableList<User> observableUserList;

    public UsersView() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #F5F7FA;");

        Label title = new Label("👤 System Users Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #111111;");

        table = new TableView<>();
        table.setPlaceholder(new Label("No user database profiles fetched."));
        table.setPrefHeight(500);

        TableColumn<User, String> colUsername = new TableColumn<>("Username");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUsername.setPrefWidth(250);

        TableColumn<User, String> colPassword = new TableColumn<>("Password Hash / Plain");
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colPassword.setPrefWidth(250);

        table.getColumns().addAll(colUsername, colPassword);

        Button add = new Button("➕ Add User");
        add.setStyle("-fx-background-color: #082C6C; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");
        add.setOnAction(e -> openAddUserDialog());

        VBox content = new VBox(15, title, add, table);
        setCenter(content);

        loadDataFromDatabase();
    }

    public void loadDataFromDatabase() {
        observableUserList = FXCollections.observableArrayList(userRepository.getAllUsers());
        table.setItems(observableUserList);
    }

    private void openAddUserDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Configuration Panel - New Operator");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        Label lblUser = new Label("Username:");
        TextField txtUser = new TextField();

        Label lblPass = new Label("Password:");
        PasswordField txtPass = new PasswordField();

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");

        grid.add(lblUser, 0, 0);
        grid.add(txtUser, 1, 0);
        grid.add(lblPass, 0, 1);
        grid.add(txtPass, 1, 1);
        grid.add(lblError, 1, 2);

        Button btnSave = new Button("Register User");
        btnSave.setStyle("-fx-background-color: #082C6C; -fx-text-fill: white; -fx-font-weight: bold;");

        btnSave.setOnAction(e -> {
            String username = txtUser.getText().trim();
            String password = txtPass.getText();

            if (username.isEmpty() || password.isEmpty()) {
                lblError.setText("Fields cannot be empty!");
                return;
            }

            User newUser = new User(username, password);
            boolean success = userRepository.addUser(newUser);

            if (success) {
                loadDataFromDatabase();
                dialog.close();
            } else {
                lblError.setText("Save failure. Username may exist.");
            }
        });

        grid.add(btnSave, 1, 3);

        Scene scene = new Scene(grid, 350, 220);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}