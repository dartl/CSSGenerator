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
        Индексовые переменные
     */
    private int iCurrent;              // Индекс текущего положения поиска
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
        iCurrent = codeHTML.indexOf("body") + 1;  // Задаем начальный указатель на тело документа
        Tag parent = new Tag("body");
        tags = addTag(parent);
        return false;
    }

    /**
     * Добавление нового тега
     */
    private List<Tag> addTag(Tag parent) {
        List<Tag> Tags = new LinkedList<Tag>();
        Tag tag = new Tag("null"); // создаем экземпляр тега
        int checkCurrent = 0;
        while(iCurrent <= codeHTML.length()) {

            checkCurrent = searchNextTag(iCurrent); // находим начало следующего тега
            if (checkCurrent == -2) { // если следующего тега нету - заканчиваем работу
                break;
            }
            if (checkCurrent != -1) { // если это начало тега а не конец или комментарий, то
                System.out.println(codeHTML.charAt(iCurrent));
                Tag tempTag = addTag(iCurrent); // Получаем текущий тег
                iCurrent++; // меняем указатель
                tag =  new Tag(tempTag.getName(), tempTag.getClass_tag(), tempTag.getId());
                if (haveChildren() && !isSoloTag(tag)) {
                    tag.setChildrenTags(addTag(tag));
                }
                if (isSoloTag(tag)) {
                    tag.setDoubleTag(false);
                }
                Tags.add(tag);  // Добавляем тег
            }
            else {
                iCurrent++; // двигаем указатель на один вперед
                int l = iCurrent;
                l = searchStartTagBracket(l) + 2; // находим начало следующего тега
                System.out.println(parent.getName());
                System.out.println(codeHTML.substring(l, l + parent.getName().length() + 1));
                System.out.println(codeHTML.substring(l, l + parent.getName().length() + 1).equals("/"+parent.getName()));
                if (codeHTML.substring(l, l + parent.getName().length() + 1).equals("/"+parent.getName()) || isSoloTag(tag)) {
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
        if (i == -1) {
            return -2;  // Больше тегов нету
        }
        if (codeHTML.charAt(i+1) == '/' ) {
            this.iCurrent = i + 1;
            return -1;  // Это не рабочий тег
        }
        else {
            this.iCurrent = i + 1;
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
     * Поиск следующей открывающей скобки
     * @param iCurrent
     * @return
     */
    private int searchStartTagBracket(int iCurrent) {
        int j;
        j = codeHTML.indexOf("<",iCurrent);
        if (j != -1) {
            return j - 1; // тег закончился
        }
        return -1;
    }

    /**
     * Поиск конца атрибута
     * @param iCurrent
     * @return
     */
    private int searchEndAttr(int iCurrent, String txt) {
        int i;
        i = txt.indexOf('"',iCurrent);
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
     *
     */
    private boolean haveEnd(String text) {
        int current = codeHTML.indexOf("</",iCurrent);
        System.out.println(codeHTML.charAt(current));
        System.out.println(codeHTML.substring(current, current + text.length() + 2));
        if (codeHTML.substring(current,current + text.length() + 2).indexOf("</"+text) != -1 ) {
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

    /**
     * Поиск комментария
     * @return
     */
    private String searchComment(int iCurrent) {
        return codeHTML.substring(iCurrent,codeHTML.indexOf("-->"));
    }

    /**
     * Обработка тега
     * @return
     */
    private Tag addTag(int сurrent) {
        int iStart = 0,             // Индекс начала тега
                iEnd;               // индекс конца названия тега
        int attrValueEnd = 0;       // Индекс конца значения атрибута
        int checkEndTagBracket;     // Индекс закрывающей скобки тега
        String textAtrr;            // Весь тег

        iStart = сurrent;           // Начало тега
        checkEndTagBracket = searchEndTagBracket(сurrent); // находим конец первого тега ">"

        textAtrr = codeHTML.substring(iStart,checkEndTagBracket+1); // Ищем конец атрибута

        Tag tag = new Tag("null"); // Создаем тег
        iEnd = сurrent = searchEndTag(сurrent);  // Ищем конец названия тега
        tag.setName(textAtrr.substring(0,iEnd-iStart+1));   // Устанавливаем имя тега
        сurrent = iEnd-iStart+1;
        int i = textAtrr.indexOf("class");  //  Находим начало класса у тега
        if(i != -1) {
            attrValueEnd = searchEndAttr(i+7, textAtrr);  // Ищем конец значения атрибута
            tag.setClass_tag(textAtrr.substring(i+7, attrValueEnd)); // Добавляем класс
            сurrent = attrValueEnd + 1;
        }

        int j = textAtrr.indexOf("id");  //  Находим начало идентификатора
        if(j != -1) {
            attrValueEnd = searchEndAttr(j+4, textAtrr);  // Ищем конец значения атрибута
            tag.setId(textAtrr.substring(j + 4, attrValueEnd)); // Добавляем идентификатор
            сurrent = attrValueEnd + 1;
        }

        iCurrent += сurrent;

        return tag;
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
