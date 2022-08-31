package life.qbic.portal.sampletracking.old.communication.notification


import life.qbic.portal.sampletracking.old.communication.Service

/**
 * <p>A service that handles notifications</p>
 *
 * @since 1.0.0
 */
abstract class NotificationService extends Service<String> {

    /**
     * This method publishes a failure message
     * @param message the message to be sent
     * @since 1.0.0
     */
    abstract publishFailure(String message)

    /**
     * This method publishes a success message
     * @param message the message to be sent
     * @since 1.0.0
     */
    abstract publishSuccess(String message)

    /**
     * This method publishes information
     * @param message the message to be sent
     * @since 1.0.0
     */
    abstract publishInformation(String message)

}
