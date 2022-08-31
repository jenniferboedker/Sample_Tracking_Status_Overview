package life.qbic.portal.sampletracking.data;

import java.util.Optional;
import life.qbic.portal.sampletracking.view.samples.viewmodel.SampleStatus;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public interface SampleStatusProvider {

  SampleStatus getForSample(String sampleCode);

  Optional<SampleStatus> getCachedStatusForSample(String sampleCode);

}
