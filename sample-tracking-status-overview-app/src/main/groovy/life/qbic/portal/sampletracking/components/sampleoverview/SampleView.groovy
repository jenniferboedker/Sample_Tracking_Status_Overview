package life.qbic.portal.sampletracking.components.sampleoverview

import life.qbic.portal.sampletracking.components.ViewModel

class SampleView extends SampleDesign{

    ViewModel viewModel

    SampleView(ViewModel viewModel) {
        super()
        this.viewModel = viewModel
        addClickListener()
    }

    private void addClickListener() {
        this.projectsButton.addClickListener({
            viewModel.projectViewEnabled = true
            viewModel.sampleViewEnabled = false
        })

        viewModel.addPropertyChangeListener({
            if(viewModel.sampleViewEnabled){
                this.setVisible(true)
                this.projectsButton.setEnabled(true)
            }else{
                this.setVisible(false)
                this.projectsButton.setEnabled(false)
            }
        })
    }
}
