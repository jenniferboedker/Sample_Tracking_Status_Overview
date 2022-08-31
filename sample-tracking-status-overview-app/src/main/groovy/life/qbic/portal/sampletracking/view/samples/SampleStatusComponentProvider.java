package life.qbic.portal.sampletracking.view.samples;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import life.qbic.portal.sampletracking.data.SampleStatusProvider;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SampleStatusComponentProvider {

  private final ExecutorService executorService;
  private final SampleStatusProvider trackingStatusProvider;

  private Map<String, SampleStatusComponent> components = new HashMap<>();

  public SampleStatusComponentProvider(ExecutorService executorService,
      SampleStatusProvider trackingStatusProvider) {
    this.executorService = executorService;
    this.trackingStatusProvider = trackingStatusProvider;
  }

  public SampleStatusComponent getForSample(String sampleCode) {
    if (components.containsKey(sampleCode)) {
      return components.get(sampleCode);
    }
    SampleStatusComponent component = new SampleStatusComponent(sampleCode, trackingStatusProvider);
    components.put(sampleCode, component);
    return component;
  }
}
