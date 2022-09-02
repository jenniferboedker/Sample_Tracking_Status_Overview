package life.qbic.portal.sampletracking.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import life.qbic.portal.sampletracking.old.datasources.Credentials;
import life.qbic.portal.sampletracking.view.projects.viewmodel.ProjectStatus;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SampleTrackingConnector implements ProjectStatusProvider, SampleStatusProvider {

  private static final Logger log = LoggerFactory.getLogger(SampleTrackingConnector.class);

  public final String samplesSuffix;
  public final String statusSuffix;
  public final String projectsSuffix;

  private final String serviceAddress;
  private final String serviceUser;
  private final String userPass;

  public SampleTrackingConnector(String serviceUrlBase, String samplesSuffix, String statusSuffix,
      String projectsSuffix, Credentials credentials) {
    this.samplesSuffix = samplesSuffix;
    this.statusSuffix = statusSuffix;
    this.projectsSuffix = projectsSuffix;
    this.serviceAddress = Objects.requireNonNull(serviceUrlBase);
    this.serviceUser = Objects.requireNonNull(credentials.getUser());
    this.userPass = Objects.requireNonNull(credentials.getPassword());
  }

  private static class Project extends ArrayList<Sample> {
  }

  private static class Sample {

    @JsonProperty("status")
    private String status;
    @JsonProperty("sampleCode")
    private String sampleCode;
    @JsonProperty("statusValidSince")
    private Instant statusValidSince;

    private Sample() {
    }

    private Sample(String status, String sampleCode, Instant statusValidSince) {
      this.status = status;
      this.sampleCode = sampleCode;
      this.statusValidSince = statusValidSince;
    }

    public String status() {
      return status;
    }

    public String code() {
      return sampleCode;
    }

    public Instant statusValidSince() {
      return statusValidSince;
    }
  }

  private final Map<String, Project> cachedProjects = Collections.synchronizedMap(new HashMap<>());

  @Override
  public Optional<ProjectStatus> getForProject(String projectCode) {
    Optional<ProjectStatus> cachedStatusForProject = getCachedStatusForProject(projectCode);
    return cachedStatusForProject.isPresent() ? cachedStatusForProject
        : askServiceForProject(projectCode).map(ProjectSatusMapper::toProjectStatus);
  }

  private Optional<ProjectStatus> getCachedStatusForProject(String projectCode) {
    synchronized (cachedProjects) {
      if (cachedProjects.containsKey(projectCode)) {
        Project project = cachedProjects.get(projectCode);
        return Optional.of(ProjectSatusMapper.toProjectStatus(project));
      }
    }
    return Optional.empty();
  }

  @Override
  public Optional<String> getForSample(String sampleCode) {
    Optional<String> cachedStatusForSample = getCachedStatusForSample(sampleCode);
    return cachedStatusForSample.isPresent() ? cachedStatusForSample
        : askServiceForSample(sampleCode).map(SampleStatusMapper::toSampleStatus);
  }

  private synchronized Optional<String> getCachedStatusForSample(String sampleCode) {
    String projectCode = sampleCode.substring(0, 5);
    synchronized (cachedProjects) {
      if (cachedProjects.containsKey(projectCode)) {
        Project project = cachedProjects.get(projectCode);
        return project.stream()
            .filter(it -> it.code().equals(sampleCode))
            .map(Sample::status)
            .findAny();
      }
    }
    return Optional.empty();
  }

  private Optional<Project> askServiceForProject(String projectCode) {
    String projectJson = requestProjectOverHttp(projectCode);
    if (projectJson.isEmpty()) {
      return Optional.empty();
    }
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    try {
      List<Sample> samples = objectMapper.readValue(projectJson, new TypeReference<ArrayList<Sample>>() {
      });
      Project project = new Project();
      project.addAll(samples);
      synchronized (cachedProjects) {
        cachedProjects.put(projectCode, project);
      }
      return Optional.of(project);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
      return Optional.empty();
    }
  }

  private String requestProjectOverHttp(String projectCode) {
    String changingProjectSuffix = String.format("/%s", projectCode);
    // uses httpclient5 https://hc.apache.org/httpcomponents-client-5.1.x/quickstart.html
    final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
    credsProvider.setCredentials(
        new AuthScope(null, -1),
        new UsernamePasswordCredentials(serviceUser, userPass.toCharArray()));
    try (CloseableHttpClient httpclient = HttpClients.custom()
        .setDefaultCredentialsProvider(credsProvider).build()) {
      HttpGet httpGet = new HttpGet(
          serviceAddress + projectsSuffix + changingProjectSuffix + statusSuffix);
      try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
        if (response.getCode() != 200) {
          throw new RuntimeException(
              String.format("Unsuccessful response for project %s: %s %s", projectCode, response.getCode(),
                  response.getReasonPhrase()));
        }
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        EntityUtils.consume(entity);

        return responseBody;
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Optional<String> requestSampleOverHttp(String sampleCode) {
    String changingSampleSuffix = String.format("/%s", sampleCode);
    // uses httpclient5 https://hc.apache.org/httpcomponents-client-5.1.x/quickstart.html
    final BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
    credsProvider.setCredentials(
        new AuthScope(null, -1),
        new UsernamePasswordCredentials(serviceUser, userPass.toCharArray()));
    try (CloseableHttpClient httpclient = HttpClients.custom()
        .setDefaultCredentialsProvider(credsProvider).build()) {
      HttpGet httpGet = new HttpGet(
          serviceAddress + samplesSuffix + changingSampleSuffix + statusSuffix);
      try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
        if (response.getCode() == 404) {
          return Optional.empty();
        }
        if (response.getCode() != 200) {
          log.warn(String.format("Unsuccessful response for sample %s: %s %s", sampleCode,
              response.getCode(),
              response.getReasonPhrase()));
          return Optional.empty();
        }
        HttpEntity entity = response.getEntity();
        String responseBody = EntityUtils.toString(entity);
        EntityUtils.consume(entity);

        return Optional.of(responseBody);
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private Optional<Sample> askServiceForSample(String sampleCode) {

    Optional<String> sampleJson = requestSampleOverHttp(sampleCode);
    if (!sampleJson.isPresent()) {
      return Optional.empty();
    }
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    try {
      Sample sample = objectMapper.readValue(sampleJson.get(), Sample.class);
      String projectCode = sampleCode.substring(0, 5);
      synchronized (cachedProjects) {
        if (cachedProjects.containsKey(projectCode)) {
          Project cachedProject = cachedProjects.get(projectCode);
          cachedProject.add(sample);
          cachedProjects.put(projectCode, cachedProject);
        }
      }
      return Optional.of(sample);
    } catch (JsonProcessingException e) {
      log.error(e.getMessage(), e);
      return Optional.empty();
    }
  }

  static private class ProjectSatusMapper {

    static ProjectStatus toProjectStatus(Project project) {
      int totalCount = project.size();
      int receivedCount = (int) project.stream()
          .filter(it -> it.status.equals("SAMPLE_RECEIVED"))
          .count();
      int sampleQcFailedCount = (int) project.stream()
          .filter(it -> it.status.equals("SAMPLE_QC_FAIL"))
          .count();
      int sampleQcPassedCount = (int) project.stream()
          .filter(it -> it.status.equals("SAMPLE_QC_PASS"))
          .count();
      int libraryPreparedCount = (int) project.stream()
          .filter(it -> it.status.equals("LIBRARY_PREP_FINISHED"))
          .count();
      int dataAvailableCount = (int) project.stream()
          .filter(it -> it.status.equals("DATA_AVAILABLE"))
          .count();
      Instant lastModified = project.stream().map(Sample::statusValidSince)
          .max(Comparator.naturalOrder()).orElse(Instant.MIN);

      return new ProjectStatus(totalCount,
          receivedCount + sampleQcFailedCount + sampleQcPassedCount + libraryPreparedCount + dataAvailableCount,
          sampleQcPassedCount + libraryPreparedCount + dataAvailableCount,
          sampleQcFailedCount + libraryPreparedCount + dataAvailableCount,
          libraryPreparedCount + dataAvailableCount,
          dataAvailableCount,
          lastModified);
    }
  }

  static private class SampleStatusMapper {

    static String toSampleStatus(Sample sample) {
      return sample.status();
    }
  }


}
