package com.hxht.autodeploy.court.mem

class CourtroomIsOnline implements Serializable  {

    private static final long serialVersionUID = 1

    Long courtroomId

    Integer status

    static constraints = {
    }

    static mapping = {
        datasource 'mem'
    }
}
