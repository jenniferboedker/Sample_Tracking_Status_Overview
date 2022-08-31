package life.qbic.portal.sampletracking;

import life.qbic.portal.sampletracking.components.projects.viewmodel.ProjectStatus;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public interface ProjectStatusProvider {
  ProjectStatus getForProject(String projectCode);


}
