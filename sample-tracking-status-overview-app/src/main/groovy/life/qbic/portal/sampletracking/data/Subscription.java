package life.qbic.portal.sampletracking.data;

import java.util.Objects;

/**
 * A subscription ot a project. Contains information associated to the subscription.
 *
 * @since 1.1.4
 */
public class Subscription {
  private final String projectCode;

  public Subscription(String projectCode) {
    Objects.requireNonNull(projectCode);
    this.projectCode = projectCode;
  }

  public String projectCode() {
    return projectCode;
  }

}
