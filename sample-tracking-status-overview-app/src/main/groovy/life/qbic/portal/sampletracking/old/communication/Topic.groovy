package life.qbic.portal.sampletracking.old.communication

/**
 * <p>A message to be sent around. This class should be extended to hold relevant content.</p>
 *
 * @since 1.0.0
 */
enum Topic {

    PROJECT_ADDED("A project was added"),
    PROJECT_REMOVED("A project was removed"),
    PROJECT_UPDATED("A project was updated"),
    NOTIFICATION_FAILURE("Failure notification"),
    NOTIFICATION_SUCCESS("Success notification"),
    NOTIFICATION_INFO("Information notification"),
    SAMPLE_COUNT_UPDATE("The number of samples in the given statuses changed")

    @Override
    String toString() {
        return super.toString()
    }
    private final String title

    Topic(String title) {
        this.title = title
    }
}
