package uk.ac.bath.csedgroup2.systemx;

import models.Category;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import static spark.Spark.post;

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

        TabbedPannel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                // 0 = Main, 1 = Stats, 2 = Settings, 3 = Categories
                if (TabbedPannel.getSelectedIndex() == 1) {
                    pnlMyStats.removeAll();
                    pnlMyStats.add(graphTest.redraw());
                }
                System.out.println("Tab: " + TabbedPannel.getSelectedIndex());
            }
        });

        frame.pack();
        frame.setVisible(true);
        frame.setTitle("ProjectX");

        frame.setSize(600,500);

        //Start Button
        btnStart.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String url = dummyUrl.getText(); //Add textview for this
                if (!"".equals(url)) {
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
        //remove anything that may have been leftover here, first
        panelCategories.removeAll();
        for (Category category : this.db.getCategories()) {
            JTextField textField = new JTextField();
            JButton deleteButton = new JButton();
            deleteButton.setText("DELETE");

            deleteButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete category " + category.getTitle() + "?", "Delete category", JOptionPane.YES_NO_OPTION);
                    if (dialogResult == JOptionPane.YES_OPTION) {
                        // Saving code here
                        System.out.println("DELETING " + category);
                        db.deleteCategory(category);
                        //redraw categories again
                        createGroupsPanel();
                    }
                }
            });

            textField.setText(category.getTitle());
            textField.setColumns(40);
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
            panelCategories.add(deleteButton);
        }
        panelCategories.repaint();

        this.createAddGroupView(panelCategories);
    }

    private void createAddGroupView(JPanel panel) {
        JTextField textField = new JTextField();
        textField.setColumns(30);
        JButton button = new JButton();
        button.setText("Create group");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Category category = new Category(textField.getText());
                try {
                    db.categoryDao.createOrUpdate(category);

                    //reset view
                    textField.setText("");
                    createGroupsPanel();
                } catch (SQLException ex) {
                    System.err.println("Could not create category " + textField.getText());
                }
            }
        });
        panel.add(textField);
        panel.add(button);
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
        new GUI(db, graphTest);
    }
}
