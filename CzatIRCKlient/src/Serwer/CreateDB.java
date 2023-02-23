package Serwer;

import java.sql.*;

public class CreateDB {
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Connection conn;
        Statement stat;
        ResultSet rs;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:baza.db");
            stat = conn.createStatement();
            conn.setAutoCommit(true);

            stat.execute("CREATE TABLE IF NOT EXISTS pokoje (\n" +
                    "id NUMBER(10) PRIMARY KEY,\n" +
                    "nazwa VARCHAR2(30) UNIQUE NOT NULL\n" +
                    ")");
            System.out.println("Utworzono pokoje");

            stat.execute("CREATE TABLE IF NOT EXISTS uzytkownicy (\n" +
                    "id NUMBER(10) PRIMARY KEY,\n" +
                    "login VARCHAR2(30) UNIQUE NOT NULL,\n" +
                    "haslo VARCHAR2(30) NOT NULL,\n" +
                    "email VARCHAR2(30) UNIQUE NOT NULL,\n" +
                    "czy_admin CHAR NOT NULL,\n" +
                    "czy_zalogowany CHAR NOT NULL,\n" +
                    "pokoj_id NUMBER(10) NULL CONSTRAINT pokoje_uzytkownicy_fk REFERENCES pokoje(id)\n" +
                    ")");
            System.out.println("Utworzono uzytkownicy");

            stat.execute("CREATE TABLE IF NOT EXISTS czarna_lista (\n" +
                    "id NUMBER(10) PRIMARY KEY,\n" +
                    "email VARCHAR2(30) UNIQUE NOT NULL\n" +
                    ")");
            System.out.println("Utworzono czarna_lista");

            stat.execute("CREATE TABLE IF NOT EXISTS znajomi (\n" +
                    "id NUMBER(10) PRIMARY KEY,\n" +
                    "uzytkownik_id NUMBER(10) CONSTRAINT uzytkownicy_znajomi_fk1 REFERENCES uzytkownicy(id),\n" +
                    "znajomy_id NUMBER(10) CONSTRAINT uzytkownicy_znajomi_fk2 REFERENCES uzytkownicy(id)\n" +
                    ")");
            System.out.println("Utworzono znajomi");

            stat.execute("CREATE TABLE IF NOT EXISTS zaproszenia (\n" +
                    "id NUMBER(10) PRIMARY KEY,\n" +
                    "nadawca_id NUMBER(10) CONSTRAINT uzytkownicy_zaproszenia_fk1 REFERENCES uzytkownicy(id),\n" +
                    "odbiorca_id NUMBER(10) CONSTRAINT uzytkownicy_zaproszenia_fk2 REFERENCES uzytkownicy(id)\n" +
                    ")");
            System.out.println("Utworzono zaproszenia");

            rs = stat.executeQuery("SELECT COUNT(id) FROM uzytkownicy");
            if(rs.getInt(1) == 0) {
                stat.execute("INSERT INTO uzytkownicy VALUES(1,'admin','admin','p.ludwiczuk@o2.pl','T','N',NULL)");
                System.out.println("Dodano domyślnego administratora");
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
