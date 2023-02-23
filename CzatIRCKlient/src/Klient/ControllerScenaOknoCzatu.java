package Klient;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.swing.*;
import java.io.IOException;
import java.util.Vector;

import javafx.scene.input.MouseEvent;

public class ControllerScenaOknoCzatu {
    @FXML
    private ListView listView;
    @FXML
    private ComboBox comboBoxListaPokojow;
    @FXML
    private ComboBox comboBoxCzyAdmin;
    @FXML
    private ComboBox comboBoxCzyZalogowany;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField textFieldWiadomosc;
    @FXML
    private TextField textFieldIdPokoju;
    @FXML
    private TextField textFieldIdUzytkownika;
    @FXML
    private TextField textFieldLoginUzytkownika;
    @FXML
    private TextField textFieldEmailUzytkownika;
    @FXML
    private TextField textFieldIdPokojuUzytkownika;
    @FXML
    private TextField textFieldNazwaPokoju;
    @FXML
    private TextField textFieldEmailZCzarnejListy;
    @FXML
    private TextField textFieldIdCzarnaLista;
    @FXML
    private Button buttonWyslij;
    @FXML
    private Button buttonOdswiez;
    @FXML
    private Button buttonDodaj;
    @FXML
    private Tab tabAdministrator;
    @FXML
    private TableView<Pokoj> tableViewPokoje;
    @FXML
    private TableView<Uzytkownik> tableViewUzytkownicy;
    @FXML
    private TableView<CzarnaLista> tableViewCzarnaLista;
    @FXML
    private TableView<Zaproszenie> tableViewZaproszenia;
    @FXML
    private TableView<Znajomy> tableViewListaZnajomych;

    private Thread thread;
    private Protocol prot = Protocol.getInstance("");
    private String aktualnyPokoj = null;

    public ControllerScenaOknoCzatu() {
        Platform.runLater(() -> {
            if(Main.czyAdmin) {
                tabAdministrator.setDisable(false);
                initializeTableViewPokoje();
                initializeTableViewUzytkownicy();
                initializeTableViewCzarnaLista();
                comboBoxCzyAdmin.getItems().addAll("T", "N");
                comboBoxCzyZalogowany.getItems().addAll("T", "N");
            }
            initializeComboBox();
            initializeTableViewZaproszenia();
            initializeTableViewListaZnajomych();
            runChat();
        });

    }
    public void initializeComboBox() {
        Vector<String> lista = prot.listaPokojow();
        comboBoxListaPokojow.getItems().addAll(lista);
    }
    public void runChat() {
        try {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        String wiadomosc = prot.odbierzWiadomosc(true);
                        if (!wiadomosc.equals("")) {
                            textArea.appendText(wiadomosc + "\n");
                        }
                    }
                }
            });
            thread.start();
            System.out.println("tread is working");
        } catch (Exception e) {
            System.out.println("Błąd tread: " + e.getMessage() + "\n" + e.getStackTrace());
        }
    }
    public void initializeListView() {
        Vector<String> lista = prot.listaOnlineZPokoju();
        listView.getItems().clear();
        listView.getItems().addAll(lista);
    }
    public void initializeTableViewPokoje() {
        Vector<String> lista = prot.getAllRooms();
        ObservableList<Pokoj> tab = FXCollections.observableArrayList();
        for(String el:lista) {
            tab.add(new Pokoj(Integer.parseInt(el.split(",")[0]),el.split(",")[1]));
        }
        TableColumn<Pokoj,Integer> col1 = new TableColumn<>("Id");
        col1.setMinWidth(200);
        col1.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Pokoj,Integer> col2 = new TableColumn<>("Nazwa pokoju");
        col2.setMinWidth(200);
        col2.setCellValueFactory(new PropertyValueFactory<>("nazwa"));

        tableViewPokoje.setItems(tab);
        tableViewPokoje.getColumns().clear();
        tableViewPokoje.getColumns().addAll(col1, col2);
    }
    public void initializeTableViewUzytkownicy() {
        Vector<String> lista = prot.getAllUsers();
        ObservableList<Uzytkownik> tab = FXCollections.observableArrayList();
        for(String el:lista) {
            tab.add(new Uzytkownik(Integer.parseInt(el.split(",")[0]), el.split(",")[1], el.split(",")[2], el.split(",")[3].charAt(0), el.split(",")[4].charAt(0), Integer.parseInt(el.split(",")[5])));
        }

        TableColumn<Uzytkownik, Integer> col1 = new TableColumn<>("Id");
        col1.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Uzytkownik, String> col2 = new TableColumn<>("Login");
        col2.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<Uzytkownik, String> col3 = new TableColumn<>("Adres e-mail");
        col3.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Uzytkownik, Character> col4 = new TableColumn<>("Czy Admin");
        col4.setCellValueFactory(new PropertyValueFactory<>("czyAdmin"));

        TableColumn<Uzytkownik, Character> col5 = new TableColumn<>("Czy Zalogowany");
        col5.setCellValueFactory(new PropertyValueFactory<>("czyZalogowany"));

        TableColumn<Uzytkownik, Integer> col6 = new TableColumn<>("Id Pokoju");
        col6.setCellValueFactory(new PropertyValueFactory<>("pokojId"));

        tableViewUzytkownicy.setItems(tab);
        tableViewUzytkownicy.getColumns().clear();
        tableViewUzytkownicy.getColumns().addAll(col1, col2, col3, col4, col5, col6);
    }

    public void initializeTableViewCzarnaLista() {
        Vector<String> lista = prot.getBlackList();
        ObservableList<CzarnaLista> tab = FXCollections.observableArrayList();
        for(String el:lista) {
            tab.add(new CzarnaLista(Integer.parseInt(el.split(",")[0]),el.split(",")[1]));
        }
        TableColumn<CzarnaLista,Integer> col1 = new TableColumn<>("Id");
        col1.setMinWidth(200);
        col1.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<CzarnaLista,String> col2 = new TableColumn<>("Adres e-mail");
        col2.setMinWidth(200);
        col2.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableViewCzarnaLista.setItems(tab);
        tableViewCzarnaLista.getColumns().clear();
        tableViewCzarnaLista.getColumns().addAll(col1, col2);
    }

    public void initializeTableViewZaproszenia() {
        Vector<String> lista = prot.getZaproszenia();
        ObservableList<Zaproszenie> tab = FXCollections.observableArrayList();
        for(String el:lista) {
            tab.add(new Zaproszenie(Integer.parseInt(el.split(",")[0]), Integer.parseInt(el.split(",")[1]), el.split(",")[2]));
        }
        TableColumn<Zaproszenie,Integer> col1 = new TableColumn<>("Id");
        col1.setMinWidth(100);
        col1.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Zaproszenie,Integer> col2 = new TableColumn<>("Id Nadawcy");
        col2.setMinWidth(100);
        col2.setCellValueFactory(new PropertyValueFactory<>("nadawcaId"));

        TableColumn<Zaproszenie,String> col3 = new TableColumn<>("Nazwa Użytkownika Nadawcy");
        col3.setMinWidth(200);
        col3.setCellValueFactory(new PropertyValueFactory<>("nadawcaLogin"));

        tableViewZaproszenia.setItems(tab);
        tableViewZaproszenia.getColumns().clear();
        tableViewZaproszenia.getColumns().addAll(col1,col2,col3);
    }

    public void initializeTableViewListaZnajomych() {
        Vector<String> lista = prot.getZnajomi();
        ObservableList<Znajomy> tab = FXCollections.observableArrayList();
        for(String el:lista) {
            tab.add(new Znajomy(Integer.parseInt(el.split(",")[0]), Integer.parseInt(el.split(",")[1]), el.split(",")[2]));
        }
        TableColumn<Znajomy,Integer> col1 = new TableColumn<>("Id");
        //col1.setMinWidth(100);
        col1.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Znajomy,Integer> col2 = new TableColumn<>("Id Znajomego");
        //col2.setMinWidth(100);
        col2.setCellValueFactory(new PropertyValueFactory<>("znajomyId"));

        TableColumn<Znajomy,String> col3 = new TableColumn<>("Nazwa Użytkownika");
        //col3.setMinWidth(200);
        col3.setCellValueFactory(new PropertyValueFactory<>("login"));

        tableViewListaZnajomych.setItems(tab);
        tableViewListaZnajomych.getColumns().clear();
        tableViewListaZnajomych.getColumns().addAll(col1,col2,col3);
    }

    public void actionButttonDolaczDoPokoju(ActionEvent actionEvent) {
        try {
            String pokoj = comboBoxListaPokojow.getValue().toString();
            System.out.println(pokoj);
            if (aktualnyPokoj == null) {
                String resp = prot.dolaczDoPokoju(pokoj);
                if (resp.equals("0 OK")) {
                    JOptionPane.showMessageDialog(null, "Dołączono do: " + pokoj);
                    buttonWyslij.setDisable(false);
                    textArea.setDisable(false);
                    textFieldWiadomosc.setDisable(false);
                    buttonDodaj.setDisable(false);
                    buttonOdswiez.setDisable(false);
                    aktualnyPokoj = pokoj;
                    initializeListView();
                } else {
                    JOptionPane.showMessageDialog(null, "Nie udało się dołączyć do: " + pokoj);
                }
            } else if (!pokoj.equals(aktualnyPokoj)) {
                String resp = prot.dolaczDoPokoju(pokoj);
                if (resp.equals("0 OK")) {
                    JOptionPane.showMessageDialog(null, "Dołączono do: " + pokoj);
                    textArea.setText("");
                    textFieldWiadomosc.setText("");
                    aktualnyPokoj = pokoj;
                    initializeListView();
                } else {
                    JOptionPane.showMessageDialog(null, "Nie udało się dołączyć do: " + pokoj);
                }
            }
        } catch (NullPointerException ex) {}
    }

    public void onActionButtonWyslij(ActionEvent actionEvent) {
        String resp = prot.wyslijWiadomosc(textFieldWiadomosc.getText());
        if (resp.equals("0 OK")) {
            textArea.appendText("TY: " + textFieldWiadomosc.getText() + "\n");
            textFieldWiadomosc.setText("");

        }
        else if(resp.startsWith("8")) {
            JOptionPane.showMessageDialog(null, "Wiadomość nie zostatła wysłana!\nDługość wiadomości powinna być większa od 0.");
            textFieldWiadomosc.setText("");
        } else {
            JOptionPane.showMessageDialog(null, "Wiadomość nie została wysłana!");
        }
    }

    public void actionButtonDodajDoZnajomych(ActionEvent actionEvent) {
        String username = listView.getSelectionModel().getSelectedItem().toString();
        String resp = prot.dodajDoZnajomych(username);
        if(resp.startsWith("0 OK")) {
            JOptionPane.showMessageDialog(null, "Wysłano zaproszenie użytkownikowi: "+username);
        }
        else {
            String[] tab = resp.split(" ");
            StringBuilder sb = new StringBuilder();
            for(int i=2;i< tab.length;i++) sb.append(tab[i]).append(" ");
            JOptionPane.showMessageDialog(null, sb);
        }
    }

    public void actionButtonOdswiezPokoje(ActionEvent actionEvent) {
        initializeTableViewPokoje();
    }

    public void actionButtonOdswiezUzytkownicy(ActionEvent actionEvent) {
        initializeTableViewUzytkownicy();
    }

    public void actionButtonOdswiezCzarnaLista(ActionEvent actionEvent) {
        initializeTableViewCzarnaLista();
    }

    public void actionButtonOdswiezZaproszenia(ActionEvent actionEvent) {
        initializeTableViewZaproszenia();
    }

    public void actionButtonOdswiezListaZnajomych(ActionEvent actionEvent) {
        initializeTableViewListaZnajomych();
    }

    public void actionButtonOdswiez(ActionEvent actionEvent) {
        initializeListView();
    }

    public void actionMenuItemWyjdz(ActionEvent actionEvent) {
        prot.disconnect();
        System.out.println("DISCONNECT");
        Platform.exit();
        System.exit(0);
    }

    public void actionMenuItemZmienHaslo(ActionEvent actionEvent) throws IOException {
        Main.stage2.setScene(new Scene(FXMLLoader.load(getClass().getResource("views/ScenaZmienHaslo.fxml"))));
        Main.stage2.setTitle("Zmień hasło");
        Main.stage2.show();
    }


    public void onKeyPressedTextFieldWiadomosc(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) buttonWyslij.fire();
    }

    public void onKeyPressedButtonWyslij(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) buttonWyslij.fire();
    }

    public void onKeyPressedButtonOdswiez(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) buttonOdswiez.fire();
    }

    public void onKeyPressedButtonDodaj(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER) buttonDodaj.fire();
    }

    public void onMouseClikedTableViewPokoje(MouseEvent mouseEvent) {
        Pokoj p = tableViewPokoje.getSelectionModel().getSelectedItem();
        textFieldIdPokoju.setText(Integer.toString(p.getId()));
        textFieldNazwaPokoju.setText(p.getNazwa());
    }

    public void onMouseClikedTableViewUzytkownicy(MouseEvent mouseEvent) {
        Uzytkownik u = tableViewUzytkownicy.getSelectionModel().getSelectedItem();
        textFieldIdUzytkownika.setText(Integer.toString(u.getId()));
        textFieldLoginUzytkownika.setText(u.getLogin());
        textFieldEmailUzytkownika.setText(u.getEmail());
        comboBoxCzyAdmin.setValue(u.getCzyAdmin());
        comboBoxCzyZalogowany.setValue(u.getCzyZalogowany());
        textFieldIdPokojuUzytkownika.setText(Integer.toString(u.getPokojId()));
    }

    public void onMouseClikedTableViewCzarnaLista(MouseEvent mouseEvent) {
        CzarnaLista c = tableViewCzarnaLista.getSelectionModel().getSelectedItem();
        textFieldIdCzarnaLista.setText(Integer.toString(c.getId()));
        textFieldEmailZCzarnejListy.setText(c.getEmail());
    }

    public void actionButtonAktualizujPokoj(ActionEvent actionEvent) {
        int id = Integer.parseInt(textFieldIdPokoju.getText());
        String nazwa = textFieldNazwaPokoju.getText();
        if(nazwa.contains(" ")) {
            JOptionPane.showMessageDialog(null, "Nazwa pokoju nie może zawierać spacji!!!");
        } else {
            String resp = prot.aktualizujNazwePokoju(id, nazwa);
            if(resp.startsWith("0 OK")) {
                JOptionPane.showMessageDialog(null, "Pomyślnie zaktualizowano nazwę pokoju!");
                initializeTableViewPokoje();
            }
            else {
                String[] tab = resp.split(" ");
                StringBuilder sb = new StringBuilder();
                for(int i=2;i< tab.length;i++) sb.append(tab[i]).append(" ");
                JOptionPane.showMessageDialog(null, sb);
            }
        }
    }

    public void actionButtonUsunPokoj(ActionEvent actionEvent) {
        int id = Integer.parseInt(textFieldIdPokoju.getText());
        String resp = prot.usunPokoj(id);
        if(resp.startsWith("0 OK")) {
            JOptionPane.showMessageDialog(null, "Pomyślnie usunięto pokój!");
            initializeTableViewPokoje();
        }
        else {
            String[] tab = resp.split(" ");
            StringBuilder sb = new StringBuilder();
            for(int i=2;i< tab.length;i++) sb.append(tab[i]).append(" ");
            JOptionPane.showMessageDialog(null, sb);
        }
    }

    public void actionButtonDodajPokoj(ActionEvent actionEvent) {
        String nazwa = textFieldNazwaPokoju.getText();
        String resp = prot.dodajPokoj(nazwa);
        if(resp.startsWith("0 OK")) {
            JOptionPane.showMessageDialog(null, "Pomyślnie dodano pokój!");
            initializeTableViewPokoje();
        }
        else {
            String[] tab = resp.split(" ");
            StringBuilder sb = new StringBuilder();
            for(int i=2;i< tab.length;i++) sb.append(tab[i]).append(" ");
            JOptionPane.showMessageDialog(null, sb);
        }
    }

    public void actionButtonAktualizujUzytkownika(ActionEvent actionEvent) {
        int id = Integer.parseInt(textFieldIdUzytkownika.getText());
        String login = textFieldLoginUzytkownika.getText();
        String email = textFieldEmailUzytkownika.getText();
        String czyAdmin = comboBoxCzyAdmin.getValue().toString();
        String czyZalogowany = comboBoxCzyZalogowany.getValue().toString();
        String pokojId = textFieldIdPokojuUzytkownika.getText();
        if(pokojId.equals("")) pokojId=null;
        if(pokojId.equals("0")) pokojId=null;
        String resp = prot.aktualizujDaneUzytkownika(id,login,email,czyAdmin,czyZalogowany,pokojId);
        if(resp.startsWith("0 OK")) {
            JOptionPane.showMessageDialog(null, "Pomyślnie zaktualizowno dane!");
            initializeTableViewUzytkownicy();
        }
        else {
            String[] tab = resp.split(" ");
            StringBuilder sb = new StringBuilder();
            for(int i=2;i< tab.length;i++) sb.append(tab[i]).append(" ");
            JOptionPane.showMessageDialog(null, sb);
        }
    }

    public void actionButtonDodajUzytkownika(ActionEvent actionEvent) {
        String login = textFieldLoginUzytkownika.getText();
        String email = textFieldEmailUzytkownika.getText();
        String czyAdmin = comboBoxCzyAdmin.getValue().toString();
        String czyZalogowany = comboBoxCzyZalogowany.getValue().toString();
        String pokojId = textFieldIdPokojuUzytkownika.getText();
        if(pokojId.equals("")) pokojId=null;
        if(pokojId.equals("0")) pokojId=null;
        String resp = prot.dodajUzytkownika(login,email,czyAdmin,czyZalogowany,pokojId);
        if(resp.startsWith("0 OK")) {
            JOptionPane.showMessageDialog(null, "Pomyślnie dodano nowego użytkownika!");
            initializeTableViewUzytkownicy();
        }
        else {
            String[] tab = resp.split(" ");
            StringBuilder sb = new StringBuilder();
            for(int i=2;i< tab.length;i++) sb.append(tab[i]).append(" ");
            JOptionPane.showMessageDialog(null, sb);
        }
    }

    public void actionButtonUsunUzytkownika(ActionEvent actionEvent) {
        String id = textFieldIdUzytkownika.getText();
        String resp = prot.usunUzytkownika(id);
        if(resp.startsWith("0 OK")) {
            JOptionPane.showMessageDialog(null, "Pomyślnie usunięto uzytkownika!");
            initializeTableViewUzytkownicy();
        }
        else {
            String[] tab = resp.split(" ");
            StringBuilder sb = new StringBuilder();
            for(int i=2;i< tab.length;i++) sb.append(tab[i]).append(" ");
            JOptionPane.showMessageDialog(null, sb);
        }
    }

    public void actionButtonDodajDoCzarnej(ActionEvent actionEvent) {
        String email = textFieldEmailUzytkownika.getText();
        String resp = prot.dodajEmailDoCzarnejListy(email);
        if(resp.startsWith("0 OK")) {
            JOptionPane.showMessageDialog(null, "Pomyślnie dodano email do czarnej listy!");
            initializeTableViewCzarnaLista();
        }
        else {
            String[] tab = resp.split(" ");
            StringBuilder sb = new StringBuilder();
            for(int i=2;i< tab.length;i++) sb.append(tab[i]).append(" ");
            JOptionPane.showMessageDialog(null, sb);
        }
    }

    public void actionButtonUsunZCzarnejListy(ActionEvent actionEvent) {
        String email = textFieldEmailZCzarnejListy.getText();
        String resp = prot.usunEmailZCzarnejListy(email);
        if(resp.startsWith("0 OK")) {
            JOptionPane.showMessageDialog(null, "Pomyślnie usunięto email z czarnej listy!");
            initializeTableViewCzarnaLista();
        }
        else {
            String[] tab = resp.split(" ");
            StringBuilder sb = new StringBuilder();
            for(int i=2;i< tab.length;i++) sb.append(tab[i]).append(" ");
            JOptionPane.showMessageDialog(null, sb);
        }
    }

    public void actionButtonPrzyjmijZaproszenie(ActionEvent actionEvent) {
        int id = tableViewZaproszenia.getSelectionModel().getSelectedItem().getId();
        String resp = prot.przyjmijZaproszenie(id);
        if(resp.startsWith("0 OK")) {
            JOptionPane.showMessageDialog(null, "Przyjęto zaproszenie!");
            initializeTableViewZaproszenia();
        }
        else {
            String[] tab = resp.split(" ");
            StringBuilder sb = new StringBuilder();
            for(int i=2;i< tab.length;i++) sb.append(tab[i]).append(" ");
            JOptionPane.showMessageDialog(null, sb);
        }
    }

    public void actionButtonDeleteInvite(ActionEvent actionEvent) {
        int id = tableViewZaproszenia.getSelectionModel().getSelectedItem().getId();
        String resp = prot.usunZaproszenie(id);
        if(resp.startsWith("0 OK")) {
            JOptionPane.showMessageDialog(null, "Usunięto zaproszenie!");
            initializeTableViewZaproszenia();
        }
        else {
            String[] tab = resp.split(" ");
            StringBuilder sb = new StringBuilder();
            for(int i=2;i< tab.length;i++) sb.append(tab[i]).append(" ");
            JOptionPane.showMessageDialog(null, sb);
        }
    }

    public void actionButtonDeleteFromFriends(ActionEvent actionEvent) {
        int id = tableViewListaZnajomych.getSelectionModel().getSelectedItem().getId();
        String resp = prot.usunZnajomego(id);
        if(resp.startsWith("0 OK")) {
            JOptionPane.showMessageDialog(null, "Usunięto znajomego!");
            initializeTableViewListaZnajomych();
        }
        else {
            String[] tab = resp.split(" ");
            StringBuilder sb = new StringBuilder();
            for(int i=2;i< tab.length;i++) sb.append(tab[i]).append(" ");
            JOptionPane.showMessageDialog(null, sb);
        }
    }
}
