package life.qbic.portal.sampletracking.services.sample

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import life.qbic.portal.sampletracking.datasources.Credentials
import org.apache.http.HttpException
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * <b>Sample Tracking Service</b>
 *
 * <p>Connects to the sample tracking service and request tracking information for samples</p>
 *
 * @since 1.1.1
 */
class SampleTracking implements SampleTrackingService{

    public final String VERSION = "/v2"
    public final String SAMPLES_ROUTE = "/samples"
    public static final String STATUS_ENDPOINT = "/status"
    public final String PROJECTS_ROUTE = "/projects"

    private final String serviceEndPoint
    private final String serviceUser
    private final String userPass

    Logger logger = LoggerFactory.getLogger(this.getClass())

    SampleTracking(String serviceUrlBase, Credentials credentials) {
        String serviceUrl = Objects.requireNonNull(serviceUrlBase)
        this.serviceEndPoint = serviceUrl + VERSION
        this.serviceUser = Objects.requireNonNull(credentials.user)
        this.userPass = Objects.requireNonNull(credentials.password)
    }

    @Override
    public Optional<TrackedSample> requestSampleStatus(String sampleCode) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault()
            HttpGet httpGet = createHttpGETSampleStatement(sampleCode)
            CloseableHttpResponse response = httpClient.execute(httpGet)
            if (response.getStatusLine().statusCode != 200){
                throw new HttpException("Sample Tracking Service returned ${response.getStatusLine().getStatusCode()} : ${response.getStatusLine().reasonPhrase} for ${sampleCode}")
            }
            String result = EntityUtils.toString(response.getEntity())
            TrackedSample trackedSample = extractTrackedSample(result)

            return Optional.of(trackedSample)
        } catch (Exception e) {
            logger.error(e.getMessage())
            return Optional.empty()
        }
    }

    private static TrackedSample extractTrackedSample(String result) {
        //the http response body is a json object
        ObjectMapper jsonMapper = new ObjectMapper()
        JsonNode node = jsonMapper.readTree(result)

        return getTrackedSampleFromJsonNode(node)
    }

    private static TrackedSample getTrackedSampleFromJsonNode(JsonNode node) {
        String status = node.get("status")
        String validSince = node.get("statusValidSince")
        String sampleCode = node.get("sampleCode")
        status = status.replace('"', '')
        validSince = validSince.replace('"', '')
        sampleCode = sampleCode.replace('"', '')

        return new TrackedSample(sampleCode, status, validSince)
    }

    private HttpGet createHttpGETSampleStatement(String sampleCode) {
        HttpGet httpGet = new HttpGet(serviceEndPoint + SAMPLES_ROUTE + "/" + sampleCode + STATUS_ENDPOINT)
        httpGet.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((serviceUser + ":" + userPass).getBytes()))

        return httpGet
    }

    public List<TrackedSample> requestProjectSamplesStatus(String projectCode) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault()
            HttpGet httpGet = createHttpGETProjectStatement(projectCode)
            CloseableHttpResponse response = httpClient.execute(httpGet)
            if (response.getStatusLine().statusCode != 200){
                throw new HttpException("Sample Tracking Service returned ${response.getStatusLine().getStatusCode()} : ${response.getStatusLine().reasonPhrase} for ${projectCode}")
            }
            String result = EntityUtils.toString(response.getEntity())
            List<TrackedSample> trackedSample = extractTrackedSamples(result)

            return trackedSample
        } catch (Exception e) {
            logger.error(e.getMessage())
            return new ArrayList<TrackedSample>()
        }
    }

    private HttpGet createHttpGETProjectStatement(String projectCode) {
        HttpGet httpGet = new HttpGet(serviceEndPoint + PROJECTS_ROUTE + "/" + projectCode + STATUS_ENDPOINT)
        httpGet.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((serviceUser + ":" + userPass).getBytes()))

        return httpGet
    }

    private static List<TrackedSample> extractTrackedSamples(String jsonString) {
        List trackedSamples = new ArrayList<TrackedSample>()

        ObjectMapper mapper = new ObjectMapper()
        def node = mapper.readTree(jsonString)

        node.each {element ->
            TrackedSample sample = getTrackedSampleFromJsonNode(element)
            trackedSamples.add(sample)
        }
        return trackedSamples
    }


    /**
     * A simple data structure to store the sample tracking information and pass it to requesting
     */
    static class TrackedSample {
        String sampleCode
        String status
        String validSince

        TrackedSample(String sampleCode, String status, String validSince) {
            this.sampleCode = sampleCode
            this.status = status
            this.validSince = validSince
        }

    }
}
