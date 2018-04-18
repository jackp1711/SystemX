package uk.ac.bath.csedgroup2.focusmonster;

import spark.Request;
import spark.Response;
import uk.ac.bath.csedgroup2.focusmonster.models.Category;
import uk.ac.bath.csedgroup2.focusmonster.models.Url;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static spark.Spark.post;

public class GUI {
    private JTabbedPane navigationPanel;
    private JPanel panelMain;
    private JPanel panelHomeScreen;
    private JPanel panelStatistics;
    private JPanel panelSettings;
    private JButton btnStart;
    private JComboBox lstCatergory;
    private JTextField dummyUrl;
    private JPanel panelCategories;
    private JPanel panelUrls;
    private JPanel panelGoals;
    private JButton dataResetButton;

    private DBF db;
    private Timer timer;
    private JFrameGraphTest graphTest;
    private JFrame frame;

    private void createJFrame() {
        frame = new JFrame();
        frame.setContentPane(panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createUIComponents();

        frame.pack();
        frame.setVisible(true);
        frame.setTitle("Focus Monster");
        frame.setSize(600,500);
    }

    public GUI(DBF db, JFrameGraphTest graphTest) {
        this.graphTest = graphTest;
        this.db = db;
        this.timer = new Timer(db);
        this.createTrackerListener();
        this.createJFrame();
    }

    /**
     * Creates contents of the Goals panel
     * This panel allows users to set goals for different categories
     * Goals are set in seconds per week
     * Goals can be to spend more or less time in each category every week
     */
    private void createGoalsPanel() {
        //remove all category goals previously in the panel and recreate from scratch
        panelGoals.removeAll();
        List<Category> categories = db.getCategories();

        for (Category category : categories) {
            //Create a "goal value" text field for each category. This is an amount in seconds per week
            JTextField goalValueTextField = new JTextField();
            goalValueTextField.setColumns(20);
            goalValueTextField.setText(Category.formatGoal(category.getGoal()));
            //set listeners on textfield change
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
                    //update value of the category goal every time user types in a new one
                    if (goalValueTextField.getText().length()>0) {
                        category.setGoal(Category.deformatTimestamp(goalValueTextField.getText()));
                        try {
                            DBF.categoryDao.update(category);
                        } catch (SQLException e) {
                            System.err.println("Could not update category " + category.getTitle());
                        }
                    }
                }
            });


            //Create a category name text field, disabled, only show name of the category
            JTextField categoryNameTextField = new JTextField();
            categoryNameTextField.setColumns(20);
            categoryNameTextField.setEnabled(false);
            categoryNameTextField.setText(category.getTitle());

            //Create combobox for goal type (more or less time)
            JComboBox<String> goalTypeCombo = new JComboBox<>(Category.getGoalTypeVectors());
            //set default combo value to the one already set in the database
            if (category.isTypeLessThan()) {
                goalTypeCombo.setSelectedItem("<");
            } else {
                goalTypeCombo.setSelectedItem(">");
            }
            //listener to update database every time user changes the value
            goalTypeCombo.addActionListener(e -> {
                JComboBox comboBox = (JComboBox)e.getSource();
                if (comboBox.getSelectedItem() == "<") {
                    category.setGoalType(Category.TYPE_LESS_THAN);
                } else {
                    category.setGoalType(Category.TYPE_MORE_THAN);
                }
                try {
                    DBF.categoryDao.update(category);
                } catch (SQLException ex) {
                    System.err.println("Could not update goal type for " + category.getTitle());
                }
            });

            //append views to the panel
            panelGoals.add(categoryNameTextField);
            panelGoals.add(goalTypeCombo);
            panelGoals.add(goalValueTextField);
        }
    }

    /**
     * helper that finds an object in a list of objects and returns its position (or -1)
     * @param o
     * @param list
     * @return position in the list
     */
    public static int findIndexOf(Object o, List<Category> list) {
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

    /**
     * Creates contents of the URLs panel
     * This panel allows users to sort visited urls into categories
     */
    private void createUrlsPanel() {
        //remove all urls that may have already been on the panel
        panelUrls.removeAll();
        ArrayList<Category> categories = (ArrayList<Category>) this.db.getCategories();

        //Values for combo box - "Others" is a null category and is the default choice. Add all categories to select from
        Vector<Category> categoryModel = new Vector<>();
        categoryModel.add(Category.createNullCategory());
        categoryModel.addAll(categories);

        for (Url url : this.db.getUrls()) {
            //For each url in database create a textfield with name and combobox with category
            //Url title CAN NOT be changed as it serves as unique ID and would break timer entry dependencies
            JTextField urlTextField = new JTextField();
            urlTextField.setText(url.getTitle());
            urlTextField.setColumns(32);
            urlTextField.setEnabled(false);

            //create category combobox
            JComboBox<Category> categoryCombo = new JComboBox<>(categoryModel);
            //set default value of combobox
            //Find the position of the selected category in the stack of all categories
            categoryCombo.setSelectedIndex(findIndexOf(url.getCategory(), categories) + 1);
            //listener to update a newly selected category to the url
            categoryCombo.addActionListener(e -> {
                JComboBox comboBox = (JComboBox)e.getSource();
                Category category = (Category)comboBox.getSelectedItem();
                //category CAN be null - url will then be sorted to a null category ("Others")
                db.changeUrlCategory(url, category);
            });
            //append textfield and categorycombo to the panel
            panelUrls.add(urlTextField);
            panelUrls.add(categoryCombo);
        }
    }

    /**
     * Creates contents of the groups panel
     * Allows user to configure differnt categories, into which urls can then be sorted
     */
    private void createGroupsPanel() {
        //remove anything that may have been leftover here, first
        panelCategories.removeAll();
        List<Category> categories = this.db.getCategories();
        for (Category category : categories) {
            //create delete button for each category
            JButton deleteButton = new JButton();
            deleteButton.setText("DELETE");

            //Before deleting category, prompt the user whether they actually want to do that
            deleteButton.addActionListener(e-> {
                int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete category " + category.getTitle() + "?", "Delete category", JOptionPane.YES_NO_OPTION);
                if (dialogResult == JOptionPane.YES_OPTION) {
                    //Deletes category and updates all dependent urls accordingly
                    db.deleteCategory(category);
                    //redraw categories again
                    createGroupsPanel();
                }
            });

            //Create category name text field
            JTextField textField = new JTextField();
            textField.setText(category.getTitle());
            textField.setColumns(40);
            //Listener to update changes to the category name
            //Category name can be freely changed, as it is not used as unique ID
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
            //append group name textfield and delete button to the panel
            panelCategories.add(textField);
            panelCategories.add(deleteButton);
        }

        //append view for adding new groups at the end here
        this.createAddGroupView(panelCategories);
    }

    /**
     * Adds text field which creates a new category to the specified panel
     * @param panel
     */
    private void createAddGroupView(JPanel panel) {
        //Editable category name text
        JTextField categoryNameTextField = new JTextField();
        categoryNameTextField.setColumns(30);

        //Submit button
        JButton button = new JButton();
        button.setText("Create category");
        button.addActionListener(e -> {
            if (!"".equals(categoryNameTextField.getText())) { //test if name is not empty
                Category category = new Category(categoryNameTextField.getText());
                try {
                    List<Category> oldCategories = DBF.categoryDao.queryForEq("title", category.getTitle());
                    //First check if category with the same name doesn't already exist
                    if (oldCategories.size() == 0) {
                        //then save it
                        DBF.categoryDao.create(category);
                    } else {
                        System.out.println("Recreating category with same name " + category.getTitle());
                    }

                    //reset view
                    categoryNameTextField.setText("");
                    createGroupsPanel();
                } catch (SQLException ex) {
                    System.err.println("Could not create category " + categoryNameTextField.getText());
                }
            }
        });
        panel.add(categoryNameTextField);
        panel.add(button);
    }

    /**
     * Creates a http API for the tab_tracker to use
     */
    private void createTrackerListener() {
        //create Spark http listener
        post("/tracker", (Request request, Response response) -> { //Listen for POST HTTP request on localhost:4567/tracker
            String message = request.queryParams("message"); //retrieve message from incoming request
            String[] parts = message.split(" ");
            if (parts.length == 3 || parts.length == 2) { //verify parsed message is the expected length
                int ts = Integer.parseInt(parts[0]);
                if (parts[1].equals("START")) { //START message contains a timestamp and an url
                    String url = parts[2];
                    timer.startTimer(url, ts);
                } else {
                    timer.stopTimer(ts); //STOP message only contains a timestamp
                }
            }
            response.status(201); // 201 Created
            return null;
        });
    }

    /**
     * Creates UI Components and needs to be called before frame.pack()
     */
    private void createUIComponents() {
        //Navigation panel behavior
        //Listener triggers every time user clicks on a tab
        navigationPanel.addChangeListener(e -> {
            // 0 = Main, 1 = Stats, 2 = Settings, 3 = Categories, 4 = URLs, 5 = goals
            switch (navigationPanel.getSelectedIndex()) {
                case 1:
                    panelStatistics.removeAll();
                    panelStatistics.add(graphTest.redraw());
                    break;
                case 2:
                    break;
                case 3:
                    createGroupsPanel();
                    break;
                case 4:
                    createUrlsPanel();
                    break;
                case 5:
                    createGoalsPanel();
                default:
                    break;
            }
        });


        //Start Button
        btnStart.addActionListener(e -> {
            String url = dummyUrl.getText();
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

        //Reset button on the settings panel
        dataResetButton.addActionListener(e-> {
            int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you wish to delete all data and reset the database?", "Delete all data", JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                //Display user dialog and verify, that they wish to reset the database
                db.resetDatabase();
            }
        });
    }

    public static void main(String[] args){
        DBF db = new DBF();
        JFrameGraphTest graphTest = new JFrameGraphTest(db);
        new GUI(db, graphTest);
    }
}
