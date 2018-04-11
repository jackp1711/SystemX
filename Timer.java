import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;

public class Timer {

    private long startTime;
    private long elapsedTime;

    private long elapsedSeconds;

    private long elapsedMinutes;
    private long remainingSeconds;

    private String website;
    private boolean productive;

    InputStreamReader ir = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(ir);

    private Calendar cal;

    public static ArrayList<Timer> historyOfUse = new ArrayList<>();

    private boolean timerOn;
    private DBF db;
    public Timer(DBF db)
    {
        this.db = db;
        startTime = 0;
        timerOn = true;

        cal = Calendar.getInstance();
    }


    public void startTimer()
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        startTime = timestamp.getTime();
        timerOn = true;
    }

    public void getTime()
    {
        elapsedTime = System.currentTimeMillis() - startTime;

    }

    public void calcRealTime()
    {
        calcSeconds();
        calcMinutesAndSeconds();
    }

    public void calcSeconds()
    {
        elapsedSeconds = elapsedTime / 1000;
    }

    public void calcMinutesAndSeconds()
    {
        elapsedMinutes = elapsedSeconds / 60;
        remainingSeconds = elapsedSeconds % 60;
    }

    public void stopTimer(String url)
    {
        timerOn = false;

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        int ts = (int) timestamp.getTime();

        getTime();
        calcRealTime();

        System.out.println("Total Time: " + elapsedSeconds + " seconds");
        System.out.println("Minutes: " + elapsedMinutes);
        System.out.println("Seconds: " + remainingSeconds);


        saveData(startTime, ts, url);
    }

    public void saveData(long startTime, long endTime, String url)
    {
        System.out.println("Saving website: " + url);

        this.db.storeData(startTime, endTime, url, "TIMER GUI");
    }

    public long getStartTime()
    {
        return startTime;
    }

    public long getElapsedTime()
    {
        return elapsedTime;
    }

    public long getElapsedSeconds()
    {
        return elapsedSeconds;
    }

    public long getRemainingSeconds()
    {
        return remainingSeconds;
    }

    public long getElapsedMinutes()
    {
        return elapsedMinutes;
    }

    public Calendar getCurrentDate()
    {
        return cal;
    }

    public boolean getProductive()
    {
        return productive;
    }

    public String getWebsite()
    {
        return website;
    }

    public Timer getHistory(int index)
    {
        return historyOfUse.get(index);
    }

}
