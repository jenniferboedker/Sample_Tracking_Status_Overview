package life.qbic.business.project.load

import life.qbic.business.project.subscribe.Subscriber

/**
 * <b>An interface to retrieve project subscription information</b>
 *
 *
 * @since 1.0.0
 */
interface SubscribedProjectsDataSource {

    /**
     * Fetches the project codes for a given subscriber
     * @param subscriber the subscriber for which the projects should be searched for
     * @return a list of project codes the subscriber is subscribed to
     * @since 1.0.0
     */
    List<String> findSubscribedProjectCodesFor(Subscriber subscriber)

}