package life.qbic.business.samples

import life.qbic.datamodel.samples.Status

/**
 * <b>A sample</b>
 *
 * <p>Samples have a QBiC code, a name and a status</p>
 *
 * @since 1.0.0
 */
class Sample {
    /**
     * Sample code uniquely defining a sample
     */
    private String code
    /**
     * Sample name
     */
    private String name
    /**
     * Status of the sample
     */
    private Status status

    Sample(String code, String name, Status status){
        this.code = code
        this.name = name
        this.status = status
    }
}