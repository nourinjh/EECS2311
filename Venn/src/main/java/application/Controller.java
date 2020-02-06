package application;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class Controller {
	
	@FXML
	private Circle circle1;
	@FXML
	private Circle circle2;
	@FXML
	private Circle circle3;
	
	@FXML
	private AnchorPane pane;
	
	@FXML
	private Button screenshotButton;
	
	@FXML
	private TextField title;

	@FXML
	private Text source;

	@FXML
	private Text target;

	@FXML // fx:id="target1"
	private Text target1; // Value injected by FXMLLoader

	@FXML // fx:id="source1"
	private Text source1; // Value injected by FXMLLoader

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;
	
	@FXML
	Alert a = new Alert(AlertType.NONE); 
	

	//Drag and drop function (Text to Text)
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
		if (event.getDragboard().hasString()) {
			event.acceptTransferModes(TransferMode.ANY);
		}
	}

	@FXML
	void handleDragDropped(DragEvent event) {
		String str = event.getDragboard().getString();
		target.setText(str);
	}

	@FXML
	void handleDragDetect(MouseEvent event) {
		Dragboard db = source1.startDragAndDrop(TransferMode.ANY);

		ClipboardContent cb = new ClipboardContent();
		cb.putString(source1.getText());

		db.setContent(cb);

		event.consume();

	}

	@FXML
	void handleDragDrop(DragEvent event) {
		String str = event.getDragboard().getString();
		target1.setText(str);
	}

	@FXML
	void handleTextDragO(DragEvent event) {
		if (event.getDragboard().hasString()) {
			event.acceptTransferModes(TransferMode.ANY);
		}
	}

	@FXML
	void handleDragDone(DragEvent event) {
		
	}

	@FXML
	void handleDragFin(DragEvent event) {

	}
	
	@FXML
	void takeScreenshot() {
		try {
			String name = title.getText();
			if (name.contentEquals("") || name == null) {
				name = "Venn Diagram";
			}
			name += ".png";
			FileChooser fc = new FileChooser();
			fc.setTitle("Save");
			fc.setInitialFileName(name);
			File selectedFile = fc.showSaveDialog(pane.getScene().getWindow());
			
	        Bounds bounds = pane.getBoundsInLocal();
	        Bounds screenBounds = pane.localToScreen(bounds);
	        int x = (int) screenBounds.getMinX();
	        int y = (int) screenBounds.getMinY();
	        int width = (int) screenBounds.getWidth();
	        int height = (int) screenBounds.getHeight();
	        Rectangle screenRect = new Rectangle(x, y, width, height);
	        BufferedImage capture = new Robot().createScreenCapture(screenRect);
	        ImageIO.write(capture, "png", selectedFile);
	        System.out.println("File successfully saved!");
	    } catch (Exception e) {
//	        ex.printStackTrace();
	    	System.out.println("Error: File not saved.");
	    }
	}
	

	// This method is called by the FXMLLoader when initialization is complete
	@FXML
	void initialize() {

	}
}
