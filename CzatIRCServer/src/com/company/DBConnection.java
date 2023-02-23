package com.company;

import javax.swing.*;
import java.sql.*;

public class DBConnection {
    private static DBConnection instance = null;
    private Connection connection;
    private Statement statement;
    private ResultSet rs;

        private DBConnection() {
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:baza.db");
            statement = connection.createStatement();
            connection.setAutoCommit(true);
            System.out.println("You are connected to database!");
            //initializeDB();
        } catch (ClassNotFoundException e) {
            System.out.println("#DBConnection(): Error: " + e);
            e.printStackTrace();
        } catch (SQLException throwables) {
            System.out.println("#DBConnection(): Error: " + throwables);
            throwables.printStackTrace();
        }
    }
//    private DBConnection() {
//        try {
//            Class.forName("oracle.jdbc.driver.OracleDriver");
//            connection = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe", "CzatIRC", "CzatIRCPass");
//            statement = connection.createStatement();
//            connection.setAutoCommit(true);
//            System.out.println("You are connected to database!");
//            //initializeDB();
//        } catch (Exception ex) {
//            System.out.println("#DBConnection(): Error: " + ex);
//            JOptionPane.showMessageDialog(null, ex);
//        }
//    }

    public static DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    private void initializeDB() {
        try {
            statement.execute("CREATE TABLE pokoje (\n" +
                    "id NUMBER(10) PRIMARY KEY,\n" +
                    "nazwa VARCHAR2(30) UNIQUE NOT NULL\n" +
                    ")");
            System.out.println("Utworzono pokoje");

            statement.execute("CREATE TABLE uzytkownicy (\n" +
                    "id NUMBER(10) PRIMARY KEY,\n" +
                    "login VARCHAR2(64) UNIQUE NOT NULL,\n" +
                    "haslo VARCHAR2(64) NOT NULL,\n" +
                    "email VARCHAR2(64) UNIQUE NOT NULL,\n" +
                    "czy_admin CHAR NOT NULL,\n" +
                    "czy_zalogowany CHAR NOT NULL,\n" +
                    "pokoj_id NUMBER(10) NULL CONSTRAINT pokoje_uzytkownicy_fk REFERENCES pokoje(id)\n" +
                    ")");
            System.out.println("Utworzono uzytkownicy");

            statement.execute("CREATE TABLE czarna_lista (\n" +
                    "id NUMBER(10) PRIMARY KEY,\n" +
                    "email VARCHAR2(64) UNIQUE NOT NULL\n" +
                    ")");
            System.out.println("Utworzono czarna_lista");

            statement.execute("CREATE TABLE znajomi (\n" +
                    "id NUMBER(10) PRIMARY KEY,\n" +
                    "uzytkownik_id NUMBER(10) CONSTRAINT uzytkownicy_znajomi_fk1 REFERENCES uzytkownicy(id),\n" +
                    "znajomy_id NUMBER(10) CONSTRAINT uzytkownicy_znajomi_fk2 REFERENCES uzytkownicy(id)\n" +
                    ")");
            System.out.println("Utworzono znajomi");

            statement.execute("CREATE TABLE zaproszenia (\n" +
                    "id NUMBER(10) PRIMARY KEY,\n" +
                    "nadawca_id NUMBER(10) CONSTRAINT uzytkownicy_zaproszenia_fk1 REFERENCES uzytkownicy(id),\n" +
                    "odbiorca_id NUMBER(10) CONSTRAINT uzytkownicy_zaproszenia_fk2 REFERENCES uzytkownicy(id)\n" +
                    ")");
            System.out.println("Utworzono zaproszenia");

            rs = statement.executeQuery("SELECT COUNT(id) FROM uzytkownicy");
            rs.next();
            if (rs.getInt(1) == 0) {
                String haslo = Main.hasz("admin");
                statement.execute("INSERT INTO uzytkownicy VALUES(1,'admin','" + haslo + "','p.ludwiczuk@o2.pl','T','N',NULL)");
                System.out.println("Dodano domyślnego administratora");
            }

            rs = statement.executeQuery("SELECT COUNT(id) FROM pokoje");
            rs.next();
            if (rs.getInt(1) == 0) {
                statement.execute("INSERT INTO pokoje VALUES(1,'p1')");
                statement.execute("INSERT INTO pokoje VALUES(2,'p2')");
                System.out.println("Dodano domyślne pokoje");
            }

        } catch (SQLException throwables) {
            System.out.println("BLADDD!!!!!");
            throwables.printStackTrace();
        }
    }

    public String[] getListaPokojow() {
        String[] lista;
        try {
            rs = statement.executeQuery("SELECT COUNT(id) FROM pokoje");
            rs.next();
            lista = new String[rs.getInt(1)];
            rs = statement.executeQuery("SELECT nazwa FROM pokoje");
            int i = 0;
            while (rs.next()) {
                lista[i] = rs.getString(1);
                i++;
            }
            return lista;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public String getAllRooms() {
        StringBuilder pokoje = new StringBuilder();
        try {
            rs = statement.executeQuery("SELECT * FROM pokoje");
            while(rs.next()) {
                pokoje.append(rs.getInt(1)).append(",").append(rs.getString(2)).append(";");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return pokoje.toString();
    }

    public String getBlackList() {
        StringBuilder czarnaLista = new StringBuilder();
        try {
            rs = statement.executeQuery("SELECT * FROM czarna_lista");
            while(rs.next()) {
                czarnaLista.append(rs.getInt(1)).append(",").append(rs.getString(2)).append(";");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return czarnaLista.toString();
    }

    public String getZaproszenia(String username) {
        StringBuilder zaproszenia = new StringBuilder();
        try {
            rs = statement.executeQuery("SELECT zaproszenia.id, zaproszenia.nadawca_id, uzytkownicy.login FROM zaproszenia, uzytkownicy WHERE zaproszenia.nadawca_id=uzytkownicy.id AND zaproszenia.odbiorca_id=(SELECT id FROM uzytkownicy WHERE login='"+username+"')");
            //rs = statement.executeQuery("SELECT * FROM zaproszenia WHERE odbiorca_id=(SELECT id FROM uzytkownicy WHERE login='"+username+"')");
            while(rs.next()) {
                zaproszenia.append(rs.getInt(1)).append(",").append(rs.getInt(2)).append(",").append(rs.getString(3)).append(";");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return zaproszenia.toString();
    }

    public String getZnajomi(String username) {
        StringBuilder znajomi = new StringBuilder();
        try {
            rs = statement.executeQuery("SELECT znajomi.id, znajomi.znajomy_id, uzytkownicy.login FROM znajomi, uzytkownicy WHERE uzytkownicy.id=znajomi.znajomy_id AND znajomi.uzytkownik_id=(SELECT id FROM uzytkownicy WHERE login='"+username+"')");
            while(rs.next()) {
                znajomi.append(rs.getInt(1)).append(",").append(rs.getInt(2)).append(",").append(rs.getString(3)).append(";");
            }
            rs = statement.executeQuery("SELECT znajomi.id, znajomi.uzytkownik_id, uzytkownicy.login FROM znajomi, uzytkownicy WHERE uzytkownicy.id=znajomi.uzytkownik_id AND znajomi.znajomy_id=(SELECT id FROM uzytkownicy WHERE login='"+username+"')");
            while(rs.next()) {
                znajomi.append(rs.getInt(1)).append(",").append(rs.getInt(2)).append(",").append(rs.getString(3)).append(";");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return znajomi.toString();
    }

    public boolean acceptInvite(int id) {
        try {
            rs = statement.executeQuery("SELECT * FROM zaproszenia WHERE id="+id);
            if(rs.next()) {
                int nadawca_id = rs.getInt("nadawca_id");
                int odbiorca_id = rs.getInt("odbiorca_id");
                rs = statement.executeQuery("SELECT COUNT(id) FROM znajomi");
                if(rs.next()) {
                    if(rs.getInt(1) == 0) {
                        statement.execute("INSERT INTO znajomi VALUES(1," + odbiorca_id + "," + nadawca_id + ")");
                    } else {
                        statement.execute("INSERT INTO znajomi VALUES((SELECT MAX(id) FROM znajomi)+1," + odbiorca_id + "," + nadawca_id + ")");
                    }
                }
                statement.execute("DELETE FROM zaproszenia WHERE id="+id);
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean deleteInvite(int id) {
        try {
            statement.execute("DELETE FROM zaproszenia WHERE id="+id);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean deleteFriend(int id) {
        try {
            statement.execute("DELETE FROM znajomi WHERE id="+id);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean removeEmailFromBlackList(String email) {
        try {
            statement.execute("DELETE FROM czarna_lista WHERE email='"+email+"'");
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean checkOldPassword(String username, String pass) {
        try {
            rs = statement.executeQuery("SELECT COUNT(login) FROM uzytkownicy WHERE login='"+username+"' AND haslo='"+pass+"'");
            if(rs.next()) {
                if(rs.getInt(1) != 0) {
                    return true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean changePassword(String username, String pass) {
        try {
            statement.execute("UPDATE uzytkownicy SET haslo='"+pass+"' WHERE login='"+username+"'");
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public String getAllUsers() {
        StringBuilder uzytkownicy = new StringBuilder();
        try {
            rs = statement.executeQuery("SELECT id, login, email, czy_admin, czy_zalogowany, pokoj_id FROM uzytkownicy");
            while(rs.next()) {
                uzytkownicy.append(rs.getInt(1)).append(",").append(rs.getString(2)).append(",").append(rs.getString(3)).append(",").append(rs.getString(4)).append(",").append(rs.getString(5)).append(",").append(rs.getInt(6)).append(";");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return uzytkownicy.toString();
    }

    public String[] getUsersInRoom(int idPokoju) {
        String[] lista;
        try {
            rs = statement.executeQuery("SELECT COUNT(login) FROM uzytkownicy WHERE pokoj_id=" + idPokoju);
            rs.next();
            lista = new String[rs.getInt(1)];
            rs = statement.executeQuery("SELECT login FROM uzytkownicy WHERE pokoj_id=" + idPokoju);
            int i = 0;
            while (rs.next()) {
                lista[i] = rs.getString(1);
                i++;
            }
            return lista;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public boolean checkCzarnaLista(String email) {
        try {
            rs = statement.executeQuery("SELECT COUNT(id) FROM czarna_lista WHERE email='" + email + "'");
            rs.next();
            if (rs.getInt(1) == 0) return true;
            return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean checkUsername(String username) {
        try {
            rs = statement.executeQuery("SELECT COUNT(id) FROM uzytkownicy WHERE login='" + username + "'");
            rs.next();
            if (rs.getInt(1) == 0) return true;
            return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean checkEmail(String email) {
        try {
            rs = statement.executeQuery("SELECT COUNT(id) FROM uzytkownicy WHERE email='" + email + "'");
            rs.next();
            if (rs.getInt(1) == 0) return true;
            return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean checkZaproszenia(String nadawca, String odbiorca) {
        int nadawcaId, odbiorcaId;
        try {
            rs = statement.executeQuery("SELECT id FROM uzytkownicy WHERE login='" + nadawca + "'");
            rs.next();
            nadawcaId = rs.getInt(1);
            rs = statement.executeQuery("SELECT id FROM uzytkownicy WHERE login='" + odbiorca + "'");
            rs.next();
            odbiorcaId = rs.getInt(1);
            rs = statement.executeQuery("SELECT id FROM zaproszenia WHERE nadawca_id=" + nadawcaId + " AND odbiorca_id=" + odbiorcaId);
            if(rs.next()) {
                return true;
            }
            else {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean checkZnajomi(String username1, String username2) {
        try {
            rs = statement.executeQuery("SELECT COUNT(id) FROM znajomi WHERE uzytkownik_id=(SELECT id FROM uzytkownicy WHERE login='" + username1 + "') AND znajomy_id=(SELECT id FROM uzytkownicy WHERE login='" + username2 + "')");
            if (rs.next() && rs.getInt(1) == 0) {
                rs = statement.executeQuery("SELECT COUNT(id) FROM znajomi WHERE uzytkownik_id=(SELECT id FROM uzytkownicy WHERE login='" + username2 + "') AND znajomy_id=(SELECT id FROM uzytkownicy WHERE login='" + username1 + "')");
                if (rs.next() && rs.getInt(1) == 0) return false;
                return true;
            }
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return true;
    }

    public void wyslijZaproszenie(String nadawca, String odbiorca) {
        try {
            int nadawcaId, odbiorcaId;
            rs = statement.executeQuery("SELECT id FROM uzytkownicy WHERE login='" + nadawca + "'");
            rs.next();
            nadawcaId = rs.getInt(1);
            rs = statement.executeQuery("SELECT id FROM uzytkownicy WHERE login='" + odbiorca + "'");
            rs.next();
            odbiorcaId = rs.getInt(1);
            rs = statement.executeQuery("SELECT COUNT(id) FROM zaproszenia");
            rs.next();
            if (rs.getInt(1) == 0) {
                statement.execute("INSERT INTO zaproszenia VALUES(1," + nadawcaId + "," + odbiorcaId + ")");
            } else {
                statement.execute("INSERT INTO zaproszenia VALUES((SELECT MAX(id) FROM zaproszenia)+1," + nadawcaId + "," + odbiorcaId + ")");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void dodajUzytkownika(String login, String haslo, String email) {
        try {
            rs = statement.executeQuery("SELECT COUNT(id) FROM uzytkownicy");
            rs.next();
            if (rs.getInt(1) == 0) {
                statement.execute("INSERT INTO uzytkownicy VALUES(1,'" + login + "','" + haslo + "','" + email + "','N','N',NULL)");
            } else {
            statement.execute("INSERT INTO uzytkownicy VALUES((SELECT MAX(id) FROM uzytkownicy)+1,'" + login + "','" + haslo + "','" + email + "','N','N',NULL)");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public boolean dodajNowegoUzytkownika(String login, String haslo, String email, String czyAdmin, String czyZalogowany, String pokojId) {
        try {
            rs = statement.executeQuery("SELECT COUNT(id) FROM uzytkownicy");
            if(rs.next()) {
                if(rs.getInt(1) == 0) {
                    statement.execute("INSERT INTO uzytkownicy VALUES(1, '"+login+"', '"+haslo+"', '"+email+"', '"+czyAdmin+"', '"+czyZalogowany+"', "+pokojId+")");
                    return true;
                } else {
                    statement.execute("INSERT INTO uzytkownicy VALUES((SELECT MAX(id) FROM uzytkownicy)+1, '"+login+"', '"+haslo+"', '"+email+"', '"+czyAdmin+"', '"+czyZalogowany+"', "+pokojId+")");
                    return true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean changeRoomName(int id, String name) {
        try {
            statement.execute("UPDATE pokoje SET nazwa='"+name+"' WHERE id="+id);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean aktualizujDaneUzytkownika(String id,String login,String email,String czyAdmin,String czyZalogowany,String pokoj_id) {
        try {
            statement.execute("UPDATE uzytkownicy SET login='"+login+"', email='"+email+"', czy_admin='"+czyAdmin+"', czy_zalogowany='"+czyZalogowany+"', pokoj_id="+pokoj_id+" WHERE id="+id);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean dodajDoCzarnejListy(String email) {
        try {
            rs = statement.executeQuery("SELECT COUNT(id) FROM czarna_lista");
            if(rs.next()) {
                if (rs.getInt(1) == 0) {
                    statement.execute("INSERT INTO czarna_Lista VALUES(1, '" + email + "')");
                } else {
                    statement.execute("INSERT INTO czarna_lista VALUES((SELECT MAX(id) FROM czarna_lista)+1, '" + email + "')");
                }
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean deleteUser(int id) {
        try {
            statement.execute("DELETE FROM uzytkownicy WHERE id="+id);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean deleteRoom(int id) {
        try {
            statement.execute("DELETE FROM pokoje WHERE id="+id);
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public boolean addRoom(String nazwa) {
        try {
            rs = statement.executeQuery("SELECT COUNT(id) FROM pokoje");
            if(rs.next()) {
                if(rs.getInt(1) == 0) {
                    statement.execute("INSERT INTO pokoje VALUES(1, '"+nazwa+"')");
                    return true;
                } else {
                    statement.execute("INSERT INTO pokoje VALUES((SELECT MAX(id) FROM pokoje)+1, '" + nazwa + "')");
                    return true;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void setNrPokoju(int number, String username) {
        try {
            statement.execute("UPDATE uzytkownicy SET pokoj_id=" + number + " WHERE login='" + username + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public int checkIdPokoju(String pokoj) {
        int id = -1;
        try {
            rs = statement.executeQuery("SELECT id FROM pokoje WHERE nazwa='" + pokoj + "'");
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return id;
    }

    public String zaloguj(String login, String haslo) {
        String resp = "error";
        try {
            rs = statement.executeQuery("SELECT czy_admin FROM uzytkownicy WHERE login='" + login + "' AND haslo='" + haslo + "'");
            if (rs.next()) {
                if (rs.getString(1).equals("T")) {
                    resp = "admin";
                } else resp = "uzytkownik";
                statement.execute("UPDATE uzytkownicy SET czy_zalogowany='T' WHERE login='" + login + "'");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return resp;
    }

    public void wyloguj(String login) {
        try {
            statement.execute("UPDATE uzytkownicy SET czy_zalogowany='N', pokoj_id=NULL WHERE login='" + login + "'");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
