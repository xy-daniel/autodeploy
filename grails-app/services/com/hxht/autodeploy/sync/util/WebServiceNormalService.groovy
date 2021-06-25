package com.hxht.autodeploy.sync.util

import com.hxht.techcrt.Dict
import com.hxht.techcrt.User
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.TrialInfo
import grails.gorm.transactions.Transactional
import org.grails.web.json.JSONObject
import org.springframework.util.ObjectUtils

import java.text.SimpleDateFormat

@Transactional
class WebServiceNormalService {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    /**
     * 根据案号推测案件类型
     *
     * @param archives 案号
     * @return 案件类别
     */
    String getCategoryByArchives(String archives) {
        //可能是刑事案件 但是需要进一步判断
        String[] criminalArr = ["刑初", "刑二初", "刑监", "刑终", "刑再", "刑令", "刑复", "刑结",
                                "刑医", "刑执", "刑他", "刑提", "刑申", "刑抗", "刑辖", "刑核",
                                "刑类推"]
        for (String criminal : criminalArr) {
            if (archives.contains(criminal)) {
                //肯定为刑事案件
                return "a1"
            }
        }
        //可能是民事案件 但是需要进一步判断
        String[] civilArr = ["民初", "民终", "民再", "民特", "民申", "民催", "民认", "民监", "民提", "民撤",
                             "民辖", "民他", "民算", "民破", "民督", "民保", "民结", "民清", "民抗", "商初"]
        for (String civil : civilArr) {
            if (archives.contains(civil)) {
                //肯定为民事案件
                return "a2"
            }
        }
        //可能是执行案件 但是需要进一步判断
        String[] executiveArr = ["执异", "执复", "执审", "执行", "执字", "执3号", "执保", "执他", "执协", "执督", "执监"]
        for (String executive : executiveArr) {
            if (archives.contains(executive)) {
                //肯定为执行案件
                return "a3"
            }
        }
        //可能是赔偿案件 但是需要进一步判断
        String[] indemnifyArr = ["赔初", "赔终", "委赔", "赔他", "法赔", "行赔", "法检赔", "法检赔"]
        for (String indemnify : indemnifyArr) {
            if (archives.contains(indemnify)) {
                //肯定为赔偿案件
                return "a4"

            }
        }
        //可能是行政案件 但是需要进一步判断
        String[] administrativeArr = ["行初", "行终", "行赔", "行审", "行申", "行再", "行监", "行他", "行抗", "行提", "行辖"]
        for (String administrative : administrativeArr) {
            if (archives.contains(administrative)) {
                //肯定为行政案件
                return "a5"
            }
        }
        //可能是信访案件 但是需要进一步判断
        String[] petitionArr = ["信字"]
        for (String petition : petitionArr) {
            if (archives.contains(petition)) {
                //肯定为信访案件
                return "a6"
            }
        }
        return "a7"
    }

    void pushCase(Long caseInfoId) {
        if (!caseInfoId){
            return
        }
        def aCase = CaseInfo.get(caseInfoId)
        def department = aCase.department
        def type = aCase.type
        List<JSONObject> syncCase = new ArrayList<>()
        JSONObject temp = new JSONObject()
        temp.put("uid", aCase.uid)
        temp.put("courtCode", Dict.findByCode("CURRENT_COURT").ext3)
        temp.put("archives", aCase.archives)
        temp.put("dept", department?department.uid:"")
        temp.put("name", aCase.name)
        //通过syncid字段传值推测的案件类型（ getCategoryByArchives方法返回值）
        temp.put("typeId", aCase.syncId? aCase.syncId : type?.code)
        temp.put("detail", aCase.detail)
        temp.put("accuse", aCase.accuser)
        temp.put("accuseLawer", aCase.prosecutionCounsel)
        temp.put("accused", aCase.accused)
        temp.put("accusedLawer", aCase.counselDefence)
        temp.put("party", "")
        if (!ObjectUtils.isEmpty(aCase.filingDate)) {
            temp.put("filingDate", simpleDateFormat.format(aCase.filingDate))
        }
        temp.put("status", aCase.active)
        temp.put("summary", aCase.caseCause)//老数据传值传的是案由所以此处传案由
        temp.put("active", 1)
        temp.put("rongjiUid", aCase.synchronizationId)
        syncCase.add(temp)
        PushHandler.pushCase(syncCase)
    }

    void pushPlan(Long planInfoId) {
        if (!planInfoId){
            return
        }
        def plan = PlanInfo.get(planInfoId)
        def courtroom = plan.courtroom
        def caseInfo = plan.caseInfo
        def judge = plan.judge
        def secretary = plan.secretary
        def collegial = plan.collegial
        String collegialString = ''
        List<JSONObject> syncPlan = new ArrayList<>()
        JSONObject temp = new JSONObject()
        temp.put("uid", plan?.uid)
        temp.put("courtRoomUid", courtroom?.uid)
        temp.put("caseUid", caseInfo?.uid)
        temp.put("judgeUserUid", judge?.uid)
        temp.put("courtCode", Dict.findByCode("CURRENT_COURT").ext3)
        temp.put("secretaryUserUid", secretary?.uid)
        if (!ObjectUtils.isEmpty(plan.startDate)) {
            temp.put("startDate", simpleDateFormat.format(plan.startDate))
        }
        if (!ObjectUtils.isEmpty(plan.endDate)) {
            temp.put("endDate", simpleDateFormat.format(plan.endDate))
        }
        if (collegial){
            for (def i = 0 ;i < collegial.size(); i++) {
                collegialString = collegialString + collegial.get(i).name
                if (collegial.size() > 1 && i < collegial.size() - 1){
                    collegialString = collegialString + ","
                }
            }
        }
        temp.put("collegialPanel", collegialString)
        temp.put("supervisorUid", null)
        temp.put("status", plan.status)
        temp.put("allowPlay", plan.allowPlay)
        temp.put("active", plan.active)
        temp.put("rongjiUid", plan.synchronizationId)
        syncPlan.add(temp)
        PushHandler.pushPlan(syncPlan)
    }

    void pushTrial(Long trialInfoId) {
        if (!trialInfoId){
            return
        }
        def trial = TrialInfo.get(trialInfoId)
        def planInfo = trial.planInfo
        def judge = trial.judge
        def secretary = trial.secretary
        List<JSONObject> syncTrial = new ArrayList<>()
        JSONObject object = new JSONObject()
        object.put("uid", trial.uid)
        object.put("courtCode", Dict.findByCode("CURRENT_COURT").ext3)
        object.put("planUid", planInfo.uid)
        object.put("judgeUserUid", judge?.uid)
        object.put("secretaryUserUid", secretary?.uid)
        if (trial.startDate){
            object.put("startDate", simpleDateFormat.format(trial.startDate))
        }else{
            object.put("startDate", null)
        }
        if (trial.endDate){
            object.put("endDate", simpleDateFormat.format(trial.endDate))
        }else{
            object.put("endDate", null)
        }
        object.put("restDate", null)
        object.put("archDate", null)
        object.put("courtrec", null)
        object.put("status", trial.status)
        object.put("active", "1")
        syncTrial.add(object)
        PushHandler.pushTrial(syncTrial)
    }

    void pushUser(List<Employee> employeeList) {
        for (Employee employee:employeeList){
            def user = User.findByEmployee(employee.id)
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            List<JSONObject> list = new ArrayList<>()
            JSONObject jsonObject = new JSONObject()
            jsonObject.put("uid", employee.uid)
            jsonObject.put("courtCode", Dict.findByCode("CURRENT_COURT").ext3)
            jsonObject.put("username", user.username)
            jsonObject.put("password", user.password)
            jsonObject.put("regTime", simpleDateFormat.format(user.dateCreated))
            jsonObject.put("realName", employee.name)
            jsonObject.put("rongjiUid", employee.synchronizationId)
            jsonObject.put("accountLocked", "true")
            list.add(jsonObject)
            PushHandler.pushUser(list)
        }

    }

}
