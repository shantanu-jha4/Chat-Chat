/**
 * <b> CS 180 - Project 4 - Circular Buffer class </b>
 * <p>
 *
 * @author (Shantanu Jha) <(jha5@purdue.edu)>
 * @author (Shivan Desai) <(desai58@purdue.edu)>      PROJECT PARTNER
 * @version (11/14/2015)
 * @lab (809) Both of us
 */

public class CircularBuffer {
    private int size;
    String[] messages;
    private int numberOfMssgs;
    private int front;
    private int rear;

    public CircularBuffer(int size) {
        this.size = size;
        messages = new String[size];
        numberOfMssgs = 0;
        front = -1;
        rear = -1;
    }

    private int getDigits(int number) {
        if (number == 0)
            return 1;
        int counter = 0;
        while (number != 0) {
            int digit = number % 10;
            counter++;
            number /= 10;
        }
        return counter;
    }

    private String addZero(int num) {
        String number = new String();
        int reqZero = 4 - getDigits(num);
        for (int k = 0; k < reqZero; k++)
            number += "0";
        return number;
    }

    public void put(String message) {
        if (front == rear + 1 || front == 0 && rear == size - 1) {
            messages[0] = null;
            rear = 0;
        } else if (rear == -1)
            front = rear = 0;
        else if (rear == size - 1)
            rear = 0;
        else
            rear++;

        String temp = addZero(numberOfMssgs);
        temp += Integer.toString(numberOfMssgs++);
        if (Integer.parseInt(temp) == 9999)
            numberOfMssgs = 0;
        messages[rear] = temp + ") " + message;
    }

    public int messageCounter() {
        int counter = 0;
        for (int k = 0; k < messages.length; k++) {
            if (messages[k] == null)
                continue;
            counter++;
        }
        return counter;
    }

    public String[] getNewest(int numMessages) {
        int count = 0;
        String[] getMessages;
        if (messageCounter() < numMessages) {
            getMessages = new String[messageCounter()];
            for (int i = 0; i < messageCounter(); i++)
                getMessages[i] = messages[i];
            return getMessages;
        }
        getMessages = new String[Math.min(messageCounter(), numMessages)];
        int i = getMessages.length - 1;
        for (int k = rear; k >= 0; k--) {
            getMessages[i--] = messages[k];
            count++;
            if (count == getMessages.length || i == -1)
                return getMessages;
        }
        for (int k = size - 1; k > front; k--) {
            getMessages[i--] = messages[k];
            count++;
            if (count == getMessages.length || i == -1)
                break;
        }
        int countNull = 0;
        for (String k : getMessages)
            if (k == null)
                countNull++;
        String getNewMessgs[] = new String[getMessages.length - countNull];
        int index = 0;
        for (int k = 0; k < getMessages.length; k++) {
            if (getMessages[k] == null)
                continue;
            getNewMessgs[index++] = getMessages[k];
        }
        return getNewMessgs;
    }
}
