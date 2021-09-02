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
     * @param projectCode the project to subscribe to
     * @param authId the authentication identifier for the subscribing user
     */
    void subscribe(String authId, String projectCode)
}
