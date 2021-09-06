package life.qbic.business.project.subscribe

/**
 * <b>Interface to access and trigger the {@link SubscribeProject} use case and add a new subscriber to a project</b>
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
}
