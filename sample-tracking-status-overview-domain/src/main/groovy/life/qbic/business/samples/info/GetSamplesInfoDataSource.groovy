package life.qbic.business.samples.info

interface GetSamplesInfoDataSource {

    Map<String, String> fetchSampleNamesFor(sampleCodes)
}
