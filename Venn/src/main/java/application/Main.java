package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 
 * @author nourinjh
 * 
 * Start of the front end
 *
 */

public class Main extends Application {

	private static Stage primaryStage;
	
    @Override
    public void start(Stage stage) throws Exception {
    	primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("/application/Test.fxml"));

        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Venn");
        primaryStage.show();
	}
    
    public static void setWindowTitle(String title) {
    	primaryStage.setTitle(title + " - Venn");
    }
    
    public static void setWindowTitle() {
    	primaryStage.setTitle("Venn");
    }

	public static void main(String[] args) {
		launch(args);
	}
}
