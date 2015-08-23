package Main;

import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.AllTag;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class MainApp extends Application {

    private Stage primaryStage;
    private Parent rootLayout;

    private File file = new File("F:\\JavaProjects\\CSSGenerator\\txt\\1.html");
    private File fileSoloTags = new File("F:\\JavaProjects\\CSSGenerator\\src\\soloTags.txt");
    private AllTag allTags;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("CSSGenerator");
        try {
            allTags = new AllTag(new Scanner(file).useDelimiter("\\Z").next(),
                    new Scanner(fileSoloTags).useDelimiter("\\Z").next().split(" "));
        } catch (Exception ex) {
        }
        allTags.chooseTag();
        allTags.printCodeHTML();
        initRootLayout();
    }

    public AllTag getAllTags() {
        return allTags;
    }

    public void setAllTags(AllTag allTags) {
        this.allTags = allTags;
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("../view/mainView.fxml"));
            this.rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            MainController controller = loader.getController();
            controller.setMainApp(this);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
