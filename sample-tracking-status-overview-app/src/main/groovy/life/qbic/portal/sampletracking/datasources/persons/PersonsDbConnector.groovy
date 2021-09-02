package life.qbic.portal.sampletracking.datasources.persons

import life.qbic.business.project.subscribe.PersonDataSource

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
class PersonsDbConnector implements PersonDataSource{
    /**
     * Returns the email that is found in the system for a user identified with the auth id
     * Returns an empty string if nothing could be found.
     * @param authId the authentication provider identifier for the given user
     * @return the email that was found for the user
     */
    @Override
    String getEmailForPerson(String authId) {
        String emailAddress = ""
        //TODO implement
        if (authId == "tobias.koch@uni-tuebingen.de") emailAddress = "tobias.koch@qbic.uni-tuebingen.de"
        //
        return Optional.ofNullable(emailAddress).orElse("")
    }
}
