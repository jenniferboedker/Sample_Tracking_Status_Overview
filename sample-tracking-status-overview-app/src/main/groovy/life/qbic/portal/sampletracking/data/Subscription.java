package life.qbic.portal.sampletracking.data;

import java.util.Objects;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
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
