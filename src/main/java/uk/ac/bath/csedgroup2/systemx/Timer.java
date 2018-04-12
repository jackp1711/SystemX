package uk.ac.bath.csedgroup2.systemx;

public class Timer {

    private int startTime;
    private boolean timerOn;

    private String url;

    private DBF db;

    public Timer(DBF db) {
        this.db = db;
    }

    private int getCurrentTimestamp() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public void startTimer() {
        startTime = getCurrentTimestamp();
        timerOn = true;
    }

    public void startTimer(String url)
    {
        this.url = url;
        this.startTimer();
    }

    public void startTimer(String url, int startTime) {
        this.stopTimer();
        this.startTimer(url);
        this.startTime = startTime;
    }

    public void stopTimer(String url) {
        this.url = url;
        this.stopTimer();
    }

    public void stopTimer() {
        int now = this.getCurrentTimestamp();
        stopTimer(now);
    }

    public void stopTimer(int endTime) {
        if (timerOn && startTime >= 0) { //check if timer is running
            saveData(startTime, endTime, url);

            //reset timer
            this.timerOn = false;
            this.startTime = 0;
        }

    }

    public void saveData(int startTime, int endTime, String url)
    {
        System.out.println("Saving website: " + url + " duration: " + (endTime - startTime));

        this.db.storeData(startTime, endTime, url);
    }

    public int getStartTime()
    {
        return startTime;
    }
}
