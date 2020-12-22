package models;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Random;
import java.util.stream.Collectors;

public class Environment extends Observable {

    protected Agent[][] grille;
    protected Agent[] agents;
    protected int height;
    protected int width;
    protected HashMap<Agent, MessageBox> messageBoxes;
    protected int nbAgent;

    public Environment(int height, int width, int nbAgent) {
        this.height = height;
        this.width = width;
        this.nbAgent = nbAgent;
    }

    /**
     * permet d'initialiser le jeu en donnant des positions aléatoire à chaque agent et un position final aléatoire pour ces même agents
     */
    public void initialiserJeu() {
        this.agents = new Agent[this.nbAgent];
        this.grille = new Agent[this.width][this.height];
        ArrayList<Index2D> finalPosPossible = new ArrayList<Index2D>();
        ArrayList<Index2D> startPosPossible = new ArrayList<Index2D>();
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                finalPosPossible.add(new Index2D(i, j));
                startPosPossible.add(new Index2D(i, j));
            }
        }
        for (int i = 0; i < this.nbAgent; i++) {
            Random rand1 = new Random();
            Random rand2 = new Random();
            int indiceFinalChoisi = rand1.nextInt(finalPosPossible.size());
            Index2D finPosPossible = finalPosPossible.get(indiceFinalChoisi);
            int indiceStartChoisi = rand2.nextInt(startPosPossible.size());
            Index2D goPosPossible = startPosPossible.get(indiceStartChoisi);

            this.agents[i] = new Agent(i + 1, finPosPossible);
            this.grille[goPosPossible.getI()][goPosPossible.getJ()] = this.agents[i];

            finalPosPossible.remove(finPosPossible);
            startPosPossible.remove(goPosPossible);
        }
        HashMap<Agent, MessageBox> messageBoxes = new HashMap<Agent, MessageBox>();
        for (Agent a : agents) {
            messageBoxes.put(a, new MessageBox());
        }
        this.setmessageBoxes(messageBoxes);

        for (Agent agent : agents) {
            agent.setEnvironment(this);
        }
    }

    /**
     * permet de déplacer un agent d'une postion vers une nouvelle position
     *
     * @param agent l'agent à déplacer
     * @param from  d'ou il vient
     * @param to    ou il va
     */
    public void move(Agent agent, Index2D from, Index2D to) {
        synchronized (this) {
            this.grille[from.getI()][from.getJ()] = null;
            this.grille[to.getI()][to.getJ()] = agent;
        }
    }


    /**
     * lance les différents threads (agent)
     */
    public void run() {
        Thread[] threads = new Thread[agents.length];

        for (int i = 0; i < agents.length; i++) {
            threads[i] = new Thread(agents[i]);
            threads[i].start();
        }
        System.out.println("agents are working");
    }

    /**
     * @return true si tous les agents sont à leurs position final sinon false
     */
    public boolean environnementFini() {
        synchronized (this) {
            for (Agent agent : agents) {
                if (!agent.isSatisfied()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @param agent l'agent dont on recherche sa position
     * @return la position de l'agent
     */
    public Index2D findAgentPosition(Agent agent) {

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (agent.equals(grille[i][j])) {
                    return new Index2D(i, j);
                }
            }
        }

        // never should happen
        return null;
    }

    /**
     * ici on recherche à partir d'un agent
     *
     * @param agent l'agent dont on recherche ses cases voisines
     * @return la liste des coordonnés des cases voisines à l'agent
     */
    public List<Index2D> getNeighbours(Agent agent) {
        return this.getNeighbours(agent.getCurrentPosition());
    }

    /**
     * ici on recherche à partir d'une position
     *
     * @param pos la position dont on recherche ses voisins
     * @return la liste des coordonnés des cases voisines à la position donné
     */
    public List<Index2D> getNeighbours(Index2D pos) {
        List<Index2D> neighbours = new ArrayList<>();

        neighbours.add(new Index2D(pos.getI(), pos.getJ() - 1)); // top
        neighbours.add(new Index2D(pos.getI(), pos.getJ() + 1)); // buttom
        neighbours.add(new Index2D(pos.getI() - 1, pos.getJ())); // left
        neighbours.add(new Index2D(pos.getI() + 1, pos.getJ())); // right

        return validNeighbours(neighbours);
    }

    /**
     * perlet de filtrer la liste des voisins pour avoir seulement les voisins dans une position possible
     * (pas de coordoné en dehors de la grille par exemple)
     *
     * @param neighbours la liste de cordonné voisine
     * @return la liste des cordonné voisine possible
     */
    private List<Index2D> validNeighbours(List<Index2D> neighbours) {
        return neighbours.stream().filter(n -> n.isValide(getWidth(), getHeight())).collect(Collectors.toList());
    }

    /**
     * permet de mettre à jour la view
     */
    public void updateView() {
        setChanged();
        notifyObservers();
    }

    public Agent getAgent(Index2D index2D) {
        return getAgent(index2D.getI(), index2D.getJ());
    }

    public Agent getAgent(int i, int j) {
        return this.grille[i][j];
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public MessageBox getMessageBox(Agent a) {
        synchronized (this) {
            return this.messageBoxes.get(a);
        }

    }

    /**
     * permet de nettoier la boite de message d'un agent, cela consiste à supprimer les messages envoyaient
     * par un agent qui n'est plus à l'endroit ou il avait envoyer son message
     *
     * @param a l'agent dont on nettoi la boite de message
     */
    public void nettoieBox(Agent a) {
        synchronized (this) {
            List<Message> newBM = this.getMessageBox(a).getMessages();
            //si l'envoyeur de la demande n'est plus la ou il a fait sa demande alors elle est obsolete
            newBM.removeIf(m -> this.getAgent(m.whereE) == null);
            this.getMessageBox(a).setMessages(newBM);
        }
    }

    /**
     * envoi un message
     *
     * @param m le message à envoyer on récupère le destinataire dans le message
     */
    public void envoi(Message m) {
        synchronized (this) {
            this.getMessageBox(m.receveur).addMessage(m);
        }
    }

    public void setmessageBoxes(HashMap<Agent, MessageBox> newBM) {
        this.messageBoxes = newBM;
    }
}
