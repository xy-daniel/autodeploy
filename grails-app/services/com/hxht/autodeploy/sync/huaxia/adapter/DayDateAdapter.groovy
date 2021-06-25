package com.hxht.autodeploy.sync.huaxia.adapter

import javax.xml.bind.annotation.adapters.XmlAdapter
import java.text.SimpleDateFormat

/**
 * 2021.03.17 daniel
 * 格式:yyyy-MM-dd
 * 实体类转化XML时,使用{@XmlJavaTypeAdapter (DayDateAdapter.class)日期格式化注解},注解在Date类型的实体类字段上进行日期格式化.
 */
class DayDateAdapter extends XmlAdapter<String, Date> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd")

    @Override
    String marshal(Date v) throws Exception {
        if (v == null) {
            return ""
        }
        return dateFormat.format(v)
    }

    @Override
    Date unmarshal(String v) throws Exception {
        return dateFormat.parse(v)
    }

}
