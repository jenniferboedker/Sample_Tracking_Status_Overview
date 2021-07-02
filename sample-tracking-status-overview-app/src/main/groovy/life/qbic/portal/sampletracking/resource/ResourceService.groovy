package life.qbic.portal.sampletracking.resource

import life.qbic.portal.sampletracking.communication.Channel
import life.qbic.portal.sampletracking.communication.Subscriber
import life.qbic.portal.sampletracking.communication.Topic

/**
 * <b>A service that holds a resource of type T and communicates changes to its content</b>
 *
 * <p>This service acts as a channel where publishers can publish changes to data
 * and subscribers are notified of changes. The data that is published is stored in the service.</p>
 *
 * @param <T>  the type of data to be communicated using this service
 * @since 1.0.0
 */
abstract class ResourceService<T> {
    protected final List<? extends T> content
    protected final Map<Topic, Channel<T>> channels

    /**
     * Creates a resource service with an empty content.
     * @since 1.0.0
     */
    ResourceService() {
        this.content = new ArrayList<>() //generics inferred from field
        channels = new HashMap<>()
    }

    /**
     * Subscribes to service update events. Update events are emitted by the service when new
     * resource items are added, removed or the resource has refreshed.
     *
     * @param subscriber The subscriber to register for update events
     * @param topic the topic to subscribe to
     * @since 1.0.0
     */
    void subscribe(Subscriber<T> subscriber, Topic topic) {
        if( channels.containsKey(topic)) {
            channels.get(topic).subscribe(subscriber)
        } else {
            throw new IllegalArgumentException("Topic $topic cannot be subscribed to.")
        }
    }

    /**
     * Unsubscribe from a topic. If no topic is found this does nothing
     *
     * @param subscription The subscription to remove
     * @param topic the topic to unsubscribe from
     * @since 1.0.0
     */
    void unsubscribe(Subscriber subscriber, Topic topic) {
        channels.get(topic)?.unsubscribe(subscriber)
    }

    /**
     * Publishes specific data to a given topic. If the topic is unknown this does nothing.
     * @param data the data to be published
     * @param topic the topic to publish to
     */
    protected void publish(T data, Topic topic) {
        channels.get(topic)?.publish(data)
    }

    /**
     * Adds a new topic to the possible topics
     * @param topic the topic to be added
     */
    protected void addTopic(Topic topic) {
        channels.put(topic, new Channel<T>())
    }

    /**
     * Adds a resource item to a resource of the service.
     *
     * @param resourceItem the resource item to add
     * @since 1.0.0
     */
    abstract void addToResource(T resourceItem)

    /**
     * Removes a resource item from the resource of the service.
     *
     * @param resourceItem the resource item to remove
     * @since 1.0.0
     */
    abstract void removeFromResource(T resourceItem)

    /**
     * Returns an iterator that provides access to all resource items of the service.
     *
     * @return An iterator over the content of the resource
     * @since 1.0.0
     */
    Iterator<? extends T> iterator() {
        return new ArrayList<>(this.content).iterator()
    }

    /**
     * Clears the content of the resource service
     *
     * @since 1.0.0
     */
    void clear() {
        // we have to copy otherwise there is a concurrent modification exception
        List<? extends T> toRemove = new ArrayList<>(content)
        toRemove.forEach { removeFromResource(it) }
    }

    /**
     * This method clears the content of the resource service and populates it with a new set of data.
     *
     * @see #clear
     * @since 1.0.0
     */
    void items(List<? extends T> items) {
        clear()
        for (def item in items) {
            addToResource(item)
        }
    }
}
