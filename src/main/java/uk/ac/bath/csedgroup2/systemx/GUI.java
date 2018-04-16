package uk.ac.bath.csedgroup2.systemx;

import com.j256.ormlite.misc.SqlExceptionUtil;
import spark.Request;
import spark.Response;
import uk.ac.bath.csedgroup2.systemx.models.Category;
import uk.ac.bath.csedgroup2.systemx.models.Url;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static spark.Spark.post;

public class GUI {
    private JTabbedPane tabbedPanel;
    private JPanel panelMain;
    private JPanel pnlMainMenu;
    private JPanel pnlMyStats;
    private JPanel pnlSettings;
    private JButton btnStart;
    private JComboBox lstCatergory;
    private JCheckBox chkEnableNotifications;
    private JButton btnSchedule;
    private JTextField dummyUrl;
    private JPanel panelCategories;
    private JPanel urlsPanel;
    private JPanel goalsPanel;
    private JButton dataResetButton;

    private DBF db;
    private Timer timer;
    private JFrameGraphTest graphTest;

    private void createJframe() {
        JFrame frame = new JFrame();
        frame.setContentPane(panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPanel.addChangeListener(e -> {
            // 0 = Main, 1 = Stats, 2 = Settings, 3 = Categories, 4 = URLs, 5 = goals
            if (tabbedPanel.getSelectedIndex() == 1) {
                pnlMyStats.removeAll();
                pnlMyStats.add(graphTest.redraw());
            }
            if (tabbedPanel.getSelectedIndex() == 4) {
                createUrlsPanel();
            }

            if (tabbedPanel.getSelectedIndex() == 3) {
                createGroupsPanel();
            }

            if (tabbedPanel.getSelectedIndex() == 5) {
                createGoalsPanel();
            }
        });

        //Start Button
        btnStart.addActionListener(e -> {
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
        });

        dataResetButton.addActionListener(e-> {
            int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete all data and reset the database?", "Delete all data", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                db.resetDatabase();
            }
        });
        frame.pack();
        frame.setVisible(true);
        frame.setTitle("ProjectX");

        frame.setSize(600,500);
    }

    public GUI(DBF db, JFrameGraphTest graphTest) {
        this.graphTest = graphTest;
        this.db = db;
        this.timer = new Timer(db);
        this.createTrackerListener();
        this.createJframe();
        //this.createGroupsPanel();
        //this.createUrlsPanel();

    }

    private void createGoalsPanel() {
        List<Category> categories = this.db.getCategories();
        Vector groupTypesModel = new Vector();
        groupTypesModel.add("<");
        groupTypesModel.add(">");

        for (Category category : categories) {
            //categoryModel.addElement(category);
            JTextField goalValueTextField = new JTextField();
            goalValueTextField.setColumns(20);
            goalValueTextField.getDocument().addDocumentListener(new DocumentListener() {
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
                    if (goalValueTextField.getText().length()>0) {
                        category.setGoal(Category.deformatTimestamp(goalValueTextField.getText()));
                        try {
                            db.categoryDao.update(category);
                        } catch (SQLException e) {
                            System.err.println("Could not update category " + category.getTitle());
                        }
                    }
                }
            });


            JTextField categoryNameTextField = new JTextField();
            categoryNameTextField.setColumns(20);
            categoryNameTextField.setEnabled(false);
            JComboBox goalTypeCombo = new JComboBox(groupTypesModel);
            if (category.isTypeLessThan()) {
                goalTypeCombo.setSelectedItem("<");
            } else {
                goalTypeCombo.setSelectedItem(">");
            }
            goalTypeCombo.addActionListener(e -> {
                JComboBox comboBox = (JComboBox)e.getSource();
                if (comboBox.getSelectedItem() == "<") {
                    category.setGoalType(Category.TYPE_LESS_THAN);
                } else {
                    category.setGoalType(Category.TYPE_MORE_THAN);
                }
                try {
                    db.categoryDao.update(category);
                } catch (SQLException ex) {
                    System.err.println("Could not update goal type for " + category.getTitle());
                }
            });


            categoryNameTextField.setText(category.getTitle());
            goalValueTextField.setText(Category.formatGoal(category.getGoal()));

            goalsPanel.add(categoryNameTextField);
            goalsPanel.add(goalTypeCombo);
            goalsPanel.add(goalValueTextField);
        }
    }

    public static int findIndexOf(Object o, List list) {
        if (o == null) {
            return -1;
        }
        for (int i = 0; i < list.size(); i++) {
            if (o.toString().equals(list.get(i).toString())) {
                return i;
            }
        }
        return -1;
    }

    private void createUrlsPanel() {
        urlsPanel.removeAll();
        ArrayList<Category> categories = (ArrayList) this.db.getCategories();

        Vector categoryModel = new Vector();
        categoryModel.add(Category.createNullCategory());
        categoryModel.addAll(categories);

        for (Url url : this.db.getUrls()) {
            JTextField urlTextField = new JTextField();
            urlTextField.setText(url.getTitle());
            urlTextField.setColumns(32);
            urlTextField.setEnabled(false);

            JComboBox categoryCombo = new JComboBox(categoryModel);
            categoryCombo.setSelectedIndex(findIndexOf(url.getCategory(), categories) + 1);
            categoryCombo.addActionListener(e -> {
                JComboBox comboBox = (JComboBox)e.getSource();
                Category category = (Category)comboBox.getSelectedItem();
                System.out.println(url.getTitle() + category);
                db.changeUrlCategory(url, category);
            });
            urlsPanel.add(urlTextField);
            urlsPanel.add(categoryCombo);
        }
    }

    private void createGroupsPanel() {
        //remove anything that may have been leftover here, first
        panelCategories.removeAll();
        List<Category> categories = this.db.getCategories();
        for (Category category : categories) {
            JTextField textField = new JTextField();
            JButton deleteButton = new JButton();
            deleteButton.setText("DELETE");

            deleteButton.addActionListener(e-> {
                int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete category " + category.getTitle() + "?", "Delete category", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    // Saving code here
                    db.deleteCategory(category);
                    //redraw categories again
                    createGroupsPanel();
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
        button.setText("Create category");
        button.addActionListener(e -> {
            Category category = new Category(textField.getText());
            try {
                db.categoryDao.createOrUpdate(category);

                //reset view
                textField.setText("");
                createGroupsPanel();
            } catch (SQLException ex) {
                System.err.println("Could not create category " + textField.getText());
            }
        });
        panel.add(textField);
        panel.add(button);
    }

    private void createTrackerListener() {
        post("/tracker", (Request request, Response response) -> {
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
            response.status(201); // 201 Created
            return null;
        });
    }

    public static void main(String[] args){
        DBF db = new DBF();
        JFrameGraphTest graphTest = new JFrameGraphTest(db);
        new GUI(db, graphTest);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
