package com.hxht.autodeploy.sync.rongji.pojo;


import com.hxht.autodeploy.service.sync.huigu.entity.Case;

/**
 * @author alice on 2017/8/15 0015.
 * @version 1.0
 * @since 1.0
 */
public class RJRemoteCase extends Case {
    private String caseTypeName;




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
