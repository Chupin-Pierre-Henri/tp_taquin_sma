package models;


import java.util.ArrayList;
import java.util.List;

public class CustomUpdater {
    List<CustomUpdateAble> updateAbles = new ArrayList<>();

    public void add(CustomUpdateAble updateAble) {
        this.updateAbles.add(updateAble);
    }

    public void update() {
        for (CustomUpdateAble updateAble : this.updateAbles) {
            updateAble.update(this);
        }
    }
}
