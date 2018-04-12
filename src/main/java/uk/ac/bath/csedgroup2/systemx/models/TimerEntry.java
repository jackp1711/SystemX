package uk.ac.bath.csedgroup2.systemx.models;

import com.j256.ormlite.field.DatabaseField;

public class TimerEntry extends CommonModel {
    @DatabaseField(id = true)
    private String id;
    @DatabaseField(foreign = true)
    private Url url;
    @DatabaseField
    private int start;
    @DatabaseField
    private int end;
    @DatabaseField
    private int duration;

    public TimerEntry() {

    }

    public TimerEntry(Url url, int start, int end, int duration) {
        this(generateId(), url, start, end, duration);
    }

    public TimerEntry(String id, Url url, int start, int end, int duration) {
        this.id = id;
        this.url = url;
        this.start = start;
        this.end = end;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String toString() {
        return this.getUrl() + " - " + this.getDuration();
    }
}
