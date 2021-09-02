package life.qbic.business.project.subscribe

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
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
