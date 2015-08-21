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
        int iStart = 0,             // Индекс начала тега
                iEnd;               // индекс конца названия тега
        int iCurrent;               // Индекс текущего положения поиска
        int attrStart = 0,          // Индекс начала атрибута
                attrEnd = 0;        // Индекс конца трибута
        int attrValueStart = 0,     // Индекс начала значения атрибута
                attrValueEnd = 0;   // Индекс конца значения атрибута
        int checkEndTagBracket;     // Индекс закрывающей скобки тега

        String temp_Class = null,          // Содержит класс текущего тега
                temp_Id = null;            // Содержит идентификатор текущего тега

        iCurrent = codeHTML.indexOf("body");  // Задаем начальный указатель на тело документа

        while(iCurrent != codeHTML.length() && iStart != -2) {

            temp_Class = null;
            temp_Id = null;

            iStart = searchNextTag(iCurrent); // находим начало следующего тега
            if (iStart == -2) { // если следующего тега нету - заканчиваем работу
                break;
            }
            if (iStart != -1) { // если это начало тега а не конец или комментарий, то
                iCurrent = iStart + 1;          // меняем указатель
                iEnd = searchEndTag(iCurrent);  // находим конец имени тега
                try {
                    if (codeHTML.charAt(iEnd+1) == ' ') {
                        attrStart = iEnd + 2;
                        checkEndTagBracket = searchEndTagBracket(iCurrent);
                        while (true) {
                            attrEnd = searchNextAttr(iCurrent) + 1;
                            iCurrent = attrStart;
                            attrValueStart = attrEnd + 2;
                            iCurrent = attrValueStart;
                            attrValueEnd = searchEndAttr(iCurrent);
                            if (codeHTML.substring(attrStart, attrEnd).equals("class")) {
                                temp_Class = codeHTML.substring(attrValueStart, attrValueEnd);
                            } else if (codeHTML.substring(attrStart, attrEnd).equals("id")) {
                                temp_Id = codeHTML.substring(attrValueStart, attrValueEnd);
                            }
                            attrStart = attrValueEnd + 2;
                            if (checkEndTagBracket < attrStart) {
                                break;
                            }
                        }
                    }
                    Tag temp = new Tag(codeHTML.substring(iStart, iEnd+1), temp_Class, temp_Id); // создаем экземпляр тега
                    tags.add(temp); // сохраняем тег в структуре
                } catch (Exception ex) {
                }

            }
            else {
                iCurrent++; // двигаем указатель на один вперед
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
            return -2;  // Больше тегов нету
        }
        if (codeHTML.charAt(i+1) == '!' || codeHTML.charAt(i+1) == '/' ) {
            return -1;  // Это не рабочий тег
        }
        else {
            return i+1; // Начало имени тега
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
            return i - 1;   // тег с атрибутами
        }
        else {
            if (j != -1)
            {
                return j-1; // тег закончился
            }
        }
        //System.out.println(i);
        return -1;
    }

    /**
     * Поиск последней скобки тега
     * @param iCurrent
     * @return
     */
    private int searchEndTagBracket(int iCurrent) {
        int j;
        j = codeHTML.indexOf(">",iCurrent);
        if (j != -1) {
            return j - 1; // тег закончился
        }
        return -1;
    }

    /**
     * Поиск следующего атрибута
     * @return
     */
    private int searchNextAttr(int iCurrent) {
        int i;
        i = codeHTML.indexOf("=",iCurrent);
        if (i != -1) {
            return i-1; // вернуть конец имени атрибута
        }
        return -1;
    }

    /**
     * Поиск конца атрибута
     * @param iCurrent
     * @return
     */
    private int searchEndAttr(int iCurrent) {
        int i;
        i = codeHTML.indexOf('"',iCurrent);
        if (i != -1) {
            return i; // вернуть конец значения атрибута
        }
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
            codeHTML += ".";
            codeHTML += tags.get(i).getClass_tag();
            codeHTML += "#";
            codeHTML += tags.get(i).getId();
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
