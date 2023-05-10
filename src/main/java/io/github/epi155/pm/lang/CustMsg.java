package io.github.epi155.pm.lang;

/**
 * Custom message error or warning
 */
public interface CustMsg {
    /**
     * message code
     *
     * @return message code
     */
    String code();

    /**
     * message builder
     *
     * @param objects message parameters
     * @return final message
     */
    String message(Object[] objects);

    /**
     * status code
     *
     * @return  status code
     */
    int statusCode();
}
