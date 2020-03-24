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

import java.io.*;
import java.net.URI;
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
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller {

	private final String DEFAULT_BACKGROUND_COLOR = "0x1d1d1d";
	private final String DEFAULT_TITLE_COLOR = "0xffffff";
	private final String DEFAULT_LEFT_COLOR = "0xb47a7a";
	private final String DEFAULT_RIGHT_COLOR = "0xb4b162";
	private final String DEFAULT_INTERSECTION_COLOR = "0x4594e3";
	private final String DEFAULT_LEFT_ITEM_COLOR = "0xffffff";
	private final String DEFAULT_RIGHT_ITEM_COLOR = "0xffffff";
	private final String DEFAULT_INTERSECTION_ITEM_COLOR = "0xffffff";
	private final double DEFAULT_CIRCLE_OPACTIY = 0.8;

	private boolean multiSelect = false;
	private boolean changesMade = false;
	private boolean answerKeyWasImported = false;
	private boolean answersAreShowing = false;

	private ObservableList<String> items = FXCollections.observableArrayList();
	private ObservableList<DraggableItem> itemsInDiagram = FXCollections.observableArrayList();
	private static ObservableList<DraggableItem> selectedItems = FXCollections.observableArrayList();
	

	private List<String> leftItemsAnswers = new ArrayList<String>();
	private List<String> rightItemsAnswers = new ArrayList<String>();
	private List<String> intersectionItemsAnswers = new ArrayList<String>();
	private List<String> unassignedItemsAnswers = new ArrayList<String>();

	@FXML
	private Circle circleLeft;
	@FXML
	private Circle circleRight;
	private Shape circleIntersection;

	@FXML
	private AnchorPane pane;
	@FXML
	private ScrollPane scrollPane;

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
	private Pane frameRect;

	@FXML
	private ColorPicker colorBackground;
	@FXML
	private ColorPicker colorTitles;

	@FXML
	private MenuItem deleteItemMenu;
	@FXML
	private Button deleteButton;

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
	private Button checkAnswersButton;

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

	private class DraggableItem extends StackPane {
		protected Label text = new Label();
		protected String description = "";
		private Color color;
		protected ImageView answerImage = new ImageView();
		private final int MAX_WIDTH = 120;
		protected char circle;

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
//			this.text.setVisible(false);
//			this.setImage("images/icon100.png");

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
					if (selectedItems.size() != 1) {
						deleteItemMenu.setText("Delete Selected Items");
						deleteButton.setTooltip(new Tooltip("Delete Selected Items"));
					} else {
						deleteItemMenu.setText("Delete Selected Item");
						deleteButton.setTooltip(new Tooltip("Delete Selected Item"));
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
			
			this.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2) {
					Alert a = new Alert(AlertType.INFORMATION);

					// Create expandable Exception.
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
					
					a.setTitle("Item details");
					String t = this.getText().length() > 50 ? this.getText().substring(0, 50) + "..." : this.getText();
					
					a.setHeaderText("Item details for \"" + t + "\":");
					ButtonType saveButton = new ButtonType("Save", ButtonData.OK_DONE);
					a.getButtonTypes().setAll(ButtonType.CANCEL, saveButton);
					Button save = (Button) a.getDialogPane().lookupButton(saveButton);
					Button cancel = (Button) a.getDialogPane().lookupButton(ButtonType.CANCEL);
					textField.textProperty().addListener(listener -> {
						if (textField.textProperty().getValue().equals("")) {
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
						changesMade();
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

		public void setColor(Color c) {
			this.text.setTextFill(c);
			this.color = c;
			changesMade();
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
				boolean deleteThis = false;
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
							d.getScene().setCursor(Cursor.HAND);
							d.checkBounds();
							mouseEvent.consume();
						});
						d.setBackground(null);
						d.getLabel().setTextFill(d.getColor());
						d.getScene().setCursor(Cursor.CLOSED_HAND);
					} else {
						if (d != this) {
							setOnMouseReleased(mouseEvent2 -> {
								d.getScene().setCursor(Cursor.DEFAULT);
								frameRect.getChildren().remove(d);
								itemsInDiagram.remove(d);
								itemsList.getItems().add(d.getText());
							});
							d.setBackground(
									new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(5))));
							d.getLabel().setTextFill(Color.WHITE);
							d.getScene().setCursor(Cursor.DISAPPEAR);
						} else {
							deleteThis = true;
						}
					}
				}
				if (deleteThis) {
					this.setBackground(
							new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(5))));
					this.getLabel().setTextFill(Color.WHITE);
					this.getScene().setCursor(Cursor.DISAPPEAR);
					this.setOnMouseReleased(mouseEvent2 -> {
						this.getScene().setCursor(Cursor.DEFAULT);
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

		private class Delta {
			double x;
			double y;
		}
	}

	private class DraggableImage extends DraggableItem {
		ImageView image = new ImageView();
		
		public DraggableImage (double x, double y, String title, Image image) {
			super(x, y, title);
			this.image.setImage(image);
			this.image.setPreserveRatio(true);
			this.image.setFitWidth(100);
			this.text.setVisible(false);
			this.text.setMaxWidth(image.getWidth());
			this.text.setWrapText(false);
			this.getChildren().add(this.image);
		}
		
		@Override
		public boolean checkBounds() {
			changesMade();
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
				this.setBackground(null);
				result = true;
			} else if (distanceToLeft <= circleLeft.getRadius() * circleLeft.getScaleX()) {
				this.setColor(colorLeftItems.getValue());
				this.circle = 'l';
				this.setBackground(null);
				result = true;
			} else if (distanceToRight <= circleRight.getRadius() * circleRight.getScaleX()) {
				this.setColor(colorRightItems.getValue());
				this.circle = 'r';
				this.setBackground(null);
				result = true;
			} else {
				this.setColor(Color.WHITE);
				this.circle = 'x';
				this.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(0), new Insets(5))));
				result = false;
			}
			return result;
		}
		
		public void scaleImage(double value) {
			this.image.setFitWidth(this.image.getImage().getWidth() * value);
			this.image.autosize();
		}
	}
	
	private void changesMade() {
		if (!changesMade && openFile != null)
			Main.primaryStage.setTitle(openFile.getName() + " (Edited) - Venn");
		changesMade = true;
//		redoStack.clear();
	}

	@FXML
	private void undo() {
		System.out.println("Undo");
		changesMade();
	}

	@FXML
	private void redo() {
		System.out.println("Redo");
		changesMade();
	}

	@FXML
	private void zoomIn() {
		if (frameRect.getScaleX() < 2) {
			frameRect.setScaleX(frameRect.getScaleX() + 0.1);
			frameRect.setScaleY(frameRect.getScaleY() + 0.1);
			scrollPane.setVvalue(scrollPane.getVmax()/2.0 + 0.05);
			scrollPane.setHvalue(scrollPane.getHmax()/2.0);
			updateIntersection();
		}
	}

	@FXML
	private void zoomOut() {
		if (frameRect.getScaleX() > 0.5) {
			frameRect.setScaleX(frameRect.getScaleX() - 0.1);
			frameRect.setScaleY(frameRect.getScaleY() - 0.1);
			updateIntersection();
		}
	}
	
	private boolean rectTooBig() {
		double width = Main.primaryStage.getWidth();
		double height = Main.primaryStage.getHeight();
		return (width < floatingMenu.getBoundsInParent().getWidth() + frameRect.getBoundsInParent().getWidth() + 50
				|| height < menuBar.getBoundsInParent().getHeight() + toolBar.getBoundsInParent().getHeight() + frameRect.getBoundsInParent().getHeight() + 50);
	}
	
	private boolean rectTooSmall() {
		double width = Main.primaryStage.getWidth();
		double height = Main.primaryStage.getHeight();
		return (width > floatingMenu.getBoundsInParent().getWidth() + frameRect.getBoundsInParent().getWidth() + 50
				|| height > menuBar.getBoundsInParent().getHeight() + toolBar.getBoundsInParent().getHeight() + frameRect.getBoundsInParent().getHeight() + 50);
	}
	
	@FXML
	private void zoomActualSize() {
		frameRect.setScaleX(1);
		frameRect.setScaleY(1);
		if (rectTooBig()) {
			while (rectTooBig()) {
				zoomOut();
				frameRect.setLayoutX(frameRect.getLayoutX() + 200);
			}
		}
		else if (rectTooSmall()) {
			while (rectTooSmall()) {
				zoomIn();
				frameRect.setLayoutX(frameRect.getLayoutX() - 200);
			}
		}
	}

	@FXML
	private void addItemToList() {
		String newItem = addItemField.getText();
		if (!(newItem.equals("") || itemsList.getItems().contains(newItem))) {
			itemsList.getItems().add(newItem);
			addItemField.setText("");
		}
		changesMade();
	}

	@FXML
	private void addItemWithEnter(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			addItemToList();
		}
		changesMade();
	}

	@FXML
	private void dragFromItemsList() {
		Dragboard dragBoard = itemsList.startDragAndDrop(TransferMode.ANY);
		ClipboardContent content = new ClipboardContent();
		content.putString(itemsList.getSelectionModel().getSelectedItem());
		dragBoard.setContent(content);
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
		dragBoard.setDragView(tempLabel.snapshot(null, null));
		dragBoard.setDragViewOffsetX(tempLabel.getWidth() / 2);
		dragBoard.setDragViewOffsetY(-tempLabel.getHeight() / 2);
		pane.getChildren().remove(tempLabel);
		changesMade();
	}

	@FXML
	private void deleteItem() {
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
		try {
			@SuppressWarnings("unchecked")
			List<File> dragged = (ArrayList<File>) event.getDragboard().getContent(DataFormat.FILES);
			for (File file : dragged) {
				System.out.println(file.getName().substring(file.getName().length() - 4));
				if (file.getName().length() > 4
						&& file.getName().substring(file.getName().length() - 4).equals(".csv")) {
					try {
						doTheCSV(file);
						System.out.println("Imported");
					} catch (Exception e) {
						System.out.println("Drag import failed");
					}
				}
			}
		} catch (Exception e) {
		}
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
		event.setDropCompleted(true);
		event.consume();
		changesMade();
	}

	@FXML
	private void takeScreenshot() {
		boolean hadChanges = changesMade;
		removeFocus();
		String mainTitle = title.getText() + "";
		String leftTitle = circleLeftTitle.getText() + "";
		String rightTitle = circleRightTitle.getText() + "";
		double scale = frameRect.getScaleX();
		double menuX = floatingMenu.getLayoutX();
		toolBar.setVisible(false);
		try {
			frameRect.setScaleX(1);
			frameRect.setScaleY(1);
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
			
			double pixelScale = 2.0;
		    WritableImage writableImage = new WritableImage((int)Math.rint(pixelScale*frameRect.getWidth()), (int)Math.rint(pixelScale*frameRect.getHeight()));
		    SnapshotParameters spa = new SnapshotParameters();
		    spa.setFill(colorBackground.getValue());
		    spa.setTransform(Transform.scale(pixelScale, pixelScale));
		    WritableImage capture = frameRect.snapshot(spa, writableImage);     
			ImageIO.write(SwingFXUtils.fromFXImage(capture, null), "png", selectedFile);
		} catch (IllegalArgumentException e) {
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("File could not be saved");
			a.setContentText("");
			a.setTitle("Error");
			a.show();
		}
		frameRect.setScaleX(scale);
		frameRect.setScaleY(scale);
		floatingMenu.setLayoutX(menuX);
		title.setText(mainTitle);
		circleLeftTitle.setText(leftTitle);
		circleRightTitle.setText(rightTitle);
		changesMade = hadChanges;
		toolBar.setVisible(true);
	}

	@FXML
	private void clearDiagram() {
		for (DraggableItem d : itemsInDiagram) {
			itemsList.getItems().add(d.getText());
			frameRect.getChildren().remove(d);
		}
		itemsInDiagram.clear();
		changesMade();
	}

	private void doTheSave(File selectedFile) {

		// TODO: Add imported images to this once implemented

		// Hierarchy of a .venn file:
		// . Diagram.venn:
		// ... Config.vlist:
		// ..... (0) Title, (1) Titles color, (2) Background color
		// ..... (0) Left circle title, (1) Left circle color, (2) Left circle scale,
		// (3) Item text color
		// ..... (0) Right circle title, (1) Right circle color, (2) Right circle scale,
		// (3) Item text color
		// ..... (0) Intersection color, (1) Intersection item text color
		// ... Unassigned.csv:
		// ..... Unassigned items separated by new lines
		// ... InDiagram.vlist
		// ..... (0) Item text, (1) item x, (2) item y, (3) item description, (4) text
		// color

		// Encryption?
		//		String test = "abcdefg";
		//		StringBuilder sb = new StringBuilder(test);
		//		for (int i = 0; i < test.length(); i++) {
		//			sb.setCharAt(i, (char) ((((sb.charAt(i) + 7) * 12) - 6) * 21));
		//		}
		//		test = sb.toString();
		//		System.out.println(test);
		//		sb = new StringBuilder(test);
		//		for (int i = 0; i < test.length(); i++) {
		//			sb.setCharAt(i, (char) ((((sb.charAt(i) / 21) + 6) / 12) - 7));
		//		}
		//		test = sb.toString();
		//		System.out.println(test);

		try {
			FileOutputStream fos = new FileOutputStream(selectedFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			File config = new File("Config.vlist");
			StringBuilder sb = new StringBuilder();
			sb.append(title.getText() + "𔓱" + colorTitles.getValue().toString() + "𔓱"
					+ colorBackground.getValue().toString() + "\n");
			sb.append(circleLeftTitle.getText() + "𔓱" + colorLeft.getValue().toString() + "𔓱" + circleLeft.getScaleX()
					+ "𔓱" + colorLeftItems.getValue().toString() + "\n");
			sb.append(circleRightTitle.getText() + "𔓱" + colorRight.getValue().toString() + "𔓱"
					+ circleRight.getScaleX() + "𔓱" + colorRightItems.getValue().toString() + "\n");
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
				sb.append(d.getText() + "𔓱" + d.getLayoutX() + "𔓱" + d.getLayoutY() + "𔓱" + d.getDescription() + "𔓱"
						+ d.getLabel().getTextFill().toString());
				if (i != itemsInDiagram.size() - 1) {
					sb.append("𔓱𔓱𔓱");
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
		changesMade();
		return list;
	}

	private void doTheLoad() {

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

			fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Venn files (*.venn)", "*.venn"));
			file = fc.showOpenDialog(pane.getScene().getWindow());
			String line, title, leftTitle, rightTitle, elements[];
			Color bgColor, leftColor, rightColor, intersectionColor, titleColor, leftTextColor, rightTextColor,
					intersectionTextColor;
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
			for (String s : items) {
				elements = s.split("𔓱");
				DraggableItem a = new DraggableItem(Double.parseDouble(elements[1]) + 5, Double.parseDouble(elements[2]) + 5,
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
			// FIXME: Crashes the JUnit tests because they don't have a title bar on the
			// window to change
			Main.primaryStage.setTitle(openFile.getName() + " - Venn");
			hideAnswers();
			clearAnswerKey();
		} catch (Exception e) {
			if (file != null) {
				Alert a = new Alert(AlertType.ERROR);
				a.setHeaderText("File could not be opened");
				a.setContentText("");
				a.setTitle("Error");
				a.show();
			}
		}
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
				doTheLoad();
			}
		} else {
			doTheLoad();
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
		hideAnswersButtonImage.setImage(new Image(getClass().getResource("images/hide.png").toExternalForm()));
		hideAnswersButton.setOnMouseClicked(event -> {
			hideAnswersButtonImage.setImage(new Image(getClass().getResource("images/show.png").toExternalForm()));
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
	private void openManual() {
		try {
			// TODO: Change link to online manual in future release
			java.awt.Desktop.getDesktop().browse(new URI("https://github.com/nourinjh/EECS2311/"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void changeColorLeft() {
		circleLeft.setFill(colorLeft.getValue());
		circleLeft.setOpacity(DEFAULT_CIRCLE_OPACTIY);
		changesMade();
	}

	@FXML
	private void changeColorItems() {
		for (DraggableItem d : itemsInDiagram) {
			d.checkBounds();
		}
		changesMade();
	}

	@FXML
	private void changeColorRight() {
		circleRight.setFill(colorRight.getValue());
		circleRight.setOpacity(DEFAULT_CIRCLE_OPACTIY);
		changesMade();
	}

	@FXML
	private void changeColorBackground() {
		pane.setStyle("-fx-background-color: #"
				+ colorBackground.getValue().toString().substring(2, colorBackground.getValue().toString().length() - 2)
				+ ";");
		changeColorTitles();
		changesMade();
	}

	@FXML
	private void changeColorTitles() {
		title.setStyle("-fx-background-color: transparent;\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2) + ";");
		circleLeftTitle.setStyle("-fx-background-color: transparent;\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2) + ";");
		circleRightTitle.setStyle("-fx-background-color: transparent;\n-fx-text-fill: #"
				+ colorTitles.getValue().toString().substring(2, colorTitles.getValue().toString().length() - 2) + ";");
		changesMade();
	}

	@FXML
	private void changeSizeLeft() {
		circleLeft.setScaleX(leftSizeSlider.getValue() / 100.0);
		circleLeft.setScaleY(leftSizeSlider.getValue() / 100.0);
		leftSizeField.setText(String.format("%.0f", leftSizeSlider.getValue()));
		updateIntersection();
		changeColorItems();
		changesMade();
	}

	@FXML
	private void updateIntersection() {
//		frameRect.getChildren().remove(circleIntersection);
//		circleIntersection = Shape.intersect(circleLeft, circleRight);
//		circleIntersection.setFill(colorIntersection.getValue());
//		circleIntersection.setOnDragDropped(event -> {
//			dropItem(event);
//		});
//		circleIntersection.mouseTransparentProperty().set(true);
//		circleIntersection.setLayoutX(circleLeft.getCenterX() - 408);
//		circleIntersection.setLayoutY(circleLeft.getCenterY() - 103);
//		circleIntersection.setOpacity(0.8);
//		frameRect.getChildren().add(circleIntersection);
//		for (DraggableItem d : itemsInDiagram) {
//			d.toFront();
//		}
//		changeColorItems();
//		changesMade();
	}

	@FXML
	private void changeSizeLeftField(KeyEvent event) {
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
	private void changeSizeRight() {
		circleRight.setScaleX(rightSizeSlider.getValue() / 100.0);
		circleRight.setScaleY(rightSizeSlider.getValue() / 100.0);
		rightSizeField.setText(String.format("%.0f", rightSizeSlider.getValue()));
		updateIntersection();
		changeColorItems();
		changesMade();
	}

	@FXML
	private void changeSizeRightField(KeyEvent event) {
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
			List<String> extensions = new ArrayList<String>();
			extensions.add("*.png");
			extensions.add("*.jpg");
			extensions.add("*.jpeg");

			ExtensionFilter csvFilter = new ExtensionFilter("CSV files (*.csv)", "*.csv");
			ExtensionFilter ansFilter = new ExtensionFilter("Venn Answer Key files (*.vansr)", "*.vansr");
			ExtensionFilter imgFilter = new ExtensionFilter("Image files (*.png, *.jpg, *.jpeg)", extensions);
			fc.getExtensionFilters().add(csvFilter);
			fc.getExtensionFilters().add(imgFilter);
			fc.getExtensionFilters().add(ansFilter);

			if (type.equals("csv")) {
				fc.setSelectedExtensionFilter(csvFilter);
			} else if (type.equals("img")) {
				fc.setSelectedExtensionFilter(imgFilter);
			} else /* if (type.equals("ans")) */ {
				fc.setSelectedExtensionFilter(ansFilter);
			}

			File file = fc.showOpenDialog(pane.getScene().getWindow());
			
			if (fc.getSelectedExtensionFilter().equals(csvFilter)) {
				itemsList.getItems().addAll(doTheCSV(file));
			}
			
			else if (fc.getSelectedExtensionFilter().equals(imgFilter)) {
				addImageToDiagram(circleLeft.getBoundsInParent().getMaxX(), circleLeft.getBoundsInParent().getMaxY(), file.getName(), new Image(getClass().getResource("images/icon.png").toExternalForm()));
				for (DraggableItem d : itemsInDiagram) {
					if (d instanceof DraggableImage) {
						((DraggableImage) d).scaleImage(0.5);
					}
				}
//				System.out.println("Image: " + file.getAbsolutePath());
//				Alert a = new Alert(AlertType.INFORMATION);
//				a.setHeaderText("Feature coming soon");
//				a.setContentText(
//						"Adding images is not yet available. This feature will be coming in a future release.");
//				a.setTitle("Feature not available");
//				a.show();
			}
			
			else if (fc.getSelectedExtensionFilter().equals(ansFilter)) {
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
		} catch (NullPointerException e) {
		} catch (Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("File could not be opened");
			a.setContentText("");
			a.setTitle("Error");
			a.show();
		}
		changesMade();
	}
		
	@FXML
	private void exportCSV() {
		try {
			String name;
			if (openFile != null) {
				name = openFile.getName();
			} else if (!title.getText().equals("")) {
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
				if (!(selectedFile.getName().length() > 5 && selectedFile.getName()
						.substring(selectedFile.getName().length() - 5).toLowerCase().equals(".csv"))) {
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
			} else if (!title.getText().equals("")) {
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
				if (!(selectedFile.getName().length() > 5 && selectedFile.getName()
						.substring(selectedFile.getName().length() - 5).toLowerCase().equals(".vansr"))) {
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
				d.setAnswerImage("images/incorrect.png");
			}
		}
		
		List<String> leftItemsAnswers = new ArrayList<String>(this.leftItemsAnswers);
		List<String> rightItemsAnswers = new ArrayList<String>(this.rightItemsAnswers);
		List<String> intersectionItemsAnswers = new ArrayList<String>(this.intersectionItemsAnswers);
		List<String> unassignedItemsAnswers = new ArrayList<String>(this.unassignedItemsAnswers);
		
		for (DraggableItem d : leftItems) {
			if (leftItemsAnswers.remove(d.getText())) {
				d.setAnswerImage("images/correct.png");
			} else {
				d.setAnswerImage("images/incorrect.png");
			}
		}
		for (DraggableItem d : rightItems) {
			if (rightItemsAnswers.remove(d.getText())) {
				d.setAnswerImage("images/correct.png");
			} else {
				d.setAnswerImage("images/incorrect.png");
			}
		}
		for (DraggableItem d : intersectionItems) {
			if (intersectionItemsAnswers.remove(d.getText())) {
				d.setAnswerImage("images/correct.png");
			} else {
				d.setAnswerImage("images/incorrect.png");
			}
		}
		for (String d : items) {
			if (!unassignedItemsAnswers.remove(d)) {
				itemsList.getSelectionModel().select(d);
			}
		}
		hideAnswersButtonImage.setImage(new Image(getClass().getResource("images/hide.png").toExternalForm()));
		hideAnswersButton.setTooltip(new Tooltip("Hide Answers"));
		answersAreShowing = true;
		hideAnswersButton.setOnMouseClicked(event -> {
			hideAnswersButtonImage.setImage(new Image(getClass().getResource("images/show.png").toExternalForm()));
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
		hideAnswersButtonImage.setImage(new Image(getClass().getResource("images/show.png").toExternalForm()));
		hideAnswersButton.setTooltip(new Tooltip("Show Answers"));
		answersAreShowing = false;
		hideAnswersButton.setOnMouseClicked(event -> {
			hideAnswersButtonImage.setImage(new Image(getClass().getResource("images/hide.png").toExternalForm()));
			hideAnswersButton.setTooltip(new Tooltip("Hide Answers"));
			answersAreShowing = true;
			checkAnswers();
		});
	}

	@FXML
	private void removeFocusEnter(KeyEvent event) {
		if (event.getCode() == KeyCode.ENTER) {
			removeFocus();
		}
	}

	@FXML
	private void removeFocus() {
		pane.requestFocus();
		itemsList.getSelectionModel().clearSelection();
		selectedItems.clear();
	}

	@FXML
	private void dropItem(DragEvent event) {
		String item = event.getDragboard().getString();
		if (addItemToDiagram(event.getX(), event.getY(), item)) {
			itemsList.getItems().remove(item);
		}
		event.setDropCompleted(true);
		event.consume();
		changesMade();
	}

	boolean addItemToDiagram(double x, double y, String text) {
		DraggableItem a = new DraggableItem(x, y, text);
		if (a.checkBounds()) {
			frameRect.getChildren().add(a);
			itemsInDiagram.add(a);
			a.toFront();
			changesMade();
			return true;
		} else {
			return false;
		}
	}
	
	boolean addImageToDiagram(double x, double y, String title, Image image) {
		DraggableImage a = new DraggableImage(x, y, title, image);
		if (a.checkBounds()) {
			frameRect.getChildren().add(a);
			itemsInDiagram.add(a);
			a.toFront();
			changesMade();
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
			selectedItems.add(d);
		}
		multiSelect = false;
	}

	@FXML
	private void showAboutWindow() {
		Alert a = new Alert(AlertType.NONE);
		a.setTitle("About Venn");
		a.setHeaderText("Venn 3.0");
		Label credits = new Label("Credits");
		credits.setStyle("-fx-underline: true;");
		credits.setScaleX(1.2);
		credits.setScaleY(1.2);
		credits.setPadding(new Insets(0, 0, 7, 4));
		Label leadDev = new Label("Lead Developer:");
		Label me = new Label(" Andrew Hocking");
		Label assistDev = new Label("Assistant Developer:");
		Label nourin = new Label(" Nourin Abd El Hadi");
		Label docs = new Label("Documentation:");
		Label others = new Label(" Nabi Khalid and Anika Prova");
		Label imgs = new Label("Toolbar icon images:");
		Hyperlink thoseicons = new Hyperlink("ThoseIcons on FlatIcon.com");
		
		GridPane pettiness = new GridPane();
		Label petty1 = new Label("Corrected Documentation:");
		Label petty2 = new Label("\tNourin Abd El Hadi and Andrew Hocking");
		Label petty3 = new Label("Zero Usable Code:");
		Label petty4 = new Label("\tNabi Khalid and Anika Prova");
		for (Node n : pettiness.getChildren()) {
			GridPane.setHgrow(n, Priority.SOMETIMES);
		}

		GridPane content = new GridPane();
		content.add(credits, 0, 0);
		content.add(leadDev, 0, 1);
		content.add(me, 1, 1);
		content.add(assistDev, 0, 2);
		content.add(nourin, 1, 2);
		content.add(docs, 0, 3);
		content.add(others, 1, 3);
		content.add(imgs, 0, 4);
		content.add(thoseicons, 1, 4);
		for (Node n : content.getChildren()) {
			GridPane.setHgrow(n, Priority.SOMETIMES);
		}
		
		a.getDialogPane().setContent(content);
		pettiness.add(petty1, 0, 0);
		pettiness.add(petty2, 1, 0);
		pettiness.add(petty3, 0, 1);
		pettiness.add(petty4, 1, 1);
		a.getDialogPane().setExpandableContent(pettiness);
		a.getDialogPane().setExpanded(false);
		
		ButtonType githubButton = new ButtonType("View Source Code");
		a.getButtonTypes().add(ButtonType.CLOSE);
		a.getButtonTypes().add(githubButton);
		ImageView iconView = new ImageView(new Image(getClass().getResource("images/icon100.png").toExternalForm()));
		a.setGraphic(iconView);
		Button github = (Button) a.getDialogPane().lookupButton(githubButton);
		Button close = (Button) a.getDialogPane().lookupButton(ButtonType.CLOSE);
		close.setDefaultButton(true);
		thoseicons.setOnAction(event -> {
			try {
				java.awt.Desktop.getDesktop().browse(new URI("https://www.flaticon.com/authors/those-icons"));
			} catch (Exception e) {
			}
			event.consume();
		});
		github.setOnAction(event -> {
			try {
				java.awt.Desktop.getDesktop().browse(new URI("https://github.com/nourinjh/EECS2311/"));
			} catch (Exception e) {
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
				pane.requestFocus();
//				// FIXME: Keyboard shortcuts run twice when this is used...? Also toolbar sits too low
//				String os = System.getProperty("os.name");
//				if (os != null && os.startsWith("Mac")) {
//					menuBar.useSystemMenuBarProperty().set(true);
//					toolBar.setLayoutY(0);
//				}
				toolBar.toFront();
				menuBar.toFront();
				doTheNew();
				leftSizeField.setAlignment(Pos.CENTER);
				rightSizeField.setAlignment(Pos.CENTER);
				frameRect.setOnMouseReleased(mouseEvent -> {
					frameRect.getScene().setCursor(Cursor.DEFAULT);
					mouseEvent.consume();
				});
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
							Main.primaryStage.close();
						} else if (result.get().equals(saveButton)) {
							try {
								save();
								if (openFile == null)
									throw new Exception();
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
				Main.primaryStage.widthProperty().addListener(listener -> {
					double width = Main.primaryStage.getWidth();
					double height = Main.primaryStage.getHeight();
					if (width < floatingMenu.getBoundsInParent().getWidth() + frameRect.getBoundsInParent().getWidth() + 50
							|| height < menuBar.getBoundsInParent().getHeight() + toolBar.getBoundsInParent().getHeight() + frameRect.getBoundsInParent().getHeight() + 50) {
						zoomOut();
//						frameRect.resizeRelocate(frameRect.getLayoutX() - 100, frameRect.getLayoutY() - 100, frameRect.getWidth() * 0.9, frameRect.getHeight() * 0.9);
						updateIntersection();
					}
					else if (width > floatingMenu.getBoundsInParent().getWidth() + frameRect.getBoundsInParent().getWidth() + 50
							|| height > menuBar.getBoundsInParent().getHeight() + toolBar.getBoundsInParent().getHeight() + frameRect.getBoundsInParent().getHeight() + 50) {
						zoomIn();
//						frameRect.relocate(frameRect.getLayoutX() - 300, frameRect.getLayoutY() - 300);
//						frameRect.resizeRelocate(frameRect.getLayoutX() - 100, frameRect.getLayoutY() - 100, frameRect.getWidth() / 0.9, frameRect.getHeight() / 0.9);
						updateIntersection();
					}
				});
				Main.primaryStage.heightProperty().addListener(listener -> {
					double width = Main.primaryStage.getWidth();
					double height = Main.primaryStage.getHeight();
					if (width < floatingMenu.getBoundsInParent().getWidth() + frameRect.getBoundsInParent().getWidth() + 50
							|| height < menuBar.getBoundsInParent().getHeight() + toolBar.getBoundsInParent().getHeight() + frameRect.getBoundsInParent().getHeight() + 50) {
						zoomOut();
//						frameRect.relocate(frameRect.getLayoutX() + 300, frameRect.getLayoutY() + 300);
//						frameRect.resizeRelocate(frameRect.getLayoutX() - 100, frameRect.getLayoutY() - 100, frameRect.getWidth() * 0.9, frameRect.getHeight() * 0.9);
						updateIntersection();
					}
					else if (width > floatingMenu.getBoundsInParent().getWidth() + frameRect.getBoundsInParent().getWidth() + 50
							|| height > menuBar.getBoundsInParent().getHeight() + toolBar.getBoundsInParent().getHeight() + frameRect.getBoundsInParent().getHeight() + 50) {
						zoomIn();
//						frameRect.relocate(frameRect.getLayoutX() - 300, frameRect.getLayoutY() - 300);
//						frameRect.resizeRelocate(frameRect.getLayoutX() - 100, frameRect.getLayoutY() - 100, frameRect.getWidth() / 0.9, frameRect.getHeight() / 0.9);
						updateIntersection();
					}
				});
				Main.primaryStage.getScene().getWindow().centerOnScreen();
				
				
			}
		});
		leftSizeField.focusedProperty().addListener((observable, hadFocus, hasFocus) -> {
			if (!hasFocus.booleanValue()) {
				changeSizeLeftField(new KeyEvent(null, null, null, KeyCode.ENTER, true, true, true, true));
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

		for (Node n : pane.getChildren()) {
			n.setFocusTraversable(false);
		}
		for (Node n : frameRect.getChildren()) {
			n.setFocusTraversable(false);
		}
		Main.primaryStage.setMinWidth(800);
		Main.primaryStage.setMinHeight(400);
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
