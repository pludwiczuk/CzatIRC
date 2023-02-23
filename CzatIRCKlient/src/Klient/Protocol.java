package Klient;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;

public class Protocol {

    private Socket soc = null;
    private Scanner read;
    private PrintWriter write;
    private static Protocol instance = null;

    private Protocol(String ip, int port) {
        try {
            soc = new Socket(ip, port);

            read = new Scanner(soc.getInputStream());
            write = new PrintWriter(soc.getOutputStream(), true);

            System.out.println("Klient podlaczyl sie do serwera");
        } catch (IOException ex) {
            System.out.println("Nie udalo sie podlaczyc do serwera: " + ex.getMessage());
        }
    }

    public static Protocol getInstance(String ip) {
        if (instance == null) {
            instance = new Protocol(ip, 9999);
            if (instance.soc == null) {
                instance = null;
            }
        }
        return instance;
    }
    public static Protocol getInstanceToExit() {
        return instance;
    }

    public synchronized String odbierzWiadomosc(boolean thread) {
        System.out.println("Odbieranie wiadomosci");
        String mess = "";
        if (thread) {
            if (!read.hasNext("0") && !read.hasNext("1") && !read.hasNext("2") && !read.hasNext("3") && !read.hasNext("4") && !read.hasNext("5") && !read.hasNext("6") && !read.hasNext("7") && !read.hasNext("8") && !read.hasNext("9") && !read.hasNext("10") && !read.hasNext("11") && !read.hasNext("12") && !read.hasNext("13") && !read.hasNext("14") && !read.hasNext("15") && !read.hasNext("16") && !read.hasNext("17") && !read.hasNext("18") && !read.hasNext("19") && !read.hasNext("20") && !read.hasNext("21") && !read.hasNext("22") && !read.hasNext("23") && !read.hasNext("24") && !read.hasNext("25")) {
                String odb = read.nextLine();
                if (odb.charAt(0) == '/') {
                    mess = odb.substring(1);
                }
                //else mess = odb;
            }
            System.out.println("Wiadomosc odebrana przez thread");
        } else {
            if (read.hasNext()) {
                mess = read.nextLine();
            }
            System.out.println("Wiadomosc odebrana");
        }
        return mess;
    }

    public String rejestracja(String username, String password, String email) {
        System.out.println("Rejestracja");
        write.println("REGISTER " + username + " " + password + " " + email);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String logowanie(String username, String password) {
        System.out.println("Logowanie");
        write.println("LOGIN " + username + " " + password);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String wyslijWiadomosc(String mess) {
        System.out.println("Wysylanie wiadomosci");
        write.println("SEND " + mess);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public Vector<String> listaPokojow() {
        Vector<String> lista = new Vector<>();
        System.out.println("Pobieranie listy pokojów");
        write.println("LIST");
        String resp = odbierzWiadomosc(false);
        for (String napis : resp.split(" ")) {
            if (!napis.equals("0") && !napis.equals("OK")) {
                lista.add(napis);
            }
        }
        return lista;
    }

    public Vector<String> getAllRooms() {
        Vector<String> lista = new Vector<>();
        System.out.println("Pobieranie informacji o pokojach");
        write.println("GETALLROOMS");
        String resp = odbierzWiadomosc(false);
        String[] dataTab = resp.split(" ");
        if(dataTab.length > 2) {
            String data = resp.split(" ")[2];
            for (String s : data.split(";")) {
                lista.add(s);
            }
        }
        return lista;
    }

    public Vector<String> getAllUsers() {
        Vector<String> lista = new Vector<>();
        System.out.println("Pobieranie informacji o użytkownikach");
        write.println("GETALLUSERS");
        String resp = odbierzWiadomosc(false);
        String[] dataTab = resp.split(" ");
        if(dataTab.length > 2) {
            String data = resp.split(" ")[2];
            for (String s : data.split(";")) {
                lista.add(s);
            }
        }
        return lista;
    }

    public Vector<String> getBlackList() {
        Vector<String> lista = new Vector<>();
        System.out.println("Pobieranie informacji o czarnej liście");
        write.println("GETBLACKLIST");
        String resp = odbierzWiadomosc(false);
        String[] dataTab = resp.split(" ");
        if(dataTab.length > 2) {
            String data = resp.split(" ")[2];
            for (String s : data.split(";")) {
                lista.add(s);
            }
        }
        return lista;
    }

    public Vector<String> listaOnlineZPokoju() {
        Vector<String> lista = new Vector<>();
        System.out.println("Pobieranie listy użytkowników online");
        write.println("ONLINELIST");
        String resp = odbierzWiadomosc(false);
        for (String napis : resp.split(" ")) {
            if (!napis.equals("0") && !napis.equals("OK")) {
                lista.add(napis);
            }
        }
        return lista;
    }

    public Vector<String> getZaproszenia() {
        Vector<String> lista = new Vector<>();
        System.out.println("Pobieranie listy zaproszeń");
        write.println("GETINVITATIONS");
        String resp = odbierzWiadomosc(false);
        String[] dataTab = resp.split(" ");
        if(dataTab.length > 2) {
            String data = resp.split(" ")[2];
            for (String s : data.split(";")) {
                lista.add(s);
            }
        }
        return lista;
    }

    public Vector<String> getZnajomi() {
        Vector<String> lista = new Vector<>();
        System.out.println("Pobieranie listy znajomych");
        write.println("GETFRIENDS");
        String resp = odbierzWiadomosc(false);
        String[] dataTab = resp.split(" ");
        if(dataTab.length > 2) {
            String data = resp.split(" ")[2];
            for (String s : data.split(";")) {
                lista.add(s);
            }
        }
        return lista;
    }

    public String przyjmijZaproszenie(int idZaproszenia) {
        System.out.println("Przyjmowanie zaproszenia");
        write.println("ACCEPTINVITE "+idZaproszenia);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String usunZaproszenie(int idZaproszenia) {
        System.out.println("Usuwanie zaproszenia");
        write.println("DELETEINVITE "+idZaproszenia);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String usunZnajomego(int id) {
        System.out.println("Usuwanie zaproszenia");
        write.println("DELETEFRIEND "+id);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String aktualizujNazwePokoju(int id, String nowaNazwa) {
        System.out.println("Aktualizowanie nazwy pokoju");
        write.println("CHANGEROOMNAME "+id+" "+nowaNazwa);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String usunPokoj(int id) {
        System.out.println("Usuwanie pokoju");
        write.println("DELETEROOM "+id);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String dodajPokoj(String nazwa) {
        System.out.println("Dodawanie pokoju");
        write.println("ADDROOM "+nazwa);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String dolaczDoPokoju(String pokoj) {
        System.out.println("Dolaczanie do pokoju: " + pokoj);
        write.println("JOIN " + pokoj);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String dodajDoZnajomych(String login) {
        System.out.println("Dodawanie do znajomych: " + login);
        write.println("ADDFRIEND "+login);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String aktualizujDaneUzytkownika(int id, String login, String email, String czyAdmin, String czyZalogowany, String pokojId) {
        System.out.println("Aktualizowanie danych użytkownika");
        write.println("CHANGEUSERDATA "+id+" "+login+" "+email+" "+czyAdmin+" "+czyZalogowany+" "+pokojId);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String dodajUzytkownika(String login, String email, String czyAdmin, String czyZalogowany, String pokojId) {
        System.out.println("Dodawanie nowego użytkownika");
        write.println("ADDNEWUSER "+login+" "+email+" "+czyAdmin+" "+czyZalogowany+" "+pokojId);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String usunUzytkownika(String id) {
        System.out.println("Usuwanie użytkownika");
        write.println("DELETEUSER "+id);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String dodajEmailDoCzarnejListy(String email) {
        System.out.println("Usuwanie pokoju");
        write.println("ADDTOBLACKLIST "+email);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String usunEmailZCzarnejListy(String email) {
        System.out.println("Usuwanie emailu z czarnej listy");
        write.println("REMOVEEMAILFROMBLACKLIST "+email);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String sprawdzStareHaslo(String haslo) {
        System.out.println("Spradzanie hasła");
        write.println("CHECKOLDPASSWORD "+haslo);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String aktualizujHaslo(String haslo) {
        System.out.println("Aktualizacja hasła");
        write.println("CHANGEPASSWORD "+haslo);
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String opuscPokoj() {
        System.out.println("Opuszczanie pokoju");
        write.println("LEAVE");
        String resp = odbierzWiadomosc(false);
        return resp;
    }

    public String disconnect() {
        System.out.println("Disconnect");
        write.println("DISCONNECT");
        String resp = odbierzWiadomosc(false);
        return resp;
    }
}
