package com.hxht.autodeploy.utils

import java.lang.management.ManagementFactory
import java.lang.management.OperatingSystemMXBean

class OSUtil {

    static String memory() {
        OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean()
        String.format("%.2f", (100 - osmb.getFreePhysicalMemorySize() / osmb.getTotalPhysicalMemorySize() * 100))
    }

    static String space() {
        File rootFile = new File("/")
        String.format("%.2f", 100 - rootFile.getFreeSpace() / rootFile.getTotalSpace() * 100)
    }
}
