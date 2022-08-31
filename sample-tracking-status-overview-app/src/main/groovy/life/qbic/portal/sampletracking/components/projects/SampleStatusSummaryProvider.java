package life.qbic.portal.sampletracking.components.projects;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import life.qbic.portal.sampletracking.TrackingStatusProvider;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SampleStatusSummaryProvider {
  private final ExecutorService executorService;
  private final TrackingStatusProvider trackingStatusProvider;

  private final Map<String, SampleStatusSummary> summaries = new HashMap<>();

  public SampleStatusSummaryProvider(ExecutorService executorService,
      TrackingStatusProvider trackingStatusProvider) {
    this.executorService = executorService;
    this.trackingStatusProvider = trackingStatusProvider;
  }

  public SampleStatusSummary getForProject(String projectCode) {
    if (summaries.containsKey(projectCode)) {
      return summaries.get(projectCode);
    }
    SampleStatusSummary statusSummary = new SampleStatusSummary(projectCode, executorService,
        trackingStatusProvider);
    summaries.put(projectCode, statusSummary);
    return statusSummary;
  }

}
