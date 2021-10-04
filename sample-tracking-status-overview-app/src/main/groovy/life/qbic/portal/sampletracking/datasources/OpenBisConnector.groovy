package life.qbic.portal.sampletracking.datasources

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.fetchoptions.ProjectFetchOptions
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.search.ProjectSearchCriteria
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria
import ch.systemsx.cisd.common.spring.HttpInvokerUtils
import groovy.util.logging.Log4j2
import life.qbic.business.DataSourceException
import life.qbic.business.project.Project
import life.qbic.business.project.load.LoadProjectsDataSource
import life.qbic.business.samples.info.GetSamplesInfoDataSource
import life.qbic.datamodel.dtos.portal.PortalUser
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
class OpenBisConnector implements LoadProjectsDataSource, GetSamplesInfoDataSource {

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
    Map<String, String> fetchSampleNamesFor(List<String> sampleCodes) throws DataSourceException {
      Map<String, String> codesToNames = new HashMap<>()
      try {
          SampleFetchOptions fetchOptions = new SampleFetchOptions()
          fetchOptions.withProperties()
          
          SampleSearchCriteria searchCriteria = new SampleSearchCriteria()
          searchCriteria.withOrOperator()
          
          for (def code : sampleCodes) {
            searchCriteria.withCode().thatEquals(code)
          }
          
          SearchResult<ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample> samples =
                  api.searchSamples(sessionToken, searchCriteria, fetchOptions)
          for (def sample : samples.getObjects()) {
              try {
                  def sampleCode = sample.code
                  def properties = sample.properties
                  def name = properties.get("Q_SECONDARY_NAME")
                  codesToNames.put(sampleCode, name)
              } catch (IllegalArgumentException e) {
                  log.error(e.message)
              }
          }
      } catch (Exception unexpected) {
          throw new DataSourceException("Could not fetch names for sample codes.", unexpected)
      }
      return codesToNames
    }


    /**
     * Maps `life.qbic.datamodel.dtos.projectmanagement.Project` to `Project`
     * @param project
     * @return a project for the given input arguments
     */
    private static final Project getProjectFrom(life.qbic.datamodel.dtos.projectmanagement.Project project) {
        return Optional.of(project).map({
            new Project(it.projectId.projectCode.toString(), it.projectTitle)
        }).orElseThrow({
            new IllegalArgumentException("Invalid project provided: $project")
        })
    }
    /**
     * @inheritDocs
     */
    @Override
    List<Project> fetchUserProjects() {
        List<Project> userProjects = []
        try {
            ProjectFetchOptions fetchOptions = new ProjectFetchOptions()
            fetchOptions.withSpace()
            SearchResult<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> projects =
                    api.searchProjects(sessionToken, new ProjectSearchCriteria(), fetchOptions)
            for (def project : projects.getObjects()) {
                try {
                    def projectCode = new ProjectCode(project.code)
                    def projectSpace = new ProjectSpace(project.space.code)
                    life.qbic.datamodel.dtos.projectmanagement.Project projectOverview = new life.qbic.datamodel.dtos.projectmanagement.Project.Builder(
                            new ProjectIdentifier(projectSpace,
                                    projectCode),
                            project.description ?: "").build()
                    userProjects << getProjectFrom(projectOverview)
                } catch (IllegalArgumentException e) {
                    // Mal-formatted project codes or spaces
                    log.error(e.message)
                }
            }
        } catch (Exception unexpected) {
            throw new DataSourceException("Could not load projects.", unexpected)
        }
        return userProjects
    }
}
