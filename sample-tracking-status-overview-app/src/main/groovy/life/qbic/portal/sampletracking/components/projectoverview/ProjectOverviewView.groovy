package life.qbic.portal.sampletracking.components.projectoverview

import com.vaadin.data.provider.ListDataProvider
import com.vaadin.shared.ui.grid.HeightMode
import com.vaadin.ui.Grid
import com.vaadin.ui.Label
import com.vaadin.ui.VerticalLayout
import com.vaadin.ui.themes.ValoTheme
import life.qbic.datamodel.dtos.projectmanagement.Project
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

    ProjectOverviewView(ProjectOverviewViewModel viewModel){
        this.viewModel = viewModel

        initLayout()
        fillGrid()
    }

    private void initLayout(){
        titleLabel = new Label("Project Overview")
        titleLabel.addStyleName(ValoTheme.LABEL_LARGE)
        projectGrid = new Grid<>()

        this.addComponents(titleLabel, projectGrid)
    }

    private void fillGrid(){
        projectGrid.addColumn({ it.projectId.projectCode.code})
                .setCaption("Project Code").setId("ProjectCode")
        projectGrid.addColumn({ it.projectTitle })
                .setCaption("Project Title").setId("ProjectTitle").setWidth(GridUtils.TITLE_COLUMN_WIDTH)
        setupDataProvider()
        //specify size of grid and layout
        projectGrid.setWidthFull()
        //todo introduce variable for description column width
        projectGrid.setHeightMode(HeightMode.ROW)
    }

    private void setupDataProvider() {
        def dataProvider = new ListDataProvider(viewModel.projects)
        projectGrid.setDataProvider(dataProvider)
    }
}
