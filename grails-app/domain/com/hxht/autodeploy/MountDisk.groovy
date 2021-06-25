package com.hxht.autodeploy

class MountDisk {
    //创建时间
    Date dateCreated

    //磁盘的挂载路径
    String urlMount

    //磁盘总空间
    long totalSpace

    //挂载磁盘未使用空间
    long usableSpace

    //挂载磁盘已经使用空间
    long freeSpace


    static constraints = {
        totalSpace maxSize: 100
        usableSpace maxSize: 100
        freeSpace maxSize: 100
        urlMount maxSize: 100
    }
    static mapping = {
        version false
        urlMount comment: "磁盘的挂载路径"
        dateCreated comment: "创建时间"
        totalSpace comment: "磁盘总空间大小"
        usableSpace comment: "挂载磁盘未使用空间"
        freeSpace comment: "挂载磁盘已经使用空间"
        comment "配置挂载磁盘路径的表格"
    }
}
