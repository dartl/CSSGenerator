package Main;

import controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Tag;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class MainApp extends Application {

    private Stage primaryStage;
    private Parent rootLayout;
    private List<Tag> tags = new LinkedList<Tag>();
    private File file = new File("F:\\JavaProjects\\CSSGenerator\\txt\\index.html");
    private String codeHTML = new String();
    private String codeCSS = new String();

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("CSSGenerator");
        try {
            codeHTML = new Scanner(file).useDelimiter("\\Z").next();
        } catch (Exception ex) {
        }
        chooseTag();
        printCodeHTML();
        initRootLayout();
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

    /**
     * Построение тэгов
     * @return
     */
    private boolean chooseTag() {
        int iStart = 0,
                iEnd;
        int iCurrent;
        iCurrent = codeHTML.indexOf("body");
        while(iCurrent != codeHTML.length() && iStart != -2) {
            iStart = searchNextTag(iCurrent);
            if (iStart != -1) {
                iCurrent = iStart + 1;
                iEnd = searchEndTag(iCurrent);
                try {
                    Tag temp = new Tag(codeHTML.substring(iStart, iEnd+1));
                    tags.add(temp);
                } catch (Exception ex) {
                }
            }
            else {
                iCurrent++;
            }
        }
        return false;
    }

    /**
     * Поиск следующего тега
     * @return
     */
    private int searchNextTag(int iCurrent) {
        int i;
        i = codeHTML.indexOf("<",iCurrent);
        //System.out.println(i);
        if (i == -1) {
            return -2;
        }
        if (codeHTML.charAt(i+1) == '!' || codeHTML.charAt(i+1) == '/' ) {
            return -1;
        }
        else {
            return i+1;
        }
    }

    /**
     * Поиск конца начального тега
     * @param iCurrent
     * @return
     */
    private int searchEndTag(int iCurrent) {
        int i, j;
        i = codeHTML.indexOf(" ",iCurrent);
        j = codeHTML.indexOf(">",iCurrent);
        if (i<j && i != -1) {
            return i - 1;
        }
        else {
            if (j != -1)
            {
                return j-1;
            }
        }
        //System.out.println(i);
        return -1;
    }

    /**
     * Тестовая печать информации о теге
     * @return
     */
    private boolean printCodeHTML() {
        codeHTML = "";
        for (int i = 0; i < tags.size(); i++) {
            codeHTML += tags.get(i).getName();
            codeHTML += "\n";
        }
        return true;
    }

    public String getCodeHTML() {
        return codeHTML;
    }

    public void setCodeHTML(String codeHTML) {
        this.codeHTML = codeHTML;
    }

    public String getCodeCSS() {
        return codeCSS;
    }

    public void setCodeCSS(String codeCSS) {
        this.codeCSS = codeCSS;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
