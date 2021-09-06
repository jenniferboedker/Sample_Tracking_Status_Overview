package life.qbic.business.project.subscribe

import groovy.transform.EqualsAndHashCode

/**
 * <p>Holds information about a subscriber</p>
 *
 * @since 1.0.0
 */
@EqualsAndHashCode
class Subscriber {
    final String firstName
    final String lastName
    final String email

    Subscriber(String firstName, String lastName, String email) {
        this.firstName = firstName
        this.lastName = lastName
        this.email = email
    }


    @Override
    String toString() {
        return "Subscriber{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}'
    }
}
