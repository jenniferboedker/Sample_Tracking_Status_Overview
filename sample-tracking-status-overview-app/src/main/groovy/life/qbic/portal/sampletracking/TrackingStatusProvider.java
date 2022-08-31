package life.qbic.portal.sampletracking;

import life.qbic.portal.sampletracking.components.projects.viewmodel.ProjectStatus;
import life.qbic.portal.sampletracking.components.samples.viewmodel.SampleStatus;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public interface TrackingStatusProvider {

  ProjectStatus getForProject(String projectCode);

  SampleStatus getForSample(String sampleCode);

}
