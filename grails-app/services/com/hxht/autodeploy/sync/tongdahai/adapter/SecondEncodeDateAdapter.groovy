package com.hxht.autodeploy.sync.tongdahai.adapter

import com.hxht.techcrt.utils.Base64Utils

import javax.xml.bind.annotation.adapters.XmlAdapter
import java.text.SimpleDateFormat

class SecondEncodeDateAdapter extends XmlAdapter<String, Date> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    @Override
    String marshal(Date v) throws Exception {
        if (v==null){
            return ""
        }
        String format = dateFormat.format(v)
        return Base64Utils.encode(format)
    }

    @Override
    Date unmarshal(String v) throws Exception {
        String decode = Base64Utils.decode(v)
        return dateFormat.parse(decode)
    }
}
