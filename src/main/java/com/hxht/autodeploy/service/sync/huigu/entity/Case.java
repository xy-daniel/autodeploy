package com.hxht.autodeploy.service.sync.huigu.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Date;
import java.util.Objects;

public class Case {
    private String uid;

    private String caseno;

    private String interfaceId;

    private String deptId;

    private String casetypeId;

    private String accuseLawer;

    private String accusedLawer;

    private Date casedate;

    private Integer flag;

    private String casename;

    private String summary;

    private String casedesc;

    private String accuse;

    private String accused;

    private String party;

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
     * @return caseno
     */
    public String getCaseno() {
        return caseno;
    }

    /**
     * @param caseno
     */
    public void setCaseno(String caseno) {
        this.caseno = caseno;
    }

    /**
     * @return interface_id
     */
    public String getInterfaceId() {
        return interfaceId;
    }

    /**
     * @param interfaceId
     */
    public void setInterfaceId(String interfaceId) {
        this.interfaceId = interfaceId;
    }

    /**
     * @return dept_id
     */
    public String getDeptId() {
        return deptId;
    }

    /**
     * @param deptId
     */
    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    /**
     * @return casetype_id
     */
    public String getCasetypeId() {
        return casetypeId;
    }

    /**
     * @param casetypeId
     */
    public void setCasetypeId(String casetypeId) {
        this.casetypeId = casetypeId;
    }

    /**
     * @return accuse_lawer
     */
    public String getAccuseLawer() {
        return accuseLawer;
    }

    /**
     * @param accuseLawer
     */
    public void setAccuseLawer(String accuseLawer) {
        this.accuseLawer = accuseLawer;
    }

    /**
     * @return accused_lawer
     */
    public String getAccusedLawer() {
        return accusedLawer;
    }

    /**
     * @param accusedLawer
     */
    public void setAccusedLawer(String accusedLawer) {
        this.accusedLawer = accusedLawer;
    }

    /**
     * @return casedate
     */
    public Date getCasedate() {
        return casedate;
    }

    /**
     * @param casedate
     */
    public void setCasedate(Date casedate) {
        this.casedate = casedate;
    }

    /**
     * @return flag
     */
    public Integer getFlag() {
        return flag;
    }

    /**
     * @param flag
     */
    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    /**
     * @return casename
     */
    public String getCasename() {
        return casename;
    }

    /**
     * @param casename
     */
    public void setCasename(String casename) {
        this.casename = casename;
    }

    /**
     * @return summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return casedesc
     */
    public String getCasedesc() {
        return casedesc;
    }

    /**
     * @param casedesc
     */
    public void setCasedesc(String casedesc) {
        this.casedesc = casedesc;
    }

    /**
     * @return accuse
     */
    public String getAccuse() {
        return accuse;
    }

    /**
     * @param accuse
     */
    public void setAccuse(String accuse) {
        this.accuse = accuse;
    }

    /**
     * @return accused
     */
    public String getAccused() {
        return accused;
    }

    /**
     * @param accused
     */
    public void setAccused(String accused) {
        this.accused = accused;
    }

    /**
     * @return party
     */
    public String getParty() {
        return party;
    }

    /**
     * @param party
     */
    public void setParty(String party) {
        this.party = party;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        Case aCase = (Case) o;

        return new EqualsBuilder()
                .append(caseno, aCase.caseno)
                .append(interfaceId, aCase.interfaceId)
                .append(deptId, aCase.deptId)
                .append(casetypeId, aCase.casetypeId)
                .append(accuseLawer, aCase.accuseLawer)
                .append(accusedLawer, aCase.accusedLawer)
                .append(casedate, aCase.casedate)
                .append(flag, aCase.flag)
                .append(casename, aCase.casename)
                .append(summary, aCase.summary)
                .append(casedesc, aCase.casedesc)
                .append(accuse, aCase.accuse)
                .append(accused, aCase.accused)
                .append(party, aCase.party)
                .isEquals();
    }

    public Case checkCase() {
        if (casetypeId != null && Objects.equals(casetypeId, "")) casetypeId = null;
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(caseno)
                .append(interfaceId)
                .append(deptId)
                .append(casetypeId)
                .append(accuseLawer)
                .append(accusedLawer)
                .append(casedate)
                .append(flag)
                .append(casename)
                .append(summary)
                .append(casedesc)
                .append(accuse)
                .append(accused)
                .append(party)
                .toHashCode();
    }

    @Override
    public String toString() {
        return "{" +
                "uid='" + uid + '\'' +
                ", caseno='" + caseno + '\'' +
                ", interfaceId='" + interfaceId + '\'' +
                ", deptId='" + deptId + '\'' +
                ", casetypeId='" + casetypeId + '\'' +
                ", accuseLawer='" + accuseLawer + '\'' +
                ", accusedLawer='" + accusedLawer + '\'' +
                ", casedate=" + casedate +
                ", flag=" + flag +
                ", casename='" + casename + '\'' +
                ", summary='" + summary + '\'' +
                ", casedesc='" + casedesc + '\'' +
                ", accuse='" + accuse + '\'' +
                ", accused='" + accused + '\'' +
                ", party='" + party + '\'' +
                '}';
    }
}