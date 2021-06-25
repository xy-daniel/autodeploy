package com.hxht.autodeploy.court.mem


/**
 * 光盘刻录
 */
class CdBurning implements Serializable {
    private static final long serialVersionUID = 1
    //通知地址
    String url
    //排序
    Integer orderNum

    static constraints = {
        url maxSize: 250
        orderNum maxSize: 50
    }
    static mapping = {
        datasource 'mem'
    }

}
