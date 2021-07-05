package life.qbic.portal.sampletracking.datasources

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.fetchoptions.ProjectFetchOptions
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.search.ProjectSearchCriteria
import ch.systemsx.cisd.common.spring.HttpInvokerUtils
import groovy.util.logging.Log4j2
import life.qbic.business.project.load.LoadProjectsDataSource
import life.qbic.datamodel.dtos.portal.PortalUser
import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace

/**
 * <b>OpenBIS connection provider</b>
 *
 * <p>Serves as a simple gateway to interact with the data management system openBIS.</p>
 *
 * @since 1.0.0
 */
@Log4j2
class OpenBisConnector implements LoadProjectsDataSource{

    private final String sessionToken

    private final IApplicationServerApi api

    private static final int TIMEOUT = 10_000

    OpenBisConnector(Credentials credentials, PortalUser portalUser, String openBisUrl) {
        this.api = HttpInvokerUtils.createServiceStub(IApplicationServerApi.class, openBisUrl + "/rmi-application-server-v3", TIMEOUT)
        this.sessionToken = api.loginAs(credentials.user, credentials.password, portalUser.authProviderId)
    }

    /**
     * @inheritDocs
     */
    @Override
    List<Project> fetchUserProjects(String userId) {
        ProjectFetchOptions fetchOptions = new ProjectFetchOptions()
        fetchOptions.withSpace()
        SearchResult<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> projects =
                 api.searchProjects(sessionToken, new ProjectSearchCriteria(), fetchOptions)
        def userProjects = []
        for (def project : projects.getObjects()) {
            try {
                def projectCode = new ProjectCode(project.code)
                def projectSpace = new ProjectSpace(project.space.code)
                Project projectOverview = new Project.Builder(new ProjectIdentifier(projectSpace, projectCode),
                        project.description?:"").build()
                userProjects << projectOverview
            } catch (IllegalArgumentException e) {
                // Mal-formatted project codes or spaces
                log.error(e.message)
            }
        }
        return userProjects
    }
}
