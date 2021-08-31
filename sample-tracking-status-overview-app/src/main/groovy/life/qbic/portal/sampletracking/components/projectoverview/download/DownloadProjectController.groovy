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
     * Triggers the download use case. If no project code is provided, throws an {@link IllegalArgumentException}
     * @param projectCode the code of the selected project
     * @throws IllegalArgumentException in case the project code is not provided
     */
    void downloadProject(String projectCode) throws IllegalArgumentException{
        if (! projectCode) {
            throw new IllegalArgumentException("No project selected for download.")
        }
        Optional.ofNullable(projectCode).ifPresent({
            downloadSamples.requestSampleCodesFor(projectCode)
        })


    }
}
