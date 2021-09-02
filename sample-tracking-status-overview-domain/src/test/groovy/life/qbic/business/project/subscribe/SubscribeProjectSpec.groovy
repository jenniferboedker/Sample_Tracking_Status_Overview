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

    def "Subscribe fails for invalid first name: #invalidFirstName"() {
        given:
        SubscribeProject subscribeProject = new SubscribeProject()
        when:
        subscribeProject.subscribe(invalidFirstName, validLastName, validEmail, validProjectCode)
        then:
        thrown(IllegalArgumentException)
        where:
        invalidFirstName << [null, ""]
    }

    def "Subscribe fails for invalid last name: #invalidLastName"() {
        given:
        SubscribeProject subscribeProject = new SubscribeProject()
        when:
        subscribeProject.subscribe(validFirstName, invalidLastName, validEmail, validProjectCode)
        then:
        thrown(IllegalArgumentException)
        where:
        invalidLastName << [null, ""]
    }

    def "Subscribe fails for invalid email address: #invalidEmail"() {
        given:
        SubscribeProject subscribeProject = new SubscribeProject()
        when:
        subscribeProject.subscribe(validFirstName, validLastName, invalidEmail, validProjectCode)
        then:
        thrown(IllegalArgumentException)
        where:
        invalidEmail << [null, ""]
    }

    def "Subscribe fails for invalid project code: #invalidProjectCode"() {
        given:
        SubscribeProject subscribeProject = new SubscribeProject()
        when:
        subscribeProject.subscribe(validFirstName, validLastName, validEmail, invalidProjectCode)
        then:
        thrown(IllegalArgumentException)
        where:
        invalidProjectCode << [null, "", "1234", "ZBCA"]
    }
}
