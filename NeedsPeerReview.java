/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package texteditorprojonmyown;

import java.awt.Dimension;
import java.awt.FileDialog;
import java.io.FileInputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.property.DoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import java.nio.file.Files;
import java.nio.charset.Charset;
import java.util.HashMap;
/**
 *
 * @author 03-aaliyah
 */
public class NeedsPeerReview extends Application {

    TabPane tbPane;
    MenuBar menuArea;
    MenuBar tstBar;
    ArrayList<String> readLines = new ArrayList<String>();
    ArrayList<File> filePointers = new ArrayList<File>();
    HashMap<Tab, File> tabFiles = new HashMap<Tab, File>(); 
    FileChooser fileLoader = new FileChooser();
    FileChooser fileSaver = new FileChooser(); 
    
    public Tab getCompleteTab(){
       Tab newTab = new Tab();
       TextArea txtArea = new TextArea();
       ScrollPane scrPane = new ScrollPane();
       txtArea.setPrefColumnCount(135);
       txtArea.setPrefRowCount(57);
       scrPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
       scrPane.setFitToHeight(true);
       scrPane.setFitToWidth(true);
       scrPane.setContent(txtArea);
       newTab.setContent(scrPane);
       newTab.setClosable(true);
       return newTab;
    }

    public MenuBar returnMenuBar(){
        menuArea = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem nwTxtAr = new MenuItem("New");
        MenuItem svTxtAr = new MenuItem("Save");
        MenuItem ldTxtAr = new MenuItem("Load");
        MenuItem svAsAr = new MenuItem("Save as");
        fileMenu.getItems().add(nwTxtAr);
        fileMenu.getItems().add(svTxtAr);
        fileMenu.getItems().add(ldTxtAr);
        fileMenu.getItems().add(svAsAr);
        Menu btnMenu = new Menu("Buttons");
        MenuItem someButton = new MenuItem("FixSomething");
        Menu sbTxtAr = new Menu("SubTextArea");
        MenuItem clrAr = new MenuItem("ClearSub");
        sbTxtAr.getItems().add(clrAr);
        menuArea.getMenus().addAll(fileMenu, btnMenu, sbTxtAr);
        return menuArea;
    }

    public AnchorPane returnAnchorPane(){
        AnchorPane anchorPane = new AnchorPane();
        return anchorPane;
    }

    public void saveSource(){
        for(int c = 0; c < tbPane.getTabs().size(); c++){            
            if(tbPane.getTabs().get(c).isSelected()){ //Whichever tab that is focused will be subjected to an operation
                                                      //Meaning, when represented in FA diagram, "focused" could be like innitial state.
                //If there is a File object associated with the selected tab, then, save to that path.
                Tab selectedTab = tbPane.getTabs().get(c);
                
                if(!(tabFiles.get(selectedTab) == null)){
                    ScrollPane scrPn = (ScrollPane) tbPane.getTabs().get(c).getContent();
                    TextArea txtTst = (TextArea) scrPn.getContent();
                    File someFile = tabFiles.get(selectedTab);           
                    try {
                        FileWriter fileWrtr = new FileWriter(someFile);
                        fileWrtr.write(txtTst.getText());
                        fileWrtr.close();
                        return;
                    } catch (IOException ex) {
                        Logger.getLogger(NeedsPeerReview.class.getName())
                            .log(Level.SEVERE, null, ex);
                    }
                }
                
                saveSourceAs();
                return;
            }
        }
    }
   
    public void saveSourceAs(){
        Tab selectedTab = null; // So, the compiler doesn't scream at me for not innitializing.
        String focusedName = ""; // Same reason as above. 

        fileSaver.setTitle("Save as");
        fileSaver.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("standard text files", "*.txt"),
            new FileChooser.ExtensionFilter("java files", "*.java"));
        File saveAsFile = fileSaver.showSaveDialog(new Stage());
        
        for(int c = 0; c < tbPane.getTabs().size(); c++){
            if(tbPane.getTabs().get(c).isSelected()){
                selectedTab = tbPane.getTabs().get(c);
                focusedName = saveAsFile.getName();
                ScrollPane scrPn = (ScrollPane)tbPane.getTabs().get(c).getContent();
                TextArea txtTst = (TextArea)scrPn.getContent();
                try{
                    FileWriter fileWriter = new FileWriter(saveAsFile);
                    fileWriter.write(txtTst.getText());
                    fileWriter.close();
                    tbPane.getTabs().get(c).setText(focusedName);
                    break;
                }catch(Exception ex){
                    Logger.getLogger(NeedsPeerReview.class.getName())
                            .log(Level.SEVERE, null, ex);
                }
            }
        }
    
        fileSaver.setInitialDirectory(saveAsFile.getParentFile()); // This line enables the saveDialog to open up on the last save location.
        tabFiles.put(selectedTab, saveAsFile); // So there is an association between Tab and the saved file.
    }
        
    public void loadSource(){
        fileLoader.setTitle("FileChooser");
        File loadedFile = fileLoader.showOpenDialog(new Stage());
        Tab newTab = getCompleteTab();
        ScrollPane newScrl = (ScrollPane) newTab.getContent();
        TextArea newTxtAr = (TextArea) newScrl.getContent();
        try {
            Charset charset = Charset.forName("ISO-8859-1");
            readLines.addAll(Files.readAllLines(loadedFile.toPath(), charset));
            for(int c = 0; c < readLines.size(); c++){
                newTxtAr.appendText(readLines.get(c));
                newTxtAr.appendText("\n");
            }
        } catch (Exception ex) {
            Logger.getLogger(NeedsPeerReview.class.getName()).log(Level.SEVERE, null, ex);
        }
        newTab.setText(loadedFile.getName());
        newTab.setClosable(true);
        newTab.setContent(newScrl);
        tbPane.getTabs().add(newTab);
        fileLoader.setInitialDirectory(loadedFile.getParentFile());
    }
    
    public void innitAll(){
        tstBar = returnMenuBar();
        tbPane = new TabPane();
        tbPane.setPrefSize(900, 800);
        tstBar.getMenus().get(0).getItems().get(0).setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e){
                Tab newTab = getCompleteTab();
                newTab.setText("NewTab");
                tbPane.getTabs().add(newTab);
                tabFiles.put(newTab, null);
            }
        });
        tstBar.getMenus().get(0).getItems().get(1).setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e){
                saveSource();
            }
        });
        tstBar.getMenus().get(0).getItems().get(2).setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e){
                loadSource();
            }
        });
        tstBar.getMenus().get(0).getItems().get(3).setOnAction(new EventHandler<ActionEvent>(){
            public void handle(ActionEvent e){
                saveSourceAs();
            }
        });            
    }
          
    public void start(Stage primaryStage) {

        innitAll();
        tbPane.prefWidthProperty().bind(primaryStage.widthProperty().subtract(150));
        tbPane.prefHeightProperty().bind(primaryStage.heightProperty().subtract(100));
        menuArea.prefWidthProperty().bind(primaryStage.widthProperty());
        Tab newTab = getCompleteTab();
        newTab.setText("NewTab");
        tbPane.getTabs().add(newTab);
        tabFiles.put(newTab, null);
        
        AnchorPane tstAnchor = returnAnchorPane();
        tstAnchor.setTopAnchor(tbPane, 25.0);
        tstAnchor.setLeftAnchor(tbPane, 10.0);
        tstAnchor.getChildren().add(tbPane);

        Group group = new Group(tstAnchor, menuArea);
        Scene scene = new Scene(group);

        primaryStage.setHeight(900);
        primaryStage.setWidth(1200);
        primaryStage.setResizable(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}