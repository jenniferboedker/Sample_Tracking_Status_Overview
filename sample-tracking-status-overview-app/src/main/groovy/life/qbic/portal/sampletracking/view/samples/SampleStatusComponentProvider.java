package life.qbic.portal.sampletracking.view.samples;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import life.qbic.portal.sampletracking.data.SampleStatusProvider;
import life.qbic.portal.sampletracking.view.samples.viewmodel.Sample;

/**
 * Provides {@link SampleStatusComponent}s.
 *
 * @since 1.1.4
 */
public class SampleStatusComponentProvider {

  private final ExecutorService executorService;
  private final SampleStatusProvider trackingStatusProvider;

  private final Map<Sample, SampleStatusComponent> components = new HashMap<>();

  public SampleStatusComponentProvider(ExecutorService executorService,
      SampleStatusProvider trackingStatusProvider) {
    this.executorService = executorService;
    this.trackingStatusProvider = trackingStatusProvider;
  }

  public SampleStatusComponent getForSample(Sample sample) {
    if (components.containsKey(sample)) {
      return components.get(sample);
    }
    SampleStatusComponent component = new SampleStatusComponent(sample, trackingStatusProvider,
        executorService);
    components.put(sample, component);
    return component;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize();
    executorService.shutdownNow();
  }
}
