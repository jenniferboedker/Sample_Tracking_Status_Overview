package life.qbic.portal.sampletracking.components

import groovy.beans.Bindable


class ViewModel {

    @Bindable boolean projectViewEnabled
    @Bindable boolean sampleViewEnabled

    ViewModel(){
        projectViewEnabled = true
        sampleViewEnabled = false
    }

}
