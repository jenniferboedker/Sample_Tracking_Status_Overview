package life.qbic.business.project.load

import life.qbic.business.project.subscribe.Subscriber

/**
 * <b>Input interface for the {@link LoadProjects} feature</b>
 *
 * <p>Provides methods to trigger the loading of projects</p>>
 *
 * @since 1.0.0
 */
interface LoadProjectsInput {

    /**
     * This method calls the output interface with all projects found.
     * In case of failure the output interface failure method is called.
     * @since 1.0.0
     */
    void loadProjects()

    /**
     * This method calls the output interface with all projects found. Loaded projects contain
     * information on the subscription status of the subscriber provided to this method.
     * In case of failure the output interface failure method is called
     * @param subscriber the subscriber for which subscription information is loaded is checked
     * @since 1.0.0
     */
    void withSubscriptions(Subscriber subscriber)
}
