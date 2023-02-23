package com.company;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket serw = new ServerSocket(9999);
        DBConnection baza = DBConnection.getInstance();
        Vector<Vector<PrintWriter>> vpw = new Vector<>();

        String OK = "0 OK";
        String[] rooms = baza.getListaPokojow();
        Map<String, Integer> roomsMap = new HashMap<>();
        for (int i = 0; i < rooms.length; i++) {
            vpw.add(new Vector<>());
            roomsMap.put(rooms[i], i);
        }
        Scanner scanner = new Scanner(System.in);
        File plikLogs;
        //System.out.println("Podaj pelna sciezke do pliku do zapisu logow. np: D:\\logi\\logs.txt");
        plikLogs = new File("logs.txt");

        boolean pliki = false;
        try {
            Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
            String test = "";
            output.append(test);
            output.close();
        } catch (Exception e) {
            System.out.println("Niepoprawna sciezka, sprawdz czy wszystkie katalogi istnieja.");
            System.out.println("Serwer zostanie teraz zatrzymany. Musisz go uruchomic ponownie.");
            pliki = true;
        }
        if (pliki) {
            System.exit(1);
        }

        System.out.println("Serwer wystartowal.");
        while (true) {

            Socket s = serw.accept();
            Scanner sk = new Scanner(s.getInputStream());
            PrintWriter pk = new PrintWriter(s.getOutputStream(), true);
            String ipAdd = s.getRemoteSocketAddress().toString();
            Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
            LocalDateTime ldt = LocalDateTime.now();
            String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Connected";
            output.append("\n" + ipIPort);
            output.close();
            Thread w;
            w = new Thread(new Runnable() {
                @Override
                public void run() {
                    String username = "";
                    boolean loggedin = false;
                    boolean czyAdmin = false;
                    int pokoj = 9999;
                    try {
                        while (true) {
                            if (sk.hasNext()) {
                                String wiadomosc = sk.nextLine();
                                if (wiadomosc.startsWith("REGISTER")) {
                                    String[] dane = wiadomosc.split(" ");
                                    if (dane.length == 4) {
                                        if (!baza.checkUsername(dane[1])) {
                                            pk.println("1 ERROR Ta nazwa uzytkownika jest juz zajeta.");
                                        } else if (!baza.checkEmail(dane[3])) {
                                            pk.println("10 ERROR Nie można założyć konta na podany adres email, ponieważ takie konto już istnieje.");
                                        } else {
                                            if (checkUsername(dane[1])) {
                                                if (checkPassword(dane[2])) {
                                                    if (checkEmail(dane[3])) {
                                                        if (baza.checkCzarnaLista(dane[3])) {
                                                            baza.dodajUzytkownika(dane[1], hasz(dane[2]), dane[3]);
                                                            username = dane[1];
                                                            loggedin = false;
                                                            pk.println(OK);
                                                            Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                                            LocalDateTime ldt = LocalDateTime.now();
                                                            String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Registered";
                                                            output.append("\n" + ipIPort);
                                                            output.close();
                                                        } else {
                                                            pk.println("11 ERROR Nie można założyć konta na podany adres email, ponieważ email znajduje się na czarnej liście.");
                                                        }
                                                    } else {
                                                        pk.println("12 ERROR Nieprawidłowy format emaila.");
                                                    }
                                                } else {
                                                    pk.println("7 ERROR Niepoprawny format hasla.");
                                                }
                                            } else {
                                                pk.println("6 ERROR Niepoprawny format loginu.");
                                            }
                                        }
                                    } else {
                                        pk.println("5 ERROR Niepoprawna komenda.");
                                    }
                                } else if (wiadomosc.startsWith("LOGIN")) {
                                    boolean poprawneLogowanie = false;
                                    czyAdmin = false;
                                    String[] dane = wiadomosc.split(" ");
                                    if (dane.length == 3) {
                                        dane[2] = hasz(dane[2]);
                                        String zaloguj = baza.zaloguj(dane[1], dane[2]);
                                        if (zaloguj.equals("uzytkownik")) {
                                            poprawneLogowanie = true;
                                        } else if (zaloguj.equals("admin")) {
                                            poprawneLogowanie = true;
                                            czyAdmin = true;
                                        }
                                        if (poprawneLogowanie) {
                                            username = dane[1];
                                            loggedin = true;
                                            if (!czyAdmin) pk.println(OK);
                                            else pk.println(OK + " admin");
                                            Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                            LocalDateTime ldt = LocalDateTime.now();
                                            String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Logged in";
                                            output.append("\n" + ipIPort);
                                            output.close();
                                        } else {
                                            pk.println("2 ERROR Niepoprawne dane logowania.");
                                        }
                                    } else {
                                        pk.println("5 ERROR Niepoprawna komenda.");
                                    }
                                } else if (wiadomosc.startsWith("LIST")) {
                                    StringBuilder resp = new StringBuilder(OK + " ");
                                    for (int i = 0; i < rooms.length; i++) resp.append(rooms[i] + " ");
                                    pk.println(resp);

                                    Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                    LocalDateTime ldt = LocalDateTime.now();
                                    String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested LIST";
                                    output.append("\n" + ipIPort);
                                    output.close();
                                } else if (wiadomosc.startsWith("JOIN")) {
                                    if (loggedin) {
                                        String[] dane = wiadomosc.split(" ");
                                        if (dane.length == 2) {
                                            boolean err = false;
                                            if (pokoj != 9999) vpw.get(pokoj).remove(pk);
                                            try {
                                                pokoj = roomsMap.get(dane[1]);
                                                vpw.get(pokoj).add(pk);
                                                int id = baza.checkIdPokoju(dane[1]);
                                                if (id != -1) {
                                                    baza.setNrPokoju(id, username);
                                                    err = false;
                                                } else {
                                                    err = true;
                                                }
                                            } catch (Exception e) {
                                                err = true;
                                            }
                                            if (!err) {
                                                pk.println(OK);
                                                Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                                LocalDateTime ldt = LocalDateTime.now();
                                                String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Joined: " + rooms[pokoj];
                                                output.append("\n" + ipIPort);
                                                output.close();
                                            } else {
                                                pk.println("3 ERROR Nieprawidlowa nazwa pokoju.");
                                            }
                                        } else {
                                            pk.println("5 ERROR Niepoprawna komenda.");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("SEND")) {
                                    if (loggedin) {
                                        String tekst = wiadomosc.replaceFirst("SEND", "");
                                        int dl = tekst.length();
                                        for (int i = 0; tekst.charAt(0) == ' ' && i < dl - 1; i++) {
                                            tekst = tekst.replaceFirst(" ", "");
                                        }
                                        if (tekst.length() > 1) {
                                            pk.println(OK);
                                            for (PrintWriter pw : vpw.get(pokoj)) {
                                                if (pw != pk) {
                                                    pw.println("/" + username + ": " + tekst);
                                                }
                                            }
                                        } else {
                                            pk.println("8 ERROR Dlugosc wiadomosci wynosi 0.");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("DISCONNECT")) {
                                    baza.wyloguj(username);
                                    pk.println(OK);
                                    break;
                                } else if (wiadomosc.startsWith("ONLINELIST")) {
                                    if (loggedin) {
                                        StringBuilder resp = new StringBuilder(OK + " ");
                                        int idPokoju = baza.checkIdPokoju(rooms[pokoj]);
                                        String[] list = baza.getUsersInRoom(idPokoju);
                                        for (String el : list) {
                                            if (!el.equals(username)) {
                                                resp.append(el + " ");
                                            }
                                        }
                                        pk.println(resp);

                                        Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                        LocalDateTime ldt = LocalDateTime.now();
                                        String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested ONLINELIST";
                                        output.append("\n" + ipIPort);
                                        output.close();
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("ADDFRIEND")) {
                                    if (loggedin) {
                                        String[] dane = wiadomosc.split(" ");
                                        if (dane.length == 2) {
                                            String odbiorca = dane[1];
                                            if (!baza.checkZaproszenia(username, odbiorca)) {
                                                if (!baza.checkZaproszenia(odbiorca, username)) {
                                                    if (!baza.checkZnajomi(username, odbiorca)) {
                                                        baza.wyslijZaproszenie(username, odbiorca);
                                                        pk.println("0 OK");
                                                    } else {
                                                        pk.println("15 ERROR Jesteście już znajomymi!");
                                                    }
                                                } else {
                                                    pk.println("14 ERROR Użytkownik wysłał zaproszenie tobie!");
                                                }
                                            } else {
                                                pk.println("13 ERROR Zaprosiłeś już tego użytkownika!");
                                            }
                                        } else {
                                            pk.println("5 ERROR Niepoprawna komenda.");
                                        }


                                        Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                        LocalDateTime ldt = LocalDateTime.now();
                                        String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested ONLINELIST";
                                        output.append("\n" + ipIPort);
                                        output.close();
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }

                                } else if (wiadomosc.startsWith("GETALLROOMS")) {
                                    if (loggedin) {
                                        if (czyAdmin) {
                                            StringBuilder resp = new StringBuilder(OK + " ");
                                            resp.append(baza.getAllRooms());
                                            pk.println(resp);

                                            Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                            LocalDateTime ldt = LocalDateTime.now();
                                            String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested GETALLROOMS";
                                            output.append("\n" + ipIPort);
                                            output.close();
                                        } else {
                                            pk.println("16 ERROR Nie posiadasz uprawnień administratora!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("GETALLUSERS")) {
                                    if (loggedin) {
                                        if (czyAdmin) {
                                            StringBuilder resp = new StringBuilder(OK + " ");
                                            resp.append(baza.getAllUsers());
                                            pk.println(resp);

                                            Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                            LocalDateTime ldt = LocalDateTime.now();
                                            String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested GETALLUSERS";
                                            output.append("\n" + ipIPort);
                                            output.close();
                                        } else {
                                            pk.println("16 ERROR Nie posiadasz uprawnień administratora!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("CHANGEROOMNAME")) {
                                    if (loggedin) {
                                        if (czyAdmin) {
                                            String[] data = wiadomosc.split(" ");
                                            if (baza.changeRoomName(Integer.parseInt(data[1]), data[2])) {
                                                pk.println(OK);

                                                Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                                LocalDateTime ldt = LocalDateTime.now();
                                                String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested CHANGEROOMNAME";
                                                output.append("\n" + ipIPort);
                                                output.close();
                                            } else {
                                                pk.println("17 ERROR Nie udało się zmienić nazwy!");
                                            }
                                        } else {
                                            pk.println("16 ERROR Nie posiadasz uprawnień administratora!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("DELETEROOM")) {
                                    if (loggedin) {
                                        if (czyAdmin) {
                                            String[] data = wiadomosc.split(" ");
                                            if (baza.deleteRoom(Integer.parseInt(data[1]))) {
                                                pk.println(OK);

                                                Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                                LocalDateTime ldt = LocalDateTime.now();
                                                String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested DELETEROOM";
                                                output.append("\n" + ipIPort);
                                                output.close();
                                            } else {
                                                pk.println("18 ERROR Nie udało się usunąć pokoju!");
                                            }
                                        } else {
                                            pk.println("16 ERROR Nie posiadasz uprawnień administratora!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("ADDROOM")) {
                                    if (loggedin) {
                                        if (czyAdmin) {
                                            String[] data = wiadomosc.split(" ");
                                            if (baza.addRoom(data[1])) {
                                                pk.println(OK);

                                                Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                                LocalDateTime ldt = LocalDateTime.now();
                                                String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested ADDROOM";
                                                output.append("\n" + ipIPort);
                                                output.close();
                                            } else {
                                                pk.println("19 ERROR Nie udało się dodać pokoju!");
                                            }
                                        } else {
                                            pk.println("16 ERROR Nie posiadasz uprawnień administratora!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("CHANGEUSERDATA")) {
                                    if (loggedin) {
                                        if (czyAdmin) {
                                            String[] dane = wiadomosc.split(" ");
                                            if (checkUsername(dane[2])) {
                                                if (checkEmail(dane[3])) {
                                                    if (baza.checkCzarnaLista(dane[3])) {
                                                        if (baza.aktualizujDaneUzytkownika(dane[1], dane[2], dane[3], dane[4], dane[5], dane[6])) {
                                                            pk.println(OK);
                                                            Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                                            LocalDateTime ldt = LocalDateTime.now();
                                                            String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested CHANGEUSERDATA";
                                                            output.append("\n" + ipIPort);
                                                            output.close();
                                                        } else {
                                                            pk.println("20 ERROR Nie udało się zmienić danych!");
                                                        }
                                                    } else {
                                                        pk.println("11 ERROR Nie można założyć konta na podany adres email, ponieważ email znajduje się na czarnej liście.");
                                                    }
                                                } else {
                                                    pk.println("12 ERROR Nieprawidłowy format emaila.");
                                                }
                                            } else {
                                                pk.println("6 ERROR Niepoprawny format loginu.");
                                            }
                                        } else {
                                            pk.println("16 ERROR Nie posiadasz uprawnień administratora!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jesteś zalogowany!");
                                    }
                                } else if (wiadomosc.startsWith("ADDNEWUSER")) {
                                    if (loggedin) {
                                        if (czyAdmin) {
                                            String[] dane = wiadomosc.split(" ");
                                            if (!baza.checkUsername(dane[1])) {
                                                pk.println("1 ERROR Ta nazwa uzytkownika jest juz zajeta.");
                                            } else if (!baza.checkEmail(dane[2])) {
                                                pk.println("10 ERROR Nie można założyć konta na podany adres email, ponieważ takie konto już istnieje.");
                                            } else {
                                                if (checkUsername(dane[1])) {
                                                    if (checkEmail(dane[2])) {
                                                        if (baza.checkCzarnaLista(dane[2])) {
                                                            if (baza.dodajNowegoUzytkownika(dane[1], hasz("default"), dane[2], dane[3], dane[4], dane[5])) {
                                                                pk.println(OK);
                                                                Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                                                LocalDateTime ldt = LocalDateTime.now();
                                                                String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested CHANGEUSERDATA";
                                                                output.append("\n" + ipIPort);
                                                                output.close();
                                                            } else {
                                                                pk.println("21 ERROR Nie udało się dodać użytkownika!");
                                                            }
                                                        } else {
                                                            pk.println("11 ERROR Nie można założyć konta na podany adres email, ponieważ email znajduje się na czarnej liście.");
                                                        }
                                                    } else {
                                                        pk.println("12 ERROR Nieprawidłowy format emaila.");
                                                    }
                                                } else {
                                                    pk.println("6 ERROR Niepoprawny format loginu.");
                                                }
                                            }
                                        } else {
                                            pk.println("16 ERROR Nie posiadasz uprawnień administratora!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jesteś zalogowany!");
                                    }
                                } else if (wiadomosc.startsWith("DELETEUSER")) {
                                    if (loggedin) {
                                        if (czyAdmin) {
                                            String[] data = wiadomosc.split(" ");
                                            if (baza.deleteUser(Integer.parseInt(data[1]))) {
                                                pk.println(OK);

                                                Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                                LocalDateTime ldt = LocalDateTime.now();
                                                String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested DELETEUSER";
                                                output.append("\n" + ipIPort);
                                                output.close();
                                            } else {
                                                pk.println("22 ERROR Nie udało się usunąć użytkownika!");
                                            }
                                        } else {
                                            pk.println("16 ERROR Nie posiadasz uprawnień administratora!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("ADDTOBLACKLIST")) {
                                    if (loggedin) {
                                        if (czyAdmin) {
                                            String[] data = wiadomosc.split(" ");
                                            if (baza.dodajDoCzarnejListy(data[1])) {
                                                pk.println(OK);

                                                Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                                LocalDateTime ldt = LocalDateTime.now();
                                                String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested ADDTOBLACKLIST";
                                                output.append("\n" + ipIPort);
                                                output.close();
                                            } else {
                                                pk.println("23 ERROR Nie udało się dodać emaila do czarnej listy!");
                                            }
                                        } else {
                                            pk.println("16 ERROR Nie posiadasz uprawnień administratora!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("GETBLACKLIST")) {
                                    if (loggedin) {
                                        if (czyAdmin) {
                                            StringBuilder resp = new StringBuilder(OK + " ");
                                            resp.append(baza.getBlackList());
                                            pk.println(resp);

                                            Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                            LocalDateTime ldt = LocalDateTime.now();
                                            String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested GETBLACK";
                                            output.append("\n" + ipIPort);
                                            output.close();
                                        } else {
                                            pk.println("16 ERROR Nie posiadasz uprawnień administratora!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("REMOVEEMAILFROMBLACKLIST")) {
                                    if (loggedin) {
                                        if (czyAdmin) {
                                            String[] dane = wiadomosc.split(" ");
                                            if (baza.removeEmailFromBlackList(dane[1])) {
                                                pk.println(OK);

                                                Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                                LocalDateTime ldt = LocalDateTime.now();
                                                String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested REMOVEEMAILFROMBLACKLIST";
                                                output.append("\n" + ipIPort);
                                                output.close();
                                            } else {
                                                pk.println("24 ERROR Nie udało się usunąć emaila z czarnej listy!");
                                            }
                                        } else {
                                            pk.println("16 ERROR Nie posiadasz uprawnień administratora!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("CHECKOLDPASSWORD")) {
                                    if (loggedin) {
                                        String[] dane = wiadomosc.split(" ");
                                        if (baza.checkOldPassword(username, hasz(dane[1]))) {
                                            pk.println(OK);

                                            Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                            LocalDateTime ldt = LocalDateTime.now();
                                            String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested CHECKOLDPASSWORD";
                                            output.append("\n" + ipIPort);
                                            output.close();
                                        } else {
                                            pk.println("25 ERROR Błąd!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("CHANGEPASSWORD")) {
                                    if (loggedin) {
                                        String[] dane = wiadomosc.split(" ");
                                        if (checkPassword(dane[1])) {
                                            if (baza.changePassword(username, hasz(dane[1]))) {
                                                pk.println(OK);

                                                Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                                LocalDateTime ldt = LocalDateTime.now();
                                                String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested CHECKOLDPASSWORD";
                                                output.append("\n" + ipIPort);
                                                output.close();
                                            } else {
                                                pk.println("25 ERROR Błąd!");
                                            }
                                        } else {
                                            pk.println("7 ERROR Niepoprawny format hasla.");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("GETINVITATIONS")) {
                                    if (loggedin) {
                                        StringBuilder resp = new StringBuilder(OK + " ");
                                        resp.append(baza.getZaproszenia(username));
                                        pk.println(resp);

                                        Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                        LocalDateTime ldt = LocalDateTime.now();
                                        String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested GETINVITATIONS";
                                        output.append("\n" + ipIPort);
                                        output.close();
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("GETFRIENDS")) {
                                    if (loggedin) {
                                        StringBuilder resp = new StringBuilder(OK + " ");
                                        resp.append(baza.getZnajomi(username));
                                        pk.println(resp);

                                        Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                        LocalDateTime ldt = LocalDateTime.now();
                                        String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested GETFRIENDS";
                                        output.append("\n" + ipIPort);
                                        output.close();
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("ACCEPTINVITE")) {
                                    if (loggedin) {
                                        String[] data = wiadomosc.split(" ");
                                        if(baza.acceptInvite(Integer.parseInt(data[1]))){
                                            pk.println(OK);

                                            Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                            LocalDateTime ldt = LocalDateTime.now();
                                            String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested ACCEPTINVITE";
                                            output.append("\n" + ipIPort);
                                            output.close();
                                        } else {
                                            pk.println("25 ERROR Błąd!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("DELETEINVITE")) {
                                    if (loggedin) {
                                        String[] data = wiadomosc.split(" ");
                                        if(baza.deleteInvite(Integer.parseInt(data[1]))){
                                            pk.println(OK);

                                            Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                            LocalDateTime ldt = LocalDateTime.now();
                                            String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested DELETEINVITE";
                                            output.append("\n" + ipIPort);
                                            output.close();
                                        } else {
                                            pk.println("25 ERROR Błąd!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("DELETEFRIEND")) {
                                    if (loggedin) {
                                        String[] data = wiadomosc.split(" ");
                                        if(baza.deleteFriend(Integer.parseInt(data[1]))){
                                            pk.println(OK);

                                            Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                            LocalDateTime ldt = LocalDateTime.now();
                                            String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Requested DELETEFRIEND";
                                            output.append("\n" + ipIPort);
                                            output.close();
                                        } else {
                                            pk.println("25 ERROR Błąd!");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                }
                                else {
                                    pk.println("5 ERROR Niepoprawna komenda.");
                                }
                            }
                        }
                        Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                        LocalDateTime ldt = LocalDateTime.now();
                        String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Disconnected";
                        output.append("\n").append(ipIPort);
                        output.close();
                        sk.close();
                        pk.close();
                        if (pokoj != 9999) vpw.get(pokoj).remove(pk);
                        s.close();
                    } catch (Exception e) {
                        System.out.println(e + " " + e.getStackTrace());
                        e.printStackTrace();
                        pk.println("Cos poszlo nie tak: blad serwera.");
                    }
                }
            });
            w.start();
        }
    }

    public static boolean checkUsername(String user) {
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

    public static boolean checkPassword(String pass) {
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

    public static boolean checkEmail(String email) {
        String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String hasz(String dane) {
        String ret = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] msgDigest = md.digest(dane.getBytes());
            BigInteger no = new BigInteger(1, msgDigest);
            ret = no.toString(16);
            while (ret.length() < 32) {
                ret = "0" + ret;
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        return ret;
    }
}

