package Oryginal;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.Vector;

public class Main {

    public static void main(String[] args) throws IOException {
        ServerSocket serw = new ServerSocket(9999);
        Vector<Vector<PrintWriter>> vpw = new Vector<>();

        Vector<PrintWriter> vpw1 = new Vector<>();
        Vector<PrintWriter> vpw2 = new Vector<>();
        Vector<PrintWriter> vpw3 = new Vector<>();
        Vector<PrintWriter> vpw4 = new Vector<>();
        Vector<PrintWriter> vpw5 = new Vector<>();
        String OK = "0 OK";
        String[] rooms = new String[5];
        Scanner scanner = new Scanner(System.in);
        File plikUsers, plikLogs;
        System.out.println("Podaj pelna sciezke do pliku z danymi uzytkownikow. np: D:\\dane\\users.txt");
        plikUsers = new File(scanner.nextLine());
        System.out.println("Podaj pelna sciezke do pliku do zapisu logow. np: D:\\logi\\logs.txt");
        plikLogs = new File(scanner.nextLine());
        System.out.println("Podaj 5 nazw pokojow (nazwy nie moga zawierac spacji):");
        rooms[0] = scanner.next();
        rooms[1] = scanner.next();
        rooms[2] = scanner.next();
        rooms[3] = scanner.next();
        rooms[4] = scanner.next();
        boolean pliki = false;
        try {
            Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
            String test = "";
            output.append(test);
            output.close();
            output = new BufferedWriter(new FileWriter(plikUsers, true));
            test = "";
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
                    int pokoj = 9999;
                    try {
                        while (true) {
                            if (sk.hasNext()) {
                                String wiadomosc = sk.nextLine();
                                if (wiadomosc.startsWith("REGISTER")) {
                                    boolean istnieje = false;
                                    String[] dane = wiadomosc.split(" ");
                                    if (dane.length == 4) {
                                        BufferedReader br = null;
                                        br = new BufferedReader(new FileReader(plikUsers));
                                        String linia = br.readLine();
                                        while (linia != null) {
                                            if (linia.split(" ")[0].equals(dane[1])) {
                                                istnieje = true;
                                                break;
                                            }
                                            linia = br.readLine();
                                        }
                                        if (istnieje) {
                                            pk.println("1 ERROR Ta nazwa uzytkownika jest juz zajeta.");
                                        } else {
                                            if (checkUsername(dane[1])) {
                                                if (checkPassword(dane[2])) {
                                                    Writer output = new BufferedWriter(new FileWriter(plikUsers, true));
                                                    String pasy = dane[1] + " " + hasz(dane[2]);
                                                    output.append("\n" + pasy);
                                                    output.close();
                                                    username = dane[1];
                                                    loggedin = true;
                                                    pk.println(OK);
                                                    output = new BufferedWriter(new FileWriter(plikLogs, true));
                                                    LocalDateTime ldt = LocalDateTime.now();
                                                    String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Registered";
                                                    output.append("\n" + ipIPort);
                                                    output.close();
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
                                    boolean istnieje = false;
                                    boolean poprawneLogowanie = false;
                                    String[] dane = wiadomosc.split(" ");
                                    if (dane.length == 3) {
                                        dane[2] = hasz(dane[2]);
                                        BufferedReader br = null;
                                        br = new BufferedReader(new FileReader(plikUsers));
                                        String linia = br.readLine();
                                        while (linia != null) {
                                            if (linia.split(" ")[0].equals(dane[1])) {
                                                istnieje = true;
                                                if (linia.split(" ")[1].equals(dane[2])) {
                                                    poprawneLogowanie = true;

                                                }
                                                break;
                                            }
                                            linia = br.readLine();
                                        }
                                        if (poprawneLogowanie) {
                                            username = dane[1];
                                            loggedin = true;
                                            pk.println(OK);
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
                                    pk.println(OK + " " + rooms[0] + " " + rooms[1] + " " + rooms[2] + " " + rooms[3] + " " + rooms[4]);
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
                                            switch (pokoj) {
                                                case 1:
                                                    pokoj = 9999;
                                                    vpw1.remove(pk);
                                                    break;
                                                case 2:
                                                    pokoj = 9999;
                                                    vpw2.remove(pk);
                                                    break;
                                                case 3:
                                                    pokoj = 9999;
                                                    vpw3.remove(pk);
                                                    break;
                                                case 4:
                                                    pokoj = 9999;
                                                    vpw4.remove(pk);
                                                    break;
                                                case 5:
                                                    pokoj = 9999;
                                                    vpw5.remove(pk);
                                                    break;
                                                default:
                                                    break;
                                            }
                                            if (dane[1].equals(rooms[0])) {
                                                pokoj = 1;
                                                vpw1.add(pk);
                                                err = false;
                                            } else if (dane[1].equals(rooms[1])) {
                                                pokoj = 2;
                                                vpw2.add(pk);
                                                err = false;
                                            } else if (dane[1].equals(rooms[2])) {
                                                pokoj = 3;
                                                vpw3.add(pk);
                                                err = false;
                                            } else if (dane[1].equals(rooms[3])) {
                                                pokoj = 4;
                                                vpw4.add(pk);
                                                err = false;
                                            } else if (dane[1].equals(rooms[4])) {
                                                pokoj = 5;
                                                vpw5.add(pk);
                                                err = false;
                                            } else {
                                                err = true;
                                            }
                                            if (!err) {
                                                pk.println(OK);
                                                Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                                                LocalDateTime ldt = LocalDateTime.now();
                                                String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Joined: " + rooms[pokoj - 1];
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
                                            switch (pokoj) {
                                                case 1:
                                                    pk.println(OK);
                                                    for (PrintWriter pw : vpw1) {
                                                        if (pw != pk) {
                                                            pw.println("/" + username + ": " + tekst);
                                                        }
                                                    }
                                                    break;
                                                case 2:
                                                    pk.println(OK);
                                                    for (PrintWriter pw : vpw2) {
                                                        if (pw != pk) {
                                                            pw.println("/" + username + ": " + tekst);
                                                        }
                                                    }
                                                    break;
                                                case 3:
                                                    pk.println(OK);
                                                    for (PrintWriter pw : vpw3) {
                                                        if (pw != pk) {
                                                            pw.println("/" + username + ": " + tekst);
                                                        }
                                                    }
                                                    break;
                                                case 4:
                                                    pk.println(OK);
                                                    for (PrintWriter pw : vpw4) {
                                                        if (pw != pk) {
                                                            pw.println("/" + username + ": " + tekst);
                                                        }
                                                    }
                                                    break;
                                                case 5:
                                                    pk.println(OK);
                                                    for (PrintWriter pw : vpw5) {
                                                        if (pw != pk) {
                                                            pw.println("/" + username + ": " + tekst);
                                                        }
                                                    }
                                                    break;
                                                default:
                                                    pk.println("4 ERROR Nie jestes w zadnym pokoju.");
                                                    break;
                                            }
                                        } else {
                                            pk.println("8 ERROR Dlugosc wiadomosci wynosi 0.");
                                        }
                                    } else {
                                        pk.println("9 ERROR Nie jestes zalogowany.");
                                    }
                                } else if (wiadomosc.startsWith("DISCONNECT")) {
                                    pk.println(OK);
                                    break;
                                } else {
                                    pk.println("5 ERROR Niepoprawna komenda.");
                                }
                            }
                        }
                        Writer output = new BufferedWriter(new FileWriter(plikLogs, true));
                        LocalDateTime ldt = LocalDateTime.now();
                        String ipIPort = "ip: " + ipAdd + " data: " + ldt + " Disconnected";
                        output.append("\n" + ipIPort);
                        output.close();
                        sk.close();
                        pk.close();
                        s.close();
                    } catch (Exception e) {
                        System.out.println(e + " " + e.getStackTrace());
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

