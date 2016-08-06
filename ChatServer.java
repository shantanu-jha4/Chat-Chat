import java.util.*;

/**
 * <b> CS 180 - Project 4 - Chat Server Skeleton </b>
 * <p>
 * <p>
 * This is the skeleton code for the ChatServer Class. This is a private chat
 * server for you and your friends to communicate.
 *
 * @author (Shantanu Jha) <(jha5@purdue.edu)>
 * @author (Shivan Desai) <(desai58@purdue.edu)>      PROJECT PARTNER
 * @version (11/14/2015)
 * @lab (809) Both of us
 */
public class ChatServer {
    private User[] users;
    private int maxMessages;
    private int userCounter;
    private CircularBuffer obj;
    private User root;
    final String addUSR = "ADD-USER";
    final String usrLGN = "USER-LOGIN";
    final String psstMSSGS = "POST-MESSAGE";
    final String getMSSGS = "GET-MESSAGES";

    public ChatServer(User[] users, int maxMessages) {
        this.users = users;
        this.maxMessages = maxMessages;
        userCounter = 0;
        obj = new CircularBuffer(maxMessages);
        root = new User("root", "cs180");
    }

    /**
     * This method begins server execution.
     */
    public void run() {
        boolean verbose = false;
        System.out.printf("The VERBOSE option is off.\n\n");
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.printf("Input Server Request: ");
            String command = in.nextLine();

            // this allows students to manually place "\r\n" at end of command
            // in prompt
            command = replaceEscapeChars(command);

            if (command.startsWith("kill"))
                break;

            if (command.startsWith("verbose")) {
                verbose = !verbose;
                System.out.printf("VERBOSE has been turned %s.\n\n", verbose ? "on" : "off");
                continue;
            }

            String response = null;
            try {
                response = parseRequest(command);
            } catch (Exception ex) {
                response = MessageFactory.makeErrorMessage(MessageFactory.UNKNOWN_ERROR,
                        String.format("An exception of %s occurred.", ex.getMessage()));
            }

            // change the formatting of the server response so it prints well on
            // the terminal (for testing purposes only)
            if (response.startsWith("SUCCESS\t"))
                response = response.replace("\t", "\n");

            // print the server response
            if (verbose)
                System.out.printf("response:\n");
            System.out.printf("\"%s\"\n\n", response);
        }

        in.close();
    }

    /**
     * Replaces "poorly formatted" escape characters with their proper values.
     * For some terminals, when escaped characters are entered, the terminal
     * includes the "\" as a character instead of entering the escape character.
     * This function replaces the incorrectly inputed characters with their
     * proper escaped characters.
     *
     * @param str - the string to be edited
     * @return the properly escaped string
     */
    public static String replaceEscapeChars(String str) {
        str = str.replace("\\r", "\r");
        str = str.replace("\\n", "\n");
        str = str.replace("\\t", "\t");

        return str;
    }

    /**
     * Determines which client command the request is using and calls the
     * function associated with that command.
     *
     * @param request - the full line of the client request (CRLF included)
     * @return the server response
     */
    public String parseRequest(String request) {
        if (request == null)
            return MessageFactory.makeErrorMessage(0);
        if (!request.endsWith(replaceEscapeChars("\r\n")))
            return MessageFactory.makeErrorMessage(10);
        String commandGiven[] = request.split("\t");
        commandGiven[0] = commandGiven[0].trim();
        if (commandGiven.length >= 3)
            if (!isFormatCorrect(commandGiven))
                return MessageFactory.makeErrorMessage(24);
        switch (commandGiven[0]) {
            case addUSR: {
                boolean exists = true;
                boolean timedOut = false;
                int count = 0;
                for (int i = 1; i < commandGiven.length; i++)
                    count++;
                if (count != 3)
                    return MessageFactory.makeErrorMessage(10);
                if (getUserIndex(commandGiven[1]) == -1) {
                    exists = false;
                    return MessageFactory.makeErrorMessage(23);
                }
                if (users[getUserIndex(commandGiven[1])].getCookie().hasTimedOut()) {
                    users[getUserIndex(commandGiven[1])].setCookie(null);
                    timedOut = true;
                    return MessageFactory.makeErrorMessage(5);
                }
                if (exists && !timedOut)
                    return addUser(commandGiven);
                if (root.getCookie() == null)
                    return MessageFactory.makeErrorMessage(5);
                if (root.getCookie().getID() == Long.parseLong(commandGiven[1])) {
                    if (root.getCookie().hasTimedOut()) {
                        root.setCookie(null);
                        return MessageFactory.makeErrorMessage(5);
                    }
                    return addUser(commandGiven);
                }
            }
            case usrLGN: {
                int count = 0;
                for (int i = 1; i < commandGiven.length; i++)
                    count++;
                if (count != 2)
                    return MessageFactory.makeErrorMessage(10);
                return userLogin(commandGiven);
            }
            case getMSSGS: {
                boolean exists = true;
                boolean timedOut = false;
                int count = 0;
                for (int i = 1; i < commandGiven.length; i++)
                    count++;
                if (count != 2)
                    return MessageFactory.makeErrorMessage(10);
                if (getUserIndex(commandGiven[1]) == -1) {
                    exists = false;
                    return MessageFactory.makeErrorMessage(23);
                }
                if (users[getUserIndex(commandGiven[1])].getCookie().hasTimedOut()) {
                    users[getUserIndex(commandGiven[1])].setCookie(null);
                    timedOut = true;
                    return MessageFactory.makeErrorMessage(5);
                }
                if (exists && !timedOut)
                    return getMessages(commandGiven);
                if (root.getCookie() == null)
                    return MessageFactory.makeErrorMessage(5);
                if (root.getCookie().getID() == Long.parseLong(commandGiven[1])) {
                    if (root.getCookie().hasTimedOut()) {
                        root.setCookie(null);
                        return MessageFactory.makeErrorMessage(5);
                    }
                    return getMessages(commandGiven);
                }
            }
            case psstMSSGS: {
                boolean exists = true;
                boolean timedOut = false;
                int count = 0;
                for (int i = 1; i < commandGiven.length; i++)
                    count++;
                if (count != 2)
                    return MessageFactory.makeErrorMessage(10);
                if (getUserIndex(commandGiven[1]) == -1) {
                    exists = false;
                    return MessageFactory.makeErrorMessage(23);
                }
                if (users[getUserIndex(commandGiven[1])].getCookie().hasTimedOut()) {
                    users[getUserIndex(commandGiven[1])].setCookie(null);
                    timedOut = true;
                    return MessageFactory.makeErrorMessage(5);
                }
                if (exists && !timedOut)
                    return postMessage(commandGiven, users[getUserIndex(commandGiven[1])].getName());
                if (root.getCookie() == null)
                    return MessageFactory.makeErrorMessage(5);
                if (root.getCookie().getID() == Long.parseLong(commandGiven[1])) {
                    if (root.getCookie().hasTimedOut()) {
                        root.setCookie(null);
                        return MessageFactory.makeErrorMessage(5);
                    }
                    return postMessage(commandGiven, users[getUserIndex(commandGiven[1])].getName());
                }
            }
            default:
                return MessageFactory.makeErrorMessage(11);
        }
    }

    private int countValidUsr() {
        int count = 0;
        for (int k = 0; k < users.length; k++) {
            if (users[k] == null)
                continue;
            count++;
        }
        return count;
    }

    private boolean isFormatCorrect(String args[]) {
        if (args[0].equals(addUSR) || args[0].equals(psstMSSGS) || args[0].equals(getMSSGS)) {
            try {
                long a = Long.parseLong(args[1]);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        if (args[0].equals(getMSSGS)) {
            try {
                int b = Integer.parseInt(args[2]);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }


    private boolean checkRoot(String[] args) {
        if (args[0].equals(addUSR) || args[0].equals(psstMSSGS) || args[0].equals(getMSSGS)) {
            if (root.getCookie() == null)
                return false;
            if (root.getCookie().getID() == Long.parseLong(args[1]))
                return true;
        }
        if (args[0].equals(usrLGN))
            return ((root.checkPassword(args[2])));
        return false;
    }

    private int getUserIndex(String cookie) {
        for (int k = 0; k < countValidUsr(); k++) {
            if (users[k].getCookie() == null)
                continue;
            if (users[k].getCookie().getID() == Long.parseLong(cookie))
                return k;
        }
        return -1;
    }

    public String addUser(String[] args) {
        if (args == null)
            return MessageFactory.makeErrorMessage(0);
        args[3] = args[3].trim();
        for (int i = 0; i < args[2].length(); ++i) {
            if (!(Character.isLetterOrDigit(args[2].charAt(i)))) {
                return (MessageFactory.makeErrorMessage(24));
            }
        }
        for (int i = 0; i < args[3].length(); ++i) {
            if (!(Character.isLetterOrDigit(args[3].charAt(i)))) {
                return (MessageFactory.makeErrorMessage(24));
            }
        }
        if (args[2].length() < 1 || args[2].length() > 20) {
            return (MessageFactory.makeErrorMessage(24));
        }
        if (args[3].length() < 4 || args[3].length() > 40) {
            return (MessageFactory.makeErrorMessage(24));
        }
        for (int i = 0; i < countValidUsr(); ++i) {
            if (users[i] == null || users[i].getName() == null)
                continue;
            if (users[i].getName().equals(args[2])) {
                return (MessageFactory.makeErrorMessage(22));
            }
        }
        if (checkRoot(args))
            root.getCookie().updateTimeOfActivity();
        if (getUserIndex(args[1]) != -1)
            users[getUserIndex(args[1])].getCookie().updateTimeOfActivity();
        users[userCounter++] = new User(args[2], args[3]);
        return "SUCCESS\r\n";
    }

    public String userLogin(String[] args) {
        if (args == null)
            return MessageFactory.makeErrorMessage(0);
        if (args.length != 3)
            return MessageFactory.makeErrorMessage(10);
        long rootcID;
        args[2] = args[2].trim();
        if (args[1].equals("root")) {
            if (!root.checkPassword(args[2]))
                return (MessageFactory.makeErrorMessage(21));
            if (root.getCookie() != null)
                return (MessageFactory.makeErrorMessage(25));
            Random r = new Random();
            rootcID = (long) r.nextInt(10000);
            String strCID;
            if (rootcID < 10) {
                strCID = "000" + rootcID;
            } else if (rootcID >= 10 && rootcID < 100) {
                strCID = "00" + rootcID;
            } else if (rootcID >= 100 && rootcID < 1000) {
                strCID = "0" + rootcID;
            } else {
                strCID = "" + rootcID;
            }
            root.setCookie(new SessionCookie(rootcID));
            //root.getCookie().updateTimeOfActivity();
            return "SUCCESS\t" + strCID + "\r\n";
        }
        int userNumber = -1;
        for (int i = 0; i < countValidUsr(); ++i) {
            if (users[i].getName().equals(args[1])) {
                userNumber = i;
                break;
            }
        }
        if (userNumber == -1) {
            return (MessageFactory.makeErrorMessage(20));
        }
        if (users[userNumber].getCookie() != null) {
            return (MessageFactory.makeErrorMessage(25));
        }
        if (!users[userNumber].checkPassword(args[2])) {
            return (MessageFactory.makeErrorMessage(21));
        }
        Random r = new Random();
        int check = 0;
        long cID;
        do {
            cID = (long) r.nextInt(10000);
            for (int i = 0; i < countValidUsr(); ++i) {
                if (users[i].getCookie() == null) {
                    continue;
                }
                if (users[i].getCookie().getID() == cID) {
                    check = 1;
                }
            }
        } while (check == 1);
        String strCID;
        if (cID < 10) {
            strCID = "000" + cID;
        } else if (cID >= 10 && cID < 100) {
            strCID = "00" + cID;
        } else if (cID >= 100 && cID < 1000) {
            strCID = "0" + cID;
        } else {
            strCID = "" + cID;
        }
        users[userNumber].setCookie(new SessionCookie(cID));
        // users[userNumber].getCookie().updateTimeOfActivity();
        return "SUCCESS\t" + strCID + "\r\n";
    }

    public String postMessage(String args[], String name) {
        if (args == null)
            return MessageFactory.makeErrorMessage(0);
        if (name == null)
            return MessageFactory.makeErrorMessage(0);
        args[2] = args[2].trim();
        if (checkRoot(args)) {
            if (args[2].length() < 1)
                return MessageFactory.makeErrorMessage(24);
            String messageToBeGiven = name + ": " + args[2];
            obj.put(messageToBeGiven);
            root.getCookie().updateTimeOfActivity();
            return "SUCCESS\r\n";
        }
        int retError = 0;
        for (int k = 0; k < countValidUsr(); k++) {
            if (users[k].getName().equals(name))
                retError++;
        }
        if (retError == 0)
            return MessageFactory.makeErrorMessage(20);
        if (args[2].length() < 1)
            return MessageFactory.makeErrorMessage(24);
        String messageToBeGiven = name + ": " + args[2];
        obj.put(messageToBeGiven);
        if (getUserIndex(args[1]) != -1)
            if (users[getUserIndex(args[1])].getCookie() != null)
                users[getUserIndex(args[1])].getCookie().updateTimeOfActivity();
        return "SUCCESS\r\n";
    }

    public String getMessages(String args[]) {
        if (args == null)
            return MessageFactory.makeErrorMessage(0);
        String messageToReturn = "";
        args[2] = args[2].trim();
        int numberReq;
        try {
            numberReq = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            return MessageFactory.makeErrorMessage(24);
        }
        if (numberReq < 0 || numberReq > maxMessages)
            return MessageFactory.makeErrorMessage(24);
        if (obj.messageCounter() == 0)
            return "SUCCESS\t" + messageToReturn + "\r\n";
        int size = obj.getNewest(numberReq).length;
        for (int k = 0; k < size - 1; k++) {
            messageToReturn += obj.getNewest(numberReq)[k];
            messageToReturn += "\t";
        }
        messageToReturn += obj.getNewest(numberReq)[size - 1];
        return "SUCCESS\t" + messageToReturn + "\r\n";
    }

}

