package life.qbic.portal.sampletracking.components.projectoverview.subscribe

import life.qbic.business.project.subscribe.SubscribeProject
import life.qbic.business.project.subscribe.SubscribeProjectInput
import life.qbic.business.project.subscribe.Subscriber

/**
 * <b>Controller allowing the view to call the subscribe project use case</b>
 *
 * <p>Used via the view to start the subscribe project use case with the currently selected project code and a provided subscriber. </p>
 *
 * @since 1.0.0
 */
class SubscribeProjectController {

    SubscribeProjectInput subscribeProject

    SubscribeProjectController(SubscribeProject subscribeProject) {
        this.subscribeProject = subscribeProject
    }
    
    /**
     * Triggers the Subscribe Project case. If no project code or subscriber is provided, throws an {@link IllegalArgumentException}
     * @param projectCode the code of the selected project
     * @param projectCode the user that will be subscribed to the project
     * @throws IllegalArgumentException in case the project code or subscriber is not provided
     */
    void subscribeProject(Subscriber subscriber, String projectCode) throws IllegalArgumentException{
        subscribeProject.subscribe(subscriber, projectCode)
    }
}
