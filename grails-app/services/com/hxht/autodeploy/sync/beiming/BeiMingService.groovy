package com.hxht.autodeploy.sync.beiming

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.CollegialType
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.PositionStatus
import com.hxht.techcrt.User
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.CaseType
import com.hxht.techcrt.court.Collegial
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.Department
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.manager.SystemController
import com.hxht.techcrt.utils.PinYinUtils
import com.hxht.techcrt.utils.UUIDGenerator
import com.hxht.techcrt.utils.http.HttpUtil
import grails.gorm.transactions.Transactional

class BeiMingService {
   
   /**
    * 通过北明排期接口，获取排期。
    */
   def getPlanForBeiMing(){
      try{
         //获取北明接口一个星期的排期数据
         def date = DateUtil.beginOfDay(new Date()) //获取今日的0点
         def start = DateUtil.format(date, "yyyyMMddHHmmss")//当天时间的0点
         def end = DateUtil.format(DateUtil.offsetDay(date, 7), "yyyyMMddHHmmss") //结束时间为七天后
         
         //请求的参数
         def paramMap = [:]
         paramMap.put("pageSize", 500)//每页条数
         paramMap.put("startTime", start)//开始时间
         paramMap.put("endTime", end)//结束时间
         paramMap.put("fydm", SystemController.currentCourt.val)//结束时间
         
         def i = 1
         while (i<100){
            paramMap.put("pageNo", i)
            def resultPlan = HttpUtil.getForBeiMing("http://146.0.2.92:8090/courtInfo/openCourtNew", paramMap)
             log.info("北明接口获取到的排期数据为：${resultPlan.toString()}")
            if (!resultPlan || !resultPlan.error || resultPlan.error == "null"){
               if (!resultPlan.value){
                   break
               }
               for (def plan: resultPlan.value){
                   def planInfo = PlanInfo.findBySynchronizationId(plan.id as String)
                   if (planInfo && plan.status != PlanStatus.PLAN){
                       continue
                   }
                   this.saveCaseAndPlan(plan)
               }
                if (resultPlan.value.size() < 500){
                    break
                }
                i++
            }else{
                log.error("同步北明数据错误[getPlanForBeiMing]，排期失败：${resultPlan}")
            }
         }
      }catch (e) {
         e.printStackTrace()
         log.error("同步北明数据错误[getPlanForBeiMing]，排期失败：${e.getMessage()}")
      }
       
   }
   
   /**
    * 通过北明排期接口，获取部门。
    */
    @Transactional
   def getDeptForBeiMing(){
      try{
         //请求的参数
         def paramMap = [:]
         paramMap.put("fydm", SystemController.currentCourt.val)//结束时间
         
         def resultDept = HttpUtil.getForBeiMing("http://146.0.2.92:8090/courtInfo/getDeptInfo", paramMap)
          log.info("北明接口获取到的部门数据为：${resultDept.toString()}")
         if (resultDept.status == 200){
            if (resultDept.data.count > 0 ){
               for (def dept: resultDept.data.list){
                  if (!(dept.state == "0")){//状态（0停用1启用2自建）
                     def department = Department.findByName(dept.deptName)
                     //如果部门名称不存在
                     if (!department){
                        department = new Department()
                        department.uid = UUIDGenerator.nextUUID()
                     }
                     department.synchronizationId = dept.deptGuid
                     department.parent = Department.findBySynchronizationId(dept.deptParentGuid)
                     department.name = dept.deptName
                     department.save()
                     if (department.hasErrors()) {
                         log.error("同步北明数据保存部门错误[BeiMingService.getDeptForBeiMing]，入库部门数据出错：${department.errors}")
                     }
                  }
               }
            }
         }else{
            log.error("同步北明数据错误[getDeptForBeiMing]，获取部门失败 返回的错误信息：${resultDept.msg}")
         }
      }catch (e) {
         e.printStackTrace()
         log.error("同步北明数据错误[getDeptForBeiMing]，获取部门失败：${e.getMessage()}")
      }
   }


   /**
    * 通过北明排期接口，获取人员。
    */
    @Transactional
   def getUserForBeiMing(){
      try{
         //请求的参数
         def paramMap = [:]
         paramMap.put("fydm", SystemController.currentCourt.val)//结束时间

         def resultUser = HttpUtil.getForBeiMing("http://146.0.2.92:8090/courtInfo/getUserInfo", paramMap)
//         def resultUser = new JSONObject(HttpUtil.result)
          log.info("北明接口获取到的人员数据为：${resultUser.toString()}")
         if (resultUser.status == 200){
            if (resultUser.data.count > 0 ){
               for (def user: resultUser.data.list){
                  if (!(user.state == "0")){//状态（0停用1启用2自建）
                     def employee = Employee.findByName(user.displayName)
                     //如果姓名不存在
                     if (!employee){
                        employee = new Employee()
                        employee.uid = UUIDGenerator.nextUUID()
                     }
                     employee.synchronizationId = user.userGuid
                     employee.name = user.displayName
                     String fyzw = user.fyzw
                     if (fyzw){
                         if (fyzw.equals("法官") || fyzw.contains("审判长") || fyzw.contains("承办")){
                             employee.position = PositionStatus.JUDGE
                         }else if (fyzw.contains("司法警察") ){
                             employee.position = PositionStatus.POLICE
                         }else if (fyzw.contains("书记员") || fyzw.contains("笔录员")){
                             employee.position = PositionStatus.SECRETARY
                         }else{
                             employee.position = PositionStatus.OTHER
                         }
                     }
                      
                     employee.dept = Department.findBySynchronizationId(user.deptGuid)
                      employee.save()
                      if (employee.hasErrors()) {
                          log.error("同步北明数据保存人员错误[BeiMingService.saveEmployee]，入库部门数据出错：${employee.errors}")
                      }
                      
                      //为这名员工创建账号
                      def username = PinYinUtils.getHanziPinYin(employee.name)
                      if (username?.contains("lu:")) {
                          username = username.replace("lu:", "lv")
                      }
                      if (User.findByUsername(username)) {
                          continue
                      }
                      def userSys = new User()
                      userSys.uid = UUIDGenerator.nextUUID()
                      userSys.enabled = true //账号启用
                      userSys.accountExpired = false //账号过期
                      userSys.accountLocked = false//账号锁定
                      userSys.username = username
                      userSys.password = '123456'//默认初始密码
                      userSys.realName = employee.name//姓名
                      userSys.employee = employee.id
                      userSys.save(flush: true)
                      if (userSys.hasErrors()) {
                          log.error("同步北明数据保存人员登录账号错误[BeiMingService.saveUser]，入库部门数据出错：${userSys.errors}")
                      }
                  }
               }
            }
         }else{
            log.error("同步北明数据错误[getuserForBeiMing]，获取部门失败 返回的错误信息：${resultUser.msg}")
         }
      }catch (e) {
         e.printStackTrace()
         log.error("同步北明数据错误[getuserForBeiMing]，获取部门失败：${e.getMessage()}")
      }
   }

    /**
     * 通过北明排期接口，获取案件所属当事人。
     */

    def getCurrentPersonForBeiMing(String ajid){
        try{
            //请求的参数
            def paramMap = [:]
            paramMap.put("ajid", ajid)//请求的案件所属id
            paramMap.put("fydm", SystemController.currentCourt.val)//结束时间
            def resultUser = HttpUtil.getForBeiMing("http://146.0.2.92:8090/courtInfo/getCurrentPerson", paramMap)
            log.info("北明接口根据案件所属id为-------${ajid}------获取到的案件当时人数据为：${resultUser.toString()}")
            return resultUser
            
        }catch (e) {
            e.printStackTrace()
            log.error("同步北明数据错误[getuserForBeiMing]，获取部门失败：${e.getMessage()}")
        }
    }

    /**
     * 保存排期和案件
     * @param plan
     * @return
     */
    @Transactional
    def saveCaseAndPlan(def plan){
        def caseInfo = CaseInfo.findByArchives(plan.ah)
        if (!caseInfo){
            caseInfo = new CaseInfo()
            caseInfo.uid = UUIDGenerator.nextUUID()
            caseInfo.archives = plan.ah
        }
        caseInfo.synchronizationId = plan.ajid as String
        caseInfo.name = plan.jcxxInfo.laayName//将案由放置到案件名称当中
        /*******处理案件类型*************/
        def typename = plan.jcxxInfo.ajlbName
        def caseType
        if (!typename) {
            caseType = CaseType.first()
        } else {
            //根据typename查询，是否有相同类型
            caseType = CaseType.findByName(typename)
            if (!caseType) {
                caseType = CaseType.findByShortName(typename)
            }
            if (!caseType) {
                caseType = CaseType.findByNameLike("%${typename}%")
            }
            if (!caseType) {
                caseType = CaseType.first()
            }
        }
        caseInfo.type = caseType
        caseInfo.summary = plan.jcxxInfo.laayName// 案由放置案件备注中
        //获取案件当事人 （原告、被告）
        def resultCurrentPerson = this.getCurrentPersonForBeiMing(plan.ajid)
        def accuse = ""
        def accused = ""
        if (resultCurrentPerson){
            for (def currentPerson: resultCurrentPerson.list){
                if (currentPerson.dsrajdwmc == "原告" || currentPerson.dsrajdwmc == "执行人" ||
                        currentPerson.dsrajdwmc == "申请人" || currentPerson.dsrajdwmc == "上诉人"){
                    if (accuse){
                        accuse = accuse + "," + currentPerson.xm
                    }else{
                        accuse = currentPerson.xm
                    }
                }else if (currentPerson.dsrajdwmc == "被告" || currentPerson.dsrajdwmc == "被执行人" ||
                        currentPerson.dsrajdwmc == "被告人" || currentPerson.dsrajdwmc == "被申请人" ){
                    if (accused){
                        accused = accused + "," + currentPerson.xm
                    }else{
                        accused = currentPerson.xm
                    }
                }
            }
        }
        caseInfo.accuser = accuse
        caseInfo.accused = accused
        caseInfo.filingDate = DateUtil.parse(plan.jcxxInfo.larq + " 00:00:00","yyyy-MM-dd HH:mm:ss")
        caseInfo.active = DataStatus.SHOW
        caseInfo.department = Department.findByName(plan.jcxxInfo.cbbmbsName as String)
        caseInfo.caseCause = plan.jcxxInfo.laayName
        caseInfo.save()
        if (caseInfo.hasErrors()) {
            log.error("同步北明数据保存部门错误[BeiMingService.savecaseInfo]，入库部门数据出错：${caseInfo.errors}")
        }

        def planInfo = PlanInfo.findBySynchronizationId(plan.id)
        if (!planInfo){
            planInfo = new PlanInfo()
            planInfo.uid = UUIDGenerator.nextUUID()
        }
        planInfo.synchronizationId = plan.id
        planInfo.courtroom = Courtroom.findByName(plan.ftbsmc as String)

        List<Collegial> collegialList = []
        for (def drjs: plan.jcxxInfo.drjs){
            def personRole = drjs.personRole as String
            if (personRole){
                switch (personRole){
                    case "审判长":
                        planInfo.judge = Employee.findByName(drjs.personName as String)
                        def collegial = Collegial.findByNameAndType(drjs.personName as String, CollegialType.PERSIDING_JUDGE)
                        if (!collegial) {
                            collegial = new Collegial(
                                    name: drjs.personName as String,
                                    type: CollegialType.PERSIDING_JUDGE,//审判长角色
                            ).save()
                            if (collegial.hasErrors()){
                                log.error("同步北明数据保存合议庭出错：${collegial.errors}")
                            }
                        }
                        collegialList.add(collegial)
                        break
                    case "审判员":
                        def collegial = Collegial.findByNameAndType(drjs.personName as String, CollegialType.JUDGE)
                        if (!collegial) {
                            collegial = new Collegial(
                                    name: drjs.personName as String,
                                    type: CollegialType.JUDGE,//审判员角色
                            ).save()
                            if (collegial.hasErrors()){
                                log.error("同步北明数据保存合议庭出错：${collegial.errors}")
                            }
                        }
                        collegialList.add(collegial)
                        break
                    case "人民陪审员":
                        def collegial = Collegial.findByNameAndType(drjs.personName as String, CollegialType.PEOPLE_ASSESSOR)
                        if (!collegial) {
                            collegial = new Collegial(
                                    name: drjs.personName as String,
                                    type: CollegialType.PEOPLE_ASSESSOR,
                            ).save()
                            if (collegial.hasErrors()){
                                log.error("同步北明数据保存合议庭出错：${collegial.errors}")
                            }
                        }
                        collegialList.add(collegial)
                        break
                    default:
                        def collegial = Collegial.findByNameAndType(drjs.personName as String,CollegialType.OTHER)
                        if (!collegial) {
                            collegial = new Collegial(
                                    name: drjs.personName as String,
                                    type: CollegialType.OTHER,
                            ).save()
                            if (collegial.hasErrors()){
                                log.error("同步北明数据保存合议庭出错：${collegial.errors}")
                            }
                        }
                        collegialList.add(collegial)
                        break
                }
            }

        }

        planInfo.undertake = Employee.findByName(plan.cbfg as String)
        planInfo.collegial = collegialList
        planInfo.secretary = Employee.findBySynchronizationId(plan.sjyid as String)?
                Employee.findBySynchronizationId(plan.sjyid as String):Employee.findByName(plan.sjy as String)
        planInfo.startDate = plan.yjkssj? DateUtil.parse(plan.yjkssj as String , "yyyyMMddHHmmss") : null
        planInfo.endDate = plan.yjjssj? DateUtil.parse(plan.yjjssj as String , "yyyyMMddHHmmss") : null
        planInfo.active = DataStatus.SHOW
        planInfo.caseInfo = caseInfo
        planInfo.status = PlanStatus.PLAN
        planInfo.save()
        if (planInfo.hasErrors()){
            log.error("同步北明数据保存排期数据出错：${planInfo.errors}")
        }
    }
   
}
