package life.qbic.business.project.subscribe

/**
 * <b>Subscribes or unsubscribes the given subscriber to/from the specified project</b>
 *
 * <p>This interface is used by {@link life.qbic.business.project.subscribe.SubscribeProject}</p>
 *
 * @since 1.0.0
 */
interface SubscriptionDataSource {

    /**
     * Creates the subscriber in the data source and subscribes him to the project.
     * @param subscriber
     * @param projectCode
     * @since 1.1.0
     */
    void subscribeToProject(Subscriber subscriber, String projectCode)
 
    /**
     * Removes the subscription relation between subscriber and project in the data source.
     * @param subscriber
     * @param projectCode
     * @since 1.0.0
     */
    void unsubscribeFromProject(Subscriber subscriber, String projectCode)

}