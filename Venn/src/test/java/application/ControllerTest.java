package application;

import static org.junit.Assert.*;


//import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

//import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.VerticalDirection;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

//import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

//import javafx.application.Platform;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;


import javafx.scene.control.*;
//import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
//import javafx.scene.paint.ColorBuilder;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
//import org.testfx.api.FxToolkit;
//import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.File;
//import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;



public class ControllerTest extends ApplicationTest {
	
	//widgets used 
	/*
	 * ObservableList<String> items; ObservableList<String> circleLeftItems;
	 * ObservableList<String> circleRightItems; ObservableList<String> bothItems;
	 * ListView<String> circleLeftItemsList; ListView<String> circleRightItemsList;
	 * ListView<String> bothItemsList;
	 */
Circle circleLeft;
Circle circleRight;
AnchorPane pane;
VBox floatingMenu;
GridPane buttonGrid;
TitledPane settingsPane;
VBox settingsBox;
ColorPicker colorLeft;
String DEFAULT_LEFT_COLOUR = "0xf59f9f";
Slider leftSizeSlider;
TextField leftSizeField;
ColorPicker colorRight;
String DEFAULT_RIGHT_COLOUR = "0xebe071";
Slider rightSizeSlider;
TextField rightSizeField;
MenuBar menuBar;
Button screenshotButton;
Pane frameRect;
ColorPicker colorBackground;
ColorPicker colorTitles;
Button deleteButton;
Button clearButton;
Button loadButton;
Button saveButton;

TextField title;
TextField circleLeftTitle;
TextField circleRightTitle;
TextField addItemField;
Button addItemButton;
ListView<String> itemsList;
Button addImageButton;
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
        
        //retrieve widgets in the GUI. */
        public <T extends Node> T find(final String query) {
            /* TestFX provides many operations to retrieve elements from the loaded GUI. */
            return lookup(query).query();
        }
        
        @Before
        public void setUp() {
            /*  retrieving the tested widgets from the GUI. */ 
             
            
        	
		/*
		 * circleLeftItems=find("#circleLeftItems");
		 * circleRightItems=find("#circleRightItems"); bothItems=find("#bothItems");
		 * circleLeftItemsList=find("#circleLeftItemsList");
		 * circleRightItemsList=find("#circleRightItemsList");
		 * bothItemsList=find("#bothItemsList");
		 */
        	circleLeft=find("#circleLeft");
        	circleRight=find("#circleRight");
        	pane=find("#pane");
        	floatingMenu=find("#floatingMenu");
        	buttonGrid=find("#buttonGrid");
        	settingsPane=find("#settingsPane");
        	settingsBox=find("#settingsBox");
        	colorLeft=find("#colorLeft");
        	//DEFAULT_LEFT_COLOUR = "0xf59f9f";
        	leftSizeSlider=find("#leftSizeSlider");
        	leftSizeField=find("#leftSizeField");
        	colorRight=find("#colorRight");
        	//DEFAULT_RIGHT_COLOUR = "0xebe071";
        	rightSizeSlider=find("#rightSizeSlider");
        	rightSizeField=find("#rightSizeField");
        	menuBar=find("#menuBar");
        	screenshotButton=find("#screenshotButton");
        	frameRect=find("#frameRect");
        	colorBackground=find("#colorBackground");
        	colorTitles=find("#colorTitles");
        	deleteButton=find("#deleteButton");
        	clearButton=find("#clearButton");
        	loadButton=find("#loadButton");
        	saveButton=find("#saveButton");

        	title=find("#title");
        	circleLeftTitle=find("#circleLeftTitle");
        	circleRightTitle=find("#circleRightTitle");
        	addItemField=find("#addItemField");
        	addItemButton=find("#addItemButton");
        	itemsList=find("#itemsList");
        	addImageButton=find("#addImageButton");
        	//controller= find("#items");
        	
        }
	//clearing events 
        @After
        public void tearDown() throws TimeoutException {
            /* Close the window. It will be re-opened at the next test. */
            FxToolkit.hideStage();
            release(new KeyCode[] {});
            release(new MouseButton[] {});
        }
        
  //exit controller
        @After
        public void afterEachTest() throws Exception {
        	FxToolkit.hideStage();
        	release(new KeyCode[] {});
        	release(new MouseButton[] {});
        }
        
        @Test
        public void testWidgetsExist() {
            final String errorMsg = "One or more widget dont exist";
       
		/*
		 * assertNotNull(errorMsg,items); assertNotNull(errorMsg,circleLeftItems);
		 * assertNotNull(errorMsg, circleRightItems); assertNotNull(errorMsg,
		 * bothItems); assertNotNull(errorMsg,circleLeftItemsList);
		 * assertNotNull(errorMsg,circleRightItemsList); assertNotNull(errorMsg,
		 * bothItemsList);
		 */
            assertNotNull(errorMsg,circleLeft);
            assertNotNull(errorMsg,circleRight);
            assertNotNull(errorMsg, pane);
            assertNotNull(errorMsg, floatingMenu);
            assertNotNull(errorMsg,buttonGrid);
            assertNotNull(errorMsg, settingsPane);
            assertNotNull(errorMsg,settingsBox);
            assertNotNull(errorMsg,colorLeft);
            
            assertNotNull(errorMsg,leftSizeSlider);
            assertNotNull(errorMsg,leftSizeField);
            assertNotNull(errorMsg,colorRight);
            
            assertNotNull(errorMsg,rightSizeSlider);
            assertNotNull(errorMsg,rightSizeField);
            assertNotNull(errorMsg,menuBar);
            assertNotNull(errorMsg, screenshotButton);
            assertNotNull(errorMsg,frameRect);
            assertNotNull(errorMsg,colorBackground);
            assertNotNull(errorMsg,colorTitles);
            assertNotNull(errorMsg,deleteButton);
            assertNotNull(errorMsg,clearButton);
            assertNotNull(errorMsg, loadButton);
            assertNotNull(errorMsg,saveButton);

            assertNotNull(errorMsg, title);
            assertNotNull(errorMsg,circleLeftTitle);
            assertNotNull(errorMsg,circleRightTitle);
            assertNotNull(errorMsg, addItemField);
            assertNotNull(errorMsg, addItemButton);
            assertNotNull(errorMsg, itemsList);
            assertNotNull(errorMsg,addImageButton);
            
        }
    
    @SuppressWarnings("unchecked")
	@Test
    public void testAddingItems() {
    	final int totalItems = 15;
    	final int itemsPerCircle = 3;
    	for (int i = 1; i <= totalItems; i++) {
	    	clickOn("#addItemField").write("Item " + i);
	    	clickOn("#addItemButton");
    	}
    	for (int i = 0; i < itemsPerCircle; i++) {
	    	drag("#itemsList").dropTo("#circleLeftItemsList");
	    	drag("#itemsList").dropTo("#bothItemsList");
	    	drag("#itemsList").dropTo("#circleRightItemsList");
    	}
    	
    	assertEquals(((ListView<String>)(find("#itemsList"))).getItems().size(), totalItems - (3 * itemsPerCircle));
    	assertEquals(((ListView<String>)(find("#circleLeftItemsList"))).getItems().size(), itemsPerCircle);
    	assertEquals(((ListView<String>)(find("#bothItemsList"))).getItems().size(), itemsPerCircle);
    	assertEquals(((ListView<String>)(find("#circleRightItemsList"))).getItems().size(), itemsPerCircle);
    }
    
    //test input text 
    
    
    @Test
    public void testTitles() {
    	clickOn("#title").write("Example Diagram");
    	clickOn("#circleLeftTitle").write("Left Circle");
    	clickOn("#circleRightTitle").write("Right Circle");
    	
    	assertEquals(((TextField)(find("#title"))).getText(), "Example Diagram");
    	assertEquals(((TextField)(find("#circleLeftTitle"))).getText(), "Left Circle");
    	assertEquals(((TextField)(find("#circleRightTitle"))).getText(), "Right Circle");
    }
    
    // Insert more tests here
    @Test
    public void testInput () {
    	//TextField newItem =  (TextField) find("#newItem");

    	  clickOn("#addItemField").write("This is a test input");
    	  clickOn("#addItemButton");
    	  
    	}
    
    //testing the buttons
    
    @SuppressWarnings("unchecked")
	//testing the delete button
    @Test
    public void testDeleteButton () {
    //TextField newItem =  (TextField) find("#newItem");
    
    	for (int i = 1; i <= 6; i++) {
	    	clickOn("#addItemField").write("Item " + i);
	    	clickOn("#addItemButton");
    	}
    	
	    WaitForAsyncUtils.waitForFxEvents();
	    
	    final int length = ((ListView<String>)(find("#itemsList"))).getItems().size();
	
	    clickOn("#itemsList");
	    clickOn("#deleteButton");
	    
	    WaitForAsyncUtils.waitForFxEvents();
	    
	    assertEquals(((ListView<String>)(find("#itemsList"))).getItems().size(), length-1);
	    
    }
    //clear button
    @SuppressWarnings("unchecked")
	@Test
    public void testClearButton () {
    	for (int i = 1; i <= 15; i++) {
	    	clickOn("#addItemField").write("Item " + i);
	    	clickOn("#addItemButton");
    	}
    
	    
	    for (int i = 0; i < 3; i++) {
	    	drag("#itemsList").dropTo("#circleLeftItemsList");
	    	drag("#itemsList").dropTo("#bothItemsList");
	    	drag("#itemsList").dropTo("#circleRightItemsList");
		}
	    clickOn("#clearButton");
	    
	    WaitForAsyncUtils.waitForFxEvents();
	    
	   assertEquals(((ListView<String>)(find("#circleLeftItemsList"))).getItems().size(), 0);
	   assertEquals(((ListView<String>)(find("#circleRightItemsList"))).getItems().size(), 0);
	   assertEquals(((ListView<String>)(find("#bothItemsList"))).getItems().size(), 0);
	    
	    
	   
     }
    
    //test screenshot button
    public void testScreenShotButton () {
    
	    clickOn("#addItemField").write("dltstuff ");
	    clickOn("#addItemButton");
	    
	    drag("#itemsList").dropTo("#bothItemsList");
	    
	    clickOn("#screenshotButton");
	    
	    WaitForAsyncUtils.waitForFxEvents();
    
    }
    
    //test colour picker 
    @Test
    public void testColourPicker () {
    	
	//testing left colour
    	clickOn("#settingsPane").moveBy(5, 5);
    	scroll(10, VerticalDirection.DOWN);
    	clickOn("#colorLeft").moveBy(5, 5).clickOn();
    	
    	Circle left = find("#circleLeft");
    	left.setFill(Color.web("#000000"));
    	Paint color = left.getFill();
        assertEquals(color, Color.web("#000000"));	
        
    //testing right colour
        
        Circle right = find("#circleRight");
    	left.setFill(Color.web("#000000"));
    	Paint colorofright = right.getFill();
        assertEquals(colorofright, Color.web("#000000"));
    
    }
}
    
    
    
 
    
    
    
    
    
    
    
    
   
   
	

