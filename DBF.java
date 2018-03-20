import java.sql.*;
import java.util.ArrayList;

public class DBF {

    public DBF() {
        this.Create();
        //this.StoreData(1, 2, "32", 1, "work","me","www.fuckoff" );
        //this.GetData();
        //this.getField("Site");
        //this.getTimes("www.fuckoff");
    }

    public void Create(){
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:PersonalData.db");
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "CREATE TABLE timer " +
                    "(StartTime         INT, " +
                    " EndTime           INT," +
                    " MyDate            DATE, " +
                    " TimeSpent         INT," +
                    " Category          VARCHAR(20)," +
                    " Source            VARCHAR(20)," +
                    " Site              VARCHAR(30))";
            stmt.executeUpdate(sql);
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            //e.printStackTrace();
            System.out.println("DB already initialized");
        }
        System.out.println("Table created successfully");
    }

    public void storeData(long startTime, long endTime, String date, int timeSpent, String cat, String Source, String site){
        Connection c = null;
        Statement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:PersonalData.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            String sql = "INSERT INTO timer (StartTime,EndTime,MyDate,TimeSpent,Category,Source,Site)" +
                    "VALUES ("+startTime+","+endTime+", '"+date+"',"+timeSpent+",'"+cat+"','"+Source+"','"+site+"' );";
            stmt.executeUpdate(sql);

            stmt.close();
            c.commit();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Records created successfully");

    }

    public void GetData(){
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:PersonalData.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM timer;" );
            while ( rs.next() ) {
                int startTime = rs.getInt("StartTime");
                int endTime = rs.getInt("EndTime");
                String date = rs.getString("MyDate");
                int time  = rs.getInt("TimeSpent");
                String cat = rs.getString("Category");
                String Source = rs.getString("Source");
                String site = rs.getString("Site");


                System.out.println(startTime+" "+endTime+" "+date+" "+time+" "+cat+" "+Source+" "+site);
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Operation done successfully");

    }

    public ArrayList<String> getField(String Field) {

        ArrayList<String> Titles = new ArrayList<String>();
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:PersonalData.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM timer;" );
            String nextEntity;
            while ( rs.next() ) {
                nextEntity = rs.getString(Field);
                if (!Titles.contains(nextEntity)) {
                    Titles.add(nextEntity);
                    System.out.println(nextEntity);
                }

            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return(Titles);
    }

    public ArrayList<Integer> getTimes(String title){
        ArrayList<Integer> Times = new ArrayList<Integer>();
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:PersonalData.db");
            c.setAutoCommit(false);
            System.out.println("Opened database successfully");

            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT TimeSpent FROM timer;" );
            while (rs.next()) {

                Times.add(rs.getInt("TimeSpent"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return (Times);

    }

    public static void main(String[] args){
        DBF db = new DBF();
    }

    public void storeData(long startTime, long endTime, String url, String timer_gui) {
        this.storeData(startTime, endTime, "WHY?", (int) (endTime - startTime) , "TODO", timer_gui, url);
    }
}
