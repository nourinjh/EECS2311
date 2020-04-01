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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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

	// exit controller
	@After
	public void afterEachTest() throws Exception {
		FxToolkit.hideStage();
		release(new KeyCode[] {});
		release(new MouseButton[] {});
	}

	@Test
	public void testAddingItems() {
		final int totalItems = 10;
		clickOn("#addItemField");
		for (int i = 1; i <= totalItems; i++) {
			write("Item " + i);
			clickOn("#addItemButton");
			WaitForAsyncUtils.waitForFxEvents();
		}
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

	// testing the buttons

	@SuppressWarnings("unchecked")
	// testing the delete button
	@Test
	public void testDeleteButton() {
		// TextField newItem = (TextField) find("#newItem");

		for (int i = 1; i < 6; i++) {
			clickOn("#addItemField").write("Item " + i);
			clickOn("#addItemButton");
		}

		WaitForAsyncUtils.waitForFxEvents();

		final int length = ((ListView<String>) (find("#itemsList"))).getItems().size();

		clickOn("Item 1");
		clickOn("#deleteButton");
		drag("Item 2").moveTo("#circleLeft").dropBy(-100, 0);
		drag("Item 3").moveTo("#circleRight").dropBy(-40, 0);
		clickOn("Item 2");
		clickOn("#deleteButton");
		clickOn("Item 3");
		clickOn("#deleteButton");

		WaitForAsyncUtils.waitForFxEvents();

		try {
			find("Item 1");
			fail();
		} catch (EmptyNodeQueryException e) {
			try {
				find("Item 2");
				fail();
			} catch (EmptyNodeQueryException f) {
				try {
					find("Item 3");
					fail();
				} catch (EmptyNodeQueryException g) {
				}
			}
		}
		assertEquals(length - 3, ((ListView<String>) (find("#itemsList"))).getItems().size());

	}

	// clear button
	@SuppressWarnings("unchecked")
	@Test
	public void testClearButton() {
		final int ITEMS = 5;
		for (int i = 1; i <= ITEMS; i++) {
			clickOn("#addItemField").write("Item " + i);
			clickOn("#addItemButton");
			WaitForAsyncUtils.waitForFxEvents();
		}
		
		drag("Item 1").moveTo("#circleLeft").dropBy(((Circle)(find("#circleLeft"))).getBoundsInParent().getWidth() / 2 - 110, 0);
		drag("Item 2").moveTo("#circleLeft").dropBy(-100, 0);
		drag("Item 3").moveTo("#circleRight").dropBy(-40, 0);
		WaitForAsyncUtils.waitForFxEvents();
		
		assertNotEquals(ITEMS, ((ListView<String>) (find("#itemsList"))).getItems().size());
		clickOn("#clearButton");
		WaitForAsyncUtils.waitForFxEvents();
		
		assertEquals(ITEMS, ((ListView<String>) (find("#itemsList"))).getItems().size());
		assertNotEquals(Label.class, find("Item 1").getClass());
	}

	// test screenshot button
	@Test
	public void testScreenShotButton() {
		String mainTitle = "Main";
		String leftTitle = "Left";
		clickOn("#title").write(mainTitle);
		clickOn("#circleLeftTitle").write(leftTitle);
		clickOn("#addItemField");
		WaitForAsyncUtils.waitForFxEvents();
		write("Test");
		clickOn("#addItemButton");
		WaitForAsyncUtils.waitForFxEvents();

		drag("Test").dropTo("#frameRect");
		clickOn("#exportButton");
		WaitForAsyncUtils.waitForFxEvents();
		clickOn("Image");
		WaitForAsyncUtils.waitForFxEvents();

		try {
		press(KeyCode.ENTER);
		Thread.sleep(500);
		release(KeyCode.ENTER);
		Thread.sleep(500);
		press(KeyCode.ENTER);
		Thread.sleep(500);
		release(KeyCode.ENTER);
		Thread.sleep(500);
		} catch (Exception e) {}
		
		assertEquals(mainTitle, ((TextField)(find("#title"))).getText());
		assertEquals(leftTitle, ((TextField)(find("#circleLeftTitle"))).getText());
		assertEquals("", ((TextField)(find("#circleRightTitle"))).getText());
	}

	// test colour picker
//	public void testColourPicker() {
//
//		// testing left colour
//		moveTo("#settingsPane").moveBy(0, -85).clickOn().moveBy(10, 10);
//		scroll(100, VerticalDirection.DOWN);
//		clickOn("#colorLeft").moveBy(10, 10).clickOn();
//
//		Circle left = find("#circleLeft");
//		left.setFill(Color.web("#000000"));
//		Paint color = left.getFill();
//		assertEquals(color, Color.web("#000000"));
//
//		// testing right colour
//
//		Circle right = find("#circleRight");
//		left.setFill(Color.web("#000000"));
//		Paint colorofright = right.getFill();
//		assertEquals(colorofright, Color.web("#000000"));
//
//	}
}
