package life.qbic.business.projectoverview
/**
 * <h1>Ratio of failed samples</h1>
 *
 * <p>This ratio indicates how many samples of a given total amount have failed</p>
 *
 * @since 1.0.0
 *
*/
class FailedSamplesRatio {

    private int failedSamples
    private int totalSamples

    FailedSamplesRatio(int failed, int total){
        failedSamples = failed
        totalSamples = total
    }


    @Override
    String toString() {
        return failedSamples +" of " + totalSamples
    }
}