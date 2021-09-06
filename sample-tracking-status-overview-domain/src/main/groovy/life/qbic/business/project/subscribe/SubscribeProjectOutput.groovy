package life.qbic.business.project.subscribe

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
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
     * @since 1.1.0
     */
    void subscriptionFailed(Subscriber subscriber, String projectCode)
}
