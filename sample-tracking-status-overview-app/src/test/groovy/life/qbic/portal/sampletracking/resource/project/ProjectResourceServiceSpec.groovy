package life.qbic.portal.sampletracking.resource.project

import life.qbic.datamodel.dtos.projectmanagement.Project
import life.qbic.datamodel.dtos.projectmanagement.ProjectCode
import life.qbic.datamodel.dtos.projectmanagement.ProjectIdentifier
import life.qbic.datamodel.dtos.projectmanagement.ProjectSpace
import life.qbic.portal.sampletracking.communication.Subscriber
import life.qbic.portal.sampletracking.communication.Topic
import spock.lang.Specification

/**
 * <p>Tests that the project resource service functions as expected</p>
 */
class ProjectResourceServiceSpec extends Specification {

    ProjectResourceService projectResourceService = new ProjectResourceService()
    Subscriber<Project> subscriber1 = Mock()
    Subscriber<Project> subscriber2 = Mock()

    def "Removing of a project on the service informs all subscribers"() {
        given: "a service with projects"
        Project project = getFakeProject("project1")
        projectResourceService.addToResource(project)

        when: "the the project is removed from the resource"
        projectResourceService.subscribe(subscriber1, Topic.PROJECT_REMOVED)
        projectResourceService.subscribe(subscriber2, Topic.PROJECT_ADDED)
        projectResourceService.removeFromResource(project)

        then: "all subscribers subscribed to the topic are informed"
        1 * subscriber1.receive(project)
        and: "no subscribers subscribed to other topics are informed"
        0 * subscriber2.receive(_)
    }

    def "Adding of a project on the service informs all subscribers"() {
        given: "a service"
        Project project = getFakeProject("project1")

        when: "the the project is removed from the resource"
        projectResourceService.subscribe(subscriber1, Topic.PROJECT_ADDED)
        projectResourceService.subscribe(subscriber2, Topic.PROJECT_REMOVED)
        projectResourceService.addToResource(project)

        then: "all subscribers subscribed to the topic are informed"
        1 * subscriber1.receive(project)
        and: "no subscribers subscribed to other topics are informed"
        0 * subscriber2.receive(_)
    }

    static Project getFakeProject(String name) {
        ProjectSpace projectSpace = new ProjectSpace("TEST_SPACE")
        ProjectCode projectCode = new ProjectCode("QABCD")
        Project.Builder projectBuilder = new Project.Builder(new ProjectIdentifier(projectSpace, projectCode), "test")
        return projectBuilder.build()
    }
}
