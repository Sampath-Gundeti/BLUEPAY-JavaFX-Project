package com.bluepay.views;
import com.bluepay.main.BluePayApp;import com.bluepay.services.AuthenticationService;import com.bluepay.utils.Ui;import javafx.geometry.*;import javafx.scene.Scene;import javafx.scene.control.*;import javafx.scene.layout.*;
public class LoginView extends BaseView {
 private final AuthenticationService auth;public LoginView(BluePayApp a,AuthenticationService s){super(a);auth=s;}
 public Scene build(){
  TextField mobile=Ui.field("10-digit mobile number");PasswordField pass=Ui.password("Password");CheckBox remember=new CheckBox("Remember me");remember.setStyle("-fx-text-fill:#A9B9CE;");
  Button login=Ui.button("LOGIN TO BLUEPAY");Button register=Ui.ghost("Create new account");Button forgot=new Button("Forgot password?");forgot.setStyle("-fx-background-color:transparent;-fx-text-fill:#65AEFF;");
  VBox form=new VBox(12,new Label("Welcome back"),Ui.label("Mobile number"),mobile,Ui.label("Password"),pass,remember,login,forgot,register);((Label)form.getChildren().get(0)).setStyle("-fx-text-fill:white;-fx-font-size:22px;-fx-font-weight:bold;");
  login.setOnAction(e->{try{auth.login(mobile.getText(),pass.getText());app.showDashboard();}catch(Exception ex){Ui.error(ex.getMessage());}});
  register.setOnAction(e->app.showRegister());
  forgot.setOnAction(e->Ui.info("For this local demo, create a new account if you have forgotten your password. Password reset by SMS is not connected."));
  return scene(authShell("Your secure demo payment companion",form));
 }
}