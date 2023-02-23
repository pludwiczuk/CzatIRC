package Klient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage stage;
    public static Stage stage2;

    public static Scene scenaAdresIP;
    public static Scene scenaStartowa;
    public static Scene scenaLogowanie;
    public static Scene scenaRejestracja;
    public static Scene scenaOknoCzatu;

    public static boolean czyAdmin=false;

    @Override
    public void start(Stage primaryStage) throws Exception{
        scenaAdresIP = new Scene(FXMLLoader.load(getClass().getResource("views/ScenaAdresIP.fxml")));
        scenaStartowa = new Scene(FXMLLoader.load(getClass().getResource("views/ScenaStartowa.fxml")));
        scenaLogowanie = new Scene(FXMLLoader.load(getClass().getResource("views/ScenaLogowanie.fxml")));
        scenaRejestracja = new Scene(FXMLLoader.load(getClass().getResource("views/ScenaRejestracja.fxml")));
        //scenaOknoCzatu = new Scene(FXMLLoader.load(getClass().getResource("ScenaOknoCzatu.fxml")));
        //scenaOknoAdmina = new Scene(FXMLLoader.load(getClass().getResource("ScenaOknoAdmina.fxml")));

        stage = primaryStage;
        stage = new Stage();
        stage.setTitle("Czat IRC");
        stage.setScene(scenaAdresIP);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> {
            Protocol prot = Protocol.getInstanceToExit();
            if (prot != null) {
                prot.disconnect();
                System.out.println("DISCONNECT");
            }
        });
        stage.show();

        stage2 = new Stage();
//        stage2.setOnCloseRequest(event -> {
//            Protocol prot = Protocol.getInstanceToExit();
//            if (prot != null) {
//                prot.disconnect();
//                System.out.println("DISCONNECT");
//            }
//        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
