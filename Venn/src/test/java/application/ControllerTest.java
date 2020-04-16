package application;

import static org.junit.Assert.*;

import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.service.query.EmptyNodeQueryException;
import org.testfx.util.WaitForAsyncUtils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javafx.scene.control.*;
import javafx.scene.shape.Circle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import application.Controller.DraggableItem;

import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ControllerTest extends ApplicationTest {

	ControllerTest controller;
	Parent mainNode;

	@Before
	public void setUpClass() throws Exception {
		controller = new ControllerTest();
		ApplicationTest.launch(Main.class);
	}

	@Override
	public void start(Stage stage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/application/Test.fxml"));

		stage.setScene(new Scene(root));
		stage.show();
		stage.toFront();
	}

	// retrieve widgets in the GUI. */
	public <T extends Node> T find(final String query) {
		/* TestFX provides many operations to retrieve elements from the loaded GUI. */
		return lookup(query).query();
	}

	// clearing events
	@After
	public void tearDown() throws TimeoutException {
		/* Close the window. It will be re-opened at the next test. */
		FxToolkit.hideStage();
		release(new KeyCode[] {});
		release(new MouseButton[] {});
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAddingItems() {
		final int totalItems = 10;
		clickOn("#addItemField");
		for (int i = 1; i <= totalItems; i++) {
			write("Item " + i);
			clickOn("#addItemButton");
			WaitForAsyncUtils.waitForFxEvents();
			assertTrue(((ListView<String>) (find("#itemsList"))).getItems().contains("Item " + 1));
		}
		clickOn("#itemsList");
		for (int i = 0; i < 9; i += 3) {
			drag("Item " + (i + 1)).moveTo("#circleLeft").dropBy(-100, (Math.random() * 200) - 100);
			WaitForAsyncUtils.waitForFxEvents();
			drag("Item " + (i + 2)).moveTo("#circleLeft").dropBy(((Circle)(find("#circleLeft"))).getBoundsInParent().getWidth() / 2 - 110, (Math.random() * 150) - 75);
			WaitForAsyncUtils.waitForFxEvents();
			drag("Item " + (i + 3)).moveTo("#circleRight").dropBy(-40, (Math.random() * 200) - 100);
			WaitForAsyncUtils.waitForFxEvents();
		}
		
		assertEquals(((DraggableItem)(find("Item 1").getParent())).getCircle(), 'l');
		assertEquals(((DraggableItem)(find("Item 2").getParent())).getCircle(), 'i');
		assertEquals(((DraggableItem)(find("Item 3").getParent())).getCircle(), 'r');
		assertEquals(((DraggableItem)(find("Item 4").getParent())).getCircle(), 'l');
		assertEquals(((DraggableItem)(find("Item 5").getParent())).getCircle(), 'i');
		assertEquals(((DraggableItem)(find("Item 6").getParent())).getCircle(), 'r');
		assertEquals(((DraggableItem)(find("Item 7").getParent())).getCircle(), 'l');
		assertEquals(((DraggableItem)(find("Item 8").getParent())).getCircle(), 'i');
		assertEquals(((DraggableItem)(find("Item 9").getParent())).getCircle(), 'r');
		assertTrue(((ListView<String>)(find("#itemsList"))).getItems().contains("Item 10"));
	}

	// test input text
	@Test
	public void testTitles() {
		clickOn("#title").write("Example Diagram");
		clickOn("#circleLeftTitle").write("Left Circle");
		clickOn("#circleRightTitle").write("Right Circle");

		assertEquals(((TextField) (find("#title"))).getText(), "Example Diagram");
		assertEquals(((TextField) (find("#circleLeftTitle"))).getText(), "Left Circle");
		assertEquals(((TextField) (find("#circleRightTitle"))).getText(), "Right Circle");
	}

	// testing the delete button
	@Test
	public void testDeleteButton() {
		// TextField newItem = (TextField) find("#newItem");

		for (int i = 1; i < 6; i++) {
			clickOn("#addItemField").write("Item " + i);
			clickOn("#addItemButton");
		}
		clickOn("#itemsList");

		WaitForAsyncUtils.waitForFxEvents();

		clickOn("Item 1");
		clickOn("#deleteButton");
		clickOn("#itemsList");
		WaitForAsyncUtils.waitForFxEvents();
		try {
			find("Item 1");
			fail();
		} catch (EmptyNodeQueryException e) {
			// Item successfully deleted
		}
		drag("Item 2").moveTo("#circleLeft").dropBy(-100, 0);
		WaitForAsyncUtils.waitForFxEvents();
		drag("Item 3").moveTo("#circleRight").dropBy(-40, 0);
		WaitForAsyncUtils.waitForFxEvents();
		
		clickOn("Item 2");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#deleteButton");
		WaitForAsyncUtils.waitForFxEvents();
		try {
			find("Item 2");
			fail();
		} catch (EmptyNodeQueryException f) {
			// Item successfully deleted
		}
		
		clickOn("Item 3");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#deleteButton");
		WaitForAsyncUtils.waitForFxEvents();
		try {
			find("Item 3");
			fail();
		} catch (EmptyNodeQueryException g) {
			// Item successfully deleted
		}
	}

	// clear button
	@SuppressWarnings("unchecked")
	@Test
	public void testRemoveButton() {
		final int ITEMS = 5;
		for (int i = 1; i <= ITEMS; i++) {
			clickOn("#addItemField").write("Item " + i);
			clickOn("#addItemButton");
			WaitForAsyncUtils.waitForFxEvents();
		}
		clickOn("#itemsList");
		drag("Item 1").moveTo("#circleLeft").dropBy(((Circle)(find("#circleLeft"))).getBoundsInParent().getWidth() / 2 - 110, 0);
		drag("Item 2").moveTo("#circleLeft").dropBy(-100, 0);
		drag("Item 3").moveTo("#circleRight").dropBy(-40, 0);
		WaitForAsyncUtils.waitForFxEvents();
		
		press(KeyCode.SHIFT);
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("Item 1");
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.SHIFT);
		WaitForAsyncUtils.waitForFxEvents();
		
		assertNotEquals(ITEMS, ((ListView<String>) (find("#itemsList"))).getItems().size());
		clickOn("#removeButton");
		WaitForAsyncUtils.waitForFxEvents();
		
		assertEquals(ITEMS - 2, ((ListView<String>) (find("#itemsList"))).getItems().size());
		assertNotEquals(Label.class, find("Item 1").getClass());

		clickOn("Edit");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("Select All");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#removeButton");
		WaitForAsyncUtils.waitForFxEvents();
		
		assertEquals(ITEMS, ((ListView<String>) (find("#itemsList"))).getItems().size());
		assertNotEquals(Label.class, find("Item 1").getClass());
	}


	// test colour picker
	@Test
	public void testColourPickers() {
		clickOn("#title");
		WaitForAsyncUtils.waitForFxEvents();
		write("Title");
		clickOn("#circleLeftTitle");
		WaitForAsyncUtils.waitForFxEvents();
		write("Left");
		clickOn("#circleRightTitle");
		WaitForAsyncUtils.waitForFxEvents();
		write("Right");
		WaitForAsyncUtils.waitForFxEvents();
		
		for (int i = 1; i <= 3; i++) {
			clickOn("#addItemField");
			WaitForAsyncUtils.waitForFxEvents();
			write("Item " + i);
			WaitForAsyncUtils.waitForFxEvents();
			clickOn("#addItemButton");
			WaitForAsyncUtils.waitForFxEvents();
		}
		clickOn("Item 1");
		WaitForAsyncUtils.waitForFxEvents();
		drag("Item 1").moveTo("#circleLeft").dropBy(-100, (Math.random() * 200) - 100);
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("Item 2");
		WaitForAsyncUtils.waitForFxEvents();
		drag("Item 2").moveTo("#circleLeft").dropBy(((Circle)(find("#circleLeft"))).getBoundsInParent().getWidth() / 2 - 110, (Math.random() * 150) - 75);
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("Item 3");
		WaitForAsyncUtils.waitForFxEvents();
		drag("Item 3").moveTo("#circleRight").dropBy(-40, (Math.random() * 200) - 100);
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#settingsButton");
		WaitForAsyncUtils.waitForFxEvents();

		// testing background colour
		String s1 = ((Pane) find("#pane")).getStyle();
		WaitForAsyncUtils.waitForFxEvents();
		moveTo("#colorBackground");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#colorBackground");
		WaitForAsyncUtils.waitForFxEvents();
		moveBy(90, 30);
		WaitForAsyncUtils.waitForFxEvents();
		clickOn();
		WaitForAsyncUtils.waitForFxEvents();
		String s3 = "-fx-background-color: #" + ((ColorPicker) find("#colorBackground")).getValue().toString()
				.substring(2, ((ColorPicker) find("#colorBackground")).getValue().toString().length() - 2) + ";";
		String s2 = ((Pane) find("#pane")).getStyle();
		assertEquals(s3, s2);
		assertNotEquals(s1, s2);

		// testing title colours
		String[] styles1 = {
			((TextField) find("#title")).getStyle(),
			((TextField) find("#circleLeftTitle")).getStyle(),
			((TextField) find("#circleRightTitle")).getStyle()
		};
		WaitForAsyncUtils.waitForFxEvents();
		moveTo("#colorTitles");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#colorTitles");
		WaitForAsyncUtils.waitForFxEvents();
		moveBy(60, 90);
		WaitForAsyncUtils.waitForFxEvents();
		clickOn();
		WaitForAsyncUtils.waitForFxEvents();
		String[] styles2 = {
			((TextField) find("#title")).getStyle(),
			((TextField) find("#circleLeftTitle")).getStyle(),
			((TextField) find("#circleRightTitle")).getStyle()
		};
		String newStyle = "-fx-background-color: transparent;\n-fx-text-fill: #"
			+ ((ColorPicker) find("#colorTitles")).getValue().toString().substring(2,
			  ((ColorPicker) find("#colorTitles")).getValue().toString().length() - 2) + ";";
		for (int i = 0; i < 3; i++) {
			assertEquals(newStyle, styles2[i]);
			assertNotEquals(styles1[i], styles2[i]);
			WaitForAsyncUtils.waitForFxEvents();
		}
		
		// testing left colour
		Color c1 = (Color) ((Circle)(find("#circleLeft"))).getFill();
		clickOn("#colorLeft");
		WaitForAsyncUtils.waitForFxEvents();
		moveBy(30, 30);
		WaitForAsyncUtils.waitForFxEvents();
		clickOn();
		WaitForAsyncUtils.waitForFxEvents();
		Color c2 = (Color) ((Circle)(find("#circleLeft"))).getFill();
		assertNotEquals(c1, c2);

		// testing left item colour
		c1 = (Color) ((Label)find("Item 1")).getTextFill();
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#colorLeftItems");
		WaitForAsyncUtils.waitForFxEvents();
		moveBy(90, 90);
		WaitForAsyncUtils.waitForFxEvents();
		clickOn();
		WaitForAsyncUtils.waitForFxEvents();
		c2 = (Color) ((Label)find("Item 1")).getTextFill();
		assertEquals(((ColorPicker)find("#colorLeftItems")).getValue(), c2);
		assertNotEquals(c1, (Color) ((Label)find("Item 1")).getTextFill());

		// testing right colour
		c1 = (Color) ((Circle)(find("#circleRight"))).getFill();
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#colorRight");
		WaitForAsyncUtils.waitForFxEvents();
		moveBy(60, 60);
		WaitForAsyncUtils.waitForFxEvents();
		clickOn();
		WaitForAsyncUtils.waitForFxEvents();
		c2 = (Color) ((Circle)(find("#circleRight"))).getFill();
		assertNotEquals(c1, c2);

		// testing right item colour
		c1 = (Color) ((Label)find("Item 3")).getTextFill();
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#colorRightItems");
		WaitForAsyncUtils.waitForFxEvents();
		moveBy(60, 30);
		WaitForAsyncUtils.waitForFxEvents();
		clickOn();
		WaitForAsyncUtils.waitForFxEvents();
		c2 = (Color) ((Label)find("Item 3")).getTextFill();
		assertEquals(((ColorPicker)find("#colorRightItems")).getValue(), c2);
		assertNotEquals(c1, (Color) ((Label)find("Item 3")).getTextFill());

		// testing intersection item colour
		c1 = (Color) ((Label)find("Item 2")).getTextFill();
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#colorIntersectionItems");
		WaitForAsyncUtils.waitForFxEvents();
		moveBy(30, 90);
		WaitForAsyncUtils.waitForFxEvents();
		clickOn();
		WaitForAsyncUtils.waitForFxEvents();
		c2 = (Color) ((Label)find("Item 2")).getTextFill();
		assertEquals(((ColorPicker)find("#colorIntersectionItems")).getValue(), c2);
		assertNotEquals(c1, (Color) ((Label)find("Item 2")).getTextFill());
	}
	
	// circle size
	@Test
	public void testCircleSize() {
		clickOn("#settingsButton");
		WaitForAsyncUtils.waitForFxEvents();
		double s1 = ((Slider) (find("#leftSizeSlider"))).getValue();
		String f1 = ((TextField) (find("#leftSizeField"))).getText();
		double c1 = ((Circle) (find("#circleLeft"))).getScaleX() * 100;
		drag("#leftSizeSlider").dropBy(70, 0);
		WaitForAsyncUtils.waitForFxEvents();
		double s2 = ((Slider) (find("#leftSizeSlider"))).getValue();
		String f2 = ((TextField) (find("#leftSizeField"))).getText();
		double c2 = ((Circle) (find("#circleLeft"))).getScaleX() * 100;
		drag().dropBy(-140, 0);
		WaitForAsyncUtils.waitForFxEvents();
		double s3 = ((Slider) (find("#leftSizeSlider"))).getValue();
		String f3 = ((TextField) (find("#leftSizeField"))).getText();
		double c3 = ((Circle) (find("#circleLeft"))).getScaleX() * 100;
		assertNotEquals(s1, s2);
		assertNotEquals(f1, f2);
		assertNotEquals(c1, c2);
		assertEquals(120, s2, 0.001);
		assertEquals("120", f2);
		assertEquals(120, c2, 0.001);
		assertNotEquals(s2, s3);
		assertNotEquals(f2, f3);
		assertNotEquals(c2, c3);
		assertEquals(75, s3, 0.001);
		assertEquals("75", f3);
		assertEquals(75, c3, 0.001);
		
		doubleClickOn("#leftSizeField").write("95");
		WaitForAsyncUtils.waitForFxEvents();
		type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(95, ((Slider) (find("#leftSizeSlider"))).getValue(), 0.001);
		assertEquals(95, ((Circle) (find("#circleLeft"))).getScaleX() * 100, 0.001);
		
		doubleClickOn("#leftSizeField").write("300");
		WaitForAsyncUtils.waitForFxEvents();
		type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(120, ((Slider) (find("#leftSizeSlider"))).getValue(), 0.001);
		assertEquals("120", ((TextField) (find("#leftSizeField"))).getText());
		assertEquals(120, ((Circle) (find("#circleLeft"))).getScaleX() * 100, 0.001);
		
		doubleClickOn("#leftSizeField").write("0");
		WaitForAsyncUtils.waitForFxEvents();
		type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(75, ((Slider) (find("#leftSizeSlider"))).getValue(), 0.001);
		assertEquals("75", ((TextField) (find("#leftSizeField"))).getText());
		assertEquals(75, ((Circle) (find("#circleLeft"))).getScaleX() * 100, 0.001);
		
		doubleClickOn("#leftSizeField").write("hello");
		WaitForAsyncUtils.waitForFxEvents();
		type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(75, ((Slider) (find("#leftSizeSlider"))).getValue(), 0.001);
		assertEquals("75", ((TextField) (find("#leftSizeField"))).getText());
		assertEquals(75, ((Circle) (find("#circleLeft"))).getScaleX() * 100, 0.001);

		s1 = ((Slider) (find("#rightSizeSlider"))).getValue();
		f1 = ((TextField) (find("#rightSizeField"))).getText();
		c1 = ((Circle) (find("#circleRight"))).getScaleX() * 100;
		drag("#rightSizeSlider").dropBy(70, 0);
		WaitForAsyncUtils.waitForFxEvents();
		s2 = ((Slider) (find("#rightSizeSlider"))).getValue();
		f2 = ((TextField) (find("#rightSizeField"))).getText();
		c2 = ((Circle) (find("#circleRight"))).getScaleX() * 100;
		drag().dropBy(-140, 0);
		WaitForAsyncUtils.waitForFxEvents();
		s3 = ((Slider) (find("#rightSizeSlider"))).getValue();
		f3 = ((TextField) (find("#rightSizeField"))).getText();
		c3 = ((Circle) (find("#circleRight"))).getScaleX() * 100;
		assertNotEquals(s1, s2);
		assertNotEquals(f1, f2);
		assertNotEquals(c1, c2);
		assertEquals(120, s2, 0.001);
		assertEquals("120", f2);
		assertEquals(120, c2, 0.001);
		assertNotEquals(s2, s3);
		assertNotEquals(f2, f3);
		assertNotEquals(c2, c3);
		assertEquals(75, s3, 0.001);
		assertEquals("75", f3);
		assertEquals(75, c3, 0.001);
		
		doubleClickOn("#rightSizeField").write("95");
		WaitForAsyncUtils.waitForFxEvents();
		type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(95, ((Slider) (find("#rightSizeSlider"))).getValue(), 0.001);
		assertEquals(95, ((Circle) (find("#circleRight"))).getScaleX() * 100, 0.001);
		
		doubleClickOn("#rightSizeField").write("300");
		WaitForAsyncUtils.waitForFxEvents();
		type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(120, ((Slider) (find("#rightSizeSlider"))).getValue(), 0.001);
		assertEquals("120", ((TextField) (find("#rightSizeField"))).getText());
		assertEquals(120, ((Circle) (find("#circleRight"))).getScaleX() * 100, 0.001);
		
		doubleClickOn("#rightSizeField").write("0");
		WaitForAsyncUtils.waitForFxEvents();
		type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(75, ((Slider) (find("#rightSizeSlider"))).getValue(), 0.001);
		assertEquals("75", ((TextField) (find("#rightSizeField"))).getText());
		assertEquals(75, ((Circle) (find("#circleRight"))).getScaleX() * 100, 0.001);
		
		doubleClickOn("#rightSizeField").write("hello");
		WaitForAsyncUtils.waitForFxEvents();
		type(KeyCode.ENTER);
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(75, ((Slider) (find("#rightSizeSlider"))).getValue(), 0.001);
		assertEquals("75", ((TextField) (find("#rightSizeField"))).getText());
		assertEquals(75, ((Circle) (find("#circleRight"))).getScaleX() * 100, 0.001);
	}
	
	// details
	@Test
	public void testDetailView() {
		clickOn("#addItemField");
		WaitForAsyncUtils.waitForFxEvents();
		write("Test");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#addItemButton");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#itemsList");
		WaitForAsyncUtils.waitForFxEvents();
		drag("Test").dropTo("#circleLeft");
		WaitForAsyncUtils.waitForFxEvents();
		doubleClickOn("Test");
		WaitForAsyncUtils.waitForFxEvents();
		doubleClickOn("#itemTitle");
		WaitForAsyncUtils.waitForFxEvents();
		type(KeyCode.DELETE);
		WaitForAsyncUtils.waitForFxEvents();
		assertTrue(((Button) find("Save")).isDisabled());
		WaitForAsyncUtils.waitForFxEvents();
		write("Hello");
		WaitForAsyncUtils.waitForFxEvents();
		assertFalse(((Button) find("Save")).isDisabled());
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#itemDescription");
		WaitForAsyncUtils.waitForFxEvents();
		write("This is a description");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("Save");
		WaitForAsyncUtils.waitForFxEvents();
		try {
			doubleClickOn("Hello");
			WaitForAsyncUtils.waitForFxEvents();
		} catch (Exception e) {
			fail();
		}
		doubleClickOn("Hello");
		WaitForAsyncUtils.waitForFxEvents();
		write("Test");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("Cancel");
		WaitForAsyncUtils.waitForFxEvents();
		try {
			doubleClickOn("Test");
			WaitForAsyncUtils.waitForFxEvents();
			fail();
		} catch (Exception e) {
			// "Test" item should not exist because we hit cancel, not save.
		}
		try {
			doubleClickOn("Hello");
			WaitForAsyncUtils.waitForFxEvents();
		} catch (Exception e) {
			fail();
		}
		try {
			clickOn("This is a description");
			WaitForAsyncUtils.waitForFxEvents();
			clickOn("Cancel");
			WaitForAsyncUtils.waitForFxEvents();
		} catch (Exception e) {
			fail();
		}
	}
	
	// move item
	@SuppressWarnings("unchecked")
	@Test
	public void testMovingItems() {
		clickOn("#addItemField");
		WaitForAsyncUtils.waitForFxEvents();
		write("Test");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#addItemButton");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#itemsList");
		WaitForAsyncUtils.waitForFxEvents();
		drag("Test").dropTo("#circleLeft");
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals('l', ((DraggableItem) (find("Test").getParent())).getCircle());
		drag("Test").moveTo("#circleLeft").dropBy(((Circle)(find("#circleLeft"))).getBoundsInParent().getWidth() / 2 - 110, 0);
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals('i', ((DraggableItem) (find("Test").getParent())).getCircle());
		drag("Test").dropTo("#circleRight");
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals('r', ((DraggableItem) (find("Test").getParent())).getCircle());
		drag("Test").dropTo("#itemsList");
		WaitForAsyncUtils.waitForFxEvents();
		assertTrue(((ListView<String>) find("#itemsList")).getItems().contains("Test"));
	}
	
	// keyboard shortcuts
	@SuppressWarnings("unchecked")
	@Test
	public void testKeyboardShortcuts() {
		clickOn("#addItemField");
		WaitForAsyncUtils.waitForFxEvents();
		write("Test");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#addItemButton");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#itemsList");
		WaitForAsyncUtils.waitForFxEvents();
		drag("Test").dropTo("#circleLeft");
		WaitForAsyncUtils.waitForFxEvents();
		double location1 = ((DraggableItem) find("Test").getParent()).getLayoutX();
		drag("Test").dropTo("#circleRight");
		WaitForAsyncUtils.waitForFxEvents();
		double location2 = ((DraggableItem) find("Test").getParent()).getLayoutX();
		assertNotEquals(location1, location2);
		press(KeyCode.SHORTCUT, KeyCode.Z);
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.SHORTCUT, KeyCode.Z);
		WaitForAsyncUtils.waitForFxEvents();
		double location3 = ((DraggableItem) find("Test").getParent()).getLayoutX();
		assertNotEquals(location2, location3);
		assertEquals(location1, location3, 0.001);
		press(KeyCode.SHORTCUT, KeyCode.Y);
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.SHORTCUT, KeyCode.Y);
		WaitForAsyncUtils.waitForFxEvents();
		double location4 = ((DraggableItem) find("Test").getParent()).getLayoutX();
		assertEquals(location2, location4, 0.001);
		
		press(KeyCode.SHORTCUT, KeyCode.O);
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.SHORTCUT, KeyCode.O);
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("Cancel");
		WaitForAsyncUtils.waitForFxEvents();
		
		press(KeyCode.SHORTCUT, KeyCode.N);
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.SHORTCUT, KeyCode.N);
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("Cancel");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#diagram");
				
		press(KeyCode.SHORTCUT, KeyCode.MINUS);
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.SHORTCUT, KeyCode.MINUS);
		WaitForAsyncUtils.waitForFxEvents();
		press(KeyCode.SHORTCUT, KeyCode.MINUS);
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.SHORTCUT, KeyCode.MINUS);
		WaitForAsyncUtils.waitForFxEvents();
		press(KeyCode.SHORTCUT, KeyCode.MINUS);
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.SHORTCUT, KeyCode.MINUS);
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(0.7, ((Pane) find("#diagram")).getScaleX(), 0.01);
		
		press(KeyCode.SHORTCUT, KeyCode.EQUALS);
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.SHORTCUT, KeyCode.EQUALS);
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(0.8, ((Pane) find("#diagram")).getScaleX(), 0.01);
		
		press(KeyCode.SHORTCUT, KeyCode.DIGIT0);
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.SHORTCUT, KeyCode.DIGIT0);
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(1.0, ((Pane) find("#diagram")).getScaleX(), 0.01);
		
		clickOn("#addItemField");
		WaitForAsyncUtils.waitForFxEvents();
		write("Hello");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#addItemButton");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#itemsList");
		WaitForAsyncUtils.waitForFxEvents();
		drag("Hello").dropTo("#circleLeft");
		WaitForAsyncUtils.waitForFxEvents();
		
		clickOn("#addItemField");
		WaitForAsyncUtils.waitForFxEvents();
		write("World");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#addItemButton");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#itemsList");
		WaitForAsyncUtils.waitForFxEvents();
		drag("World").moveTo("#circleLeft").dropBy(((Circle)(find("#circleLeft"))).getBoundsInParent().getWidth() / 2 - 110, 0);
		WaitForAsyncUtils.waitForFxEvents();
		
		press(KeyCode.SHORTCUT, KeyCode.A);
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.SHORTCUT, KeyCode.A);
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#removeButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(3, ((ListView<String>) (find("#itemsList"))).getItems().size());

		
		press(KeyCode.SHORTCUT, KeyCode.S);
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.SHORTCUT, KeyCode.S);
		WaitForAsyncUtils.waitForFxEvents();
		press(KeyCode.ESCAPE);
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.ESCAPE);
		WaitForAsyncUtils.waitForFxEvents();
		
		press(KeyCode.SHORTCUT, KeyCode.SHIFT, KeyCode.S);
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.SHORTCUT, KeyCode.SHIFT, KeyCode.S);
		WaitForAsyncUtils.waitForFxEvents();
		press(KeyCode.ESCAPE);
		WaitForAsyncUtils.waitForFxEvents();
		release(KeyCode.ESCAPE);
		WaitForAsyncUtils.waitForFxEvents();
	}
	
	// undo/redo
	@SuppressWarnings("unchecked")
	@Test
	public void testUndoRedo() {
		clickOn("#addItemField");
		WaitForAsyncUtils.waitForFxEvents();
		write("Test");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#addItemButton");
		WaitForAsyncUtils.waitForFxEvents();
		doubleClickOn("Test");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#deleteButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertFalse(((ListView<String>) find("#itemsList")).getItems().contains("Test"));
		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertTrue(((ListView<String>) find("#itemsList")).getItems().contains("Test"));
		clickOn("#redoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertFalse(((ListView<String>) find("#itemsList")).getItems().contains("Test"));
		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("Test");
		WaitForAsyncUtils.waitForFxEvents();
		
		drag("Test").dropTo("#circleLeft");
		WaitForAsyncUtils.waitForFxEvents();
		double x1 = ((Label) find("Test")).getParent().getLayoutX();
		double y1 = ((Label) find("Test")).getParent().getLayoutY();
		Color ltxt = (Color) ((Label) find("Test")).getTextFill();
		drag("Test").moveTo("#circleLeft").dropBy(((Circle)(find("#circleLeft"))).getBoundsInParent().getWidth() / 2 - 110, 0);
		WaitForAsyncUtils.waitForFxEvents();
		double x2 = ((Label) find("Test")).getParent().getLayoutX();
		double y2 = ((Label) find("Test")).getParent().getLayoutY();
		Color itxt = (Color) ((Label) find("Test")).getTextFill();
		drag("Test").dropTo("#circleRight");
		WaitForAsyncUtils.waitForFxEvents();
		double x3 = ((Label) find("Test")).getParent().getLayoutX();
		double y3 = ((Label) find("Test")).getParent().getLayoutY();
		Color rtxt = (Color) ((Label) find("Test")).getTextFill();
		
		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertNotEquals(x3, ((Label) find("Test")).getParent().getLayoutX());
		assertNotEquals(y3, ((Label) find("Test")).getParent().getLayoutY());
		assertEquals(x2, ((Label) find("Test")).getParent().getLayoutX(), 10);
		assertEquals(y2, ((Label) find("Test")).getParent().getLayoutY(), 10);
		
		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertNotEquals(x2, ((Label) find("Test")).getParent().getLayoutX());
		assertNotEquals(y2, ((Label) find("Test")).getParent().getLayoutY());
		assertEquals(x1, ((Label) find("Test")).getParent().getLayoutX(), 10);
		assertEquals(y1, ((Label) find("Test")).getParent().getLayoutY(), 10);
		
		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertTrue(((ListView<String>) find("#itemsList")).getItems().contains("Test"));
		
		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertFalse(((ListView<String>) find("#itemsList")).getItems().contains("Test"));
		
		clickOn("#redoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertTrue(((ListView<String>) find("#itemsList")).getItems().contains("Test"));
		
		clickOn("#redoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(x1, ((Label) find("Test")).getParent().getLayoutX(), 10);
		assertEquals(y1, ((Label) find("Test")).getParent().getLayoutY(), 10);

		clickOn("#redoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(x2, ((Label) find("Test")).getParent().getLayoutX(), 10);
		assertEquals(y2, ((Label) find("Test")).getParent().getLayoutY(), 10);

		clickOn("#redoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(x3, ((Label) find("Test")).getParent().getLayoutX(), 10);
		assertEquals(y3, ((Label) find("Test")).getParent().getLayoutY(), 10);

		clickOn("Test");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#deleteButton");
		WaitForAsyncUtils.waitForFxEvents();
		try {
			find("Test");
			fail();
		} catch (Exception e) {}
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		try {
			assertEquals(x3, ((Label) find("Test")).getParent().getLayoutX(), 10);
			assertEquals(y3, ((Label) find("Test")).getParent().getLayoutY(), 10);
		} catch (Exception e) {
			fail();
		}
		clickOn("#redoButton");
		WaitForAsyncUtils.waitForFxEvents();
		try {
			find("Test");
			fail();
		} catch (Exception e) {}
		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();

		clickOn("Test");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#removeButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertTrue(((ListView<String>) find("#itemsList")).getItems().contains("Test"));
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		try {
			assertEquals(x3, ((Label) find("Test")).getParent().getLayoutX(), 10);
			assertEquals(y3, ((Label) find("Test")).getParent().getLayoutY(), 10);
		} catch (Exception e) {
			fail();
		}
		clickOn("#redoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertTrue(((ListView<String>) find("#itemsList")).getItems().contains("Test"));
		WaitForAsyncUtils.waitForFxEvents();

		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		String text1 = ((DraggableItem) find("Test").getParent()).getText();
		String desc1 = ((DraggableItem) find("Test").getParent()).getDescription();
		
		doubleClickOn("Test");
		WaitForAsyncUtils.waitForFxEvents();
		doubleClickOn("#itemTitle");
		WaitForAsyncUtils.waitForFxEvents();
		write("New title");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#itemDescription");
		WaitForAsyncUtils.waitForFxEvents();
		write("New description");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("Save");
		WaitForAsyncUtils.waitForFxEvents();
		
		String text2 = ((DraggableItem) find("New title").getParent()).getText();
		String desc2 = ((DraggableItem) find("New title").getParent()).getDescription();
		
		assertNotEquals(text1, text2);
		assertNotEquals(desc1, desc2);
		
		moveTo("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		
		assertEquals(text1, ((DraggableItem) find("Test").getParent()).getText());
		assertEquals(desc1, ((DraggableItem) find("Test").getParent()).getDescription());
		
		clickOn("#redoButton");
		WaitForAsyncUtils.waitForFxEvents();
		
		assertEquals(text2, ((DraggableItem) find("New title").getParent()).getText());
		assertEquals(desc2, ((DraggableItem) find("New title").getParent()).getDescription());
		
		double keyX, keyY, keyX2, keyY2;
		
		keyX = find("New title").getLayoutX();
		keyY = find("New title").getLayoutY();
		clickOn("New title");
		WaitForAsyncUtils.waitForFxEvents();
		type(KeyCode.UP);
		WaitForAsyncUtils.waitForFxEvents();
		keyX2 = find("New title").getLayoutX();
		keyY2 = find("New title").getLayoutY();
		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(keyX, find("New title").getLayoutX(), 0.0001);
		assertEquals(keyY, find("New title").getLayoutY(), 0.0001);
		clickOn("#redoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(keyX2, find("New title").getLayoutX(), 0.0001);
		assertEquals(keyY2, find("New title").getLayoutY(), 0.0001);
		
		keyX = find("New title").getLayoutX();
		keyY = find("New title").getLayoutY();
		clickOn("New title");
		WaitForAsyncUtils.waitForFxEvents();
		type(KeyCode.DOWN);
		WaitForAsyncUtils.waitForFxEvents();
		keyX2 = find("New title").getLayoutX();
		keyY2 = find("New title").getLayoutY();
		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(keyX, find("New title").getLayoutX(), 0.0001);
		assertEquals(keyY, find("New title").getLayoutY(), 0.0001);
		clickOn("#redoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(keyX2, find("New title").getLayoutX(), 0.0001);
		assertEquals(keyY2, find("New title").getLayoutY(), 0.0001);
		
		keyX = find("New title").getLayoutX();
		keyY = find("New title").getLayoutY();
		clickOn("New title");
		WaitForAsyncUtils.waitForFxEvents();
		type(KeyCode.LEFT);
		WaitForAsyncUtils.waitForFxEvents();
		keyX2 = find("New title").getLayoutX();
		keyY2 = find("New title").getLayoutY();
		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(keyX, find("New title").getLayoutX(), 0.0001);
		assertEquals(keyY, find("New title").getLayoutY(), 0.0001);
		clickOn("#redoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(keyX2, find("New title").getLayoutX(), 0.0001);
		assertEquals(keyY2, find("New title").getLayoutY(), 0.0001);
		
		keyX = find("New title").getLayoutX();
		keyY = find("New title").getLayoutY();
		clickOn("New title");
		WaitForAsyncUtils.waitForFxEvents();
		type(KeyCode.RIGHT);
		WaitForAsyncUtils.waitForFxEvents();
		keyX2 = find("New title").getLayoutX();
		keyY2 = find("New title").getLayoutY();
		clickOn("#undoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(keyX, find("New title").getLayoutX(), 0.0001);
		assertEquals(keyY, find("New title").getLayoutY(), 0.0001);
		clickOn("#redoButton");
		WaitForAsyncUtils.waitForFxEvents();
		assertEquals(keyX2, find("New title").getLayoutX(), 0.0001);
		assertEquals(keyY2, find("New title").getLayoutY(), 0.0001);
		
		clickOn("File");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("New");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("OK");
		WaitForAsyncUtils.waitForFxEvents();
		
		String bg = ((Pane) find("#pane")).getStyle();
		String ttl = ((TextField) find("#title")).getStyle();
		String lttl = ((TextField) find("#circleLeftTitle")).getStyle();
		String rttl = ((TextField) find("#circleRightTitle")).getStyle();
		Color lcrcl = (Color) ((Circle) find("#circleLeft")).getFill();
		Color rcrcl = (Color) ((Circle) find("#circleRight")).getFill();
		
		testColourPickers();
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#settingsButton");
		WaitForAsyncUtils.waitForFxEvents();
		testCircleSize();
		WaitForAsyncUtils.waitForFxEvents();
		
		String bg2 = ((Pane) find("#pane")).getStyle();
		String ttl2 = ((TextField) find("#title")).getStyle();
		String lttl2 = ((TextField) find("#circleLeftTitle")).getStyle();
		String rttl2 = ((TextField) find("#circleRightTitle")).getStyle();
		Color lcrcl2 = (Color) ((Circle) find("#circleLeft")).getFill();
		Color rcrcl2 = (Color) ((Circle) find("#circleRight")).getFill();
		String ttl2txt = ((TextField) find("#title")).getText();
		String lttl2txt = ((TextField) find("#circleLeftTitle")).getText();
		String rttl2txt = ((TextField) find("#circleRightTitle")).getText();
		double lcs = ((Circle) find("#circleLeft")).getScaleX();
		double rcs = ((Circle) find("#circleRight")).getScaleX();
		Color ltxt2 = (Color) ((Label) find("Item 1")).getTextFill();
		Color itxt2 = (Color) ((Label) find("Item 2")).getTextFill();
		Color rtxt2 = (Color) ((Label) find("Item 3")).getTextFill();
		
		assertNotEquals(bg, bg2);
		assertNotEquals(ttl, ttl2);
		assertNotEquals(lttl, lttl2);
		assertNotEquals(rttl, rttl2);
		assertNotEquals(lcrcl, lcrcl2);
		assertNotEquals(rcrcl, rcrcl2);
		assertNotEquals("", ttl2txt);
		assertNotEquals("", lttl2txt);
		assertNotEquals("", rttl2txt);
		assertNotEquals(1.0, lcs);
		assertNotEquals(1.0, rcs);
		assertNotEquals(ltxt, ltxt2);
		assertNotEquals(itxt, itxt2);
		assertNotEquals(rtxt, rtxt2);
		
		while (!((Button) find("#undoButton")).isDisabled()) {
			clickOn("#undoButton");
			WaitForAsyncUtils.waitForFxEvents();
		}
		
		assertEquals(bg, ((Pane) find("#pane")).getStyle());
		assertEquals(ttl, ((TextField) find("#title")).getStyle());
		assertEquals(lttl, ((TextField) find("#circleLeftTitle")).getStyle());
		assertEquals(rttl, ((TextField) find("#circleRightTitle")).getStyle());
		assertEquals(lcrcl, (Color) ((Circle) find("#circleLeft")).getFill());
		assertEquals(rcrcl, (Color) ((Circle) find("#circleRight")).getFill());
		assertEquals("", ((TextField) find("#title")).getText());
		assertEquals("", ((TextField) find("#circleLeftTitle")).getText());
		assertEquals("", ((TextField) find("#circleRightTitle")).getText());
		assertEquals(1.0, ((Circle) find("#circleLeft")).getScaleX(), 0.01);
		assertEquals(1.0, ((Circle) find("#circleRight")).getScaleX(), 0.01);
		
		while (!((Button) find("#redoButton")).isDisabled()) {
			clickOn("#redoButton");
			WaitForAsyncUtils.waitForFxEvents();
		}
		
		assertEquals(bg2, ((Pane) find("#pane")).getStyle());
		assertEquals(ttl2, ((TextField) find("#title")).getStyle());
		assertEquals(lttl2, ((TextField) find("#circleLeftTitle")).getStyle());
		assertEquals(rttl2, ((TextField) find("#circleRightTitle")).getStyle());
		assertEquals(lcrcl2, (Color) ((Circle) find("#circleLeft")).getFill());
		assertEquals(rcrcl2, (Color) ((Circle) find("#circleRight")).getFill());
		assertEquals(ttl2txt, ((TextField) find("#title")).getText());
		assertEquals(lttl2txt, ((TextField) find("#circleLeftTitle")).getText());
		assertEquals(rttl2txt, ((TextField) find("#circleRightTitle")).getText());
		assertEquals(lcs, ((Circle) find("#circleLeft")).getScaleX(), 0.01);
		assertEquals(rcs, ((Circle) find("#circleRight")).getScaleX(), 0.01);
		assertEquals(ltxt2, (Color) ((Label) find("Item 1")).getTextFill());
		assertEquals(itxt2, (Color) ((Label) find("Item 2")).getTextFill());
		assertEquals(rtxt2, (Color) ((Label) find("Item 3")).getTextFill());
		
		while (!((Button) find("#undoButton")).isDisabled()) {
			clickOn("#undoButton");
			WaitForAsyncUtils.waitForFxEvents();
		}
		
		clickOn("#addItemField");
		WaitForAsyncUtils.waitForFxEvents();
		write("Test");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#addItemButton");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("#itemsList");
		WaitForAsyncUtils.waitForFxEvents();
		drag("Test").dropTo("#circleLeft");
		WaitForAsyncUtils.waitForFxEvents();
		Color ltxt3 = (Color) ((Label) find("Test")).getTextFill();
		drag("Test").moveTo("#circleLeft").dropBy(((Circle)(find("#circleLeft"))).getBoundsInParent().getWidth() / 2 - 110, 0);
		WaitForAsyncUtils.waitForFxEvents();
		Color itxt3 = (Color) ((Label) find("Test")).getTextFill();
		drag("Test").dropTo("#circleRight");
		WaitForAsyncUtils.waitForFxEvents();
		Color rtxt3 = (Color) ((Label) find("Test")).getTextFill();
		assertEquals(ltxt, ltxt3);
		assertEquals(itxt, itxt3);
		assertEquals(rtxt, rtxt3);
	}
}
