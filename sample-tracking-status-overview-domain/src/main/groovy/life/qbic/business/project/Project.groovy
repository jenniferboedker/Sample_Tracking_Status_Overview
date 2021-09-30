package life.qbic.business.project

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

    Project(String code, String title) {
        this.code = code
        this.title = title
    }
}
