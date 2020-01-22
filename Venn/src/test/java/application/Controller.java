package application;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
//import javafx.scene.shape.Circle;
import javafx.scene.text.Text;



public class Controller {
	
	@FXML
	private Text source;
	
	@FXML
	private Text target;
	
	@FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    
    @FXML
    void handleDragDetection(MouseEvent event) {
    	Dragboard db = source.startDragAndDrop(TransferMode.ANY);
    	
    	ClipboardContent cb = new ClipboardContent();
    	cb.putString(source.getText());
    	
    	db.setContent(cb);
    	
    	event.consume();
    	
    }
    
    @FXML
    void handleTextDragOver(DragEvent event) {
    	if(event.getDragboard().hasString()) {
    		event.acceptTransferModes(TransferMode.ANY);
    	}
    }
    
    @FXML
    void handleDragDropped(DragEvent event) {
    	String str = event.getDragboard().getString();
    	target.setText(str);
    }
    
    @FXML
    void handleDragDone(DragEvent event) {
    	source.setText("done");
    }


    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {

    }
}
