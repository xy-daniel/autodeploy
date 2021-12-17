package com.hxht.autodeploy.utils

import com.hxht.autodeploy.device.DataTable
import com.hxht.autodeploy.device.Device
import com.jcraft.jsch.ChannelExec
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.Session

import java.nio.file.Files
import java.nio.file.Paths

/**
 * SshUtil created in 20210712 by daniel
 */
class SshUtil {

    /**
     * ssh连接
     * @param host 主机地址
     * @param port 主机端口
     * @param user 主机用户名
     * @param password 主机密码
     * @return 连接* @throws JSchException    连接异常
     */
    static Session connect(String host, int port, String user, String password) throws JSchException {
        println "请求ssh连接,参数:host=${host},port=${port},user=${user},password=${password}."
        JSch jSch = new JSch()
        Session session = jSch.getSession(user, host, port)
        session.setPassword(password)
        session.setConfig("StrictHostKeyChecking", "no")
        session.connect(6000)
        if (session.isConnected()) {
            println "连接成功."
            return session
        }
        return null
    }

    /**
     * ssh断开连接
     * @param session 连接
     */
    static void disconnect(Session session) {
        println "请求ssh断开连接."
        session.disconnect()
        println "断开连接成功."
    }

    /**
     * 执行命令
     * @param session 通道
     * @param command 命令
     * @return 执行结果* @throws JSchException    连接异常
     */
    static String exec(Session session, String command) throws JSchException {
        println "[${session.userName}@${session.host}]# ${command}"
        List<String> resultLines = new ArrayList<>()
        ChannelExec channel = null
        try {
            channel = (ChannelExec) session.openChannel("exec")
            channel.setCommand(command)
            InputStream input = channel.getInputStream()
            channel.connect(6000)
            try {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(input))
                String inputLine = null
                while ((inputLine = inputReader.readLine()) != null) {
                    resultLines.add(inputLine)
                }
                println resultLines as String
            } finally {
                if (input != null) {
                    try {
                        input.close()
                    } catch (e) {
                        println "执行流关闭失败,错误信息:\n${e.getStackTrace()}"
                    }
                }
            }
        } catch (e) {
            println "通道流关闭失败,错误信息:\n${e.getStackTrace()}"
        } finally {
            if (channel != null) {
                try {
                    channel.disconnect()
                } catch (e) {
                    println "通道关闭失败,错误信息:\n${e.getStackTrace()}"
                }
            }
        }
        return resultLines.join(",")
    }

    /**
     * 文件上传
     * @param source 资源路径
     * @param session 连接
     * @param destination 目标路径
     * @return 标识
     */
    static long scpTo(String source, Session session, String destination) {
        println "开始从${source}上传文件到${destination}."
        FileInputStream fileInputStream = null
        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec")
            OutputStream out = channel.getOutputStream()
            InputStream input = channel.getInputStream()
            boolean timestamp = false
            String command = "scp"
            if (timestamp) {
                command += " -p"
            }
            command += " -t " + destination
            channel.setCommand(command)
            channel.connect(6000)
            if (checkAck(input) != 0) {
                return -1
            }
            File _file = new File(source)
            if (timestamp) {
                command = "T " + (_file.lastModified() / 1000) + " 0"
                command += (" " + (_file.lastModified() / 1000) + " 0\n")
                out.write(command.getBytes())
                out.flush()
                if (checkAck(input) != 0) {
                    return -1
                }
            }
            long fileSize = _file.length()
            command = "C0644 " + fileSize + " "
            if (source.lastIndexOf('/') > 0) {
                command += source.substring(source.lastIndexOf('/') + 1)
            } else {
                command += source
            }
            command += "\n"
            out.write(command.getBytes())
            out.flush()
            if (checkAck(input) != 0) {
                return -1
            }
            //send content of file
            fileInputStream = new FileInputStream(source)
            byte[] buf = new byte[1024]
            long sum = 0
            while (true) {
                int len = fileInputStream.read(buf, 0, buf.length)
                if (len <= 0) {
                    break
                }
                out.write(buf, 0, len)
                sum += len
            }
            //send '\0'
            buf[0] = 0
            out.write(buf, 0, 1)
            out.flush()
            if (checkAck(input) != 0) {
                return -1
            }
            println "上传完成."
            return sum
        } catch (JSchException e) {
            println "上传文件产生JSchException异常,错误信息:${e.getStackTrace()}"
        } catch (IOException e) {
            println "上传文件产生IOException异常,错误信息:${e.getStackTrace()}"
        } catch (Exception e) {
            println "上传文件产生异常,错误信息:${e.getStackTrace()}"
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close()
                } catch (Exception e) {
                    println "文件流关闭失败,错误信息:${e.getStackTrace()}"
                }
            }
        }
        return -1
    }

    /**
     * 文件下载
     * @param session 连接
     * @param source 资源路径
     * @param destination 目标路径
     * @return 标识
     */
    static long scpFrom(Session session, String source, String destination) {
        println "开始从${source}下载文件到${destination}."
        FileOutputStream fileOutputStream = null
        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec")
            channel.setCommand("scp -f " + source)
            OutputStream out = channel.getOutputStream()
            InputStream input = channel.getInputStream()
            channel.connect()
            byte[] buf = new byte[1024]
            buf[0] = 0
            out.write(buf, 0, 1)
            out.flush()
            while (true) {
                if (checkAck(input) != (char) 'C') {
                    break
                }
            }
            input.read(buf, 0, 4)
            long fileSize = 0
            while (true) {
                if (input.read(buf, 0, 1) < 0) {
                    break
                }
                if ((buf[0] as char) == (' ' as char)) {
                    break
                }
                fileSize = fileSize * 10L + (long) (buf[0] - (char) '0')
            }
            String file = null
            for (int i = 0; ; i++) {
                input.read(buf, i, 1)
                if (buf[i] == (byte) 0x0a) {
                    file = new String(buf, 0, i)
                    break
                }
            }
            buf[0] = 0
            out.write(buf, 0, 1)
            out.flush()
            if (Files.isDirectory(Paths.get(destination))) {
                fileOutputStream = new FileOutputStream(destination + File.separator + file)
            } else {
                fileOutputStream = new FileOutputStream(destination)
            }
            long sum = 0
            while (true) {
                int len = input.read(buf, 0, buf.length)
                if (len <= 0) {
                    break
                }
                sum += len
                if (len >= fileSize) {
                    fileOutputStream.write(buf, 0, (int) fileSize)
                    break
                }
                fileOutputStream.write(buf, 0, len)
                fileSize -= len
            }
            println "下载完成."
            return sum
        } catch (JSchException e) {
            println "scp to catched jsch exception, ${e.getMessage()}"
        } catch (IOException e) {
            println "scp to catched io exception, ${e.getMessage()}"
        } catch (Exception e) {
            println "scp to error, ${e.getStackTrace()}"
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close()
                } catch (Exception e) {
                    println "File output stream close error, ${e.getMessage()}"
                }
            }
        }
        return -1
    }

    /**
     * 公共方法
     * @param input 输出流
     * @return 校验结果* @throws IOException IO异常
     */
    private static int checkAck(InputStream input) throws IOException {
        int b = input.read()
        if (b == 0) return b
        if (b == -1) return b
        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer()
            int c
            for (int i = 0; i >= 0; i++) {
                c = input.read()
                sb.append((char) c)
                if (c != (char) '\n') {
                    break
                }
            }
            if (b == 1 || b == 2) {
                println sb.toString()
            }
        }
        return b
    }

    /**
     * MySQL数据库备份
     * @return
     */
    static String mysqldump(DataTable dataTable) {
        Device device = dataTable.device
        Session session = connect(device.ip, device.port, device.point, device.pwd)
        String sqlName = UUIDGenerator.nextUUID()
        String tableDirPath = ""
        def osName = System.getProperty("os.name")
        if (osName.startsWith("Windows")) {
            tableDirPath = "D:" + File.separator
        } else {
            tableDirPath = File.separator
        }
        tableDirPath += "home" + File.separator + "hxht" + File.separator + "mysqldump" + File.separator + dataTable.tableName
        def file = new File(tableDirPath)
        if (!file.exists()) {
            file.mkdir()
            file.canRead()
            file.canWrite()
            file.canExecute()
        }
        exec(session, "mysqldump -u${dataTable.username} -p${dataTable.password} ${dataTable.tableName} | gzip > /opt/${sqlName}.sql.gz")
        scpFrom(session, "/opt/${sqlName}.sql.gz", tableDirPath + File.separator + sqlName + ".sql.gz")
        disconnect(session)
        return tableDirPath + File.separator + sqlName + ".sql.gz"
    }
}
