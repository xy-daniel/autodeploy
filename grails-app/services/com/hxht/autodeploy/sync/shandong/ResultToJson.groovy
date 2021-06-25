package com.hxht.autodeploy.sync.shandong

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONException
import com.alibaba.fastjson.JSONObject

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.SQLException

class ResultToJson {
    public static String str="[{\"DD\":\"第七审判庭\",\"JSSJ\":\"2020-08-10 12:00:00.0\",\"DSR\":\"上诉人:郝钢柱被上诉人:李卫平\",\"SJY\":\"武文静\",\"LABM\":\"立案一庭\",\"AH\":\"(2020)鲁17民终2447号\",\"LARQ\":\"2020-07-17 08:49:02.0\",\"SN\":\"199600000125754\",\"KSSJ\":\"2020-08-10 09:00:00.0\",\"SPZ\":\"田佰旺\",\"TC\":\"1\"},{\"DD\":\"第八审判庭\",\"JSSJ\":\"2020-08-10 12:00:00.0\",\"DSR\":\"上诉人:吴素云被上诉人:马凡新\",\"SJY\":\"郭艳红\",\"LABM\":\"立案一庭\",\"AH\":\"(2020)鲁17民终2500号\",\"LARQ\":\"2020-07-22 16:08:44.0\",\"SN\":\"199600000126179\",\"KSSJ\":\"2020-08-10 09:00:00.0\",\"SPZ\":\"刘秋桦\",\"TC\":\"1\"}]"

    /**
     * 将resultSet转化为JSON数组
     *
     * @param rs
     * @return
     * @throws SQLException
     * @throws com.alibaba.fastjson.JSONException
     */
    static JSONArray resultSetToJsonArry(ResultSet rs) throws SQLException, JSONException {
        // json数组
        JSONArray array = new JSONArray()
        // 获取列数
        ResultSetMetaData metaData = rs.getMetaData()
        int columnCount = metaData.getColumnCount()
        // 遍历ResultSet中的每条数据
        while (rs.next()) {
            JSONObject jsonObj = new JSONObject()
            // 遍历每一列
            for (int i = 1 ;i <= columnCount ;i++) {
                String columnName = metaData.getColumnLabel(i)
                String value = rs.getString(columnName)
                jsonObj.put(columnName, value)
            }
            array.add(jsonObj)
        }
        return array
    }
}
