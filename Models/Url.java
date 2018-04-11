package Models;

import com.j256.ormlite.field.DatabaseField;

public class Url extends CommonModel {
    @DatabaseField(id = true)
    private String title;
    @DatabaseField(foreign = true)
    private Category category;

    public Url() {

    }

    public Url(String title, Category category) {
        this.title = title;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String toString() {
        return this.getTitle();
    }
}
