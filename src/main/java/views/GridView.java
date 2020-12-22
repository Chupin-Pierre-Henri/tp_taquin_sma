package views;


import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import models.Agent;
import models.Environment;

import java.util.Observable;
import java.util.Observer;


public class GridView implements Observer {
    protected Scene scene;
    HBox hbox = new HBox();
    Environment environment;
    GridPane gridPane = new GridPane();

    public GridView(int width, int height, Environment environment) {
        this.environment = environment;

        this.initView();
        this.scene = new Scene(this.hbox, width, height);
    }

    protected void initView() {
        this.updateGrid();

        gridPane.setHgap(15);
        gridPane.setVgap(15);
        gridPane.setPadding(new Insets(20));

        this.hbox.getChildren().addAll(gridPane);
    }

    public void update() {
        Platform.runLater(() -> {
            updateGrid();
        });
    }


    private void updateGrid() {
        gridPane.getChildren().clear();

        for (int i = 0; i < this.environment.getWidth(); i++) {
            for (int j = 0; j < this.environment.getHeight(); j++) {

                Agent agent = this.environment.getAgent(i, j);
                Text text;
                if (agent == null) {
                    text = new Text("0");
                } else {
                    text = new Text(agent.getId()+"");
                }

                text.setStyle("-fx-font: 45 arial;");

                gridPane.add(text, j, i);
            }
        }
    }


    public Scene getScene() {
        return scene;
    }


    @Override
    public void update(Observable o, Object arg) {
        this.update();
    }
}
