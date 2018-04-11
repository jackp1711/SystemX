package Models;

import com.j256.ormlite.field.DatabaseField;

public class Category extends CommonModel {
    @DatabaseField(id = true)
    private String title;

    public Category() {

    }

    public Category(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        return this.getTitle();
    }
}
