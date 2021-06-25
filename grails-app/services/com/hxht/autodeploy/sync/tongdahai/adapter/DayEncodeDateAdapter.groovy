package com.hxht.autodeploy.sync.tongdahai.adapter

import cn.hutool.core.date.DateUtil
import com.hxht.techcrt.utils.Base64Utils

import javax.xml.bind.annotation.adapters.XmlAdapter

/**
 * 2021.05.24 >>> 新建日期格式化处理器 daniel
 */
class DayEncodeDateAdapter extends XmlAdapter<String, Date> {

    @Override
    Date unmarshal(String s) throws Exception {
        return DateUtil.parse(Base64Utils.decode(s), "yyyyMMdd")
    }

    @Override
    String marshal(Date date) throws Exception {
        return date == null ? "" : Base64Utils.encode(DateUtil.format(date, "yyyyMMdd"))
    }
}
