package life.qbic.portal.sampletracking.components.projectoverview.download

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
class DownloadProjectController {
  
    DownloadSamples downloadSamples

    DownloadProjectCrontroller(DownloadSamples downloadSamples) {
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
