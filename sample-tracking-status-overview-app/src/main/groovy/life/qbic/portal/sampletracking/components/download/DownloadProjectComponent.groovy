package life.qbic.portal.sampletracking.components.download


import com.vaadin.ui.Button
import life.qbic.portal.sampletracking.components.projectoverview.ProjectSummary

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
class DownloadProjectComponent extends Button {
    private String projectCode
    private int downloadableSamplesCount

    private DownloadProjectComponent(String projectCode, int downloadableSamplesCount) {
        this.projectCode = projectCode
        this.downloadableSamplesCount = downloadableSamplesCount
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
            println("Clicked download button for $projectCode")
        })
    }

    static DownloadProjectComponent from(ProjectSummary projectSummary) {
        //TODO replace samples received with samples available
        DownloadProjectComponent component = new DownloadProjectComponent(projectSummary.code, projectSummary.samplesReceived)
        return component
    }
}
