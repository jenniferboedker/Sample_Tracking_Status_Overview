package life.qbic.portal.sampletracking.components.projects;

import java.util.List;
import life.qbic.portal.sampletracking.components.projects.viewmodel.Project;

public interface ProjectRepository {

  List<Project> findAll();

}
