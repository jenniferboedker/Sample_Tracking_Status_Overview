package life.qbic.portal.sampletracking.view.projects


/**
 * <b>The state of a project status.</b>
 *
 * <p>A status can have the following states:
 * <ul>
 *     <li><b>COMPLETED</b>: When all samples in a project have reached this status and the status is not a failure status.</li>
 *     <li><b>FAILED</b>: When a status is a failure status and samples are presently in the failed status.</li>
 *     <li><b>IN_PROGRESS</b>: When not all samples were evaluated for the current status. <b>OR</b>: If the status is a failure status and there are not samples in the status.</li>
 * </ul>
 * </p>
 *
 * @since 1.0.0
 */
enum State {
    COMPLETED("status-completed"),
    FAILED("status-failed"),
    IN_PROGRESS("status-in-progress")

    /**
     * The class in the CSS that should be assigned to the element.
     * This does only contain the programmatic part of the css class and does not account for
     * vaadin prefixes like <code>'.v-grid-cell.'</code>
     * @since 1.0.0
     */
    final String cssClass

    State(String cssClass) {
        this.cssClass = cssClass
    }
}
