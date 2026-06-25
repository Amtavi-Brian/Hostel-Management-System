package view;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SettingView extends BorderPane {

    public SettingView() {
        this.setPadding(new Insets(25));
        this.setStyle("-fx-background-color: #F5F7FA;");

        Label title = new Label("⚙️ System Settings");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #111111;");

        Label description = new Label("Configure and personalize your Campus Crib hostel application preferences.");
        description.setStyle("-fx-text-fill: #555555; -fx-font-size: 14px;");

        CheckBox darkMode = new CheckBox("Enable Dark Mode Interface Theme");
        darkMode.setStyle("-fx-font-size: 14px; -fx-cursor: hand;");

        CheckBox emailNotifications = new CheckBox("Send automated booking confirmation emails to students");
        emailNotifications.setStyle("-fx-font-size: 14px; -fx-cursor: hand;");
        emailNotifications.setSelected(true);

        CheckBox autoRefresh = new CheckBox("Enable auto-refresh loop on the main dashboard preview tables");
        autoRefresh.setStyle("-fx-font-size: 14px; -fx-cursor: hand;");

        Button save = new Button("Save Settings");
        save.setPrefHeight(40);
        save.setPrefWidth(150);
        save.setStyle("-fx-background-color: #082C6C; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");

        VBox content = new VBox(20,
                title,
                description,
                new Separator(),
                darkMode,
                emailNotifications,
                autoRefresh,
                save
        );

        setCenter(content);
    }
}