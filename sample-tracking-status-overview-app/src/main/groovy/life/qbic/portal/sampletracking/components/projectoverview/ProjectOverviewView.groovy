package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.icons.VaadinIcons
import com.vaadin.shared.data.sort.SortDirection
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.Button
import com.vaadin.ui.Grid
import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.renderers.DateRenderer
import com.vaadin.ui.renderers.TextRenderer
import com.vaadin.ui.themes.ValoTheme
import life.qbic.business.projectoverview.FailedSamplesRatio
import life.qbic.business.projectoverview.Project
import life.qbic.portal.sampletracking.components.GridUtils

/**
 * <h1>This class generates the layout for the ProductOverview use case</h1>
 *
 * <p>This view will be the entry point for the user and provides an overview of her projects. And the projects overall status.
 * From here, the user can navigate to the StatusOverview.</p>
 *
 * @since 1.0.0
 *
*/
class ProjectOverviewView extends VerticalLayout{

    private Label titleLabel
    private ProjectOverviewViewModel viewModel

    private Grid<Project> projectGrid
    private Button detailsButton
    private Button downloadButton

    ProjectOverviewView(ProjectOverviewViewModel viewModel){
        this.viewModel = viewModel

        initLayout()
        fillGrid()
    }

    private void initLayout(){
        titleLabel = new Label("Project Overview")
        titleLabel.addStyleName(ValoTheme.LABEL_LARGE)
        projectGrid = new Grid<>()

        detailsButton = new Button("Show Details",VaadinIcons.INFO_CIRCLE_O)
        detailsButton.addStyleName(ValoTheme.LABEL_LARGE)
        detailsButton.enabled = false
        downloadButton = new Button("Download Batch Data",VaadinIcons.DOWNLOAD)
        downloadButton.addStyleName(ValoTheme.LABEL_LARGE)
        downloadButton.enabled = false

        HorizontalLayout buttonLayout = new HorizontalLayout(detailsButton,downloadButton)

        this.addComponents(titleLabel,buttonLayout,projectGrid)
    }

    private void fillGrid(){
        projectGrid.addColumn({ it.projectCode })
                .setCaption("Project Code").setId("ProjectCode")
        Grid.Column<Project,String> descriptionColumn = projectGrid.addColumn({ it.projectDescription })
                .setCaption("Description").setId("Description")
        //todo add progressbar here
        Grid.Column<Project, FailedSamplesRatio> failedSamplesColumn = projectGrid.addColumn({ it.failedSamples })
                .setCaption("Failed Samples").setId("FailedSamples")
        //failedSamplesColumn.setRenderer(failedSamples -> failedSamples, new TextRenderer())
        Grid.Column<Project,Date> dateColumn = projectGrid.addColumn({ it.lastUpdate })
                .setCaption("Last Update").setId("LastUpdate")
        dateColumn.setRenderer(date -> date, new DateRenderer('%1$tY-%1$tm-%1$td'))

        setupDataProvider()

        //specify size of grid and layout
        projectGrid.setWidthFull()
        //todo introduce variable for description column width
        descriptionColumn.setWidth(GridUtils.DESCRIPTION_COLUMN_LIMIT)
        projectGrid.setHeightMode(HeightMode.ROW)
        projectGrid.sort("LastUpdate", SortDirection.ASCENDING)
    }

    private void setupDataProvider() {
        def dataProvider = new ListDataProvider(viewModel.projects)
        projectGrid.setDataProvider(dataProvider)
    }
}