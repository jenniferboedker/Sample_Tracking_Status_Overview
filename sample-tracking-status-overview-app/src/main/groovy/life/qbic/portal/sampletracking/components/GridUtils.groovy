package life.qbic.portal.sampletracking.components

import com.vaadin.ui.Grid

/**
 * <b>Utility functions for Vaadin Grids.</b>
 *
 * <p>These functions can be used to adapt vaadin grid functionality such as grid column resizeability or grid responsiveness.</p>
 *
 * @since 1.0.2
 */
interface GridUtils {

    /**
     * Enables the possibility of manually resizing vaadin columns in a grid
     *
     * Manual resizing of grid columns in vaadin 8 disables the automatic calculation of column width upon screen resizing,
     * which either causes the grid to flow outside of the screen if the column width is increased or leave empty grid space if column width is decreased
     *
     * @param Grid grid the {@link com.vaadin.ui.Grid}, for which the columns should be resizable
     *
     * @since 1.0.2
     */
    void enableResizableColumns(Grid grid)

    /**
     * Disables the possibility of manually resizing vaadin columns in a grid
     *
     * Manual resizing of grid columns in vaadin 8 disables the automatic calculation of column width upon screen resizing,
     * which either causes the grid to flow outside of the screen if the column width is increased or leave empty grid space if column width is decreased
     *
     * @param Grid grid the {@link com.vaadin.ui.Grid}, for which the columns should be non-resizable
     *
     * @since 1.0.2
     */
    void disableResizableColumns(Grid grid)

    /**
     * Makes sure that the full grid width is used by the columns upon browser resizing
     *
     * Ensures that the grid width is allocated upon all the columns even if max-width attributes for individual columns are set
     * Without this recalculation the unused column width would show as empty space on the right side of the grid
     *
     * @param Grid grid the {@link com.vaadin.ui.Grid}, which should be responsive to browser window resizing
     *
     * @since 1.0.2
     */
    void enableDynamicResizing(Grid grid)

    /**
     * Disables adjustment of column width to fill grid width upon screen resizing
     *
     * The unused column width shows as empty space on the right side of the grid
     *
     * @param Grid grid the {@link com.vaadin.ui.Grid}, which should not be responsive to browser window resizing
     *
     * @since 1.0.2
     */
    void disableDynamicResizing(Grid grid)

}
