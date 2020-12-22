import javafx.application.Application;
import javafx.stage.Stage;
import models.Environment;
import views.GridView;


public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    public void start(Stage stage) throws Exception {
        Environment env = new Environment(5, 5, 10);
        env.initialiserJeu();
        int width = 50, height = 50;
        GridView gridView = new GridView(width * 12, height * 10, env);
        env.addObserver(gridView);
        stage.setScene(gridView.getScene());
        stage.show();
        env.run();
    }
}

