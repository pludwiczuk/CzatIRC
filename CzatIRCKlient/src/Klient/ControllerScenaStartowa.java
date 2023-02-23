package Klient;

import javafx.event.ActionEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import javax.swing.*;

public class ControllerScenaStartowa {

    public void actionBtnLogowanie(ActionEvent actionEvent) {
        showScenaLogowanie();
    }

    public void actionBtnRejestracja(ActionEvent actionEvent) {
        showScenaRejestracja();
//        Main.stage.setScene(Main.scenaRejestracja);
//        Main.stage.setTitle("Rejestracja");
//        JOptionPane.showMessageDialog(null, "Login powinien składać się z min 3 znaków i nie powinien zawierać żadnych białych i polskich znaków!\nHasło powinno składać się z min 5 znaków i nie powinno zawierać żadnych białych i polskich znaków!");
    }

    private void showScenaLogowanie() {
//        Main.stage2 = new Stage();
        Main.stage2.setScene(Main.scenaLogowanie);
        Main.stage2.setTitle("Logowanie");
        Main.stage2.setResizable(false);
        Main.stage2.show();
    }

    private void showScenaRejestracja() {
//        Main.stage2 = new Stage();
        Main.stage2.setScene(Main.scenaRejestracja);
        Main.stage2.setTitle("Rejestracja");
        Main.stage2.setResizable(false);
        Main.stage2.show();
        JOptionPane.showMessageDialog(null, "Login powinien składać się z min 3 znaków i nie powinien zawierać żadnych białych i polskich znaków!\nHasło powinno składać się z min 5 znaków i nie powinno zawierać żadnych białych i polskich znaków!");

    }

    public void onKeyPressedBtnLogowanie(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) showScenaLogowanie();
    }

    public void onKeyPressedBtnRejestracja(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) showScenaRejestracja();
    }
}
