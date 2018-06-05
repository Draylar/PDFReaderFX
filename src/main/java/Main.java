import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;


import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main extends Application {

    PreferenceManager prefs = new PreferenceManager();

    public int currentPage;

    public final int pdfScale = 1;
    public final int width = 600;
    public final int height = 900;

    public ImageView imageView;
    public WritableImage fxImage;
    public BufferedImage img;
    public PDFRenderer renderer;
    public PDDocument doc;

    public Scene scene;
    public VBox box;

    final MenuBar navBar = new MenuBar();
    final Menu fileButton = new Menu("Menu");
    final MenuItem menuItem = new MenuItem("Open PDF");

    File currentPDFFile;

    public static void main(String[] args) { launch(args);  }

    @Override
    public void start(Stage primaryStage) {
        // prefs.resetPreferences();

        // check previous sessions basically sees if the user has used the application before
        // and if they were viewing a pdf before they closed it last
        checkLastSession();
        // load the starting page
        loadStartingPage();

        // box to store the image in
        box = new VBox();
        box.getChildren().add(imageView);

        // add box event
        pdfClickEventHandler();

        // add dropdown items to the first menu button, and then add the menu buttons to the menu bar
        fileButton.getItems().addAll(menuItem);
        navBar.getMenus().addAll(fileButton);

        // create a borderpane. borderpanes organize content like this:
        /*
                                 top window
                    |----|-----------------------------|----|
                    |    |_____________________________|    |
                    |    |                             |    |
                    |    |                             |    |
                    |    |            center           |    |
                    |    |                             |    |  right window
        left window |    |                             |    |
                    |    |_____________________________|    |
                    |    |                             |    |
                    |----|-----------------------------|----|
                                  center window
         */
        BorderPane root = new BorderPane();

        // now we're going to set the center to the main pdf and the top to the nav bar
        root.setCenter(box);
        root.setTop(navBar);

        // as well as set the VBox alignment to center so the pdf is centered when we resize the application
        box.setAlignment(Pos.CENTER);

        // create our scene with the borderpane and a default width/height
        // todo: add config options for width & height
        scene = new Scene(root, width, height);

        // set up the primary stage
        primaryStage.setTitle("PDF FX");
        primaryStage.setScene(scene);

        // set up the event for when we close the app
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                if(currentPDFFile != null && !prefs.isResettingPreferences) {
                    prefs.setPDFDirectory(currentPDFFile);
                    prefs.setCurrentPage(currentPage);
                } else { // user is resetting preferences
                    prefs.setResettingPreferences(false);
                }
            }
        });

        // rock and roll
        primaryStage.show();
    }

    public void pdfClickEventHandler() {
        box.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                if((event.getX()) > (scene.getWidth() / 2)) {
                    nextPage();
                } else {
                    previousPage();
                }
            }
        });
    }

    public void loadStartingPage() {
        try {
            doc = PDDocument.load(currentPDFFile);
            renderer = new PDFRenderer(doc);
            img = renderer.renderImage(currentPage, pdfScale);
        } catch (IOException e) {
            e.printStackTrace();
        }

        fxImage = SwingFXUtils.toFXImage(img, null);
        imageView = new ImageView(fxImage);
    }

    public void nextPage() {
        currentPage++;
        try {
            img = renderer.renderImage(currentPage, pdfScale);
        } catch (Exception e) {
            e.printStackTrace();
        }
        fxImage = SwingFXUtils.toFXImage(img, null);
        imageView.setImage(fxImage);
    }

    public void previousPage() {
        currentPage--;
        try {
            img = renderer.renderImage(currentPage, pdfScale);
        } catch (Exception e) {
            e.printStackTrace();
        }
        fxImage = SwingFXUtils.toFXImage(img, null);
        imageView.setImage(fxImage);
    }

    public void checkLastSession() {
        // if we don't have a directory from a previous session
        if(prefs.getPDFDirectory().toString() == "noDirectory") {
            currentPDFFile = openDirectoryChooser();
        } else {
            currentPDFFile = prefs.getPDFDirectory();
        }

        // now we check the previous page settings
        if(prefs.getCurrentPage() != 0) {
            currentPage = prefs.getCurrentPage();
        }
    }

    public File openDirectoryChooser() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JFileChooser fileChooser = new JFileChooser();

        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle("Choose a PDF to read");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("getSelectedFile() : " + fileChooser.getSelectedFile());
        } else {
            System.out.println("No Selection ");
        }

        return fileChooser.getSelectedFile();
    }

    }
