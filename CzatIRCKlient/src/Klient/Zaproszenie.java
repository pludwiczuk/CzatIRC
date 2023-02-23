package Klient;

public class Zaproszenie {
    private int id;
    private int nadawcaId;
    private String nadawcaLogin;

    public Zaproszenie(int id, int nadawcaId, String nadawcaLogin) {
        this.id = id;
        this.nadawcaId = nadawcaId;
        this.nadawcaLogin = nadawcaLogin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNadawcaId() {
        return nadawcaId;
    }

    public void setNadawcaId(int nadawcaId) {
        this.nadawcaId = nadawcaId;
    }

    public String getNadawcaLogin() {
        return nadawcaLogin;
    }

    public void setNadawcaLogin(String nadawcaLogin) {
        this.nadawcaLogin = nadawcaLogin;
    }
}
