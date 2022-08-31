package life.qbic.portal.sampletracking.components.projects;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import life.qbic.portal.sampletracking.ProjectStatusProvider;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class ProjectStatusComponentProvider {
  private final ExecutorService executorService;
  private final ProjectStatusProvider trackingStatusProvider;

  private final Map<String, ProjectStatusComponent> summaries = new HashMap<>();

  public ProjectStatusComponentProvider(ExecutorService executorService,
      ProjectStatusProvider trackingStatusProvider) {
    this.executorService = executorService;
    this.trackingStatusProvider = trackingStatusProvider;
  }

  public ProjectStatusComponent getForProject(String projectCode) {
    if (summaries.containsKey(projectCode)) {
      return summaries.get(projectCode);
    }
    ProjectStatusComponent statusSummary = new ProjectStatusComponent(projectCode, executorService,
        trackingStatusProvider);
    summaries.put(projectCode, statusSummary);
    return statusSummary;
  }

}
