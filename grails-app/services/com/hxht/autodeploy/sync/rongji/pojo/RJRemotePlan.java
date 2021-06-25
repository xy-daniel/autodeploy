package com.hxht.autodeploy.sync.rongji.pojo;


import com.hxht.autodeploy.service.sync.huigu.entity.Plan;

/**
 * @author alice on 2017/8/11 0011.
 * @version 1.0
 * @since 1.0
 */
public class RJRemotePlan extends Plan {
    private String judgeCode;
    private String judgeName;
    private String secretaryCode;
    private String secretaryName;
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
