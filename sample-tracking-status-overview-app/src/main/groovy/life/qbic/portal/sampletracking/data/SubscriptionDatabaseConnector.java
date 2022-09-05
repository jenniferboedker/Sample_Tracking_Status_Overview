package life.qbic.portal.sampletracking.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import life.qbic.business.project.subscribe.Subscriber;
import life.qbic.portal.sampletracking.data.database.ConnectionProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SubscriptionDatabaseConnector implements SubscriptionRepository{

  private final ConnectionProvider connectionProvider;
  private final Subscriber subscriber;

  private static final Logger log = LogManager.getLogger(SubscriptionDatabaseConnector.class);

  public SubscriptionDatabaseConnector(ConnectionProvider connectionProvider,
      Subscriber subscriber) {
    this.connectionProvider = connectionProvider;
    this.subscriber = subscriber;
  }

  @Override
  public List<Subscription> findAll() {
    List<Subscription> subscriptions = new ArrayList<>();
    try (Connection connection = connectionProvider.connect()) {
      String query = "SELECT project_code FROM subscriptions WHERE person_id = ?";
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      int personId = getPersonId().orElseThrow(
          () -> new RuntimeException("Could not find user in the database"));
      preparedStatement.setInt(1, personId);
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        Subscription subscription = new Subscription(resultSet.getString("project_code"));
        subscriptions.add(subscription);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return subscriptions;
  }

  @Override
  public boolean add(Subscription subscription) {
    return subscribeToProject(subscription.projectCode());
  }

  @Override
  public boolean remove(Subscription subscription) {
    return unsubscribeFromProject(subscription.projectCode());
  }

  private Optional<Integer> getPersonId() {
    try (Connection connection = connectionProvider.connect()) {
      String query = "SELECT id FROM person WHERE user_id = ?";
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, subscriber.getEmail());
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return Optional.of(resultSet.getInt("id"));
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private boolean subscribeToProject(String projectCode) {
    try (Connection connection = connectionProvider.connect()) {
      int personId = getPersonId().orElseThrow(
          () -> new RuntimeException("Could not find user in the database"));
      if (existingSubscriptionExists(connection, personId, projectCode)) {
        return false;
      }
      connection.setAutoCommit(false);
      try {
        addSubscription(connection, personId, projectCode);
        connection.commit();
        return true;
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
        connection.rollback();
        return false;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private void addSubscription(Connection connection, int personId, String projectCode)
      throws SQLException {

    PreparedStatement insertSubscription = connection.prepareStatement(
        "INSERT INTO subscriptions (project_code, person_id) VALUES(?, ?)");
    insertSubscription.setString(1, projectCode);
    insertSubscription.setInt(2, personId);
    insertSubscription.execute();
  }

  private static boolean existingSubscriptionExists(Connection connection, int personId, String projectCode)
      throws SQLException {
    PreparedStatement preparedStatement = connection.prepareStatement(
        "SELECT * FROM subscriptions WHERE project_code = ? AND person_id = ?");
    preparedStatement.setString(1, projectCode);
    preparedStatement.setInt(2, personId);
    ResultSet resultSet = preparedStatement.executeQuery();
    return resultSet.next();
  }

  private boolean unsubscribeFromProject(String projectCode) {
    try (Connection connection = connectionProvider.connect()) {
      connection.setAutoCommit(false);
      int personId = getPersonId().orElseThrow(() -> new RuntimeException("Could not find user in the database"));
      if (!existingSubscriptionExists(connection, personId, projectCode)) {
        return true;
      }
      try {
        removeSubscription(connection, personId, projectCode);
        connection.commit();
        return true;
      } catch (SQLException e) {
        log.error(e.getMessage(), e);
        connection.rollback();
        return false;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private static void removeSubscription(Connection connection, int subscriberId, String projectCode)
      throws SQLException {
    PreparedStatement preparedStatement = connection.prepareStatement(
        "DELETE FROM subscriptions WHERE project_code = ? AND person_id = ?");
    preparedStatement.setString(1, projectCode);
    preparedStatement.setInt(2, subscriberId);
    preparedStatement.execute();
  }
}
