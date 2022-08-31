package life.qbic.portal.sampletracking.components;

/**
 * <b>short description</b>
 *
 * <p>detailed description</p>
 *
 * @since <version tag>
 */
public interface GridFilter<T> {

  boolean test(T item);
  void clear();

}
