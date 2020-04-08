package application;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import application.Controller.DraggableImage;
import application.Controller.DraggableItem;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public interface DoableAction {
	
	public boolean invert();
	
	public String toString();
	
}

class ActionGroup implements DoableAction {

	List<DoableAction> actionList;
	
	public ActionGroup(List<DoableAction> actionList) {
		this.actionList = actionList;
	}

	@Override
	public boolean invert() {
		boolean success = true;
		for (DoableAction action : actionList) {
			success = success && action.invert();
		}
		return success;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder("Action Group:\n");
		for(DoableAction a : actionList) {
			sb.append("   ");
			sb.append(a.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
}

class AddItemToDiagramAction extends RemoveItemAction implements DoableAction {

	public AddItemToDiagramAction(DraggableItem item, ObservableList<DraggableItem> itemsInDiagram,
			ListView<String> itemsList) {
		this(item, itemsInDiagram, itemsList, null);
	}
	
	public AddItemToDiagramAction(DraggableItem item, ObservableList<DraggableItem> itemsInDiagram,
			ListView<String> itemsList, ObservableList<String> imagesInDiagram) {
		super(item, itemsInDiagram, itemsList, imagesInDiagram);
		inDiagram = true;
	}
	
	public String toString() {
		return "Add Item to Diagram";
	}
}

class AddItemToListAction implements DoableAction {
	
	private String item;
	private ListView<String> itemsList;
	private TextField addItemField;
	private boolean added;

	public AddItemToListAction(String item, ListView<String> itemsList, TextField addItemField) {
		this.item = item;
		this.itemsList= itemsList;
		this.addItemField = addItemField;
		this.added = true;
	}

	@Override
	public boolean invert() {
		if (added) {
			itemsList.getItems().remove(item);
			if (addItemField != null)
				addItemField.setText(item);
			added = false;
		} else {
			itemsList.getItems().add(item);
			if (addItemField != null)
				addItemField.setText("");
			added = true;
		}
		return false;
	}
	
	public String toString() {
		return "Add Item to List";
	}
}

class ChangeCircleColorAction implements DoableAction {
	
	private Circle circle;
	private Color color1;
	private Color color2;

	public ChangeCircleColorAction(Circle circle, Color newColor) {
		this.circle = circle;
		this.color1 = (Color)(circle.getFill());
		this.color2 = newColor;
	}

	@Override
	public boolean invert() {
		Color temp = color1;
		color1 = color2;
		color2 = temp;
		
		circle.setFill(color1);
		return circle.getFill().equals(color1);
	}
	public String toString() {
		return "Change Circle Color";
	}
}

class ChangeCircleSizeAction implements DoableAction {
	
	private Circle circle;
	private double scale1;
	private double scale2;

	public ChangeCircleSizeAction(Circle circle, double size) {
		this.circle = circle;
		this.scale1 = circle.getScaleX();
		this.scale2 = size;
	}

	@Override
	public boolean invert() {
		double temp = scale1;
		scale1 = scale2;
		scale2 = temp;
		
		circle.setScaleX(scale1);
		circle.setScaleY(scale1);
		
		return circle.getScaleX() == scale1 && circle.getScaleY() == scale1;
	}
	
	public String toString() {
		return "Change Circle Size";
	}
}

class ChangedTitleAction implements DoableAction {

	private TextField title;
	private boolean wasUndone;
	
	public ChangedTitleAction(TextField title) {
		this.title = title;
		this.wasUndone = false;
	}

	@Override
	public boolean invert() {
		String text = title.getText();
		if (wasUndone) {
			title.redo();
		} else {
			title.undo();
		}
		return !text.contentEquals(title.getText());
	}
	
	public String toString() {
		return "Change Title";
	}
}

class ChangeItemColorAction implements DoableAction {
	
	private Color color1;
	private Color color2;
	private ColorPicker colorPicker;
	private List<DraggableItem> items;
	
	
	public ChangeItemColorAction(Color oldColor, ColorPicker colorPicker, List<DraggableItem> items) {
		this.color1 = oldColor;
		this.color2 = colorPicker.getValue();
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
	
	public String toString() {
		return "Change Colour";
	}
}

class ChangeItemDetailsAction implements DoableAction {
	
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
		this.text1 = oldText;
		this.desc1 = oldDesc;
		this.text2 = newText;
		this.desc2 = newDesc;
		this.imagesInDiagram = imagesInDiagram;
		this.itemImages = itemImages;
		this.tempPath = tempPath;
		this.isImage = true;
	}

	// Text
	public ChangeItemDetailsAction(DraggableItem item, String oldText, String oldDesc, String newText, String newDesc) {
		this.item= item ;
		this.text1 = oldText;
		this.desc1 = oldDesc;
		this.text2 = newText;
		this.desc2 = newDesc;
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

	
	public String toString() {
		return "Change Item Details";
	}
}

class DeleteFromListAction implements DoableAction {

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
	
	public String toString() {
		return "Delete Item";
	}
}

class DeleteItemAction implements DoableAction {
	protected double x, y;
	protected boolean inDiagram;
	protected DraggableItem item;
	protected Pane parent;
	protected ObservableList<DraggableItem> itemsInDiagram;
	protected ObservableList<String> imagesInDiagram;
	
	public DeleteItemAction(DraggableItem item, ObservableList<DraggableItem> itemsInDiagram, ObservableList<String> imagesInDiagram) {
		this.item = item;
		this.x = item.getLayoutX();
		this.y = item.getLayoutY();
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
			return item.getLayoutX() == x && item.getLayoutY() == y;
		}
	}
	
	public String toString() {
		return "Delete Item";
	}
}

class MoveItemAction implements DoableAction {
	double x1, x2, y1, y2;
	DraggableItem item;

	public MoveItemAction (DraggableItem item, double oldX, double oldY, double newX, double newY) {
		this.item = item;
		this.x1 = oldX;
		this.y1 = oldY;
		this.x2 = newX;
		this.y2 = newY;
	}
	
	Thread inversion = new Thread() {
		public void run() {
			double temp = x1;
			x1 = x2;
			x2 = temp;
			temp = y1;
			y1 = y2;
			y2 = temp;

			try {
				Thread.sleep(500);
			} catch (Exception e) {
				e.printStackTrace();
			}
			Platform.runLater(() -> {
				System.out.println(item.getLayoutX() + "," + item.getLayoutY());
				item.setVisible(false);
				item.setLayoutX(x1);
				item.setLayoutY(y1);
				item.setVisible(true);
				System.out.println(item.getLayoutX() + "," + item.getLayoutY());
			});
		}
	};
		
	@Override
	public boolean invert() {
		

	    if (Platform.isFxApplicationThread()) {
	    	inversion.run();
	    } else {
			Platform.runLater(inversion);
	    }

		
		return item.getLayoutX() == x1 && item.getLayoutY() == y1;
	}
	
	public String toString() {
		return "Move Item from (" + x1 + "," + y1 + ") to (" + x2 + "," + y2 + ")";
	}
}

class RemoveItemAction extends DeleteItemAction implements DoableAction {
	protected ListView<String> itemsList;
	
	public RemoveItemAction(DraggableItem item, ObservableList<DraggableItem> itemsInDiagram, ListView<String> itemsList, ObservableList<String> imagesInDiagram) {
		super(item, itemsInDiagram, imagesInDiagram);
		this.itemsList = itemsList;
	}
	
	public RemoveItemAction(DraggableItem item, ObservableList<DraggableItem> itemsInDiagram, ListView<String> itemsList) {
		this(item, itemsInDiagram, itemsList, null);
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
	
	public String toString() {
		return "Remove Item from Diagram";
	}
}

class SetStyleAction implements DoableAction {
	
	private Node node;
	private String style1;
	private String style2;

	public SetStyleAction(Node pane, String newStyle) {
		this.node = pane;
		this.style1 = pane.getStyle();
		this.style2 = newStyle;
	}

	@Override
	public boolean invert() {
		String temp = style1;
		style1 = style2;
		style2 = temp;
		
		node.setStyle(style1);
		return node.getStyle().contentEquals(style1);
	}
	
	public String toString() {
		return "Change Colour";
	}
}
