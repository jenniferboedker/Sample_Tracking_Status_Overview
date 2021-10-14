package life.qbic.business.project.subscribe

/**
 * <b>Interface to access and trigger the {@link SubscribeProject} use case and add or remove a new subscriber to/from a project</b>
 *
 * @since <version tag>
 */
interface SubscribeProjectInput {

    /**
     * Subscribes a user with the provided information to a project
     * @param subscriber Subscriber that should be subscribed to a project
     * @param projectCode the project to subscribe to
     * @since 1.0.0
     */
    void subscribe(Subscriber subscriber, String projectCode)
    
    /**
     * Unsubscribes a user with the provided information from a project
     * @param subscriber Subscriber that should be unsubscribed from a project
     * @param projectCode the project to unsubscribe from
     * @since 1.0.0
     */
    void unsubscribe(Subscriber subscriber, String projectCode)
}
