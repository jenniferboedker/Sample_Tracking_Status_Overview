package life.qbic.portal.sampletracking.old.communication

/**
 * <b>A service that brokers communication messages</b>
 *
 * <p>This service acts as a broker of channels where publishers can publish changes to.
 * Subscribers to a certain topic can be informed with messages of type T.</p>
 *
 * @param <T>  the type of message to be communicated using this service
 * @since 1.0.0
 */
abstract class Service<T> {

    protected final Map<Topic, Channel<T>> channels

    Service() {
        this.channels = new HashMap<>()
    }

    /**
     * Subscribes to messages with a given topic.
     * If the topic is unknown this method throws an IllegalArgumentException.
     *
     * @param subscriber The subscriber to register for update events
     * @param topic the topic to subscribe to
     * @since 1.0.0
     */
    void subscribe(Subscriber<T> subscriber, Topic topic) {
        if (channels.containsKey(topic)) {
            channels.get(topic).subscribe(subscriber)
        } else {
            throw new IllegalArgumentException("Topic $topic cannot be subscribed to.")
        }
    }

    /**
     * Unsubscribe from a topic.
     * If the topic is unknown this method throws an IllegalArgumentException.
     *
     * @param subscription The subscription to remove
     * @param topic the topic to unsubscribe from
     * @throws IllegalArgumentException in case the topic is unknown by the service
     * @since 1.0.0
     */
    void unsubscribe(Subscriber subscriber, Topic topic) {
        if (channels.containsKey(topic)) {
            channels.get(topic).unsubscribe(subscriber)
        } else {
            throw new IllegalArgumentException("Topic $topic cannot be unsubscribed from.")
        }
    }

    /**
     * Publishes a specific message to a given topic. If the topic is unknown this method throws an IllegalArgumentException.
     * @param data the data to be published
     * @param topic the topic to publish to
     * @throws IllegalArgumentException in case the topic is unknown by the service
     */
    protected void publish(T data, Topic topic) throws IllegalArgumentException{
        if (channels.containsKey(topic)) {
            channels.get(topic).publish(data)
        } else {
            throw new IllegalArgumentException("Topic $topic cannot be published to.")
        }
    }

    /**
     * Adds a new topic to the possible topics
     * @param topic the topic to be added
     */
    protected void addTopic(Topic topic) {
        channels.put(topic, new Channel<T>())
    }
}
