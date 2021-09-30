package life.qbic.business.project

import java.sql.Timestamp

/**
 * <b>Represents a QBiC project</b>
 *
 * <p>Represents a QBiC project, contains unmodifiable fields</p>
 *
 * @since 1.0.0
 */
class Project {
    final String code
    final String title
    Timestamp lastChanged

    Project(String code, String title) {
        this.code = code
        this.title = title
    }
}
