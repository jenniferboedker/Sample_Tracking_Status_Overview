package life.qbic.portal.sampletracking.data;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SampleTrackingConnector implements ProjectStatusProvider, SampleStatusProvider {

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

    final String status;
    final String code;
    final Instant statusValidSince;

    private Sample(String status, String code, Instant statusValidSince) {
      this.status = status;
      this.code = code;
      this.statusValidSince = statusValidSince;
    }

    public String status() {
      return status;
    }

    public String code() {
      return code;
    }

    public Instant statusValidSince() {
      return statusValidSince;
    }
  }

  private final Map<String, Project> cachedProjects = new HashMap<>();

  @Override
  public Optional<ProjectStatus> getForProject(String projectCode) {
    Optional<ProjectStatus> cachedStatusForProject = getCachedStatusForProject(projectCode);
    return cachedStatusForProject.isPresent() ? cachedStatusForProject
        : askServiceForProject(projectCode).map(ProjectSatusMapper::toProjectStatus);
  }

  private Optional<ProjectStatus> getCachedStatusForProject(String projectCode) {
    if (cachedProjects.containsKey(projectCode)) {
      Project project = cachedProjects.get(projectCode);
      return Optional.of(ProjectSatusMapper.toProjectStatus(project));
    }
    return Optional.empty();
  }

  @Override
  public Optional<String> getForSample(String sampleCode) {
    Optional<String> cachedStatusForSample = getCachedStatusForSample(sampleCode);
    return cachedStatusForSample.isPresent() ? cachedStatusForSample
        : askServiceForSample(sampleCode).map(SampleStatusMapper::toSampleStatus);
  }

  private Optional<String> getCachedStatusForSample(String sampleCode) {
    String projectCode = sampleCode.substring(0, 5);
    if (cachedProjects.containsKey(projectCode)) {
      Project project = cachedProjects.get(projectCode);
      return project.stream()
          .filter(it -> it.code().equals(sampleCode))
          .map(Sample::status)
          .findAny();
    }
    return Optional.empty();
  }

  private Optional<Project> askServiceForProject(String projectCode) {
    String changingProjectSuffix = String.format("/%s", projectCode);
    // uses httpclient5 https://hc.apache.org/httpcomponents-client-5.1.x/quickstart.html
    BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(new AuthScope(serviceAddress, -1),
        new UsernamePasswordCredentials(serviceUser, userPass.toCharArray()));
    try (CloseableHttpClient httpclient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build()) {
      HttpGet httpGet = new HttpGet(
          serviceAddress + projectsSuffix + changingProjectSuffix + statusSuffix);
      try (CloseableHttpResponse response = httpclient.execute(httpGet)) {
        System.out.println(response.getCode() + " " + response.getReasonPhrase());
        HttpEntity entity = response.getEntity();

        String response1 = EntityUtils.toString(entity);
        System.out.println(response1);
        //todo parse sample response
        //TODO add to cache
        EntityUtils.consume(entity);
      } catch (ParseException e) {
        throw new RuntimeException(e);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return Optional.empty();
  }

  private Optional<Sample> askServiceForSample(String sampleCode) {
    //TODO add to cache
    return Optional.empty();
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
          receivedCount,
          sampleQcPassedCount,
          sampleQcFailedCount,
          libraryPreparedCount,
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
