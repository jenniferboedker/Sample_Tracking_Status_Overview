package life.qbic.portal.sampletracking.data;

import java.util.Optional;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public interface SampleStatusProvider {

  Optional<String> getForSample(String sampleCode);
}
