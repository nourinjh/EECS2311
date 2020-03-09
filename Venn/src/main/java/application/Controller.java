// To export as a runnable JAR file:
// 1. Right click on "application" package
// 2. Click "Export"
// 3. Select "Runnable JAR file"
// 4. Select "Extract required libraries into generated JAR"
// 5. Click finish
// 6. Pray

/**
 * 
 * @author Andrew Hocking, Nourin Abd El Hadi 
 * 
 */

package application;

import java.awt.Rectangle;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Stack;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.MenuItem;

import javafx.scene.control.Slider;
import javafx.scene.control.SplitMenuButton;
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
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller {

	private final String DEFAULT_BACKGROUND_COLOR = "0x1d1d1d";
	private final String DEFAULT_TITLE_COLOR = "0xFFFFFF";
	private final double DEFAULT_CIRCLE_OPACTIY = 0.6;
	private boolean multiSelect = false;

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
	private final String DEFAULT_LEFT_COLOR = "0xf59f9f";
	@FXML
	private ColorPicker colorLeftItems;
	private final String DEFAULT_LEFT_ITEM_COLOR = "0xffffff";
	@FXML
	private Slider leftSizeSlider;
	@FXML
	private TextField leftSizeField;
	@FXML
	private ColorPicker colorRight;
	private final String DEFAULT_RIGHT_COLOR = "0xebe071";
	@FXML
	private ColorPicker colorRightItems;
	private final String DEFAULT_RIGHT_ITEM_COLOR = "0xffffff";
	@FXML
	private Slider rightSizeSlider;
	@FXML
	private TextField rightSizeField;
	@FXML
	private ColorPicker colorBothItems;
	private final String DEFAULT_BOTH_ITEM_COLOR = "0xffffff";

	@FXML
	private MenuBar menuBar;

	@FXML
	private Button importButton;

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
	private Button makeDummyItemButton;

	@FXML // ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;

	@FXML // URL location of the FXML file that was given to the FXMLLoader
	private URL location;

	@FXML
	Alert a = new Alert(AlertType.NONE);

	private File openFile = null;

	private enum InCircle {
		LEFT, RIGHT, BOTH, NONE;
	}

	static Color leftColor;
	static Color rightColor;
	static Color bothColor;
	static Color noneColor;

	class DraggableItem extends StackPane {
		private Label text = new Label();
		private String description;
		private Color color;
		private InCircle circle;

		DraggableItem(double x, double y) {
			relocate(x - 5.0D, y - 5.0D);
			getChildren().add(this.text);
			text.setTextFill(Color.WHITE);

			setPadding(new Insets(10));
			setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.NONE, new CornerRadii(1),
					new BorderWidths(5), new Insets(2))));
			requestFocus();
			circle = InCircle.NONE;
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
							d.setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.NONE,
									new CornerRadii(1), new BorderWidths(5), new Insets(2))));

						}
						selectedItems.clear();
					}
					if (hasFocus.booleanValue()) {
						if (!multiSelect) {
							for (DraggableItem d : selectedItems) {
								d.setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.NONE,
										new CornerRadii(1), new BorderWidths(5), new Insets(2))));
							}
							selectedItems.clear();
						}
						selectedItems.add(this);
						setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.SOLID,
								new CornerRadii(1), new BorderWidths(5), new Insets(2))));
					}
				} catch (Exception e) {
					removeFocus();
				}
			});

//			this.translateXProperty().addListener((observable, oldValue, newValue) -> {
//				setLayoutX((double)oldValue + (double)newValue);
//			});
//			this.translateYProperty().addListener((observable, oldValue, newValue) -> {
//				setLayoutY((double)oldValue + (double)newValue);
//			});

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
		}

		public Label getLabel() {
			return this.text;
		}

		public InCircle getCircle() {
			return this.circle;
		}

		public String getText() {
			return this.text.getText();
		}

		public void setColor(Color c) {
			this.text.setTextFill(c);
			this.color = c;
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

				if ((newX > circleLeft.getBoundsInParent().getMinX() - (getWidth() / 2)
						&& newX < circleRight.getBoundsInParent().getMaxX() - (getWidth() / 2))
						&& (newY > circleLeft.getBoundsInParent().getMinY() - (getHeight() / 2)
								&& newY < circleRight.getBoundsInParent().getMaxY() - (getHeight() / 2))) {
					this.setBackground(null);

					this.text.setTextFill(this.color);
					getScene().setCursor(Cursor.CLOSED_HAND);
					setOnMouseReleased(mouseEvent2 -> {
						getScene().setCursor(Cursor.HAND);
						checkBounds();
						mouseEvent.consume();
					});
				} else {

					this.setBackground(
							new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(5))));

					this.text.setTextFill(Color.WHITE);
					getScene().setCursor(Cursor.DISAPPEAR);
					setOnMouseReleased(mouseEvent2 -> {
						getScene().setCursor(Cursor.DEFAULT);
						frameRect.getChildren().remove(this);
						itemsInDiagram.remove(this);
						itemsList.getItems().add(this.getText());
					});
				}
//				boolean intersectsLeft = (Math.pow((newX - ((circleLeft.getBoundsInParent().getMinX() + circleLeft.getBoundsInParent().getMaxX()) / 2)), 2) + (Math.pow((newY - ((circleLeft.getBoundsInParent().getMinY() + circleLeft.getBoundsInParent().getMaxY()) / 2)), 2))) > Math.pow(circleLeft.getRadius(), 2);
//				boolean intersectsRight = (Math.pow((newX - ((circleRight.getBoundsInParent().getMinX() + circleRight.getBoundsInParent().getMaxX()) / 2)), 2) + (Math.pow((newY - ((circleRight.getBoundsInParent().getMinY() + circleRight.getBoundsInParent().getMaxY()) / 2)), 2))) > Math.pow(circleRight.getRadius(), 2);
//				if (intersectsLeft || intersectsRight) {
//					setLayoutX(newX);
//					setLayoutY(newY);
//				}
				mouseEvent.consume();
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

		void checkBounds() {
			if (this.getBoundsInParent().intersects(circleLeft.getBoundsInParent())
					&& this.getBoundsInParent().intersects(circleRight.getBoundsInParent())) {
				this.setColor(bothColor);
				circle = InCircle.BOTH;
			} else if (this.getBoundsInParent().intersects(circleLeft.getBoundsInParent())) {
				this.setColor(leftColor);
				circle = InCircle.LEFT;
			} else if (this.getBoundsInParent().intersects(circleRight.getBoundsInParent())) {
				this.setColor(rightColor);
				circle = InCircle.RIGHT;
			} else {
				this.setColor(noneColor);
				circle = InCircle.NONE;
			}
		}

		private class Delta {
			double x;
			double y;
		}
	}

	public class Stack {
		static final int MAX = 1000;
		int top;
		int a[] = new int[MAX]; // Maximum size of Stack

		boolean isEmpty() {
			return (top < 0);
		}

		Stack() 
	    { 
	        top = -1; 
	    }

		boolean push(int x) {
			if (top >= (MAX - 1)) {
				System.out.println("Stack Overflow");
				return false;
			} else {
				a[++top] = x;
				System.out.println(x + " pushed into stack");
				return true;
			}
		}

		int pop() {
			if (top < 0) {
				System.out.println("Stack Underflow");
				return 0;
			} else {
				int x = a[top--];
				return x;
			}
		}
	}
	
	Stack undoStack = new Stack();
	Stack redoStack = new Stack();
	

	@FXML
	void Undo() {
		
		
		System.out.println("Undo");

	}

	@FXML
	void Redo() {
		System.out.println("Redo");
	}
	
	
	//context menu stuff
	@FXML
	void circleRightMenu(){
		
		ContextMenu contextMenu = new ContextMenu();
        MenuItem menuItem1 = new MenuItem("Choice 1");
        MenuItem menuItem2 = new MenuItem("Choice 2");
        MenuItem menuItem3 = new MenuItem("Choice 3");

        menuItem3.setOnAction((event) -> {
            System.out.println("Choice 3 clicked!");
        });

        contextMenu.getItems().addAll(menuItem1,menuItem2,menuItem3);
        
        addItemField.setContextMenu(contextMenu);
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
		pane.getChildren().remove(tempLabel);
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
		
	}

	@FXML
	void takeScreenshot() {
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
			fc.setTitle("Save");
			fc.setInitialFileName(name);
			File selectedFile = fc.showSaveDialog(pane.getScene().getWindow());
			floatingMenu.setLayoutX(pane.getScene().getWindow().getX() - floatingMenu.getWidth() - 10);

			Bounds bounds = frameRect.getBoundsInLocal();// pane.getBoundsInLocal();
			Bounds screenBounds = frameRect.localToScreen(bounds);// pane.localToScreen(bounds);
			int x = (int) screenBounds.getMinX() - 15;
			int y = (int) screenBounds.getMinY() - 15;
			int width = (int) screenBounds.getWidth() + 30;
			int height = (int) screenBounds.getHeight() + 30;
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
			frameRect.getChildren().remove(d);
		}
		itemsInDiagram.clear();
	}

	void doTheSave(File selectedFile) {

		// File hierarchy of a .venn file:
		// . Diagram.venn:
		// ... Config.vlist:
		// ..... (0) Title, (1) Titles color, (2) Background color, (3) Intersection
		// item text color
		// ..... (0) Left circle title, (1) Left circle color, (2) Left circle scale,
		// (3) Item text color
		// ..... (0) Right circle title, (1) Right circle color, (2) Right circle scale,
		// (3) Item text color
		// ... Unassigned.csv:
		// ..... Unassigned items separated by new lines
		// ... InDiagram.vlist
		// ..... (0) Item text, (1) item color, (2) item x, (3) item y, (4) item
		// description

		try {
			FileOutputStream fos = new FileOutputStream(selectedFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			File config = new File("Config.vlist");
			StringBuilder sb = new StringBuilder();
			sb.append(title.getText() + "𔓱" + colorTitles.getValue().toString() + "𔓱"
					+ colorBackground.getValue().toString() + "𔓱" + colorBothItems.getValue().toString() + "\n");
			sb.append(circleLeftTitle.getText() + "𔓱" + colorLeft.getValue().toString() + "𔓱" + circleLeft.getScaleX()
					+ "𔓱" + colorLeftItems.getValue().toString() + "\n");
			sb.append(circleRightTitle.getText() + "𔓱" + colorRight.getValue().toString() + "𔓱"
					+ circleRight.getScaleX() + "𔓱" + colorRightItems.getValue().toString());
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
				sb.append(d.getText() + "𔓱" + d.getLabel().getTextFill().toString() + "𔓱" + d.getLayoutX() + "𔓱"
						+ d.getLayoutY() + "𔓱" + "item description");
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

			if (!(selectedFile.getName().length() > 5 && selectedFile.getName()
					.substring(selectedFile.getName().length() - 5).toLowerCase().equals(".venn"))) {
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
				line = line.substring(1, line.length() - 1);
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
		// ..... (0) Title, (1) Titles color, (2) Background color, (3) Intersection
		// item text color
		// ..... (0) Left circle title, (1) Left circle color, (2) Left circle scale,
		// (3) Item text color
		// ..... (0) Right circle title, (1) Right circle color, (2) Right circle scale,
		// (3) Item text color
		// ... Unassigned.csv:
		// ..... Unassigned items separated by new lines
		// ... InDiagram.vlist
		// ..... (0) Item text, (1) item color, (2) item x, (3) item y, (4) item
		// description

		try {
			FileChooser fc = new FileChooser();
			List<String> extensions = new ArrayList<String>();
			extensions.add("*.venn");

			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Venn files (*.venn)", "*.venn"));
			File file = fc.showOpenDialog(pane.getScene().getWindow());
			String line, title, leftTitle, rightTitle, elements[];
			Color bgColor, leftColor, rightColor, titleColor, leftTextColor, rightTextColor, bothTextColor;
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
			bothTextColor = Color.web(elements[3]);

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
				DraggableItem a = new DraggableItem(Double.parseDouble(elements[2]), Double.parseDouble(elements[3]),
						elements[0]);
				a.setColor(Color.web(elements[1]));
				a.setDescription(elements[4]);
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
			this.colorBothItems.setValue(bothTextColor);
			this.changeColorItems();

			openFile = file;
			// FIXME: Crashes the JUnit tests because they don't have a title bar on the
			// window to change
			Main.setWindowTitle(openFile.getName());
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
		circleLeft.setFill(Color.web(DEFAULT_LEFT_COLOR));
		circleLeft.setOpacity(DEFAULT_CIRCLE_OPACTIY);
		circleRight.setFill(Color.web(DEFAULT_RIGHT_COLOR));
		circleRight.setOpacity(DEFAULT_CIRCLE_OPACTIY);
		colorLeft.setValue(Color.web(DEFAULT_LEFT_COLOR));
		colorRight.setValue(Color.web(DEFAULT_RIGHT_COLOR));
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
		colorBothItems.setValue(Color.web(DEFAULT_BOTH_ITEM_COLOR));
		changeColorItems();

		openFile = null;
		// FIXME: Crashes the JUnit tests because they don't have a title bar on the
		// window to change

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
	void changeColorItems() {
		leftColor = colorLeftItems.getValue();
		rightColor = colorRightItems.getValue();
		bothColor = colorBothItems.getValue();
		for (DraggableItem d : itemsInDiagram) {
			if (d.getCircle() == InCircle.LEFT) {
				d.setColor(leftColor);
			} else if (d.getCircle() == InCircle.RIGHT) {
				d.setColor(rightColor);
			} else if (d.getCircle() == InCircle.BOTH) {
				d.setColor(bothColor);
			}
		}
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
		title.setStyle("-fx-background-color: transparent;\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2) + ";");
		circleLeftTitle.setStyle("-fx-background-color: transparent;\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2) + ";");
		circleRightTitle.setStyle("-fx-background-color: transparent;\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2) + ";");
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
		}
	}

	@FXML
	void importFile() {
		// Set up as SplitMenuButton and split into different methods
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

			File file = fc.showOpenDialog(pane.getScene().getWindow());
			if (fc.getSelectedExtensionFilter().equals(csvFilter)) {
				itemsList.getItems().addAll(importCSV(file));
			} else if (fc.getSelectedExtensionFilter().equals(imgFilter)) {
				System.out.println("Image: " + file.getAbsolutePath());
				a.setAlertType(AlertType.INFORMATION);
				a.setHeaderText("Feature coming soon");
				a.setContentText(
						"Adding images is not yet available. This feature will be coming in a future release.");
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
	void makeDummyItem() {

		removeFocus();
		String[] words = { "foobar", "foo", "bar", "baz", "qux", "quux", "quuz", "corge", "grault", "garply", "waldo",
				"flub", "plugh", "xyzzy", "thud", "wibble", "wobble", "wubble", "flob",
				"supercalifragilisticexpialidocious",
				"This is a very long string. Extremely long, in fact. This is to test how it handles long strings!" };
		addItemToDiagram(floatingMenu.getLayoutX() + floatingMenu.getWidth() + 20,
				circleLeftTitle.getLayoutY() + 5 * circleLeftTitle.getHeight(),
				words[(int) (Math.random() * words.length)]);

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
	}

	void addItemToDiagram(double x, double y, String text) {
		DraggableItem a = new DraggableItem(x, y, text);
		frameRect.getChildren().add(a);
		itemsInDiagram.add(a);
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
				settingsPane.expandedProperty().addListener(listener -> {
					if (settingsPane.expandedProperty().getValue().equals(true)) {
						floatingMenu.setLayoutY(floatingMenu.getLayoutY() - (settingsPane.getHeight() / 2));
					} else {
						floatingMenu.setLayoutY(floatingMenu.getLayoutY() + (settingsPane.getHeight() / 2));
					}
				});
				leftSizeField.setAlignment(Pos.CENTER);
				rightSizeField.setAlignment(Pos.CENTER);
				frameRect.setOnMouseReleased(mouseEvent -> {
					frameRect.getScene().setCursor(Cursor.DEFAULT);
					mouseEvent.consume();
				});
			}
		});

		for (Node n : pane.getChildren()) {
			n.setFocusTraversable(false);
		}
		for (Node n : buttonGrid.getChildren()) {
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
		itemsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		itemsList.setFocusTraversable(true);
		pane.setFocusTraversable(true);

		itemsList.setItems(items);
	}
}
