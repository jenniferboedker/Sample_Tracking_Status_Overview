package life.qbic.portal.sampletracking.data;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.fetchoptions.ProjectFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.search.ProjectSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import life.qbic.datamodel.dtos.portal.PortalUser;
import life.qbic.portal.sampletracking.view.projects.viewmodel.Project;
import life.qbic.portal.sampletracking.view.samples.viewmodel.Sample;

public class OpenBisConnector implements ProjectRepository, SampleRepository, NgsSampleRepository {

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
      //todo can we optimize the load by using search criteria smart?
      SearchResult<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> projectSearchResult =
          api.searchProjects(sessionToken, new ProjectSearchCriteria(), fetchOptions);

      List<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> projects = projectSearchResult.getObjects().stream()
          .filter(hasValidProjectCode())
              .filter(isNgsProject())
          .sorted(Comparator.comparing(
              ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project::getModificationDate).reversed())
          .collect(Collectors.toList());

     // System.out.println(projects.get(0).getSamples());

      cachedProjects.addAll(projects);
    }
    return cachedProjects.stream()
        .map(it -> new Project(it.getCode(),
            Optional.ofNullable(it.getDescription()).orElse("")))
        .collect(Collectors.toList());
  }

  @Override
  public List<String> findNGSSamplesForProject(String projectCode){

    return findAllSamplesForProject(projectCode).stream()
            .map(Sample::code)
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
      sampleSearchCriteria.withType().withCode().thatEquals("Q_TEST_SAMPLE");
      //sampleSearchCriteria.withProperty("Q_SAMPLE_TYPE").thatContains("DNA");
      //sampleSearchCriteria.withProperty("Q_SAMPLE_TYPE").thatContains("RNA");

      SampleFetchOptions fetchOptions = new SampleFetchOptions();
      fetchOptions.withType();
      fetchOptions.withProperties();
      SearchResult<ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample> sampleSearchResult = api.searchSamples(
          sessionToken, sampleSearchCriteria, fetchOptions);
      samples = sampleSearchResult.getObjects();
      samples = samples.stream().filter(OpenBisConnector::isNGSSample).collect(Collectors.toList());

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

  private Predicate<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> isNgsProject() {
    //if there is a ngs sample on test sample level its at least multi omics and needs to be included

    return project -> findAllSamplesForProject(project.getCode()).size() > 0;
  }

  private static boolean isNGSSample(ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample it) {
    if(it == null || it.getProperty("Q_SAMPLE_TYPE") == null) return false;

    return it.getProperty("Q_SAMPLE_TYPE").contains("DNA") || it.getProperty("Q_SAMPLE_TYPE").contains("RNA");
  }

  private static Function<ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample, Sample> convertToSample() {
    return it -> new Sample(it.getCode(), it.getProperty("Q_SECONDARY_NAME"));
  }

  private static Predicate<ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample> notAnEntity() {
    return it -> !it.getCode().contains("ENTITY");
  }

}
