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

    // instances of managers
    private PreferenceManager prefs = new PreferenceManager();

    // numbers
    private int currentPage;
    private final int pdfScale = 1;
    private final int width = 600;
    private final int height = 900;

    // pdf-related objects
    private ImageView imageView;
    private WritableImage fxImage;
    private BufferedImage img;
    private PDFRenderer renderer;
    private PDDocument doc;
    private File currentPDFFile;

    // layouts
    private Scene scene;
    private VBox box;

    // menu objects
    private final MenuBar navBar = new MenuBar();
    private final Menu fileButton = new Menu("Menu");
    private final MenuItem menuItem = new MenuItem("Open PDF");

    // stage instance
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        // call setup functions
        checkLastSession();
        loadStartingPage();

        // box to store the image in
        box = new VBox();
        box.getChildren().add(imageView);

        // add box event
        pdfClickEventHandler();

        // add dropdown items to the first menu button, and then add the menu buttons to the menu bar
        fileButton.getItems().addAll(menuItem);
        navBar.getMenus().addAll(fileButton);

        // set up root
        BorderPane root = new BorderPane();
        root.setCenter(box);
        root.setTop(navBar);

        // set alignment
        box.setAlignment(Pos.CENTER);

        // create our scene with the borderpane and a default width/height
        // todo: add config options for width & height
        scene = new Scene(root, width, height);

        // set up the primary stage
        primaryStage.setTitle("PDF FX");
        primaryStage.setScene(scene);

        initCloseRequest();

        // rock and roll
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args);  }

    private void initCloseRequest() {
        // set up the event for when we close the app
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent event) {
                if(currentPDFFile != null && !prefs.isResettingPreferences) {
                    prefs.setPDFDirectory(currentPDFFile);
                    prefs.setCurrentPage(currentPage);
                } else { // user is resetting preferences
                    prefs.setResettingPreferences(false);
                }
            }
        });
    }

    private void pdfClickEventHandler() {
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

    private void loadStartingPage() {
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

    private void nextPage() {
        currentPage++;
        try {
            img = renderer.renderImage(currentPage, pdfScale);
        } catch (Exception e) {
            e.printStackTrace();
        }
        fxImage = SwingFXUtils.toFXImage(img, null);
        imageView.setImage(fxImage);
    }

    private void previousPage() {
        currentPage--;
        try {
            img = renderer.renderImage(currentPage, pdfScale);
        } catch (Exception e) {
            e.printStackTrace();
        }
        fxImage = SwingFXUtils.toFXImage(img, null);
        imageView.setImage(fxImage);
    }

    private void checkLastSession() {
        // if we don't have a directory from a previous session
        if(prefs.getPDFDirectory().toString().equals("noDirectory")) {
            currentPDFFile = openDirectoryChooser();
        } else {
            currentPDFFile = prefs.getPDFDirectory();
        }

        // now we check the previous page settings
        if(prefs.getCurrentPage() != 0) {
            currentPage = prefs.getCurrentPage();
        }
    }

    private File openDirectoryChooser() {
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
