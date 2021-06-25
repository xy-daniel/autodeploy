package com.hxht.autodeploy.court.mem
/**
 * 案件签名信息临时存放
 */
class Signature implements Serializable {
    private static final long serialVersionUID = 1
    /**
     * 关联的plan id
     */
    Long planId
    /**
     * 签名人员汉字
     */
    String name
    /**
     * 签名图片地址
     */
    String path
    /**
     * 文件大小
     */
    String size
    /**
     * 文件类型
     */
    String type
    /**
     * 创建时间
     */
    Date dateCreated


    static constraints = {
        name maxSize: 60
        path maxSize: 250
        size maxSize: 120
        type maxSize: 120
    }

    static mapping = {
        datasource 'mem'
    }
}
