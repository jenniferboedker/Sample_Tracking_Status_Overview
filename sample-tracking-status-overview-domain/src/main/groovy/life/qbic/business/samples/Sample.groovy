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
        if(SampleCodeFunctions.isQbicBarcode(code)){
           this.code = code
        }else{
            throw new IllegalArgumentException("Sample code is not a QBiC barcode")
        }
        this.name = Objects.requireNonNull(name, "Sample name must not be null")
        this.status = Objects.requireNonNull(status, "Sample status must not be null")
    }


    @Override
    public String toString() {
        return "Sample{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}