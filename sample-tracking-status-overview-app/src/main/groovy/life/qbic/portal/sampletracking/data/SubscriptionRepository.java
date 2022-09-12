package life.qbic.portal.sampletracking.data;

import java.util.List;

/**
 * CRUD access to subscriptions
 *
 * @since 1.1.4
 */
public interface SubscriptionRepository {

  /**
   * Lists all subscriptions
   * @return a list of subscriptions
   */
  List<Subscription> findAll();

  /**
   * Adds a subscription
   * @param subscription the subscription
   * @return true if the subscription exists after this operation, false otherwise
   */
  boolean add(Subscription subscription);

  /**
   * Removes a subscription
   * @param subscription the subscription
   * @return true if the subscription does not exist after this operation, false otherwise
   */
  boolean remove(Subscription subscription);
}
