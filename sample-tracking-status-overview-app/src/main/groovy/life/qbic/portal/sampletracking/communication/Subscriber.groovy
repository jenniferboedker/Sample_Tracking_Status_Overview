package life.qbic.portal.sampletracking.communication

/**
 * The subscription interface serves as a functional interface
 * to receive communication of type TOPIC emitted by a Channel.
 *
 * @param <DATA> the type of data this subscriber can recieve
 * @since 1.0.0
 */
interface Subscriber<DATA> {
    /**
     * This method is called when a new message was published to the channel.
     * @param data the data that is received
     */
    void receive(DATA data)
}