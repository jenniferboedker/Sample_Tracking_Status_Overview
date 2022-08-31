package life.qbic.portal.sampletracking.data;

import static org.apache.logging.log4j.LogManager.getLogger;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.fetchoptions.ProjectFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.search.ProjectSearchCriteria;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import life.qbic.datamodel.dtos.portal.PortalUser;
import life.qbic.portal.sampletracking.old.datasources.Credentials;
import life.qbic.portal.sampletracking.view.projects.viewmodel.Project;
import life.qbic.portal.sampletracking.view.samples.viewmodel.Sample;
import org.apache.logging.log4j.Logger;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class OpenBisConnector implements ProjectRepository, SampleRepository {

  private static final Logger log = getLogger(OpenBisConnector.class);

  private final String sessionToken;

  private final IApplicationServerApi api;

  private static final int TIMEOUT = 10_000;

  private List<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> cache = new ArrayList<>();


  public OpenBisConnector(Credentials credentials, PortalUser portalUser,
      String openBisUrl) {
    this.api = HttpInvokerUtils.createServiceStub(IApplicationServerApi.class,
        openBisUrl + "/rmi-application-server-v3", TIMEOUT);
    this.sessionToken = api.loginAs(credentials.getUser(), credentials.getPassword(),
        portalUser.getAuthProviderId());
  }

  @Override
  public List<Project> findAllProjects() {
    if (cache.isEmpty()) {
      ProjectFetchOptions fetchOptions = new ProjectFetchOptions();
      fetchOptions.withSpace();
      SearchResult<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> projectSearchResult =
          api.searchProjects(sessionToken, new ProjectSearchCriteria(), fetchOptions);

      List<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> projects = projectSearchResult.getObjects().stream()
          .filter(it -> isValidProjectCode(it.getCode())).collect(Collectors.toList());
      cache.addAll(projects);
    }
    return cache.stream().map(it -> new Project(it.getCode(), Optional.ofNullable(it.getDescription()).orElse("")))
        .collect(Collectors.toList());
  }

  @Override
  public List<Sample> findAllSamplesForProject(String projectCode) {
    Optional<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> cachedProject = cache.stream()
        .filter(it -> it.getCode().equals(projectCode))
        .findAny();
    if (cachedProject.isPresent()) {
      ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project project = cachedProject.get();
      return project.getSamples().stream()
          .map(it -> new Sample(it.getCode(), it.getProperty("Q_SECONDARY_NAME")))
          .collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  private boolean isValidProjectCode(String code) {
    return Pattern.compile("^Q[A-X0-9]{4}$").asPredicate().test(code);
  }
}
