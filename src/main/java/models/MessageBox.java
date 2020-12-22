package models;

import java.util.ArrayList;
import java.util.List;

/**
 * ici on a une boite de message elle est composé d'une liste de message c'est une classe très simple elle permet juste de gérer les message reçu par l'agent
 */
public class MessageBox {
    protected List<Message> messages = new ArrayList<>();

    public MessageBox() {
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public List<Message> getMessages() {
        synchronized (this) {
            return messages;
        }
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public boolean isEmpty() {
        return messages.isEmpty();
    }

    public Message firstMessage() {
        if (isEmpty()) {
            return null;
        }
        return messages.get(0);
    }

    public void suppMessage() {
        if (!isEmpty()) {
            messages.remove(0);
        }
    }

    public void suppMessage(Message m) {
        if (!isEmpty()) {
            messages.remove(m);
        }
    }
}
