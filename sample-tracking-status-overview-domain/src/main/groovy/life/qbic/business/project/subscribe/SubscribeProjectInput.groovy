package life.qbic.business.project.subscribe

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
interface SubscribeProjectInput {

    /**
     * Subscribes a user with the authentication id to a specific project
     * @param authId the authentication identifier for the subscribing user
     * @param projectCode the project to subscribe to
     */
    void subscribe(String authId, String projectCode)
}
