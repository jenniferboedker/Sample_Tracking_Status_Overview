package life.qbic.portal.sampletracking.components.projectoverview

import life.qbic.business.project.load.LoadProjectsInput

/**
 * <b>Controller for loading projects</b>
 * <p>Translates view requests to use case call</p>
 *
 * @since 1.0.0
 */
class LoadProjectsController {
    private final LoadProjectsInput loadProjectsInput

    /**
     *
     * @param loadProjectsInput the input for loading projects
     * @since 1.0.0
     */
    LoadProjectsController(LoadProjectsInput loadProjectsInput) {
        this.loadProjectsInput = loadProjectsInput
    }

    //TODO implement
}
