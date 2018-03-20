import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import org.jfree.chart.ChartPanel;

public class GUI {
    private JTabbedPane TabbedPannel;
    private JPanel PanelMain;
    private JPanel pnlMainMenu;
    private JPanel pnlMyStats;
    private JPanel pnlSettings;
    private JButton btnStart;
    private JComboBox lstCatergory;
    private JCheckBox chkEnableNotifications;
    private JButton btnSchedule;
    private JTextField dummyUrl;

    private DBF db;
    private Timer timer;
    private JFrameGraphTest graphTest;

    private void createJframe() {
        JFrame frame = new JFrame();
            frame.setContentPane(PanelMain);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pnlMyStats.add(graphTest.createPieChart("TestChart"));
            frame.pack();
            frame.setVisible(true);
            frame.setTitle("ProjectX");

            frame.setSize(600,500);

        //Start Button Code
            btnStart.addActionListener(new ActionListener() {
            long startTime;
            long endTime;
            public void actionPerformed(ActionEvent e) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                int ts = (int) timestamp.getTime();

                String url = dummyUrl.getText(); //Add textview for this
                if (!url.equals("")) {
                    //When starting
                    if (btnStart.getText().equals("Start")){
                        btnStart.setText("Stop");
                        System.out.println("Start Time: " + ts);
                        lstCatergory.setEnabled(false);
                        timer.startTimer();
                    }
                    //When stopping
                    else{
                        btnStart.setText("Start");
                        System.out.println("End Time: " + ts);
                        timer.stopTimer(url);
                        lstCatergory.setEnabled(true);
                    }
                }
            }
        });
    }

    public GUI(DBF db, JFrameGraphTest graphTest) {
        this.graphTest = graphTest;
        this.db = db;
        this.timer = new Timer(db);
        this.createJframe();
    }

    public static void main(String[] args){
        DBF db = new DBF();
        JFrameGraphTest graphTest = new JFrameGraphTest(db);
        GUI gui = new GUI(db, graphTest);
    }
}
