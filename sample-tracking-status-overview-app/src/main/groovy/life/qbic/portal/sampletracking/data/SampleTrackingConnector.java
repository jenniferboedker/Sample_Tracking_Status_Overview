package life.qbic.portal.sampletracking.data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import life.qbic.portal.sampletracking.old.datasources.Credentials;
import life.qbic.portal.sampletracking.view.projects.viewmodel.ProjectStatus;

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
    //TODO add to cache
    return Optional.empty();
  }

  private Optional<Sample> askServiceForSample(String sampleCode) {
    //TODO add to cache
    return Optional.empty();
  }

  static private class ProjectSatusMapper {

    static ProjectStatus toProjectStatus(Project project) {
      return emptyStatus();
    }

    public static ProjectStatus emptyStatus() {
      return new ProjectStatus(0, 0, 0, 0, 0, 0, Instant.MIN);
    }

  }

  static private class SampleStatusMapper {

    static String toSampleStatus(Sample sample) {
      return sample.status();
    }
  }


}
