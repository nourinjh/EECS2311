// To export as a runnable JAR file:
// 1. Right click on "application" package
// 2. Click "Export"
// 3. Select "Runnable JAR file"
// 4. Select "Extract required libraries into generated JAR"
// 5. Click finish
// 6. Pray

/**
 * 
 * @author Andrew Hocking
 * 
 */

package application;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.effect.BlendMode;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

public class Controller {

	private static final String DEFAULT_BACKGROUND_COLOUR = "0x1d1d1d";
	private static final String DEFAULT_TITLE_COLOUR = "0xFFFFFF";
	private static final double DEFAULT_CIRCLE_OPACTIY = 0.6;
	private static boolean settingsIsOpen = false;
	private static boolean multiSelect = false;

	@FXML
	private ObservableList<String> items = FXCollections.observableArrayList();
	@FXML
	private ObservableList<DraggableItem> itemsInDiagram = FXCollections.observableArrayList();
	@FXML
	private ObservableList<DraggableItem> selectedItems = FXCollections.observableArrayList(); 

	@FXML
	private Circle circleLeft;
	@FXML
	private Circle circleRight;

	@FXML
	private AnchorPane pane;

	@FXML
	private VBox floatingMenu;
	@FXML
	private GridPane buttonGrid;

	@FXML
	private TitledPane settingsPane;
	@FXML
	private VBox settingsBox;
	@FXML
	private ColorPicker colorLeft;
	private final String DEFAULT_LEFT_COLOUR = "0xf59f9f";
	@FXML
	private Slider leftSizeSlider;
	@FXML
	private TextField leftSizeField;
	@FXML
	private ColorPicker colorRight;
	private final String DEFAULT_RIGHT_COLOUR = "0xebe071";
	@FXML
	private Slider rightSizeSlider;
	@FXML
	private TextField rightSizeField;

	@FXML
	private MenuBar menuBar;

	@FXML
	private Button screenshotButton;
	@FXML
	private Pane frameRect;

	@FXML
	private ColorPicker colorBackground;
	@FXML
	private ColorPicker colorTitles;

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

	@FXML
	private Button addImageButton;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML
	Alert a = new Alert(AlertType.NONE);

	private File openFile = null;
	
	class DraggableItem extends StackPane {
		private Label text = new Label();
		private String description;

		DraggableItem(double x, double y) {
			relocate(x - 5.0D, y - 5.0D);
			getChildren().add(this.text);
			text.setTextFill(Color.WHITE);
			setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.NONE, new CornerRadii(1), new BorderWidths(5), new Insets(2, 2, 2, 2))));
			requestFocus();

			this.text.focusedProperty().addListener((observable, hadFocus, hasFocus) -> {
				if (!hasFocus.booleanValue() && getParent() != null && getParent() instanceof Pane
						&& (this.text.getText() == null || this.text.getText().trim().isEmpty())) {
					((Pane) getParent()).getChildren().remove(this);
				}
			});
			
		
			this.focusedProperty().addListener((observable, hadFocus, hasFocus) -> {
				if (!hasFocus.booleanValue()) {
					for (DraggableItem d : selectedItems) {
						d.setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.NONE, new CornerRadii(1), new BorderWidths(5), new Insets(2, 2, 2, 2))));						
					}
					selectedItems.clear();
				}
			});
			
			this.setOnKeyPressed(keyEvent -> {
				if (keyEvent.getCode() == KeyCode.DELETE || keyEvent.getCode() == KeyCode.BACK_SPACE) {
					deleteItem();
				}
				if (keyEvent.getCode() == KeyCode.ESCAPE) {
					pane.requestFocus();
				}
				if (keyEvent.getCode() == KeyCode.SHORTCUT || keyEvent.getCode() == KeyCode.SHIFT) {
					multiSelect = true;
				}
			});
			
			this.setOnKeyReleased(keyEvent -> {
				if (keyEvent.getCode() == KeyCode.SHORTCUT || keyEvent.getCode() == KeyCode.SHIFT) {
					multiSelect = false;
				}
			});
						
			enableDrag();
		}

		public DraggableItem(double d, double e, String text) {
			this(d, e);
//			String t = text;
//			if (text.length() > 25) {
//				t = text.substring(0, 25);;
//				for (int i = 25; i < text.length(); i+=25) {
//					if (text.charAt(i - 1) != ' ' || text.charAt(i - 1) != ',' | text.charAt(i - 1) != '.' || text.charAt(i - 1) != '!' || text.charAt(i - 1) != '?') {
//						t += "-";
//					} else if (text.charAt(i - 1) == ' '){
//						i++;
//					}
//					t += "\n" + text.substring(i, Math.min(text.length(), i + 25));
//				}
//			}
//			this.text.setText(t);
			this.text.setText(text);
		}
		
		public Label getLabel() {
			return this.text;
		}
		
		public String getText() {
			return this.text.getText();
		}
		
		public void changeColor(Color c) {
			this.text.setTextFill(c);
		}
		
		public void setDescription(String desc) {
			this.description = desc;
		}
		
		public String getDescription() {
			return this.description;
		}

		private void enableDrag() {
			Delta dragDelta = new Delta();
			setOnMousePressed(mouseEvent -> {
				toFront();

				dragDelta.x = mouseEvent.getX();
				dragDelta.y = mouseEvent.getY();
				getScene().setCursor(Cursor.CLOSED_HAND);
				requestFocus();
				if (!multiSelect) {
					for (DraggableItem d : selectedItems) {
						d.setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.NONE, new CornerRadii(1), new BorderWidths(5), new Insets(2, 2, 2, 2))));						
					}
					selectedItems.clear();
				}
				selectedItems.add(this);
				setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.SOLID, new CornerRadii(1), new BorderWidths(5), new Insets(2, 2, 2, 2))));
			});
			setOnMouseReleased(mouseEvent -> {
				getScene().setCursor(Cursor.HAND);
//				if (this.text.intersects(circleLeft.getBoundsInParent()) && this.text.intersects(circleRight.getBoundsInParent())) {
//					this.text.setTextFill(Color.BLUE);
//				}
//				else if (this.text.intersects(circleLeft.getBoundsInParent())) {
//					this.text.setTextFill(Color.RED);
//				}
//				else if (this.text.intersects(circleRight.getBoundsInParent())) {
//					this.text.setTextFill(Color.GREEN);
//				}
//				else {
//					this.text.setTextFill(Color.WHITE);
//				}
			});				
			setOnMouseDragged(mouseEvent -> {
				double newX = getLayoutX() + mouseEvent.getX() - dragDelta.x;
				if (newX > floatingMenu.getLayoutX() + floatingMenu.getWidth() && newX < getScene().getWidth() - getWidth()) {
					setLayoutX(newX);
				}
				double newY = getLayoutY() + mouseEvent.getY() - dragDelta.y;
				if (newY > circleLeftTitle.getLayoutY() + 3*circleLeftTitle.getHeight() + getHeight() && newY < getScene().getHeight() - getHeight()) {
					setLayoutY(newY);
				}
			});
			setOnMouseEntered(mouseEvent -> {
				if (!mouseEvent.isPrimaryButtonDown()) {
					getScene().setCursor(Cursor.HAND);
				}
			});
			setOnMouseExited(mouseEvent -> {
				if (!mouseEvent.isPrimaryButtonDown())
					getScene().setCursor(Cursor.DEFAULT);
			});
		}

		private class Delta {
			double x;
			double y;
		}
	}

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
		if (event.getCode() == KeyCode.ENTER) {
			addItemToList();
		}
	}

	@FXML
	void dragFromItemsList() {
		Dragboard dragBoard = itemsList.startDragAndDrop(TransferMode.ANY);
		ClipboardContent content = new ClipboardContent();
		content.putString(itemsList.getSelectionModel().getSelectedItem());
		dragBoard.setContent(content);
	}

	@FXML
	void deleteItem() {
		if (itemsList.isFocused()) {
			itemsList.getItems().removeAll(itemsList.getSelectionModel().getSelectedItems());
			if (itemsList.getItems().isEmpty()) {
				addItemField.requestFocus();
			}
		} else {
			for (DraggableItem d : selectedItems) {
				pane.getChildren().remove(d);
			}
			itemsInDiagram.removeAll(selectedItems);
			selectedItems.clear();
		}
	}

	@FXML
	void keyPressOnList(KeyEvent event) {
		if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
			deleteItem();
		}
		if (event.getCode() == KeyCode.ESCAPE) {
			pane.requestFocus();
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
		event.acceptTransferModes(TransferMode.ANY);
	}

	@SuppressWarnings("unchecked")
	@FXML
	void dragDroppedOnItemsList(DragEvent event) {
		String item = event.getDragboard().getString();
		if (!itemsList.getItems().contains(item)) {
			itemsList.getItems().add(item);
			((ListView<String>) event.getGestureSource()).getItems().remove(item);
		}
		event.setDropCompleted(true);
		event.consume();
	}
	
/*
	Old drag code is in this comment block, for future reference...
	@FXML
	void dragFromCircleRightItemsList() {
		Dragboard dragBoard = circleRightItemsList.startDragAndDrop(TransferMode.ANY);
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
		if (!circleRightItemsList.getItems().contains(item)) {
			circleRightItemsList.getItems().add(item);
			((ListView<String>) event.getGestureSource()).getItems().remove(item);
		}
		event.setDropCompleted(true);
		event.consume();
	}

	@FXML
	void dragFromCircleLeftItemsList() {
		Dragboard dragBoard = circleLeftItemsList.startDragAndDrop(TransferMode.ANY);
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
		if (!circleLeftItemsList.getItems().contains(item)) {
			circleLeftItemsList.getItems().add(item);
			((ListView<String>) event.getGestureSource()).getItems().remove(item);
		}
		event.setDropCompleted(true);
		event.consume();
	}

	@FXML
	void dragFromBothItemsList() {
		Dragboard dragBoard = bothItemsList.startDragAndDrop(TransferMode.ANY);
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
		if (!bothItemsList.getItems().contains(item)) {
			bothItemsList.getItems().add(item);
			((ListView<String>) event.getGestureSource()).getItems().remove(item);
		}
		event.setDropCompleted(true);
		event.consume();
	}
 */

	@FXML
	void takeScreenshot() {
		String mainTitle = title.getText() + "";
		String leftTitle = circleLeftTitle.getText() + "";
		String rightTitle = circleRightTitle.getText() + "";
		double menuX = floatingMenu.getLayoutX();
		try {
			String name = title.getText();
			if (name.equals("")) {
				if (!(leftTitle.equals("") && rightTitle.equals(""))) {
					name = leftTitle + " vs " + rightTitle;
				} else {
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
			floatingMenu.setLayoutX(pane.getScene().getWindow().getX() - floatingMenu.getWidth() - 10);

			Bounds bounds = frameRect.getBoundsInLocal();// pane.getBoundsInLocal();
			Bounds screenBounds = frameRect.localToScreen(bounds);// pane.localToScreen(bounds);
			int x = (int) screenBounds.getMinX();
			int y = (int) screenBounds.getMinY();
			int width = (int) screenBounds.getWidth();
			int height = (int) screenBounds.getHeight();
			Rectangle screenRect = new Rectangle(x, y, width, height);
			BufferedImage capture = new Robot().createScreenCapture(screenRect);
			ImageIO.write(capture, "png", selectedFile);
		} catch (Exception e) {
			System.out.println("Error: File not saved.");
			System.out.println(e);
			a.setAlertType(AlertType.ERROR);
			a.setHeaderText("File could not be saved");
			a.setContentText("");
			a.setTitle("Error");
			a.show();
		}
		floatingMenu.setLayoutX(menuX);
		title.setText(mainTitle);
		circleLeftTitle.setText(leftTitle);
		circleRightTitle.setText(rightTitle);
	}

	@FXML
	void clearDiagram() {
		for (DraggableItem d : itemsInDiagram) {
			itemsList.getItems().add(d.getText());
			pane.getChildren().remove(d);
		}
		itemsInDiagram.clear();
	}
	
	void doTheSave(File selectedFile) {
		
		// File hierarchy of a .venn file:
		// . Diagram.venn:
		// ... Config.vlist:
		// ..... (0) Title, (1) Titles color, (2) Background color
		// ..... (0) Left circle title, (1) Left circle color, (2) Left circle scale
		// ..... (0) Right circle title, (1) Right circle color, (2) Right circle scale
		// ... Unassigned.csv:
		// ..... Unassigned items separated by new lines
		// ... InDiagram.vlist
		// ..... (0) Item text, (1) item color, (2) item x, (3) item y, (4) item description
		
		try {
			FileOutputStream fos = new FileOutputStream(selectedFile);
		    ZipOutputStream zos = new ZipOutputStream(fos);
		    
		    File config = new File("Config.vlist");
		    StringBuilder sb = new StringBuilder();
		    sb.append(title.getText() + "𔓱" + colorTitles.getValue().toString() + "𔓱" + colorBackground.getValue().toString() + "\n");
		    sb.append(circleLeftTitle.getText() + "𔓱" + colorLeft.getValue().toString() + "𔓱" + circleLeft.getScaleX() + "\n");
		    sb.append(circleRightTitle.getText() + "𔓱" + colorRight.getValue().toString() + "𔓱" + circleRight.getScaleX());
		    BufferedWriter bw = new BufferedWriter(new FileWriter(config));
		    bw.write(sb.toString());
		    bw.close();
		    
		    File unassigned = new File("Unassigned.csv");
		    sb = new StringBuilder();
		    bw = new BufferedWriter(new FileWriter(unassigned));
		    if (!itemsList.getItems().isEmpty()) {
		    	bw.write(itemsList.getItems().get(0));
		    	if (itemsList.getItems().size() > 1) {
			    	for (int i = 1; i < itemsList.getItems().size(); i++) {
			    		if (itemsList.getItems().get(i).contains(",")) {
			    			bw.append("\n\"" + itemsList.getItems().get(i) + "\"");
			    		} else {
			    			bw.append("\n" + itemsList.getItems().get(i));
			    		}
			    	}
		    	}
		    }
		    bw.close();
		    
		    File inDiagram = new File("InDiagram.vlist");
		    sb = new StringBuilder();
		    for (int i = 0; i < itemsInDiagram.size(); i++) {
		    	DraggableItem d = itemsInDiagram.get(i);
		    	sb.append(d.getText() + "𔓱" + d.getLabel().getTextFill().toString() + "𔓱" + d.getLayoutX() + "𔓱" + d.getLayoutY() + "𔓱" + "item description");
		    	if (i != itemsInDiagram.size() - 1) {
		    		sb.append("\n");
		    	}
		    }
		    bw = new BufferedWriter(new FileWriter(inDiagram));
		    bw.write(sb.toString());
		    bw.close();
		    
		    File[] files = {config, unassigned, inDiagram};
		    byte[] buffer = new byte[128];
			for (int i = 0; i < files.length; i++) {
				File f = files[i];
				if (!f.isDirectory()) {
					ZipEntry entry = new ZipEntry(f.getName());
					FileInputStream fis = new FileInputStream(f);
					zos.putNextEntry(entry);
					int read = 0;
					while ((read = fis.read(buffer)) != -1) {
						zos.write(buffer, 0, read);
					}
					zos.closeEntry();
					fis.close();
				}
			}
			zos.close();
			fos.close();
			
			for (File f : files) {
				f.delete();
			}
			
			openFile = selectedFile;
			Main.setWindowTitle(selectedFile.getName());
		} catch (Exception e) {
			System.out.println("Error: File not saved.");
			System.out.println(e);
			e.printStackTrace();
			a.setAlertType(AlertType.ERROR);
			a.setHeaderText("File could not be saved");
			a.setContentText("");
			a.show();
		}

	}
	
	@FXML
	void save() {
		if (openFile != null) {
			doTheSave(openFile);
		} else {
			saveAs();
		}
	}

	@FXML
	void saveAs() {
		try {
			String name;
			if (openFile != null) {
				name = openFile.getName();
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
			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Venn files (*.venn)", "*.venn"));
			File selectedFile = fc.showSaveDialog(pane.getScene().getWindow());
			if (!(selectedFile.getName().length() > 5 && selectedFile.getName().substring(selectedFile.getName().length() - 5).toLowerCase().equals(".venn"))) {
				selectedFile.renameTo(new File(selectedFile.getAbsolutePath() + ".venn"));
			}
			doTheSave(selectedFile);
		} catch (Exception e) {
			System.out.println("Error: File not saved.");
			System.out.println(e);
			e.printStackTrace();
			a.setAlertType(AlertType.ERROR);
			a.setHeaderText("File could not be saved");
			a.setContentText("");
			a.show();
		}
	}
	
	ObservableList<String> importCSV(File csv) throws Exception {
		ObservableList<String> list = FXCollections.observableArrayList();
		String line;
		BufferedReader br = new BufferedReader(new FileReader(csv));
		while ((line = br.readLine()) != null) {
			if (line.contains(",")) {
				line = line.substring(1, line.length()-1);
			}
	        list.add(line);
	    }
		br.close();
		return list;
	}

	void doTheLoad() {
		
		// File hierarchy of a .venn file:
		// . Diagram.venn:
		// ... Config.vlist:
		// ..... (0) Title, (1) Titles color, (2) Background color
		// ..... (0) Left circle title, (1) Left circle color, (2) Left circle scale
		// ..... (0) Right circle title, (1) Right circle color, (2) Right circle scale
		// ... Unassigned.csv:
		// ..... Unassigned items separated by new lines
		// ... InDiagram.vlist
		// ..... (0) Item text, (1) item color, (2) item x, (3) item y, (4) item description
		
		try {
			FileChooser fc = new FileChooser();
			List<String> extensions = new ArrayList<String>();
			extensions.add("*.venn");
			extensions.add("*.csv");
			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Venn and CSV files (*.venn, *.csv)", extensions));
			File file = fc.showOpenDialog(pane.getScene().getWindow());
			// TODO: Separate into an "Import CSV" function and an "Open" function
			if (file.getName().substring(file.getName().length() - 4).toLowerCase().equals(".csv")) {
				itemsList.getItems().addAll(importCSV(file));
			} else if (file.getName().substring(file.getName().length() - 5).toLowerCase().equals(".venn")) {
				String line, title, leftTitle, rightTitle, elements[];
				Color bgColor, leftColor, rightColor, titleColor;
				double leftScale, rightScale;
				ObservableList<String> unassignedItems = FXCollections.observableArrayList();
				ObservableList<DraggableItem> inDiagram = FXCollections.observableArrayList();
				ZipFile vennFile = new ZipFile(file);
				
				// Config
				ZipEntry ze = vennFile.getEntry("Config.vlist");
				BufferedReader br = new BufferedReader(new InputStreamReader(vennFile.getInputStream(ze)));
				
				elements = br.readLine().split("𔓱");
				title = elements[0];
				titleColor = Color.web(elements[1]);
				bgColor = Color.web(elements[2]);
				
				elements = br.readLine().split("𔓱");
				leftTitle = elements[0];
				leftColor = Color.web(elements[1]);
				leftScale = Double.parseDouble(elements[2]);
				
				elements = br.readLine().split("𔓱");
				rightTitle = elements[0];
				rightColor = Color.web(elements[1]);
				rightScale = Double.parseDouble(elements[2]);
				br.close();
				
				// Unassigned
				ze = vennFile.getEntry("Unassigned.csv");
				br = new BufferedReader(new InputStreamReader(vennFile.getInputStream(ze)));
				while ((line = br.readLine()) != null) {
					if (line.contains(",")) {
						line = line.substring(1, line.length()-1);
					}
					unassignedItems.add(line);
				}
				br.close();				

				// InDiagram
				ze = vennFile.getEntry("InDiagram.vlist");
				br = new BufferedReader(new InputStreamReader(vennFile.getInputStream(ze)));
				while ((line = br.readLine()) != null) {
					elements = line.split("𔓱");
					DraggableItem a = new DraggableItem(Double.parseDouble(elements[2]), Double.parseDouble(elements[3]), elements[0]);
					a.changeColor(Color.web(elements[1]));
					a.setDescription(elements[4]);
					inDiagram.add(a);
				}
				br.close();
								
				this.title.setText(title);
				this.circleLeftTitle.setText(leftTitle);
				this.circleRightTitle.setText(rightTitle);
				this.colorBackground.setValue(bgColor);
				this.changeColorBackground();
				this.colorLeft.setValue(leftColor);
				this.changeColorLeft();
				this.colorRight.setValue(rightColor);
				this.changeColorRight();
				this.colorTitles.setValue(titleColor);
				this.changeColorTitles();
				this.leftSizeSlider.setValue(leftScale*100);
				this.changeSizeLeft();
				this.rightSizeSlider.setValue(rightScale*100);
				this.changeSizeLeft();
				this.itemsList.getItems().clear();
				this.itemsList.getItems().addAll(unassignedItems);
				this.pane.getChildren().removeAll(this.itemsInDiagram);
				this.pane.getChildren().addAll(inDiagram);
				this.itemsInDiagram.clear();
				this.itemsInDiagram.addAll(inDiagram);
				
				openFile = file;
				// FIXME: Crashes the JUnit tests because they don't have a title bar on the window to change
				Main.setWindowTitle(openFile.getName());
			}
		} catch (Exception e) {
			System.out.println("Error: File not opened.");
			System.out.println(e);
			e.printStackTrace();
			a.setAlertType(AlertType.ERROR);
			a.setHeaderText("File could not be opened");
			a.setContentText("");
			a.setTitle("Error");
			a.show();
		}
	}
	/* Old loading code, for future reference...
				String content = new String(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
				List<String> elements = new ArrayList<String>();
				for (String s : content.split("𔓱𔓱")) {
					elements.add(s);
				}
				
				title.setText(elements.get(0));
				circleLeftTitle.setText(elements.get(1));
				circleLeft.setFill(Color.web(elements.get(2)));
				colorLeft.setValue(Color.web(elements.get(2)));
				circleLeft.setOpacity(DEFAULT_CIRCLE_OPACTIY);
				circleRightTitle.setText(elements.get(3));
				circleRight.setOpacity(DEFAULT_CIRCLE_OPACTIY);
				circleRight.setFill(Color.web(elements.get(4)));
				colorRight.setValue(Color.web(elements.get(4)));
				itemsList.getItems().clear();
				for (String s : elements.get(5).split("𔓱")) {
					if (!s.equals(""))
						itemsList.getItems().add(s);
				}
				circleLeftItemsList.getItems().clear();
				for (String s : elements.get(6).split("𔓱")) {
					if (!s.equals(""))
						circleLeftItemsList.getItems().add(s);
				}
				bothItemsList.getItems().clear();
				for (String s : elements.get(7).split("𔓱")) {
					if (!s.equals(""))
						bothItemsList.getItems().add(s);
				}
				circleRightItemsList.getItems().clear();
				for (String s : elements.get(8).split("𔓱")) {
					if (!s.equals(""))
						circleRightItemsList.getItems().add(s);
				}
				
				circleLeft.setScaleX(Double.parseDouble(elements.get(9)));
				circleLeft.setScaleY(Double.parseDouble(elements.get(9)));
				leftSizeSlider.setValue(100*Double.parseDouble(elements.get(9)));
				leftSizeField.setText(String.format("%.0f", 100*Double.parseDouble(elements.get(9))));
				circleRight.setScaleX(Double.parseDouble(elements.get(10)));
				circleRight.setScaleY(Double.parseDouble(elements.get(10)));
				rightSizeSlider.setValue(100*Double.parseDouble(elements.get(10)));
				rightSizeField.setText(String.format("%.0f", 100*Double.parseDouble(elements.get(10))));
	
				colorBackground.setValue(Color.web(elements.get(11)));
				colorTitles.setValue(Color.web(elements.get(12)));
				changeColorBackground();
				// FIXME: Crashes the JUnit tests because they don't have a title bar on the window to change
				Main.setWindowTitle(openFile.getName());
	 */

	@FXML
	void loadFromFile() {
		a.setAlertType(AlertType.CONFIRMATION);
		a.setHeaderText("Are you sure you want to open another file?");
		a.setContentText("You will lose any unsaved changes");
		Optional<ButtonType> result = a.showAndWait();
		if (result.get() == ButtonType.OK) {
			doTheLoad();
		}
	}

	@FXML
	void newDiagram() {
		a.setAlertType(AlertType.CONFIRMATION);
		a.setHeaderText("Are you sure you want to open a new file?");
		a.setContentText("You will lose any unsaved changes");
		Optional<ButtonType> result = a.showAndWait();

		if (result.get() == ButtonType.OK) {
			doTheNew();
		}
	}

	@FXML
	void doTheNew() {
		title.setText("");
		circleLeftTitle.setText("");
		circleRightTitle.setText("");
		itemsList.getItems().clear();
		circleLeft.setFill(Color.web(DEFAULT_LEFT_COLOUR));
		circleLeft.setOpacity(DEFAULT_CIRCLE_OPACTIY);
		circleRight.setFill(Color.web(DEFAULT_RIGHT_COLOUR));
		circleRight.setOpacity(DEFAULT_CIRCLE_OPACTIY);
		colorLeft.setValue(Color.web(DEFAULT_LEFT_COLOUR));
		colorRight.setValue(Color.web(DEFAULT_RIGHT_COLOUR));
		colorTitles.setValue(Color.web(DEFAULT_TITLE_COLOUR));
		colorBackground.setValue(Color.web(DEFAULT_BACKGROUND_COLOUR));
		changeColorBackground();
		leftSizeSlider.setValue(100);
		changeSizeLeft();
		rightSizeSlider.setValue(100);
		changeSizeRight();
		pane.getChildren().removeAll(itemsInDiagram);
		itemsInDiagram.clear();
		openFile = null;
		// FIXME: Crashes the JUnit tests because they don't have a title bar on the window to change
		Main.setWindowTitle();
	}

	@FXML
	void openManual() {
		try {
			// Change link to online PDF of manual in future release
			java.awt.Desktop.getDesktop().browse(new URI("https://github.com/nourinjh/EECS2311/"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void changeColorLeft() {
		circleLeft.setFill(colorLeft.getValue());
		circleLeft.setOpacity(DEFAULT_CIRCLE_OPACTIY);
	}

	@FXML
	void changeColorRight() {
		circleRight.setFill(colorRight.getValue());
		circleRight.setOpacity(DEFAULT_CIRCLE_OPACTIY);
	}

	@FXML
	void changeColorBackground() {
		pane.setStyle("-fx-background-color: #"
				+ colorBackground.getValue().toString().substring(2, colorBackground.getValue().toString().length() - 2)
				+ ";");
		changeColorTitles();
	}

	@FXML
	void changeColorTitles() {
		title.setStyle("-fx-background-color: #"
				+ colorBackground.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2)
				+ ";\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2)
				+ ";");
		circleLeftTitle.setStyle("-fx-background-color: #"
				+ colorBackground.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2)
				+ ";\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2)
				+ ";");
		circleRightTitle.setStyle("-fx-background-color: #"
				+ colorBackground.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2)
				+ ";\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2)
				+ ";");
	}

	@FXML
	void changeSizeLeft() {
		circleLeft.setScaleX(leftSizeSlider.getValue() / 100.0);
		circleLeft.setScaleY(leftSizeSlider.getValue() / 100.0);
		leftSizeField.setText(String.format("%.0f", leftSizeSlider.getValue()));
	}

	@FXML
	void changeSizeLeftField(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			double size;
			try {
				size = Double.parseDouble(leftSizeField.getText());
				if (size < 25) {
					size = 25;
					leftSizeField.setText("25");
				}
				if (size > 250) {
					size = 250;
					leftSizeField.setText("250");
				}
				circleLeft.setScaleX(size / 100.0);
				circleLeft.setScaleY(size / 100.0);
				leftSizeSlider.setValue(size);
			} catch (Exception e) {
				leftSizeField.setText(String.format("%.0f", leftSizeSlider.getValue()));
			}
		}
	}

	@FXML
	void changeSizeRight() {
		circleRight.setScaleX(rightSizeSlider.getValue() / 100.0);
		circleRight.setScaleY(rightSizeSlider.getValue() / 100.0);
		rightSizeField.setText(String.format("%.0f", rightSizeSlider.getValue()));
	}

	@FXML
	void changeSizeRightField(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			double size;
			try {
				size = Double.parseDouble(rightSizeField.getText());
				if (size < 25) {
					size = 25;
					rightSizeField.setText("25");
				}
				if (size > 250) {
					size = 250;
					rightSizeField.setText("250");
				}
				circleRight.setScaleX(size / 100.0);
				circleRight.setScaleY(size / 100.0);
				rightSizeSlider.setValue(size);
			} catch (Exception e) {
				rightSizeField.setText(String.format("%.0f", rightSizeSlider.getValue()));
			}
		}
	}

	@FXML
	void addImage() {
//		try {
//			FileChooser fc = new FileChooser();
//			List<String> extensions = new ArrayList<String>();
//			extensions.add("*.png");
//			extensions.add("*.jpg");
//			extensions.add("*.jpeg");
//			fc.getExtensionFilters()
//					.add(new FileChooser.ExtensionFilter("Image files (*.png, *.jpg, *.jpeg)", extensions));
//			File file = fc.showOpenDialog(pane.getScene().getWindow());
//			System.out.println(file.getAbsolutePath());
//		} catch (Exception e) {
//			System.out.println("Error: File not opened.");
//			System.out.println(e);
//			a.setAlertType(AlertType.ERROR);
//			a.setHeaderText("File could not be opened");
//			a.setContentText("");
//			a.setTitle("Error");
//			a.show();
//		}
//		a.setAlertType(AlertType.INFORMATION);
//		a.setHeaderText("This feature is not yet available");
//		a.setContentText("Adding images to diagrams will be available in a future release");
//		a.setTitle("Feature not yet available");
//		a.show();
		String[] words = {"foobar", "foo", "bar", "baz", "qux", "quux", "quuz", "corge", "grault", "garply", "waldo", "flub", "plugh", "xyzzy", "thud", "wibble", "wobble", "wubble", "flob"};
		addItemToDiagram(floatingMenu.getLayoutX() + floatingMenu.getWidth() + 20, circleLeftTitle.getLayoutY() + 5*circleLeftTitle.getHeight(), words[(int)(Math.random() * words.length)]);
//		addItemToDiagram(floatingMenu.getLayoutX() + floatingMenu.getWidth() + 20, circleLeftTitle.getLayoutY() + 5*circleLeftTitle.getHeight(), "This is a very long string. Extremely long, in fact. It's a string that just keeps going, and going, and going! I hope it's not way too long to handle!");
	}

	@FXML
	void expandSettings() {
		if (settingsPane.isExpanded() && !settingsIsOpen) {
			settingsIsOpen = true;
			floatingMenu.setLayoutY(floatingMenu.getLayoutY() - (settingsPane.getHeight() / 2));
		} else if (!settingsPane.isExpanded()) {
			settingsIsOpen = false;
			floatingMenu.setLayoutY(floatingMenu.getLayoutY() + (settingsPane.getHeight() / 2));
		}
	}

	@FXML
	void removeFocusEnter(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			removeFocus();
		}
	}

	@FXML
	void removeFocus() {
		pane.requestFocus();
		itemsList.getSelectionModel().clearSelection();
		selectedItems.clear();
	}
	
	@SuppressWarnings("unchecked")
	@FXML
	void dropItem(DragEvent event) {
		String item = event.getDragboard().getString();
		addItemToDiagram(event.getSceneX(), event.getSceneY(), item);
		((ListView<String>) event.getGestureSource()).getItems().remove(item);
		event.setDropCompleted(true);
		event.consume();
	}
	
	void addItemToDiagram(double x, double y, String text) {
		DraggableItem a = new DraggableItem(x, y, text);
		pane.getChildren().add(a);
		itemsInDiagram.add(a);
	}

	// TODO:
	// - Customize colors of dragged items
	// - Categorize dragged items
	// - Detail view for dragged items -> Tooltips?
	// - Implement saving and loading dragged items
	// - Enable CSV file imports -> Rethink entire loading and saving system -> .zip as .venn?
	// - Add right click menus
	// - Responsive design
	// - Import an image
	// - Add tooltips
	// - Quick-action toolbar with icon buttons

	// This method is called by the FXMLLoader when initialization is complete
	@FXML
	void initialize() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				pane.requestFocus();
//				// FIXME: Crashes when using keyboard shortcuts :(
//				String os = System.getProperty("os.name");
//				if (os != null && os.startsWith("Mac"))
//					menuBar.useSystemMenuBarProperty().set(true);
				doTheNew();
				settingsPane.setExpanded(false);
				leftSizeField.setAlignment(Pos.CENTER);
				rightSizeField.setAlignment(Pos.CENTER);
//				frameRect.getChildren().add(new javafx.scene.shape.Rectangle((int)(circleLeft.getBoundsInParent().getMinX()), (int)(circleLeft.getBoundsInParent().getMinY()), (int)(2*circleLeft.getRadius()), (int)(2*circleLeft.getRadius())));
//				frameRect.getChildren().add(new javafx.scene.shape.Rectangle((int)(circleRight.getBoundsInParent().getMinX()), (int)(circleRight.getBoundsInParent().getMinY()), (int)(2*circleRight.getRadius()), (int)(2*circleRight.getRadius())));
			}
		});

		for (Node n : pane.getChildren()) {
			n.setFocusTraversable(false);
		}
		for (Node n : buttonGrid.getChildren()) {
			if (n != addImageButton)
			n.setFocusTraversable(false);
		}
		for (Node n : frameRect.getChildren()) {
			n.setFocusTraversable(false);
		}
		addItemButton.setFocusTraversable(false);
		addItemField.setFocusTraversable(false);
		settingsPane.setFocusTraversable(false);
		for (Node n : settingsBox.getChildren()) {
			n.setFocusTraversable(false);
		}
		leftSizeSlider.setFocusTraversable(false);
		rightSizeSlider.setFocusTraversable(false);
		leftSizeField.setFocusTraversable(false);
		rightSizeField.setFocusTraversable(false);
		
		itemsList.setFocusTraversable(true);
		pane.setFocusTraversable(true);
		
		itemsList.setItems(items);
	}
}
