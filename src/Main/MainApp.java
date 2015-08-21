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
    private File fileSoloTags = new File("F:\\JavaProjects\\CSSGenerator\\src\\soloTags.txt");
    private String codeHTML = new String();
    private String codeCSS = new String();
    private String soloTags = new String();

    /*
        Индоксовые переменные
     */
    private int iStart = 0,             // Индекс начала тега
            iEnd;               // индекс конца названия тега
    private int iCurrent;               // Индекс текущего положения поиска
    private int attrStart = 0,          // Индекс начала атрибута
            attrEnd = 0;        // Индекс конца трибута
    private int attrValueStart = 0,     // Индекс начала значения атрибута
            attrValueEnd = 0;   // Индекс конца значения атрибута
    private int checkEndTagBracket;     // Индекс закрывающей скобки тега

    private String temp_Class = null,          // Содержит класс текущего тега
            temp_Id = null;            // Содержит идентификатор текущего тега

    private int iTab = 0;              // Уровень табуляции

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("CSSGenerator");
        try {
            codeHTML = new Scanner(file).useDelimiter("\\Z").next();
            soloTags = new Scanner(fileSoloTags).useDelimiter("\\Z").next();
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

        iCurrent = codeHTML.indexOf("body");  // Задаем начальный указатель на тело документа
        tags = addTag();

        return false;
    }

    /**
     * Добавление нового тега
     */
    private List<Tag> addTag() {
        List<Tag> Tags = new LinkedList<Tag>();
        Tag temp = new Tag(null); // создаем экземпляр тега
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
                            if (checkEndTagBracket <= attrStart) {
                                break;
                            }
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
                        }
                    }
                    temp = new Tag(codeHTML.substring(iStart, iEnd+1), temp_Class, temp_Id); // создаем экземпляр тега
                    if (haveChildren() && !isSoloTag(temp)) {
                        temp.setChildrenTags(addTag());
                    }
                    if (isSoloTag(temp)) {
                        temp.setDoubleTag(false);
                    }
                    Tags.add(temp); // сохраняем тег в структуре
                } catch (Exception ex) {
                   ex.printStackTrace(System.out);
                }

            }
            else {
                iCurrent++; // двигаем указатель на один вперед
                if (codeHTML.substring(iCurrent, iCurrent + temp.getName().length()).equals(temp.getName()) || isSoloTag(temp)) {
                    break;
                }
            }
        }
        return Tags;
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
        int i, j, k;
        i = codeHTML.indexOf(" ",iCurrent);
        j = codeHTML.indexOf(">",iCurrent);
        if (i < j && i != -1) {
            return i - 1;   // тег с атрибутами
        }
        else if (j != -1) {
            if (codeHTML.charAt(j-1) == '/') {
                return j-2; // тег закончился
            }
            return j-1; // тег закончился
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
     *
     */
    private boolean haveChildren() {
        int i;
        i = codeHTML.indexOf("<",iCurrent);
        if (codeHTML.charAt(i+1) != '!' && codeHTML.charAt(i+1) != '/' ) {
            return true;  // есть дочерние елементы
        }
        return false;
    }

    /**
     * Тестовая печать информации о теге
     * @return
     */
    private boolean printCodeHTML() {
        codeHTML = "";
        printCodeHTMLrecurs(tags);
        return true;
    }

    /**
     * Тестовая печать информации о теге
     * @return
     */
    private boolean printCodeHTMLrecurs(List<Tag> tag) {
        for (int i = 0; i < tag.size(); i++) {
            for (int j = 0; j < iTab; j++) {
                codeHTML += '\t';
            }
            codeHTML += tag.get(i).getName();
            codeHTML += ".";
            codeHTML += tag.get(i).getClass_tag();
            codeHTML += "#";
            codeHTML += tag.get(i).getId();
            codeHTML += "\n";
            if (tag.get(i).getChildrenTags().size() != 0) {
                iTab++;
                printCodeHTMLrecurs(tag.get(i).getChildrenTags());
                iTab--;
            }
        }
        return true;
    }

    /**
     * Является ли тег одинарным
     * @param temp
     * @return
     */
    private boolean isSoloTag(Tag temp) {
        if(soloTags.indexOf(temp.getName()) != -1) {
            return true;
        }
        return false;
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
