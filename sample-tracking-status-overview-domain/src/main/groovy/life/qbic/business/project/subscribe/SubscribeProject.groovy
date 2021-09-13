package life.qbic.business.project.subscribe

import life.qbic.business.DataSourceException
import life.qbic.datamodel.validation.ValidationException
import life.qbic.datamodel.validation.projectmanagement.ProjectCodeValidator

import java.util.function.Consumer

/**
 * <b>Subscribe to a project</b>
 *
 * <p>This use case allows the subscription of a project in the context of sample tracking.</p>
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
            InputValidator.validate(subscriber, projectCode)
        } catch (ValidationException validationException) {
            throw new IllegalArgumentException(validationException.getMessage())
        }
        try {
            dataSource.subscribeToProject(subscriber, projectCode)
            output.subscriptionAdded(projectCode)
        } catch (DataSourceException dataSourceException) {
            output.subscriptionFailed(subscriber, projectCode)
        } catch (Exception e) {
            throw new RuntimeException("Could not subscribe ${subscriber.firstName} ${subscriber.lastName} (${subscriber.email}) to ${projectCode}.")
        }
    }

    private static class InputValidator {
        static void validate(Subscriber subscriber, String projectCode) throws ValidationException {
            validateSubscriber.accept(subscriber)
            projectCodeValidator.accept(projectCode)
        }
        private static Consumer<Subscriber> validateFirstName = {
            Subscriber subscriber ->
                if (!subscriber.firstName) throw new ValidationException(
                        "Please provide a first name.")
        }
        private static Consumer<Subscriber> validateLastName = {
            Subscriber subscriber ->
                if (!subscriber.lastName) throw new ValidationException(
                        "Please provide a first name.")
        }
        private static Consumer<Subscriber> validateEmail = {
            Subscriber subscriber ->
                if (!subscriber.email) throw new ValidationException("Please provide a first name.")
        }
        private static Consumer<Subscriber> validateSubscriber = validateFirstName.andThen(
                validateLastName).andThen(validateEmail)
        private static Consumer<String> projectCodeValidator = new ProjectCodeValidator()
    }
}

