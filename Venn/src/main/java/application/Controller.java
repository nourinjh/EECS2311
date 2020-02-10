package application;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
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
	private ObservableList<String> items = FXCollections.observableArrayList();
	@FXML
	private ObservableList<String> circleLeftItems = FXCollections.observableArrayList("TEST", "test", "ASD", "asd");
	@FXML
	private ObservableList<String> circleRightItems = FXCollections.observableArrayList();
	@FXML
	private ObservableList<String> bothItems = FXCollections.observableArrayList();
	@FXML
	private ListView<String> circleLeftItemsList;
	@FXML
	private ListView<String> circleRightItemsList;
	@FXML
	private ListView<String> bothItemsList;
	
	@FXML
	private Circle circleLeft;
	@FXML
	private Circle circleRight;

	@FXML
	private AnchorPane pane;
	
	@FXML
	private Button screenshotButton;
	
	@FXML
	private TextField title;
	
	@FXML
	private TextField addItemField;
	@FXML
	private Button addItemButton;
	@FXML
	private ListView<String> itemsList;

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
	void addItemToList() {
		String newItem = addItemField.getText();
		if (!(newItem.equals(""))) {
			items.add(newItem);
			addItemField.setText("");
			itemsList.setItems(items);
		}
	}
	
	@FXML
	void dragFromItemsList() {
		Dragboard dragBoard = itemsList.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent(); 
        content.putString(itemsList.getSelectionModel().getSelectedItem());
        dragBoard.setContent(content);
	}
	
	@FXML
	void dragOntoItemsList() {
		itemsList.setBlendMode(BlendMode.DIFFERENCE);
	}
	
	@FXML
	void dragExitedItemsList() {
		itemsList.setBlendMode(null);
	}
	
	@FXML
	void dragOverList(DragEvent event) {
		event.acceptTransferModes(TransferMode.MOVE);
	}
	
	@FXML
	void dragDroppedOnItemsListFromCircleLeft(DragEvent event) {
		String item = event.getDragboard().getString();
		itemsList.getItems().add(item);
        circleLeftItems.remove(item);
        event.setDropCompleted(true);
	}
	
	@FXML
	void dragDroppedOnItemsListFromCircleRight(DragEvent event) {
		String item = event.getDragboard().getString();
		itemsList.getItems().add(item);
        circleRightItems.remove(item);
        event.setDropCompleted(true);
	}
	
	@FXML
	void dragFromCircleRightItemsList() {
		Dragboard dragBoard = circleRightItemsList.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent(); 
        content.putString(circleRightItemsList.getSelectionModel().getSelectedItem());
        dragBoard.setContent(content);
	}
	
	@FXML
	void dragOntoCircleRightItemsList() {
		circleRightItemsList.setBlendMode(BlendMode.DIFFERENCE);
	}
	
	@FXML
	void dragExitedCircleRightItemsList() {
		circleRightItemsList.setBlendMode(null);
	}
		
	@FXML
	void dragDroppedOnCircleRightItemsListFromItemsList(DragEvent event) {
		String item = event.getDragboard().getString();
		circleRightItemsList.getItems().add(item);
        items.remove(item);
        event.setDropCompleted(true);
	}
	
	@FXML
	void dragDroppedOnCircleRightItemsListFromCircleLeft(DragEvent event) {
		String item = event.getDragboard().getString();
		circleRightItemsList.getItems().add(item);
        circleLeftItems.remove(item);
        event.setDropCompleted(true);
	}
	
	@FXML
	void dragFromCircleLeftItemsList() {
		Dragboard dragBoard = circleLeftItemsList.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent(); 
        content.putString(circleLeftItemsList.getSelectionModel().getSelectedItem());
        dragBoard.setContent(content);
	}
	
	@FXML
	void dragOntoCircleLeftItemsList() {
		circleLeftItemsList.setBlendMode(BlendMode.DIFFERENCE);
	}
	
	@FXML
	void dragExitedCircleLeftItemsList() {
		circleLeftItemsList.setBlendMode(null);
	}
		
	@FXML
	void dragDroppedOnCircleLeftItemsListFromItemsList(DragEvent event) {
		String item = event.getDragboard().getString();
		circleLeftItemsList.getItems().add(item);
        items.remove(item);
        event.setDropCompleted(true);
	}
	
	@FXML
	void dragDroppedOnCircleLeftItemsListFromCircleRight(DragEvent event) {
		String item = event.getDragboard().getString();
		circleLeftItemsList.getItems().add(item);
        circleRightItems.remove(item);
        event.setDropCompleted(true);
	}
	
	@FXML
	void takeScreenshot() {
		try {
			String name = title.getText();
			if (name.equals("")) {
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
	
	@FXML
	void moveFromItemsToCircleLeft() {
		String item = itemsList.getSelectionModel().getSelectedItem();
		circleLeftItems.add(item);
		items.remove(item);
	}

	

	// This method is called by the FXMLLoader when initialization is complete
	@FXML
	void initialize() {
		ObservableList<String> strictlyLeftItems = FXCollections.observableArrayList(circleLeftItems);
		strictlyLeftItems.removeAll(circleRightItems);
		ObservableList<String> strictlyRightItems = FXCollections.observableArrayList(circleLeftItems);
		strictlyLeftItems.removeAll(circleLeftItems);
		itemsList.setItems(items);
		circleLeftItemsList.setItems(strictlyLeftItems);
		circleRightItemsList.setItems(strictlyRightItems);
	}
}
