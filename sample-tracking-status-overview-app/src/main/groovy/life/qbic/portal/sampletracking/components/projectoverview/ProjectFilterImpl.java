package life.qbic.portal.sampletracking.components.projectoverview;

import static org.apache.commons.lang.StringUtils.containsIgnoreCase;

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
public class ProjectFilterImpl implements ProjectFilter {

  private String substring = "";
  private boolean emptyProjectAllowed = false;

  @Override
  public ProjectFilter containingText(String substring) {
    this.substring = substring;
    return this;
  }

  @Override
  public ProjectFilter allowEmptyProjects(boolean emptyProjectAllowed) {
    this.emptyProjectAllowed = emptyProjectAllowed;
    return this;
  }

  @Override
  public Predicate<? extends ProjectSummary> asPredicate() {

    return containsText().and(it -> emptyProjectAllowed || it.getTotalSampleCount() > 0);
  }

  private Predicate<ProjectSummary> containsText() {
    Predicate<ProjectSummary> textInCode = (ProjectSummary it) -> containsIgnoreCase(
        it.getCode(), substring);
    Predicate<ProjectSummary> textInTitle = it -> containsIgnoreCase(it.getTitle(), substring);
    Predicate<ProjectSummary> noTextProvided = it -> substring.isEmpty();

    return textInCode.or(noTextProvided).or(textInTitle);
  }
}
