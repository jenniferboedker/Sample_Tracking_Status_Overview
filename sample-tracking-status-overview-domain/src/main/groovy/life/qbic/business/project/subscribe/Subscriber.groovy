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
    final String eMail

    Subscriber(String firstName, String lastName, String eMail) {
        this.firstName = firstName
        this.lastName = lastName
        this.eMail = eMail
    }


    @Override
    String toString() {
        return "Subscriber{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", eMail='" + eMail + '\'' +
                '}'
    }
}
