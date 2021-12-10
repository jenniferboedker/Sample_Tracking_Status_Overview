package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.data.provider.DataProvider
import com.vaadin.data.provider.ListDataProvider
import com.vaadin.ui.CheckBox
import com.vaadin.ui.renderers.ComponentRenderer
import com.vaadin.ui.renderers.Renderer
import life.qbic.business.project.subscribe.Subscriber
import life.qbic.portal.sampletracking.communication.notification.NotificationService
import life.qbic.portal.sampletracking.components.ViewModel
import life.qbic.portal.sampletracking.components.projectoverview.statusdisplay.SampleCount
import life.qbic.portal.sampletracking.components.projectoverview.statusdisplay.State
import life.qbic.portal.sampletracking.components.projectoverview.subscribe.SubscribeProjectController
import life.qbic.portal.sampletracking.components.projectoverview.subscribe.SubscriptionCheckboxFactory

class ProjectView extends ProjectDesign{

    private final ViewModel viewModel
    final static int MAX_CODE_COLUMN_WIDTH = 400
    private final SubscriptionCheckboxFactory subscriptionCheckboxFactory


    ProjectView(ViewModel viewModel, SubscribeProjectController subscribeProjectController, NotificationService notificationService, Subscriber subscriber) {
        super()
        this.viewModel = viewModel
        this.subscriptionCheckboxFactory = new SubscriptionCheckboxFactory(subscribeProjectController, subscriber,notificationService)

        bindData()
        addClickListener()
    }

    /**
     * NO LAYOUTING HERE
     */
    private void bindData(){
        projectGrid.getColumn("hasSubscription").setRenderer({ new CheckBox("",it as Boolean)}, new ComponentRenderer())
                .setMaximumWidth(MAX_CODE_COLUMN_WIDTH).setStyleGenerator({"subscription-checkbox"})

        projectGrid.getColumn("title").setMaximumWidth(800)
        projectGrid.getColumn("code").setMaximumWidth(MAX_CODE_COLUMN_WIDTH)

        projectGrid.getColumn("samplesReceived").setExpandRatio(1).setStyleGenerator({ProjectSummary project -> getStyleForColumn(project.samplesReceived)})
        projectGrid.getColumn("samplesQc").setExpandRatio(1).setStyleGenerator({ProjectSummary project -> getStyleForColumn(project.samplesQc)})
        projectGrid.getColumn("samplesLibraryPrepFinished").setExpandRatio(1).setStyleGenerator({ProjectSummary project -> getStyleForColumn(project.samplesLibraryPrepFinished)})
        projectGrid.getColumn("sampleDataAvailable").setExpandRatio(1)setStyleGenerator({ProjectSummary project -> getStyleForColumn(project.sampleDataAvailable)})

        refreshDataProvider()
    }

    private void refreshDataProvider() {
        DataProvider dataProvider = new ListDataProvider(viewModel.projectOverviews)
        projectGrid.setDataProvider(dataProvider)

        if( viewModel.selectedProject ) {
            projectGrid.select(viewModel.selectedProject)
        }

        filterEmptyProjects()
    }

    private void filterEmptyProjects(){
        ListDataProvider<ProjectSummary> dataProvider = (ListDataProvider<ProjectSummary>) projectGrid.getDataProvider()
        dataProvider.setFilter(ProjectSummary::getTotalSampleCount, totalNumber -> totalNumber > 0)
    }

    private static String getStyleForColumn(SampleCount sampleStatusCount) {
        State state = determineCompleteness(sampleStatusCount)
        return state.getCssClass()
    }

    /**
     * Determines the state of the current status. Is it in progress or did it complete already
     * @param sampleCount The total number of samples registered
     */
    private static State determineCompleteness(SampleCount sampleCount) {
        if (sampleCount.failingSamples > 0) {
            return State.FAILED
        } else if (sampleCount.totalSampleCount == 0) {
            return State.IN_PROGRESS
        } else if (sampleCount.passingSamples == sampleCount.totalSampleCount) {
            return State.COMPLETED
        } else if (sampleCount.passingSamples < sampleCount.totalSampleCount) {
            return State.IN_PROGRESS
        } else {
            //unexpected!!
            throw new IllegalStateException("status count $sampleCount.passingSamples must not be greater total count $sampleCount.totalSampleCount")
        }
    }


    private void addClickListener() {
        this.samplesButton.addClickListener({
            viewModel.projectViewEnabled = false
            viewModel.sampleViewEnabled = true
        })

        viewModel.addPropertyChangeListener({
            if(viewModel.projectViewEnabled){
                this.setVisible(true)
                this.samplesButton.setEnabled(true)
            }else{
                this.setVisible(false)
                this.samplesButton.setEnabled(false)
            }
        })
    }
}