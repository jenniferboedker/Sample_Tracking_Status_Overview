package life.qbic.portal.sampletracking.data;

import java.util.Optional;
import life.qbic.portal.sampletracking.view.projects.viewmodel.ProjectStatus;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public interface ProjectStatusProvider {
  ProjectStatus getForProject(String projectCode);

  Optional<ProjectStatus> getCachedStatusForProject(String projectCode);


}
