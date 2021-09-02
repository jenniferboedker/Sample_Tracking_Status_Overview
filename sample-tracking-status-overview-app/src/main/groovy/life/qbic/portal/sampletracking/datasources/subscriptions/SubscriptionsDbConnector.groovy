package life.qbic.portal.sampletracking.datasources.subscriptions

import life.qbic.business.project.subscribe.Subscriber
import life.qbic.business.project.subscribe.SubscriptionDataSource

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
class SubscriptionsDbConnector implements SubscriptionDataSource {
    /**
     * Creates the subscriber in the data source and subscribes him to the project.
     * @param subscriber
     * @param projectCode
     * @since 1.1.0
     */
    @Override
    void subscribeToProject(Subscriber subscriber, String projectCode) {
        //TODO implement
        println "subscriber = $subscriber, projectCode = $projectCode"
    }
}
