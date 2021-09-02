package life.qbic.business.project.subscribe

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
interface SubscriptionDataSource {

    /**
     * Creates the subscriber in the data source and subscribes him to the project.
     * @param subscriber
     * @param projectCode
     * @since 1.1.0
     */
    void subscribeToProject(Subscriber subscriber, String projectCode)

}