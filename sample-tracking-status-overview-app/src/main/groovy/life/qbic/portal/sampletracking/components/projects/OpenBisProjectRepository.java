package life.qbic.portal.sampletracking.components.projects;

import static org.apache.logging.log4j.LogManager.getLogger;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.fetchoptions.ProjectFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.search.ProjectSearchCriteria;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import life.qbic.datamodel.dtos.portal.PortalUser;
import life.qbic.portal.sampletracking.components.projects.viewmodel.Project;
import life.qbic.portal.sampletracking.old.datasources.Credentials;
import org.apache.logging.log4j.Logger;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class OpenBisProjectRepository implements ProjectRepository {

  private static final Logger log = getLogger(OpenBisProjectRepository.class);

  private final String sessionToken;

  private final IApplicationServerApi api;

  private static final int TIMEOUT = 10_000;


  public OpenBisProjectRepository(Credentials credentials, PortalUser portalUser,
      String openBisUrl) {
    this.api = HttpInvokerUtils.createServiceStub(IApplicationServerApi.class,
        openBisUrl + "/rmi-application-server-v3", TIMEOUT);
    this.sessionToken = api.loginAs(credentials.getUser(), credentials.getPassword(),
        portalUser.getAuthProviderId());
  }

  @Override
  public List<Project> findAll() {
    ProjectFetchOptions fetchOptions = new ProjectFetchOptions();
    fetchOptions.withSpace();
    SearchResult<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> projects =
        api.searchProjects(sessionToken, new ProjectSearchCriteria(), fetchOptions);
    return projects.getObjects().stream()
        .filter(it -> isValidProjectCode(it.getCode()))
        .map(it -> new Project(it.getCode(), Optional.ofNullable(it.getDescription()).orElse("")))
        .collect(Collectors.toList());
  }

  private boolean isValidProjectCode(String code) {
    return Pattern.compile("^Q[A-X0-9]{4}$").asPredicate().test(code);
  }
}
