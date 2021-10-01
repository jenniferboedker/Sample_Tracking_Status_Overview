package life.qbic.portal.sampletracking.components.projectoverview.subscribe

import life.qbic.business.project.Project
import life.qbic.business.project.subscribe.SubscribeProjectOutput
import life.qbic.business.project.subscribe.Subscriber
import life.qbic.portal.sampletracking.Constants
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.resource.ResourceService

/**
 * <b>Presents the notification message informing about a successful or failed subscription along with the projectCode</b>
 *
 * <p>Is called from a use case with the projectCode for a successful subscription or with the projectCode and Subscriber on a failed subscription
 * This information is added to the generated notification message to inform the user about his subscription status for the specified project</p>
 *
 * @since 1.0.0
 */
class SubscribeProjectPresenter implements SubscribeProjectOutput {

    private final NotificationService notificationService
    private final ResourceService<Project> projectResourceService

    SubscribeProjectPresenter(ResourceService<Project> projectResourceService, NotificationService notificationService) {
        this.projectResourceService = projectResourceService
        this.notificationService = notificationService
    }

    /**
    * A subscription was added for a given project
    * @param project the project code of the subscribed project
    * @since 1.1.0
    */
    @Override
    void subscriptionAdded(String project) {
        String message = "Subscription to ${project} was successful"
        notificationService.publishSuccess(message)
        projectResourceService.replace({ it.code == project },
                {
                    it.hasSubscription = true
                    return it
                })
    }

    /**
     * A subscription was not possible
     * @param subscriber the subscriber that was provided
     * @param projectCode the project the subscription was attempted on
     * @since 1.1.0
     */
    @Override
    void subscriptionFailed(Subscriber subscriber, String projectCode) {
        String message = "An unexpected while trying to subscribe to project ${projectCode}. " +
                    "Please contact ${Constants.CONTACT_HELPDESK}."
        notificationService.publishFailure(message)
    }
}
