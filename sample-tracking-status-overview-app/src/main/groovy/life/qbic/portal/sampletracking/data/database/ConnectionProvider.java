package life.qbic.portal.sampletracking.data.database;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides the ability to connect to a SQL resource
 *
 * @since: 1.0.0
 */
interface ConnectionProvider {

    Connection connect() throws SQLException;
}
