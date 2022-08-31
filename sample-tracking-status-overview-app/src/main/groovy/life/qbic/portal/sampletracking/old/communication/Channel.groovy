package life.qbic.portal.sampletracking.old.communication
/**
 * A channel used to publish to that informs subscribers about publications
 *
 * @param <T> the type of data that can be moved over the channel
 * @since 1.0.0
 */
class Channel<T> {

    protected final Collection<Subscriber<T>> subscribers

    Channel() {
        this.subscribers = new LinkedList<>()
    }

    /**
     * Subscribes to service update events.
     *
     * @param subscriber The subscriber to register for update events
     */
    void subscribe(Subscriber<T> subscriber) {
        subscribers.add(subscriber)
    }

    /**
     * Unsubscribe from the service events.
     *
     * @param subscription The subscription to remove
     */
    void unsubscribe(Subscriber<T> subscriber) {
        subscribers.remove(subscriber)
    }

    /**
     * Sends a message to all subscribed subscribers
     *
     * @param message the message to be sent
     * @since 1.0.0
     */
    void publish(T message) {
        for (Subscriber<T> subscriber : subscribers) {
            subscriber.receive(message)
        }
    }
}
