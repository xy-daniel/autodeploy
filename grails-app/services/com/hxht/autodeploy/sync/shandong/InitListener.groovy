package com.hxht.autodeploy.sync.shandong

import com.alibaba.fastjson.JSONObject

/**
 * 用于初始化获取对应法院sybase数据库地址
 * */
class InitListener {
    private static Map<String, JSONObject> court_sybase_url=new HashMap<String,JSONObject>()
    private static void addCourt(String currentcourtId,String courtName,String sybaseIp,String remeoteCourtId){
        JSONObject object=new JSONObject()
        object.put("name",courtName)
        object.put("sybaseIp",sybaseIp)
        object.put("romeoteCourtId",remeoteCourtId)
        court_sybase_url.put(currentcourtId,object)
    }
    static {
        /**
         * 如果需要新增ip,请在里面添加对应的法院信息
         * */
        addCourt("FG0","菏泽市中级人民法院","142.176.1.36","0FG")//菏泽中院
        addCourt("FG6","巨野县人民法院","142.176.1.36","0FG6")//巨野中院（菏泽下）
        addCourt("FA0","威海市中级人民法院","142.208.1.2","0FA")//威海中院
        addCourt("FA4","乳山市人民法院","142.208.1.2","0FA4")//乳山
        addCourt("FG7","郓城县人民法院","142.176.1.36","0FG7")//郓城县人民（菏泽下）
        addCourt("FFB","临沭县人民法院","142.160.65.51","0FFB");//临沭中院
        addCourt("FF7","蒙阴县人民法院","142.160.65.51","0FF7");//蒙阴县法院（临沂下）
        addCourt("FG4","成武县人民法院","142.176.1.36","0FG4");//成武县法院（菏泽下）
    }
    static JSONObject getSybaseByObject(String currentCourtId){
        return court_sybase_url.get(currentCourtId)
    }
}
