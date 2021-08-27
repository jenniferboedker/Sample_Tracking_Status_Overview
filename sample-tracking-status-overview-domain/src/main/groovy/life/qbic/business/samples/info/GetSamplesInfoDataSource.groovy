package life.qbic.business.samples.info

interface GetSamplesInfoDataSource {

  //used for testing here, description in other PR
    Map<String, String> fetchSampleNamesFor(sampleCodes)
}
