package com.hxht.autodeploy.sync.rongji

import com.hxht.techcrt.*
import com.hxht.techcrt.court.*
import com.hxht.techcrt.court.admin.ToolBoxService
import com.hxht.techcrt.court.manager.info.DepartmentService
import com.hxht.techcrt.sync.DealWsXml
import com.hxht.techcrt.sync.util.WebServiceNormalService
import com.hxht.techcrt.utils.UUIDGenerator
import grails.core.GrailsApplication
import grails.events.EventPublisher
import grails.gorm.transactions.Transactional
import org.dom4j.Element

@Transactional
class RongJiService implements EventPublisher{

    GrailsApplication grailsApplication
    DepartmentService departmentService
    ToolBoxService toolBoxService
    WebServiceNormalService webServiceNormalService

    def startRongji() {

        def tempIp = DealWsXml.getCourtIpRongji(Dict.findByCode("CURRENT_COURT").ext3)
        if (!tempIp) {
            log.info("获取榕基的法庭IP为空,现在运行停止." + Dict.findByCode("CURRENT_COURT").ext3)
            return
        }
        //最优先获取webservice排期
        //ws获取批量排期
        def wsClient = RongJiUtilService.newInstance()
        def result = wsClient.callPlan("http://" + tempIp + "/" + grailsApplication.config.getProperty('syncData.rongji.url'), "getKtxxList")
        if (!result) {
            log.info("榕基接口定时任务获取排期,返回结果为空,本次结束.")
            return
        }
//        def result = testService.getStr()
//        log.info("福建榕基接口获取ws返回结果:" + result)
        def elements = AnalysisXml.analysisXml(result, "KTXX")
//        List<Employee> resultEmployeeList = new ArrayList<>()
        def caseTypeNo = null
        for (Element element : elements) {
            try {
                //1.构造case,判断case是否需要更新
                def tempCase = AnalysisXml.dealCase(element)
                if (!tempCase) {
                    log.info("case榕基接口案件解析为空,本条跳过.")
                    continue
                }
                //判断下案件类型（当前案件名判断案件类型--先根据老数据的summary字段再根据casename判断，都不存在则取案件类型第一条）
                def caseType = CaseType.findByNameLike("%${tempCase.getSummary()}%")
                if (!caseType){
                    caseType = getCaseTypeByCaseno(tempCase.getCaseno())
                    if (!caseType){
                        //此处标记案件类型未在本地找到
                        caseTypeNo = 1
                        log.info("榕基接口中根据接口中的案件类型++" + tempCase.getSummary() + "++和案号++" +
                                tempCase.getCaseno() + "都未能查找出对应的案号所以取值为案件类型表的第一个")
                        caseType = CaseType.findAll()?.get(0)
                    }
                }
                //案件同步id是否存在
                def keyCase = CaseInfo.findBySynchronizationId(tempCase.getInterfaceId())
                if (!keyCase){
                    log.info("根据同步Id：${tempCase.getInterfaceId()}查询旧数据CaseInfo不存在,现在根据案号查找")
                    //不存在则进行查看案号，因为案号不能重复
                    keyCase = CaseInfo.findByArchives(tempCase.getCaseno())
                    if (!keyCase){
                        log.info("根据案号：${tempCase.getCaseno()}没有查找到了对应的案件,现在新建一个CaseInfo")
                        keyCase = new CaseInfo()
                    }else{
                        log.debug("根据案号：${tempCase.getCaseno()}查找到了对应的案件")
                    }
                }else{
                    log.debug("根据${tempCase.getInterfaceId()}查询旧数据CaseInfo存在")
                }

                //部门不存在则进行插入部门
                def deptName = element.elementTextTrim("CBSPT")
                def department = Department.findByName(deptName)
                if (!department){
                    departmentService.addSave(deptName,null)
                    log.info("根据部门名称：${deptName}没有查找到对应的部门,现在新增此部门")
                }else{
                    log.debug("根据部门名称：${deptName},查找到了对应的部门")
                }

                keyCase.caseCause = element.elementTextTrim("AY")//数据同步用的案由
                keyCase.archives = tempCase.getCaseno()//案号
                keyCase.name = tempCase.getCasename()//案件名称
                keyCase.type = caseType//案件类型
                keyCase.accused = tempCase.getAccused()//被告
                keyCase.accuser = tempCase.getAccuse() //原告
                keyCase.filingDate = tempCase.getCasedate() //立案日期
                keyCase.synchronizationId = tempCase.getInterfaceId() //案件同步id
                keyCase.active = DataStatus.SHOW
               if (keyCase.id){
                   //案件信息改变的则向CMP平台推送数据并本地跟新
                   if (keyCase.hasChanged()){
                       keyCase.save(flush: true)
                       if (keyCase.hasErrors()) {
                           log.info("榕基接口保存存在的案件时出错keyCase.save caseInfo [${keyCase.errors}]")
                           throw new RuntimeException()
                       }
                       //如果案件类型不存在则进行推测 向CMP平台发送案件类型代字
                       if (caseTypeNo == 1){
                           String type = webServiceNormalService.getCategoryByArchives(tempCase.getCaseno())
                           log.info("榕基接口案件类型推测为: {$type}。用于向CMP平台转发"  )
                           keyCase.setSyncId(type)
                       }
                       //向CMP推送数据
                       this.notify("pushCmpCaseAndPlanAndTrial",keyCase.id,null,null)
                   }
               }else{
                   keyCase.uid = UUIDGenerator.nextUUID()
                   keyCase.save(flush: true)
                   if (keyCase.hasErrors()) {
                       log.info("榕基接口保存新增的案件时出错keyCase.save caseInfo [${keyCase.errors}]")
                       throw new RuntimeException()
                   }
                   //如果案件类型不存在则进行推测 向CMP平台发送案件类型代字
                   if (caseTypeNo == 1){
                       String type = webServiceNormalService.getCategoryByArchives(tempCase.getCaseno())
                       log.info("榕基接口案件类型推测为: {$type}。用于向CMP平台转发"  )
                       keyCase.setSyncId(type)
                   }
                   //向CMP推送数据
                   this.notify("pushCmpCaseAndPlanAndTrial",keyCase.id,null,null)
               }

                //2.构造plan,判断是否需要更新
                def tempPlan = AnalysisXml.dealPlan(element)
                if (tempPlan){
                    def roomName = element.element("FTSYJL").elementTextTrim("FTMC")
                    log.info("榕基排期接口获取开庭位置为: " + roomName + " .")
                    def courtRoom = Courtroom.findByName(roomName)
                    if (!courtRoom){
                        log.info("根据${roomName}没有查找到具体的法庭，现在准备去模糊查询")
                        def courtroomList = Courtroom.createCriteria().list {
                            like("name", "%${roomName}%")
                        } as List<Courtroom>
                        if (!courtroomList){
                            log.info("法庭依然不存在则进行选择法庭列表第一个")
                            courtRoom = Courtroom.findAll()?.get(0)
                            if (!courtRoom){
                                log.info("榕基排期接口未查找出任何法庭，本条排期同步可能会失败")
                            }
                        }else{
                            log.info("模糊查询到部分可能对应的法庭,长度为：${courtroomList.size()}")
                            if (courtroomList.size() == 1){
                                courtRoom = courtroomList.get(0)
                                log.info("找到了具体的法庭,法庭名称为：${courtRoom.name}")
                            }else{
                                log.info("模糊查到到多余一个的法庭，现在取字数最少的法庭，也即更具体")
                                courtRoom = courtroomList.get(0)
                                for (int i=1;i<courtroomList.size();i++){
                                    log.debug("当前选择的法庭名称为：${courtRoom.name},法庭名称长度为：${courtRoom.name.length()}")
                                    if (courtRoom.name.length() > courtroomList.get(i).name.length()){
                                        courtRoom = courtroomList.get(i)
                                    }
                                }
                                log.info("最终选择的法庭名称为：${courtRoom.name}")
                            }
                        }
                    }
                    //通过同步id判断是否需要同步
                    def keyPlan = PlanInfo.findBySynchronizationId(tempPlan.getInterfaceplanId())
                    if (!keyPlan){
                        log.info("根据同步Id：${tempPlan.getInterfaceplanId()},没有查询到对应的排期，现在新建一个排期")
                        keyPlan = new PlanInfo()
                    }
                    keyPlan.caseInfo = keyCase
                    keyPlan.courtroom = courtRoom
                    keyPlan.startDate = tempPlan.getStartDate()
                    keyPlan.endDate = tempPlan.getEndDate()
                    keyPlan.synchronizationId = tempPlan.getInterfaceplanId()

                    //处理法官
                    def judgeName = tempPlan.getJudgeName()
                    log.info("榕基数据排期法官姓名：${judgeName}")
                    if (judgeName && judgeName != "null"){
                        log.info("榕基数据排期法官姓名存在，现在根据姓名去查找对应的法官")
                        def judgeList = Employee.findAllByName(judgeName)
                        if (!judgeList){//不存在则进行插入处理
                            log.info("根据法官姓名：${judgeName},没有查找到对应的法官，现在新建一个法官")
                            def employee = new Employee()
                            employee.uid = UUIDGenerator.nextUUID()
                            employee.name = judgeName
                            employee.position = 2//法官
                            employee.dept = Department.findByName(deptName)
                            employee.synchronizationId = tempPlan.getJudgeCode()
                            employee.save(flush: true)
                            if (employee.hasErrors()) {
                                log.error("处理榕基对接接口+++保存不存在的人员（法官）出错------[${employee.errors}]")
                                throw new RuntimeException()
                            }
                            //创建登录用户
                            toolBoxService.importUser(employee)
//                            resultEmployeeList.add(employee)
                            keyPlan.judge = employee
                        }else{
                            log.info("根据法官姓名：${judgeName},查找到了对应的法官,可能对应的法官列表长度为：${judgeList.size()}")
                            if (judgeList.size() == 1) {
                                def tempJudge = judgeList.get(0)
                                if (!tempJudge.synchronizationId){
                                    tempJudge.synchronizationId = tempPlan.getJudgeCode()
                                    tempJudge.save(flush: true)
                                    if (tempJudge.hasErrors()) {
                                        log.error("榕基接口处理榕基对接接口+++保存存在的人员（法官）出错------[${tempJudge.errors}]")
                                        throw new RuntimeException()
                                    }
//                                    resultEmployeeList.add(tempJudge)
                                }
                                keyPlan.judge = tempJudge
                            } else {
                                def tempJudge = judgeList.get(0)
                                log.info("榕基接口案件排期 同步标识:" + tempPlan.getInterfaceplanId() + "." +
                                        "产生法官数据冲突,同步法官标识:" + tempPlan.getJudgeCode() + ".法官姓名:" + tempPlan.getJudgeName() +
                                        ".现在准备从数据库中取出的默认第一个法官进行赋值操作.tempJudge:" + tempJudge.id)
                                keyPlan.judge = tempJudge
                            }
                        }
                    }else {
                        log.info("榕基接口案件排期,榕基接口id: " + tempPlan.getInterfaceplanId() + " 没有法官,现在将会匹配数据库中默认用户管理员")
                        keyPlan.judge = Employee.findByName("管理员")
                    }

                    //处理排期中书记员数据
                    def secretaryName = tempPlan.getSecretaryName()
                    if (secretaryName && secretaryName != "null"){
                        def secretaryList = Employee.findAllByName(secretaryName)
                        if (!secretaryList){//不存在则进行插入处理
                            def employee = new Employee()
                            employee.uid = UUIDGenerator.nextUUID()
                            employee.name = secretaryName
                            employee.position = 6//书记员
                            employee.dept = Department.findByName(deptName)
                            employee.synchronizationId = tempPlan.getSecretaryCode()
                            employee.save(flush: true)
                            if (employee.hasErrors()) {
                                log.error("榕基接口处理榕基对接接口+++保存不存在的人员（书记员）出错------[${employee.errors}]")
                                throw new RuntimeException()
                            }
                            //创建登录用户
                            toolBoxService.importUser(employee)
//                            resultEmployeeList.add(employee)
                            keyPlan.secretary = employee
                        }else{
                            if (secretaryList.size() == 1) {
                                def tempsecretary = secretaryList.get(0)
                                if (!tempsecretary.synchronizationId){
                                    tempsecretary.synchronizationId = tempPlan.getSecretaryCode()
                                    tempsecretary.save(flush: true)
                                    if (tempsecretary.hasErrors()) {
                                        log.error("榕基接口处理榕基对接接口+++保存存在的人员（书记员）出错------[${tempsecretary.errors}]")
                                        throw new RuntimeException()
                                    }
//                                    resultEmployeeList.add(tempsecretary)
                                }
                                keyPlan.secretary = tempsecretary
                            } else {
                                def tempsecretary = secretaryList.get(0)
                                log.info("榕基接口案件排期 同步标识:" + tempPlan.getInterfaceplanId() + "." +
                                        "产生书记员数据冲突,同步书记员标识:" + tempPlan.getSecretaryCode() + ".书记员姓名:" + tempPlan.getSecretaryCode() +
                                        ".现在准备从数据库中取出的默认第一个书记员进行赋值操作.tempsecretary:" + tempsecretary.id)
                                keyPlan.secretary = tempsecretary
                            }
                        }
                    }else {
                        log.info("案件排期,榕基接口id: " + tempPlan.getInterfaceplanId() + " 没有书记员,现在将会匹配数据库中默认用户管理员")
                        keyPlan.secretary = Employee.findByName("管理员")
                    }

                    //合议庭成员
                    def collegialAll = element.elementTextTrim("HYTCY")
                    if (collegialAll && collegialAll != "null"){
                        if (!Collegial.findBySynchronizationId(tempPlan.getInterfaceplanId())){
                            def collegialArr = collegialAll.split(",")
                            def collegialList = []
                            for (def c : collegialArr) {
                                def collegial = new Collegial()
                                collegial.name = c
                                collegial.type = CollegialType.OTHER
                                collegial.synchronizationId = tempPlan.getInterfaceplanId()
                                collegial.save(flush: true)
                                if (collegial.hasErrors()) {
                                    log.info("榕基接口保存排期时出错PlanService.save collegial [${collegial.errors}]")
                                    throw new RuntimeException()
                                }
                                collegialList.add(collegial)
                            }
                            keyPlan.collegial = collegialList
                        }
                    }

                    //处理承办人
                    def undertakeName = element.elementTextTrim("CBR")
                    //承办人编号
                    def cbrbs = element.elementTextTrim("CBRBS")
                    if (undertakeName){
                        def undertakeList = Employee.findAllByName(undertakeName)
                        if (!undertakeList){//不存在则进行插入处理
                            def employee = new Employee()
                            employee.uid = UUIDGenerator.nextUUID()
                            employee.name = undertakeName
                            employee.position = 255//承办人
                            employee.dept = Department.findByName(deptName)
                            employee.synchronizationId = cbrbs
                            employee.save(flush: true)
                            if (employee.hasErrors()) {
                                log.error("处理榕基对接接口+++保存不存在的人员（承办人）出错------[${employee.errors}]")
                                throw new RuntimeException()
                            }
                            //创建登录用户
                            toolBoxService.importUser(employee)
//                            resultEmployeeList.add(employee)
                            keyPlan.undertake = employee
                        }else{
                            if (undertakeList.size() == 1) {
                                def tempundertake = undertakeList.get(0)
                                if (!tempundertake.synchronizationId){
                                    tempundertake.synchronizationId = cbrbs
                                    tempundertake.save(flush: true)
                                    if (tempundertake.hasErrors()) {
                                        log.error("处理榕基对接接口+++保存存在的人员（承办人）出错------[${tempundertake.errors}]")
                                        throw new RuntimeException()
                                    }
                                }
                                keyPlan.undertake = tempundertake
                            } else {
                                def tempundertake = undertakeList.get(0)
                                log.info("榕基接口案件排期 同步标识:" + tempPlan.getInterfaceplanId() + "." +
                                        "产生承办人数据冲突,同步承办人标识:" + cbrbs + ".承办人姓名:" + undertakeName +
                                        ".现在准备从数据库中取出的默认第一个承办人进行赋值操作.tempundertake:" + tempundertake.id)
                                keyPlan.undertake = tempundertake
                            }
                        }
                    }else {
                        log.info("案件排期,榕基接口id: " + tempPlan.getInterfaceplanId() + " 没有承办人,现在将会匹配数据库中默认用户管理员")
                        keyPlan.undertake = Employee.findByName("管理员")
                    }

                    //判断本地是否存在此排期
                    if (keyPlan.id){
                        //只更新本地为排期状态的排期
                        if (keyPlan.status == PlanStatus.PLAN){
                            if (keyPlan.hasChanged()){
                                //列出所有脏读属性 并查看脏读属性是否有变化 没有变化则向CMP推送并更新
                                def listProperty = keyPlan.listDirtyPropertyNames()
                                for (int pro = 0; pro < listProperty.size(); pro++) {
                                    if (keyPlan.(listProperty.get(pro)).hasChanged()){
                                        keyPlan.save(flush: true)
                                        if (keyPlan.hasErrors()){
                                            log.error("处理榕基对接接口+++保存存在的排期出错------[${keyPlan.errors}]")
                                            throw new RuntimeException()
                                        }
                                        //向CMP推送数据
                                        this.notify("pushCmpCaseAndPlanAndTrial",null,keyPlan.id,null)
                                    }
                                }
                            }
                        }
                    }else{
                        keyPlan.active = DataStatus.SHOW
                        keyPlan.uid = UUIDGenerator.nextUUID()
                        keyPlan.status = PlanStatus.PLAN
                        keyPlan.save(flush: true)
                        if (keyPlan.hasErrors()){
                            log.error("处理榕基对接接口+++保存不存在的排期出错------[${keyPlan.errors}]")
                            throw new RuntimeException()
                        }
                        //向CMP推送数据
                        this.notify("pushCmpCaseAndPlanAndTrial",null,keyPlan.id,null)
                    }

                }

            } catch (Exception e) {
                log.error(e.getMessage())
            }
        }
        log.info("数据库写入完毕,现在开始同步 .\n")
        //循环结束
        log.info("榕基接口同步结束,本次ws任务结束.")
    }

    def getCaseTypeByCaseno(String archives) {
        //根据返回的类型判断是刑事 民事等
        def caseTypeList = CaseType.findAll()
        for (def caseType : caseTypeList){
            if (caseType.shortName){
                if (archives.contains(caseType.shortName)){
                    return caseType
                }
            }
        }
    }
}
