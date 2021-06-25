package com.hxht.autodeploy.exception;

/**
 * @author alice
 *
 * user-defined Exception;
 * @version 1.0
 * @since 1.0
 */
public class MethodNotSupportException extends RuntimeException {
    private static final long serialVersionUID = 8579551458856455128L;
    private static final int code = 2000;

    public MethodNotSupportException(String methodName) {
        super("Method " + methodName + " not support!");
    }

    public static int getCode() {
        return code;
    }
}
