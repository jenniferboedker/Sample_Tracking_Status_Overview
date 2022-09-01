package life.qbic.portal.sampletracking.data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import life.qbic.portal.sampletracking.view.projects.viewmodel.ProjectStatus;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SampleTracking implements ProjectStatusProvider, SampleStatusProvider {

  private static class Project extends ArrayList<Sample> {
  }

  private static class Sample {

    String status;
    String code;
    Instant statusValidSince;

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

  private final Map<String, Project> cache = new HashMap<>();

  @Override

  public ProjectStatus getForProject(String projectCode) {
    return getCachedStatusForProject(projectCode).orElse(askService());
  }

  private ProjectStatus emptyStatus() {
    return new ProjectStatus(0, 0, 0, 0, 0, 0, Instant.MIN);
  }

  private ProjectStatus askService() {
    return null;
  }

  private Optional<ProjectStatus> getCachedStatusForProject(String projectCode) {
    if (cache.containsKey(projectCode)) {
      return Optional.empty();//fixme
    }
    return Optional.empty();
  }

  @Override
  public String getForSample(String sampleCode) {
    if (cache.containsKey(sampleCode.substring(0, 5))) {
      Project project = cache.get(sampleCode.substring(0, 5));
      project.stream().filter(it -> it.code().equals(sampleCode)).findAny();
    }

    return askServiceForSample();
  }

  private String askServiceForSample() {
    return null;
  }
}
