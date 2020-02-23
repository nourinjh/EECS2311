package application;

import static org.junit.Assert.*;

import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

public class ControllerTest extends ApplicationTest {
	
	private static ControllerTest controller;
    
	@Before
	public void setUpClass() throws Exception {
		controller = new ControllerTest();
		ApplicationTest.launch(Main.class);
	}
	
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/application/Test.fxml"));

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
	}
    
    @Test
    public void testAddingItems() {
    	for (int i = 1; i <= 15; i++) {
	    	clickOn("#addItemField").write("Item " + i);
	    	clickOn("#addItemButton");
    	}
    	for (int i = 0; i < 3; i++) {
	    	drag("#itemsList").dropTo("#circleLeftItemsList");
	    	drag("#itemsList").dropTo("#bothItemsList");
	    	drag("#itemsList").dropTo("#circleRightItemsList");
    	}
    	// Do some sort of verification here
    }
    
    @Test
    public void testTitles() {
    	clickOn("#title").write("Example Diagram");
    	clickOn("#circleLeftTitle").write("Left Circle");
    	clickOn("#circleRightTitle").write("Right Circle");
    	// Do some sort of verification here
    }
    
    // Insert more tests here
    
    @After
    public void afterEachTest() throws Exception {
    	FxToolkit.hideStage();
    	release(new KeyCode[] {});
    	release(new MouseButton[] {});
    }
}
