package Klient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ControllerRejestracja {
    @FXML
    private Button btnZarejestruj;
    @FXML
    private TextField textFieldLogin;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private PasswordField passFieldHaslo;
    @FXML
    private PasswordField passFieldPowtorzHaslo;
    @FXML
    private Label label;


    public void actionZarejestruj(ActionEvent actionEvent) {
        Protocol prot = Protocol.getInstance("");
        String username = textFieldLogin.getText();
        String email = textFieldEmail.getText();
        String pass1 = passFieldHaslo.getText();
        String pass2 = passFieldPowtorzHaslo.getText();
        if (checkUsername(username)) {
            if (pass1.equals(pass2)) {
                if (checkPassword(pass1)) {
                    if (checkEmail(email)) {
                        label.setVisible(false);
                        String resp = prot.rejestracja(username, pass1, email);
                        System.out.println(resp);
                        if (resp.equals("0 OK")) {
                            System.out.println("Zarejestrowano");
                            JOptionPane.showMessageDialog(null, "Zarejestrowano");
                            Main.stage2.setTitle("Logowanie");
                            Main.stage2.setScene(Main.scenaLogowanie);
                            Main.stage2.setOnCloseRequest(event -> {
                                if (prot != null) {
                                    prot.disconnect();
                                    System.out.println("DISCONNECT");
                                }
                            });
                        } else if (resp.startsWith("1 ERROR")) {
                            JOptionPane.showMessageDialog(null, "Nazwa użytkownika jest już zajęta!");
                        } else if (resp.startsWith("10 ERROR")) {
                            JOptionPane.showMessageDialog(null,"Nie można założyć konta na podany adres email, ponieważ takie konto już istnieje.");
                        } else if (resp.startsWith("11 ERROR")) {
                            JOptionPane.showMessageDialog(null,"Nie można założyć konta na podany adres email, ponieważ email znajduje się na czarnej liście.");
                        } else if (resp.startsWith("12 ERROR")) {
                            JOptionPane.showMessageDialog(null, "Nieprawidłowy format emaila!");
                        } else if (resp.startsWith("7 ERROR")) {
                            JOptionPane.showMessageDialog(null, "Nieprawidłowy format hasła!");
                        } else if (resp.startsWith("6 ERROR")) {
                            JOptionPane.showMessageDialog(null, "Niepoprawny format loginu!");
                        }
                        else {
                            JOptionPane.showMessageDialog(null, "Nie udało się zarejestrować!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Niepoprawny adres email!");
                        textFieldEmail.setText("");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Niepoprawny format hasła!");
                    passFieldHaslo.setText("");
                    passFieldPowtorzHaslo.setText("");
                }
            } else {
                label.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Niepoprawny format loginu!");
            textFieldLogin.setText("");
        }
    }

    public void onKeyPressedBtnZarejestruj(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) btnZarejestruj.fire();
    }

    public void onKeyPressedTextFieldLogin(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) btnZarejestruj.fire();
    }

    public void onKeyPressedTextFieldEmail(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) btnZarejestruj.fire();
    }

    public void onKeyPressedPassHaslo(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) btnZarejestruj.fire();
    }

    public void onKeyPressedPassPowtorzHaslo(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) btnZarejestruj.fire();
    }


    private boolean checkPassword(String pass) {
        if (pass.length() >= 5) {
            for (int i = 0; i < pass.length(); i++) {
                if ((int) pass.charAt(i) < 33 || (int) pass.charAt(i) > 125) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
    private boolean checkUsername(String user) {
        if (user.length() >= 3) {
            for (int i = 0; i < user.length(); i++) {
                if ((int) user.charAt(i) < 33 || (int) user.charAt(i) > 125) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }
    private boolean checkEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
