package Klient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.swing.*;

public class ControllerScenaZmienHaslo {
    @FXML
    private Button buttonZmien;
    @FXML
    private PasswordField passwordField1;
    @FXML
    private PasswordField passwordField2;
    @FXML
    private PasswordField passwordField3;
    private Protocol prot = Protocol.getInstance("");

    public ControllerScenaZmienHaslo() {

    }

    public void actionButtonZmien(ActionEvent actionEvent) {
        String old = passwordField1.getText();
        String pass1 = passwordField2.getText();
        String pass2 = passwordField3.getText();
        if(prot.sprawdzStareHaslo(old).startsWith("0 OK")) {
            if(pass1.equals(pass2)) {
                String resp = prot.aktualizujHaslo(pass1);
                if(resp.startsWith("0 OK")) {
                    JOptionPane.showMessageDialog(null, "Pomyślnie zaktualizowano hasło!");
                }
                else {
                    String[] tab = resp.split(" ");
                    StringBuilder sb = new StringBuilder();
                    for(int i=2;i< tab.length;i++) sb.append(tab[i]).append(" ");
                    JOptionPane.showMessageDialog(null, sb);
                    JOptionPane.showMessageDialog(null, "Hasło powinno składać się z min 5 znaków i nie powinno zawierać żadnych białych i polskich znaków!");

                }
            }
            else {
                JOptionPane.showMessageDialog(null, "Nowe i powtórzone hasła są różne!");
            }
        }
        else {
            JOptionPane.showMessageDialog(null, "Podane stare hasło jest błędne!");
        }
    }

    public void onKeyPressedPasswordField1(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) buttonZmien.fire();
    }

    public void onKeyPressedPasswordField2(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) buttonZmien.fire();
    }

    public void onKeyPressedPasswordField3(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) buttonZmien.fire();
    }
}
