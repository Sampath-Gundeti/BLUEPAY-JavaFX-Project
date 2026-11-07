package com.bluepay.views;
import com.bluepay.main.BluePayApp;import com.bluepay.utils.*;import javafx.geometry.*;import javafx.scene.*;import javafx.scene.control.*;import javafx.scene.layout.*;import javafx.scene.paint.Color;
public abstract class BaseView {
 protected final BluePayApp app; protected BaseView(BluePayApp app){this.app=app;}
 protected Scene scene(Node content){StackPane root=new StackPane(content);root.setStyle("-fx-background-color:"+Ui.BG+";");Scene s=new Scene(root,1180,780);s.getStylesheets().clear();return s;}
 protected VBox authShell(String subtitle,Node form){Label brand=new Label("B");brand.setStyle("-fx-background-color:#126FE4;-fx-text-fill:white;-fx-font-size:28px;-fx-font-weight:900;-fx-padding:12 20;-fx-background-radius:14;");Label name=new Label("BLUEPAY");name.setStyle("-fx-text-fill:white;-fx-font-size:25px;-fx-font-weight:900;");Label sub=new Label(subtitle);sub.setStyle("-fx-text-fill:#91A2BA;-fx-font-size:13px;");VBox box=new VBox(16,brand,name,sub,form);box.setAlignment(Pos.CENTER);box.setMaxWidth(420);box.setPadding(new Insets(36));box.setStyle("-fx-background-color:#111B2B;-fx-background-radius:22;-fx-border-color:#243956;-fx-border-radius:22;");return box;}
 protected void protect(){if(!SessionManager.getInstance().isAuthenticated())app.showLogin();}
}