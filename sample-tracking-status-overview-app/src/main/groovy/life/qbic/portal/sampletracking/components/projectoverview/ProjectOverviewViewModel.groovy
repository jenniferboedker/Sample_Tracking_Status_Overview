package life.qbic.portal.sampletracking.components.projectoverview

import life.qbic.business.projectoverview.Project
import life.qbic.datamodel.dtos.business.services.Product
import life.qbic.datamodel.dtos.general.Person
import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Sample
import life.qbic.portal.sampletracking.communication.Subscriber
import life.qbic.portal.sampletracking.communication.Topic
import life.qbic.portal.sampletracking.resource.project.ProjectResourceService

/**
 * <h1>ViewModel for the {@link ProjectOverviewView}</h1>
 *
 * <p>This model stores the values that are displayed on the {@link ProjectOverviewView}</p>
 *
 * @since 1.0.0
 *
*/
class ProjectOverviewViewModel {

    ObservableList projects = new ObservableList(new ArrayList<Project>())
    final ProjectResourceService projectResourceService

    Subscriber<life.qbic.datamodel.dtos.projectmanagement.Project> projectAddedSubscription
    Subscriber<life.qbic.datamodel.dtos.projectmanagement.Project> projectRemovedSubscription

    ProjectOverviewViewModel(ProjectResourceService projectResourceService){
        this.projectResourceService = projectResourceService
        fetchProjectData()
        subscribeToResources()
        //Todo Remove Mock data when project data is in service
        generateMockData()
    }

    private void fetchProjectData() {
        projects.clear()
        projects.addAll(projectResourceService.iterator())
    }

    private void subscribeToResources() {
        this.projectResourceService.subscribe(projectAddedSubscription, Topic.PROJECT_ADDED)
        this.projectResourceService.subscribe(projectRemovedSubscription, Topic.PROJECT_REMOVED)
        //ToDo How to trigger fetchProjectData here?
    }
    //Todo Remove Mock data generation when project resource service provides list of projects
    void generateMockData(){
        Location location = new Location()

        Sample sample = new Sample()
        sample.setCode("Q1234AC")
        sample.setCurrentLocation(location)

        Project project1 = new Project.Builder("Q1234","This is a test project","Project Title 1",[sample]).progress(0).build()
        Project project2 = new Project.Builder("Q1244","This is a test project 2","Project Title 2",[sample]).progress(1).build()
        Project project3 = new Project.Builder("Q1233","This is a test project 3","Project Title 3",[sample]).progress(0.9).failedSamples(1).build()

        projects.addAll(project1,project2,project3)
    }
}