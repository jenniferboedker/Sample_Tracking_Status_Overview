package life.qbic.portal.sampletracking.components.projectoverview

import life.qbic.business.projectoverview.Project
import life.qbic.datamodel.samples.Location
import life.qbic.datamodel.samples.Sample

/**
 * <h1>ViewModel for the {@link ProjectOverviewView}</h1>
 *
 * <p>This model stores the values that are displayed on the {@link ProjectOverviewView}</p>
 *
 * @since 1.0.0
 *
*/
class ProjectOverviewViewModel {

    List<Project> projects = []

    ProjectOverviewViewModel(){
        generateMockData()
    }

    void generateMockData(){
        Location location = new Location()

        Sample sample = new Sample()
        sample.setCode("Q1234AC")
        sample.setCurrentLocation(location)

        Project project1 = new Project.Builder("Q1234","This is a test project",[sample]).progress(0).build()
        Project project2 = new Project.Builder("Q1244","This is a test project 2",[sample]).progress(1).build()
        Project project3 = new Project.Builder("Q1233","This is a test project 3",[sample]).progress(0.9).failedSamples(1).build()

        projects.addAll(project1,project2,project3)
    }
}