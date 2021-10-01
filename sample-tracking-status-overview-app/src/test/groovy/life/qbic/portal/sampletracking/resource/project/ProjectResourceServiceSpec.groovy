package life.qbic.portal.sampletracking.resource.project

import life.qbic.business.project.Project
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

    def "Adding of a project adds the project to the resource"() {
        given: "a project"
        Project project = getFakeProject("I am added")
        when: "a project is added to the resource service"
        projectResourceService.addToResource(project)
        then: "the project is added to the resource"
        projectResourceService.iterator().toList().contains(project)
    }

    def "Removing of a project removes the project to the resource"() {
        given: "a project from the service"
        Project project = getFakeProject("I am added")
        projectResourceService.addToResource(project)
        and: "the adding functionality works"
        assert projectResourceService.iterator().toList().contains(project)
        when: "a project is removed from the resource service"
        projectResourceService.removeFromResource(project)
        then: "the project is added to the resource"
        ! projectResourceService.iterator().toList().contains(project)
    }


    static Project getFakeProject(String name) {
        return new Project("QABCD", name)
    }
}
