package life.qbic.portal.sampletracking.data;

import java.util.List;
import life.qbic.portal.sampletracking.view.projects.viewmodel.Project;

public interface ProjectRepository {

  List<Project> findAll();

}
