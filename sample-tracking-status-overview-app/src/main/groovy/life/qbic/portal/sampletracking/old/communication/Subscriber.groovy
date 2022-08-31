package life.qbic.portal.sampletracking.old.communication

/**
 * The subscription interface serves as a functional interface
 * to receive communication of type TOPIC emitted by a Channel.
 *
 * @param <T> the type of data this subscriber can receive
 * @since 1.0.0
 */
interface Subscriber<T> {
    /**
     * This method is called when a new message was published to the channel.
     * @param data the data that is received
     */
    void receive(T data)
}
