package life.qbic.portal.sampletracking.datasources

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.fetchoptions.ProjectFetchOptions
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.search.ProjectSearchCriteria
import ch.systemsx.cisd.common.spring.HttpInvokerUtils
import life.qbic.business.project.load.LoadProjectsDataSource
import life.qbic.datamodel.dtos.portal.PortalUser
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace

/**
 * <class short description - 1 Line!>
 *
 * <More detailed description - When to use, what it solves, etc.>
 *
 * @since 1.0.0
 */
class OpenBisConnector implements LoadProjectsDataSource{

    private final String sessionToken

    private final IApplicationServerApi api

    private static final int TIMEOUT = 10_000

    OpenBisConnector(Credentials credentials, PortalUser portalUser, String openBisUrl) {
        this.api = HttpInvokerUtils.createServiceStub(IApplicationServerApi.class, openBisUrl, TIMEOUT)
        this.sessionToken = api.loginAs(credentials.user, credentials.password, portalUser.authProviderId)
    }

    @Override
    List<Project> fetchUserProjects(String userId) {
        ProjectFetchOptions fetchOptions = new ProjectFetchOptions()
        SearchResult<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> projects =
                 api.searchProjects(sessionToken, new ProjectSearchCriteria(), fetchOptions)

        def userProjects = []
        for (def project : projects.getObjects()) {
            def projectCode = new ProjectCode(project.code)
            def projectSpace = new ProjectSpace(project.space.code)
            Project projectOverview = new Project.Builder(new ProjectIdentifier(projectSpace, projectCode),
                    project.description).build()
            userProjects << projectOverview
        }
        return userProjects
    }
}
