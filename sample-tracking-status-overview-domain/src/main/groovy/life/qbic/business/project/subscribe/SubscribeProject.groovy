package life.qbic.business.project.subscribe

import life.qbic.business.DataSourceException
import life.qbic.datamodel.validation.ValidationException
import life.qbic.datamodel.validation.projectmanagement.ProjectCodeValidator

import java.util.function.Consumer

/**
 * <b>Subscribe to a project</b>
 *
 * <p>This use case allows the subscribtion of a project in the context of sample tracking.</p>
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

    /**
     * Subscribes a user with the authentication id to a specific project
     * @param firstName the first name of the subscriber
     * @param lastName the last name of the subscriber
     * @param email the email address of the subscriber
     * @param projectCode the project to subscribe to
     * @since 1.0.0
     */
    @Override
    void subscribe(String firstName, String lastName, String email, String projectCode) {
        try {
            InputValidator.validate(firstName, lastName, email, projectCode)
        } catch (ValidationException validationException) {
            throw new IllegalArgumentException(validationException.getMessage())
        }
        Subscriber subscriber = new Subscriber(firstName, lastName, email)
        try {
            dataSource.subscribeToProject(subscriber, projectCode)
            output.subscriptionAdded(projectCode)
        } catch (DataSourceException dataSourceException) {
            output.subscriptionFailed(subscriber.firstName,
                    subscriber.lastName,
                    subscriber.email,
                    projectCode)
        } catch (Exception e) {
            throw new RuntimeException("Could not subscribe ${subscriber.firstName} ${subscriber.lastName} (${subscriber.email}) to ${projectCode}.")
        }
    }

    private static class InputValidator {
        static void validate(String firstName, String lastName, String email, String projectCode) throws ValidationException {
            Subscriber subscriber = new Subscriber(firstName, lastName, email)
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

