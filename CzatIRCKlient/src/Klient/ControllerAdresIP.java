package Klient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.swing.*;
import java.util.regex.Pattern;

public class ControllerAdresIP {
    private static final Pattern IPv4PATTERN = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    private static final Pattern IPv6PATTERN = Pattern.compile("^((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)::((?:[0-9A-Fa-f]{1,4}(?::[0-9A-Fa-f]{1,4})*)?)$");

    @FXML
    private TextField textField;
    @FXML
    private Button btnPolacz;
    @FXML
    private Label label;


    public void actionPolacz(ActionEvent actionEvent) {
        label.setVisible(false);
        String ip = textField.getText();
        if (checkIp(ip)) {
            Protocol prot = Protocol.getInstance(ip);
            if (prot != null) {
                Main.stage.setScene(Main.scenaStartowa);
                Main.stage.setTitle("Czat IRC");
            } else {
                label.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Niepoprawny adres ip!");
        }
    }



    private static boolean checkIp(final String ip) {
        if (ip.equals("localhost")) {
            return true;
        }
        boolean z;
        if( z=IPv4PATTERN.matcher(ip).matches()){return z;}
        else{ return IPv6PATTERN.matcher(ip).matches();}
    }

    public void onKeyPressedTextField(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) btnPolacz.fire();
    }

    public void onKeyPressedBtnPolacz(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) btnPolacz.fire();
    }
}
