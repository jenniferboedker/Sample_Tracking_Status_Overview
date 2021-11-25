package life.qbic.portal.sampletracking.components.projectoverview.subscribe

import com.vaadin.ui.CheckBox
import life.qbic.business.project.subscribe.Subscriber
import life.qbic.portal.sampletracking.components.projectoverview.ProjectSummary

/**
 * <b>Checkboxes to handle project subscription</b>
 *
 * <p>Creates a checkbox for a {@link ProjectSummary} and adds a listener that triggers unsubscription from the project</p>
 *
 * @since 1.0.0
 */
class subscriptionCheckboxFactory {

    private final SubscribeProjectController subscribeProjectController
    private final Subscriber subscriber

    subscriptionCheckboxFactory(SubscribeProjectController subscribeProjectController, Subscriber subscriber){
        this.subscribeProjectController = subscribeProjectController
        this.subscriber = subscriber
    }

    /**
     * Creates an instance of a {@link CheckBox} and adds a listener that triggers (un)subscription based on the checkbox value
     * @param project A {@link ProjectSummary} to which the checkbox is bound
     * @return A checkbox which is already preconfigured with listeners and bound to the subscribe use case
     */
    CheckBox createInstance(ProjectSummary project){
        CheckBox checkBox = initCheckbox(project)
        addListener(checkBox,project)

        return checkBox
    }

    private CheckBox initCheckbox(ProjectSummary project){
        CheckBox checkBox = new CheckBox()
        checkBox.value = project.hasSubscription
        return checkBox
    }

    private void addListener(CheckBox checkBox, ProjectSummary summary){
        checkBox.addValueChangeListener({
            summary.hasSubscription = checkBox.value

            if(checkBox.value){
                subscribeProjectController.subscribeProject(subscriber, summary.code)
            } else{
                subscribeProjectController.unsubscribeProject(subscriber, summary.code)
            }

        })
    }
}