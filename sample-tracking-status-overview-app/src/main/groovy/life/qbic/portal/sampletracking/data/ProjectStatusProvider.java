package life.qbic.portal.sampletracking.data;

import java.util.Optional;
import life.qbic.portal.sampletracking.view.projects.viewmodel.ProjectStatus;

/**
 * Provides project statuses for projects
 *
 * @since 1.1.4
 */
public interface ProjectStatusProvider {

  /**
   * Provide the project status for a project
   * @param projectCode the project code
   * @return the project status if exists, Optional::empty otherwise.
   */
  Optional<ProjectStatus> getForProject(String projectCode);

}
