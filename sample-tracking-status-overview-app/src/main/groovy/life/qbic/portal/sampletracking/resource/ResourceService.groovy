package life.qbic.portal.sampletracking.resource

import life.qbic.portal.sampletracking.communication.Channel
import life.qbic.portal.sampletracking.communication.Service
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
abstract class ResourceService<T> extends Service<T> {
    protected final List<? extends T> content

    /**
     * Creates a resource service with an empty content.
     * @since 1.0.0
     */
    ResourceService() {
        super()
        this.content = new ArrayList<>() //generics inferred from field
    }

    /**
     * Adds a resource item to a resource of the service.
     * This method is expected to publish the changed item in an appropriate channel
     * <p> Assuming you have one general topic for all messages, then you would publish to the matching channel</p>
     * <p> Assuming you have a dedicated topic for adding items, then you would publish to the matching channel.</p>
     * <p><b>After execution, the resource item must be contained in the resource content.</b></p>
     * @param resourceItem the resource item to add
     * @since 1.0.0
     */
    abstract void addToResource(T resourceItem)

    /**
     * Removes a resource item from the resource of the service.
     * This method is expected to publish the changed item in an appropriate channel
     * <p> Assuming you have one general topic for all messages, then you would publish to the matching channel</p>
     * <p> Assuming you have a dedicated topic for removing items, then you would publish to the matching channel.</p>
     * <p><b>After execution, the resource item must not be contained in the resource content.</b></p>
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
}
