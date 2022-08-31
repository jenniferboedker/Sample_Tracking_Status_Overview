package life.qbic.portal.sampletracking;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
@FunctionalInterface
public interface SubscriptionStatusProvider {

  boolean getForProject(String projectCode);

}
