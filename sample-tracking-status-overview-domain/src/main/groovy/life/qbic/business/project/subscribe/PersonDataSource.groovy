package life.qbic.business.project.subscribe

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
interface PersonDataSource {

    /**
     * Returns the email that is found in the system for a user identified with the auth id
     * @param authId the authentication provider identifier for the given user
     * @return the email that was found for the user
     */
    String getEmailForPerson(String authId)

}