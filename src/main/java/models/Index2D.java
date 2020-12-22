package models;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * ici on donne à chaque case i,j une valeur 2D ce qui simplifie le code et sa compréhension
 */
public class Index2D {
    protected int i, j;

    public Index2D(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public ArrayList<Index2D> closest(List<Index2D> other) {
        double minDist = Double.POSITIVE_INFINITY;
        ArrayList<Index2D> closest = new ArrayList<>();
        for (Index2D i : other) {
            if (minDist > distance(i)) {
                closest.clear();
                minDist = distance(i);
                closest.add(i);
            }
            if(minDist == distance(i)){
                closest.add(i);
            }
        }
        return closest;
    }

    public Index2D closestAgent(List<Index2D> other) {
        double minDist = Double.POSITIVE_INFINITY;
        Index2D closest = null;

        for (Index2D i: other) {
            if (minDist > distance(i)) {
                minDist = distance(i);
                closest = i;
            }
        }

        return closest;
    }

    public int distance(Index2D other) {
        return Math.abs(i - other.i) + Math.abs((j - other.j));
    }


    public boolean isValide(int maxI, int maxJ) {

        return i >= 0 && i < maxI && j >= 0 && j < maxJ;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    public int getJ() {
        return j;
    }

    public void setJ(int j) {
        this.j = j;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Index2D index2D = (Index2D) o;
        return i == index2D.i &&
            j == index2D.j;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j);
    }
}
