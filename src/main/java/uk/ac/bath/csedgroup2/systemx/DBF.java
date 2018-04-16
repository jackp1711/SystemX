package uk.ac.bath.csedgroup2.systemx;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RawRowMapper;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import uk.ac.bath.csedgroup2.systemx.models.Category;
import uk.ac.bath.csedgroup2.systemx.models.TimerEntry;
import uk.ac.bath.csedgroup2.systemx.models.Url;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DBF {

    private static final String DB_SOURCE = "jdbc:sqlite:TrackerDatabase.sqlite";
    public static Dao<Url, String> urlDao;
    public static Dao<Category, String> categoryDao;
    public static Dao<TimerEntry, String> timerEntryDao;

    public DBF() {
        this.startup();
    }

    public void startup() {
        try {
            ConnectionSource connectionSource = new JdbcConnectionSource(DBF.DB_SOURCE);
            urlDao = DaoManager.createDao(connectionSource, Url.class);
            categoryDao = DaoManager.createDao(connectionSource, Category.class);
            timerEntryDao = DaoManager.createDao(connectionSource, TimerEntry.class);

            TableUtils.createTable(connectionSource, Url.class);
            TableUtils.createTable(connectionSource, Category.class);
            TableUtils.createTable(connectionSource, TimerEntry.class);

            generateSampleData();
        } catch (Exception e) {
            //There will be an exception when trying to recreate tables. There shouldn't be an exception on the first run of the application
        }
    }

    public void resetDatabase() {
        try {
            TableUtils.dropTable(urlDao.getConnectionSource(), Url.class, true);
            TableUtils.dropTable(urlDao.getConnectionSource(), Category.class, true);
            TableUtils.dropTable(urlDao.getConnectionSource(), TimerEntry.class, true);
            startup();
        } catch (SQLException e) {
            //Error dropping tables
        }
    }

    public void generateSampleData() {
        Category c1 = new Category("PRODUCTIVE");
        c1.setGoalType(Category.TYPE_MORE_THAN);
        c1.setGoal(90000);
        Category c2 = new Category("TIME-wasters");
        c2.setGoalType(Category.TYPE_LESS_THAN);
        c2.setGoal(90000);
        Category c3 = new Category("Entertainment");
        c3.setGoalType(Category.TYPE_LESS_THAN);
        c3.setGoal(72000);

        Url url1 = new Url("youtube.com", c3);
        Url url2 = new Url("facebook.com", c2);
        Url url3 = new Url("bbc.co.uk", c3);
        Url url4 = new Url("docs.google.com", c1);
        try {

            categoryDao.create(c1);
            categoryDao.create(c2);
            categoryDao.create(c3);
            urlDao.create(url1);
            urlDao.create(url2);
            urlDao.create(url3);
            urlDao.create(url4);

        } catch (SQLException e) {
            System.err.println("Error creating sample data");
        }
    }

    public TimerEntry storeData(int start, int end, String website) {
        System.out.println("trying to store " + start + " " + end + " " + website);
        Url url = null;
        try {
            url = urlDao.queryForId(website);
        } catch (SQLException e) {
            System.err.println("Url could not be fetched from database, create a new one");
        }
        if (url == null) {
            Url newUrl = new Url(website, null);
            try {
                urlDao.create(newUrl);
                url = newUrl;
            } catch (SQLException ex) {
                System.err.println("Could not save url to database");
            }
        }

        if (url != null) {
            TimerEntry timerEntry = new TimerEntry(url, start, end, (int) (end - start));

            try {
                timerEntryDao.create(timerEntry);
                return timerEntry;
            } catch (SQLException e) {
                System.err.println("Could not save time entry to database");
            }
        }
        return null;
    }

    public List<TimerEntry> getEntriesSinceTime(int time) {
        try {
            QueryBuilder<TimerEntry, String> queryBuilder = timerEntryDao.queryBuilder();
            queryBuilder.where().gt("start", time);
            return queryBuilder.query();
        } catch (SQLException e) {
            System.err.println("Could not retrieve data");
        }
        return new ArrayList<>();
    }

    public List<TimerEntry> getGroupedEntriesSinceTime(int time) {
        ArrayList<TimerEntry> timerEntryList = new ArrayList<>();

        try {
            QueryBuilder<TimerEntry, String> queryBuilder = timerEntryDao.queryBuilder();
            queryBuilder.selectRaw("SUM(duration), url_id");
            queryBuilder.groupBy("url_id");
            queryBuilder.where().gt("start", time);

            GenericRawResults<TimerEntry> results = timerEntryDao.queryRaw(queryBuilder.prepareStatementString(), new RawRowMapper<TimerEntry>() {
                @Override
                public TimerEntry mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    TimerEntry t = new TimerEntry();
                    t.setUrl(urlDao.queryForId(resultColumns[1]));
                    t.setDuration(Integer.parseUnsignedInt(resultColumns[0]));
                    return t;
                }
            });
            for (TimerEntry timerEntry : results) {
                timerEntryList.add(timerEntry);
            }
            return timerEntryList;

        } catch (SQLException e) {
            System.err.println("Could not retrieve data");
        }
        return new ArrayList<>();
    }

    public void deleteCategory(Category category) {
        try {
            List<Url> urls = urlDao.queryForEq("category_id", category.getId());
            for(Url url : urls) {
                url.setCategory(null);
                urlDao.update(url);
            }
            categoryDao.delete(category);
        } catch (SQLException ex) {
            System.err.println("Could not delete category " + category);
        }
    }

    public List<Url> getUrls() {
        try {
            return urlDao.queryForAll();
        } catch (SQLException e) {
            System.err.println("Could not query for all categories");
        }
        return new ArrayList<>();
    }

    public void changeUrlCategory(Url url, Category category) {
        try {
            url.setCategory(category);
            urlDao.update(url);
        } catch (SQLException e) {
            System.out.println(url.getTitle() + category);
            e.printStackTrace();
            System.err.println("Updating url category failed");
        }
    }

    public List<Category> getCategories() {
        try {
            return categoryDao.queryForAll();
        } catch (SQLException e) {
            System.err.println("Could not query for all categories");
        }
        return new ArrayList<>();
    }

    public List<Category> getGroupedCategoriesSinceTime(int time) {
        ArrayList<Category> categoriesList = new ArrayList<>();
        try {
            String query = "select category.title, sum(timerentry.duration) from main.timerentry left join url on url.title = timerentry.url_id left join category on url.category_id = category.id where timerentry.start > ? group by category.title;";

            GenericRawResults<Category> results = categoryDao.queryRaw(query, new RawRowMapper<Category>() {
                @Override
                public Category mapRow(String[] columnNames, String[] resultColumns) throws SQLException {
                    Category c = new Category();
                    c.setTitle(resultColumns[0] != null ? resultColumns[0] : "Others");
                    c.setDuration(Integer.parseInt(resultColumns[1]));
                    return c;
                }
            }, "" + time);
            for (Category category : results) {
                categoriesList.add(category);
            }
            return categoriesList;
        } catch (SQLException e) {
            System.err.println("Could not retrieve groupped categories");
        }
        return new ArrayList<>();
    }
}
