package life.qbic.business.samples.count

import life.qbic.business.DataSourceException


/**
 * <b>Provides project identifiers</b>
 *
 * <p>This interface is used by {@link LoadSamples}</p>
 *
 * @since 1.0.0
 */
interface CountSamplesDataSource {

    /**
     * Counts samples of a project that have at least reached the status "sample received"
     * @return number of samples that are part of this project and which have been received
     * @since 1.0.0
     * @throws DataSourceException in case of a technical error with the data source
     */
    List<String> countReceivedSamples(String projectCode) throws DataSourceException
    
    /**
     * Counts all samples of a project
     * @return number of samples that are part of this project
     * @since 1.0.0
     * @throws DataSourceException in case of a technical error with the data source
     */
    List<String> countSamples(String projectCode) throws DataSourceException
}
