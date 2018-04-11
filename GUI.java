import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Timestamp;

import Models.Category;
import org.jfree.chart.ChartPanel;

import static spark.Spark.post;
import spark.Request;
import spark.Response;
import spark.Route;

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
    private JPanel panelCategories;

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
            public void actionPerformed(ActionEvent e) {
                String url = dummyUrl.getText(); //Add textview for this
                if (!url.equals("")) {
                    //When starting
                    if (btnStart.getText().equals("Start")){
                        btnStart.setText("Stop");
                        lstCatergory.setEnabled(false);
                        timer.startTimer();
                    }
                    //When stopping
                    else{
                        btnStart.setText("Start");
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
        this.createTrackerListener();
        this.createJframe();
        this.createGroupsPanel();
    }

    private void createGroupsPanel() {
        for (Category category : this.db.getCategories()) {
            JTextField textField = new JTextField();
            textField.setText(category.getTitle());
            textField.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                    saveChangedCategory();
                }

                public void removeUpdate(DocumentEvent e) {
                    saveChangedCategory();
                }
                public void insertUpdate(DocumentEvent e) {
                    saveChangedCategory();
                }

                public void saveChangedCategory() {
                    if (textField.getText().length()>0) {
                        category.setTitle(textField.getText());
                        try {
                            db.categoryDao.update(category);
                        } catch (SQLException e) {
                            System.err.println("Could not update category " + category.getTitle());
                        }
                    }
                }
            });
            panelCategories.add(textField);
        }
    }

    private void createTrackerListener() {
        post("/tracker", new Route() {
            @Override
            public Object handle(Request request, Response response) {
                String message = request.queryParams("message");
                String[] parts = message.split(" ");
                if (parts.length == 3 || parts.length == 2) {
                    int ts = Integer.parseInt(parts[0]);
                    if (parts[1].equals("START")) {
                        String url = parts[2];
                        timer.startTimer(url, ts);
                    } else {
                        timer.stopTimer(ts);
                    }
                }
                System.out.println(message);
                response.status(201); // 201 Created
                return null;
            }
        });
    }

    public static void main(String[] args){
        DBF db = new DBF();
        JFrameGraphTest graphTest = new JFrameGraphTest(db);
        GUI gui = new GUI(db, graphTest);
    }
}
