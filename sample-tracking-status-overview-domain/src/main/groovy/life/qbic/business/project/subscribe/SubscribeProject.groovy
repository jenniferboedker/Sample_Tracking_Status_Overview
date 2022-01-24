package life.qbic.business.project.subscribe

import life.qbic.business.DataSourceException
import life.qbic.datamodel.validation.ValidationException
import life.qbic.datamodel.validation.projectmanagement.ProjectCodeValidator

import java.util.function.Consumer

/**
 * <b>Subscribe to a project or remove a subscription</b>
 *
 * <p>This use case allows the subscription and unsubscription to/from a project in the context of sample tracking.</p>
 *
 * @since 1.0.0
 */
class SubscribeProject implements SubscribeProjectInput {

    private final SubscriptionDataSource dataSource
    private final SubscribeProjectOutput output

    SubscribeProject(SubscriptionDataSource dataSource, SubscribeProjectOutput output) {
        this.dataSource = dataSource
        this.output = output
    }

    @Override
    void subscribe(Subscriber subscriber, String projectCode) {
        try {
            dataSource.subscribeToProject(subscriber, projectCode)
            output.subscriptionAdded(projectCode)
        } catch (DataSourceException ignored) {
            output.subscriptionFailed(subscriber, projectCode)
        } catch (Exception ignored) {
            throw new RuntimeException("Could not subscribe ${subscriber.firstName} ${subscriber.lastName} (${subscriber.email}) to ${projectCode}.")
        }
    }
    
    @Override
    void unsubscribe(Subscriber subscriber, String projectCode) {
        try {
            dataSource.unsubscribeFromProject(subscriber, projectCode)
            output.subscriptionRemoved(projectCode)
        } catch (DataSourceException dataSourceException) {
            output.unsubscriptionFailed(subscriber, projectCode)
        } catch (Exception e) {
            throw new RuntimeException("Could not unsubscribe ${subscriber.firstName} ${subscriber.lastName} (${subscriber.email}) from ${projectCode}.")
        }
    }

}

