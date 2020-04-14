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

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Stack;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
//import javafx.scene.shape.Shape;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller {

	private final String VERSION = "1.3";
//	private final String DEFAULT_INTERSECTION_COLOR = "0x4594e3";
	private final String DEFAULT_BACKGROUND_COLOR = "0x1d1d1d";
	private final String DEFAULT_TITLE_COLOR = "0xffffff";
	private final String DEFAULT_LEFT_COLOR = "0xb47a7a";
	private final String DEFAULT_RIGHT_COLOR = "0xb4b162";
	private final String DEFAULT_LEFT_ITEM_COLOR = "0xffcccc";
	private final String DEFAULT_RIGHT_ITEM_COLOR = "0xe6e6b3";
	private final String DEFAULT_INTERSECTION_ITEM_COLOR = "0xffe699";
	private final double DEFAULT_CIRCLE_OPACTIY = 0.75;
	
	private double leftScale = 1;
	private double rightScale = 1;
	
	private String tempPath;
	private String titleText = "";
	private String leftText = "";
	private String rightText = "";
	private String addFieldText = "";

	private boolean multiSelect = false;
	private boolean changesMade = false;
	private boolean answerKeyWasImported = false;
	private boolean answersAreShowing = false;
	static boolean trackChanges = true;

	private ObservableList<String> items = FXCollections.observableArrayList();
	@FXML
	private ObservableList<DraggableItem> itemsInDiagram = FXCollections.observableArrayList();
	private ObservableList<String> imagesInDiagram = FXCollections.observableArrayList();
	@FXML
	private static ObservableList<DraggableItem> selectedItems = FXCollections.observableArrayList();
	private List<File> itemImages = new ArrayList<File>();

	private List<String> leftItemsAnswers = new ArrayList<String>();
	private List<String> rightItemsAnswers = new ArrayList<String>();
	private List<String> intersectionItemsAnswers = new ArrayList<String>();
	private List<String> unassignedItemsAnswers = new ArrayList<String>();
	private List<String> acceptedImageTypes = new ArrayList<String>();
	
	private Stack<Action> undoStack = new Stack<Action>();
	private Stack<Action> redoStack = new Stack<Action>();

//	private Color leftBackgroundColor = Color.web(DEFAULT_LEFT_COLOR);
//	private Color rightBackgroundColor = Color.web(DEFAULT_RIGHT_COLOR);
	private Color backgroundColor = Color.web(DEFAULT_BACKGROUND_COLOR);
	private Color titleColor = Color.web(DEFAULT_TITLE_COLOR);
	private Color leftItemColor = Color.web(DEFAULT_LEFT_ITEM_COLOR);
	private Color rightItemColor = Color.web(DEFAULT_RIGHT_ITEM_COLOR);
	private Color intersectionItemColor = Color.web(DEFAULT_INTERSECTION_ITEM_COLOR);

	@FXML
	private Circle circleLeft;
	@FXML
	private Circle circleRight;
//	private Shape circleIntersection;

	@FXML
	private AnchorPane pane;
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private StackPane scrollContent;

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
	private ColorPicker colorIntersectionItems;
//	@FXML
//	private ColorPicker colorIntersection;

	@FXML
	private MenuBar menuBar;
	@FXML
	private ToolBar toolBar;

	@FXML
	private Pane diagram;

	@FXML
	private ColorPicker colorBackground;
	@FXML
	private ColorPicker colorTitles;

	@FXML
	private MenuItem deleteMenu;
	@FXML
	private Button deleteButton;
	
	@FXML
	private MenuItem undoMenu;
	@FXML
	private MenuItem redoMenu;
	@FXML
	private Button undoButton;
	@FXML
	private Button redoButton;
	
	@FXML
	private MenuItem zoomInMenu;
	@FXML
	private MenuItem zoomOutMenu;
	@FXML
	private Button zoomInButton;
	@FXML
	private Button zoomOutButton;
	
	@FXML
	private MenuItem removeMenu;
	@FXML
	private Button removeButton;
	@FXML
	private Button saveButton;
	@FXML
	private Button openButton;
	@FXML
	private MenuButton importButton;
	@FXML
	private MenuButton exportButton;
	@FXML
	private MenuButton settingsButton;

	@FXML
	private TextField title;
	@FXML
	private TextField circleLeftTitle;
	@FXML
	private TextField circleRightTitle;
	
	@FXML
	private Button hideAnswersButton;
	@FXML
	private ImageView hideAnswersButtonImage;

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

	private static File openFile = null;
	
	class DraggableItem extends StackPane {
		protected Label text = new Label();
		protected String description = "";
		private Color color;
		protected ImageView answerImage = new ImageView();
		private final int MAX_WIDTH = 120;
		protected char circle;
		public double oldX, oldY;
		protected List<Action> actionList = new ArrayList<Action>();


		public DraggableItem(double x, double y) {
			relocate(x - 5, y - 5);
			getChildren().add(this.text);
			getChildren().add(this.answerImage);

			this.text.setPadding(new Insets(10));
			this.setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.NONE, new CornerRadii(1),
					new BorderWidths(5), new Insets(0))));
			requestFocus();
			this.text.setTextAlignment(TextAlignment.CENTER);
			this.text.setMaxWidth(MAX_WIDTH);
			this.text.setWrapText(true);

			this.focusedProperty().addListener((observable, hadFocus, hasFocus) -> {
				try {
					if (!hasFocus.booleanValue() &&  !multiSelect) {
						for (DraggableItem d : selectedItems) {
							d.setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.NONE,
									new CornerRadii(1), new BorderWidths(5), new Insets(0))));
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
						this.setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.SOLID,
								new CornerRadii(1), new BorderWidths(5), new Insets(0))));
					}
				} catch (Exception e) {
					removeFocus();
				}
			});
			
//			ContextMenu contextMenu = new ContextMenu();
//			MenuItem delete = new MenuItem();
//			delete.setOnAction(event -> deleteMenu.fire());
//			MenuItem remove = new MenuItem();
//			remove.setOnAction(event -> removeMenu.fire());
//			contextMenu.getItems().add(delete);
//			contextMenu.getItems().add(remove);
//			
//			this.setOnContextMenuRequested(event -> {
//				delete.setText(deleteMenu.getText());
//				remove.setText(removeMenu.getText());
//				contextMenu.show(this, this.getBoundsInParent().getMinX(), this.getBoundsInParent().getMinY());
//			});

			this.setOnKeyPressed(keyEvent -> {
				if (keyEvent.getCode() == KeyCode.DELETE || keyEvent.getCode() == KeyCode.BACK_SPACE) {
					deleteItem();
				}
				if (keyEvent.getCode() == KeyCode.ESCAPE) {
					removeFocus();
				}
				if (keyEvent.getCode() == KeyCode.SHIFT) {
					multiSelect = true;
				}
				if (keyEvent.getCode() == KeyCode.CONTROL || keyEvent.getCode() == KeyCode.COMMAND || keyEvent.getCode() == KeyCode.SHORTCUT) {
					removeFocus();
				}
				if (keyEvent.getCode() == KeyCode.UP) {
					this.actionList.clear();
					for (DraggableItem d : selectedItems) {
						d.setLayoutY(d.getLayoutY() - 10);
						if (!d.checkBounds()) {
							d.setLayoutY(d.getLayoutY() + 10);
							d.checkBounds();
							d.setBackground(null);
						} else {
							actionList.add(new MoveItemAction(d, d.getLayoutX(), d.getLayoutY(), d.getLayoutX(),
									d.getLayoutY() - 10));
						}
					}
					String message = selectedItems.size() == 1 ? "Move Item" : "Move Items";
					changesMade(new ActionGroup(actionList, message));
				}
				if (keyEvent.getCode() == KeyCode.DOWN) {
					this.actionList.clear();
					for (DraggableItem d : selectedItems) {
						d.setLayoutY(d.getLayoutY() + 10);
						if (!d.checkBounds()) {
							d.setLayoutY(d.getLayoutY() - 10);
							d.checkBounds();
							d.setBackground(null);
						} else {
							actionList.add(new MoveItemAction(d, d.getLayoutX(), d.getLayoutY(), d.getLayoutX(),
									d.getLayoutY() + 10));
						}
					}
					String message = selectedItems.size() == 1 ? "Move Item" : "Move Items";
					changesMade(new ActionGroup(actionList, message));
				}
				if (keyEvent.getCode() == KeyCode.LEFT) {
					this.actionList.clear();
					for (DraggableItem d : selectedItems) {
						d.setLayoutX(d.getLayoutX() - 10);
						if (!d.checkBounds()) {
							d.setLayoutX(d.getLayoutX() + 10);
							d.checkBounds();
							d.setBackground(null);
						} else {
							actionList.add(new MoveItemAction(d, d.getLayoutX(), d.getLayoutY(), d.getLayoutX() - 10,
									d.getLayoutY()));
						}
					}
					String message = selectedItems.size() == 1 ? "Move Item" : "Move Items";
					changesMade(new ActionGroup(actionList, message));
				}
				if (keyEvent.getCode() == KeyCode.RIGHT) {
					this.actionList.clear();
					for (DraggableItem d : selectedItems) {
						d.setLayoutX(d.getLayoutX() + 10);
						if (!d.checkBounds()) {
							d.setLayoutX(d.getLayoutX() - 10);
							d.checkBounds();
							d.setBackground(null);
						} else {
							actionList.add(new MoveItemAction(d, d.getLayoutX(), d.getLayoutY(), d.getLayoutX() + 10,
									d.getLayoutY()));
						}
					}
					String message = selectedItems.size() == 1 ? "Move Item" : "Move Items";
					changesMade(new ActionGroup(actionList, message));
				}
				keyEvent.consume();
			});

			this.setOnKeyReleased(keyEvent -> {
				if (keyEvent.getCode() == KeyCode.SHIFT || keyEvent.getCode() == KeyCode.CONTROL || keyEvent.getCode() == KeyCode.COMMAND || keyEvent.getCode() == KeyCode.SHORTCUT) {
					multiSelect = false;
				}
				keyEvent.consume();
			});
			
			this.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2) {
					String oldText = this.getText();
					String oldDesc = this.getDescription();
					
					Alert a = new Alert(AlertType.INFORMATION);

					TextField textField = new TextField(this.getText());
					textField.setPromptText("Enter a title for this item");
					textField.setId("itemTitle");

					TextArea textArea = new TextArea(description);
					textArea.setPromptText("Enter a description for this item");
					textArea.setEditable(true);
					textArea.setWrapText(true);
					textArea.setId("itemDescription");

					textArea.setMaxWidth(Double.MAX_VALUE);
					textArea.setMaxHeight(Double.MAX_VALUE);
					GridPane.setVgrow(textArea, Priority.ALWAYS);
					GridPane.setHgrow(textArea, Priority.ALWAYS);

					GridPane content = new GridPane();
					content.setMaxWidth(Double.MAX_VALUE);
					content.add(textField, 0, 0);
					content.add(textArea, 0, 1);

					a.getDialogPane().setContent(content);
					
					a.setTitle("Item details");
					String t = this.getText().length() > 50 ? this.getText().substring(0, 50) + "..." : this.getText();
					
					a.setHeaderText("Item details for \"" + t + "\":");
					ButtonType saveButton = new ButtonType("Save", ButtonData.OK_DONE);
					a.getButtonTypes().setAll(ButtonType.CANCEL, saveButton);
					Button save = (Button) a.getDialogPane().lookupButton(saveButton);
					Button cancel = (Button) a.getDialogPane().lookupButton(ButtonType.CANCEL);
					textField.textProperty().addListener(listener -> {
						if (textField.textProperty().getValue().contentEquals("")) {
							save.setDisable(true);
							a.headerTextProperty().setValue("Title field cannot be blank");
						} else {
							String txt = textField.textProperty().getValue().length() > 50 ? textField.textProperty().getValue().substring(0, 50) + "..." : textField.textProperty().getValue();
							save.setDisable(false);
							a.headerTextProperty().setValue("Item details for \"" + txt + "\":");
						}
					});
					save.setOnAction(e -> {
						this.text.setText(textField.getText());
						setDescription(textArea.getText());
						e.consume();
						a.close();
						event.consume();
						changesMade(new ChangeItemDetailsAction(this, oldText, oldDesc, this.getText(), this.getDescription()));
					});
					cancel.setOnAction(e -> {
						a.close();
						e.consume();
						event.consume();
					});
					a.show();
				}
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
		
		public String toString() {
			return this.getText();
		}
		
		public void setColor(Color c) {
			this.text.setTextFill(c);
			this.color = c;
		}

		public Color getColor() {
			return this.color;
		}
		
		public void setAnswerImage(String image) {
			this.answerImage.setImage(new Image(getClass().getResource(image).toExternalForm()));
		}
		
		public void clearAnswerImage() {
			this.answerImage.setImage(null);
		}

		public void setDescription(String desc) {
			this.description = desc;
		}

		public String getDescription() {
			return this.description;
		}
		
		protected void enableDrag() {
			Delta dragDelta = new Delta();
			setOnMousePressed(mouseEvent -> {
				toFront();
				dragDelta.x = mouseEvent.getX();
				dragDelta.y = mouseEvent.getY();
				getScene().setCursor(Cursor.CLOSED_HAND);
				requestFocus();
				for (DraggableItem d : selectedItems) {
					d.oldX = d.getLayoutX();
					d.oldY = d.getLayoutY();
				}
				mouseEvent.consume();
				this.actionList.clear();
			});
			setOnMouseReleased(mouseEvent -> {
				getScene().setCursor(Cursor.HAND);
				checkBounds();
				mouseEvent.consume();
			});
			setOnMouseDragged(mouseEvent -> {
				List<Action> actionList = new ArrayList<Action>();
				removeFocus();
				this.requestFocus();
				for (DraggableItem d : selectedItems) {
					if (answersAreShowing) {
						hideAnswers();
					}
					double newX = d.getLayoutX() + mouseEvent.getX() - dragDelta.x;
					double newY = d.getLayoutY() + mouseEvent.getY() - dragDelta.y;
					d.setLayoutX(newX);
					d.setLayoutY(newY);
					if (d.checkBounds()) {
						d.setBackground(null);
						d.getLabel().setTextFill(d.getColor());
						d.getScene().setCursor(Cursor.CLOSED_HAND);
						setOnMouseReleased(mouseEvent2 -> {
							for (DraggableItem i : selectedItems) {
								actionList.add(new MoveItemAction(i, i.oldX, i.oldY, newX, newY));
								i.getScene().setCursor(Cursor.HAND);
								i.checkBounds();
								mouseEvent.consume();
								mouseEvent2.consume();
								String message = selectedItems.size() == 1 ? "Move Item" : "Move Items";
								changesMade(new ActionGroup(actionList, message));
							}
							mouseEvent.consume();
							mouseEvent2.consume();
						});
					} else {
						d.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(5))));
						d.getLabel().setTextFill(Color.WHITE);
						d.getScene().setCursor(Cursor.DISAPPEAR);
						setOnMouseReleased(mouseEvent2 -> {
							String message = selectedItems.size() == 1 ? "Remove Item from Diagram" : "Remove Items from Diagram";
							List<String> removed = new ArrayList<String>();
							for (int i = selectedItems.size() - 1; i >= 0; i--) {
								if (!removed.contains(selectedItems.get(i).getText())) {
									removed.add(selectedItems.get(i).getText());
									if (selectedItems.get(i) instanceof DraggableImage) {
										actionList.add(new RemoveItemAction(selectedItems.get(i), selectedItems.get(i).oldX, selectedItems.get(i).oldY, itemsInDiagram, itemsList, imagesInDiagram));
										imagesInDiagram.remove(selectedItems.get(i).getText());
									} else {
										actionList.add(new RemoveItemAction(selectedItems.get(i), selectedItems.get(i).oldX, selectedItems.get(i).oldY, itemsInDiagram, itemsList));
									}
									diagram.getChildren().remove(selectedItems.get(i));
									itemsInDiagram.remove(selectedItems.get(i));
									itemsList.getItems().add(selectedItems.get(i).getText());
								}
								selectedItems.remove(i);
							}
							pane.getScene().setCursor(Cursor.DEFAULT);
							changesMade(new ActionGroup(actionList, message));
							mouseEvent.consume();
							mouseEvent2.consume();
						});
					}
				}
				mouseEvent.consume();
			});
			setOnMouseEntered(mouseEvent -> {
				if (!mouseEvent.isPrimaryButtonDown()) {
					getScene().setCursor(Cursor.HAND);
					mouseEvent.consume();
				}
				diagram.setOnMouseClicked(mouseEvent2 -> {
				});
				pane.setOnMouseClicked(mouseEvent2 -> {
				});
			});
			setOnMouseExited(mouseEvent -> {
				if (!mouseEvent.isPrimaryButtonDown())
					getScene().setCursor(Cursor.DEFAULT);
				mouseEvent.consume();

				diagram.setOnMouseClicked(mouseEvent2 -> removeFocus());
				pane.setOnMouseClicked(mouseEvent2 -> removeFocus());
			});
		}

		public boolean checkBounds() {
			Point2D centreLeft = new Point2D(
					(circleLeft.getBoundsInParent().getMinX() + circleLeft.getBoundsInParent().getMaxX()) / 2,
					(circleLeft.getBoundsInParent().getMinY() + circleLeft.getBoundsInParent().getMaxY()) / 2);
			Point2D centreRight = new Point2D(
					(circleRight.getBoundsInParent().getMinX() + circleRight.getBoundsInParent().getMaxX()) / 2,
					(circleRight.getBoundsInParent().getMinY() + circleRight.getBoundsInParent().getMaxY()) / 2);
			Point2D itemLocation = new Point2D(
					(this.getBoundsInParent().getMinX() + this.getBoundsInParent().getMaxX()) / 2,
					(this.getBoundsInParent().getMinY() + this.getBoundsInParent().getMaxY()) / 2);
			double distanceToLeft = itemLocation.distance(centreLeft);
			double distanceToRight = itemLocation.distance(centreRight);

			if (distanceToLeft <= circleLeft.getRadius() * circleLeft.getScaleX()
					&& distanceToRight <= circleRight.getRadius() * circleRight.getScaleX()) {
				this.setColor(colorIntersectionItems.getValue());
				this.circle = 'i';
				this.setBackground(null);
				return true;
			} else if (distanceToLeft <= circleLeft.getRadius() * circleLeft.getScaleX()) {
				this.setColor(colorLeftItems.getValue());
				this.circle = 'l';
				this.setBackground(null);
				return true;
			} else if (distanceToRight <= circleRight.getRadius() * circleRight.getScaleX()) {
				this.setColor(colorRightItems.getValue());
				this.circle = 'r';
				this.setBackground(null);
				return true;
			} else {
				this.setColor(Color.WHITE);
				this.circle = 'x';
				this.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(5))));
				return false;
			}
		}
		
		public char getCircle() {
			return this.circle;
		}

		protected class Delta {
			double x;
			double y;
		}
	}

	class DraggableImage extends DraggableItem {
		private ImageView image = new ImageView();
		private File imageFile;
		private final double MAX_WIDTH = 100;
		private String extension;
		
		public DraggableImage (double x, double y, String title, File imageFile) {
			super(x, y, title);
			try {
				Image image;
				image = new Image(imageFile.toURI().toURL().toExternalForm());
				
				this.imageFile = new File(tempPath + "imgs" + File.separatorChar + title);
				if (!imageFile.equals(this.imageFile))
					Files.copy(new FileInputStream(imageFile), Paths.get(tempPath + "imgs" + File.separatorChar + title));
//					Files.copy(imageFile, this.imageFile);
				this.imageFile.deleteOnExit();
				itemImages.add(this.imageFile);
				this.image.setImage(image);
				this.image.setPreserveRatio(true);
				this.image.setFitWidth(MAX_WIDTH);
				this.text.setVisible(false);
				this.text.setMaxWidth(MAX_WIDTH);
				this.text.setWrapText(false);
				this.getChildren().add(this.image);
				this.answerImage.toFront();
				this.extension = this.getText().substring(this.getText().lastIndexOf('.'));
				enableDrag();
			} catch (Exception e) {
				System.err.println("DraggableImage could not be created");
				e.printStackTrace();
			}
						
			this.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2) {
					Alert a = new Alert(AlertType.INFORMATION);

					TextField textField = new TextField(this.getText());
					textField.setPromptText("Enter a title for this item");

					TextArea textArea = new TextArea(description);
					textArea.setPromptText("Enter a description for this item");
					textArea.setEditable(true);
					textArea.setWrapText(true);

					textArea.setMaxWidth(Double.MAX_VALUE);
					textArea.setMaxHeight(Double.MAX_VALUE);
					GridPane.setVgrow(textArea, Priority.ALWAYS);
					GridPane.setHgrow(textArea, Priority.ALWAYS);

					GridPane content = new GridPane();
					content.setMaxWidth(Double.MAX_VALUE);
					content.add(textField, 0, 0);
					content.add(textArea, 0, 1);

					a.getDialogPane().setContent(content);
					try {
						ImageView image = new ImageView(new Image(this.imageFile.toURI().toURL().toExternalForm()));
						image.setPreserveRatio(true);
						image.setFitWidth(MAX_WIDTH);
						a.setGraphic(image);
					} catch (Exception e) {
						// Image couldn't be loaded, which should be impossible, since this is an image item
					}
					
					a.setTitle("Item details");
					String t = this.getText().length() > 40 ? this.getText().substring(0, 40) + "..." : this.getText();
					
					a.setHeaderText("Item details for \"" + t + "\":");
					ButtonType saveButton = new ButtonType("Save", ButtonData.OK_DONE);
					a.getButtonTypes().setAll(ButtonType.CANCEL, saveButton);
					Button save = (Button) a.getDialogPane().lookupButton(saveButton);
					Button cancel = (Button) a.getDialogPane().lookupButton(ButtonType.CANCEL);
					textField.textProperty().addListener(listener -> {
						if (textField.textProperty().getValue().contentEquals("")) {
							save.setDisable(true);
							a.headerTextProperty().setValue("Title field cannot be blank");
						} else {
							String name = textField.getText().endsWith(getImageExtension()) ? textField.getText() : textField.getText() + getImageExtension();
							if (!name.contentEquals(this.getText()) && new File(tempPath + "imgs" + File.separatorChar + name).exists()) {
								save.setDisable(true);
								a.headerTextProperty().setValue("An image called \"" + textField.getText() + "\" already exists");
							} else {
								String txt = textField.textProperty().getValue().length() > 40 ? textField.textProperty().getValue().substring(0, 40) + "..." : textField.textProperty().getValue();
								save.setDisable(false);
								a.headerTextProperty().setValue("Item details for \"" + txt + "\":");
							}
						}
					});
					save.setOnAction(e -> {
						if (!textField.getText().contentEquals(this.getText())) {
							if (!textField.getText().endsWith(getImageExtension())) {
								textField.setText(textField.getText() + getImageExtension());
							}
							changesMade(new ChangeItemDetailsAction(this, this.getText(), this.getDescription(), textField.getText(), textArea.getText(), imagesInDiagram, itemImages, tempPath));
							imagesInDiagram.remove(this.getText());
							this.text.setText(textField.getText());
							itemImages.remove(this.imageFile);
							File newImageFile = new File(tempPath + "imgs" + File.separatorChar + textField.getText());
							this.imageFile.renameTo(newImageFile);
							this.imageFile = newImageFile;
							this.imageFile.deleteOnExit();
							itemImages.add(this.imageFile);
							imagesInDiagram.add(this.getText());
						}
						setDescription(textArea.getText());
						e.consume();
						a.close();
						event.consume();
					});
					cancel.setOnAction(e -> {
						a.close();
						e.consume();
						event.consume();
					});
					a.show();
				}
			});
		}
		
		@Override
		public boolean checkBounds() {
			Point2D centreLeft = new Point2D(
					(circleLeft.getBoundsInParent().getMinX() + circleLeft.getBoundsInParent().getMaxX()) / 2,
					(circleLeft.getBoundsInParent().getMinY() + circleLeft.getBoundsInParent().getMaxY()) / 2);
			Point2D centreRight = new Point2D(
					(circleRight.getBoundsInParent().getMinX() + circleRight.getBoundsInParent().getMaxX()) / 2,
					(circleRight.getBoundsInParent().getMinY() + circleRight.getBoundsInParent().getMaxY()) / 2);
			Point2D itemLocation = new Point2D(
					(this.getBoundsInParent().getMinX() + this.getBoundsInParent().getMaxX()) / 2,
					(this.getBoundsInParent().getMinY() + this.getBoundsInParent().getMaxY()) / 2);
			double distanceToLeft = itemLocation.distance(centreLeft);
			double distanceToRight = itemLocation.distance(centreRight);

			boolean result;
			if (distanceToLeft <= circleLeft.getRadius() * circleLeft.getScaleX()
					&& distanceToRight <= circleRight.getRadius() * circleRight.getScaleX()) {
				this.setColor(colorIntersectionItems.getValue().brighter());
				this.circle = 'i';
//				this.image.setOpacity(1);
				result = true;
			} else if (distanceToLeft <= circleLeft.getRadius() * circleLeft.getScaleX()) {
				this.setColor(colorLeftItems.getValue());
				this.circle = 'l';
//				this.image.setOpacity(1);
				result = true;
			} else if (distanceToRight <= circleRight.getRadius() * circleRight.getScaleX()) {
				this.setColor(colorRightItems.getValue());
				this.circle = 'r';
//				this.image.setOpacity(1);
				result = true;
			} else {
				this.setColor(Color.WHITE);
				this.circle = 'x';
				this.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(5), new Insets(-5))));
//				this.image.setOpacity(0.75);
				result = false;
			}
			return result;
		}
				
		@Override
		protected void enableDrag() {
			Delta dragDelta = new Delta();
			setOnMousePressed(mouseEvent -> {
				toFront();
				this.actionList.clear();
				this.setBackground(new Background(new BackgroundFill(this.getColor(), new CornerRadii(5), new Insets(-5))));
				dragDelta.x = mouseEvent.getX();
				dragDelta.y = mouseEvent.getY();
				getScene().setCursor(Cursor.CLOSED_HAND);
				requestFocus();
				for (DraggableItem d : selectedItems) {
					d.oldX = d.getLayoutX();
					d.oldY = d.getLayoutY();
				}
				mouseEvent.consume();
			});
			
			setOnMouseReleased(mouseEvent -> {
				getScene().setCursor(Cursor.HAND);
				checkBounds();
				this.setBackground(null);
				mouseEvent.consume();
			});
			
			setOnMouseDragged(mouseEvent -> {
				for (DraggableItem d : selectedItems) {
					if (answersAreShowing) {
						hideAnswers();
					}
					double newX = d.getLayoutX() + mouseEvent.getX() - dragDelta.x;
					double newY = d.getLayoutY() + mouseEvent.getY() - dragDelta.y;
					d.setLayoutX(newX);
					d.setLayoutY(newY);
					if (d.checkBounds()) {
						setOnMouseReleased(mouseEvent2 -> {
							for (DraggableItem i : selectedItems) {
								actionList.add(new MoveItemAction(i, i.oldX, i.oldY, newX, newY));
								i.getScene().setCursor(Cursor.HAND);
								i.checkBounds();
								i.setBackground(null);
								mouseEvent.consume();
								mouseEvent2.consume();
								String message = selectedItems.size() == 1 ? "Move Item" : "Move Items";
								changesMade(new ActionGroup(actionList, message));
							}
							mouseEvent.consume();
							mouseEvent2.consume();
						});
						d.setBackground(new Background(new BackgroundFill(d.getColor(), new CornerRadii(5), new Insets(-5))));
						d.getLabel().setTextFill(d.getColor());
						d.getScene().setCursor(Cursor.CLOSED_HAND);
					} else {
						d.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(-5))));
						d.getLabel().setTextFill(Color.WHITE);
						d.getScene().setCursor(Cursor.DISAPPEAR);
						setOnMouseReleased(mouseEvent2 -> {
							String message = selectedItems.size() == 1 ? "Remove Item from Diagram" : "Remove Items from Diagram";
							List<String> removed = new ArrayList<String>();
							for (int i = selectedItems.size() - 1; i >= 0; i--) {
								if (!removed.contains(selectedItems.get(i).getText())) {
									removed.add(selectedItems.get(i).getText());
									if (selectedItems.get(i) instanceof DraggableImage) {
										actionList.add(new RemoveItemAction(selectedItems.get(i), selectedItems.get(i).oldX, selectedItems.get(i).oldY, itemsInDiagram, itemsList, imagesInDiagram));
										imagesInDiagram.remove(selectedItems.get(i).getText());
									} else {
										actionList.add(new RemoveItemAction(selectedItems.get(i), selectedItems.get(i).oldX, selectedItems.get(i).oldY, itemsInDiagram, itemsList));
									}
									diagram.getChildren().remove(selectedItems.get(i));
									itemsInDiagram.remove(selectedItems.get(i));
									itemsList.getItems().add(selectedItems.get(i).getText());
								}
								selectedItems.remove(i);
							}
							pane.getScene().setCursor(Cursor.DEFAULT);
							changesMade(new ActionGroup(actionList, message));
							mouseEvent.consume();
							mouseEvent2.consume();
						});
					}
				}
				mouseEvent.consume();
			});

			setOnMouseEntered(mouseEvent -> {
				if (!mouseEvent.isPrimaryButtonDown()) {
					getScene().setCursor(Cursor.HAND);
					mouseEvent.consume();
				}
				diagram.setOnMouseClicked(mouseEvent2 -> {/* Do nothing */});
				pane.setOnMouseClicked(mouseEvent2 -> {/* Do nothing */});
			});
			setOnMouseExited(mouseEvent -> {
				if (!mouseEvent.isPrimaryButtonDown())
					getScene().setCursor(Cursor.DEFAULT);
				mouseEvent.consume();

				diagram.setOnMouseClicked(mouseEvent2 -> removeFocus());
				pane.setOnMouseClicked(mouseEvent2 -> removeFocus());
			});
		}
		
		public void deleteImage() {
			this.imageFile.delete();
			itemImages.remove(this.imageFile);
		}
		
		public File getImageFile() {
			return this.imageFile;
		}
		
		public void setImageFile(File imageFile) {
			this.imageFile = imageFile;
		}
		
		public String getImageExtension() {
			return this.extension;
		}

	}
	
	public void changeWindowTitle(String title) {
		try {
			Main.primaryStage.setTitle(title);
		} catch (Exception e) {
			System.err.println("Main.primaryStage not found");
		}
	}
	
	private void changesMade(Action action) {
		if (Controller.trackChanges) {
			redoStack.clear();
			undoStack.add(action);
			undoMenu.setDisable(false);
			undoButton.setDisable(false);
			undoMenu.setText("Undo " + action.toString());
			if (!changesMade && openFile != null)
	//			XXX: Crashes JUnit test because there's no real "window" with TestFX
				 changeWindowTitle(openFile.getName() + " (Edited) - Venn");
			changesMade = true;
		}
	}
	
	private void moveActionStacks(Stack<Action> popStack, MenuItem popMenu, Button popButton, String popString, Stack<Action> pushStack, MenuItem pushMenu, Button pushButton, String pushString) {
		if (!popStack.isEmpty()) {
			Action action = popStack.pop();
			// FIXME: HACK
			if (action instanceof ImportImageAction) {
				List<String> list = new ArrayList<String>(items);
				moveActionStacks(popStack, popMenu, popButton, popString, pushStack, pushMenu, pushButton, pushString);
				itemsList.getItems().clear();
				itemsList.getItems().addAll(list);
			}
			action.invert();
			pushStack.push(action);
			pushMenu.setDisable(false);
			pushButton.setDisable(false);
			pushMenu.setText(pushString + " " + action.toString());
			if (!changesMade && openFile != null)
	//			XXX: Crashes JUnit test because there's no real "window" with TestFX
				 changeWindowTitle(openFile.getName() + " (Edited) - Venn");
			changesMade = true;
			if (popStack.isEmpty()) {
				popMenu.setDisable(true);
				popButton.setDisable(true);
				popMenu.setText(popString);
			} else {
				popMenu.setDisable(false);
				popButton.setDisable(false);
				popMenu.setText(popString + " " + popStack.peek().toString());
			}
		}
	}

	@FXML
	private void undo() {
		moveActionStacks(undoStack, undoMenu, undoButton, "Undo", redoStack, redoMenu, redoButton, "Redo");
	}

	@FXML
	private void redo() {
		moveActionStacks(redoStack, redoMenu, redoButton, "Redo", undoStack, undoMenu, undoButton, "Undo");
	}

	@FXML
	private void zoomIn() {
		if (diagram.getScaleX() < 1) {
			diagram.setScaleX(diagram.getScaleX() + 0.1);
			diagram.setScaleY(diagram.getScaleY() + 0.1);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					diagram.setLayoutX(diagram.getLayoutX() + diagram.getScaleX() * 100);
					diagram.setLayoutY(diagram.getLayoutY() + diagram.getScaleY() * 100);
					scrollContent.setPrefSize(
							Math.max(scrollContent.getBoundsInParent().getMaxX(),
									scrollPane.getViewportBounds().getWidth()),
							Math.max(scrollContent.getBoundsInParent().getMaxY(),
									scrollPane.getViewportBounds().getHeight()));
				}
			});
		}
		if (diagram.getScaleX() >= 1){
			zoomInButton.setDisable(true);
			zoomInMenu.setDisable(true);
		}
		zoomOutButton.setDisable(false);
		zoomOutMenu.setDisable(false);
	}

	@FXML
	private void zoomOut() {
		if (diagram.getScaleX() > 0.5) {
			diagram.setScaleX(diagram.getScaleX() - 0.1);
			diagram.setScaleY(diagram.getScaleY() - 0.1);
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					scrollContent.setPrefSize(
							Math.max(scrollContent.getBoundsInParent().getMaxX(),
									scrollPane.getViewportBounds().getWidth()),
							Math.max(scrollContent.getBoundsInParent().getMaxY(),
									scrollPane.getViewportBounds().getHeight()));
				}
			});
		}
		if (diagram.getScaleX() <= 0.5){
			zoomOutButton.setDisable(true);
			zoomOutMenu.setDisable(true);
		}
		zoomInButton.setDisable(false);
		zoomInMenu.setDisable(false);
	}
		
	@FXML
	private void zoomActualSize() {
		diagram.setScaleX(1);
		diagram.setScaleY(1);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				scrollContent.setPrefSize(
						scrollPane.getViewportBounds().getWidth(),
						scrollPane.getViewportBounds().getHeight());
			}
		});
	}

	@FXML
	private void addItemToList() {
		String newItem = addItemField.getText();
		if (!(newItem.contentEquals("") || new File(tempPath + "imgs" + File.separatorChar + newItem).exists())) {
			itemsList.getItems().add(newItem);
		}
		addItemField.setText("");
		changesMade(new AddToListAction(newItem, itemsList, addItemField));
	}

	@FXML
	private void dragFromItemsList() {
		Dragboard dragBoard = itemsList.startDragAndDrop(TransferMode.ANY);
		ClipboardContent content = new ClipboardContent();
		content.putString(itemsList.getSelectionModel().getSelectedItem());
		dragBoard.setContent(content);
		// Create the drag image
		// If it's an image, then use that image as the drag image
		// Otherwise, create a temporary label, take a snapshot of it, and use that
		if (new File(tempPath + "imgs" + File.separatorChar + dragBoard.getString()).exists() && !imagesInDiagram.contains(dragBoard.getString())) {
			try {
				ImageView image = new ImageView(new Image(new File(tempPath + "imgs" + File.separatorChar + dragBoard.getString()).toURI().toURL().toExternalForm()));
				pane.getChildren().add(image);
				image.setPreserveRatio(true);
				image.setFitWidth(100);
				double pixelScale = 2.0;
			    WritableImage writableImage = new WritableImage((int)Math.rint(pixelScale*image.getBoundsInParent().getWidth()), (int)Math.rint(pixelScale*image.getBoundsInParent().getHeight()));
			    SnapshotParameters spa = new SnapshotParameters();
			    spa.setFill(Color.TRANSPARENT);
				dragBoard.setDragView(image.snapshot(spa, writableImage), image.getBoundsInParent().getWidth(), -image.getBoundsInParent().getHeight());
				pane.getChildren().remove(image);
			} catch (Exception e) {
				System.err.println("DraggableImage could not be created.");
				e.printStackTrace();
			}
		} else {
			Label tempLabel = new Label(dragBoard.getString());
			pane.getChildren().add(tempLabel);
			tempLabel.setTextAlignment(TextAlignment.CENTER);
			tempLabel.setLayoutX(35);
			tempLabel.setLayoutY(35);
			tempLabel.setMaxWidth(105);
			tempLabel.setWrapText(true);
			tempLabel.setPadding(new Insets(10));
			tempLabel.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(0), new Insets(0))));
			tempLabel.setTextFill(Color.BLACK);
			dragBoard.setDragView(tempLabel.snapshot(null, null), tempLabel.getWidth() / 2, -tempLabel.getHeight() / 2);
			pane.getChildren().remove(tempLabel);
		}
	}

	@FXML
	private void deleteItem() {
		if (itemsList.isFocused()) {
			changesMade(new DeleteFromListAction(itemsList, (List<String>)(itemsList.getSelectionModel().getSelectedItems())));
			itemsList.getItems().removeAll(itemsList.getSelectionModel().getSelectedItems());
			if (itemsList.getItems().isEmpty()) {
				addItemField.requestFocus();
			}
		} else {
			List<Action> actionList= new ArrayList<Action>();
			for (DraggableItem d : selectedItems) {
				actionList.add(new DeleteItemAction(d, d.oldX, d.oldY, itemsInDiagram, imagesInDiagram));
				if (d instanceof DraggableImage) {
					((DraggableImage) d).deleteImage();
					imagesInDiagram.remove(d.getText());
				}
				diagram.getChildren().remove(d);
				itemsInDiagram.remove(d);
			}
			String message = selectedItems.size() == 1 ? "Delete Item" : "Delete Items";
			changesMade(new ActionGroup(actionList, message));
			selectedItems.clear();
		}
		removeFocus();
	}

	@FXML
	private void keyPressOnList(KeyEvent event) {
		if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
			deleteItem();
		}
		if (event.getCode() == KeyCode.ESCAPE) {
			pane.requestFocus();
		}
	}

	@FXML
	private void dragOntoItemsList(DragEvent event) {
		itemsList.setBlendMode(BlendMode.DIFFERENCE);
	}

	@FXML
	private void dragExitedItemsList() {
		itemsList.setBlendMode(null);
	}

	@FXML
	private void dragOverList(DragEvent event) {
		event.acceptTransferModes(TransferMode.ANY);
	}

	@FXML
	private void dragDroppedOnItemsList(DragEvent event) {
		// Drag a file onto the items list to import it
		try {
			@SuppressWarnings("unchecked")
			List<File> dragged = (ArrayList<File>) event.getDragboard().getContent(DataFormat.FILES);
			for (File file : dragged) {
				if (file.getName().endsWith(".csv")) {
					doTheCSVImport(file);
				} else if (file.getName().endsWith(".venn")) {
					if (changesMade) {
						Alert a = new Alert(AlertType.CONFIRMATION);
						a.setHeaderText("Are you sure you want to open another file?");
						a.setContentText("You will lose any unsaved changes");
						a.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
						Optional<ButtonType> result = a.showAndWait();
						if (result.get() == ButtonType.OK) {
							doTheLoad(file);
						}
					} else {
						doTheLoad(file);
					}
				} else if (file.getName().endsWith(".vansr")) {
					doTheAnswerImport(file);
				} else if (acceptedImageTypes.contains(file.getName().substring(file.getName().lastIndexOf(".")))) {
					doTheImageImport(file);
				}
			}
		} catch (ClassCastException e) {
			// You're dropping files, so dragged can't not be a list of files unless it's a text
			// item, but those are handled elsewhere so they should be ignored here.
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Drag import failed");
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("File could not be imported");
			a.setContentText("");
			a.setTitle("Error");
			a.show();
		}
		event.setDropCompleted(true);
		event.consume();
	}

	@FXML
	private void takeScreenshot() {
		// Taking a screenshot isn't making a change, but some operations within it do make changes.
		// Since we'll undo all the changes we've made, we'll save the change state and restore it after.
		// So if there were unsaved changes, there will still be unsaved changes, and if there weren't,
		// then this won't create any.
		boolean hadChanges = changesMade;
		removeFocus();
		String mainTitle = title.getText();
		String leftTitle = circleLeftTitle.getText();
		String rightTitle = circleRightTitle.getText();
		double scale = diagram.getScaleX();
		try {
			// Set scale to 1 temporarily so screenshot is right resolution
			diagram.setScaleX(1);
			diagram.setScaleY(1);
			// If the diagram has a title, make that the default title of the image
			String name = title.getText();
			if (name.contentEquals("")) {
				// If not, then if the two circles have titles, make "[LEFT] vs [RIGHT]" the default title of the image
				if (!(leftTitle.contentEquals("") && rightTitle.contentEquals(""))) {
					name = leftTitle + " vs " + rightTitle;
				} else {
					// If all else fails, set the default title to "Venn Diagram"
					name = "Venn Diagram";
				}
				// If the title is blank, add a space so the prompt text doesn't show in the image
				title.setText(" ");
			}
			// If the circle titles are blank, add a space so the prompt text doesn't show in the image
			if (circleLeftTitle.getText().contentEquals(""))
				circleLeftTitle.setText(" ");
			if (circleRightTitle.getText().contentEquals(""))
				circleRightTitle.setText(" ");
			name += ".png";
			// Do the snapshot
			double pixelScale = 2.0;
		    WritableImage writableImage = new WritableImage((int)Math.rint(pixelScale*diagram.getWidth()), (int)Math.rint(pixelScale*diagram.getHeight()));
		    SnapshotParameters spa = new SnapshotParameters();
		    spa.setFill(colorBackground.getValue());
		    spa.setTransform(Transform.scale(pixelScale, pixelScale));
		    WritableImage capture = diagram.snapshot(spa, writableImage); 
		    // Select the file location, and save the file
		    FileChooser fc = new FileChooser();
		    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png"));
		    fc.setTitle("Save");
		    fc.setInitialFileName(name);
		    File selectedFile = fc.showSaveDialog(pane.getScene().getWindow());
			ImageIO.write(SwingFXUtils.fromFXImage(capture, null), "png", selectedFile);
		} catch (IllegalArgumentException e) {
			// If the user cancels the save dialogue, then selectedFile will be null, which will
			// throw an IllegalArgumentException from ImageIO.write(...). Don't react, because they
			// just cancelled the save dialogue.
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("File could not be saved");
			a.setContentText("");
			a.setTitle("Error");
			a.show();
		}
		// Put scale, titles, and change state back to the way they were before taking the screenshot
		diagram.setScaleX(scale);
		diagram.setScaleY(scale);
		title.setText(mainTitle);
		circleLeftTitle.setText(leftTitle);
		circleRightTitle.setText(rightTitle);
		changesMade = hadChanges;
	}

	@FXML
	private void removeSelectedItems() {
		String message = selectedItems.size() == 1 ? "Remove Selected Item from Diagram" : "Remove Selected Items from Diagram";
		List<Action> actionList= new ArrayList<Action>();
		for (DraggableItem d : selectedItems) {
			actionList.add(new RemoveItemAction(d, d.oldX, d.oldY, itemsInDiagram, itemsList));
			if (d != null) {
				itemsList.getItems().add(d.getText());
				diagram.getChildren().remove(d); 
				if (imagesInDiagram.contains(d.getText())) {
					imagesInDiagram.remove(d.getText());
				}
			}
		}
		removeFocus();
		changesMade(new ActionGroup(actionList, message));
	}
	
	private void removeOrphanedImages() {
		for (File f : itemImages) {
			if (!(imagesInDiagram.contains(f.getName()) || itemsList.getItems().contains(f.getName()))) {
				f.delete();
			}
		}
	}

	private void doTheSave(File selectedFile) {

		// Hierarchy of a .venn file:
		// . Diagram.venn:
		// ... Config.vlist:
		// ..... (0) Title, (1) Titles color, (2) Background color
		// ..... (0) Left circle title, (1) Left circle color, (2) Left circle scale, (3) Item text color
		// ..... (0) Right circle title, (1) Right circle color, (2) Right circle scale, (3) Item text color
		// ..... (0) [Intersection color](TEMPORARILY DISABLED), (1) Intersection item text color
		// ... Unassigned.csv:
		// ..... Unassigned items separated by new lines
		// ... InDiagram.vlist:
		// ..... (0) Item text, (1) item x, (2) item y, (3) item description, (4) color
		// ... Images.csv:
		// ..... Image file names separated by new lines
		// ... imgs: (Directory)
		// ..... Image files

		try {
			FileOutputStream fos = new FileOutputStream(selectedFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			File config = new File(tempPath + "Config.vlist");
			StringBuilder sb = new StringBuilder();
			sb.append(title.getText() + "𔓱" + colorTitles.getValue().toString() + "𔓱"
					+ colorBackground.getValue().toString() + "\n");
			sb.append(circleLeftTitle.getText() + "𔓱" + colorLeft.getValue().toString() + "𔓱" + circleLeft.getScaleX()
					+ "𔓱" + colorLeftItems.getValue().toString() + "\n");
			sb.append(circleRightTitle.getText() + "𔓱" + colorRight.getValue().toString() + "𔓱"
					+ circleRight.getScaleX() + "𔓱" + colorRightItems.getValue().toString() + "\n");
			sb.append(/*colorIntersection.getValue().toString()*/ "HappySpelunking𔓱" + colorIntersectionItems.getValue().toString());
			BufferedWriter bw = new BufferedWriter(new FileWriter(config));
			bw.write(sb.toString());
			bw.close();

			File unassigned = new File(tempPath + "Unassigned.csv");
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
			
			removeOrphanedImages();
			File imagesList = new File(tempPath + "Images.csv");
			sb = new StringBuilder();
			bw = new BufferedWriter(new FileWriter(imagesList));
			if (!imagesInDiagram.isEmpty()) {
				bw.write("\"" + imagesInDiagram.get(0) + "\"");
				if (imagesInDiagram.size() > 1) {
					for (int i = 1; i < imagesInDiagram.size(); i++) {
						bw.append("\n\"" + imagesInDiagram.get(i) + "\"");
					}
				}
			}
			bw.close();

			File inDiagram = new File(tempPath + "InDiagram.vlist");
			sb = new StringBuilder();
			for (int i = 0; i < itemsInDiagram.size(); i++) {
				DraggableItem d = itemsInDiagram.get(i);
				sb.append(d.getText() + "𔓱" + d.getLayoutX() + "𔓱" + d.getLayoutY() + "𔓱" + d.getDescription() + "𔓱"
						+ d.getLabel().getTextFill().toString());
				if (i != itemsInDiagram.size() - 1) {
					sb.append("𔓱𔓱𔓱");
				}
			}
			bw = new BufferedWriter(new FileWriter(inDiagram));
			bw.write(sb.toString());
			bw.close();

			File[] files = { config, unassigned, inDiagram, imagesList };
			byte[] buffer = new byte[128];
			for (File f : files) {
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
						
			File imgsDir = new File(tempPath + "imgs");
			zos.putNextEntry(new ZipEntry(imgsDir.getName() + File.separatorChar));
			zos.closeEntry();
			File[] imgs = imgsDir.listFiles();
			for (File f : imgs) {
				if (!f.isHidden()) {
					ZipEntry entry = new ZipEntry(imgsDir.getName() + File.separatorChar + f.getName());
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
//			XXX: Crashes JUnit test because there's no real "window" with TestFX
			 changeWindowTitle(selectedFile.getName() + " - Venn");
			changesMade = false;
		} catch (Exception e) {
			e.printStackTrace();
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("File could not be saved");
			a.setContentText("");
			a.show();
		}

	}

	@FXML
	private void save() {
		if (openFile != null) {
			doTheSave(openFile);
		} else {
			saveAs();
		}
	}

	@FXML
	private void saveAs() {
		try {
			String name;
			if (openFile != null) {
				name = openFile.getName();
			} else if (!title.getText().contentEquals("")) {
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
				if (!selectedFile.getName().endsWith(".venn")) {
					selectedFile.renameTo(new File(selectedFile.getAbsolutePath() + ".venn"));
				}
				doTheSave(selectedFile);
			}
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
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
		return list;
	}
	
	private void open() {
		File file = null;
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Venn files (*.venn)", "*.venn"));
		file = fc.showOpenDialog(pane.getScene().getWindow());
		doTheLoad(file);
	}

	private void doTheLoad(File file) {
		
		// Hierarchy of a .venn file:
		// . Diagram.venn:
		// ... Config.vlist:
		// ..... (0) Title, (1) Titles color, (2) Background color
		// ..... (0) Left circle title, (1) Left circle color, (2) Left circle scale, (3) Item color
		// ..... (0) Right circle title, (1) Right circle color, (2) Right circle scale, (3) Item color
		// ..... (0) [Intersection color](TEMPORARILY DISABLED), (1) Intersection item text color
		// ... Unassigned.csv:
		// ..... Unassigned items separated by new lines
		// ... InDiagram.vlist:
		// ..... (0) Item text, (1) item x, (2) item y, (3) item description, (4) color
		// ... Images.csv:
		// ..... Image file names separated by new lines
		// ... imgs: (Directory)
		// ..... Image files

		Controller.trackChanges = false;
		try {
			String line, title, leftTitle, rightTitle, elements[];
			Color bgColor, leftColor, rightColor, /*intersectionColor,*/ titleColor, leftTextColor, rightTextColor,
					intersectionTextColor;
			double leftScale, rightScale;
			ObservableList<String> unassignedItems = FXCollections.observableArrayList();
			ObservableList<DraggableItem> inDiagram = FXCollections.observableArrayList();
			List<File> images = new ArrayList<File>();
			List<String> imgStrings = new ArrayList<String>();
			ZipFile vennFile = new ZipFile(file);
			ZipEntry ze;
			BufferedReader br;
			
			// Images
			ze = vennFile.getEntry("Images.csv");
			br = new BufferedReader(new InputStreamReader(vennFile.getInputStream(ze)));
			while ((line = br.readLine()) != null) {
				String fileName = "imgs" + File.separatorChar + line.substring(1, line.length() - 1);
				ze = vennFile.getEntry(fileName);
				File newImage = new File(tempPath + fileName);
				if (!newImage.exists())
					Files.copy(vennFile.getInputStream(ze), Paths.get(tempPath + fileName));
				images.add(newImage);
				imgStrings.add(line.substring(1, line.length() - 1));
				newImage.deleteOnExit();
			}
			br.close();

			// Config
			ze = vennFile.getEntry("Config.vlist");
			br = new BufferedReader(new InputStreamReader(vennFile.getInputStream(ze)));

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
//			intersectionColor = Color.web(elements[0]);
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
			StringBuilder sb = new StringBuilder("");
			boolean isFirstLine = true;
			while ((line = br.readLine()) != null) {
                if(isFirstLine) {
                    sb.append(line);
                    isFirstLine = false;
                } else {
                    sb.append("\n").append(line);
                }
			}
			String[] items = sb.toString().split("𔓱𔓱𔓱");
			if (items.length > 0 && !items[0].contentEquals("")) {
				for (String s : items) {
					elements = s.split("𔓱");
					DraggableItem a = null;
					File f = new File(tempPath + "imgs" + File.separatorChar + elements[0]);
					if (f.exists()) {
						a = new DraggableImage(Double.parseDouble(elements[1]) + 5, Double.parseDouble(elements[2]) + 5, elements[0], f);
					}
					else {
						a = new DraggableItem(Double.parseDouble(elements[1]) + 5, Double.parseDouble(elements[2]) + 5, elements[0]);
					}
					a.setDescription(elements[3]);
					a.setColor(Color.web(elements[4]));
					inDiagram.add(a);
				}
			}
			br.close();
			vennFile.close();

			this.title.setText(title);
			this.circleLeftTitle.setText(leftTitle);
			this.circleRightTitle.setText(rightTitle);
			this.colorBackground.setValue(bgColor);
			this.updateBackgroundColor();
			this.colorLeft.setValue(leftColor);
			this.updateLeftCircleColor();
			this.colorRight.setValue(rightColor);
			this.updateRightCircleColor();
//			this.colorIntersection.setValue(intersectionColor);
//			this.updateIntersection();
			this.colorTitles.setValue(titleColor);
			this.updateTitleColors();
			this.leftSizeSlider.setValue(leftScale * 100);
			this.changeSizeLeft();
			this.rightSizeSlider.setValue(rightScale * 100);
			this.changeSizeRight();
			this.leftScale = leftScale;
			this.rightScale = rightScale;
			this.itemsList.getItems().clear();
			this.itemsList.getItems().addAll(unassignedItems);
			this.diagram.getChildren().removeAll(this.itemsInDiagram);
			this.diagram.getChildren().addAll(inDiagram);
			this.itemsInDiagram.clear();
			this.itemsInDiagram.addAll(inDiagram);
			this.imagesInDiagram.clear();
			this.imagesInDiagram.addAll(imgStrings);
			this.colorLeftItems.setValue(leftTextColor);
			this.colorRightItems.setValue(rightTextColor);
			this.colorIntersectionItems.setValue(intersectionTextColor);
			this.leftItemColor = leftTextColor;
			this.rightItemColor = rightTextColor;
			this.intersectionItemColor = intersectionTextColor;
			this.itemImages = images;
			this.undoStack.clear();
			this.redoStack.clear();
			this.undoMenu.setDisable(true);
			this.undoButton.setDisable(true);
			this.redoMenu.setDisable(true);
			this.redoButton.setDisable(true);
			this.changesMade = false;
			Controller.openFile = file;
//			XXX: Crashes JUnit test because there's no real "window" with TestFX
			 changeWindowTitle(openFile.getName() + " - Venn");
			hideAnswers();
			clearAnswerKey();
			removeOrphanedImages();
			multiSelect = false;
		} catch (Exception e) {
			if (file != null) {
				e.printStackTrace();
				Alert a = new Alert(AlertType.ERROR);
				a.setHeaderText("File could not be opened");
				a.setContentText("");
				a.setTitle("Error");
				a.show();
			}
		}
		Controller.trackChanges = true;
	}

	@FXML
	private void loadFromFile() {
		if (changesMade) {
			Alert a = new Alert(AlertType.CONFIRMATION);
			a.setHeaderText("Are you sure you want to open another file?");
			a.setContentText("You will lose any unsaved changes");
			a.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
			Optional<ButtonType> result = a.showAndWait();
			if (result.get() == ButtonType.OK) {
				open();
			}
		} else {
			open();
		}
	}

	@FXML
	private void newDiagram() {
		if (changesMade) {
			Alert a = new Alert(AlertType.CONFIRMATION);
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
	
	private void clearAnswerKey() {
		hideAnswersButton.setDisable(true);
		hideAnswersButtonImage.setImage(new Image(getClass().getResource("images" + File.separatorChar + "hide.png").toExternalForm()));
		hideAnswersButton.setOnMouseClicked(event -> {
			hideAnswersButtonImage.setImage(new Image(getClass().getResource("images" + File.separatorChar + "show.png").toExternalForm()));
			hideAnswersButton.setTooltip(new Tooltip("Show Answers"));
			answersAreShowing = false;
			hideAnswers();
		});
		leftItemsAnswers.clear();
		rightItemsAnswers.clear();
		intersectionItemsAnswers.clear();
		unassignedItemsAnswers.clear();
	}

	@FXML
	private void doTheNew() {
		Controller.trackChanges = false;
		clearAnswerKey();
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
		colorLeftItems.setValue(Color.web(DEFAULT_LEFT_ITEM_COLOR));
		colorRightItems.setValue(Color.web(DEFAULT_RIGHT_ITEM_COLOR));
		colorIntersectionItems.setValue(Color.web(DEFAULT_INTERSECTION_ITEM_COLOR));
		leftItemColor = Color.web(DEFAULT_LEFT_ITEM_COLOR);
		rightItemColor = Color.web(DEFAULT_RIGHT_ITEM_COLOR);
		intersectionItemColor = Color.web(DEFAULT_INTERSECTION_ITEM_COLOR);
//		colorIntersection.setValue(Color.web(DEFAULT_INTERSECTION_COLOR));
		rightSizeSlider.setValue(100);
		leftSizeSlider.setValue(100);
		titleColor = Color.web(DEFAULT_TITLE_COLOR);
		backgroundColor = Color.web(DEFAULT_BACKGROUND_COLOR);
//		leftBackgroundColor = Color.web(DEFAULT_LEFT_COLOR);
//		rightBackgroundColor = Color.web(DEFAULT_RIGHT_COLOR);
		leftItemColor = Color.web(DEFAULT_LEFT_ITEM_COLOR);
		rightItemColor = Color.web(DEFAULT_RIGHT_ITEM_COLOR);
		intersectionItemColor = Color.web(DEFAULT_INTERSECTION_ITEM_COLOR);
		addItemField.setText("");
		addFieldText = "";
		updateTitleColors();
		updateItemColors();
		updateBackgroundColor();
		changeSizeLeft();
		changeSizeRight();
		leftScale = 1;
		rightScale = 1;
		diagram.getChildren().removeAll(itemsInDiagram);
		itemsInDiagram.clear();
		imagesInDiagram.clear();
		for (File f : itemImages) {
			f.delete();
		}
		itemImages.clear();
		
		openFile = null;
//		XXX: Crashes JUnit test because there's no real "window" with TestFX
		 changeWindowTitle("Venn");
		changesMade = false;
		undoStack.clear();
		redoStack.clear();
		undoMenu.setDisable(true);
		undoButton.setDisable(true);
		undoMenu.setText("Undo");
		redoMenu.setDisable(true);
		redoButton.setDisable(true);
		redoMenu.setText("Redo");
		Controller.trackChanges = true;
	}

	@FXML
	private void openManual() {
		try {
			// TODO: Change link to online manual in future release
			java.awt.Desktop.getDesktop().browse(new URI("https://github.com/nourinjh/EECS2311/"));
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("Manual could not be opened");
			a.setContentText("");
			a.setTitle("Error");
			a.show();
		}
	}

	@FXML
	private void updateLeftCircleColor() {
		changesMade(new ChangeCircleColorAction(circleLeft, colorLeft));
		circleLeft.setFill(colorLeft.getValue());
		circleLeft.setOpacity(DEFAULT_CIRCLE_OPACTIY);
	}

	@FXML
	private void updateItemColors() {
		List<Action> actionList = new ArrayList<Action>();
		actionList.add(new ChangeItemColorAction(leftItemColor, colorLeftItems, itemsInDiagram));
		actionList.add(new ChangeItemColorAction(rightItemColor, colorRightItems, itemsInDiagram));
		actionList.add(new ChangeItemColorAction(intersectionItemColor, colorIntersectionItems, itemsInDiagram));
		leftItemColor = colorLeftItems.getValue();
		rightItemColor = colorRightItems.getValue();
		intersectionItemColor = colorIntersectionItems.getValue();
		changesMade(new ActionGroup(actionList, "Change Text Colour"));
		for (DraggableItem d : itemsInDiagram) {
			d.checkBounds();
		}
	}

	@FXML
	private void updateRightCircleColor() {
		changesMade(new ChangeCircleColorAction(circleRight, colorRight));
		circleRight.setFill(colorRight.getValue());
		circleRight.setOpacity(DEFAULT_CIRCLE_OPACTIY);
	}

	@FXML
	private void updateBackgroundColor() {
		String newStyle = "-fx-background-color: #"
				+ colorBackground.getValue().toString().substring(2, colorBackground.getValue().toString().length() - 2)
				+ ";";
		changesMade(new ChangeBackgroundColorAction(pane, backgroundColor, colorBackground));
		pane.setStyle(newStyle);
		backgroundColor = colorBackground.getValue();
	}

	@FXML
	private void updateTitleColors() {
		String titleStyle = "-fx-background-color: transparent;\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2) + ";";
		String leftStyle = "-fx-background-color: transparent;\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2) + ";";
		String rightStyle = "-fx-background-color: transparent;\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2) + ";";
		List<Action> actionList = new ArrayList<Action>();
		actionList.add(new ChangeTitleColorAction(title, titleColor, colorTitles));
		actionList.add(new ChangeTitleColorAction(circleLeftTitle, titleColor, colorTitles));
		actionList.add(new ChangeTitleColorAction(circleRightTitle, titleColor, colorTitles));
		changesMade(new ActionGroup(actionList, "Change Text Colour"));

		title.setStyle(titleStyle);
		circleLeftTitle.setStyle(leftStyle);
		circleRightTitle.setStyle(rightStyle);
		titleColor = colorTitles.getValue();
	}

	@FXML
	private void changeSizeLeft() {
		changeSizeSlider(leftSizeSlider, leftSizeField, circleLeft);
	}
		
//	@FXML
//	private void updateIntersection() {
//		frameRect.getChildren().remove(circleIntersection);
//		circleIntersection = Shape.intersect(circleLeft, circleRight);
//		circleIntersection.setFill(colorIntersection.getValue());
//		circleIntersection.setOnDragDropped(event -> {
//			dropItem(event);
//		});
//		circleIntersection.mouseTransparentProperty().set(true);
//		circleIntersection.setLayoutX(circleLeft.getCenterX() - 391);
//		circleIntersection.setLayoutY(circleLeft.getCenterY() - 69);
//		circleIntersection.setOpacity(0.8);
//		frameRect.getChildren().add(circleIntersection);
//		for (DraggableItem d : itemsInDiagram) {
//			d.toFront();
//		}
//		updateItemColors();
////		changesMade();
//	}

	@FXML
	private void changeSizeLeftField(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			try {
				double d = Double.parseDouble(leftSizeField.getText());
				if (d / 100 != leftScale) {
					d = d < 75 ? 75 : d > 120 ? 120 : d;
					leftSizeSlider.setValue(d);
					changeSizeLeft();
					changesMade(new ChangeCircleSizeAction(circleLeft, leftScale, leftSizeSlider, leftSizeField));
					leftScale = leftSizeSlider.getValue() / 100;
				}
			} catch (Exception e) {
				leftSizeField.setText(String.format("%.0f", leftSizeSlider.getValue()));
			}
		}
	}

	@FXML
	private void changeSizeRight() {
		changeSizeSlider(rightSizeSlider, rightSizeField, circleRight);
	}
	
	private void changeSizeSlider(Slider slider, TextField field, Circle circle) {
		circle.setScaleX(slider.getValue() / 100.0);
		circle.setScaleY(slider.getValue() / 100.0);
		field.setText(String.format("%.0f", slider.getValue()));
//		updateIntersection();
		Controller.trackChanges = false;
		updateItemColors();
		Controller.trackChanges = true;
	}
	
	@FXML
	private void changeSizeRightField(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			try {
				double d = Double.parseDouble(rightSizeField.getText());
				if (d / 100 != rightScale) {
					d = d < 75 ? 75 : d > 120 ? 120 : d;
					rightSizeSlider.setValue(d);
					changeSizeRight();
					changesMade(new ChangeCircleSizeAction(circleRight, rightScale, rightSizeSlider, rightSizeField));
					rightScale = rightSizeSlider.getValue() / 100;
				}
			} catch (Exception e) {
				rightSizeField.setText(String.format("%.0f", rightSizeSlider.getValue()));
			}
		}
	}

	@FXML
	private void importCSV() {
		importFile("csv");
	}

	@FXML
	private void importImage() {
		importFile("img");
	}

	@FXML
	private void importAnswer() {
		importFile("ans");
	}

	private void importFile(String type) {
		try {
			FileChooser fc = new FileChooser();
			List<String> imgExtensions = new ArrayList<String>();
			StringBuilder imageFilesString = new StringBuilder("Image files (");
			for (int i = 0; i < acceptedImageTypes.size(); i++) {
				imgExtensions.add("*" + acceptedImageTypes.get(i));
				imageFilesString.append("*");
				imageFilesString.append(acceptedImageTypes.get(i));
				if (i < acceptedImageTypes.size() - 1)
					imageFilesString.append(", ");
			}
			imageFilesString.append(")");
			ExtensionFilter csvFilter = new ExtensionFilter("CSV files (*.csv)", "*.csv");
			ExtensionFilter ansFilter = new ExtensionFilter("Venn Answer Key files (*.vansr)", "*.vansr");
			ExtensionFilter imgFilter = new ExtensionFilter(imageFilesString.toString(), imgExtensions);
			fc.getExtensionFilters().add(csvFilter);
			fc.getExtensionFilters().add(imgFilter);
			fc.getExtensionFilters().add(ansFilter);

			if (type.contentEquals("csv")) {
				fc.setSelectedExtensionFilter(csvFilter);
			} else if (type.contentEquals("img")) {
				fc.setSelectedExtensionFilter(imgFilter);
			} else /* if (type.contentEquals("ans")) */ {
				fc.setSelectedExtensionFilter(ansFilter);
			}

			File file = fc.showOpenDialog(pane.getScene().getWindow());
			
			if (fc.getSelectedExtensionFilter().equals(csvFilter)) {
				doTheCSVImport(file);
			}
			
			else if (fc.getSelectedExtensionFilter().equals(imgFilter)) {
				doTheImageImport(file);
			}
			
			else if (fc.getSelectedExtensionFilter().equals(ansFilter)) {
				doTheAnswerImport(file);
			}
		} catch (NullPointerException e) {
			// If the user cancels the save dialogue, then file will be null, which will throw
			// a NullPointerException from all the doThe*(...) methods. Don't react, because
			// they just cancelled the save dialogue. 
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("File could not be imported");
			a.setContentText("");
			a.setTitle("Error");
			a.show();
		}
	}

	private void doTheImageImport(File file, double x, double y) {
		String imgName = tempPath + "imgs" + File.separatorChar + incrementImageNameIfExists(file.getName());
		addImageToDiagram(x, y, imgName.substring(tempPath.length() + 5), file);
		changesMade(new ImportImageAction());
	}
	
	private void doTheImageImport(File file) {
		doTheImageImport(file, diagram.getWidth()/2 - 50, diagram.getHeight()/2 + 50);
		changesMade(new ImportImageAction());
	}
	
	private String incrementImageNameIfExists(String imgName) {
		if (new File(tempPath + "imgs" + File.separatorChar + imgName).exists()) {
			int i = 2;
			imgName = imgName.substring(0, imgName.lastIndexOf('.')) + " " + i + imgName.substring(imgName.lastIndexOf('.'));
			while (new File(tempPath + "imgs" + File.separatorChar + imgName).exists()) {
				i++;
				imgName = imgName.substring(0, imgName.lastIndexOf('.') - 1) + i + imgName.substring(imgName.lastIndexOf('.'));
			}
		}
		return imgName;
	}

	private void doTheAnswerImport(File file) throws ZipException, IOException {
		List<String> left = new ArrayList<String>();
		List<String> right = new ArrayList<String>();
		List<String> intersection = new ArrayList<String>();
		List<String> unassigned = new ArrayList<String>();
		ZipFile answerFile = new ZipFile(file);
		ZipEntry ze;
		BufferedReader br;
		String line;
		
		ze = answerFile.getEntry("leftAnswers.csv");
		br = new BufferedReader(new InputStreamReader(answerFile.getInputStream(ze)));
		while ((line = br.readLine()) != null) {
			if (line.contains(",")) {
				line = line.substring(1, line.length() - 1);
			}
			left.add(line);

		}
		br.close();
		
		ze = answerFile.getEntry("rightAnswers.csv");
		br = new BufferedReader(new InputStreamReader(answerFile.getInputStream(ze)));
		while ((line = br.readLine()) != null) {
			if (line.contains(",")) {
				line = line.substring(1, line.length() - 1);
			}
			right.add(line);

		}
		br.close();
		
		ze = answerFile.getEntry("intersectionAnswers.csv");
		br = new BufferedReader(new InputStreamReader(answerFile.getInputStream(ze)));
		while ((line = br.readLine()) != null) {
			if (line.contains(",")) {
				line = line.substring(1, line.length() - 1);
			}
			intersection.add(line);

		}
		br.close();
		
		ze = answerFile.getEntry("unassignedAnswers.csv");
		br = new BufferedReader(new InputStreamReader(answerFile.getInputStream(ze)));
		while ((line = br.readLine()) != null) {
			if (line.contains(",")) {
				line = line.substring(1, line.length() - 1);
			}
			unassigned.add(line);
		}
		br.close();
		answerFile.close();
		
		leftItemsAnswers = left;
		rightItemsAnswers = right;
		intersectionItemsAnswers = intersection;
		unassignedItemsAnswers = unassigned;
		hideAnswersButton.setDisable(false);
		answerKeyWasImported = true;
		checkAnswers();
	}
		
	@FXML
	private void exportCSV() {
		try {
			String name;
			if (openFile != null) {
				name = openFile.getName().substring(0, openFile.getName().length() - 5) + ".csv";
			} else if (!title.getText().contentEquals("")) {
				name = title.getText() + ".csv";
			} else if (!circleLeftTitle.getText().contentEquals("") && !circleRightTitle.getText().contentEquals("")) {
				name = circleLeftTitle.getText() + " vs " + circleRightTitle.getText() + ".csv";
			} else {
				name = "Venn Diagram.csv";
			}

			FileChooser fc = new FileChooser();
			fc.setTitle("Save");
			fc.setInitialFileName(name);
			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
			File selectedFile = fc.showSaveDialog(pane.getScene().getWindow());

			if (selectedFile != null) {
				if (!selectedFile.getName().endsWith(".csv")) {
					selectedFile.renameTo(new File(selectedFile.getAbsolutePath() + ".csv"));
				}
				List<String> allItems = new ArrayList<String>();
				allItems.addAll(items);
				for (DraggableItem d : itemsInDiagram) {
					allItems.add(d.getText());
				}
				BufferedWriter bw = new BufferedWriter(new FileWriter(selectedFile));
				if (!allItems.isEmpty()) {
					bw.write(allItems.get(0));
					if (allItems.size() > 1) {
						for (int i = 1; i < allItems.size(); i++) {
							if (allItems.get(i).contains(",")) {
								bw.append("\n\"" + allItems.get(i) + "\"");
							} else {
								bw.append("\n" + allItems.get(i));
							}
						}
					}
				}
				bw.close();
			}
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("File could not be saved");
			a.setContentText("");
			a.show();
		}
	}
	
	@FXML
	private void exportAnswer() {
		try {
			String name;
			if (openFile != null) {
				name = openFile.getName().substring(0, openFile.getName().length()-5);
			} else if (!title.getText().contentEquals("")) {
				name = title.getText() + ".vansr";
			} else if (!circleLeftTitle.getText().contentEquals("") && !circleRightTitle.getText().contentEquals("")) {
				name = circleLeftTitle.getText() + " vs " + circleRightTitle.getText() + ".vansr";
			} else {
				name = "Venn Diagram.vansr";
			}

			FileChooser fc = new FileChooser();
			fc.setTitle("Save");
			fc.setInitialFileName(name);
			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Venn Answer Key files (*.vansr)", "*.vansr"));
			File selectedFile = fc.showSaveDialog(pane.getScene().getWindow());

			if (selectedFile != null) {
				if (!selectedFile.getName().endsWith(".vansr")) {
					selectedFile.renameTo(new File(selectedFile.getAbsolutePath() + ".vansr"));
				}
				
				List<String> leftItems = new ArrayList<String>();
				List<String> rightItems = new ArrayList<String>();
				List<String> intersectionItems = new ArrayList<String>();
				for (DraggableItem d : itemsInDiagram) {
					if (d.getColor().equals(colorLeftItems.getValue())) {
						leftItems.add(d.getText());
					} else if (d.getColor().equals(colorRightItems.getValue())) {
						rightItems.add(d.getText());
					} else if (d.getColor().equals(colorIntersectionItems.getValue())) {
						intersectionItems.add(d.getText());
					}
				}
				FileOutputStream fos = new FileOutputStream(selectedFile);
				ZipOutputStream zos = new ZipOutputStream(fos);
				BufferedWriter bw;

				File leftFile = new File("leftAnswers.csv");
				bw = new BufferedWriter(new FileWriter(leftFile));
				if (!leftItems.isEmpty()) {
					bw.write(leftItems.get(0));
					if (leftItems.size() > 1) {
						for (int i = 1; i < leftItems.size(); i++) {
							if (leftItems.get(i).contains(",")) {
								bw.append("\n\"" + leftItems.get(i) + "\"");
							} else {
								bw.append("\n" + leftItems.get(i));
							}
						}
					}
				}
				bw.close();

				File rightFile = new File("rightAnswers.csv");
				bw = new BufferedWriter(new FileWriter(rightFile));
				if (!rightItems.isEmpty()) {
					bw.write(rightItems.get(0));
					if (rightItems.size() > 1) {
						for (int i = 1; i < rightItems.size(); i++) {
							if (rightItems.get(i).contains(",")) {
								bw.append("\n\"" + rightItems.get(i) + "\"");
							} else {
								bw.append("\n" + rightItems.get(i));
							}
						}
					}
				}
				bw.close();

				File intersectionFile = new File("intersectionAnswers.csv");
				bw = new BufferedWriter(new FileWriter(intersectionFile));
				if (!intersectionItems.isEmpty()) {
					bw.write(intersectionItems.get(0));
					if (intersectionItems.size() > 1) {
						for (int i = 1; i < intersectionItems.size(); i++) {
							if (intersectionItems.get(i).contains(",")) {
								bw.append("\n\"" + intersectionItems.get(i) + "\"");
							} else {
								bw.append("\n" + intersectionItems.get(i));
							}
						}
					}
				}
				bw.close();
				
				File unassignedFile = new File("unassignedAnswers.csv");
				bw = new BufferedWriter(new FileWriter(unassignedFile));
				if (!items.isEmpty()) {
					bw.write(items.get(0));
					if (items.size() > 1) {
						for (int i = 1; i < items.size(); i++) {
							if (items.get(i).contains(",")) {
								bw.append("\n\"" + items.get(i) + "\"");
							} else {
								bw.append("\n" + items.get(i));
							}
						}
					}
				}
				bw.close();
				
				

				File[] files = { leftFile, rightFile, intersectionFile, unassignedFile };
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
			}
		} catch (NullPointerException e) {
			// If the user cancels the save dialogue, then selectedFile will be null, which will
			// throw a NullPointerException from all the methods that use it. Don't react, because
			// they just cancelled the save dialogue. 
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("File could not be saved");
			a.setContentText("");
			a.show();
		}
				
	}
	
	@FXML
	private void checkAnswers() {
		if (!answerKeyWasImported) {
			importAnswer();
		}
		List<DraggableItem> leftItems = new ArrayList<DraggableItem>();
		List<DraggableItem> rightItems = new ArrayList<DraggableItem>();
		List<DraggableItem> intersectionItems = new ArrayList<DraggableItem>();
		for (DraggableItem d : itemsInDiagram) {
			d.checkBounds();
			if (d.getCircle() == 'l') {
				leftItems.add(d);
			} else if (d.getCircle() == 'r') {
				rightItems.add(d);
			} else if (d.getCircle() == 'i') {
				intersectionItems.add(d);
			} else {
				d.setAnswerImage("images" + File.separatorChar + "incorrect.png");
			}
		}
		
		List<String> leftItemsAnswers = new ArrayList<String>(this.leftItemsAnswers);
		List<String> rightItemsAnswers = new ArrayList<String>(this.rightItemsAnswers);
		List<String> intersectionItemsAnswers = new ArrayList<String>(this.intersectionItemsAnswers);
		List<String> unassignedItemsAnswers = new ArrayList<String>(this.unassignedItemsAnswers);
		
		for (DraggableItem d : leftItems) {
			if (leftItemsAnswers.remove(d.getText())) {
				d.setAnswerImage("images" + File.separatorChar + "correct.png");
			} else {
				d.setAnswerImage("images" + File.separatorChar + "incorrect.png");
			}
		}
		for (DraggableItem d : rightItems) {
			if (rightItemsAnswers.remove(d.getText())) {
				d.setAnswerImage("images" + File.separatorChar + "correct.png");
			} else {
				d.setAnswerImage("images" + File.separatorChar + "incorrect.png");
			}
		}
		for (DraggableItem d : intersectionItems) {
			if (intersectionItemsAnswers.remove(d.getText())) {
				d.setAnswerImage("images" + File.separatorChar + "correct.png");
			} else {
				d.setAnswerImage("images" + File.separatorChar + "incorrect.png");
			}
		}
		for (String d : items) {
			if (!unassignedItemsAnswers.remove(d)) {
				itemsList.getSelectionModel().select(d);
			}
		}
		hideAnswersButtonImage.setImage(new Image(getClass().getResource("images" + File.separatorChar + "hide.png").toExternalForm()));
		hideAnswersButton.setTooltip(new Tooltip("Hide Answers"));
		answersAreShowing = true;
		hideAnswersButton.setOnMouseClicked(event -> {
			hideAnswersButtonImage.setImage(new Image(getClass().getResource("images" + File.separatorChar + "show.png").toExternalForm()));
			hideAnswersButton.setTooltip(new Tooltip("Show Answers"));
			answersAreShowing = false;
			hideAnswers();
		});
	}
	
	@FXML
	private void hideAnswers() {
		for (DraggableItem d : itemsInDiagram) {
			d.clearAnswerImage();
		}
		itemsList.getSelectionModel().clearSelection();
		hideAnswersButtonImage.setImage(new Image(getClass().getResource("images" + File.separatorChar + "show.png").toExternalForm()));
		hideAnswersButton.setTooltip(new Tooltip("Show Answers"));
		answersAreShowing = false;
		hideAnswersButton.setOnMouseClicked(event -> {
			hideAnswersButtonImage.setImage(new Image(getClass().getResource("images" + File.separatorChar + "hide.png").toExternalForm()));
			hideAnswersButton.setTooltip(new Tooltip("Hide Answers"));
			answersAreShowing = true;
			checkAnswers();
		});
	}

	@FXML
	private void removeFocus() {
		pane.requestFocus();
		itemsList.getSelectionModel().clearSelection();
		for (int i = selectedItems.size() - 1; i >= 0; i--) {
			selectedItems.get(i).setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.NONE,
					new CornerRadii(1), new BorderWidths(5), new Insets(0))));
			selectedItems.remove(i);
		}
	}
	
	private void doTheCSVImport(File file) throws Exception {
		List<String> addedItems = doTheCSV(file);
		itemsList.getItems().addAll(addedItems);
		List<Action> actionList = new ArrayList<Action>();
		for (String s : addedItems) {
			Action a = new AddToListAction(s, itemsList, null);
			actionList.add(a);
		}
		changesMade(new ActionGroup(actionList, "Import CSV File"));
	}

	@FXML
	private void dropItem(DragEvent event) {
		// Drag an item from the item list into the diagram
		String item = event.getDragboard().getString();
		if (itemsList.getItems().contains(item)) {
			if (addItemToDiagram(event.getX(), event.getY(), item)) {
				itemsList.getItems().remove(item);
			}
		} else {
			// Drag a file onto the diagram to import it
			try {
				@SuppressWarnings("unchecked")
				List<File> dragged = (ArrayList<File>) event.getDragboard().getContent(DataFormat.FILES);
				for (File file : dragged) {
					if (file.getName().endsWith(".csv")) {
						doTheCSVImport(file);
					} else if (file.getName().endsWith(".venn")) {
						if (changesMade) {
							Alert a = new Alert(AlertType.CONFIRMATION);
							a.setHeaderText("Are you sure you want to open another file?");
							a.setContentText("You will lose any unsaved changes");
							a.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);
							Optional<ButtonType> result = a.showAndWait();
							if (result.get() == ButtonType.OK) {
								doTheLoad(file);
							}
						} else {
							doTheLoad(file);
						}
					} else if (file.getName().endsWith(".vansr")) {
						doTheAnswerImport(file);
					} else if (acceptedImageTypes.contains(file.getName().substring(file.getName().lastIndexOf(".")))) {
						doTheImageImport(file, event.getX(), event.getY());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Drag import failed");
				Alert a = new Alert(AlertType.ERROR);
				a.setHeaderText("File could not be imported");
				a.setContentText("");
				a.setTitle("Error");
				a.show();
			}
			event.setDropCompleted(true);
			event.consume();
		}
	}

	boolean addItemToDiagram(double x, double y, String text) {
		// Returns true if item was added, false otherwise
		DraggableItem a = null;
		// If an image by this name exists and is not already in the diagram, add the image to the diagram instead of the text
		if (new File(tempPath + "imgs" + File.separatorChar + text).exists() && !imagesInDiagram.contains(text)) {
			return addImageToDiagram(x, y, text, new File(tempPath + "imgs" + File.separatorChar + text));
		} else {
			// Otherwise just add the text to the diagram
			a = new DraggableItem(x, y, text);
			if (a.checkBounds()) {
				diagram.getChildren().add(a);
				itemsInDiagram.add(a);
				a.toFront();
				changesMade(new AddItemAction(a, x, y, itemsInDiagram, itemsList));
				return true;
			} else {
				return false;
			}
		}
	}
	
	boolean addImageToDiagram(double x, double y, String title, File imageFile) {
		// Returns true if item was added, false otherwise
		DraggableImage a = new DraggableImage(x, y, title, imageFile);
		if (a.checkBounds()) {
			diagram.getChildren().add(a);
			itemsInDiagram.add(a);
			imagesInDiagram.add(title);
			a.toFront();
			changesMade(new AddItemAction(a, x, y, itemsInDiagram, itemsList, imagesInDiagram));
			return true;
		} else {
			return false;
		}
	}

	@FXML
	private void selectAll() {
		for (DraggableItem d : itemsInDiagram) {
			multiSelect = true;
			d.requestFocus();
		}
		multiSelect = false;
	}

	@FXML
	private void showAboutWindow() {
		Alert a = new Alert(AlertType.NONE);
		a.setTitle("About Venn");
		a.setHeaderText("Venn " + VERSION);
		Label credits = new Label("Credits");
		credits.setStyle("-fx-underline: true;");
		credits.setScaleX(1.2);
		credits.setScaleY(1.2);
		credits.setPadding(new Insets(0, 0, 7, 4));
		Hyperlink thoseicons = new Hyperlink("ThoseIcons on FlatIcon.com");
		
		// For the record, I put this in here over two weeks before handing in this project.
		// If either Nabi or Anika looked at any of the changes made, they would have seen this.
		// Is this petty? Absolutely. Did they do anything to help with this project?
		// Absolutely not. So do I feel guilty? Absolutely not. They earned their F's.
		// So now they're getting credit where credit is due.
		GridPane pettiness = new GridPane();
		for (Node n : pettiness.getChildren()) {
			GridPane.setHgrow(n, Priority.SOMETIMES);
		}

		GridPane content = new GridPane();
		content.add(credits, 0, 0);
		content.add(new Label("Lead Developer:"), 0, 1);		content.add(new Label("  Andrew Hocking"), 1, 1);
		content.add(new Label("Assistant Developer:"), 0, 2);	content.add(new Label("  Nourin Abd El Hadi"), 1, 2);
		content.add(new Label("Documentation:"), 0, 3);			content.add(new Label("  Nabi Khalid"), 1, 3);
																content.add(new Label("  Anika Prova"), 1, 4);
		content.add(new Label("Unit testing:"), 0, 5);			content.add(new Label("  Andrew Hocking"), 1, 5);
		content.add(new Label("App icon:"), 0, 6);				content.add(new Label("  Andrew Hocking"), 1, 6);
		content.add(new Label("Toolbar icon images:"), 0, 7);	content.add(thoseicons, 1, 7);
																content.add(new Label("  Andrew Hocking"), 1, 8);
		for (Node n : content.getChildren()) {
			GridPane.setHgrow(n, Priority.SOMETIMES);
		}
		
		a.getDialogPane().setContent(content);
		pettiness.add(new Label(" Corrected Documentation:"), 0, 0);	pettiness.add(new Label("\t\tNourin Abd El Hadi"), 1, 0);
																		pettiness.add(new Label("\t\tAndrew Hocking"), 1, 1);
		pettiness.add(new Label(" Zero Usable Code:"), 0, 2);			pettiness.add(new Label("\t\tNabi Khalid"), 1, 2);
																		pettiness.add(new Label("\t\tAnika Prova"), 1, 3);
		a.getDialogPane().setExpandableContent(pettiness);
		a.getDialogPane().setExpanded(false);
		
		ButtonType githubButton = new ButtonType("View Source Code");
		a.getButtonTypes().add(ButtonType.CLOSE);
		a.getButtonTypes().add(githubButton);
		ImageView iconView = new ImageView(new Image(getClass().getResource("images" + File.separatorChar + "icon.png").toExternalForm()));
		iconView.setPreserveRatio(true);
		iconView.setFitWidth(100);
		a.setGraphic(iconView);
		Button github = (Button) a.getDialogPane().lookupButton(githubButton);
		Button close = (Button) a.getDialogPane().lookupButton(ButtonType.CLOSE);
		close.setDefaultButton(true);
		thoseicons.setOnAction(event -> {
			try {
				java.awt.Desktop.getDesktop().browse(new URI("https://www.flaticon.com/authors/those-icons"));
			} catch (Exception e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Link could not be opened");
				alert.setContentText("");
				alert.setTitle("Error");
				alert.show();
				a.close();
			}
			event.consume();
		});
		github.setOnAction(event -> {
			try {
				java.awt.Desktop.getDesktop().browse(new URI("https://github.com/nourinjh/EECS2311/"));
			} catch (Exception e) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setHeaderText("Link could not be opened");
				alert.setContentText("");
				alert.setTitle("Error");
				alert.show();
				a.close();
			}
			event.consume();
		});
		close.setOnAction(event -> {
			a.close();
			event.consume();
		});
		a.setWidth(515);
		a.getDialogPane().setMinWidth(515);
//		a.getDialogPane().setMinSize(480, 275);
//		a.getDialogPane().setMaxSize(480, 350);
		a.show();
		a.setResizable(false);
	}
	
	// This method is called by the FXMLLoader when initialization is complete
	@FXML
	private void initialize() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					File temp = File.createTempFile("venn", null);
					tempPath = temp.getParent() + File.separatorChar;
					temp.delete();
					File f = new File(tempPath + "imgs" + File.separatorChar);
					f.mkdir();
					f.deleteOnExit();
				} catch (IOException e) {
					System.err.println("Temp directory could not be created. Using current directory instead.");
					tempPath = "";
					e.printStackTrace();
				}
				pane.requestFocus();
				toolBar.toFront();
				menuBar.toFront();
				doTheNew();
				leftSizeField.setAlignment(Pos.CENTER);
				rightSizeField.setAlignment(Pos.CENTER);
				diagram.setOnMouseReleased(mouseEvent -> {
					diagram.getScene().setCursor(Cursor.DEFAULT);
					mouseEvent.consume();
				});
								
//				XXX: Crashes JUnit test because there's no real "window" with TestFX
				pane.getScene().getWindow().setOnCloseRequest(event -> {
					if (changesMade) {
						Alert a = new Alert(AlertType.CONFIRMATION);
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
						Optional<ButtonType> result = a.showAndWait();
						if (result.get().equals(dontSaveButton)) {
							event.consume();
							try {
								Main.primaryStage.close();
							} catch (Exception e) {
								System.err.println("Main.primaryStage not found");
							}
						} else if (result.get().equals(saveButton)) {
							save();
							event.consume();
							try {
								Main.primaryStage.close();
							} catch (Exception e) {
								System.err.println("Main.primaryStage not found");
							}
						} else {
							event.consume();
						}
						a.getButtonTypes().removeAll(saveButton, dontSaveButton, cancelButton);
					} else {
						event.consume();
						try {
							Main.primaryStage.close();
						} catch (Exception e) {
							System.err.println("Main.primaryStage not found");
						}
					}
				});
				
								
//				XXX: Crashes JUnit test because there's no real "window" with TestFX
				try {
					double width = Math.min(Toolkit.getDefaultToolkit().getScreenSize().width - 100, Main.primaryStage.getMaxWidth());
					double height = Math.min(Toolkit.getDefaultToolkit().getScreenSize().height - 100, Main.primaryStage.getMaxHeight());
					Main.primaryStage.setWidth(width);
					Main.primaryStage.setHeight(height);
					Main.primaryStage.getScene().getWindow().centerOnScreen();
				} catch (Exception e) {
					System.err.println("Main.primaryStage not found");
				}
				acceptedImageTypes.add(".png");
				acceptedImageTypes.add(".jpg");
				acceptedImageTypes.add(".jpeg");
				acceptedImageTypes.add(".gif");
				acceptedImageTypes.add(".bmp");
			}
		});
				
		leftSizeField.focusedProperty().addListener((observable, hadFocus, hasFocus) -> {
			if (!hasFocus.booleanValue()) {
				try {
					double d = Double.parseDouble(leftSizeField.getText());
					if (d / 100 != leftScale) {
						d = d < 75 ? 75 : d > 120 ? 120 : d;
						leftSizeSlider.setValue(d);
						changeSizeLeft();
						changesMade(new ChangeCircleSizeAction(circleLeft, leftScale, leftSizeSlider, leftSizeField));
						leftScale = leftSizeSlider.getValue() / 100;
					}
				} catch (Exception e) {
					leftSizeField.setText(String.format("%.0f", leftSizeSlider.getValue()));
				}
			}
		});
		
		rightSizeField.focusedProperty().addListener((observable, hadFocus, hasFocus) -> {
			if (!hasFocus.booleanValue()) {
				try {
					double d = Double.parseDouble(rightSizeField.getText());
					if (d / 100 != rightScale) {
						d = d < 75 ? 75 : d > 120 ? 120 : d;
						rightSizeSlider.setValue(d);
						changeSizeRight();
						changesMade(new ChangeCircleSizeAction(circleRight, rightScale, rightSizeSlider, rightSizeField));
						rightScale = rightSizeSlider.getValue() / 100;
					}
				} catch (Exception e) {
					rightSizeField.setText(String.format("%.0f", rightSizeSlider.getValue()));
				}
			}
		});
				
		title.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.SHORTCUT || event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.CONTROL || event.getCode() == KeyCode.COMMAND) {
				removeFocus();
			}
		});
		
		title.setOnKeyReleased(event -> {
			if (!title.textProperty().getValueSafe().contentEquals(titleText)) {
				String newText = title.textProperty().getValueSafe();
				if (newText.length() <= 30) {
					changesMade(new TypeInFieldAction(title, titleText, newText));
					titleText = newText;
				} else {
					int pos = title.getCaretPosition();
					title.setText(titleText);
					title.positionCaret(pos - 1);
				}
			}
		});
		
		circleLeftTitle.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.SHORTCUT || event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.CONTROL || event.getCode() == KeyCode.COMMAND) {
				removeFocus();
			}
		});
		
		circleLeftTitle.setOnKeyReleased(event -> {
			if (!circleLeftTitle.textProperty().getValueSafe().contentEquals(leftText)) {
				String newText = circleLeftTitle.textProperty().getValueSafe();
				if (newText.length() <= 26) {
					changesMade(new TypeInFieldAction(circleLeftTitle, leftText, newText));
					leftText = newText;
				} else {
					int pos = circleLeftTitle.getCaretPosition();
					circleLeftTitle.setText(leftText);
					circleLeftTitle.positionCaret(pos - 1);
				}
			}
		});
		
		circleRightTitle.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.SHORTCUT || event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.CONTROL || event.getCode() == KeyCode.COMMAND) {
				removeFocus();
			}
		});
		
		circleRightTitle.setOnKeyReleased(event -> {
			if (!circleRightTitle.textProperty().getValueSafe().contentEquals(rightText)) {
				String newText = circleRightTitle.textProperty().getValueSafe();
				if (newText.length() <= 26) {
					changesMade(new TypeInFieldAction(circleRightTitle, rightText, newText));
					rightText = newText;
				} else {
					int pos = circleRightTitle.getCaretPosition();
					circleRightTitle.setText(rightText);
					circleRightTitle.positionCaret(pos - 1);
				}
			}
		});
		
		addItemField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.SHORTCUT || event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.CONTROL || event.getCode() == KeyCode.COMMAND) {
				removeFocus();
			} else if (event.getCode() == KeyCode.ENTER) {
				addItemToList();
			}
		});
		
		addItemField.setOnKeyReleased(event -> {
			if (!addItemField.textProperty().getValueSafe().contentEquals(addFieldText)) {
				String newText = addItemField.textProperty().getValueSafe();
				changesMade(new TypeInFieldAction(addItemField, addFieldText, newText));
				addFieldText = newText;
			}
		});
		
		scrollPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
			@Override
			public void changed(ObservableValue<? extends Bounds> observableValue, Bounds oldBounds, Bounds newBounds) {
				scrollContent.setPrefSize(Math.max(diagram.getBoundsInParent().getMaxX(), newBounds.getWidth()),
						Math.max(diagram.getBoundsInParent().getMaxY(), newBounds.getHeight()));
			}
		});
				
		leftSizeSlider.setOnMouseClicked(event -> {
			leftScale = circleLeft.getScaleX();
			Controller.trackChanges = false;
		});
		
		rightSizeSlider.setOnMouseClicked(event -> {
			rightScale = circleRight.getScaleX();
			Controller.trackChanges = false;
		});
		
		leftSizeSlider.setOnMouseReleased(event -> {
			Controller.trackChanges = true;
			changeSizeLeft();
			changesMade(new ChangeCircleSizeAction(circleLeft, leftScale, leftSizeSlider, leftSizeField));
			leftScale = leftSizeSlider.getValue() / 100;
		});
		
		rightSizeSlider.setOnMouseReleased(event -> {
			Controller.trackChanges = true;
			changeSizeRight();
			changesMade(new ChangeCircleSizeAction(circleRight, rightScale, rightSizeSlider, rightSizeField));
			rightScale = rightSizeSlider.getValue() / 100;
		});
		
		selectedItems.addListener(new ListChangeListener<DraggableItem>() {
	        @Override
	        public void onChanged(ListChangeListener.Change<? extends DraggableItem> c) {
	        	if (selectedItems.size() == 1) {
					removeMenu.setText("Remove Selected Item from Diagram");
					removeButton.setTooltip(new Tooltip("Remove Selected Item from Diagram"));
	        	} else {
					removeMenu.setText("Remove Selected Items from Diagram");
					removeButton.setTooltip(new Tooltip("Remove Selected Items from Diagram"));
	        	}
	        	if (selectedItems.size() < 1) {
	        		removeButton.setDisable(true);
	        		removeMenu.setDisable(true);
	        	} else {
	        		removeButton.setDisable(false);
	        		removeMenu.setDisable(false);
	        	}
        		if (itemsList.getSelectionModel().getSelectedItems().size() + selectedItems.size() == 1) {
					deleteMenu.setText("Delete Selected Item");
					deleteButton.setTooltip(new Tooltip("Delete Selected Item"));
        		} else {
					deleteMenu.setText("Delete Selected Items");
					deleteButton.setTooltip(new Tooltip("Delete Selected Items"));
        		}
	        	if (itemsList.getSelectionModel().getSelectedItems().size() + selectedItems.size() < 1) {
	        		deleteButton.setDisable(true);
	        		deleteMenu.setDisable(true);
	        	} else {
	        		deleteButton.setDisable(false);
	        		deleteMenu.setDisable(false);
	        	}
	        }
	    });
		
		itemsList.getSelectionModel().selectedIndexProperty().addListener(listener -> {
    		if (itemsList.getSelectionModel().getSelectedItems().size() + selectedItems.size() == 1) {
				deleteMenu.setText("Delete Selected Item");
				deleteButton.setTooltip(new Tooltip("Delete Selected Item"));
    		} else {
				deleteMenu.setText("Delete Selected Items");
				deleteButton.setTooltip(new Tooltip("Delete Selected Items"));
    		}
        	if (itemsList.getSelectionModel().getSelectedItems().size() + selectedItems.size() < 1) {
        		deleteButton.setDisable(true);
        		deleteMenu.setDisable(true);
        	} else {
        		deleteButton.setDisable(false);
        		deleteMenu.setDisable(false);
        	}
		});		
		
		addItemField.focusedProperty().addListener(listener -> {
			itemsList.getSelectionModel().clearSelection();
		});
		
		addItemField.textProperty().addListener(listener -> {
			
		});

		for (Node n : pane.getChildren()) {
			n.setFocusTraversable(false);
		}
		for (Node n : diagram.getChildren()) {
			n.setFocusTraversable(false);
		}
		
		try {
//		XXX: Crashes JUnit test because there's no real "window" with TestFX
			 Main.primaryStage.setMinWidth(800);
			 Main.primaryStage.setMinHeight(400);
			 Main.primaryStage.setMaxWidth(1280);
			 Main.primaryStage.setMaxHeight(822);
		} catch (Exception e) {
			System.err.println("Main.primaryStage not found");
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
