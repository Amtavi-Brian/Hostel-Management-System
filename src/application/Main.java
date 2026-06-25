package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import view.LoginView;
import view.DashboardView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            LoginView loginView = new LoginView();
            
            Scene entryScene = new Scene(loginView, 500, 450);

            loginView.getLoginBtn().setOnAction(e -> {
                String inputUser = loginView.getUsername();
                String inputPass = loginView.getPassword();

                if (inputUser.equalsIgnoreCase("admin") && inputPass.equals("admin123")) {
                    try {
                        DashboardView dashboardView = new DashboardView();
                        
                        dashboardView.start(primaryStage);
                        
                    } catch (Exception ex) {
                        showModalAlert("Runtime Error", "Critical failure initializing main dashboard panel.");
                        ex.printStackTrace();
                    }
                } else {
                    showModalAlert("Invalid Credentials", "The administrative user credentials supplied are incorrect.");
                }
            });

            primaryStage.setTitle("Campus Crib - Gateway");
            primaryStage.setScene(entryScene);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showModalAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}