package uk.ac.bath.csedgroup2.focusmonster.models;

import com.j256.ormlite.field.DatabaseField;

public class Category extends CommonModel {
    public static final boolean TYPE_LESS_THAN = false;
    public static final boolean TYPE_MORE_THAN = true;

    @DatabaseField(id = true)
    private String id;
    @DatabaseField(canBeNull = false)
    private String title;
    @DatabaseField
    private boolean goalType;
    @DatabaseField
    private int goal; //value in seconds, per week

    //aggregated value, not saved to DB
    private int duration;

    public Category() {

    }

    public Category(String title) {
        this.title = title;
        if (this.id == null) {
            this.id = generateId();
        }
    }

    public boolean isTypeMoreThan() {
        return !isTypeLessThan();
    }

    public boolean isTypeLessThan() {
        return goalType == Category.TYPE_LESS_THAN;
    }

    public boolean getGoalType() {
        return goalType;
    }

    public void setGoalType(boolean goalType) {
        this.goalType = goalType;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
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

    public static Category createNullCategory() {
        Category cat = new Category();
        cat.setTitle("Others");
        return cat;
    }

    public static String formatGoal(int seconds) {
        return "" + seconds;
    }

    public static int deformatTimestamp(String date) {
        return Integer.parseInt(date);
    }
}