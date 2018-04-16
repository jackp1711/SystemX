package uk.ac.bath.csedgroup2.systemx;

import spark.Request;
import spark.Response;
import uk.ac.bath.csedgroup2.systemx.models.Category;
import uk.ac.bath.csedgroup2.systemx.models.Url;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.SQLException;
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

    private DBF db;
    private Timer timer;
    private JFrameGraphTest graphTest;

    private void createJframe() {
        JFrame frame = new JFrame();
        frame.setContentPane(panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        tabbedPanel.addChangeListener(e -> {
            // 0 = Main, 1 = Stats, 2 = Settings, 3 = Categories
            if (tabbedPanel.getSelectedIndex() == 1) {
                pnlMyStats.removeAll();
                pnlMyStats.add(graphTest.redraw());
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
        this.createGroupsPanel();
        this.createUrlsPanel();
    }

    private void createUrlsPanel() {
        List<Category> categories = this.db.getCategories();
        List<Url> urls = this.db.getUrls();

        Vector categoryModel = new Vector();
        categoryModel.add(Category.createNullCategory());
        categoryModel.addAll(categories);


        for(Category category : db.getCategories()) {
            //categoryModel.addElement(category);
            System.out.println("adding to combo: " + category);
        }

        for (Url url : urls) {
            JTextField urlTextField = new JTextField();
            urlTextField.setText(url.getTitle());
            urlTextField.setColumns(32);
            urlTextField.setEnabled(false);

            JComboBox categoryCombo = new JComboBox(categoryModel);
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
