package com.bluepay.views;
import com.bluepay.main.BluePayApp;import com.bluepay.services.AuthenticationService;import com.bluepay.utils.Ui;import javafx.scene.Scene;import javafx.scene.control.*;import javafx.scene.layout.*;
public class RegistrationView extends BaseView {
 private final AuthenticationService auth;public RegistrationView(BluePayApp a,AuthenticationService s){super(a);auth=s;}
 public Scene build(){
  TextField name=Ui.field("Full name"),mobile=Ui.field("10-digit mobile"),email=Ui.field("Email address");PasswordField pw=Ui.password("Create password"),confirm=Ui.password("Confirm password");
  Button reg=Ui.button("CREATE ACCOUNT"),back=Ui.ghost("Back to login");
  VBox form=new VBox(9,new Label("Create your account"),Ui.label("Full name"),name,Ui.label("Mobile number"),mobile,Ui.label("Email"),email,Ui.label("Password"),pw,Ui.label("Confirm password"),confirm,reg,back);
  ((Label)form.getChildren().get(0)).setStyle("-fx-text-fill:white;-fx-font-size:22px;-fx-font-weight:bold;");
  reg.setOnAction(e->{try{String msg=auth.register(name.getText(),mobile.getText(),email.getText(),pw.getText(),confirm.getText());auth.login(mobile.getText(),pw.getText());Ui.info(msg);app.showDashboard();}catch(Exception ex){Ui.error(ex.getMessage());}});
  back.setOnAction(e->app.showLogin());return scene(authShell("Join the BLUEPAY demo experience",form));
 }
}