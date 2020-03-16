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
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.MenuItem;

import javafx.scene.control.Slider;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller {

	private final String DEFAULT_BACKGROUND_COLOR = "0x1d1d1d";
	private final String DEFAULT_TITLE_COLOR = "0xFFFFFF";
	private final String DEFAULT_LEFT_COLOR = "0xf59f9f";
	private final String DEFAULT_RIGHT_COLOR = "0xebe071";
	private final String DEFAULT_INTERSECTION_COLOR = "0xccb26e";
	private final String DEFAULT_LEFT_ITEM_COLOR = "0xffffff";
	private final String DEFAULT_RIGHT_ITEM_COLOR = "0xffffff";
	private final String DEFAULT_INTERSECTION_ITEM_COLOR = "0xffffff";
	private final double DEFAULT_CIRCLE_OPACTIY = 0.6;
	
	private boolean multiSelect = false;
	private boolean changesMade = false;

	@FXML
	private ObservableList<String> items = FXCollections.observableArrayList();
	@FXML
	private ObservableList<DraggableItem> itemsInDiagram = FXCollections.observableArrayList();
	@FXML
	private static ObservableList<DraggableItem> selectedItems = FXCollections.observableArrayList();

	@FXML
	private Circle circleLeft;
	@FXML
	private Circle circleRight;
	private Shape circleIntersection;

	@FXML
	private AnchorPane pane;

	@FXML
	private VBox floatingMenu;

	@FXML
	private ColorPicker colorLeft;
	@FXML
	private ColorPicker colorLeftItems;
	@FXML
	private Slider leftSizeSlider;
	@FXML
	private TextField leftSizeField;
	@FXML
	private ColorPicker colorRight;
	@FXML
	private ColorPicker colorRightItems;
	@FXML
	private Slider rightSizeSlider;
	@FXML
	private TextField rightSizeField;
	@FXML
	private ColorPicker colorIntersection;
	@FXML
	private ColorPicker colorIntersectionItems;

	@FXML
	private MenuBar menuBar;
	@FXML
	private ToolBar toolBar;

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
	private MenuItem deleteItemMenu;
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

	private static File openFile = null;

	class DraggableItem extends StackPane {
		private Label text = new Label();
		private String description;
		private Color color;

		DraggableItem(double x, double y) {
			relocate(x - 5.0D, y - 5.0D);
			getChildren().add(this.text);
			text.setTextFill(Color.WHITE);

			setPadding(new Insets(10));
			setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.NONE, new CornerRadii(1), new BorderWidths(5), new Insets(0))));
			requestFocus();
			this.text.setMaxWidth(85);
			this.text.setWrapText(true);

			this.text.focusedProperty().addListener((observable, hadFocus, hasFocus) -> {
				if (!hasFocus.booleanValue() && getParent() != null && getParent() instanceof Pane
						&& (this.text.getText() == null || this.text.getText().trim().isEmpty())) {
					((Pane) getParent()).getChildren().remove(this);
				}
			});

			this.focusedProperty().addListener((observable, hadFocus, hasFocus) -> {
				try {
					if (!hasFocus.booleanValue()
							&& (this.getScene().getFocusOwner().getClass() != this.getClass() || !multiSelect)) {
						for (DraggableItem d : selectedItems) {
							d.setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.NONE, new CornerRadii(1), new BorderWidths(5), new Insets(0))));						
						}
						selectedItems.clear();
					}
					if (hasFocus.booleanValue()) {
						toFront();
						if (!multiSelect) {
							for (DraggableItem d : selectedItems) {
								d.setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.NONE,
										new CornerRadii(1), new BorderWidths(5), new Insets(0))));
							}
							selectedItems.clear();
						}
						selectedItems.add(this);
						if (selectedItems.size() != 1) {
							deleteItemMenu.setText("Delete Selected Items");
						} else {
							deleteItemMenu.setText("Delete Selected Item");
						}
						setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.SOLID, new CornerRadii(1), new BorderWidths(5), new Insets(0))));
					}
				} catch (Exception e) {
					removeFocus();
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
				keyEvent.consume();
			});

			this.setOnKeyReleased(keyEvent -> {
				if (keyEvent.getCode() == KeyCode.SHORTCUT || keyEvent.getCode() == KeyCode.SHIFT) {
					multiSelect = false;
				}
				keyEvent.consume();
			});

			enableDrag();
			checkBounds();
		}

		public DraggableItem(double d, double e, String text) {
			this(d, e);
			this.text.setText(text);
		}

		public Label getLabel() {
			return this.text;
		}

		public String getText() {
			return this.text.getText();
		}

		public void setColor(Color c) {
			this.text.setTextFill(c);
			this.color = c;
			changesMade();
		}

		public void setDescription(String desc) {
			this.description = desc;
			changesMade();
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

				mouseEvent.consume();
			});
			setOnMouseReleased(mouseEvent -> {
				getScene().setCursor(Cursor.HAND);
				checkBounds();
				mouseEvent.consume();
			});
			setOnMouseDragged(mouseEvent -> {
				double newX = getLayoutX() + mouseEvent.getX() - dragDelta.x;
				double newY = getLayoutY() + mouseEvent.getY() - dragDelta.y;
				setLayoutX(newX);
				setLayoutY(newY);

				if (checkBounds()) { 
					this.setBackground(null);
					this.text.setTextFill(this.color);
					getScene().setCursor(Cursor.CLOSED_HAND);
					setOnMouseReleased(mouseEvent2 -> {
						getScene().setCursor(Cursor.HAND);
						checkBounds();
						mouseEvent.consume();
					});
				} else {

					this.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(5))));

					this.text.setTextFill(Color.WHITE);
					getScene().setCursor(Cursor.DISAPPEAR);
					setOnMouseReleased(mouseEvent2 -> {
						getScene().setCursor(Cursor.DEFAULT);
						frameRect.getChildren().remove(this);
						itemsInDiagram.remove(this);
						itemsList.getItems().add(this.getText());
					});
				}
				mouseEvent.consume();
				changesMade();
			});
			setOnMouseEntered(mouseEvent -> {
				if (!mouseEvent.isPrimaryButtonDown()) {
					getScene().setCursor(Cursor.HAND);
					mouseEvent.consume();
				}
				frameRect.setOnMouseClicked(mouseEvent2 -> {
				});
				pane.setOnMouseClicked(mouseEvent2 -> {
				});
			});
			setOnMouseExited(mouseEvent -> {
				if (!mouseEvent.isPrimaryButtonDown())
					getScene().setCursor(Cursor.DEFAULT);
				mouseEvent.consume();

				frameRect.setOnMouseClicked(mouseEvent2 -> removeFocus());
				pane.setOnMouseClicked(mouseEvent2 -> removeFocus());
			});
		}

		public boolean checkBounds() {
			changesMade();
			Point2D centreLeft = new Point2D((circleLeft.getBoundsInParent().getMinX() + circleLeft.getBoundsInParent().getMaxX())/2, (circleLeft.getBoundsInParent().getMinY() + circleLeft.getBoundsInParent().getMaxY())/2);
			Point2D centreRight = new Point2D((circleRight.getBoundsInParent().getMinX() + circleRight.getBoundsInParent().getMaxX())/2, (circleRight.getBoundsInParent().getMinY() + circleRight.getBoundsInParent().getMaxY())/2);
			Point2D itemLocation = new Point2D((this.getBoundsInParent().getMinX() + this.getBoundsInParent().getMaxX())/2, (this.getBoundsInParent().getMinY() + this.getBoundsInParent().getMaxY())/2);
			double distanceToLeft = itemLocation.distance(centreLeft);
			double distanceToRight = itemLocation.distance(centreRight);
			
			if (distanceToLeft <= circleLeft.getRadius() * circleLeft.getScaleX() && distanceToRight <= circleRight.getRadius() * circleRight.getScaleX()) {
				this.setColor(colorIntersectionItems.getValue());
				this.setBackground(null);
				return true;
			} else if (distanceToLeft <= circleLeft.getRadius() * circleLeft.getScaleX()) {
				this.setColor(colorLeftItems.getValue());
				this.setBackground(null);
				return true;
			} else if (distanceToRight <= circleRight.getRadius() * circleRight.getScaleX()) {
				this.setColor(colorRightItems.getValue());
				this.setBackground(null);
				return true;
			} else {
				this.setColor(Color.WHITE);
				this.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(5))));
				return false;
			}
		}

		private class Delta {
			double x;
			double y;
		}
	}
	
	void changesMade() {
		if (!changesMade && openFile != null)
			Main.primaryStage.setTitle(openFile.getName() + " (Edited) - Venn");
		changesMade = true;
//		redoStack.clear();
	}

	@FXML
	void undo() {
		System.out.println("Undo");
		changesMade();
	}
	
	@FXML
	void redo() {
		System.out.println("Redo");
		changesMade();
	}
	
	@FXML
	void addItemToList() {
		String newItem = addItemField.getText();
		if (!(newItem.equals("") || itemsList.getItems().contains(newItem))) {
			itemsList.getItems().add(newItem);
			addItemField.setText("");
		}
		changesMade();
	}

	@FXML
	void addItemWithEnter(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			addItemToList();
		}
		changesMade();
	}

	@FXML
	void dragFromItemsList() {
		Dragboard dragBoard = itemsList.startDragAndDrop(TransferMode.ANY);
		ClipboardContent content = new ClipboardContent();
		content.putString(itemsList.getSelectionModel().getSelectedItem());
		dragBoard.setContent(content);
		Label tempLabel = new Label(dragBoard.getString());
		pane.getChildren().add(tempLabel);
		tempLabel.setLayoutX(35);
		tempLabel.setLayoutY(35);
		tempLabel.setMaxWidth(105);
		tempLabel.setWrapText(true);
		tempLabel.setPadding(new Insets(10));
		tempLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
		tempLabel.setTextFill(Color.BLACK);
		dragBoard.setDragView(tempLabel.snapshot(null, null));
		dragBoard.setDragViewOffsetX(tempLabel.getWidth()/2);
		dragBoard.setDragViewOffsetY(-tempLabel.getHeight()/2);
		pane.getChildren().remove(tempLabel);
		changesMade();
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
				frameRect.getChildren().remove(d);
			}
			itemsInDiagram.removeAll(selectedItems);
			selectedItems.clear();
		}
		changesMade();
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

	@FXML
	void dragDroppedOnItemsList(DragEvent event) {
		event.setDropCompleted(true);
		event.consume();
		changesMade();
	}
		 
	@FXML
	void takeScreenshot() {
		boolean hadChanges = changesMade;
		removeFocus();
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
			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
			fc.setTitle("Save");
			fc.setInitialFileName(name);
			File selectedFile = fc.showSaveDialog(pane.getScene().getWindow());
			floatingMenu.setLayoutX(pane.getScene().getWindow().getX() - floatingMenu.getWidth() - 10);

			Bounds bounds = frameRect.getBoundsInLocal();
			Bounds screenBounds = frameRect.localToScreen(bounds);
			int x = (int) screenBounds.getMinX() - 15;
			int y = (int) screenBounds.getMinY() - 15;
			int width = (int) screenBounds.getWidth() + 30;
			int height = (int) screenBounds.getHeight() + 30;
			Rectangle screenRect = new Rectangle(x, y, width, height);
			BufferedImage capture = new Robot().createScreenCapture(screenRect);
			ImageIO.write(capture, "png", selectedFile);
		} catch (IllegalArgumentException e) {
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
		changesMade = hadChanges;
	}

	@FXML
	void clearDiagram() {
		for (DraggableItem d : itemsInDiagram) {
			itemsList.getItems().add(d.getText());
			frameRect.getChildren().remove(d);
		}
		itemsInDiagram.clear();
		changesMade();
	}

	void doTheSave(File selectedFile) {
		
		// TODO: Add imported images to this once implemented

		// Hierarchy of a .venn file:
		// . Diagram.venn:
		// ... Config.vlist:
		// ..... (0) Title, (1) Titles color, (2) Background color
		// ..... (0) Left circle title, (1) Left circle color, (2) Left circle scale, (3) Item text color
		// ..... (0) Right circle title, (1) Right circle color, (2) Right circle scale, (3) Item text color
		// ..... (0) Intersection color, (1) Intersection item text color
		// ... Unassigned.csv:
		// ..... Unassigned items separated by new lines
		// ... InDiagram.vlist
		// ..... (0) Item text, (1) item x, (2) item y, (3) item description, (4) text color
		
		// Encryption?
		// String test = "abcdefg";
		// StringBuilder sb = new StringBuilder(test);
		// for (int i = 0; i < test.length(); i++) {
		//     sb.setCharAt(i, (char)((((sb.charAt(i) + 7) * 12) - 6) * 21));
		// }
		// test = sb.toString();
		// System.out.println(test);
		// sb = new StringBuilder(test);
		// for (int i = 0; i < test.length(); i++) {
		//     sb.setCharAt(i, (char)((((sb.charAt(i) / 21) + 6) / 12) - 7));
		// }
		// test = sb.toString();
		// System.out.println(test);


		try {
			FileOutputStream fos = new FileOutputStream(selectedFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			File config = new File("Config.vlist");
			StringBuilder sb = new StringBuilder();
			sb.append(title.getText() + "𔓱" + colorTitles.getValue().toString() + "𔓱" + colorBackground.getValue().toString() + "\n");
			sb.append(circleLeftTitle.getText() + "𔓱" + colorLeft.getValue().toString() + "𔓱" + circleLeft.getScaleX() + "𔓱" + colorLeftItems.getValue().toString() + "\n");
			sb.append(circleRightTitle.getText() + "𔓱" + colorRight.getValue().toString() + "𔓱" + circleRight.getScaleX() + "𔓱" + colorRightItems.getValue().toString() + "\n");
			sb.append(colorIntersection.getValue().toString() + "𔓱" + colorIntersectionItems.getValue().toString());
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
				sb.append(d.getText() + "𔓱" + d.getLayoutX() + "𔓱" + d.getLayoutY() + "𔓱" + "item description" + "𔓱" + d.getLabel().getTextFill().toString());
				if (i != itemsInDiagram.size() - 1) {
					sb.append("\n");
				}
			}
			bw = new BufferedWriter(new FileWriter(inDiagram));
			bw.write(sb.toString());
			bw.close();

			File[] files = { config, unassigned, inDiagram };
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
			Main.primaryStage.setTitle(selectedFile.getName() + " - Venn");
			changesMade = false;
		} catch (NullPointerException e) {
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

			if (selectedFile != null) {
				if (!(selectedFile.getName().length() > 5 && selectedFile.getName()
						.substring(selectedFile.getName().length() - 5).toLowerCase().equals(".venn"))) {
					selectedFile.renameTo(new File(selectedFile.getAbsolutePath() + ".venn"));
				}
				doTheSave(selectedFile);
			}
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

	ObservableList<String> doTheCSV(File csv) throws Exception {
		ObservableList<String> list = FXCollections.observableArrayList();
		String line;
		BufferedReader br = new BufferedReader(new FileReader(csv));
		while ((line = br.readLine()) != null) {
			if (line.contains(",")) {
				line = line.substring(1, line.length() - 1);
			}
			list.add(line);
		}
		br.close();
		changesMade();
		return list;
	}

	void doTheLoad() {

		// TODO: Add imported images to this once implemented

		// Hierarchy of a .venn file:
		// . Diagram.venn:
		// ... Config.vlist:
		// ..... (0) Title, (1) Titles color, (2) Background color
		// ..... (0) Left circle title, (1) Left circle color, (2) Left circle scale, (3) Item text color
		// ..... (0) Right circle title, (1) Right circle color, (2) Right circle scale, (3) Item text color
		// ..... (0) Intersection color, (1) Intersection item text color
		// ... Unassigned.csv:
		// ..... Unassigned items separated by new lines
		// ... InDiagram.vlist
		// ..... (0) Item text, (1) item x, (2) item y, (3) item description, (4) text color

		File file = null;
		try {
			FileChooser fc = new FileChooser();
			List<String> extensions = new ArrayList<String>();
			extensions.add("*.venn");

			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Venn files (*.venn)", "*.venn"));
			file = fc.showOpenDialog(pane.getScene().getWindow());
			String line, title, leftTitle, rightTitle, elements[];
			Color bgColor, leftColor, rightColor, intersectionColor, titleColor, leftTextColor, rightTextColor, intersectionTextColor;
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
			leftTextColor = Color.web(elements[3]);

			elements = br.readLine().split("𔓱");
			rightTitle = elements[0];
			rightColor = Color.web(elements[1]);
			rightScale = Double.parseDouble(elements[2]);
			rightTextColor = Color.web(elements[3]);

			elements = br.readLine().split("𔓱");
			intersectionColor = Color.web(elements[0]);
			intersectionTextColor = Color.web(elements[1]);
			br.close();

			// Unassigned
			ze = vennFile.getEntry("Unassigned.csv");
			br = new BufferedReader(new InputStreamReader(vennFile.getInputStream(ze)));
			while ((line = br.readLine()) != null) {
				if (line.contains(",")) {
					line = line.substring(1, line.length() - 1);
				}
				unassignedItems.add(line);

			}
			br.close();

			// InDiagram
			ze = vennFile.getEntry("InDiagram.vlist");
			br = new BufferedReader(new InputStreamReader(vennFile.getInputStream(ze)));
			while ((line = br.readLine()) != null) {
				elements = line.split("𔓱");
				DraggableItem a = new DraggableItem(Double.parseDouble(elements[1]), Double.parseDouble(elements[2]),
						elements[0]);
				a.setDescription(elements[3]);
				a.setColor(Color.web(elements[4]));
				inDiagram.add(a);
			}
			br.close();
			vennFile.close();

			this.title.setText(title);
			this.circleLeftTitle.setText(leftTitle);
			this.circleRightTitle.setText(rightTitle);
			this.colorBackground.setValue(bgColor);
			this.changeColorBackground();
			this.colorLeft.setValue(leftColor);
			this.changeColorLeft();
			this.colorRight.setValue(rightColor);
			this.changeColorRight();
			this.colorIntersection.setValue(intersectionColor);
			this.updateIntersection();
			this.colorTitles.setValue(titleColor);
			this.changeColorTitles();
			this.leftSizeSlider.setValue(leftScale * 100);
			this.changeSizeLeft();
			this.rightSizeSlider.setValue(rightScale * 100);
			this.changeSizeRight();
			this.itemsList.getItems().clear();
			this.itemsList.getItems().addAll(unassignedItems);
			this.frameRect.getChildren().removeAll(this.itemsInDiagram);
			this.frameRect.getChildren().addAll(inDiagram);
			this.itemsInDiagram.clear();
			this.itemsInDiagram.addAll(inDiagram);
			this.colorLeftItems.setValue(leftTextColor);
			this.colorRightItems.setValue(rightTextColor);
			this.colorIntersectionItems.setValue(intersectionTextColor);

			openFile = file;
			changesMade = false;
			// FIXME: Crashes the JUnit tests because they don't have a title bar on the window to change
			Main.primaryStage.setTitle(openFile.getName() + " - Venn");
		} catch (Exception e) {
			if (file != null) {
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
	}

	@FXML
	void loadFromFile() {
		if (changesMade) {
			a.setAlertType(AlertType.CONFIRMATION);
			a.setHeaderText("Are you sure you want to open another file?");
			a.setContentText("You will lose any unsaved changes");
			a.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
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
		if (changesMade) {
			a.setAlertType(AlertType.CONFIRMATION);
			a.setHeaderText("Are you sure you want to open a new file?");
			a.setContentText("You will lose any unsaved changes");
			Optional<ButtonType> result = a.showAndWait();
	
			if (result.get() == ButtonType.OK) {
				doTheNew();
			}
		} else {
			doTheNew();
		}
	}

	@FXML
	void doTheNew() {
		title.setText("");
		circleLeftTitle.setText("");
		circleRightTitle.setText("");
		itemsList.getItems().clear();
		circleLeft.setFill(Color.web(DEFAULT_LEFT_COLOR));
		circleLeft.setOpacity(DEFAULT_CIRCLE_OPACTIY);
		circleRight.setFill(Color.web(DEFAULT_RIGHT_COLOR));
		circleRight.setOpacity(DEFAULT_CIRCLE_OPACTIY);
		colorLeft.setValue(Color.web(DEFAULT_LEFT_COLOR));
		colorRight.setValue(Color.web(DEFAULT_RIGHT_COLOR));
		colorIntersection.setValue(Color.web(DEFAULT_INTERSECTION_COLOR));
		colorTitles.setValue(Color.web(DEFAULT_TITLE_COLOR));
		colorBackground.setValue(Color.web(DEFAULT_BACKGROUND_COLOR));
		changeColorBackground();
		leftSizeSlider.setValue(100);
		changeSizeLeft();
		rightSizeSlider.setValue(100);
		changeSizeRight();

		frameRect.getChildren().removeAll(itemsInDiagram);
		itemsInDiagram.clear();
		colorLeftItems.setValue(Color.web(DEFAULT_LEFT_ITEM_COLOR));
		colorRightItems.setValue(Color.web(DEFAULT_RIGHT_ITEM_COLOR));
		colorIntersectionItems.setValue(Color.web(DEFAULT_INTERSECTION_ITEM_COLOR));
		changeColorItems();

		openFile = null;
		// FIXME: Crashes the JUnit tests because they don't have a title bar on the window to change

		Main.primaryStage.setTitle("Venn");
		changesMade = false;
	}

	@FXML
	void openManual() {
		try {
			// TODO: Change link to online manual in future release
			java.awt.Desktop.getDesktop().browse(new URI("https://github.com/nourinjh/EECS2311/"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	void changeColorLeft() {
		circleLeft.setFill(colorLeft.getValue());
		circleLeft.setOpacity(DEFAULT_CIRCLE_OPACTIY);
		changesMade();
	}

	@FXML
	void changeColorItems() {
		for (DraggableItem d : itemsInDiagram) {
			d.checkBounds();
		}
		changesMade();
	}

	@FXML
	void changeColorRight() {
		circleRight.setFill(colorRight.getValue());
		circleRight.setOpacity(DEFAULT_CIRCLE_OPACTIY);
		changesMade();
	}

	@FXML
	void changeColorBackground() {
		pane.setStyle("-fx-background-color: #"
				+ colorBackground.getValue().toString().substring(2, colorBackground.getValue().toString().length() - 2)
				+ ";");
		changeColorTitles();
		changesMade();
	}

	@FXML
	void changeColorTitles() {
		title.setStyle("-fx-background-color: transparent;\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2)
				+ ";");
		circleLeftTitle.setStyle("-fx-background-color: transparent;\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2)
				+ ";");
		circleRightTitle.setStyle("-fx-background-color: transparent;\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2)
				+ ";");
		changesMade();
	}

	@FXML
	void changeSizeLeft() {
		circleLeft.setScaleX(leftSizeSlider.getValue() / 100.0);
		circleLeft.setScaleY(leftSizeSlider.getValue() / 100.0);
		leftSizeField.setText(String.format("%.0f", leftSizeSlider.getValue()));
		updateIntersection();
		changeColorItems();
		changesMade();
	}
	
	@FXML
	void updateIntersection() {
//		pane.getChildren().remove(circleIntersection);
//		circleIntersection = Shape.intersect(circleLeft, circleRight);
//		circleIntersection.setFill(colorIntersection.getValue());
//		circleIntersection.setOnDragDropped(event -> {
//			dropItem(event);
//		});
//		circleIntersection.mouseTransparentProperty().set(true);
//		pane.getChildren().add(circleIntersection);
//		changeColorItems();
//		changesMade();
	}

	@FXML
	void changeSizeLeftField(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			double size;
			try {
				size = Double.parseDouble(leftSizeField.getText());
				if (size < 50) {
					size = 50;
					leftSizeField.setText("50");
				}
				if (size > 120) {
					size = 120;
					leftSizeField.setText("120");
				}
				circleLeft.setScaleX(size / 100.0);
				circleLeft.setScaleY(size / 100.0);
				leftSizeSlider.setValue(size);
			} catch (Exception e) {
				leftSizeField.setText(String.format("%.0f", leftSizeSlider.getValue()));
			}
			updateIntersection();
			changeColorItems();
			changesMade();
		}
	}

	@FXML
	void changeSizeRight() {
		circleRight.setScaleX(rightSizeSlider.getValue() / 100.0);
		circleRight.setScaleY(rightSizeSlider.getValue() / 100.0);
		rightSizeField.setText(String.format("%.0f", rightSizeSlider.getValue()));
		updateIntersection();
		changeColorItems();
		changesMade();
	}

	@FXML
	void changeSizeRightField(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			double size;
			try {
				size = Double.parseDouble(rightSizeField.getText());
				if (size < 50) {
					size = 50;
					rightSizeField.setText("50");
				}
				if (size > 120) {
					size = 120;
					rightSizeField.setText("120");
				}
				circleRight.setScaleX(size / 100.0);
				circleRight.setScaleY(size / 100.0);
				rightSizeSlider.setValue(size);
			} catch (Exception e) {
				rightSizeField.setText(String.format("%.0f", rightSizeSlider.getValue()));
			}
			updateIntersection();
			changeColorItems();
			changesMade();
		}
	}
	
	@FXML
	void importCSV() {
		importFile("csv");
	}
	@FXML
	void importImage() {
		importFile("img");
	}
	@FXML
	void importAnswer() {
		importFile("ans");
	}

	void importFile(String type) {
		// TODO: Set up as SplitMenuButton and split into different methods
		// One for each import type, plus a generic catch-all (fc.setSelectedExtensionFilter(filter)?)
		try {
			FileChooser fc = new FileChooser();
			List<String> extensions = new ArrayList<String>();
			extensions.add("*.png");
			extensions.add("*.jpg");
			extensions.add("*.jpeg");

			ExtensionFilter csvFilter = new ExtensionFilter("CSV files (*.csv)", "*.csv");
			ExtensionFilter ansFilter = new ExtensionFilter("Answer key files (*.venn)", "*.venn");
			ExtensionFilter imgFilter = new ExtensionFilter("Image files (*.png, *.jpg, *.jpeg)", extensions);
			fc.getExtensionFilters().add(csvFilter);
			fc.getExtensionFilters().add(imgFilter);
			fc.getExtensionFilters().add(ansFilter);
			
			if (type.equals("csv")) {
				fc.setSelectedExtensionFilter(csvFilter);
			} else if (type.equals("img")) {
				fc.setSelectedExtensionFilter(imgFilter);
			} else /*if (type.equals("ans"))*/ {
				fc.setSelectedExtensionFilter(ansFilter);
			}

			File file = fc.showOpenDialog(pane.getScene().getWindow());
			if (fc.getSelectedExtensionFilter().equals(csvFilter)) {
				itemsList.getItems().addAll(doTheCSV(file));
			} else if (fc.getSelectedExtensionFilter().equals(imgFilter)) {
				System.out.println("Image: " + file.getAbsolutePath());
				a.setAlertType(AlertType.INFORMATION);
				a.setHeaderText("Feature coming soon");
				a.setContentText("Adding images is not yet available. This feature will be coming in a future release.");
				a.setTitle("Feature not available");
				a.show();
			} else if (fc.getSelectedExtensionFilter().equals(ansFilter)) {
				// Compare current file to answers
				// Change backgrounds of items to bright red or green for wrong or right
				System.out.println("Answer file: " + file.getAbsolutePath());
				a.setAlertType(AlertType.INFORMATION);
				a.setHeaderText("Feature coming soon");
				a.setContentText("Comparing answers is not yet available. This feature will be coming in a future release.");
				a.setTitle("Feature not available");
				a.show();
			}
		} catch (NullPointerException e) {
		} catch (Exception e) {
			System.out.println("Error: File not opened.");
			System.out.println(e);
			a.setAlertType(AlertType.ERROR);
			a.setHeaderText("File could not be opened");
			a.setContentText("");
			a.setTitle("Error");
			a.show();
		}
		changesMade();
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

	@FXML
	void dropItem(DragEvent event) {
		String item = event.getDragboard().getString();
		addItemToDiagram(event.getX(), event.getY(), item);
		itemsList.getItems().remove(item);
		event.setDropCompleted(true);
		event.consume();
		changesMade();
	}

	void addItemToDiagram(double x, double y, String text) {
		DraggableItem a = new DraggableItem(x, y, text);
		frameRect.getChildren().add(a);
		itemsInDiagram.add(a);
		a.toFront();
		changesMade();
	}
	

	@FXML
	void selectAll() {
		for (DraggableItem d : itemsInDiagram) {
			multiSelect = true;
			d.requestFocus();
			selectedItems.add(d);
		}
		multiSelect = false;
	}
	
	@FXML
	void showAboutWindow() {
		Alert a = new Alert(AlertType.NONE);
		a.setTitle("About Venn");
		a.setHeaderText("Venn 3.0");
		StringBuilder sb = new StringBuilder("");
		sb.append("Credits\n\n");
		sb.append("Lead Developer: Andrew Hocking\n");
		sb.append("Assistant Developer: Nourin Abd El Hadi\n");
		sb.append("Documentation: Nabi Khalid and Anika Prova\n");
		sb.append("Toolbar icon images: ThoseIcons on FlatIcon.com");
		a.setContentText(sb.toString());
		ButtonType linkButton = new ButtonType("View ThoseIcons");
		a.getButtonTypes().add(linkButton);
		ImageView iconView = new ImageView(new Image(getClass().getResource("images/icon50.png").toExternalForm()));
		a.setGraphic(iconView);
		Optional<ButtonType> result = a.showAndWait();
		try {
			if (result.get() == linkButton) {
				try {
					java.awt.Desktop.getDesktop().browse(new URI("https://www.flaticon.com/authors/those-icons"));
				} catch (Exception e) {
					e.printStackTrace();
				}			
			} else {
				a.close();
			}
		} catch (Exception e) {
			a.close();
		}
	}


	// This method is called by the FXMLLoader when initialization is complete
	@FXML
	void initialize() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				pane.requestFocus();
//				// FIXME: Keyboard shortcuts run twice when this is used...? Also toolbar sits too low
//				String os = System.getProperty("os.name");
//				if (os != null && os.startsWith("Mac")) {
//					menuBar.useSystemMenuBarProperty().set(true);
//					toolBar.setLayoutY(0);
//				}
				doTheNew();
				leftSizeField.setAlignment(Pos.CENTER);
				rightSizeField.setAlignment(Pos.CENTER);
				frameRect.setOnMouseReleased(mouseEvent -> {
					frameRect.getScene().setCursor(Cursor.DEFAULT);
					mouseEvent.consume();
				});
			}
		});
		title.textProperty().addListener(changed -> {
			changesMade();
		});
		circleLeftTitle.textProperty().addListener(changed -> {
			changesMade();
		});
		circleRightTitle.textProperty().addListener(changed -> {
			changesMade();
		});
		
		Main.primaryStage.setOnCloseRequest(event -> {
			if (changesMade) {
				a.setAlertType(AlertType.CONFIRMATION);
				ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.YES);
				ButtonType dontSaveButton = new ButtonType("Don't Save", ButtonBar.ButtonData.NO);
				ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
				a.setTitle("Save changes?");
				if (openFile == null)
					a.setHeaderText("Do you want to save your changes?");
				else
					a.setHeaderText("Do you want to save your changes to \"" + openFile.getName() + "\"?");
				a.setContentText(null);
				a.getButtonTypes().setAll(saveButton, dontSaveButton, cancelButton);
//				a.setContentText("Would you like to save your changes before quitting?");
				Optional<ButtonType> result = a.showAndWait();
				if (result.get().equals(dontSaveButton)) {
					event.consume();
					Main.primaryStage.close();
				} else if (result.get().equals(saveButton)){
					try {
						save();
						if (openFile == null) throw new Exception();
						event.consume();
						Main.primaryStage.close();
					} catch (Exception e) {					
						event.consume();
					}
				} else {
					event.consume();
				}
				a.getButtonTypes().removeAll(saveButton, dontSaveButton, cancelButton);
			} else {
				event.consume();
				Main.primaryStage.close();
			}
		});

		for (Node n : pane.getChildren()) {
			n.setFocusTraversable(false);
		}
		for (Node n : frameRect.getChildren()) {
			n.setFocusTraversable(false);
		}
		addItemButton.setFocusTraversable(false);
		addItemField.setFocusTraversable(false);
		leftSizeSlider.setFocusTraversable(false);
		rightSizeSlider.setFocusTraversable(false);
		leftSizeField.setFocusTraversable(false);
		rightSizeField.setFocusTraversable(false);
		itemsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		itemsList.setFocusTraversable(true);
		pane.setFocusTraversable(true);
		itemsList.setItems(items);
	}
}
