package life.qbic.business.samples.info

import life.qbic.business.DataSourceException
import life.qbic.datamodel.samples.Status

/**
 * <b>Fetches sample information for provided sample codes</b>
 *
 * <p>This interface is implemented by {@link OpenBisConnector}</p>
 *
 * @since 1.0.0
 */
interface GetSamplesInfoDataSource {

    /**
     * Given a list of sample codes, returns a mapping between sample codes and the associated sample names
     * @param List sampleCodes the codes of a number of samples
     * @return Map with sample codes as keys and sample names as values
     * @since 1.0.0
     */
    Map<String, String> fetchSampleNamesFor(List<String> sampleCodes) throws DataSourceException

}
