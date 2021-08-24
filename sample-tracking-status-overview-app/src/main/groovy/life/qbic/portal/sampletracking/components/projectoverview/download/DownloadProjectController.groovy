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
     * Triggers the download use case
     * @param projectCode
     */
    void downloadProject(String projectCode) {
        downloadSamples.requestSampleCodesFor(projectCode)
    }
}
