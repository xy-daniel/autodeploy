package com.hxht.autodeploy.sync.util

import com.hxht.techcrt.sync.tongdahai.MarshallerListener
import com.hxht.techcrt.sync.tongdahai.model.MyModel
import com.hxht.techcrt.sync.tongdahai.model.RespResult
import com.hxht.techcrt.utils.Base64Utils
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.DocumentHelper
import org.dom4j.Element

import javax.xml.bind.JAXBContext
import javax.xml.bind.Marshaller
import javax.xml.bind.Unmarshaller
import java.lang.reflect.Field

/**
 * XML生成和解析工具类 created by daniel
 * 2021.03.17 >>> 增加华夏响应值解析工具 daniel
 */
class XMLUtils {

    /**
     * 解码
     */
    static Object convertToXmlDecode(Object obj, Class objData) {
        Field[] fields = obj.getClass().getDeclaredFields()
        for (Field field:fields) {
            field.setAccessible(true)
            //1.解析RespResult
            if (field.getType() == RespResult.class) {
                RespResult result = (RespResult) field.get(obj)
                Field[] resultFields = result.getClass().getDeclaredFields()
                for (Field resultField : resultFields) {
                    resultField.setAccessible(true)
                    if (resultField.getType() == String.class) {
                        String resultStr = (String) resultField.get(result)
                        String decode = Base64Utils.decode(resultStr)
                        resultField.set(result, decode)
                    }
                }
                field.set(obj, result)
            }
            //2.解析objData
            if (field.getType() == objData) {
                Object result = field.get(obj)
                //反射
                Field[] resultFields = result.getClass().getDeclaredFields()
                //循环反射
                for (Field resultField:resultFields) {
                    //获取public
                    resultField.setAccessible(true)
                    //2.1 解析serverTime
                    if (resultField.getType() == Date.class) {
                        //设置对应的值
                        resultField.set(result, new Date())
                    }
                    //2.2 解析String字段
                    if (resultField.getType() == String.class) {
                        String resultStr = (String) resultField.get(result)
                        String decode = Base64Utils.decode(resultStr)
                        resultField.set(result, decode)
                    }
                    //2.3 解析List字段
                    if (resultField.getType() == List.class) {
                        List<Object> resultList = (List<Object>) resultField.get(result)
                        //2.3.1 处理过程
                        for (Object model:resultList) {
                            //反射
                            Field[] modelFields = model.getClass().getDeclaredFields()
                            //循环反射
                            for (Field modelField:modelFields) {
                                if (modelField.getType() == String.class) {
                                    modelField.setAccessible(true)
                                    String modelStr = (String) modelField.get(model)
                                    String decode = Base64Utils.decode(modelStr)
                                    modelField.set(model, decode)
                                }
                            }
                        }
                        resultField.set(result, resultList)
                    }
                }
                field.set(obj, result)
            }
        }
        return obj
    }
    /**
     * JavaBean TO StringXML 不进行编码
     *
     * @param obj JAVABean
     * @return StringXML
     */
    static String convertToXml(Object obj) {
        // 创建输出流
        StringWriter sw = new StringWriter()
        try {
            // 利用jdk中自带的转换类实现
            JAXBContext context = JAXBContext.newInstance(obj.getClass())

            Marshaller marshaller = context.createMarshaller()
            // 格式化xml输出的格式
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE)
            marshaller.setListener(new MarshallerListener())
            // 将对象转换成输出流形式的xml
            marshaller.marshal(obj, sw)
        } catch (Exception e) {
            println "convertToXml" + e.getMessage()
            return null
        }
        return sw.toString()
    }

    /**
     * JavaBean TO StringXML  并且对标签内容进行Base64编码
     *
     * @param obj JAVABean
     * @return String XML
     */
    static String convertToXmlEncode(Object obj) {
        // 创建输出流
        StringWriter sw = new StringWriter()
        try {
            Field[] fields = obj.getClass().getDeclaredFields()
            for (Field field : fields) {
                field.setAccessible(true)
                if (field.getType() == String.class) {
                    String o = (String) field.get(obj)
                    String encode = Base64Utils.encode(o)
                    field.set(obj, encode)
                }
            }
            // 利用jdk中自带的转换类实现
            JAXBContext context = JAXBContext.newInstance(obj.getClass())
            Marshaller marshaller = context.createMarshaller()
            // 格式化xml输出的格式
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE)
            marshaller.setListener(new MarshallerListener())
            // 将对象转换成输出流形式的xml
            marshaller.marshal(obj, sw)
        } catch (Exception e) {
            println "convertToXmlEncode："+e.getMessage()
            return null
        }
        return sw.toString()
    }

    /**
     * StringXML TO JavaBean 不对标签内容进行解码
     *
     * @param clazz  JavaBean的class
     * @param xmlStr StringXML
     * @return JavaBean
     */
    static Object convertXmlStrToObjectDecode(Class clazz, String xmlStr) {
        Object xmlObject = null
        try {
            // 进行将Xml转成对象的核心接口
            Unmarshaller unmarshaller = JAXBContext.newInstance(clazz).createUnmarshaller()
            StringReader sr = new StringReader(xmlStr)
            xmlObject = unmarshaller.unmarshal(sr)
            println xmlObject.toString()
            decodeObject(xmlObject)
        } catch (Exception e) {
                println "convertXmlStrToObjectDecode:"+e.getMessage()
            return null
        }
        return xmlObject
    }

    private static Object decodeObject(Object object) throws IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields()
        for (Field field : fields) {
            field.setAccessible(true)
            if (field.get(object) instanceof  String) {
                String o = (String) field.get(object)
                String encode = Base64Utils.decode(o)
                field.set(object, encode)
            }
            if (field.get(object) instanceof List) {
                ArrayList list = (ArrayList) field.get(object)
                for (Object o : list) {
                    decodeObject(o)
                }
            }
            if (field.get(object) instanceof MyModel) {
                Object o = field.get(object)
                decodeObject(o)
            }
        }
        return object
    }

    /**
     * 解析华夏响应码
     */
    static String getXMLCode(String response) {
        Document doc = null
        try {
            doc = DocumentHelper.parseText(response)
            Element rootElement = doc.getRootElement()
            return rootElement.attributeValue("code")
        } catch (DocumentException e) {
            println "解析XML响应Desc异常:" + e.getMessage()
            return ""
        }
    }

    /**
     * 解析华夏响应描述
     */
    static String getXMLDesc(String response) {
        Document doc = null
        try {
            doc = DocumentHelper.parseText(response)
            Element rootElement = doc.getRootElement()
            return rootElement.attributeValue("desc")
        } catch (DocumentException e) {
            println "解析XML响应Desc异常:" + e.getMessage()
            return ""
        }
    }
}
