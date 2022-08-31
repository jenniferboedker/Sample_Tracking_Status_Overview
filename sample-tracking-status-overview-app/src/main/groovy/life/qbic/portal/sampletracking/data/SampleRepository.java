package life.qbic.portal.sampletracking.data;

import java.util.List;
import life.qbic.portal.sampletracking.view.samples.viewmodel.Sample;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public interface SampleRepository {
  List<Sample> findAllSamplesForProject(String projectCode);
}
