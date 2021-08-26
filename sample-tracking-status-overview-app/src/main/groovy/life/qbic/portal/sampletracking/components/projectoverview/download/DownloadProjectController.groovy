package life.qbic.portal.sampletracking.components.projectoverview.download


import life.qbic.business.samples.download.DownloadSamplesInput

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
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
