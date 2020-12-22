package models;

import java.util.Objects;

/**
 * ici cet classe est la classe qui définit un message il y a des paramètre come reponse et type qui sont à optimiser car
 * c'est pour la partie de reflexion de l'agent en fonction du type et réponse il doit faire certaines action nous n'avons pas eu encore el temps de tout mettre en place
 */
public class Message {

    protected Agent receveur;
    protected Agent envoyeur;
    protected int reponse;
    protected Index2D whereR;
    protected Index2D whereE;
    protected int type;

    /**
     * @param receveur qui reçoit le message
     * @param envoyeur qui à envoyer le message
     * @param reponse  un entier représentant la reponse d'une demande (0 c'est ok, -1 je ne peux pas)
     * @param whereR   la position du receveur
     * @param whereE   la position de l'envoyeur
     * @param type     un entier représentant le type de message (0 accusé de reception, 1 demande d'action a faire)
     */
    public Message(Agent receveur, Agent envoyeur, int reponse, Index2D whereR, Index2D whereE, int type) {
        this.receveur = receveur;
        this.envoyeur = envoyeur;
        this.reponse = reponse;
        this.whereR = whereR;
        this.whereE = whereE;
        this.type = type;

    }

    public int getDemande() {
        return reponse;
    }

    public Index2D getWhereE() {
        return whereE;
    }

    public int getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Message)) {
            return false;
        }
        Message message = (Message) o;
        return reponse == message.reponse &&
            Objects.equals(receveur, message.receveur) &&
            Objects.equals(envoyeur, message.envoyeur);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receveur, envoyeur, reponse);
    }
}
