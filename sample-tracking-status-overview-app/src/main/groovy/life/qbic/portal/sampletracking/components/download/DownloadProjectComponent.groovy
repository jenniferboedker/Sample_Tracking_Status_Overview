package life.qbic.portal.sampletracking.components.download


import com.vaadin.ui.Button
import groovy.transform.EqualsAndHashCode
import life.qbic.portal.sampletracking.components.projectoverview.ProjectSummary

import java.util.function.Consumer

/**
 * <b>A component providing users with the ability to start a project download</b>
 *
 * @since 1.0.0
 */
@EqualsAndHashCode(includeFields = true, callSuper = true)
class DownloadProjectComponent extends Button {
    private  Consumer<String> download
    private String projectCode
    private int downloadableSamplesCount

    private DownloadProjectComponent(String projectCode, int downloadableSamplesCount, Consumer<String> download) {
        this.projectCode = projectCode
        this.downloadableSamplesCount = downloadableSamplesCount
        this.download = download
        generateCaption()
        setupListener()
    }

    private void generateCaption() {
        if (downloadableSamplesCount) {
            String caption =  "Download $downloadableSamplesCount samples"
            this.setCaption(caption)
        } else {
            this.setCaption("No data available")
            this.setEnabled(false)
        }

    }

    private void setupListener() {
        this.addClickListener({
            download.accept(projectCode)
        })
    }

    static DownloadProjectComponent from(ProjectSummary projectSummary, Consumer<String> downloadProject) {
        //TODO replace samples received with samples available
        DownloadProjectComponent component = new DownloadProjectComponent(projectSummary.code, projectSummary.samplesReceived, downloadProject)
        return component
    }
}
