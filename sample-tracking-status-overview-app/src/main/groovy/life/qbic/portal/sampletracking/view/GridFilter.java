package life.qbic.portal.sampletracking.view;

/**
 * A filter applicable to {@link ResponsiveGrid}s. Filters items displayed in the grid it is added to.
 *
 * @since 1.1.4
 */
public interface GridFilter<T> {

  /**
   * Tests whether an item passes the filter or not.
   * @param item the item to test
   * @return true in case the item passes the filter, false otherwise
   */
  boolean test(T item);
}
