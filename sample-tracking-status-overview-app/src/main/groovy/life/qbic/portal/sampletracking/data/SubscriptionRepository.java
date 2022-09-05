package life.qbic.portal.sampletracking.data;

import java.util.List;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
@FunctionalInterface
public interface SubscriptionRepository {

  List<Subscription> findAll();


}
