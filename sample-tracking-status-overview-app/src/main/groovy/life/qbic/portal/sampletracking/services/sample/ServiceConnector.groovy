package life.qbic.portal.sampletracking.services.sample

import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.json.JSONObject
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

    public final String SAMPLES_ENDPOINT = "/v2/samples"
    public static final String STATUS_ENDPOINT = "/status"

    private final String serviceEndPoint
    private final String serviceUser
    private final String userPass

    Logger logger = LoggerFactory.getLogger(this.getClass())

    SampleTracking(String serviceUrlBase, String serviceUser, String userPassword) {
        String serviceUrl = Objects.requireNonNull(serviceUrlBase)
        this.serviceEndPoint = serviceUrl + SAMPLES_ENDPOINT
        this.serviceUser = Objects.requireNonNull(serviceUser)
        this.userPass = Objects.requireNonNull(userPassword)
    }

    @Override
    public Optional<TrackedSample> requestSampleStatus(String sampleCode) {
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault()
            HttpGet httpGet = createHttpGETStatement(sampleCode)
            CloseableHttpResponse response = httpClient.execute(httpGet)

            String result = EntityUtils.toString(response.getEntity())
            TrackedSample trackedSample = extractTrackedSample(result)

            return Optional<TrackedSample>.of(trackedSample)
        } catch (Exception e) {
            logger.error(e.getMessage())
            return Optional.empty()
        }
    }

    private TrackedSample extractTrackedSample(String result) {
        //the http response body is a json object
        JSONObject resultJSON = new JSONObject(result)

        String status = resultJSON.get("status")
        String validSince = resultJSON.get("validSince")
        String sampleCode = resultJSON.get("sampleCode")

        return new TrackedSample(sampleCode, status, validSince)
    }

    private HttpGet createHttpGETStatement(String sampleCode) {
        HttpGet httpGet = new HttpGet(serviceEndPoint + "/" + sampleCode + STATUS_ENDPOINT)
        httpGet.addHeader("Authorization", "Basic " + Base64.getEncoder().encodeToString((serviceUser + ":" + userPass).getBytes()))

        return httpGet
    }


    /**
     * A simple data structure to store the sample tracking information and pass it to requesting
     */
    class TrackedSample {
        String sampleCode
        String status
        String validSince

        TrackedSample(String sampleCode, String status, String validSince){
           this.sampleCode = sampleCode
            this.status = status
            this.validSince = validSince
        }

    }
}
