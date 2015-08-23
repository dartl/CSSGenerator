package model;

import java.util.LinkedList;
import java.util.List;

public class AllTag {
    private List<Tag> tags = new LinkedList<Tag>(); // Список всех тегов
    private String codeHTML = new String();         // Код документа, а потом выходной код
    private String soloTags[];                      // Массив одинарных тегов

    /*
        Индексовые переменные
     */
    private int iCurrent;              // Индекс текущего положения поиска
    private int iTab = 0;              // Уровень табуляции

    public AllTag(String codeHTML, String[] soloTags) {
        this.codeHTML = codeHTML;
        this.soloTags = soloTags;
    }

    /**
     * Построение тэгов
     * @return
     */
    public boolean chooseTag() {
        iCurrent = codeHTML.indexOf("body") + 1;  // Задаем начальный указатель на тело документа
        Tag parent = new Tag("body");
        tags = addTag(parent);
        searchDoubleTags(tags);
        return false;
    }

    /**
     * Добавление нового тега
     */
    private List<Tag> addTag(Tag parent) {
        List<Tag> Tags = new LinkedList<Tag>();
        Tag tag = new Tag("null"); // создаем экземпляр тега
        int checkCurrent = 0;
        String tempComment;

        while(iCurrent <= codeHTML.length()) {

            checkCurrent = searchNextTag(iCurrent); // находим начало следующего тега
            if (checkCurrent == -1) { // если следующего тега нету - заканчиваем работу
                break;
            }
            if (checkCurrent == -3) {
                Tags.get(Tags.size()-1).setComment(searchComment(iCurrent));
                iCurrent = codeHTML.indexOf("-->", iCurrent);
                checkCurrent = searchNextTag(iCurrent); // находим начало следующего тега
            }
            if (checkCurrent != -2) { // если это начало тега а не конец или комментарий, то
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
                if (isSoloTag(tag)) {
                    iCurrent = l -= 2;
                }
                l = searchStartTagBracket(l) + 2; // находим начало следующего тега
                /*System.out.println(parent.getName() + " + " + codeHTML.substring(l, l + parent.getName().length() + 1) +
                " = " + codeHTML.substring(l, l + parent.getName().length() + 1).equals("/"+parent.getName()));*/
                if (codeHTML.substring(l, l + parent.getName().length() + 1).equals("/" + parent.getName())) {
                    break;
                }
            }
        }
        return Tags;
    }

    /**
     * Поиск начала имени следующего тега
     * @return
     */
    private int searchNextTag(int iCurrent) {
        int i;
        i = codeHTML.indexOf("<",iCurrent);
        if (i == -1) {
            return -1;  // Больше тегов нету
        }
        if (codeHTML.charAt(i+1) == '/' ) {
            this.iCurrent = i + 1;
            return -2;  // Это не рабочий тег
        }
        else if (codeHTML.charAt(i+1) == '!' ) {
            return -3;  // Это комментарий
        }
        else {
            this.iCurrent = i + 1;
            return i+1; // Начало имени тега
        }
    }

    /**
     * Поиск конца имени тега
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
     * Поиск конца названия атрибута
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
     * Проверка - есть ли у текущего тега дочерние элементы
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
     * Является ли тег одинарным
     * @param temp
     * @return
     */
    private boolean isSoloTag(Tag temp) {
        for (int i = 0; i < soloTags.length; i++) {
            if (soloTags[i].equals(temp.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Поиск комментария
     * @return
     */
    private String searchComment(int iCurrent) {
        return codeHTML.substring(codeHTML.indexOf("<!--") + 4,codeHTML.indexOf("-->"));
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
        if(j != -1 && textAtrr.charAt(j+3) == '=') {
            attrValueEnd = searchEndAttr(j+4, textAtrr);  // Ищем конец значения атрибута
            tag.setId(textAtrr.substring(j + 4, attrValueEnd)); // Добавляем идентификатор
            сurrent = attrValueEnd + 1;
        }

        iCurrent += сurrent;

        return tag;
    }

    /**
     * Тестовая печать информации о теге
     * @return
     */
    public boolean printCodeHTML() {
        codeHTML = "";
        String selector = "body";
        printCodeHTMLrecurs(tags, selector);
        return true;
    }

    /**
     * Тестовая печать информации о теге
     * @return
     */
    private boolean printCodeHTMLrecurs(List<Tag> tag, String selector) {
        String temp_selector = selector;
        String temp;

        for (int i = 0; i < tag.size(); i++) {
            temp = temp_selector + " " + tag.get(i).getName();
            printSelector(temp);
            if (tag.get(i).getClass_tag() != null) {
                printSelector(temp + "." + tag.get(i).getClass_tag());
            }
            if (tag.get(i).getId() != null) {
                printSelector(temp + "#" + tag.get(i).getId());
            }
            if (tag.get(i).getChildrenTags().size() != 0) {
                iTab++;
                printCodeHTMLrecurs(tag.get(i).getChildrenTags(), temp);
                iTab--;
            }
        }
        return true;
    }

    /**
     * Печать одного селектора
     */
    private void printSelector(String selector) {
        for (int j = 0; j < iTab; j++) {
            codeHTML += '\t';
        }
        codeHTML += selector + "{\n";
        for (int j = 0; j < iTab; j++) {
            codeHTML += '\t';
        }
        codeHTML += "}";
        codeHTML += "\n";
    }

    /**
     * Избавляемся от большинства ненужных повторяющихся элементов
     * @param tags
     */
    private void searchDoubleTags(List<Tag> tags) {
        for (int  i = 0; i < tags.size(); i ++) {
            for (int  j = 0; j < tags.size(); j ++) {
                if (i != j && tags.get(i).equals(tags.get(j))) {
                    tags.remove(j);
                }
            }
        }
        for (int  i = 0; i < tags.size(); i ++) {
            if (tags.get(i).getChildrenTags() != null) {
                searchDoubleTags(tags.get(i).getChildrenTags());
            }
        }
    }

    public String getCodeHTML() {
        return codeHTML;
    }

    public void setCodeHTML(String codeHTML) {
        this.codeHTML = codeHTML;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String[] getSoloTags() {
        return soloTags;
    }

    public void setSoloTags(String[] soloTags) {
        this.soloTags = soloTags;
    }
}
