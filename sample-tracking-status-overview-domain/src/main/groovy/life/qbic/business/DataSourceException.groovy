package life.qbic.business

/**
 * <b>Something went wrong with the data source</b>
 *
 * <p>Exception to be thrown if there is an error in the data source</p>
 *
 * @since 1.0.0
 */
class DataSourceException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the specified detail message.
     * The cause is not initialized, and may subsequently be initialized by a call to initCause.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the getMessage() method
     * @since 1.0.0
     */
    DataSourceException(String message) {
        super(message)
    }

    /**
     * Constructs a new runtime exception with the specified detail message and cause.
     Note that the detail message associated with cause is not automatically incorporated in this runtime exception's detail message.

     * @param message the detailed message (which is saved for later retrieval by the getMessage() method).
     * @param cause  the cause (which is saved for later retrieval by the getCause() method).
     *  (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     * @since 1.0.0
     */
    DataSourceException(String message, Throwable cause) {
        super(message, cause)
    }
}
