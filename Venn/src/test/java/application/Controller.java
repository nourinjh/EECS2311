package application;

/**
 * Sample Skeleton for 'Test.fxml' Controller Class
 */
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.text.Text;

public class Controller {
	
	private Text source;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;
	
	@FXML
    void handle(DragEvent event) {
		Dragboard db = source.startDragAndDrop(TransferMode.ANY);
		ClipboardContent cp = new ClipboardContent();
		
		cp.putString(source.getText());
		
		db.setContent(cp);
		
		event.consume();
    }

	@FXML // This method is called by the FXMLLoader when initialization is complete
	void initialize() {

	}
}
