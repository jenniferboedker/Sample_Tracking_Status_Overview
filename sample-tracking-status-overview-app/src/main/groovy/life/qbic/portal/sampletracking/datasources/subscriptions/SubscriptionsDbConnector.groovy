package life.qbic.portal.sampletracking.datasources.subscriptions

import groovy.util.logging.Log4j2

import life.qbic.business.project.subscribe.Subscriber
import life.qbic.business.project.subscribe.SubscriptionDataSource

import life.qbic.business.DataSourceException
import life.qbic.portal.sampletracking.datasources.database.ConnectionProvider

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet

/**
 * <b>Provides methods to handle user's subscriptions to projects</b>
 *
 * <p>Connects to the subscription database in order to store or fetch user's subsriptions to project. Uses Subscriber objects and the project code</p>
 *
 * @since 1.0.0
 */
class SubscriptionsDbConnector implements SubscriptionDataSource {
  
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
    void subscribeToProject(Subscriber subscriber, String projectCode) {
          try {
            Connection connection = connectionProvider.connect()
            connection.setAutoCommit(false)
      
            connection.withCloseable { it ->
              try {
                int subscriberId = getSubscriberId(it, subscriber)
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
    
    private int getSubscriberId(Connection connection, Subscriber subscriber) {
          subscriberId = fetchExistingSubscriberId(subscriber)
          if(subscriberId <= 0) {
            String query = "INSERT INTO subscriber (first_name, last_name, email) VALUES(?, ?, ?)"
            
            def statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)
            
            statement.setString(1, subscriber.firstName)
            statement.setString(2, subscriber.lastName)
            statement.setString(3, subscriber.eMail)
            statement.execute()
            def keys = statement.getGeneratedKeys()
            while (keys.next()) {
              subscriberId = keys.getInt(1)
            }
          }
          return subscriberId
    }
    
    private void addSubscription(Connection connection, int subscriberId, String projectCode) {
          String query = "INSERT INTO subscription (project_code, subscriber_id) VALUES(?, ?)"
            
          def statement = connection.prepareStatement(query)
            
          statement.setInt(1, subscriberId)
          statement.setString(2, projectCode)
          statement.execute()
    }

    private int fetchExistingSubscriberId(Subscriber subscriber) {
          String query = "SELECT id FROM subscriber WHERE first_name = ? AND last_name = ? AND email = ?"
          Connection connection = connectionProvider.connect()

          int personId = -1

          connection.withCloseable {
            def statement = connection.prepareStatement(query)
            statement.setString(1, person.firstName)
            statement.setString(2, person.lastName)
            statement.setString(3, person.emailAddress)

            ResultSet result = statement.executeQuery()
            while (result.next()) {
              personId = result.getInt(1)
            }
          }
          return personId
    }
}
