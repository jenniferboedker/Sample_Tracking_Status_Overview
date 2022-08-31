package life.qbic.portal.sampletracking.old.communication.notification


import life.qbic.portal.sampletracking.old.communication.Channel
import life.qbic.portal.sampletracking.old.communication.Topic

/**
 * <p>A service that handles notifications</p>
 *
 * @since 1.0.0
 */
class MessageBroker extends NotificationService {

    private final Channel<String> errorChannel
    private final Channel<String> successChannel
    private final Channel<String> infoChannel

    MessageBroker() {
        super()
        this.errorChannel = new Channel<>()
        this.successChannel = new Channel<>()
        this.infoChannel = new Channel<>()
        addTopic(Topic.NOTIFICATION_FAILURE)
        addTopic(Topic.NOTIFICATION_SUCCESS)
        addTopic(Topic.NOTIFICATION_INFO)
    }

    @Override
    publishFailure(String message) {
        publish(message, Topic.NOTIFICATION_FAILURE)
    }

    @Override
    publishSuccess(String message) {
        publish(message, Topic.NOTIFICATION_SUCCESS)
    }

    @Override
    publishInformation(String message) {
        publish(message, Topic.NOTIFICATION_INFO)
    }



}
