package life.qbic.portal.sampletracking.components.projects.viewmodel;

import java.util.Objects;

public class Project {

  private final String code;
  private final String title;
  private SubscriptionStatus subscriptionStatus;
  private ProjectStatus projectStatus;


  public Project(String code, String title) {
    this.code = code;
    this.title = title;
  }

  public String code() {
    return code;
  }

  public String title() {
    return title;
  }


  public SubscriptionStatus subscriptionStatus() {
    return subscriptionStatus;
  }

  public Project setSubscriptionStatus(
      SubscriptionStatus subscriptionStatus) {
    this.subscriptionStatus = subscriptionStatus;
    return this;
  }

  public ProjectStatus projectStatus() {
    return projectStatus;
  }

  public Project setProjectStatus(ProjectStatus projectStatus) {
    this.projectStatus = projectStatus;
    return this;
  }


  public boolean hasAvailableData() {
    if (Objects.isNull(projectStatus)) {
      return false;
    }
    return projectStatus.countDataAvailable() > 0;
  }

  public static class SubscriptionStatus {

    private final boolean subscribed;

    public SubscriptionStatus(boolean subscribed) {
      this.subscribed = subscribed;
    }

    public boolean isSubscribed() {
      return subscribed;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      SubscriptionStatus that = (SubscriptionStatus) o;

      return isSubscribed() == that.isSubscribed();
    }

    @Override
    public int hashCode() {
      return (isSubscribed() ? 1 : 0);
    }
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
    if (!title.equals(project.title)) {
      return false;
    }
    if (!Objects.equals(subscriptionStatus, project.subscriptionStatus)) {
      return false;
    }
    return Objects.equals(projectStatus, project.projectStatus);
  }

  @Override
  public int hashCode() {
    int result = code.hashCode();
    result = 31 * result + title.hashCode();
    result = 31 * result + (subscriptionStatus != null ? subscriptionStatus.hashCode() : 0);
    result = 31 * result + (projectStatus != null ? projectStatus.hashCode() : 0);
    return result;
  }
}
