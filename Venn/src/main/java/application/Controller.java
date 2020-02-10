package application;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
            Robot robot = new Robot();
            String format = "jpg";
            String fileName = "FullScreenshot." + format;
             
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenFullImage = robot.createScreenCapture(screenRect);
            ImageIO.write(screenFullImage, format, new File(fileName));
             
            System.out.println("A full screenshot saved!");
        } catch (AWTException | IOException ex){
            System.err.println(ex);
        }
		
	}

	

	// This method is called by the FXMLLoader when initialization is complete
	@FXML
	void initialize() {

	}
}
