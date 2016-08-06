/**
 * <b> CS 180 - Project 4 - Session Cookie class </b>
 * <p>
 *
 * @author (Shantanu Jha) <(jha5@purdue.edu)>
 * @author (Shivan Desai) <(desai58@purdue.edu)>      PROJECT PARTNER
 * @version (11/6/2015)
 * @lab (809) Both of us
 */
public class SessionCookie {
    public static int timeoutLength = 300;
    private long id;
    private long lastTimeOfActivity;

    public SessionCookie(long id) {
        this.id = id;
        updateTimeOfActivity();
    }

    public long getID() {
        return this.id;
    }

    public void updateTimeOfActivity() {
        lastTimeOfActivity = System.currentTimeMillis();
    }

    public boolean hasTimedOut() {
        return (System.currentTimeMillis() - lastTimeOfActivity) > (timeoutLength * 1000);
    }
}
