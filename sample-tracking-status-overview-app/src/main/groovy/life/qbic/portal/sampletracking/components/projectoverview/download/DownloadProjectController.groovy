package life.qbic.portal.sampletracking.components.projectoverview.download


import life.qbic.business.samples.download.DownloadSamplesInput

/**
 * <b>Controller allowing the view to call the download project use case</b>
 *
 * <p>Used via the view to start the download project use case with the currently selected project code.
 * Starting point where sample codes for this project with associated data are fed into the rest of the use case.</p>
 *
 * @since 1.0.0
 */
class DownloadProjectController {
  
    DownloadSamplesInput downloadSamples

    DownloadProjectController(DownloadSamplesInput downloadSamples) {
        this.downloadSamples = downloadSamples
    }
    
    /**
     * Triggers the download use case
     * @param projectCode
     */
    void downloadProject(String projectCode) {
        Optional.ofNullable(projectCode).ifPresent({
            downloadSamples.requestSampleCodesFor(projectCode)
        })
    }
}
