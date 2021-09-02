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
}
