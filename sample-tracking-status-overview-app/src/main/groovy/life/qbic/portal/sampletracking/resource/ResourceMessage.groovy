package life.qbic.portal.sampletracking.resource

import life.qbic.portal.sampletracking.communication.Message


/**
 * <p>A message that communicates changes in a resource of type T</p>
 *
 * @param <T>  the data type stored in the resource
 * @since 1.0.0
 */
abstract class ResourceMessage<T> extends Message {
    public Optional<T> payload
    private final MessageType type

    ResourceMessage(T payload, MessageType type) {
        this.payload = Optional.of(payload)
        this.type = type ?: MessageType.NONE
    }

    enum MessageType {
        ADDED,
        REMOVED,
        NONE
    }


}
