package uk.ac.bath.csedgroup2.focusmonster;

import uk.ac.bath.csedgroup2.focusmonster.models.TimerEntry;

public class Timer {

    /** start time in seconds **/
    private int startTime;
    /** is the timer running? **/
    private boolean timerOn;
    /** url being tracked **/
    private String url;

    private DBF db;

    public Timer(DBF db) {
        this.db = db;
    }

    /**
     * @return current system timestamp in seconds
     */
    public static int getCurrentTimestamp() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    /**
     * Starts the timer with the current timestamp as startTime
     */
    public void startTimer() {
        startTime = getCurrentTimestamp();
        timerOn = true;
    }

    /**
     * starts the timer with the current timestamp and configures the url being tracked
     * @param url
     */
    public void startTimer(String url)
    {
        this.url = url;
        this.startTimer();
    }

    /**
     * Starts the timer with a specified url and a specified startTime
     * Mostly used by the tab_tracker, which always sends a timestamp with the request
     * @param url
     * @param startTime
     */
    public void startTimer(String url, int startTime) {
        this.stopTimer();
        this.startTimer(url);
        this.startTime = startTime;
    }

    /**
     * Stops timer with current timestamp, timer entry is saved with url specified
     * @param url
     */
    public void stopTimer(String url) {
        this.url = url;
        this.stopTimer();
    }

    /**
     * Stops timer with current timestamp and the url that has been set previously
     */
    public void stopTimer() {
        int now = getCurrentTimestamp();
        stopTimer(now);
    }

    /**
     * Stops timer at a specified timestamp and the url that has been set previously
     * @param endTime
     */
    public void stopTimer(int endTime) {
        if (timerOn && startTime >= 0) { //check if timer is running
            saveData(startTime, endTime, url);

            //reset timer
            this.timerOn = false;
            this.startTime = 0;
        }

    }

    /**
     * Passes data to DBF to be stored into database
     * @param startTime
     * @param endTime
     * @param url
     * @return TimerEntry or null as received from DBF
     */
    public TimerEntry saveData(int startTime, int endTime, String url) {
        //Check whether the url has been set and is not an empty string
        if (!("").equals(url) && url != null) {
            return this.db.storeData(startTime, endTime, url);
        }
        System.err.println("Url has not been set or is an empty string");
        return null;
    }
}
