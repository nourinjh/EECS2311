package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

	public static Stage primaryStage;

	@Override
	public void start(Stage stage) throws Exception {
		primaryStage = stage;
		
		Parent root = FXMLLoader.load(getClass().getResource("/application/Test.fxml"));

		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("Venn");
		primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("images/icon.png")));
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
