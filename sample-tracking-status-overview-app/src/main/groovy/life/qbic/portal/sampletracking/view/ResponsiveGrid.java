package life.qbic.portal.sampletracking.view;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.shared.Registration;
import com.vaadin.ui.Grid;
import java.util.Collection;
import java.util.Objects;

public class ResponsiveGrid<T> extends Grid<T> {

  private Registration browserWindowResizeListener;
  private GridFilter<T> gridFilter;

  @Override

  public void attach() {
    super.attach();
    browserWindowResizeListener = this.getUI().getPage()
        .addBrowserWindowResizeListener(it -> recalculateColumnWidths());
  }

  @Override
  public void detach() {
    super.detach();
    if (Objects.nonNull(browserWindowResizeListener)) {
      browserWindowResizeListener.remove();
    }
  }

  public void setFilter(GridFilter<T> gridFilter) {
    this.gridFilter = gridFilter;
    filterData();
  }

  public GridFilter<T> getGridFilter() {
    return gridFilter;
  }

  @Override
  public void setItems(Collection<T> items) {
    super.setItems(items);
    filterData();
  }

  private void filterData() {
    if (Objects.isNull(gridFilter)) {
      return;
    }
    ListDataProvider<T> dataProvider = (ListDataProvider<T>) getDataProvider();
    dataProvider.setFilter(gridFilter::test);
    setDataProvider(dataProvider);
  }


}
