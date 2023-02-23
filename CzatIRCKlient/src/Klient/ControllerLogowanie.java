package Klient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.swing.*;
import java.io.IOException;

public class ControllerLogowanie {
    @FXML
    private TextField textField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button btnZaloguj;


    public void actionBtnZaloguj(ActionEvent actionEvent) throws IOException {
        Protocol prot = Protocol.getInstance("");
        String username = textField.getText();
        String password = passwordField.getText();

        String resp = prot.logowanie(username, password);
        if (resp.equals("0 OK")) {
            System.out.println("Zalogowano");
            Main.scenaOknoCzatu = new Scene(FXMLLoader.load(getClass().getResource("views/ScenaOknoCzatu.fxml")));
            Main.stage.setScene(Main.scenaOknoCzatu);
            Main.stage.setTitle("Czat IRC");
            Main.stage2.hide();
        } else if (resp.startsWith("2")) {
            System.out.println("Niezalogowano");
            JOptionPane.showMessageDialog(null, "Błędny login lub hasło!");
        } else if (resp.equals("0 OK admin")) {
            System.out.println("Zalogowano jako administrator");
            Main.czyAdmin=true;
            //Main.scenaOknoAdmina = new Scene(FXMLLoader.load(getClass().getResource("ScenaOknoAdmina.fxml")));
            Main.scenaOknoCzatu = new Scene(FXMLLoader.load(getClass().getResource("views/ScenaOknoCzatu.fxml")));
            Main.stage.setScene(Main.scenaOknoCzatu);
            Main.stage.setTitle("Czat IRC - admin");
            Main.stage2.hide();
        } else {
            JOptionPane.showMessageDialog(null, "Nie udało się zalogować!");
        }
    }

    public void onKeyPressedBtnZaloguj(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) btnZaloguj.fire();
    }

    public void onKeyPressedPasswordField(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) btnZaloguj.fire();
    }
}
