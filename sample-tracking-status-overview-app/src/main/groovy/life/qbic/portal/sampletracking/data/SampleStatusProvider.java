package life.qbic.portal.sampletracking.data;

import java.util.Optional;

public interface SampleStatusProvider {

  /**
   * Provides the sample status of a sample
   * @param sampleCode the sample code
   * @return the sample status of the sample. If not found, Optional::empty
   */
  Optional<String> getForSample(String sampleCode);
}
