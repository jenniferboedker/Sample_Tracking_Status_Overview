package life.qbic.business.project.load

import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier

/**
 * <b>Data source interface for the {@link LoadProjects} feature</b>
 *
 * <p>Provides functionality to load projects from the data source</p>>
 *
 * @since 1.0.0
 */
interface LoadProjectsInfoDataSource {

    /**
     * Loads project DTOs given a user identifier
     * @param projectIdentifiers the project identifiers for which data should be loaded
     * @return a list of projects the user has access to
     * @since 1.0.0
     */
    List<Project> projectsFromIds(List<ProjectIdentifier> projectIdentifiers)
}