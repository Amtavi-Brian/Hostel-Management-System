package view;

import database.IStudentRepository;
import database.PostgresStudentRepository;
import model.Student;
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

public class StudentsView extends BorderPane {

    private final IStudentRepository studentRepository = new PostgresStudentRepository();
    private TableView<Student> table;
    private ObservableList<Student> observableStudentList;

    public StudentsView() {
        this.setPadding(new Insets(20));
        this.setStyle("-fx-background-color: #F5F7FA;");

        Label title = new Label("🎓 Student Management Profiles");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #111111;");

        table = new TableView<>();
        table.setPlaceholder(new Label("No student database records fetched."));
        table.setPrefHeight(500);

        TableColumn<Student, String> colId = new TableColumn<>("Student ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        colId.setPrefWidth(120);

        TableColumn<Student, String> colName = new TableColumn<>("Full Name");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colName.setPrefWidth(200);

        TableColumn<Student, String> colEmail = new TableColumn<>("Email Address");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(220);

        TableColumn<Student, String> colPhone = new TableColumn<>("Phone Number");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colPhone.setPrefWidth(140);

        TableColumn<Student, String> colCourse = new TableColumn<>("Course / Program");
        colCourse.setCellValueFactory(new PropertyValueFactory<>("course"));
        colCourse.setPrefWidth(120);

        table.getColumns().addAll(colId, colName, colEmail, colPhone, colCourse);

        Button add = new Button("➕ Add Student");
        add.setStyle("-fx-background-color: #082C6C; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        Button update = new Button("✏️ Update");
        update.setStyle("-fx-background-color: #F59E0B; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        Button delete = new Button("❌ Delete");
        delete.setStyle("-fx-background-color: #EF4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

        add.setOnAction(e -> openAddStudentDialog());

        HBox buttons = new HBox(10, add, update, delete);

        VBox content = new VBox(15, title, buttons, table);
        setCenter(content);

        loadDataFromDatabase();
    }

    public void loadDataFromDatabase() {
        observableStudentList = FXCollections.observableArrayList(studentRepository.getAllStudents());
        table.setItems(observableStudentList);
    }

    private void openAddStudentDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Registration Panel - New Student");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        Label lblId = new Label("Student ID:");
        TextField txtId = new TextField();
        txtId.setPromptText("e.g. ST-1015");

        Label lblName = new Label("Full Name:");
        TextField txtName = new TextField();

        Label lblEmail = new Label("Email:");
        TextField txtEmail = new TextField();

        Label lblPhone = new Label("Phone:");
        TextField txtPhone = new TextField();

        Label lblCourse = new Label("Course:");
        TextField txtCourse = new TextField();

        Label lblError = new Label();
        lblError.setStyle("-fx-text-fill: #EF4444; -fx-font-size: 12px;");

        grid.add(lblId, 0, 0);
        grid.add(txtId, 1, 0);
        grid.add(lblName, 0, 1);
        grid.add(txtName, 1, 1);
        grid.add(lblEmail, 0, 2);
        grid.add(txtEmail, 1, 2);
        grid.add(lblPhone, 0, 3);
        grid.add(txtPhone, 1, 3);
        grid.add(lblCourse, 0, 4);
        grid.add(txtCourse, 1, 4);
        grid.add(lblError, 1, 5);

        Button btnSave = new Button("Save Student");
        btnSave.setStyle("-fx-background-color: #082C6C; -fx-text-fill: white; -fx-font-weight: bold;");
        
        btnSave.setOnAction(e -> {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String email = txtEmail.getText().trim();
            String phone = txtPhone.getText().trim();
            String course = txtCourse.getText().trim();

            if (id.isEmpty() || name.isEmpty() || email.isEmpty() || phone.isEmpty() || course.isEmpty()) {
                lblError.setText("All fields are required!");
                return;
            }

            Student newStudent = new Student(id, name, email, phone, course);
            boolean success = studentRepository.addStudent(newStudent);

            if (success) {
                loadDataFromDatabase();
                dialog.close();
            } else {
                lblError.setText("Database save failure. Check constraints.");
            }
        });

        grid.add(btnSave, 1, 6);

        Scene scene = new Scene(grid, 400, 350);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}