package com.hxht.autodeploy.service.sync.huigu.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;

public class Plan {
    private String uid;

    private String interfaceplanId;

    private String caseId;

    private String judgeId;

    private String secretaryId;

    private String courtroomId;

    private Date startDate;

    private Date endDate;

    private Integer isPublic;

    private Integer status;

    private Byte allowplay;

    private Integer flag;
    private String info;

    /**
     * @return uid
     */
    public String getUid() {
        return uid;
    }

    /**
     * @param uid
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * @return interfaceplan_id
     */
    public String getInterfaceplanId() {
        return interfaceplanId;
    }

    /**
     * @param interfaceplanId
     */
    public void setInterfaceplanId(String interfaceplanId) {
        this.interfaceplanId = interfaceplanId;
    }

    /**
     * @return case_id
     */
    public String getCaseId() {
        return caseId;
    }

    /**
     * @param caseId
     */
    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    /**
     * @return judge_id
     */
    public String getJudgeId() {
        return judgeId;
    }

    /**
     * @param judgeId
     */
    public void setJudgeId(String judgeId) {
        this.judgeId = judgeId;
    }

    /**
     * @return secretary_id
     */
    public String getSecretaryId() {
        return secretaryId;
    }

    /**
     * @param secretaryId
     */
    public void setSecretaryId(String secretaryId) {
        this.secretaryId = secretaryId;
    }

    /**
     * @return courtroom_id
     */
    public String getCourtroomId() {
        return courtroomId;
    }

    /**
     * @param courtroomId
     */
    public void setCourtroomId(String courtroomId) {
        this.courtroomId = courtroomId;
    }

    /**
     * @return start_date
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return end_date
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return status
     */
    public Integer getStatus() {
        return status;
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
    }

    /**
     * @param status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return allowplay
     */
    public Byte getAllowplay() {
        return allowplay;
    }

    /**
     * @param allowplay
     */
    public void setAllowplay(Byte allowplay) {
        this.allowplay = allowplay;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Plan plan = (Plan) o;

        return new EqualsBuilder()
                .append(uid, plan.uid)
                .append(interfaceplanId, plan.interfaceplanId)
                .append(caseId, plan.caseId)
                .append(judgeId, plan.judgeId)
                .append(secretaryId, plan.secretaryId)
                .append(courtroomId, plan.courtroomId)
                .append(startDate, plan.startDate)
                .append(endDate, plan.endDate)
                .append(isPublic, plan.isPublic)
                .append(status, plan.status)
                .append(allowplay, plan.allowplay)
                .append(flag, plan.flag)
                .append(info, plan.info)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(uid)
                .append(interfaceplanId)
                .append(caseId)
                .append(judgeId)
                .append(secretaryId)
                .append(courtroomId)
                .append(startDate)
                .append(endDate)
                .append(isPublic)
                .append(status)
                .append(allowplay)
                .append(flag)
                .append(info)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "Plan{" +
                "uid='" + uid + '\'' +
                ", interfaceplanId='" + interfaceplanId + '\'' +
                ", caseId='" + caseId + '\'' +
                ", judgeId='" + judgeId + '\'' +
                ", secretaryId='" + secretaryId + '\'' +
                ", courtroomId='" + courtroomId + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isPublic=" + isPublic +
                ", status=" + status +
                ", allowplay=" + allowplay +
                ", flag=" + flag +
                ", info='" + info + '\'' +
                '}';
    }
}