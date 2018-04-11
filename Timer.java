import java.sql.Timestamp;

public class Timer {

    private long startTime;
    private boolean timerOn;

    private String url;

    private DBF db;

    public Timer(DBF db) {
        this.db = db;
    }

    private long getCurrentTimestamp() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return timestamp.getTime();
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

    public void stopTimer(String url) {
        this.url = url;
        this.stopTimer();
    }

    public void stopTimer() {
        if (timerOn && startTime >= 0) { //check if timer is running
            long now = this.getCurrentTimestamp();
            saveData(startTime, now, url);

            //reset timer
            this.timerOn = false;
            this.startTime = 0;
        } else {
            System.err.println("Tried to stop timer when it was not running");
        }

    }

    public void saveData(long startTime, long endTime, String url)
    {
        System.out.println("Saving website: " + url + " duration: " + (endTime - startTime));

        this.db.storeData(startTime, endTime, url, "TIMER GUI");
    }

    public long getStartTime()
    {
        return startTime;
    }
}
