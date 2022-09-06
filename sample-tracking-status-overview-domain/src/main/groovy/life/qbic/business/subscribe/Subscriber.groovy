package life.qbic.business.subscribe

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
    final String title

    Subscriber(String firstName, String lastName, String title, String email) {
        this.firstName = firstName
        this.lastName = lastName
        this.title = title
        this.email = email
    }


    @Override
    String toString() {
        return "Subscriber{" +
                "title='" + title + '\'' +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}'
    }
}
