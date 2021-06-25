package com.hxht.autodeploy.utils

class ExceptionUtil {
    public static String getStackTrace(Throwable throwable) {
        def sw = new StringWriter()
        def pw = new PrintWriter(sw)
        try {
            throwable.printStackTrace(pw)
            return sw.toString()
        } finally {
            pw.close()
        }
    }

}
