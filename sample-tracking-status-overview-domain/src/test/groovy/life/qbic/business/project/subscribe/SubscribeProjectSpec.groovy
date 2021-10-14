package life.qbic.business.project.subscribe

import life.qbic.business.DataSourceException
import spock.lang.Shared
import spock.lang.Specification

/**
 * <b>Specification for the SubscribeProject use case</b>
 *
 * @since 1.0.0
 */
class SubscribeProjectSpec extends Specification {
    @Shared final String validFirstName = "firstname"
    @Shared final String validLastName = "lastname"
    @Shared final String validEmail = "email@addre.ss"
    final String validProjectCode = "QABCD"

    final Subscriber validSubscriber = new Subscriber(validFirstName, validLastName, validEmail)

    SubscribeProjectOutput output = Mock()
    SubscriptionDataSource subscriptionDataSource = Mock()
    SubscribeProject subscribeProject = new SubscribeProject(subscriptionDataSource, output)

    def "Subscribe fails for invalid first name: #invalidFirstName"() {
        when:
        subscribeProject.subscribe(invalidSubscriber, validProjectCode)
        then:
        thrown(IllegalArgumentException)
        where:
        invalidFirstName << [null, ""]
        invalidSubscriber = new Subscriber(invalidFirstName, validLastName, validEmail)
    }

    def "Subscribe fails for invalid last name: #invalidLastName"() {
        when:
        subscribeProject.subscribe(invalidSubscriber, validProjectCode)
        then:
        thrown(IllegalArgumentException)
        where:
        invalidLastName << [null, ""]
        invalidSubscriber = new Subscriber(validFirstName, invalidLastName, validEmail)
    }

    def "Subscribe fails for invalid email address: #invalidEmail"() {
        when:
        subscribeProject.subscribe(invalidSubscriber, validProjectCode)
        then:
        thrown(IllegalArgumentException)
        where:
        invalidEmail << [null, ""]
        invalidSubscriber = new Subscriber(validFirstName, validLastName, invalidEmail)
    }

    def "Subscribe fails for invalid project code: #invalidProjectCode"() {
        when:
        subscribeProject.subscribe(validSubscriber, invalidProjectCode)
        then:
        thrown(IllegalArgumentException)
        where:
        invalidProjectCode << [null, "", "1234", "ZBCA"]
    }

    def "Subscribe does not throw an IllegalArgumentException for valid arguments"() {
        when:
        subscribeProject.subscribe(validSubscriber, validProjectCode)
        then:
        notThrown(IllegalArgumentException)
    }

    def "Subscribe informs output of success if no exception is thrown"() {
        when:
        subscribeProject.subscribe(validSubscriber, validProjectCode)
        then:
        1 * output.subscriptionAdded(_)
        0 * output.subscriptionFailed(validSubscriber, validProjectCode)
    }

    def "Subscribe informs output in case of data source failure"() {
        given:
        subscriptionDataSource = Stub()
        subscriptionDataSource.subscribeToProject(_ as Subscriber,
                _ as String) >> { throw new DataSourceException("Some exception.") }
        subscribeProject = new SubscribeProject(subscriptionDataSource, output)
        when:
        subscribeProject.subscribe(validSubscriber, validProjectCode)
        then:
        1 * output.subscriptionFailed(validSubscriber, validProjectCode)
    }

    def "Subscribe throws a RuntimeException in case of unexpected failure"() {
        given:
        subscriptionDataSource = Stub()
        subscriptionDataSource.subscribeToProject(_ as Subscriber, _ as String)
                >> { throw exception }
        subscribeProject = new SubscribeProject(subscriptionDataSource, output)
        when:
        subscribeProject.subscribe(validSubscriber, validProjectCode)
        then: "a new runtime exception is thrown"
        thrown(RuntimeException)
        where: "the original exception is"
        exception << [new IllegalStateException(), new RuntimeException(), new IllegalArgumentException()]
    }

    
    // unsubscription
    
    def "Unsubscribe fails for invalid email address: #invalidEmail"() {
        when:
        subscribeProject.unsubscribe(invalidSubscriber, validProjectCode)
        then:
        thrown(IllegalArgumentException)
        where:
        invalidEmail << [null, ""]
        invalidSubscriber = new Subscriber(validFirstName, validLastName, invalidEmail)
    }

    def "Unsubscribe fails for invalid project code: #invalidProjectCode"() {
        when:
        subscribeProject.unsubscribe(validSubscriber, invalidProjectCode)
        then:
        thrown(IllegalArgumentException)
        where:
        invalidProjectCode << [null, "", "1234", "ZBCA"]
    }

    def "Unsubscribe does not throw an IllegalArgumentException for valid arguments"() {
        when:
        subscribeProject.unsubscribe(validSubscriber, validProjectCode)
        then:
        notThrown(IllegalArgumentException)
    }

    def "Unsubscribe informs output of success if no exception is thrown"() {
        when:
        subscribeProject.unsubscribe(validSubscriber, validProjectCode)
        then:
        1 * output.subscriptionRemoved(_)
        0 * output.unsubscriptionFailed(validSubscriber, validProjectCode)
    }

    def "unsubscribe informs output in case of data source failure"() {
        given:
        subscriptionDataSource = Stub()
        subscriptionDataSource.unsubscribeFromProject(_ as Subscriber,
                _ as String) >> { throw new DataSourceException("Some exception.") }
        subscribeProject = new SubscribeProject(subscriptionDataSource, output)
        when:
        subscribeProject.unsubscribe(validSubscriber, validProjectCode)
        then:
        1 * output.unsubscriptionFailed(validSubscriber, validProjectCode)
    }

    def "Unsubscribe throws a RuntimeException in case of unexpected failure"() {
        given:
        subscriptionDataSource = Stub()
        subscriptionDataSource.unsubscribeFromProject(_ as Subscriber, _ as String)
                >> { throw exception }
        subscribeProject = new SubscribeProject(subscriptionDataSource, output)
        when:
        subscribeProject.unsubscribe(validSubscriber, validProjectCode)
        then: "a new runtime exception is thrown"
        thrown(RuntimeException)
        where: "the original exception is"
        exception << [new IllegalStateException(), new RuntimeException(), new IllegalArgumentException()]
    }

}
