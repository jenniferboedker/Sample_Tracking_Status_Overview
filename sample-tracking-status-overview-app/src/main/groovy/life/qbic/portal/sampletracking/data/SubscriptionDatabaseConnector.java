package life.qbic.portal.sampletracking.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import life.qbic.business.project.subscribe.Subscriber;
import life.qbic.portal.sampletracking.data.database.ConnectionProvider;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public class SubscriptionDatabaseConnector implements SubscriptionRepository {

  private final ConnectionProvider connectionProvider;
  private final Subscriber subscriber;

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
      preparedStatement.setInt(1, getPersonId());
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

  private int getPersonId() {
    try (Connection connection = connectionProvider.connect()) {
      String query = "SELECT id FROM person WHERE user_id = ?";
      PreparedStatement preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, subscriber.getEmail());
      ResultSet resultSet = preparedStatement.executeQuery();
      if (resultSet.next()) {
        return resultSet.getInt("id");
      } else {
        throw new RuntimeException(String.format("No person found for %s.", subscriber));
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
