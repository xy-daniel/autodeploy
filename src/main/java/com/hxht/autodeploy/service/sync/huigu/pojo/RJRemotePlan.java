package com.hxht.autodeploy.service.sync.huigu.pojo;

import com.hxht.autodeploy.service.sync.huigu.entity.Plan;

public class RJRemotePlan extends Plan {
    //法官接口id (来自荣基
    private String judgeCode;
    private String judgeName;
    //书记员接口id (来自荣基
    private String secretaryCode;
    private String secretaryName;
    //合议庭成员
    private String collegialPanel;
    private String supervisorUid;

    public String getJudgeCode() {
        return judgeCode;
    }

    public void setJudgeCode(String judgeCode) {
        this.judgeCode = judgeCode;
    }

    public String getJudgeName() {
        return judgeName;
    }

    public void setJudgeName(String judgeName) {
        this.judgeName = judgeName;
    }

    public String getSecretaryCode() {
        return secretaryCode;
    }

    public void setSecretaryCode(String secretaryCode) {
        this.secretaryCode = secretaryCode;
    }

    public String getSecretaryName() {
        return secretaryName;
    }

    public void setSecretaryName(String secretaryName) {
        this.secretaryName = secretaryName;
    }

    public String getCollegialPanel() {
        return collegialPanel;
    }

    public void setCollegialPanel(String collegialPanel) {
        this.collegialPanel = collegialPanel;
    }

    public String getSupervisorUid() {
        return supervisorUid;
    }

    public void setSupervisorUid(String supervisorUid) {
        this.supervisorUid = supervisorUid;
    }

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
