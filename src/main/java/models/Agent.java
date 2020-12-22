package models;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Agent implements Runnable {

    private final Index2D finalPosition;
    protected int id;
    Index2D currentPosition = new Index2D(-1, -1);
    private Environment environment;


    public Agent(int id, Index2D finalPosition) {
        this.id = id;
        this.finalPosition = finalPosition;
    }

    /**
     * permet de savoir si l'agent est à sa position final
     *
     * @return true si l'agent est à sa pos final false sinon
     */
    public boolean isSatisfied() {
        return currentPosition.equals(finalPosition);
    }


    /**
     * lance le travaille de l'agent, tant que l'environement n'est pas fini il fait la suite d'action suivante en boucle
     * regarde si sa boite de message est vide ou non
     * si elle est pas vide alors il va d'abord la néttoyer avant de regarder le premier message
     * si se message est une demande de bouger alors il va essayer de bouger et va envoyer sa réussite ou non à l'agent qui lui a demandé de bouger
     * si se message est un accusé de reception qui lui dit que la demande a été rejeté alors
     * si sa boite de message est vide et qu'il n'est pas sur sa position final alors il regarde la direction qui le rapproche le plus de sa position final
     * il regarde si il peut aller vers cet position si il peut il le fait et si il peut pas alors il envoi un message à l'agent dessus pour qu'il ce déplace
     * puis il recommence ces actions
     *
     * nous avons quelque problème de conflit sur les déplacement il faudrait rajouter une memoire au agent pour éviter qu'il ne fasse toujours les même cycles d'action et déplacement
     */
    @Override
    public void run() {
        currentPosition = environment.findAgentPosition(this);

        while (environment.environnementFini()) {
            this.sleep();


            List<Index2D> neighbours = environment.getNeighbours(this);
            Index2D closest = finalPosition.closest(neighbours);
            Agent neighbour = environment.getAgent(closest);

            if (!environment.getMessageBox(this).isEmpty()) {
                environment.nettoieBox(this);
                Message newMessage = environment.getMessageBox(this).firstMessage();
                if (newMessage != null) {
                    int type = newMessage.type;
                    int reponse = newMessage.getDemande();

                    //si on a une demande de bouger
                    if (type == 1) { //on essaie de faire ce que le demandeur veut
                        ArrayList<Index2D> posDeplacement = new ArrayList<Index2D>();
                        double distanceMin = 1000.0;
                        for (Index2D i : neighbours) {
                            if (environment.getAgent(i) == null) {
                                if (distanceMin > i.distance(finalPosition)) {
                                    posDeplacement.clear();
                                    distanceMin = i.distance(finalPosition);
                                    posDeplacement.add(i);
                                }
                                if (distanceMin == i.distance(finalPosition)) {
                                    posDeplacement.add(i);
                                }
                            }
                        }
                        if (!posDeplacement.isEmpty()) { // on ce déplace et on envoie l'accusé de reception
                            Index2D oldPos = currentPosition;
                            Random rand = new Random();
                            Index2D posChoisi = posDeplacement.get(rand.nextInt(posDeplacement.size()));
                            environment.move(this, currentPosition, posChoisi);
                            currentPosition = posChoisi;
                            environment.updateView();
                            Message accuserReception = new Message(newMessage.envoyeur, this, 0, newMessage.getWhereE(), currentPosition, 0);
                            environment.envoi(accuserReception);
                            environment.getMessageBox(this).suppMessage();
                            for (Message m : environment.getMessageBox(this).getMessages()) { //ici on fait en sorte que pour toute les demandes de bouge toi envoyer pour une même position soit enlevé vu qu'on a bougé
                                if (m.getType() == 1 && m.whereR == oldPos) {
                                    accuserReception = new Message(m.envoyeur, this, 0, newMessage.getWhereE(), currentPosition, 0);
                                    environment.envoi(accuserReception);
                                    environment.getMessageBox(this).suppMessage(m);
                                }
                            }
                        } else {
                            Message accuserReception = new Message(newMessage.envoyeur, this, -1, newMessage.getWhereE(), currentPosition, 0);
                            environment.envoi(accuserReception);
                            environment.getMessageBox(this).suppMessage();
                        }
                    }
                    //si on a une réponse à notre demande il faudra le gérer pour qu'il fasse certainnes action en fonction de la réponse.
                    else if (type == 0) {
                        environment.getMessageBox(this).suppMessage();
                        if (reponse == 0) {
                        }
                    }
                }

            }


            if (isSatisfied()) {
                continue; // il n'essaye pas de bougé si il est satisfé
            }

            if (neighbour == null) {
                // move there
                environment.move(this, currentPosition, closest);
                currentPosition = closest;
                environment.updateView();

            } else {
                // send msg to the blocking agent
                Message m = new Message(neighbour, this, 1, closest, currentPosition, 1);
                environment.envoi(m);

            }


        }

        System.out.println("agent satisfied: " + id);
    }

    private void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Agent agent = (Agent) o;
        return id == agent.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * récupère la position actuel de l'agent
     *
     * @return la position de l'agent
     */
    public Index2D getCurrentPosition() {
        return currentPosition;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public int getId() {
        return id;
    }


}
