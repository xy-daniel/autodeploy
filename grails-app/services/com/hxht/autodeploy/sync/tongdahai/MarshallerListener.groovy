package com.hxht.autodeploy.sync.tongdahai

import javax.xml.bind.Marshaller
import java.lang.reflect.Field

class MarshallerListener extends Marshaller.Listener {

    public static final String BLANK_CHAR = ""

    @Override
    void beforeMarshal(Object source) {
        super.beforeMarshal(source)
        Field[] fields = source.getClass().getDeclaredFields()
        for (Field f : fields) {
            f.setAccessible(true)
            //获取字段上注解<pre name="code" class="java">
            try {
                if (f.getType() == String.class && f.get(source) == null) {
                    f.set(source, BLANK_CHAR)//为null标签的设置成空串;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace()
            }
        }
    }
}
