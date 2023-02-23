package Klient;

public class Uzytkownik {
    private int id;
    private String login;
    private String email;
    private char czyAdmin;
    private char czyZalogowany;
    private int pokojId;

    public Uzytkownik(int id, String login, String email, char czyAdmin, char czyZalogowany, int pokojId) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.czyAdmin = czyAdmin;
        this.czyZalogowany = czyZalogowany;
        this.pokojId = pokojId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public char getCzyAdmin() {
        return czyAdmin;
    }

    public void setCzyAdmin(char czyAdmin) {
        this.czyAdmin = czyAdmin;
    }

    public char getCzyZalogowany() {
        return czyZalogowany;
    }

    public void setCzyZalogowany(char czyZalogowany) {
        this.czyZalogowany = czyZalogowany;
    }

    public int getPokojId() {
        return pokojId;
    }

    public void setPokojId(int pokojId) {
        this.pokojId = pokojId;
    }
}
