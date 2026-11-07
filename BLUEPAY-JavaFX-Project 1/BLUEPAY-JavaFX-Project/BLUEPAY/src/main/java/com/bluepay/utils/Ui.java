package com.bluepay.utils;
import javafx.geometry.*;import javafx.scene.Node;import javafx.scene.control.*;import javafx.scene.layout.*;import javafx.scene.paint.Color;
public final class Ui {
 public static final String BG="#080D18", PANEL="#111B2B", BLUE="#167BEE", MUTED="#91A2BA";
 private Ui(){}
 public static Label title(String s){Label l=new Label(s);l.setStyle("-fx-font-size:25px;-fx-font-weight:800;-fx-text-fill:white;");return l;}
 public static Label label(String s){Label l=new Label(s);l.setStyle("-fx-text-fill:#A9B9CE;-fx-font-size:12px;");return l;}
 public static Label value(String s){Label l=new Label(s);l.setStyle("-fx-text-fill:white;-fx-font-size:15px;-fx-font-weight:bold;");return l;}
 public static Button button(String s){Button b=new Button(s);b.setMaxWidth(Double.MAX_VALUE);b.setStyle("-fx-background-color:linear-gradient(to right,#087CF0,#3455E8);-fx-text-fill:white;-fx-font-weight:bold;-fx-padding:12 18;-fx-background-radius:11;");return b;}
 public static Button ghost(String s){Button b=new Button(s);b.setMaxWidth(Double.MAX_VALUE);b.setStyle("-fx-background-color:#17263B;-fx-text-fill:#DCEAFF;-fx-padding:11 15;-fx-background-radius:10;-fx-border-color:#2A4262;-fx-border-radius:10;");return b;}
 public static TextField field(String prompt){TextField f=new TextField();f.setPromptText(prompt);f.setStyle("-fx-background-color:#091321;-fx-text-fill:white;-fx-prompt-text-fill:#64758C;-fx-border-color:#293D59;-fx-border-radius:9;-fx-background-radius:9;-fx-padding:12;");return f;}
 public static PasswordField password(String prompt){PasswordField f=new PasswordField();f.setPromptText(prompt);f.setStyle("-fx-background-color:#091321;-fx-text-fill:white;-fx-prompt-text-fill:#64758C;-fx-border-color:#293D59;-fx-border-radius:9;-fx-background-radius:9;-fx-padding:12;");return f;}
 public static VBox card(Node... nodes){VBox b=new VBox(12,nodes);b.setPadding(new Insets(20));b.setStyle("-fx-background-color:#111B2B;-fx-background-radius:17;-fx-border-color:#1E304A;-fx-border-radius:17;");return b;}
 public static void info(String s){new Alert(Alert.AlertType.INFORMATION,s,ButtonType.OK).showAndWait();}
 public static void error(String s){new Alert(Alert.AlertType.ERROR,s,ButtonType.OK).showAndWait();}
 public static boolean confirm(String s){return new Alert(Alert.AlertType.CONFIRMATION,s,ButtonType.YES,ButtonType.NO).showAndWait().orElse(ButtonType.NO)==ButtonType.YES;}
}