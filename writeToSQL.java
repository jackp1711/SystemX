import java.sql.*;
import java.util.Calendar;

public class writeToSQL {

    Timer session;

    private String websiteName;

    private long elapsedMinutes;
    private long remainingSeconds;

    private boolean productive;

    private Calendar cal;

    public writeToSQL(Timer t)
    {
        session = t;
        websiteName = t.getWebsite();

        elapsedMinutes = t.getElapsedMinutes();
        remainingSeconds = t.getRemainingSeconds();

        cal = t.getCurrentDate();
        productive = t.getProductive();

    }

    public void addToDatabase()
    {
        try {
            // create a mysql database connection
            String myDriver = "org.gjt.mm.mysql.Driver";
            String myUrl = "jdbc:mysql://localhost/test";
            Class.forName(myDriver);
            Connection conn = DriverManager.getConnection(myUrl, "root", "");

            // the mysql insert statement
            String query = " insert into users (website_name, time_spent, date_recorded, is_productive)"
                    + " values (?, ?, ?, ?, ?)";

            // create the mysql insert preparedstatement
            PreparedStatement preparedStmt = conn.prepareStatement(query);
            preparedStmt.setString(1, websiteName);
            preparedStmt.setString(2, elapsedMinutes + ":" + remainingSeconds );
            preparedStmt.setDate(3, new Date(1234561456)); //FIXME
            preparedStmt.setBoolean(4, productive);

            // execute the preparedstatement
            preparedStmt.execute();

            conn.close();
        }

        catch (Exception e)
        {
            System.err.println("Exception.");
            System.err.println(e.getMessage());
        }
    }
}
