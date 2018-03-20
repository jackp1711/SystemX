import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;

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
    private JLabel lbl1;
    private JLabel lbl2;
    private JTextField dummyUrl;

    private DBF db;
    private Timer timer;

    private void createJframe() {
        JFrame frame = new JFrame();
            frame.setContentPane(PanelMain);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    public GUI(DBF db) {

        this.db = db;
        this.timer = new Timer(db);
        this.createJframe();
    }

    public static void main(String[] args){
        DBF db = new DBF();
        GUI gui = new GUI(db);
    }
}
