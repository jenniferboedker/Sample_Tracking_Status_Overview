package life.qbic.portal.sampletracking;

import life.qbic.portal.sampletracking.components.projects.viewmodel.Project.SubscriptionStatus;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public interface SubscriptionStatusProvider {

  SubscriptionStatus getForProject(String projectCode);

}
