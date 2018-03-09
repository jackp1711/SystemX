import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    public GUI() {

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

                //When starting
                if (btnStart.getText().equals("Start")){
                    btnStart.setText("Stop");
                    startTime = System.currentTimeMillis();
                    System.out.println("Start Time: " + startTime);
                    lstCatergory.setEnabled(false);
                }
                //When stopping
                else{
                    btnStart.setText("Start");
                    endTime = System.currentTimeMillis();
                    System.out.println("End Time: " + endTime);
                    System.out.println("Change in time: " + (endTime - startTime));
                    lstCatergory.setEnabled(true);
                }


            }
        });
    }

}
