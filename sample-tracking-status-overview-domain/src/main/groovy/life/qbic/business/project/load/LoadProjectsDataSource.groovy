package life.qbic.business.project.load

import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier

/**
 * <b>Provides project identifiers</b>
 *
 * <p>This interface is used by {@link LoadProjects}</p>
 *
 * @since 1.0.0
 */
interface LoadProjectsDataSource {

    /**
     * Loads project identifiers given a user identifier
     * @param userId the identifier that can be used to retrieve projects
     * @return a list of project identifiers the user has access to
     * @since 1.0.0
     */
    List<ProjectIdentifier> projectIdentifiersForUser(String userId)
}
