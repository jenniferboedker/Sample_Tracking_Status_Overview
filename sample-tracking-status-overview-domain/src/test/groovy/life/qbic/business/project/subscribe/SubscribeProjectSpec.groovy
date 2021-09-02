package life.qbic.business.project.subscribe

import spock.lang.Specification

/**
 * <b><short description></b>
 *
 * <p><detailed description></p>
 *
 * @since <version tag>
 */
class SubscribeProjectSpec extends Specification {
    String validFirstName = "firstname"
    String validLastName = "lastname"
    String validEmail = "email@addre.ss"
    String validProjectCode = "QABCD"

    def "Subscribe fails for invalid first name: #firstName"() {
        given:
            SubscribeProject subscribeProject = new SubscribeProject()
        when:
            subscribeProject.subscribe(firstName, validLastName, validEmail, validProjectCode)
        then:
            thrown(IllegalArgumentException)
        where:
        firstName << [null, ""]
    }
    def "Subscribe fails for invalid last name: #lastName"() {
        given:
            SubscribeProject subscribeProject = new SubscribeProject()
        when:
            subscribeProject.subscribe(validFirstName, lastName, validEmail, validProjectCode)
        then:
            thrown(IllegalArgumentException)
        where:
        lastName << [null, ""]
    }
    def "Subscribe fails for invalid email address: #email"() {
        given:
            SubscribeProject subscribeProject = new SubscribeProject()
        when:
            subscribeProject.subscribe(validFirstName, validLastName, email, validProjectCode)
        then:
            thrown(IllegalArgumentException)
        where:
        email << [null, ""]
    }
    def "Subscribe fails for invalid project code: #projectCode"() {
        given:
            SubscribeProject subscribeProject = new SubscribeProject()
        when:
        subscribeProject.subscribe(validFirstName, validLastName, validEmail, projectCode)
        then:
            thrown(IllegalArgumentException)
        where:
            projectCode << [null, "", "1234", "ZBCA"]
    }
}
