package life.qbic.business.project.load


import life.qbic.business.project.Project
import life.qbic.business.project.subscribe.Subscriber
import spock.lang.Specification

import java.time.Instant

/**
 * <b>Tests the load project use case</b>
 *
 * @since 1.0.0
 */
class LoadProjectsSpec extends Specification {

    Subscriber subscriber =  new Subscriber("First", "Last", "e.ma@i.ll")

    def "LoadProjects successful execution returns expected projects"() {
        given: "a project"
        String projectCode = "QABCD"
        Project fetchedProject = new Project(projectCode, "AwesomeProject")

        and: "a basic setup"
        LoadProjectsDataSource dataSource = Stub()
        dataSource.fetchUserProjects() >> [fetchedProject]
        LoadProjectsOutput output = Mock()

        and: "some mock data sources"
        LastChangedDateDataSource lastChangedDateDataSource = Mock()
        SubscribedProjectsDataSource subscriptionDataSource = Mock()

        and: "a use case under test"
        LoadProjects loadProjects = new LoadProjects(dataSource, output, lastChangedDateDataSource, subscriptionDataSource)

        when:"the use case is run"
        loadProjects.loadProjects()

        then: "the timestamp in the success notification is as expected"
        1 * output.loadedProjects([
                { Project project ->
                    assert project.code == fetchedProject.code
                    assert project.title == fetchedProject.title
               }
        ])
        0 * output.failedExecution(_)
    }

    def "LoadProjectsFor successful execution returns expected projects"() {
        given: "a project"
        String projectCode = "QABCD"
        Project fetchedProject = new Project(projectCode, "AwesomeProject")

        and: "a basic setup"
        LoadProjectsDataSource dataSource = Stub()
        dataSource.fetchUserProjects() >> [fetchedProject]
        LoadProjectsOutput output = Mock()

        and: "last changes"
        Instant expectedTimestamp = Instant.now()
        LastChangedDateDataSource changeDataSource = Stub()
        changeDataSource.getLatestChange(fetchedProject.code) >> expectedTimestamp

        and: "subscription information"
        SubscribedProjectsDataSource subscribedProjectsDataSource = Stub()
        subscribedProjectsDataSource.findSubscribedProjectCodesFor(_) >> [projectCode]
        boolean expectedSubscription = true

        and: "a use case under test"
        LoadProjects loadProjects = new LoadProjects(dataSource, output, changeDataSource, subscribedProjectsDataSource)

        when:"the use case is run"
        loadProjects.loadUserProjectsFor(subscriber)

        then:"the timestamp in the success notification is as expected"
        1 * output.loadedProjects(_) >> { arguments ->
            final Project project = arguments.get(0)
            assert project.lastChanged == expectedTimestamp
            assert project.hasSubscription == expectedSubscription
        }
        0 * output.failedExecution(_)
    }

    /*def "LoadUserProjectsWithSubscriptionsFor successful execution lead to success notifications"() {
        given:
        Subscriber subscriber = new Subscriber("Test", "user", "123@invalid.com")
        SubscribedProjectsDataSource subscribedProjectsDataSource = Stub()
        LoadProjectsDataSource dataSource = Stub()
        Project subscribedProject = new Project("QABCD", "AwesomeProject")
        Project notSubscribedProject = new Project("QBCDE", "AwesomeProject")
        List<Project> projects = [subscribedProject, notSubscribedProject]
        dataSource.fetchUserProjects() >> projects
        subscribedProjectsDataSource.findSubscribedProjectCodesFor(subscriber) >> [subscribedProject.code]
        LoadProjectsOutput output = Mock()
        LoadProjects loadProjects = new LoadProjects(dataSource, output, , subscribedProjectsDataSource, )
        when:"the use case is run"
        loadProjects.loadUserProjectsFor(subscriber)
        then:"a successful message is send"
        subscribedProject.hasSubscription && ! notSubscribedProject.hasSubscription
        1 * output.loadedProjects(projects)
        0 * output.failedExecution(_ as String)
    }

    def "unsuccessful execution of the use case lead to failure notifications"() {
        given:
        LoadProjectsDataSource dataSource = Stub()
        LastChangedDateDataSource changeDataSource = Stub()
        dataSource.fetchUserProjects() >> {
            throw new RuntimeException("Testing runtime exceptions")
        }
        LoadProjectsOutput output = Mock()
        LoadProjects loadProjects = new LoadProjects(dataSource, changeDataSource, output)
        when:"the use case is run"
        loadProjects.loadProjects()
        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.loadedProjects(_)
    }


    def "LoadUserProjectsWithSubscriptionsFor unsuccessful execution lead to failure"() {
        given:
        Subscriber subscriber = new Subscriber("Test", "user", "123@invalid.com")
        SubscribedProjectsDataSource subscribedProjectsDataSource = Stub()
        LoadProjectsDataSource dataSource = Stub()
        dataSource.fetchUserProjects() >> {throw exception}
        subscribedProjectsDataSource.findSubscribedProjectCodesFor(subscriber) >> {throw exception}
        LoadProjectsOutput output = Mock()
        LoadProjects loadProjects = new LoadProjects(subscribedProjectsDataSource, dataSource, output)
        when:"the use case is run"
        loadProjects.loadUserProjectsFor(subscriber)
        then:"a failure message is send"
        0 * output.loadedProjects(_ as List<String>)
        1 * output.failedExecution(_ as String)
        where:
        exception << [new RuntimeException(), new DataSourceException("Test")]
    }

    def "a DataSourceException leads to a failure notification and no projects being loaded"() {
        given:
        LoadProjectsDataSource dataSource = Stub()
        LastChangedDateDataSource changeDataSource = Stub()
        dataSource.fetchUserProjects() >> {
            throw new DataSourceException("Testing data source exception")
        }
        LoadProjectsOutput output = Mock()
        LoadProjects loadProjects = new LoadProjects(dataSource, changeDataSource, output)

        when:"the use case is run"
        loadProjects.loadProjects()

        then:"a failure message is send"
        1 * output.failedExecution(_)
        0 * output.loadedProjects(_)
    }*/
}
