package model;

import java.util.LinkedList;
import java.util.List;

public class Tag {
    String name;                                    // Название тега
    String class_tag;                               // Классы тега
    String id;                                      // Id тега
    boolean doubleTag = true;                       // Является ли тег двойным
    List<Tag> childrenTags = new LinkedList<Tag>(); // Дочерние теги
    String comment;                                 // Комментарий к тегу

    public Tag(String name) {
        this.name = name;
    }

    public Tag(String name, boolean doubleTag) {
        this.name = name;
        this.doubleTag = doubleTag;
    }

    public Tag(String name, String class_tag, String id) {
        this.name = name;
        this.class_tag = class_tag;
        this.id = id;
    }

    public Tag(String name, boolean doubleTag, List<Tag> childrenTags) {
        this.name = name;
        this.doubleTag = doubleTag;
        this.childrenTags = childrenTags;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String commentStart) {
        this.comment = commentStart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDoubleTag() {
        return doubleTag;
    }

    public void setDoubleTag(boolean doubleTag) {
        this.doubleTag = doubleTag;
    }

    public List<Tag> getChildrenTags() {
        return childrenTags;
    }

    public void setChildrenTags(List<Tag> childrenTags) {
        this.childrenTags = childrenTags;
    }

    public String getClass_tag() {
        return class_tag;
    }

    public void setClass_tag(String class_tag) {
        this.class_tag = class_tag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}