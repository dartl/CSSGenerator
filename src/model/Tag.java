package model;

import java.util.LinkedList;
import java.util.List;

public class Tag {
    String name;                                    // Название тега
    boolean doubleTag;                              // Является ли тег двойным
    List<Tag> childrenTags = new LinkedList<Tag>(); // Дочерние теги

    public Tag(String name) {
        this.name = name;
        this.doubleTag = true;
    }

    public Tag(String name, boolean doubleTag) {
        this.name = name;
        this.doubleTag = doubleTag;
    }

    public Tag(String name, boolean doubleTag, List<Tag> childrenTags) {
        this.name = name;
        this.doubleTag = doubleTag;
        this.childrenTags = childrenTags;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        if (doubleTag != tag.doubleTag) return false;
        if (childrenTags != null ? !childrenTags.equals(tag.childrenTags) : tag.childrenTags != null) return false;
        if (name != null ? !name.equals(tag.name) : tag.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (doubleTag ? 1 : 0);
        result = 31 * result + (childrenTags != null ? childrenTags.hashCode() : 0);
        return result;
    }
}