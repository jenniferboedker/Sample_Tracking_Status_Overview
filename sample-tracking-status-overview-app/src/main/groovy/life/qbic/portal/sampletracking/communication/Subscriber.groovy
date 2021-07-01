package life.qbic.portal.sampletracking.communication

/**
 * The subscription interface serves as a functional interface
 * to receive communication of type T emitted by a Channel.
 *
 * @since 1.0.0
 */
interface Subscriber<T> {
    /**
     * This method is called when a new message was published to the channel.
     * @param t A message of type T
     */
    void receive(T t)
}