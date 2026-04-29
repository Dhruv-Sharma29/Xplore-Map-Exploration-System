package controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import database.UserDAO;
import database.DBConnection;
import util.Session;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField routeField;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        DBConnection.initialize();
    }

    @FXML
    private void handleLogin() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();
        String route = routeField.getText().trim();

        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Please enter username and password.");
            return;
        }

        if (route.isEmpty()) {
            errorLabel.setText("Please enter at least one location.");
            return;
        }

        if (UserDAO.login(user, pass)) {
            Session.currentUser = user;
            Session.customRoute = route;
            loadMainUI();
        } else {
            errorLabel.setText("Invalid credentials. Try Registering.");
        }
    }

    @FXML
    private void handleRegister() {
        String user = usernameField.getText().trim();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            errorLabel.setText("Username and password required to register.");
            return;
        }

        if (UserDAO.register(user, pass)) {
            errorLabel.setStyle("-fx-text-fill: limegreen; -fx-font-weight: bold;");
            errorLabel.setText("Registered! You can now login.");
        } else {
            errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            errorLabel.setText("Username already exists.");
        }
    }

    private void loadMainUI() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/ui/MainUI.fxml")));
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
