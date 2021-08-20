package life.qbic.business.project.download

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
interface DownloadProjectInput {

    /**
     * Provides a description of downloadable data for a given project code
     * @param projectCode the project for which downloadable content should be searched
     * @return a project download
     */
    ProjectDownload download(String projectCode)

}