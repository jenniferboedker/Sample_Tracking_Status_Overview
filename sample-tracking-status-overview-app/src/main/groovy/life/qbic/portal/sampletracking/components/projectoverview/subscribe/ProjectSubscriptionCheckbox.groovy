package life.qbic.portal.sampletracking.components.projectoverview.subscribe

import com.sun.tools.javac.comp.Check
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
class ProjectSubscriptionCheckbox {

    private final SubscribeProjectController subscribeProjectController

    ProjectSubscriptionCheckbox(SubscribeProjectController subscribeProjectController){
        this.subscribeProjectController = subscribeProjectController
    }

    /**
     * Creates an instance of a {@link CheckBox} and adds a listener that triggers (un)subscription based on the checkbox value
     * @param project
     * @param subscriber
     * @return
     */
    //todo I think its not nice that we need to provide the subscriber to the checkbox, why not adding it to the subscribe use case directly?
    CheckBox createInstance(ProjectSummary project, Subscriber subscriber){
        CheckBox checkBox = initCheckbox(project)
        addListener(checkBox,project, subscriber)

        return checkBox
    }

    private CheckBox initCheckbox(ProjectSummary project){
        CheckBox checkBox = new CheckBox()
        checkBox.value = project.hasSubscription
        return checkBox
    }

    private void addListener(CheckBox checkBox, ProjectSummary summary, Subscriber subscriber){
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