package life.qbic.business.project.subscribe

import life.qbic.datamodel.identifiers.SampleCodeFunctions

import java.util.function.Consumer

/**
  * <b><short description></b>
  *
  * <p><detailed description></p>
  *
  * @since <version tag>
  */
class SubscribeProject implements SubscribeProjectInput {
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
    }

    private static class InputValidator {
        static void validate(String firstName, String lastName, String email, String projectCode) {
            Subscriber subscriber = new Subscriber(firstName, lastName, email)
            validateFirstName.andThen(validateLastName).andThen(validateEmail).accept(subscriber)
            validateProjectCode.accept(projectCode)
        }
        private static Consumer<Subscriber> validateFirstName = { Subscriber subscriber ->
            if (! subscriber.firstName) throw new ValidationException("Please provide a first name.")
        }
        private static Consumer<Subscriber> validateLastName = { Subscriber subscriber ->
            if (! subscriber.lastName) throw new ValidationException("Please provide a first name.")
        }
        private static Consumer<Subscriber> validateEmail = { Subscriber subscriber ->
            if (! subscriber.eMail) throw new ValidationException("Please provide a first name.")
        }
        private static Consumer<String> validateProjectCode = { String projectCode ->
            ValidationException validationException = new ValidationException("Please provide a valid QBiC barcode.")
            if (! projectCode) {
                throw validationException
            }
            if (! SampleCodeFunctions.isQbicBarcode(projectCode)) {
                throw validationException
            }
        }
    }

    private static class ValidationException extends RuntimeException {
        ValidationException(String message) {
            super(message)
        }
    }
}
