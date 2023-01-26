package life.qbic.portal.sampletracking.data;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.fetchoptions.ProjectFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.project.search.ProjectSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import life.qbic.datamodel.dtos.portal.PortalUser;
import life.qbic.portal.sampletracking.view.projects.viewmodel.Project;
import life.qbic.portal.sampletracking.view.samples.viewmodel.Sample;

public class OpenBisConnector implements ProjectRepository, SampleRepository, NgsSampleRepository {

  private final String sessionToken;

  private final IApplicationServerApi api;

  private static final int TIMEOUT = 10_000;
  private final Map<String, List<ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample>> sampleCache = new HashMap<>();
  private final Map<String, ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> projectCache = new HashMap<>();



  public OpenBisConnector(Credentials credentials, PortalUser portalUser,
      String openBisUrl) {
    this.api = HttpInvokerUtils.createServiceStub(IApplicationServerApi.class,
        openBisUrl + "/rmi-application-server-v3", TIMEOUT);
    this.sessionToken = api.loginAs(credentials.getUser(), credentials.getPassword(),
        portalUser.getAuthProviderId());
  }

  @Override
  public List<Project> findAllProjects() {
    if (projectCache.isEmpty()) {
      updateCache();
    }
    return projectCache.entrySet().stream()
        .map(it -> new Project(it.getKey(), Optional.ofNullable(it.getValue().getDescription()).orElse("")))
        .collect(toList());
  }

  @Override
  public List<String> findNGSSamplesForProject(String projectCode){

    return findAllSamplesForProject(projectCode).stream()
            .map(Sample::code)
            .collect(toList());
  }

  @Override
  public List<Sample> findAllSamplesForProject(String projectCode) {

    List<ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample> cachedProjectSamples = sampleCache
        .entrySet().stream()
        .filter(it -> it.getKey().equals(projectCode)).map(Entry::getValue).findAny().orElse(new ArrayList<>());
    return cachedProjectSamples.stream().map(convertToSample()).collect(toList());
  }

  private void updateCache() {

    SampleSearchCriteria isNgs = new SampleSearchCriteria();
    isNgs.withOrOperator();
    //search for all sample types that are related to NGS
    isNgs.withProperty("Q_SAMPLE_TYPE").thatContains("DNA");
    isNgs.withProperty("Q_SAMPLE_TYPE").thatContains("RNA");
    isNgs.withProperty("Q_SAMPLE_TYPE").thatContains("AMPLICON");
    isNgs.withProperty("Q_SAMPLE_TYPE").thatContains("R_RNA");
    isNgs.withProperty("Q_SAMPLE_TYPE").thatContains("M_RNA");
    isNgs.withProperty("Q_SAMPLE_TYPE").thatContains("SINGLE_NUCLEI");
    isNgs.withProperty("Q_SAMPLE_TYPE").thatContains("CF_DNA");
    // this guarantees that no q-entity is contained as only test samples have Q_SAMPLE_TYPE

    SampleFetchOptions fetchOptions = new SampleFetchOptions();
    fetchOptions.withType();
    fetchOptions.withProperties();
    SearchResult<ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample> sampleSearchResult = api.searchSamples(
        sessionToken, isNgs, fetchOptions);
    Map<String, List<ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample>> projectSampleMap = sampleSearchResult.getObjects()
        .stream()
        .filter(hasValidSampleCode())
        .collect(groupingBy(this::getProject));
    sampleCache.putAll(projectSampleMap);

    ProjectFetchOptions projectFetchOptions = new ProjectFetchOptions();
    projectFetchOptions.withSpace();
    ProjectSearchCriteria projectSearchCriteria = new ProjectSearchCriteria();

    SearchResult<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> projectSearchResult = api.searchProjects(
        sessionToken, projectSearchCriteria, projectFetchOptions);
    List<ch.ethz.sis.openbis.generic.asapi.v3.dto.project.Project> projects = projectSearchResult.getObjects()
        .stream().filter(it -> sampleCache.containsKey(it.getCode())).collect(toList());
    projects.forEach(it -> projectCache.put(it.getCode(), it));
  }

  private String getProject(
      ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample sample) {
    return sample.getCode().substring(0, 5);
  }

  private Predicate<? super ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample> hasValidSampleCode() {
    return sample -> sample.getCode().matches("Q[A-X0-9]{4}[0-9]{3}[A-X0-9]{2}$");
  }

  private static Function<ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample, Sample> convertToSample() {
    return it -> new Sample(it.getCode(), it.getProperty("Q_SECONDARY_NAME"));
  }

}
