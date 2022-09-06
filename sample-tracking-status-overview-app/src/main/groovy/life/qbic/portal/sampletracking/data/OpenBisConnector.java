package life.qbic.portal.sampletracking.data;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.fetchoptions.ProjectFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.search.ProjectSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import life.qbic.datamodel.dtos.portal.PortalUser;
import life.qbic.portal.sampletracking.view.projects.viewmodel.Project;
import life.qbic.portal.sampletracking.view.samples.viewmodel.Sample;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class OpenBisConnector implements ProjectRepository, SampleRepository {

  private final String sessionToken;

  private final IApplicationServerApi api;

  private static final int TIMEOUT = 10_000;

  private final List<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> cachedProjects = new ArrayList<>();
  private final Map<String, List<ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample>> cachedSamples = new HashMap<>();



  public OpenBisConnector(Credentials credentials, PortalUser portalUser,
      String openBisUrl) {
    this.api = HttpInvokerUtils.createServiceStub(IApplicationServerApi.class,
        openBisUrl + "/rmi-application-server-v3", TIMEOUT);
    this.sessionToken = api.loginAs(credentials.getUser(), credentials.getPassword(),
        portalUser.getAuthProviderId());
  }

  @Override
  public List<Project> findAllProjects() {
    if (cachedProjects.isEmpty()) {
      ProjectFetchOptions fetchOptions = new ProjectFetchOptions();
      fetchOptions.withSpace();
      SearchResult<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> projectSearchResult =
          api.searchProjects(sessionToken, new ProjectSearchCriteria(), fetchOptions);

      List<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> projects = projectSearchResult.getObjects().stream()
          .filter(hasValidProjectCode()).sorted(Comparator.comparing(
              ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project::getRegistrationDate).reversed())
          .collect(Collectors.toList());
      cachedProjects.addAll(projects);
    }
    return cachedProjects.stream()
        .map(it -> new Project(it.getCode(),
            Optional.ofNullable(it.getDescription()).orElse("")))
        .collect(Collectors.toList());
  }

  @Override
  public List<Sample> findAllSamplesForProject(String projectCode) {

    List<ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample> samples;
    if (cachedSamples.containsKey(projectCode)) {
      samples = cachedSamples.get(projectCode);
    } else {
      SampleSearchCriteria sampleSearchCriteria = new SampleSearchCriteria();
      sampleSearchCriteria.withCode().thatStartsWith(projectCode);
      SampleFetchOptions fetchOptions = new SampleFetchOptions();
      fetchOptions.withProperties();
      SearchResult<ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample> sampleSearchResult = api.searchSamples(
          sessionToken, sampleSearchCriteria, fetchOptions);
      samples = sampleSearchResult.getObjects();
      cachedSamples.put(projectCode, samples);
    }
    return samples.stream()
        .filter(hasValidSampleCode())
        .filter(notAnEntity())
        .map(convertToSample())
        .collect(Collectors.toList());
  }

  private Predicate<? super ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample> hasValidSampleCode() {
    return sample -> sample.getCode().matches("Q[A-X0-9]{4}[0-9]{3}[A-X0-9]{2}$");
  }

  private Predicate<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> hasValidProjectCode() {
    return project -> project.getCode().matches("^Q[A-X0-9]{4}$");
  }

  private static Function<ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample, Sample> convertToSample() {
    return it -> new Sample(it.getCode(), it.getProperty("Q_SECONDARY_NAME"));
  }

  private static Predicate<ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample> notAnEntity() {
    return it -> !it.getCode().contains("ENTITY");
  }

}
