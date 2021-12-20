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

    Subscriber subscriber =  new Subscriber("First", "Last", "Dr.","e.ma@i.ll")

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
        1 * output.loadedProjects([fetchedProject])
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
        subscribedProjectsDataSource.findSubscribedProjectCodesFor(subscriber) >> [projectCode]
        boolean expectedSubscription = true

        and: "a use case under test"
        LoadProjects loadProjects = new LoadProjects(dataSource, output, changeDataSource, subscribedProjectsDataSource)

        when:"the use case is run"
        loadProjects.withSubscriptions(subscriber)

        then:"the timestamp in the success notification is as expected"
        1 * output.loadedProjects(_) >> { arguments ->
            final List<Project> projects = arguments.get(0)
            Project project = projects.first()
            assert project.lastChanged == expectedTimestamp
            assert project.hasSubscription == expectedSubscription
        }
        0 * output.failedExecution(_)
    }
}
