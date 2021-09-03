package life.qbic.portal.sampletracking.communication

/**
 * <p>A message to be sent around. This class should be extended to hold relevant content.</p>
 *
 * @since 1.0.0
 */
enum Topic {

    PROJECT_ADDED("A project was added"),
    PROJECT_REMOVED("A project was removed"),
    NOTIFICATION_FAILURE("Failure notification"),
    NOTIFICATION_SUCCESS("Success notification"),
    NOTIFICATION_INFO("Information notification"),
    SAMPLE_RECEIVED_COUNT_UPDATE("The count of received samples was updated"),
    SAMPLE_FAILED_QC_COUNT_UPDATE("The count of samples failing QC was updated"),
    SAMPLE_DATA_AVAILABLE_COUNT_UPDATE("The count of available samples data was updated"),
    SAMPLE_LIBRARY_PREP_FINISHED("The count of samples with finished library prep was updated")

    @Override
    String toString() {
        return super.toString()
    }
    private final String title

    Topic(String title) {
        this.title = title
    }
}
