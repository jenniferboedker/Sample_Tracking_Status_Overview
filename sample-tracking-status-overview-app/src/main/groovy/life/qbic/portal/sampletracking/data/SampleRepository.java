package life.qbic.portal.sampletracking.data;

import java.util.List;
import life.qbic.portal.sampletracking.view.samples.viewmodel.Sample;

public interface SampleRepository {

  /**
   * Retrieves samples for a project code
   * @param projectCode the project code
   * @return a list of samples for the project.
   */
  List<Sample> findAllSamplesForProject(String projectCode);
}
