package life.qbic.portal.sampletracking.components.projectoverview.subscribe

import life.qbic.business.project.subscribe.SubscribeProjectOutput
import life.qbic.business.project.subscribe.Subscriber
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.projectoverview.ProjectOverviewViewModel
/**
 * <b>Presents a download manifest to the viewModel containing identifiers that point to associated data</b>
 *
 * <p>Is called from a use case with a manifest String containing identifiers (e.g. of samples) that have associated data.
 * This manifest is added to the view model in order to further use it, e.g. present it to the user.</p>
 *
 * @since 1.0.0
 */
class SubscribeProjectPresenter implements SubscribeProjectOutput {

    private final ProjectOverviewViewModel viewModel
    private final NotificationService notificationService

    SubscribeProjectPresenter(NotificationService notificationService, ProjectOverviewViewModel viewModel) {
        this.notificationService = notificationService
        this.viewModel = viewModel
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
                    "Please contact ${Constants.QBIC_HELPDESK_EMAIL}."
        notificationService.publishFailure(message)
    }
}
