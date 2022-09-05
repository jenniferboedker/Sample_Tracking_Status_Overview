package life.qbic.portal.sampletracking.data.database;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <b>Creates a connection to a SQL database</b>
 *
 * <p>A class for setting up the connection to the user database.
 * It should be used when data needs to be retrieved from the
 * DB or written into it.</p>
 *
 * @since 1.0.0
 */
public class DatabaseSession implements ConnectionProvider {

    private static final Logger log = LogManager.getLogger(DatabaseSession.class);

    protected static DatabaseSession INSTANCE;

    private BasicDataSource dataSource;

    private DatabaseSession() {
        //This is a private Singleton constructor
        dataSource = null;
    }


    /**
     * Initiates the database connection
     * The instance is only created if there is no other existing
     * @param user the user to use for the database
     * @param password the password to use for the database connection
     * @param host the database host
     * @param port the port on which the database is hosted
     * @param sqlDatabase the name of the database
     */
    public static void init(String user,
                     String password,
                     String host,
                     String port,
                     String sqlDatabase) {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseSession();

            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            String url = String.format("jdbc:mysql://%s:%s/%s", host, port, sqlDatabase);

            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setUrl(url);
            basicDataSource.setUsername(user);
            basicDataSource.setPassword(password);
            basicDataSource.setMinIdle(5);
            basicDataSource.setMaxIdle(10);
            basicDataSource.setMaxOpenPreparedStatements(100);
            INSTANCE.dataSource = basicDataSource;
        } else {
            log.warn(
                String.format("Skipped overwrite existing connection to %s:%s with %s:%s.", host,
                    port, host, port));
        }
    }

    /**
     * Creates a database connection by login into the database based on the given credentials
     *
     * @return Connection, otherwise null if connecting to the database fails
     * @throws SQLException if a database access error occurs or the url is {@code null}
     */
    @Override
    public Connection connect() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Returns the current DatabaseSession object
     * @return the global database session
     */
    public static DatabaseSession getInstance() {
        if (Objects.isNull(INSTANCE)) {
            throw new RuntimeException(
                "Call the init method first. Instance has not been initialized.");
        } else {
            return INSTANCE;
        }
    }
}
