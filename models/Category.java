package models;

import com.j256.ormlite.field.DatabaseField;

public class Category extends CommonModel {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField(canBeNull = false)
    private String title;

    //aggreavated value, not saved to DB
    private int duration;

    public Category() {

    }

    public Category(String title) {
        this.title = title;
        if (this.id == null) {
            this.id = generateId();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
