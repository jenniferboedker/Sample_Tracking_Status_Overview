package life.qbic.portal.sampletracking.communication
/**
 * A channel used to publish to that informs subscribers about publications
 *
 * @param <DATA> the type of data that can be moved over the channel
 * @since 1.0.0
 */
class Channel<DATA> {

    protected final Collection<Subscriber<DATA>> subscribers

    Channel() {
        this.subscribers = new LinkedList<>()
    }

    /**
     * Subscribes to service update events.
     *
     * @param subscriber The subscriber to register for update events
     */
    void subscribe(Subscriber<DATA> subscriber) {
        subscribers.add(subscriber)
    }

    /**
     * Unsubscribe from the service events.
     *
     * @param subscription The subscription to remove
     */
    void unsubscribe(Subscriber<DATA> subscriber) {
        subscribers.remove(subscriber)
    }

    /**
     * Sends a message to all subscribed subscribers
     *
     * @param message the message to be sent
     * @since 1.0.0
     */
    void publish(DATA message) {
        for (Subscriber<DATA> subscriber : subscribers) {
            subscriber.receive(message)
        }
    }
}
