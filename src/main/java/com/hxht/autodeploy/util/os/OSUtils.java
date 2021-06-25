package com.hxht.autodeploy.util.os;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

public class OSUtils {

    /**
     * 功能：获取Linux系统cpu使用率
     */
    public static float cpuUsage() {
        try {
            Map<?, ?> map1 = OSUtils.cpuInfo();
            Thread.sleep(5 * 1000);
            Map<?, ?> map2 = OSUtils.cpuInfo();
            long user1 = Long.parseLong(map1.get("user").toString());
            long nice1 = Long.parseLong(map1.get("nice").toString());
            long system1 = Long.parseLong(map1.get("system").toString());
            long idle1 = Long.parseLong(map1.get("idle").toString());

            long user2 = Long.parseLong(map2.get("user").toString());
            long nice2 = Long.parseLong(map2.get("nice").toString());
            long system2 = Long.parseLong(map2.get("system").toString());
            long idle2 = Long.parseLong(map2.get("idle").toString());

            long total1 = user1 + system1 + nice1;
            long total2 = user2 + system2 + nice2;
            float total = total2 - total1;

            long totalIdle1 = user1 + nice1 + system1 + idle1;
            long totalIdle2 = user2 + nice2 + system2 + idle2;
            float totalIdle = totalIdle2 - totalIdle1;

            float cpUsage = (total / totalIdle) * 100;
            return cpUsage;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 功能：CPU使用信息
     */
    public static Map<?, ?> cpuInfo() {
        InputStreamReader inputs = null;
        BufferedReader buffer = null;
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            inputs = new InputStreamReader(new FileInputStream("/proc/stat"));
            buffer = new BufferedReader(inputs);
            String line = "";
            while (true) {
                line = buffer.readLine();
                if (line == null) {
                    break;
                }
                if (line.startsWith("cpu")) {
                    StringTokenizer tokenizer = new StringTokenizer(line);
                    List<String> temp = new ArrayList<>();
                    while (tokenizer.hasMoreElements()) {
                        String value = tokenizer.nextToken();
                        temp.add(value);
                    }
                    map.put("user", temp.get(1));
                    map.put("nice", temp.get(2));
                    map.put("system", temp.get(3));
                    map.put("idle", temp.get(4));
                    map.put("iowait", temp.get(5));
                    map.put("irq", temp.get(6));
                    map.put("softirq", temp.get(7));
                    map.put("stealstolen", temp.get(8));
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != buffer) {
                    buffer.close();
                }
                if (null != inputs) {
                    inputs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    /**
     * 功能：内存使用率
     */
    public static Map<String, Object> memoryUsage() {
        Map<String, Object> map = new HashMap<>();
        InputStreamReader inputs = null;
        BufferedReader buffer = null;
        try {
            inputs = new InputStreamReader(new FileInputStream("/proc/meminfo"));
            buffer = new BufferedReader(inputs);
            String line = "";
            while (true) {
                line = buffer.readLine();
                if (line == null)
                    break;
                int beginIndex = 0;
                int endIndex = line.indexOf(":");
                if (endIndex != -1) {
                    String key = line.substring(beginIndex, endIndex);
                    beginIndex = endIndex + 1;
                    endIndex = line.length();
                    String memory = line.substring(beginIndex, endIndex);
                    String value = memory.replace("kB", "").trim();
                    map.put(key, value);
                }
            }
            long memTotal = Long.parseLong(map.get("MemTotal").toString());
            long memFree = Long.parseLong(map.get("MemFree").toString());
            long memUsed = memTotal - memFree;
            long buffers = Long.parseLong(map.get("Buffers").toString());
            long cached = Long.parseLong(map.get("Cached").toString());
            double usage = (double) (memUsed - buffers - cached) / memTotal * 100;
            Map<String, Object> dataMap = new HashMap<>();
            //内存总量
            dataMap.put("memTotal", memTotal / (1024 * 1024));
            //已用内存
            dataMap.put("memUsed", memUsed / (1024 * 1024));
            //剩余内存
            dataMap.put("memFree", memFree / (1024 * 1024));
            //内存使用率
            dataMap.put("usage", usage);
            return dataMap;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != buffer) {
                    buffer.close();
                }
                if (null != inputs) {
                    inputs.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 磁盘空间
     */
    public static Map<String, Object> diskUsage() {
        Map<String, Object> dataMap = new HashMap<>();
        try {
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec("df -hl");// df -hl 查看硬盘空间
            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(
                        p.getInputStream()));
                String str;
                String[] strArray;
                int line = 0;
                while ((str = in.readLine()) != null) {
                    line++;
                    if (line != 2) {
                        continue;
                    }
                    int m = 0;
                    strArray = str.split(" ");
                    for (String para : strArray) {
                        if (para.trim().length() == 0)
                            continue;
                        ++m;
                        if (para.endsWith("G") || para.endsWith("T")) {
                            if (m == 2) {
                                //磁盘总空间
                                if (para.endsWith("G")) {
                                    dataMap.put("diskTotal", para.substring(0, para.indexOf("G")));
                                }
                                if (para.endsWith("T")) {
                                    dataMap.put("diskTotal", String.valueOf(Double.parseDouble(para.substring(0, para.indexOf("T"))) * 1024));
                                }
                            }
                            if (m == 3) {
                                //磁盘已使用空间
                                if (para.endsWith("G")) {
                                    dataMap.put("diskUsed", para.substring(0, para.indexOf("G")));
                                }
                                if (para.endsWith("T")) {
                                    dataMap.put("diskUsed", String.valueOf(Double.parseDouble(para.substring(0, para.indexOf("T"))) * 1024));
                                }
                            }
                        }
                        if (para.endsWith("%")) {
                            if (m == 5) {
                                //磁盘使用率
                                dataMap.put("diskUsage", para.substring(0, para.indexOf("%")));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != in) {
                    in.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataMap;
    }
}
