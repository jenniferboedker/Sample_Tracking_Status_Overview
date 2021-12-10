package life.qbic.portal.sampletracking.components.projectoverview.subscribe

import com.vaadin.ui.CheckBox
import groovy.util.logging.Log4j2
import life.qbic.business.project.subscribe.Subscriber
import life.qbic.portal.sampletracking.Constants
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.projectoverview.ProjectSummary

/**
 * <b>Checkboxes to handle project subscription</b>
 *
 * <p>Creates a checkbox for a {@link ProjectSummary} and adds a listener that triggers unsubscription from the project</p>
 *
 * @since 1.0.0
 */
@Log4j2
class SubscriptionCheckboxFactory {

    private final SubscribeProjectController subscribeProjectController
    private final Subscriber subscriber
    private final NotificationService notificationService

    private Map<String,CheckBox> projectCodeToCheckBox

    SubscriptionCheckboxFactory(SubscribeProjectController subscribeProjectController, Subscriber subscriber, NotificationService notificationService){
        this.subscribeProjectController = subscribeProjectController
        this.subscriber = subscriber
        this.notificationService = notificationService
        this.projectCodeToCheckBox = new HashMap<>()
    }

    /**
     * Returns an instance of a {@link CheckBox} or creates a new one if none exits already.
     * Furthermore, a listener is added that triggers (un)subscription based on the checkbox value
     * @param project A {@link ProjectSummary} to which the checkbox is bound
     * @return A checkbox which is already preconfigured with listeners and bound to the subscribe use case
     */
    CheckBox getSubscriptionCheckbox(ProjectSummary project){
        println(project.toString())

        if(projectCodeToCheckBox.containsKey(project.code)){
            return projectCodeToCheckBox.get(project.code)
        }

        CheckBox checkBox = initCheckbox(project)
        addListener(checkBox,project)
        projectCodeToCheckBox.put(project.code, checkBox)

        return checkBox
    }

    private CheckBox initCheckbox(ProjectSummary project){
        CheckBox checkBox = new CheckBox()
        checkBox.value = project.hasSubscription
        return checkBox
    }

    private void addListener(CheckBox checkBox, ProjectSummary summary){
        checkBox.addValueChangeListener({
            try{
                if (checkBox.value) {
                    subscribeProjectController.subscribeProject(subscriber, summary.code)
                } else {
                    subscribeProjectController.unsubscribeProject(subscriber, summary.code)
                }
                summary.hasSubscription = checkBox.value
            }catch(Exception exception){
                notificationService.publishFailure("There was a failure while changing the subscription value of project ${summary.code}. Contact ${Constants.CONTACT_HELPDESK}")
                log.error("There was a failure while changing the subscription value of project ${summary.code} for ${subscriber.firstName}  ${subscriber.lastName}")
                log.error(exception.message)
            }

        })
    }
}