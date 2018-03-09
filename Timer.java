import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public Timer()
    {
        startTime = 0;
        timerOn = true;

        cal = Calendar.getInstance();
    }


    public void startTimer()
    {
        startTime = System.currentTimeMillis();
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

    public void stopTimer()
    {
        timerOn = false;

        getTime();
        calcRealTime();

        System.out.println("Total Time: " + elapsedSeconds + " seconds");
        System.out.println("Minutes: " + elapsedMinutes);
        System.out.println("Seconds: " + remainingSeconds);

        try
        {
            saveData();
        }
        catch (Exception e)
        {
            System.out.println("Data save failed");
        }
    }

    public void saveData() throws IOException
    {
        System.out.println("What was the website name?");

        website = br.readLine();

        System.out.println("Was the website productive? Y/N");

        productiveCheck(br.readLine());

    }

    public void productiveCheck(String s)
    {
        if (s.equals("Y"))
        {
            productive = true;
        }

        else if (s.equals("N"))
        {
            productive = false;
        }
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

    public static void main(String[] args) throws IOException
    {
        Timer t = new Timer();


        while(t.timerOn)
        {
            String s = t.br.readLine();

            if (s.equals("Start"))
            {
                t.startTimer();
            }

            else if (s.equals("Stop"))
            {
                t.stopTimer();

                historyOfUse.add(t);
            }
        }

        writeToSQL saveData = new writeToSQL(t);


        System.exit(0);
    }
}
