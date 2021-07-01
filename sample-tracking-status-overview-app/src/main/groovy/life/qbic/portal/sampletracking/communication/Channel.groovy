package life.qbic.portal.sampletracking.communication
/**
 * A channel used to publish to a chosen topic and informs subscribers about publications
 *
 * @param <T>  the type of message this channel accepts
 * @since 1.0.0
 */
abstract class Channel<T extends Message> {

    protected final Collection<Subscriber<T>> subscribers

    Channel() {
        this.subscribers = new LinkedList<>()
    }

    /**
     * Subscribes to service update events. Update events are emitted by the service when new
     * resource items are added, removed or the resource has refreshed.
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
    protected void publish(T message) {
        for (Subscriber subscriber : subscribers) {
            subscriber.receive(message)
        }
    }
}
