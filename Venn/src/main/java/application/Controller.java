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
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class Controller {
	
	@FXML
	private ObservableList<String> items = FXCollections.observableArrayList();
	@FXML
	private ObservableList<String> circleLeftItems = FXCollections.observableArrayList();
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
	private javafx.scene.shape.Rectangle frameRect;
	
	@FXML
	private Button deleteButton;
	@FXML
	private Button clearButton;
	
	@FXML
	private TextField title;
	@FXML
	private TextField circleLeftTitle;
	@FXML
	private TextField circleRightTitle;
	
	@FXML
	private TextField addItemField;
	@FXML
	private Button addItemButton;
	@FXML
	private ListView<String> itemsList;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;
	
	@FXML
	Alert a = new Alert(AlertType.NONE); 

	@FXML
	void addItemToList() {
		String newItem = addItemField.getText();
		if (!(newItem.equals("") || itemsList.getItems().contains(newItem))) {
			itemsList.getItems().add(newItem);
			addItemField.setText("");
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
	void deleteItem() {
		String s = itemsList.getSelectionModel().getSelectedItem();
		if (s != null) {
			itemsList.getItems().remove(s);
			itemsList.getSelectionModel().clearSelection();
		} else {
		s = circleLeftItemsList.getSelectionModel().getSelectedItem();
		if (s != null) {
			circleLeftItemsList.getItems().remove(s);
			circleLeftItemsList.getSelectionModel().clearSelection();
		} else {
		s = circleRightItemsList.getSelectionModel().getSelectedItem();
		if (s != null) {
			circleRightItemsList.getItems().remove(s);
			circleRightItemsList.getSelectionModel().clearSelection();
		} else {
		s = bothItemsList.getSelectionModel().getSelectedItem();
		if (s != null) {
			bothItemsList.getItems().remove(s);
			bothItemsList.getSelectionModel().clearSelection();
		}}}}
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
	void dragDroppedOnItemsList(DragEvent event) {
		String item = event.getDragboard().getString();
		if(!itemsList.getItems().contains(item)) {
			itemsList.getItems().add(item);
			((ListView<String>)event.getGestureSource()).getItems().remove(item);
		}
        event.setDropCompleted(true);
        event.consume();
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
	void dragDroppedOnCircleRightItemsList(DragEvent event) {
		String item = event.getDragboard().getString();
		if(!circleRightItemsList.getItems().contains(item)) {
			circleRightItemsList.getItems().add(item);
			((ListView<String>)event.getGestureSource()).getItems().remove(item);
		}
        event.setDropCompleted(true);
        event.consume();
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
	void dragDroppedOnCircleLeftItemsList(DragEvent event) {
		String item = event.getDragboard().getString();
		if(!circleLeftItemsList.getItems().contains(item)) {
			circleLeftItemsList.getItems().add(item);
			((ListView<String>)event.getGestureSource()).getItems().remove(item);
		}
        event.setDropCompleted(true);
        event.consume();
	}
	
	@FXML
	void dragFromBothItemsList() {
		Dragboard dragBoard = bothItemsList.startDragAndDrop(TransferMode.MOVE);
        ClipboardContent content = new ClipboardContent(); 
        content.putString(bothItemsList.getSelectionModel().getSelectedItem());
        dragBoard.setContent(content);
	}
	
	@FXML
	void dragOntoBothItemsList() {
		bothItemsList.setBlendMode(BlendMode.DIFFERENCE);
	}
	
	@FXML
	void dragExitedBothItemsList() {
		bothItemsList.setBlendMode(null);
	}
		
	@FXML
	void dragDroppedOnBothItemsList(DragEvent event) {
		String item = event.getDragboard().getString();
		if(!bothItemsList.getItems().contains(item)) {
			bothItemsList.getItems().add(item);
			((ListView<String>)event.getGestureSource()).getItems().remove(item);
		}
        event.setDropCompleted(true);
        event.consume();
	}
		
	@FXML
	void takeScreenshot() {
		String mainTitle = title.getText()+"";
		String leftTitle = circleLeftTitle.getText()+"";
		String rightTitle = circleRightTitle.getText()+"";
		try {
			String name = title.getText();
			if (name.equals("")) {
				if (!(leftTitle.equals("") && rightTitle.equals(""))) {
					name = leftTitle + " vs " + rightTitle;
				}
				else {
					name = "Venn Diagram";
					circleLeftTitle.setText(" ");
					circleRightTitle.setText(" ");
				}
				title.setText(" ");
			}
			name += ".png";
			FileChooser fc = new FileChooser();
			fc.setTitle("Save");
			fc.setInitialFileName(name);
			File selectedFile = fc.showSaveDialog(pane.getScene().getWindow());
			
	        Bounds bounds = frameRect.getBoundsInLocal();//pane.getBoundsInLocal();
	        Bounds screenBounds = frameRect.localToScreen(bounds);//pane.localToScreen(bounds);
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
	    	System.out.println(e);
	    }
		title.setText(mainTitle);
		circleLeftTitle.setText(leftTitle);
		circleRightTitle.setText(rightTitle);
	}
	
	@FXML
	void clearDiagram() {
		itemsList.getItems().addAll(circleLeftItemsList.getItems());
		itemsList.getItems().addAll(circleRightItemsList.getItems());
		itemsList.getItems().addAll(bothItemsList.getItems());
		circleLeftItemsList.getItems().clear();
		circleRightItemsList.getItems().clear();
		bothItemsList.getItems().clear();
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
