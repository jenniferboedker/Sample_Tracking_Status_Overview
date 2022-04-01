package life.qbic.portal.sampletracking.datasources.subscriptions

import groovy.util.logging.Log4j2
import life.qbic.business.DataSourceException
import life.qbic.business.project.load.SubscribedProjectsDataSource
import life.qbic.business.project.subscribe.Subscriber
import life.qbic.business.project.subscribe.SubscriptionDataSource
import life.qbic.portal.sampletracking.datasources.database.ConnectionProvider

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * <b>Provides methods to handle user's subscriptions to projects</b>
 *
 * <p>Connects to the subscription database in order to store or fetch user's subscriptions to project. Uses Subscriber objects and the project code</p>
 *
 * @since 1.0.0
 */
@Log4j2
class SubscriptionsDbConnector implements SubscriptionDataSource, SubscribedProjectsDataSource {
  
  private final ConnectionProvider connectionProvider
  
      /**
       * Creates a database connector that will connect to the database using the provided connection
       * provider.
       * @param connectionProvider the connection provider providing the connections that are used to
       * connect to the database
       * @since 1.0.0
       */
      SubscriptionsDbConnector(ConnectionProvider connectionProvider) {
          this.connectionProvider = connectionProvider
      }

    /**
     * Creates the subscriber in the data source, if they don't exist, and subscribes them to the project.
     * @param subscriber
     * @param projectCode
     * @since 1.1.0
     */
    @Override
    void subscribeToProject(Subscriber subscriber, String projectCode) throws DataSourceException{
          try {
            Connection connection = connectionProvider.connect()
            connection.setAutoCommit(false)
      
            connection.withCloseable { it ->
              try {
                int subscriberId = getSubscriberId(subscriber).orElseThrow({new DataSourceException("Could not find user in the database")})
                addSubscription(it, subscriberId, projectCode)
                connection.commit()
              } catch (Exception e) {
                log.error(e.message)
                log.error(e.stackTrace.join("\n"))
                connection.rollback()
                throw new DataSourceException("Subscription to {$projectCode} could not be created: {$subscriber}")
              } finally {
                connection.close()
              }
            }
          } catch (Exception e) {
            log.error(e)
            log.error(e.stackTrace.join("\n"))
            throw new DataSourceException("Subscription to {$projectCode} could not be created: {$subscriber}")
          }
    }
    
    /**
     * Unsubscribes a subscriber from a project, if they were subscribed to it
     * @param subscriber
     * @param projectCode
     * @since 1.0.0
     */
    @Override
    void unsubscribeFromProject(Subscriber subscriber, String projectCode) {
          try {
            Connection connection = connectionProvider.connect()
            connection.setAutoCommit(false)
      
            connection.withCloseable { it ->
              try {
                int subscriberId = getSubscriberId(subscriber).orElseThrow({new DataSourceException("Could not find user in the database")})
                // action must only be taken if this subscriber exists
                if(subscriberId > 0) {
                  removeSubscription(it, subscriberId, projectCode)
                  connection.commit()
                }
              } catch (Exception e) {
                log.error(e.message)
                log.error(e.stackTrace.join("\n"))
                connection.rollback()
                throw new DataSourceException("Could not remove subscription from {$projectCode} for: {$subscriber}")
              } finally {
                connection.close()
              }
            }
          } catch (Exception e) {
            log.error(e)
            log.error(e.stackTrace.join("\n"))
            throw new DataSourceException("Could not remove subscription from {$projectCode} for: {$subscriber}")
          }
    }
    
    private Optional<Integer> getSubscriberId(Subscriber subscriber) {
        String query = "SELECT id FROM person WHERE user_id = ? AND active = 1"

        Optional<Integer> personId = Optional.empty()
        Connection connection = connectionProvider.connect()

        connection.withCloseable {
            def statement = connection.prepareStatement(query)
            statement.setString(1, subscriber.email)

            ResultSet result = statement.executeQuery()
            while (result.next()) {
                personId = Optional.of(result.getInt(1))
            }
        }
        return personId
    }
    
    private void addSubscription(Connection connection, int subscriberId, String projectCode) {
        if (!projectAlreadySubscribed(subscriberId, projectCode)) {
            String query = "INSERT INTO subscriptions (project_code, person_id) VALUES(?, ?)"

            def statement = connection.prepareStatement(query)

            statement.setString(1, projectCode)
            statement.setInt(2, subscriberId)
            statement.execute()
        }
    }
    
    private static void removeSubscription(Connection connection, int subscriberId, String projectCode) {
          // we do not need to check if the subscription exists, here, as removing a non-existent row does not lead to errors
          String query = "DELETE FROM subscriptions WHERE project_code = ? AND person_id = ?"

          def statement = connection.prepareStatement(query)

          statement.setString(1, projectCode)
          statement.setInt(2, subscriberId)
          statement.execute()
    }

    private boolean projectAlreadySubscribed(int subscriberId, String projectCode) {
        String query = "SELECT id FROM subscriptions WHERE project_code = ? AND person_id = ? "
        Connection connection = connectionProvider.connect()
        boolean isAlreadySubscribed = false
        connection.withCloseable {
            def statement = connection.prepareStatement(query)
            statement.setString(1, projectCode)
            statement.setInt(2, subscriberId)
            ResultSet resultSet = statement.executeQuery()
            if (resultSet.next()) {
                isAlreadySubscribed = true
            }
        }
        return isAlreadySubscribed
    }

    @Override
    List<String> findSubscribedProjectCodesFor(Subscriber subscriber) {
        List<String> subscribedProjects = []

        String query = "SELECT project_code FROM subscriptions WHERE person_id = ?"
        Connection connection = connectionProvider.connect()

        connection.withCloseable {
            int subscriberId = getSubscriberId(subscriber).orElseThrow({new DataSourceException("Could not find user in the database")})

            PreparedStatement statement = connection.prepareStatement(query)
            statement.setInt(1, subscriberId)

            ResultSet resultSet = statement.executeQuery()
            while(resultSet.next()) {
                subscribedProjects <<  resultSet.getString("project_code")
            }
        }
        return subscribedProjects
    }
}
