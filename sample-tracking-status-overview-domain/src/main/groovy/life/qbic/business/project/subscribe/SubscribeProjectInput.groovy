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
     * Subscribes a user with the provided information to a project
     * @param firstName the first name of the subscriber
     * @param lastName the last name of the subscriber
     * @param email the email address of the subscriber
     * @param projectCode the project to subscribe to
     * @since 1.0.0
     */
    void subscribe(String firstName, String lastName, String email, String projectCode)
}
