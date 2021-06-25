package com.hxht.autodeploy.court.statistics

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.Resp
import com.hxht.techcrt.court.*
import com.hxht.techcrt.enums.RespType
import com.hxht.techcrt.utils.DateUtils
import org.grails.web.json.JSONObject

import java.text.NumberFormat

/**
 * 图表生成   法庭统计和部门统计自上而下升序排列
 */
class ChartsController {

    def index() {
        //获取当前年份
        int currentYear = Calendar.getInstance().get(Calendar.YEAR)
        List<Integer> yearList = new ArrayList<>()
        for (int i = currentYear; i >= 2011; i--) {
            yearList.add(i)
        }
        ["years": yearList, "current": currentYear]
    }

    /**
     * 获取每个法官每个月的庭审数据
     */
    def getChartsInfoByJudge() {
        def judgesVo = ""
        //获取所有法官
        def judgesPo = Employee.findAllByPosition(2)
        //获取当前年份
        def currentYear = params.get("year") as String
        for (int i = 0; i < judgesPo.size(); i++) {
            //获取此法官
            def judge = judgesPo[i]
            //一条具体信息---->到前端按照、分割开
            def info = judge.name + "、"
            //检查其全年的数量
            def numsThisJudge = TrialInfo.countByJudgeAndStartDateBetween(judge, DateUtil.parse(currentYear + "-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"), DateUtil.parse(((currentYear as Integer) + 1) + "-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"))
            if (numsThisJudge == 0) {
                info = info + "0、0、0、0、0、0、0、0、0、0、0、0"
            } else {
                //此法官所有排期---->按月份分开
                for (int j = 1; j <= 12; j++) {
                    //开始时间
                    def startTime = DateUtils.handleMonth(j)
                    //结束时间
                    def endTime = DateUtils.handleMonth(j + 1)
                    def trialPerMonth
                    //01-11月庭审数据---->按照开庭时间进行统计
                    if (j != 12) {
                        trialPerMonth = TrialInfo.countByJudgeAndStartDateBetween(judge, DateUtil.parse(currentYear + "-" + startTime + "-01 00:00:00", "yyyy-MM-dd HH:mm:ss"), DateUtil.parse(currentYear + "-" + endTime + "-01 00:00:00", "yyyy-MM-dd HH:mm:ss"))
                        //12月庭审数据
                    } else {
                        trialPerMonth = TrialInfo.countByJudgeAndStartDateBetween(judge, DateUtil.parse(currentYear + "-" + startTime + "-01 00:00:00", "yyyy-MM-dd HH:mm:ss"), DateUtil.parse(((currentYear as Integer) + 1) + "-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"))
                    }
                    //本月没有数据
                    if (trialPerMonth == 0) {
                        info = info + "0"
                        //本月有数据
                    } else {
                        info = info + (trialPerMonth as String)
                    }
                    //非最后数据按照、分割
                    if (j != 12) {
                        info = info + "、"
                    }
                }
            }
            //获取完一个用户---->填充返回值
            if (judgesVo == "") {
                judgesVo = judgesVo + info
            } else {
                judgesVo = judgesVo + "," + info
            }
        }
        render Resp.toJson(RespType.SUCCESS, judgesVo)
    }

    /**
     * 获取每个法庭每个月的庭审数
     */
    def getChartsInfoByCourt() {
        //返回前端的数据
        def courtsVo = ""
        //获取所有法庭
        def courtsPo = Courtroom.findAll()
        List<String> stringList = new ArrayList<>()
        //获取当前年份
        def currentYear = params.get("year") as String
        //遍历所有法庭
        for (int i = 0; i < courtsPo.size(); i++) {
            //获取其中的一个法庭
            def court = courtsPo[i]
            //每一个法庭的具体信息
            def info = court.name + "、"
            //按照月份查询每个月的庭审数量
            //这个部门每年的数量
            int numPerYear = 0
            for (int j = 1; j <= 12; j++) {
                //开始时间
                def startTime = DateUtils.handleMonth(j)
                //结束时间
                def endTime = DateUtils.handleMonth(j + 1)
                def trialPerMonth
                if (j != 12) {
                    trialPerMonth = TrialInfo.countByCourtroomAndStartDateBetween(court, DateUtil.parse(currentYear + "-" + startTime + "-01 00:00:00", "yyyy-MM-dd HH:mm:ss"), DateUtil.parse(currentYear + "-" + endTime + "-01 00:00:00", "yyyy-MM-dd HH:mm:ss"))
                    //12月庭审数据
                } else {
                    trialPerMonth = TrialInfo.countByCourtroomAndStartDateBetween(court, DateUtil.parse(currentYear + "-" + startTime + "-01 00:00:00", "yyyy-MM-dd HH:mm:ss"), DateUtil.parse(((currentYear as Integer) + 1) + "-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"))
                }
                //本月没有数据
                if (trialPerMonth == 0) {
                    info = info + "0"
                    //本月有数据
                } else {
                    info = info + (trialPerMonth as String)
                }
                //非最后数据按照、分割
                if (j != 12) {
                    info = info + "、"
                }
                numPerYear = numPerYear + trialPerMonth
            }
            info = info + "、" + numPerYear
            //现在是直接添加到deptsVo中，现在我们不直接添加到deptsVo中了，我们添加到ArrayList中
            if (stringList.size() == 0) {
                stringList.add(info)
            } else {
                boolean flag = false
                for (int j = 0; j < stringList.size(); j++) {
                    //将这个字符串按照、分割
                    def strArr = stringList[j].split("、")
                    //取第14个数据
                    Integer str14 = Integer.parseInt(strArr[13])
                    if (str14 < numPerYear) {
                        flag = true
                        stringList.add(j, info)
                        break
                    }
                }
                //循环完成没有添加进去
                if (!flag) {
                    stringList.add(info)
                }
            }
        }
        for (int i = stringList.size() - 1; i >= 0; i--) {
            if (courtsVo == "") {
                courtsVo = courtsVo + stringList.get(i)
            } else {
                courtsVo = courtsVo + "," + stringList.get(i)
            }
        }
        render Resp.toJson(RespType.SUCCESS, courtsVo)
    }

    /**
     * 获取每个案件每个月的庭审数
     */
    def getChartsInfoByCase() {
        //返回到视图的数据
        def casesVo = ""
        //获取当前年份
        def currentYear = params.get("year") as String
        for (int i = 1; i <= 12; i++) {
            //开始时间
            def startTime = DateUtils.handleMonth(i)
            //结束时间
            def endTime = DateUtils.handleMonth(i + 1)
            List<CaseInfo> cases = new ArrayList<>()
            //根据开庭时间获取本月所有庭审
            def trialPerMonth
            if (i != 12) {
                trialPerMonth = TrialInfo.findAllByEndDateBetween(DateUtil.parse(currentYear + "-" + startTime + "-01 00:00:00", "yyyy-MM-dd HH:mm:ss"), DateUtil.parse(currentYear + "-" + endTime + "-01 00:00:00", "yyyy-MM-dd HH:mm:ss"))
            } else {
                trialPerMonth = TrialInfo.findAllByEndDateBetween(DateUtil.parse(currentYear + "-" + startTime + "-01 00:00:00", "yyyy-MM-dd HH:mm:ss"), DateUtil.parse(((currentYear as Integer) + 1) + "-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss"))
            }
            for (TrialInfo trial : trialPerMonth) {
                cases.add(trial.planInfo.caseInfo)
            }
            //去重
            HashSet h = new HashSet(cases)
            cases.clear()
            cases.addAll(h)
            if (casesVo == "") {
                casesVo = casesVo + cases.size()
            } else {
                casesVo = casesVo + "、" + cases.size()
            }
        }
        //返回一个12个数据拼接成的数据串
        render Resp.toJson(RespType.SUCCESS, casesVo)
    }

    /**
     * 获取每个部门每个月的庭审数(按照开始庭审时间计算)
     */
    def getChartsInfoByDept() {
        //返回到视图的数据
        def deptsVo = ""
        //获取所有部门
        def deptsPo = Department.findAll()
        List<String> stringList = new ArrayList<>()
        //获取当前年份
        def currentYear = params.get("year") as String
        //遍历部门
        for (int i = 0; i < deptsPo.size(); i++) {
            //获取此部门
            def dept = deptsPo[i]
            //获取此部门的所有人---->我们需要的只是法官和书记员
            def emps = Employee.findAllByDept(dept)
            //所有法官
            List<Employee> judgesPo = new ArrayList<>()
            //所有书记员
            List<Employee> secretarysPo = new ArrayList<>()
            for (Employee emp : emps) {
                //法官
                if (emp.position == 2) {
                    judgesPo.add(emp)
                }
                //书记员
                if (emp.position == 6) {
                    secretarysPo.add(emp)
                }
            }
            //---------------------到这儿将此部门中的法官和书记员分配到了两个list中-----------------------------
            //根据书记员或者法官将查询到的所有的庭审都放置到一个List中
            List<TrialInfo> trialPerDept = new ArrayList<>()
            for (Employee judge : judgesPo) {
                //获取所有与庭审
                List<TrialInfo> trials = TrialInfo.findAllByJudge(judge)
                if (trials.size() != 0) {
                    trialPerDept.addAll(trials)
                }
            }
            for (Employee secretary : secretarysPo) {
                //获取所有与庭审
                List<TrialInfo> trials = TrialInfo.findAllBySecretary(secretary)
                if (trials.size() != 0) {
                    trialPerDept.addAll(trials)
                }
            }
            //去重
            HashSet h = new HashSet(trialPerDept)
            trialPerDept.clear()
            trialPerDept.addAll(h)
            //获取到此了部门的所有的庭审---->同时有了这个部门的名称和12个月的总数再循环遍历月份进行取值
            def trialPerDeptVo = dept.name + "、"
            int numPerYear = 0
            for (int j = 1; j <= 12; j++) {
                //开始时间
                def startTime = DateUtils.handleMonth(j)
                //结束时间
                def endTime = DateUtils.handleMonth(j + 1)
                int countPerMonth = 0
                //遍历这个部门的所有庭审，我们根据时间区间进行相加如果在这个时间内,减少遍历时间倒着遍历，遍历完进行删除
                for (int k = 0; k < trialPerDept.size(); k++) {
                    //将时间字符串修改为时间格式---->开始时间和结束时间
                    def startDate
                    def endDate
                    if (j != 12) {
                        startDate = DateUtil.parse(currentYear + "-" + startTime + "-01 00:00:00", "yyyy-MM-dd HH:mm:ss")
                        endDate = DateUtil.parse(currentYear + "-" + endTime + "-01 00:00:00", "yyyy-MM-dd HH:mm:ss")
                    } else {
                        startDate = DateUtil.parse(currentYear + "-" + startTime + "-01 00:00:00", "yyyy-MM-dd HH:mm:ss")
                        endDate = DateUtil.parse(((currentYear as Integer) + 1) + "-01-01 00:00:00", "yyyy-MM-dd HH:mm:ss")
                    }
                    //--------------->到此得到了时间区间，下面进行判断
                    //得到此庭审
                    def trial = trialPerDept[k]
                    //按照开庭时间统计
                    if (trial.startDate >= startDate && trial.startDate < endDate) {
                        countPerMonth++
                    }
                }
                //所有庭审循环完毕就得到了本月的数据。。。
                //本月没有数据
                if (countPerMonth == 0) {
                    trialPerDeptVo = trialPerDeptVo + "0"
                    //本月有数据
                } else {
                    trialPerDeptVo = trialPerDeptVo + (countPerMonth as String)
                }
                //非最后数据按照、分割
                if (j != 12) {
                    trialPerDeptVo = trialPerDeptVo + "、"
                }
                numPerYear = numPerYear + countPerMonth
            }
            trialPerDeptVo = trialPerDeptVo + "、" + numPerYear
            //现在是直接添加到deptsVo中，现在我们不直接添加到deptsVo中了，我们添加到ArrayList中
            if (stringList.size() == 0) {
                stringList.add(trialPerDeptVo)
            } else {
                boolean flag = false
                for (int j = 0; j < stringList.size(); j++) {
                    //将这个字符串按照、分割
                    def strArr = stringList[j].split("、")
                    //取第14个数据
                    Integer str14 = Integer.parseInt(strArr[13])
                    if (str14 < numPerYear) {
                        flag = true
                        stringList.add(j, trialPerDeptVo)
                        break
                    }
                }
                //循环完成没有添加进去
                if (!flag) {
                    stringList.add(trialPerDeptVo)
                }
            }
        }
        for (int i = stringList.size() - 1; i >= 0; i--) {
            if (deptsVo == "") {
                deptsVo = deptsVo + stringList.get(i)
            } else {
                deptsVo = deptsVo + "," + stringList.get(i)
            }
        }
        render Resp.toJson(RespType.SUCCESS, deptsVo)
    }

    /**
     * 获取从开始时间往后一周的语音识别率统计
     */
    def getStatisSpeech() {
        //获取当前时间
        if (!params.startDate) {
            render Resp.toJson(RespType.SUCCESS)
            return
        }
        def startDate = params.date("startDate", "yyyy/MM/dd")
        def endDate1 = DateUtil.offsetDay(startDate, 1)
        def endDate2 = DateUtil.offsetDay(startDate, 2)
        def endDate3 = DateUtil.offsetDay(startDate, 3)
        def endDate4 = DateUtil.offsetDay(startDate, 4)
        def endDate5 = DateUtil.offsetDay(startDate, 5)
        def list = []
        def courtRoomList = Courtroom.findAll()
        for (def courtroom : courtRoomList) {
            def map = new JSONObject()
            map.put("courtRoomName", courtroom.name)
            def map1 = this.speechCount(map, startDate, endDate1, courtroom, "trialNum1", "speechNum1", "speechPercent1")
            def map2 = this.speechCount(map1, endDate1, endDate2, courtroom, "trialNum2", "speechNum2", "speechPercent2")
            def map3 = this.speechCount(map2, endDate2, endDate3, courtroom, "trialNum3", "speechNum3", "speechPercent3")
            def map4 = this.speechCount(map3, endDate3, endDate4, courtroom, "trialNum4", "speechNum4", "speechPercent4")
            def map5 = this.speechCount(map4, endDate4, endDate5, courtroom, "trialNum5", "speechNum5", "speechPercent5")

            def trialNumAll = Integer.parseInt(map1.get("trialNum1")) + Integer.parseInt(map1.get("trialNum2")) +
                    Integer.parseInt(map1.get("trialNum3")) + Integer.parseInt(map1.get("trialNum4")) + Integer.parseInt(map1.get("trialNum5"))
            def speechNumAll = Integer.parseInt(map1.get("speechNum1")) + Integer.parseInt(map1.get("speechNum2")) +
                    Integer.parseInt(map1.get("speechNum3")) + Integer.parseInt(map1.get("speechNum4")) + Integer.parseInt(map1.get("speechNum5"))
            map5.put("trialNumAll", trialNumAll.toString())
            map5.put("speechNumAll", speechNumAll.toString())
            def numberFormat = NumberFormat.getInstance()
            // 设置精确到小bai数点后2位
            numberFormat.setMaximumFractionDigits(2)
            def result
            if (trialNumAll == 0) {
                result = "0"
                map5.put("speechPercentAll", result + "%")
            } else {
                result = numberFormat.format((float) speechNumAll / (float) trialNumAll * 100)
                map5.put("speechPercentAll", result + "%")
            }
            list.add(map5)
        }
        def model = new JSONObject()
        model.put("rows", list)
        render(model)
    }

    /**
     * 获取从开始时间往后一周的语音识别率统计日期获取
     */
    def getStatisSpeechDate() {
        //获取当前时间
        if (!params.startDate) {
            render Resp.toJson(RespType.SUCCESS)
            return
        }
        def startDate = params.date("startDate", "yyyy/MM/dd")
        def endDate1 = DateUtil.offsetDay(startDate, 1)
        def endDate2 = DateUtil.offsetDay(startDate, 2)
        def endDate3 = DateUtil.offsetDay(startDate, 3)
        def endDate4 = DateUtil.offsetDay(startDate, 4)
        def model = new JSONObject()
        model.put("trialDate1", DateUtils.dateToWeek(DateUtil.format(startDate as Date, "yyyy-MM-dd")) + "（" + DateUtil.format(startDate, "yyyy-MM-dd") + "）")
        model.put("trialDate2", DateUtils.dateToWeek(DateUtil.format(endDate1 as Date, "yyyy-MM-dd")) + "（" + DateUtil.format(endDate1, "yyyy-MM-dd") + "）")
        model.put("trialDate3", DateUtils.dateToWeek(DateUtil.format(endDate2 as Date, "yyyy-MM-dd")) + "（" + DateUtil.format(endDate2, "yyyy-MM-dd") + "）")
        model.put("trialDate4", DateUtils.dateToWeek(DateUtil.format(endDate3 as Date, "yyyy-MM-dd")) + "（" + DateUtil.format(endDate3, "yyyy-MM-dd") + "）")
        model.put("trialDate5", DateUtils.dateToWeek(DateUtil.format(endDate4 as Date, "yyyy-MM-dd")) + "（" + DateUtil.format(endDate4, "yyyy-MM-dd") + "）")
        render(model)
    }

    def speechCount(def map, def startDate, def endDate, def courtroom, def trialNum, def speechNum, def speechPercent) {
        //第一天开庭数量
        def sizeNum = TrialInfo.countByCourtroomAndStartDateGreaterThanAndStartDateLessThan(courtroom, startDate, endDate)
        map.put(trialNum, sizeNum.toString())
        //第一天语音识别数量
        def speechSizeNum = TrialInfo.countByCourtroomAndStartDateGreaterThanAndStartDateLessThanAndisCourtSpeech(courtroom, startDate, endDate, 1)
        map.put(speechNum, speechSizeNum.toString())
        def numberFormat = NumberFormat.getInstance()
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(2)
        def result
        if (sizeNum == 0) {
            result = "0"
            map.put(speechPercent, result)
        } else {
            result = numberFormat.format((float) speechSizeNum / (float) sizeNum * 100)
            map.put(speechPercent, result + "%")
        }
        return map
    }
}
