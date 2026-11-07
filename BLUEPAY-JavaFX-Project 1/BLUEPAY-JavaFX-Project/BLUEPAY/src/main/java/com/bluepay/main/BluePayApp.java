package com.bluepay.main;

import com.bluepay.database.DatabaseConnection;
import com.bluepay.services.*;
import com.bluepay.utils.SessionManager;
import com.bluepay.views.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BluePayApp extends Application {
    private Stage stage;
    private final AuthenticationService auth = new AuthenticationService();
    private final PaymentService payments = new PaymentService();
    private final AccountService accounts = new AccountService();
    private final SessionManager session = SessionManager.getInstance();

    @Override public void start(Stage primaryStage) {
        stage = primaryStage;
        DatabaseConnection.initialize();
        stage.setTitle("BLUEPAY • UPI Payment Simulator");
        stage.setMinWidth(980); stage.setMinHeight(680);
        showLogin();
        stage.show();
    }
    public void showLogin() { setScene(new LoginView(this, auth).build()); }
    public void showRegister() { setScene(new RegistrationView(this, auth).build()); }
    public void showDashboard() {
        if (!session.isAuthenticated()) { showLogin(); return; }
        setScene(new DashboardView(this, auth, payments, accounts).build());
    }
    public void logout() {
        if (new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION,
                "Are you sure you want to log out?", javafx.scene.control.ButtonType.YES,
                javafx.scene.control.ButtonType.NO).showAndWait().orElse(javafx.scene.control.ButtonType.NO)
                == javafx.scene.control.ButtonType.YES) {
            session.clear(); showLogin();
        }
    }
    private void setScene(Scene scene) { stage.setScene(scene); stage.centerOnScreen(); }
    @Override public void stop() { DatabaseConnection.close(); }
    public static void main(String[] args) { launch(args); }
}