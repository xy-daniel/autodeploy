package com.hxht.autodeploy.sync.shandong

import com.alibaba.fastjson.JSONObject
import com.hxht.techcrt.DataStatus
import com.hxht.techcrt.PlanStatus
import com.hxht.techcrt.court.CaseInfo
import com.hxht.techcrt.court.CaseType
import com.hxht.techcrt.court.Courtroom
import com.hxht.techcrt.court.Department
import com.hxht.techcrt.court.Employee
import com.hxht.techcrt.court.PlanInfo
import com.hxht.techcrt.court.manager.SystemController
import com.hxht.techcrt.utils.UUIDGenerator
import grails.gorm.transactions.Transactional

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.text.SimpleDateFormat

class ShanDongService {
    ConnectionUtilService connectionUtilService
    DataImportService dataImportService
    /**
     * 预定案件排期信息
     * <p>
     * 注意此处，每一条数据都是一条事物，要保证排期，案件，人员编号，法庭事物一致性
     */
    def getCaseAndPlan() {
        //获取一个星期排期数据
        Connection conn = null
        ResultSet rs =null
        def array
        try{
            def fydm = SystemController.currentCourt.ext3 //获取当前法院分级码
            //获取对应法院的sybase地址
            JSONObject romote= InitListener.getSybaseByObject(fydm)
            String sybaseIp=romote.getString("sybaseIp")
            String romeoteCourtId=romote.getString("romeoteCourtId")
            log.info("获取到的sybase ip:"+sybaseIp)
            log.info("获取到的对方给的法院标码:"+romeoteCourtId)
            def sybaseUser = "escloud"
            def sybasePwd = "eastsoft.cn"
            conn = connectionUtilService.getInstance(sybaseIp,sybaseUser,sybasePwd)
            String sql="SELECT c.SAAY1,c.SN,c.AH,p.KSSJ, p.JSSJ ,p.DD ,c.DSR ,p.TC ,c.CASENAME ,c.SJY ,c.SPZ,c.LARQ ,c.LABM,c.AJXZ  \n" +
                    "FROM CASES_PQ p,  CASES c \n" +
                    "where p.CASE_SN =c.SN  and p.KSSJ >=?  and p.KSSJ <=?  and c.COURT_NO =? "
            //获取开始时间字符串
            Date startDate = new Date()
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd")
            String startStr = format.format(startDate)//开始时间（今天）
            //获取15天后时间字符串
            Calendar calendar = Calendar.getInstance()
            calendar.setTime(startDate)
            calendar.add(Calendar.DAY_OF_YEAR, 15)
            Date endDate = calendar.getTime()
            String endStr = format.format(endDate)//结束时间
            PreparedStatement ps = conn.prepareStatement(sql)
            ps.setString(1,startStr)
            ps.setString(2,endStr)
            ps.setString(3,romeoteCourtId)
            rs = ps.executeQuery()
            array= ResultToJson.resultSetToJsonArry(rs)
            //把结果集转成json数组
          /*  def str1="[{\"DD\":\"第七审判庭\",\"JSSJ\":\"2020-08-10 12:00:00.0\",\"DSR\":\"上诉人:郝钢柱被上诉人:李卫平\",\"SJY\":\"武文静\",\"LABM\":\"立案一庭\",\"AH\":\"(2020)鲁17民终2447号\",\"LARQ\":\"2020-07-17 08:49:02.0\",\"SN\":\"199600000125754\",\"KSSJ\":\"2020-08-10 09:00:00.0\",\"SPZ\":\"田佰旺\",\"TC\":\"1\"},{\"DD\":\"第八审判庭\",\"JSSJ\":\"2020-08-10 12:00:00.0\",\"DSR\":\"上诉人:吴素云被上诉人:马凡新\",\"SJY\":\"郭艳红\",\"LABM\":\"立案一庭\",\"AH\":\"(2020)鲁17民终2500号\",\"LARQ\":\"2020-07-22 16:08:44.0\",\"SN\":\"199600000126179\",\"KSSJ\":\"2020-08-10 09:00:00.0\",\"SPZ\":\"刘秋桦\",\"TC\":\"1\"}]"
            array= JSONArray.parseArray(str1)*/
            //模拟数据测试
            log.info("获取的结果集记录数:"+array.size())
            log.info("获取的结果集:"+array.toJSONString())
            //释放数据
            if(array.size()>0){
                for(int i=0;i<array.size();i++) {
                    JSONObject object=array.getJSONObject(i)
                    // 处理部门
                    def dept = dataImportService.dept(object.getString("LABM"))
                    // 处理书记员
                    def secretary = dataImportService.secretary(object.getString("SJY"),dept)
                    //法官取得审判长
                    def judge = dataImportService.judge(object.getString("SPZ"),dept)
                    //法庭
                    def courtroom = dataImportService.addCourtRoom(object.getString("DD"))
                    //案件类型
                    def type = dataImportService.getCaseType(object.getString("AJXZ"))
                    sub(secretary, judge, dept, courtroom,object, type)
                }
            }
        }catch (Exception e){
            log.info("获取预定案件排期失败:" + e.getMessage())
        }finally {
            //断开数据库链接
            if(null!=conn){
                try {
                    conn.close()
                } catch (SQLException throwables) {
                    throwables.printStackTrace()
                }
            }
            rs=null
            array=null
        }
    }

    @Transactional
    def sub(Employee secretary, Employee judege, Department dept, Courtroom courtroom, JSONObject object, CaseType type) throws Exception {
        def casename=object.getString("CASENAME")
        def dsr=object.getString("DSR")
        def ay=object.getString("SAAY1")
        if (!casename){
            casename = ay
            if (!casename){
                casename = dsr
            }
        }
        //案号
        def archivies =  object.getString("AH")
        //案件编号 interfaceid---去掉所有","号
        def interface_id = object.getString("SN").replace(",","")
        //审判次数
        def spcx = object.getString("TC")+ ""
        //原告,被告
        JSONObject dcrjson= dataImportService.getDsr(dsr)
        def accuser = dcrjson.getString("accuse")
        def accused = dcrjson.getString("accused")
        //开始时间
        def startDate = object.getDate("KSSJ")
        //结束时间
        def endDate = object.getDate("JSSJ")
        //立案日期
        def fillingDate = object.getDate("LARQ")


        //案号判断本地是否有此次案件信息
        def caseInfo = CaseInfo.findByArchives(archivies)
        if (!caseInfo) {//不存在
            caseInfo = new CaseInfo()
            caseInfo.uid = UUIDGenerator.nextUUID()
            caseInfo.archives = archivies
            caseInfo.name = casename
            caseInfo.accuser = accuser
            caseInfo.accused = accused
            caseInfo.filingDate = fillingDate
            caseInfo.caseCause = ay
            caseInfo.summary = ay
            caseInfo.type = type
            caseInfo.department = dept
            caseInfo.active = DataStatus.SHOW //使用
            caseInfo.synchronizationId = interface_id
            caseInfo.save(flush:true)
            if (caseInfo.hasErrors()){
                log.error("ShanDongService.saveCase 处理山东法院对接接口---保存不存在的案件信息出错------[${caseInfo.errors}]")
                throw new RuntimeException()
            }
        } else {
            // 处理排期编号
            def caseInfo1 = CaseInfo.findBySynchronizationId(interface_id)
            //存在相同id,判断是本地立案还是东软的案件
            if (caseInfo1) {
                //是东软导入的案件,进行更新
                caseInfo.archives = archivies
                caseInfo.name = casename
                caseInfo.accuser = accuser
                caseInfo.accused = accused
                caseInfo.filingDate = fillingDate
                caseInfo.caseCause = ay
                caseInfo.summary = ay
                caseInfo.type = type
                caseInfo.department = dept
                caseInfo.active = DataStatus.SHOW //使用
                caseInfo.synchronizationId = interface_id
                caseInfo.save(flush:true)
                if (caseInfo.hasErrors()){
                    log.error("ShanDongService.saveCase 处理山东法院对接接口---保存不存在的案件信息出错------[${caseInfo.errors}]")
                    throw new RuntimeException()
                }
            } else {
                //说明不用更新
            }
        }

        def sycPlan = interface_id + "-" + spcx //排期的案号 + 庭审的庭次
        def plan = PlanInfo.findBySynchronizationId(sycPlan)
        if (plan){
            if (plan.status == PlanStatus.PLAN){//如果是排期状态则更新排期
                plan.judge = judege
                plan.secretary = secretary
                plan.courtroom = courtroom
                plan.synchronizationId = sycPlan //排期的案号 + 庭审的庭次
                plan.uid = UUIDGenerator.nextUUID()
                //预定开始日期+时间
                plan.startDate = startDate
                //预定结束时间
                plan.endDate = endDate
                plan.status = PlanStatus.PLAN
                plan.active = DataStatus.SHOW
                plan.allowPlay = 0
                plan.caseInfo = caseInfo
                plan.save(flush: true)
                if (plan.hasErrors()) {
                    def msg = "[DataImportService.sub]处理山东法院对接接口---保存plan 失败 errors [${plan.errors}]"
                    log.error(msg)
                    throw new RuntimeException(msg)
                }
            }
        }else{
            plan = new PlanInfo()
            plan.uid = UUIDGenerator.nextUUID()
            plan.judge = judege
            plan.secretary = secretary
            plan.courtroom = courtroom
            plan.synchronizationId = sycPlan //排期的案号 + 庭审的庭次
            plan.uid = UUIDGenerator.nextUUID()
            //预定开始日期+时间
            plan.startDate = startDate
            //预定结束时间
            plan.endDate = endDate
            plan.status = PlanStatus.PLAN
            plan.active = DataStatus.SHOW
            plan.allowPlay = 0
            plan.caseInfo = caseInfo
            plan.save(flush: true)
            if (plan.hasErrors()) {
                def msg = "[DataImportService.sub]处理山东法院对接接口---保存plan 失败 errors [${plan.errors}]"
                log.error(msg)
                throw new RuntimeException(msg)
            }
        }
    }
}
