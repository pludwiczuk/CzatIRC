package Klient;

public class Znajomy {
    private int id;
    private int znajomyId;
    private String login;

    public Znajomy(int id, int znajomyId, String login) {
        this.id = id;
        this.znajomyId = znajomyId;
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getZnajomyId() {
        return znajomyId;
    }

    public void setZnajomyId(int znajomyId) {
        this.znajomyId = znajomyId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

}
