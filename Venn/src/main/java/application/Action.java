package application;

/**
 * 
 * @author Andrew Hocking
 * 
 */

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import application.Controller.DraggableImage;
import application.Controller.DraggableItem;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

interface Action {
	
	public boolean invert();
	public String toString();
	
}

class ActionGroup implements Action {

	protected List<Action> actionList;
	protected String message;
	
	public ActionGroup(List<Action> actionList, String message) {
		this.actionList = actionList;
		this.message = message;
	}

	@Override
	public boolean invert() {
		boolean success = true;
		for (Action action : actionList) {
			System.out.println(action);
			action.invert();
		}
		return success;
	}
	
	@Override
	public String toString() {
		return message;
	}
}

class AddItemAction extends RemoveItemAction implements Action {

	public AddItemAction(DraggableItem item, double x, double y, ObservableList<DraggableItem> itemsInDiagram,
			ListView<String> itemsList) {
		this(item, x, y, itemsInDiagram, itemsList, null);
	}
	
	public AddItemAction(DraggableItem item, double x, double y, ObservableList<DraggableItem> itemsInDiagram,
			ListView<String> itemsList, ObservableList<String> imagesInDiagram) {
		super(item, x, y, itemsInDiagram, itemsList, imagesInDiagram);
		inDiagram = true;
	}
	
	@Override
	public String toString() {
		return "Add Item to Diagram";
	}
}

class AddToListAction implements Action {
	
	private String item;
	private ListView<String> itemsList;
	private TextField addItemField;
	private boolean added;

	public AddToListAction(String item, ListView<String> itemsList, TextField addItemField) {
		this.item = item;
		this.itemsList= itemsList;
		this.addItemField = addItemField;
		this.added = true;
	}

	@Override
	public boolean invert() {
		if (this.added) {
			itemsList.getItems().remove(item);
			if (addItemField != null)
				addItemField.setText(item);
			this.added = false;
			return !this.itemsList.getItems().contains(item);
		} else {
			itemsList.getItems().add(item);
			if (addItemField != null)
				addItemField.setText("");
			this.added = true;
			return this.itemsList.getItems().contains(item);
		}
	}
	
	@Override
	public String toString() {
		return "Add Item to List";
	}
}

class ChangeBackgroundColorAction implements Action {
	private Node node;
	private Color color1;
	private Color color2;
	private ColorPicker colorPicker;

	public ChangeBackgroundColorAction(Node node, Color oldColor, ColorPicker colorPicker) {
		this.node = node;
		this.color1 = colorPicker.getValue();
		this.color2 = oldColor;
		this.colorPicker = colorPicker;
	}

	@Override
	public boolean invert() {
		Color temp = color1;
		color1 = color2;
		color2 = temp;
		
		colorPicker.setValue(color1);
		node.setStyle("-fx-background-color: #"
				+ color1.toString().substring(2, color1.toString().length() - 2)
				+ ";");
		return true;
	}
	
	@Override
	public String toString() {
		return "Change Colour";
	}
}

class ChangeCircleColorAction implements Action {
	
	private Circle circle;
	private Color color1;
	private Color color2;
	private ColorPicker colorPicker;

	public ChangeCircleColorAction(Circle circle, ColorPicker colorPicker) {
		this.circle = circle;
		this.color1 = colorPicker.getValue();
		this.color2 = (Color)(circle.getFill());
		this.colorPicker = colorPicker;
	}

	@Override
	public boolean invert() {
		Color temp = color1;
		color1 = color2;
		color2 = temp;
		
		circle.setFill(color1);
		colorPicker.setValue(color1);
		return circle.getFill().equals(color1);
	}@Override
	public String toString() {
		return "Change Circle Color";
	}
}

class ChangeCircleSizeAction implements Action {
	
	private Circle circle;
	private double scale1;
	private double scale2;
	private Slider slider;
	private TextField field;

	public ChangeCircleSizeAction(Circle circle, double oldScale, Slider slider, TextField field) {
		this.circle = circle;
		this.scale1 = slider.getValue() / 100;
		this.scale2 = oldScale;
		this.slider = slider;
		this.field = field;
	}

	@Override
	public boolean invert() {
		Controller.trackChanges = false;
		double temp = scale1;
		scale1 = scale2;
		scale2 = temp;
		
		circle.setScaleX(scale1);
		circle.setScaleY(scale1);
		slider.setValue(scale1 * 100);
		field.setText(String.format("%.0f", scale1 * 100));

		
		return circle.getScaleX() == scale1 && circle.getScaleY() == scale1;
	}
	
	@Override
	public String toString() {
		return "Change Circle Size";
	}
}

class ChangeItemColorAction implements Action {
	
	private Color color1;
	private Color color2;
	private ColorPicker colorPicker;
	private List<DraggableItem> items;
	
	
	public ChangeItemColorAction(Color oldColor, ColorPicker colorPicker, List<DraggableItem> items) {
		this.color1 = colorPicker.getValue();
		this.color2 = oldColor;
		this.colorPicker = colorPicker;
		this.items = items;
	}

	@Override
	public boolean invert() {
		Color temp = color1;
		color1 = color2;
		color2 = temp;
		
		boolean success = true;
		colorPicker.setValue(color1);
		for (DraggableItem d : items) {
			success = success && d.checkBounds();
		}
		return success;
	}
	
	@Override
	public String toString() {
		return "Change Colour";
	}
}

class ChangeItemDetailsAction implements Action {
	
	private DraggableItem item;
	private String text1;
	private String desc1;
	private String text2;
	private String desc2;
	private ObservableList<String> imagesInDiagram = null;
	private List<File> itemImages = null;
	private String tempPath = null;
	private boolean isImage;

	// Images
	public ChangeItemDetailsAction(DraggableItem item, String oldText, String oldDesc, String newText,
			String newDesc, ObservableList<String> imagesInDiagram, List<File> itemImages, String tempPath) {
		this.item= item ;
		this.text1 = newText;
		this.desc1 = newDesc;
		this.text2 = oldText;
		this.desc2 = oldDesc;
		this.imagesInDiagram = imagesInDiagram;
		this.itemImages = itemImages;
		this.tempPath = tempPath;
		this.isImage = true;
	}

	// Text
	public ChangeItemDetailsAction(DraggableItem item, String oldText, String oldDesc, String newText, String newDesc) {
		this.item= item ;
		this.text1 = newText;
		this.desc1 = newDesc;
		this.text2 = oldText;
		this.desc2 = oldDesc;
		this.isImage = false;
	}

	@Override
	public boolean invert() {
		String temp = text1;
		text1 = text2;
		text2 = temp;
		temp = desc1;
		desc1 = desc2;
		desc2 = temp;
		
		if (isImage) {
			imagesInDiagram.remove(text2);
			imagesInDiagram.add(text1);
			DraggableImage imageItem = (DraggableImage) item;
			imageItem.getLabel().setText(text1);
			imageItem.setDescription(desc1);
			itemImages.remove(imageItem.getImageFile());
			File newImageFile = new File(tempPath + "imgs" + File.separatorChar + text1);
			imageItem.getImageFile().renameTo(newImageFile);
			imageItem.setImageFile(newImageFile);
			imageItem.getImageFile().deleteOnExit();
			itemImages.add(imageItem.getImageFile());
		} else {
			item.getLabel().setText(text1);
			item.setDescription(desc1);
		}
		return item.getText().contentEquals(text1) && item.getDescription().contentEquals(desc1);
	}

	@Override
	public String toString() {
		return "Change Item Details";
	}
}

class ChangeTitleColorAction implements Action {
	private TextField node;
	private Color color1;
	private Color color2;
	private ColorPicker colorPicker;

	public ChangeTitleColorAction(TextField node, Color oldColor, ColorPicker colorPicker) {
		this.node = node;
		this.color1 = colorPicker.getValue();
		this.color2 = oldColor;
		this.colorPicker = colorPicker;
	}

	@Override
	public boolean invert() {
		Color temp = color1;
		color1 = color2;
		color2 = temp;
		
		colorPicker.setValue(color1);
		node.setStyle("-fx-background-color: transparent;\n-fx-text-fill: #"
				+ color1.toString().substring(2, color1.toString().length() - 2) + ";");
		return true;
	}
	
	@Override
	public String toString() {
		return "Change Colour";
	}
}

class DeleteFromListAction implements Action {

	private ListView<String> itemsList;
	private List<String> items;
	private boolean inList;
	
	public DeleteFromListAction(ListView<String> itemsList, List<String> items) {
		this.itemsList = itemsList;
		this.items = new ArrayList<String>();
		this.items.addAll(items);
		this.inList = false;
	}

	@Override
	public boolean invert() {
		if (inList) {
			itemsList.getItems().removeAll(items);
			inList = false;
			return !itemsList.getItems().containsAll(items);
		} else {
			itemsList.getItems().addAll(items);
			inList = true;
			return itemsList.getItems().containsAll(items);
		}
	}
	
	@Override
	public String toString() {
		return "Delete Item";
	}
}

class DeleteItemAction implements Action {
	protected double x, y;
	protected boolean inDiagram;
	protected DraggableItem item;
	protected Pane parent;
	protected ObservableList<DraggableItem> itemsInDiagram;
	protected ObservableList<String> imagesInDiagram;
	
	public DeleteItemAction(DraggableItem item, double oldX, double oldY, ObservableList<DraggableItem> itemsInDiagram, ObservableList<String> imagesInDiagram) {
		this.item = item;
		this.x = oldX;
		this.y = oldY;
		this.parent = (Pane) (item.getParent());
		this.itemsInDiagram = itemsInDiagram;
		this.imagesInDiagram = imagesInDiagram;
		this.inDiagram = false;
	}

	@Override
	public boolean invert() {
		if (inDiagram) {
			parent.getChildren().remove(item);
			itemsInDiagram.remove(item);
			if (imagesInDiagram != null)
				imagesInDiagram.remove(item.getText());
			inDiagram = false;
			return !itemsInDiagram.contains(item);
		} else {
			parent.getChildren().add(item);
			itemsInDiagram.add(item);
			item.setLayoutX(x);
			item.setLayoutY(y);
			if (imagesInDiagram != null)
				imagesInDiagram.add(item.getText());
			inDiagram = true;
			item.checkBounds();
			item.setBorder(new Border(new BorderStroke(Color.DEEPSKYBLUE, BorderStrokeStyle.NONE, new CornerRadii(1),
					new BorderWidths(5), new Insets(0))));
			item.setBackground(null);
			return item.getLayoutX() == x && item.getLayoutY() == y;
		}
	}
	
	@Override
	public String toString() {
		return "Delete Item";
	}
}

class ImportImageAction implements Action {
	public boolean invert() { return true; }
	public String toString() { return "Import Image"; }
}

class MoveItemAction implements Action {
	double x1, x2, y1, y2;
	DraggableItem item;

	public MoveItemAction (DraggableItem item, double oldX, double oldY, double newX, double newY) {
		this.item = item;
		this.x1 = newX;
		this.y1 = newY;
		this.x2 = oldX;
		this.y2 = oldY;
	}
			
	@Override
	public boolean invert() {
		double temp = x2;
		x2 = x1;
		x1 = temp;
		temp = y2;
		y2 = y1;
		y1 = temp;
		
		item.setLayoutX(x1);
		item.setLayoutY(y1);
		item.checkBounds();
		
		return item.getLayoutX() == x1 && item.getLayoutY() == y1;
	}
	
	@Override
	public String toString() {
		return "Move \"" + item + "\" from (" + x2 + "," + y2 + ") to (" + x1 + "," + y1 + ")";
	}
}

class MoveItemsWithKeyboardAction implements Action {
	private List<DraggableItem> items;
	private KeyCode direction;
	private double distance;
	
	public MoveItemsWithKeyboardAction(List<DraggableItem> items, KeyCode direction, double distance) {
		this.items = items;
		this.direction = direction;
		this.distance = distance;
	}
	
	@Override
	public boolean invert() {
		final double moveX;
		final double moveY;
		final KeyCode newDirection;
		
		switch(direction) {
			case UP:
				moveX = 0;
				moveY = distance;
				newDirection = KeyCode.DOWN;
				break;
			case DOWN:
				moveX = 0;
				moveY = -distance;
				newDirection = KeyCode.UP;
				break;
			case LEFT:
				moveX = distance;
				moveY = 0;
				newDirection = KeyCode.RIGHT;
				break;
			case RIGHT:
				moveX = -distance;
				moveY = 0;
				newDirection = KeyCode.LEFT;
				break;
			default:
				return false;
		}
		items.forEach(each -> {
			each.setLayoutX(each.getLayoutX() + moveX);
			each.setLayoutY(each.getLayoutY() + moveY);
		});
		this.direction = newDirection;
		return true;
	}
	
	@Override
	public String toString() {
		return items.size() == 1 ? "Move Item" : "Move Items";

	}
}

class RemoveItemAction extends DeleteItemAction implements Action {
	protected ListView<String> itemsList;
	
	public RemoveItemAction(DraggableItem item, double oldX, double oldY, ObservableList<DraggableItem> itemsInDiagram, ListView<String> itemsList, ObservableList<String> imagesInDiagram) {
		super(item, oldX, oldY, itemsInDiagram, imagesInDiagram);
		this.itemsList = itemsList;
	}
	
	public RemoveItemAction(DraggableItem item, double oldX, double oldY, ObservableList<DraggableItem> itemsInDiagram, ListView<String> itemsList) {
		this(item, oldX, oldY, itemsInDiagram, itemsList, null);
	}

	@Override
	public boolean invert() {
		if (inDiagram) {
			if (itemsList != null)
				itemsList.getItems().add(item.getText());
		} else {
			if (itemsList != null)
				itemsList.getItems().remove(item.getText());
		}
		return super.invert();
	}
	
	@Override
	public String toString() {
		return "Remove Item from Diagram";
	}
}

class TypeInFieldAction implements Action {

	private TextField title;
	private String text1, text2;
	private int position;
	
	public TypeInFieldAction(TextField title, String oldText, String newText) {
		this.title = title;
		this.text1 = newText;
		this.text2 = oldText;
		this.position = title.getCaretPosition();
	}

	@Override
	public boolean invert() {
		String text = title.getText();
		String temp = text1;
		text1 = text2;
		text2 = temp;
		title.setText(text1);
		int newPosition = position - (text2.length() - text1.length());
		if (text1.length() > text2.length())
			newPosition -= 1;
		if (newPosition > text1.length()) {
			title.positionCaret(text1.length());
		} else {
			title.positionCaret(newPosition);
		}

		return !text.contentEquals(title.getText());
	}
	
	@Override
	public String toString() {
		return "Typing";
	}
}
