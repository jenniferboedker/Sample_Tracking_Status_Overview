package life.qbic.portal.sampletracking.data

import groovy.transform.Immutable

/**
 * Holds the credentials for authentication.
 *
 * @since 1.0.0
 */
@Immutable
class Credentials {

    /**
     * The user id
     */
    String user

    /**
     * The user password
     */
    String password
}
