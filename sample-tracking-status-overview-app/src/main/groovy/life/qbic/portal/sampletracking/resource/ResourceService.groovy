package life.qbic.portal.sampletracking.resource


import life.qbic.portal.sampletracking.communication.Channel

/**
 * <b>A service that holds a resource of type V and communicates changes to its content</b>
 *
 * <p>This service acts as a channel where publishers can publish changes to data
 * and subscribers are notified of changes. The data that is published is stored in the service.</p>
 *
 * @param <V>  the type of data to be communicated using this service
 * @since 1.0.0
 */
abstract class ResourceService<V> extends Channel<ResourceMessage<V>> {
    protected final List<? extends V> content

    /**
     * Creates a resource service with an empty content.
     */
    ResourceService() {
        this.content = new ArrayList<>() //generics inferred from field
    }

    /**
     * Adds a resource item to a resource of the service.
     *
     * @param resourceItem the resource item to add
     * @since 1.0.0
     */
    abstract <RESOURCE extends V> void addToResource(RESOURCE resourceItem)

    /**
     * Removes a resource item from the resource of the service.
     *
     * @param resourceItem the resource item to remove
     * @since 1.0.0
     */
    abstract <RESOURCE extends V> void removeFromResource(RESOURCE resourceItem)

    /**
     * Returns an iterator that provides access to all resource items of the service.
     *
     * @return An iterator over the content of the resource
     * @since 1.0.0
     */
    Iterator<? extends V> iterator() {
        return new ArrayList<>(this.content).iterator()
    }

    /**
     * Clears the content of the resource service
     *
     * @since 1.0.0
     */
    void clear() {
        // we have to copy otherwise there is a concurrent modification exception
        List<? extends V> toRemove = new ArrayList<>(content)
        toRemove.forEach { removeFromResource(it) }
    }

    /**
     * This method clears the content of the resource service and populates it with a new set of data.
     *
     * @see #clear
     * @since 1.0.0
     */
    abstract void items(List<? extends V> items)
}
