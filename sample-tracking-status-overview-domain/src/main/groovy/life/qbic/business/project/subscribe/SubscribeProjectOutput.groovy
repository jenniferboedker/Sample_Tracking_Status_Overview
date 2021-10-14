package life.qbic.business.project.subscribe

/**
 * <b>Output interface for the {@link life.qbic.business.project.subscribe.SubscribeProject} feature</b>
 *
 * <p>Publishes the project code if a subscription or unsubscription was successful or the subscriber and projectCode if the process failed</p>
 *
 * @since 1.0.0
 */
interface SubscribeProjectOutput {

    /**
     * A subscription was added for a given project
     * @param project the project code of the subscribed project
     * @since 1.1.0
     */
    void subscriptionAdded(String project)

    /**
     * A subscription was not possible
     * @param subscriber the subscriber that was provided
     * @param projectCode the project the subscription was attempted on
     * @since 1.0.0
     */
    void subscriptionFailed(Subscriber subscriber, String projectCode)
    
    /**
     * Unsubscription for a given project was successful
     * @param project the project code of the unsubscribed project
     * @since 1.1.0
     */
    void subscriptionRemoved(String project)

    /**
     * Unsubscription was not possible
     * @param subscriber the subscriber that was provided
     * @param projectCode the project the unsubscription was attempted on
     * @since 1.1.0
     */
    void unsubscriptionFailed(Subscriber subscriber, String projectCode)
}
