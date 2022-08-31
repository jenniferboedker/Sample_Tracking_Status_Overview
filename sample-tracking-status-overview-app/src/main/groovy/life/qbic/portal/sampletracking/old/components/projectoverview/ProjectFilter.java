package life.qbic.portal.sampletracking.old.components.projectoverview;

import java.util.function.Predicate;

/**
 * <b>A filter for project summaries</b>
 *
 * <p>This filter can be used to filter project summary objects. It can be configured and later retrieved as
 * a predicate.</p>
 *
 * <pre>
 * {@code
 * var condition = new ProjectFilterImpl().containingText("my text").asPredicate();
 * projects.filter(condition); // only project summaries with "my text" in their title or code
 * }
 * </pre>
 *
 * @since 1.0.0
 */
public interface ProjectFilter {

  /**
   * Configures the filter to pass projects with the name or code containing the text provided.
   *
   * @param substring the string contained in either name or code
   * @return a ProjectFilter configured with the substring
   * @since 1.0.0
   */
  ProjectFilter containingText(String substring);

  /**
   * Configures the filter to pass projects with or without samples.
   * @param emptyProjectAllowed whether projects without samples should pass or not
   * @return a ProjectFilter configured to allow/disallow empty project
   * @since 1.0.0
   */
  ProjectFilter allowEmptyProjects(boolean emptyProjectAllowed);

  Predicate<? extends ProjectSummary> asPredicate();

}
