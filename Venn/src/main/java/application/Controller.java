package application;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
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
	private Pane frameRect;
	
	@FXML
	private Button deleteButton;
	@FXML
	private Button clearButton;
	
	@FXML
	private Button loadButton;
	@FXML
	private Button saveButton;
	
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
	
	private String fileTitle = null;

	@FXML
	void addItemToList() {
		String newItem = addItemField.getText();
		if (!(newItem.equals("") || itemsList.getItems().contains(newItem))) {
			itemsList.getItems().add(newItem);
			addItemField.setText("");
		}
	}
	
	@FXML
	void addItemWithEnter(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER)  {
			addItemToList();
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
		if (itemsList.isFocused()) {
			itemsList.getItems().remove(itemsList.getSelectionModel().getSelectedItem());
			if (itemsList.getItems().isEmpty()) {
				addItemField.requestFocus();
			}
		} else if (circleLeftItemsList.isFocused()) {
			circleLeftItemsList.getItems().remove(circleLeftItemsList.getSelectionModel().getSelectedItem());
			circleLeftItemsList.getSelectionModel().clearSelection();
		} else if (circleRightItemsList.isFocused()) {
			circleRightItemsList.getItems().remove(circleRightItemsList.getSelectionModel().getSelectedItem());
			circleRightItemsList.getSelectionModel().clearSelection();
		} else if (bothItemsList.isFocused()) {
			bothItemsList.getItems().remove(bothItemsList.getSelectionModel().getSelectedItem());
			bothItemsList.getSelectionModel().clearSelection();
		}
	}
	
	@FXML
	void deleteItemWithDeleteKey(KeyEvent event) {
		if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE)  {
			addItemToList();
        }
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
	
	@SuppressWarnings("unchecked")
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
		
	@SuppressWarnings("unchecked")
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
		
	@SuppressWarnings("unchecked")
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
		
	@SuppressWarnings("unchecked")
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
				}
				title.setText(" ");
			}
			if (circleLeftTitle.getText().contentEquals(""))
				circleLeftTitle.setText(" ");
			if (circleRightTitle.getText().contentEquals(""))
				circleRightTitle.setText(" ");
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
	    	a.setAlertType(AlertType.ERROR);
	    	a.setContentText("File was not saved");
	    	a.setTitle("Error");
	    	a.show();
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
	
	@FXML
	void saveToFile() {
		try {
			String name;
			if (fileTitle != null) {
				name = fileTitle;
			} else if (!title.getText().equals("")) {
				name = title.getText() + ".venn";
			} else if (!circleLeftTitle.getText().contentEquals("") && !circleRightTitle.getText().contentEquals("")) {
				name = circleLeftTitle.getText() + " vs " + circleRightTitle.getText() + ".venn";
			} else {
				name = "Venn Diagram.venn";
			}
			
			FileChooser fc = new FileChooser();
			fc.setTitle("Save");
			fc.setInitialFileName(name);
			File selectedFile = fc.showSaveDialog(pane.getScene().getWindow());
	
			/*
			 * Order of items in file:
			 * Title
			 * Left circle title
			 * Right circle title
			 * Unassigned items list
			 * Left circle items list
			 * Centre items list
			 * Right circle items list
			 */
		    String fileContent = title.getText() + "𔓱𔓱" + circleLeftTitle.getText() + "𔓱𔓱" + circleRightTitle.getText() + "𔓱𔓱";
		    for (String i : itemsList.getItems()) {
		    	fileContent += i + "𔓱";
		    }
		    fileContent += "𔓱";
		    for (String i : circleLeftItemsList.getItems()) {
		    	fileContent += i + "𔓱";
		    }
		    fileContent += "𔓱";
		    for (String i : bothItemsList.getItems()) {
		    	fileContent += i + "𔓱";
		    }
		    fileContent += "𔓱";
		    for (String i : circleRightItemsList.getItems()) {
		    	fileContent += i + "𔓱";
		    }
		    fileContent = fileContent.substring(0, fileContent.length()-2);
		     
		    BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
		    writer.write(fileContent);
		    writer.close();
		} catch (Exception e) {
	    	System.out.println("Error: File not saved.");
	    	System.out.println(e);
	    	a.setAlertType(AlertType.ERROR);
	    	a.setContentText("File was not saved");
	    	a.setTitle("Error");
	    	a.show();
		}
	}
	
	void doTheLoad() {
		try {
			FileChooser fc = new FileChooser();
			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Venn files (*.venn)", "*.venn"));
			File file = fc.showOpenDialog(pane.getScene().getWindow());
			String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
			fileTitle = file.getName();
			List<String> elements = new ArrayList<String>();
			for (String s : content.split("𔓱𔓱")) {
				elements.add(s);
			}
			title.setText(elements.get(0));
			circleLeftTitle.setText(elements.get(1));
			circleRightTitle.setText(elements.get(2));
			itemsList.getItems().clear();
			for (String s : elements.get(3).split("𔓱")) {
				itemsList.getItems().add(s);
			}
			circleLeftItemsList.getItems().clear();
			for (String s : elements.get(4).split("𔓱")) {
				circleLeftItemsList.getItems().add(s);
			}
			bothItemsList.getItems().clear();
			for (String s : elements.get(5).split("𔓱")) {
				bothItemsList.getItems().add(s);
			}
			circleRightItemsList.getItems().clear();
			for (String s : elements.get(6).split("𔓱")) {
				circleRightItemsList.getItems().add(s);
			}
		} catch (Exception e) {
			System.out.println("Error: File not saved.");
			System.out.println(e);
			a.setAlertType(AlertType.ERROR);
			a.setContentText("File could not be opened");
			a.setTitle("Error");
			a.show();
		}
	}
	
	@FXML
	void loadFromFile() {
		if (!title.getText().equals("") || !circleLeftTitle.getText().equals("") || !circleRightTitle.getText().equals("") || !itemsList.getItems().isEmpty()
				|| !circleLeftItemsList.getItems().isEmpty() || !bothItemsList.getItems().isEmpty() || !circleRightItemsList.getItems().isEmpty()) {
			a.setAlertType(AlertType.CONFIRMATION);
			a.setHeaderText("Are you sure you want to open another file?");
			a.setContentText("You will lose any unsaved changes");
			Optional<ButtonType> result = a.showAndWait();
	
			if (result.get() == ButtonType.OK){
				doTheLoad();
			}
		} else {
			doTheLoad();
		}
	}
	

	// This method is called by the FXMLLoader when initialization is complete
	@FXML
	void initialize() {
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	            pane.requestFocus();
	        }
	    });
		deleteButton.setFocusTraversable(false);
		clearButton.setFocusTraversable(false);
		screenshotButton.setFocusTraversable(false);
		loadButton.setFocusTraversable(false);
		saveButton.setFocusTraversable(false);
		addItemButton.setFocusTraversable(false);
		addItemField.setFocusTraversable(false);
		ObservableList<String> strictlyLeftItems = FXCollections.observableArrayList(circleLeftItems);
		strictlyLeftItems.removeAll(circleRightItems);
		ObservableList<String> strictlyRightItems = FXCollections.observableArrayList(circleLeftItems);
		strictlyLeftItems.removeAll(circleLeftItems);
		itemsList.setItems(items);
		circleLeftItemsList.setItems(strictlyLeftItems);
		circleRightItemsList.setItems(strictlyRightItems);
	}
}
