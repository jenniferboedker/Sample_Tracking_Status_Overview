package life.qbic.portal.sampletracking.components.projects;

import java.util.Objects;
import life.qbic.portal.sampletracking.components.GridFilter;
import life.qbic.portal.sampletracking.components.projects.viewmodel.Project;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
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

  @Override
  public void clear() {
    containedText = "";
  }


}
