package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

/**
 * Clean login panel layout UI for Campus Crib.
 * Modularized as a VBox component managed by the application launcher class.
 * @author brian
 */
public class LoginView extends VBox {

    private TextField usernameField;
    private PasswordField passwordField;
    private Button loginBtn;
    private Label errorLabel; // Added for Input Validation criteria feedback

    public LoginView() {
        initUI();
    }

    private void initUI() {
        // Layout Properties
        this.setAlignment(Pos.CENTER);
        this.setSpacing(20);
        this.setPadding(new Insets(40));
        this.setStyle("-fx-background-color: #F5F7FA;"); 

        // Styled layout card to group things nicely
        VBox loginCard = new VBox(15);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setMaxWidth(360);
        loginCard.setPadding(new Insets(35));
        loginCard.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-radius: 10;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5);"
        );

        Label title = new Label("🏨 Campus Crib Login");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #082C6C;");

        Label subtitle = new Label("Sign in to manage hosteling records");
        subtitle.setStyle("-fx-text-fill: gray; -fx-font-size: 13px;");

        // Input Fields
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefHeight(40);
        usernameField.setStyle("-fx-background-radius: 5;");

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-background-radius: 5;");

        // Error message text display
        errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-size: 13px; -fx-wrap-text: true;");

        // Interactive Button
        loginBtn = new Button("Login Securely");
        loginBtn.setPrefHeight(40);
        loginBtn.setPrefWidth(Double.MAX_VALUE); 
        loginBtn.setStyle(
                "-fx-background-color: #082C6C;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 14px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
        );

        // Build UI Hierarchy 
        loginCard.getChildren().addAll(title, subtitle, new Separator(), usernameField, passwordField, errorLabel, loginBtn);
        this.getChildren().add(loginCard);
    }

    // --- Exposed Accessors for Event Capture Routing ---
    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return passwordField.getText();
    }

    public Button getLoginBtn() {
        return loginBtn;
    }

    public Label getErrorLabel() {
        return errorLabel;
    }
}