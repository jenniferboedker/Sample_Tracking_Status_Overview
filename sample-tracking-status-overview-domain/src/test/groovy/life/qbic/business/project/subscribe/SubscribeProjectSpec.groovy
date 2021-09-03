package life.qbic.business.project.subscribe

import life.qbic.business.DataSourceException
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


    SubscribeProjectOutput output = Mock()
    SubscriptionDataSource subscriptionDataSource = Mock()
    SubscribeProject subscribeProject = new SubscribeProject(subscriptionDataSource, output)

    def "Subscribe fails for invalid first name: #invalidFirstName"() {
        when:
        subscribeProject.subscribe(invalidFirstName, validLastName, validEmail, validProjectCode)
        then:
        thrown(IllegalArgumentException)
        where:
        invalidFirstName << [null, ""]
    }

    def "Subscribe fails for invalid last name: #invalidLastName"() {
        when:
        subscribeProject.subscribe(validFirstName, invalidLastName, validEmail, validProjectCode)
        then:
        thrown(IllegalArgumentException)
        where:
        invalidLastName << [null, ""]
    }

    def "Subscribe fails for invalid email address: #invalidEmail"() {
        when:
        subscribeProject.subscribe(validFirstName, validLastName, invalidEmail, validProjectCode)
        then:
        thrown(IllegalArgumentException)
        where:
        invalidEmail << [null, ""]
    }

    def "Subscribe fails for invalid project code: #invalidProjectCode"() {
        when:
        subscribeProject.subscribe(validFirstName, validLastName, validEmail, invalidProjectCode)
        then:
        thrown(IllegalArgumentException)
        where:
        invalidProjectCode << [null, "", "1234", "ZBCA"]
    }

    def "Subscribe does not throw an IllegalArgumentException for valid arguments"() {
        when:
        subscribeProject.subscribe(validFirstName, validLastName, validEmail, validProjectCode)
        then:
        notThrown(IllegalArgumentException)
    }

    def "Subscribe informs output of success if no exception is thrown"() {
        when:
        subscribeProject.subscribe(validFirstName, validLastName, validEmail, validProjectCode)
        then:
        1 * output.subscriptionAdded(_)
        0 * output.subscriptionFailed(_, _, _, _)
    }

    def "Subscribe informs output in case of data source failure"() {
        given:
        subscriptionDataSource = Stub()
        subscriptionDataSource.subscribeToProject(_ as Subscriber,
                _ as String) >> { throw new DataSourceException("Some exception.") }
        subscribeProject = new SubscribeProject(subscriptionDataSource, output)
        when:
        subscribeProject.subscribe(validFirstName, validLastName, validEmail, validProjectCode)
        then:
        1 * output.subscriptionFailed(_, _, _, _)
    }

    def "Subscribe throws a RuntimeException in case of unexpected failure"() {
        given:
        subscriptionDataSource = Stub()
        subscriptionDataSource.subscribeToProject(_ as Subscriber, _ as String)
                >> { throw exception }
        subscribeProject = new SubscribeProject(subscriptionDataSource, output)
        when:
        subscribeProject.subscribe(validFirstName, validLastName, validEmail, validProjectCode)
        then: "a new runtime exception is thrown"
        thrown(RuntimeException)
        where: "the original exception is"
        exception << [new IllegalStateException(), new RuntimeException(), new IllegalArgumentException()]
    }


}
