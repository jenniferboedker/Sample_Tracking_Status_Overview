package life.qbic.portal.sampletracking.view.projects;

import java.util.Objects;
import life.qbic.portal.sampletracking.view.GridFilter;
import life.qbic.portal.sampletracking.view.projects.viewmodel.Project;

/**
 * A filter for projects. This filter allows to filter for projects containing a text.
 *
 * @since 1.1.4
 */
public class ProjectFilter implements GridFilter<Project> {

  private String containedText = "";

  public ProjectFilter containingText(String substring) {
    this.containedText = substring;
    return this;
  }


  @Override
  public boolean test(Project project) {
    if (Objects.isNull(project)) {
      return false;
    }
    return containsText(project.code())
        || containsText(project.title());
  }

  private boolean containsText(String value) {
    if (Objects.isNull(value)) {
      return false;
    }
    return value.toLowerCase().contains(containedText.toLowerCase());
  }


}
