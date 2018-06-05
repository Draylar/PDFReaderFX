import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
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
    private VBox box = new VBox();

    // menu objects
    private final MenuBar navBar = new MenuBar();
    private final Menu fileButton = new Menu("Menu");
    private final MenuItem menuItem = new MenuItem("Open PDF");

    // stage instance
    private Stage stage;

    // root
    private BorderPane root = new BorderPane();

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        // call setup functions
        checkLastSession();
        loadStartingPage();
        initPageClick();
        initCloseRequest();

        // add dropdown items to the first menu button, and then add the menu button to the menu bar
        fileButton.getItems().addAll(menuItem);
        navBar.getMenus().addAll(fileButton);

        // set up layout
        root.setCenter(box);
        root.setTop(navBar);
        box.getChildren().add(imageView);
        box.setAlignment(Pos.CENTER);

        // set up stage and scene
        scene = new Scene(root, width, height);
        primaryStage.setTitle("PDF FX");
        primaryStage.setScene(scene);

        // rock and roll
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args);  }


    /**
     * Sets up the close request for the application.
     * When the close button is pressed, the current PDF and page are saved to preferences for latter use.
     */
    private void initCloseRequest() {
        // set up the event for when we close the app
        stage.setOnCloseRequest(closeEvent -> {
            if(currentPDFFile != null && !prefs.isResettingPreferences) {
                prefs.setPDFDirectory(currentPDFFile);
                prefs.setCurrentPage(currentPage);
            } else { // user is resetting preferences
                prefs.setResettingPreferences(false);
            }
        });
    }


    /**
     * Sets up the click event for the main screen.
     * If the click is on the right side of the screen the page increases by one, and the left side turns back a page.
     */
    private void initPageClick() {
        box.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                if((mouseEvent.getX()) > (scene.getWidth() / 2)) {
                    nextPage();
                } else {
                    previousPage();
                }
            });
    }


    /**
     * Loads the starting page when the application is opened.
     * Should be called when the application starts.
     */
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


    /**
     * Moves to the next page.
     */
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


    /**
     * Goes to the previous page.
     */
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


    /**
     * Checks to see if a preference was set from the user previously using the application.
     * If the user did use the application before, we open the previously opened PDF and page.
     */
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


    /**
     * Opens the directory chooser so the user can select a PDF to read.
     * @return the selected file
     */
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
