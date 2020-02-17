package application;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.sun.javafx.robot.FXRobot;
import com.sun.javafx.robot.FXRobotFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;

public class Controller {

	private static final double DEFAULT_CIRCLE_OPACTIY = 0.6;
	private static boolean settingsIsOpen = false;

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
	private VBox floatingMenu;

	@FXML
	private TitledPane settingsPane;
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
		if (event.getCode() == KeyCode.ENTER) {
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
		event.acceptTransferModes(TransferMode.MOVE);
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
		if (!circleRightItemsList.getItems().contains(item)) {
			circleRightItemsList.getItems().add(item);
			((ListView<String>) event.getGestureSource()).getItems().remove(item);
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
		if (!circleLeftItemsList.getItems().contains(item)) {
			circleLeftItemsList.getItems().add(item);
			((ListView<String>) event.getGestureSource()).getItems().remove(item);
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
		if (!bothItemsList.getItems().contains(item)) {
			bothItemsList.getItems().add(item);
			((ListView<String>) event.getGestureSource()).getItems().remove(item);
		}
		event.setDropCompleted(true);
		event.consume();
	}

	@FXML
	void takeScreenshot() {
		String mainTitle = title.getText() + "";
		String leftTitle = circleLeftTitle.getText() + "";
		String rightTitle = circleRightTitle.getText() + "";
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
			a.setHeaderText("File could not be opened");
			a.setContentText("");
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
			fileTitle = selectedFile.getName();

			// Order of items in file:
			// 0) Title
			// 1) Left circle title
			// 2) Left circle colour
			// 3) Right circle title
			// 4) Right circle colour
			// 5) Unassigned items list
			// 6) Left circle items list
			// 7) Centre items list
			// 8) Right circle items list
			// 9) Left circle scale
			// 10) Right circle scale
			
			String fileContent = title.getText() + "𔓱𔓱" + circleLeftTitle.getText() + "𔓱𔓱"
					+ circleLeft.getFill().toString() + "𔓱𔓱" + circleRightTitle.getText() + "𔓱𔓱"
					+ circleRight.getFill().toString() + "𔓱𔓱";
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
//			fileContent = fileContent.substring(0, fileContent.length() - 2);
			fileContent += "𔓱" + circleLeft.getScaleX() + "𔓱𔓱" + circleRight.getScaleY();

			BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile));
			writer.write(fileContent);
			writer.close();
		} catch (Exception e) {
			System.out.println("Error: File not saved.");
			System.out.println(e);
			a.setAlertType(AlertType.ERROR);
			a.setHeaderText("File could not be saved");
			a.setContentText("");
			a.show();
		}
	}

	void doTheLoad() {
		// Order of items in file:
		// 0) Title
		// 1) Left circle title
		// 2) Left circle colour
		// 3) Right circle title
		// 4) Right circle colour
		// 5) Unassigned items list
		// 6) Left circle items list
		// 7) Centre items list
		// 8) Right circle items list
		// 9) Left circle scale
		// 10) Right circle scale
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
			circleLeft.setFill(Color.web(elements.get(2)));
			circleLeft.setOpacity(DEFAULT_CIRCLE_OPACTIY);
			circleRightTitle.setText(elements.get(3));
			circleRight.setOpacity(DEFAULT_CIRCLE_OPACTIY);
			circleRight.setFill(Color.web(elements.get(4)));
			circleLeft.setScaleX(Double.parseDouble(elements.get(9)));
			circleLeft.setScaleY(Double.parseDouble(elements.get(9)));
			circleRight.setScaleX(Double.parseDouble(elements.get(10)));
			circleRight.setScaleY(Double.parseDouble(elements.get(10)));
			itemsList.getItems().clear();
			for (String s : elements.get(5).split("𔓱")) {
				itemsList.getItems().add(s);
			}
			circleLeftItemsList.getItems().clear();
			for (String s : elements.get(6).split("𔓱")) {
				circleLeftItemsList.getItems().add(s);
			}
			bothItemsList.getItems().clear();
			for (String s : elements.get(7).split("𔓱")) {
				bothItemsList.getItems().add(s);
			}
			circleRightItemsList.getItems().clear();
			for (String s : elements.get(8).split("𔓱")) {
				circleRightItemsList.getItems().add(s);
			}
		} catch (Exception e) {
			System.out.println("Error: File not opened.");
			System.out.println(e);
			a.setAlertType(AlertType.ERROR);
			a.setHeaderText("File could not be opened");
			a.setContentText("");
			a.setTitle("Error");
			a.show();
		}
	}

	@FXML
	void loadFromFile() {
		if (!title.getText().equals("") || !circleLeftTitle.getText().equals("")
				|| !circleRightTitle.getText().equals("") || !itemsList.getItems().isEmpty()
				|| !circleLeftItemsList.getItems().isEmpty() || !bothItemsList.getItems().isEmpty()
				|| !circleRightItemsList.getItems().isEmpty()
				|| !circleLeft.getFill().toString().contentEquals(DEFAULT_LEFT_COLOUR)
				|| !circleRight.getFill().toString().contentEquals(DEFAULT_RIGHT_COLOUR)) {
			a.setAlertType(AlertType.CONFIRMATION);
			a.setHeaderText("Are you sure you want to open another file?");
			a.setContentText("You will lose any unsaved changes");
			Optional<ButtonType> result = a.showAndWait();

			if (result.get() == ButtonType.OK) {
				doTheLoad();
			}
		} else {
			doTheLoad();
		}
	}

	@FXML
	void newDiagram() {
		if (!title.getText().equals("") || !circleLeftTitle.getText().equals("")
				|| !circleRightTitle.getText().equals("") || !itemsList.getItems().isEmpty()
				|| !circleLeftItemsList.getItems().isEmpty() || !bothItemsList.getItems().isEmpty()
				|| !circleRightItemsList.getItems().isEmpty()
				|| !circleLeft.getFill().toString().contentEquals(DEFAULT_LEFT_COLOUR)
				|| !circleRight.getFill().toString().contentEquals(DEFAULT_RIGHT_COLOUR)
				|| !(circleLeft.getScaleX() != 1) || !(circleRight.getScaleX() != 1)) {
			a.setAlertType(AlertType.CONFIRMATION);
			a.setHeaderText("Are you sure you want to open a new file?");
			a.setContentText("You will lose any unsaved changes");
			Optional<ButtonType> result = a.showAndWait();

			if (result.get() == ButtonType.OK) {
				title.setText("");
				circleLeftTitle.setText("");
				circleRightTitle.setText("");
				itemsList.getItems().clear();
				circleLeftItemsList.getItems().clear();
				circleRightItemsList.getItems().clear();
				bothItemsList.getItems().clear();
				circleLeft.setFill(Color.web(DEFAULT_LEFT_COLOUR));
				circleLeft.setOpacity(DEFAULT_CIRCLE_OPACTIY);
				circleRight.setFill(Color.web(DEFAULT_RIGHT_COLOUR));
				circleRight.setOpacity(DEFAULT_CIRCLE_OPACTIY);
				colorLeft.setValue(Color.web(DEFAULT_LEFT_COLOUR));
				colorRight.setValue(Color.web(DEFAULT_RIGHT_COLOUR));
				circleLeft.setScaleX(1);
				circleLeft.setScaleY(1);
				circleRight.setScaleX(1);
				circleRight.setScaleY(1);
				fileTitle = null;
			}
		}
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
	void changeColourLeft() {
		circleLeft.setFill(colorLeft.getValue());
		circleLeft.setOpacity(DEFAULT_CIRCLE_OPACTIY);
	}

	@FXML
	void changeColourRight() {
		circleRight.setFill(colorRight.getValue());
		circleRight.setOpacity(DEFAULT_CIRCLE_OPACTIY);
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
		try {
			FileChooser fc = new FileChooser();
			List<String> extensions = new ArrayList<String>();
			extensions.add("*.png");
			extensions.add("*.jpg");
			extensions.add("*.jpeg");
			fc.getExtensionFilters()
					.add(new FileChooser.ExtensionFilter("Image files (*.png, *.jpg, *.jpeg)", extensions));
			File file = fc.showOpenDialog(pane.getScene().getWindow());
			System.out.println(file.getAbsolutePath());
		} catch (Exception e) {
			System.out.println("Error: File not opened.");
			System.out.println(e);
			a.setAlertType(AlertType.ERROR);
			a.setHeaderText("File could not be opened");
			a.setContentText("");
			a.setTitle("Error");
			a.show();
		}
	}

	@FXML
	void expandSettings() {
		if (settingsPane.isExpanded() && !settingsIsOpen) {
			settingsIsOpen = true;
			floatingMenu.setLayoutY(floatingMenu.getLayoutY() - (settingsPane.getHeight() / 2));
		} else if (!settingsPane.isExpanded()){
			settingsIsOpen = false;
			floatingMenu.setLayoutY(floatingMenu.getLayoutY() + (settingsPane.getHeight() / 2));
		}
	}

	/*
	 * TODO: - Add right click menus - Responsive design - Import an image - Drag
	 * items wherever
	 */

	// This method is called by the FXMLLoader when initialization is complete
	@FXML
	void initialize() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				pane.requestFocus();
//				String os = System.getProperty("os.name");
//				if (os != null && os.startsWith("Mac"))
//					menuBar.useSystemMenuBarProperty().set(true);
				colorLeft.setValue((Color) (circleLeft.getFill()));
				colorRight.setValue((Color) (circleRight.getFill()));
				circleLeft.setFill(Color.web(DEFAULT_LEFT_COLOUR));
				circleLeft.setOpacity(DEFAULT_CIRCLE_OPACTIY);
				circleRight.setFill(Color.web(DEFAULT_RIGHT_COLOUR));
				circleRight.setOpacity(DEFAULT_CIRCLE_OPACTIY);
				settingsPane.setExpanded(false);
				leftSizeField.setAlignment(Pos.CENTER);
				rightSizeField.setAlignment(Pos.CENTER);
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
