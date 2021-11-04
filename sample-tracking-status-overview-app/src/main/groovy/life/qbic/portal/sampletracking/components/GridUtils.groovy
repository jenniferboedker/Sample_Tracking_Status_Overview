package life.qbic.portal.sampletracking.components

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.shared.ui.ValueChangeMode
import com.vaadin.ui.Grid
import com.vaadin.ui.TextField
import com.vaadin.ui.components.grid.HeaderRow
import com.vaadin.ui.themes.ValoTheme
import org.apache.commons.lang3.StringUtils

/**
 * A helper class with static utility functions for Vaadin Grids.
 *
 * @since 1.0.0
 */
class GridUtils {

    /**
     * Provides a filter field into a header row of a grid for a given column.
     *
     * The current implementation filters for content that contains the filter criteria in the
     * column values and ignores the case.
     *
     * @param dataProvider The grid's {@link ListDataProvider}
     * @param column The column to add the filter to
     * @param headerRow The{@link com.vaadin.ui.components.grid.HeaderRow} of the {@link Grid}, where the filter input field is added
     */
    static <T> void setupColumnFilter(ListDataProvider<T> dataProvider,
                                      Grid.Column<T, String> column,
                                      HeaderRow headerRow) {
        TextField filterTextField = new TextField()
        filterTextField.addValueChangeListener(event -> {
            dataProvider.addFilter(element ->
                    StringUtils.containsIgnoreCase(column.getValueProvider().apply(element), filterTextField.getValue())
            )
        })
        styleFilterTextField(filterTextField, column.getCaption())
        headerRow.getCell(column).setComponent(filterTextField)
    }

    private static void styleFilterTextField(TextField filterTextField, String columnCaption) {
        filterTextField.setValueChangeMode(ValueChangeMode.EAGER)
        filterTextField.addStyleName(ValoTheme.TEXTFIELD_TINY)
        filterTextField.setPlaceholder("Filter by $columnCaption")
        filterTextField.setSizeFull()
    }

    /**
     * Provides filter fields into the header row of the provided grid for multiple column identifiers
     *
     * The current implementation filters for content that contains the filter criteria in the
     * column values and ignores the case.
     *
     * @param grid The vaadin grid {@link Grid} for which the filter columns should be added
     * @param columnIdentifiers The column identifiers specifying into which column a filter should be added
     */
    static <T> void setupFilters(Grid<T> grid, Collection<String> columnIdentifiers) {
        HeaderRow customerFilterRow
        if (grid.headerRowCount > 1) {
            customerFilterRow = grid.getHeaderRow(1)
        } else {
            customerFilterRow = grid.appendHeaderRow()
        }
        ListDataProvider<Object> gridDataProvider = grid.getDataProvider() as ListDataProvider<Object>
        columnIdentifiers.forEach { String columnId ->
            setupColumnFilter(gridDataProvider,
                    grid.getColumn(columnId),
                    customerFilterRow)
        }
    }
}
