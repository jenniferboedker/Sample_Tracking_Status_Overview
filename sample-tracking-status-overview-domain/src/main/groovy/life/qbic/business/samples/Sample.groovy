package life.qbic.business.samples

import groovy.transform.EqualsAndHashCode
import life.qbic.datamodel.identifiers.SampleCodeFunctions
import life.qbic.datamodel.samples.Status

/**
 * <b>A sample</b>
 *
 * <p>Samples have a QBiC code, a name and a status</p>
 *
 * @since 1.0.0
 */
@EqualsAndHashCode
class Sample {
    /**
     * Sample code uniquely defining a sample
     */
    final String code
    /**
     * Sample name
     */
    final String name
    /**
     * Status of the sample
     */
    final Status status

    /**
     * Constructs a new Sample. Throws an IllegalArgumentException in case the sample code is not valid
     * @param code the code of the sample
     * @param name the name of the sample
     * @param status the status the sample is in
     * @throws IllegalArgumentException in case the sample code is not valid
     * @since 1.0.0
     */
    Sample(String code, String name, Status status) throws IllegalArgumentException {
        if (!SampleCodeFunctions.isQbicBarcode(code)) {
            throw new IllegalArgumentException("Sample code $code is not a QBiC barcode")
        }
        this.code = code
        this.name = Objects.requireNonNull(name, "Sample name must not be null")
        this.status = Objects.requireNonNull(status, "Sample status must not be null")
    }
}