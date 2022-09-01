package life.qbic.portal.sampletracking.view.projects.viewmodel;

import java.time.Instant;

public class Project {

  private final String code;
  private final String title;

  private ProjectStatus projectStatus;
  private boolean subscribed;

  public Project(String code, String title) {
    this.code = code;
    this.title = title;
    projectStatus = new ProjectStatus(0, 0, 0, 0, 0, 0, Instant.MIN);
  }

  public String code() {
    return code;
  }

  public String title() {
    return title;
  }

  public boolean subscribed() {
    return subscribed;
  }

  public void setSubscribed(boolean subscribed) {
    this.subscribed = subscribed;
  }

  public ProjectStatus projectStatus() {
    return projectStatus;
  }

  public void setProjectStatus(
      ProjectStatus projectStatus) {
    this.projectStatus = projectStatus;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Project project = (Project) o;

    if (!code.equals(project.code)) {
      return false;
    }
    return title.equals(project.title);
  }

  @Override
  public int hashCode() {
    int result = code.hashCode();
    result = 31 * result + title.hashCode();
    return result;
  }
}
