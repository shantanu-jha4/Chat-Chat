/**
 * <b> CS 180 - Project 4 - User class </b>
 * <p>
 *
 * @author (Shantanu Jha) <(jha5@purdue.edu)>
 * @author (Shivan Desai) <(desai58@purdue.edu)>      PROJECT PARTNER
 * @version (11/14/2015)
 * @lab (809) Both of us
 */
public class User {
    private String username;
    private String password;
    private SessionCookie cookie;

    public User(String username, String password, SessionCookie cookie) {
        this(username, password);
        this.cookie = cookie;
        if (cookie != null)
            cookie.updateTimeOfActivity();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return username;
    }

    public boolean checkPassword(String password) {
        return password.equals(this.password);
    }

    public void setCookie(SessionCookie cookie) {
        this.cookie = cookie;
    }

    public SessionCookie getCookie() {
        return cookie;
    }
}
