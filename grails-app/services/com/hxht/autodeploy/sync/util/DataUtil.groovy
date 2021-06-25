package com.hxht.autodeploy.sync.util;

import org.apache.log4j.Logger;
import org.dom4j.*;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultElement;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller

/**
 * 工具类向需要访问数据库的方法提供接口
 */
class DataUtil {
    private static Logger log = Logger.getLogger(DataUtil.class)

    /**
     * xml数据转换成bean
     *
     * @param xml       xml数据
     * @param valueType
     * @param <T>
     * @return bean
     */
    @SuppressWarnings("unchecked")
    static <T> T fromXML(String xml, Class<T> valueType) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(valueType);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (T) unmarshaller.unmarshal(new StringReader(xml));
    }

    static String toXML(Object obj) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(obj.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");// //编码格式
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);// 是否格式化生成的xml串
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);// 是否省略xm头声明信息
        StringWriter writer = new StringWriter();
        marshaller.marshal(obj, writer);
        return writer.toString();
    }

    /**
     * 无子节点时专用xml解析成map方法
     * 数据实例："<Entity AJLB="6" BH="000000000082603" D_JSSJ="2016-12-22 12:00:00"
     * D_KSSJ="2016-12-22 09:00:00" D_YDSJ="2016-12-12" FT="13" KTZT=""
     * LXFS="" MS="" QXSJ="2016-12-12 15:02:55" SFKT="1" SFYC="0" SYRS=""
     * XH="1" YDR="1296"/>"
     * 经测试，可试用 1.xml（获取用户）
     * 经测试，可试用 2.xml（获取部门）
     * 经测试，可试用 3.xml（获取法庭）
     * 经测试，可试用 6.xml（获取排期）
     *
     * @param str xml字符串
     * @return
     * @throws DocumentException
     */
    static List<Map> xml2MapNoChild(String str) throws DocumentException {
        List<Map> result = new ArrayList<>();
        Document doc = DocumentHelper.parseText(str);
        Element element = doc.getRootElement();
        List list = element.elements();
        for (Object aList : list) {
            Element el = (Element) aList;
            List<DefaultAttribute> attributeList = el.attributes();
            Map<String, String> temp = new HashMap<>();
            for (DefaultAttribute da : attributeList) {
                temp.put(da.getName(), da.getText());
            }
            result.add(temp);
            System.out.println("===============下一个");
        }
        return result;
    }

    /**
     * 包含一个子节点时专用xml解析成map方法
     * 数据实例： "<caseAllInfo>\n" +
     * "<caseInfo>\n" +
     * "<case ah=\"(2016)最高法民申2763号\" ajlb=\"2\" baktxh=\"1\" bh=\"000000000079675\" cbr=\"535\" tc=\"0\" ydzt=\"2\"/>\n" +
     * "</caseInfo>\n" +
     * "</caseAllInfo>\n";
     * 经测试，可试用 4.xml
     *
     * @param str xml字符串
     * @return
     * @throws DocumentException
     */
    static List<Map> xml2MapChild(String str) throws DocumentException {
        List<Map> result = new ArrayList<>();
        Document doc = DocumentHelper.parseText(str);
        Element element = doc.getRootElement();
        List list = element.elements();
        for (Object aList : list) {
            try {
                DefaultElement el = (DefaultElement) aList;
                List<Element> elements = el.elements();
                List<DefaultAttribute> listAttr = elements.get(0).attributes();
                Map<String, String> temp = new HashMap<>();
                for (DefaultAttribute da : listAttr) {
                    temp.put(da.getName(), da.getText());
                }
                result.add(temp);
            }catch (Exception e){
                DefaultElement el = (DefaultElement) aList;
                log.error("解析xml单条数据出错，数据："+el.content());
            }

        }
        return result;
    }

    private static Map Dom2Map(Element e) {
        Map map = new HashMap();
        List list = e.elements();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                Element iter = (Element) list.get(i);
                List mapList = new ArrayList();

                if (iter.elements().size() > 0) {
                    Map m = Dom2Map(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), m);
                } else {
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(iter.getText());
                        }
                        if (obj.getClass().getName().equals("java.util.ArrayList")) {
                            mapList = (List) obj;
                            mapList.add(iter.getText());
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), iter.getText());
                }
            }
        } else
            map.put(e.getName(), e.getText());
        return map;
    }

    /**
     * 通用xml解析成map方法
     * 经测试，优化解析 5.xml
     *
     * @param str xml字符串
     * @return
     * @throws DocumentException
     */
    static Map xml2mapWithAttr(String str) throws DocumentException {
        Document doc = DocumentHelper.parseText(str);
        return xml2mapWithAttr(doc.getRootElement());
    }

    static Map xml2mapWithAttr(Element element) {
        Map<String, Object> map = new LinkedHashMap<>();

        List<Element> list = element.elements();
        List<Attribute> listAttr0 = element.attributes(); // 当前节点的所有属性的list
        for (Attribute attr : listAttr0) {
            map.put(attr.getName(), attr.getValue());
        }
        if (list.size() > 0) {
            for (Element iter : list) {
                List mapList = new ArrayList();

                if (iter.elements().size() > 0) {
                    Map m = xml2mapWithAttr(iter);
                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            mapList.add(m);
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            mapList.add(m);
                        }
                        map.put(iter.getName(), mapList);
                    } else
                        map.put(iter.getName(), m);
                } else {

                    List<Attribute> listAttr = iter.attributes(); // 当前节点的所有属性的list
                    Map<String, Object> attrMap = null;
                    boolean hasAttributes = false;
                    if (listAttr.size() > 0) {
                        hasAttributes = true;
                        attrMap = new LinkedHashMap<String, Object>();
                        for (Attribute attr : listAttr) {
                            attrMap.put(attr.getName(), attr.getValue());
                        }
                    }

                    if (map.get(iter.getName()) != null) {
                        Object obj = map.get(iter.getName());
                        if (!(obj instanceof List)) {
                            mapList = new ArrayList();
                            mapList.add(obj);
                            // mapList.add(iter.getText());
                            if (hasAttributes) {
                                attrMap.put("text", iter.getText());
                                mapList.add(attrMap);
                            } else {
                                mapList.add(iter.getText());
                            }
                        }
                        if (obj instanceof List) {
                            mapList = (List) obj;
                            // mapList.add(iter.getText());
                            if (hasAttributes) {
                                attrMap.put("text", iter.getText());
                                mapList.add(attrMap);
                            } else {
                                mapList.add(iter.getText());
                            }
                        }
                        map.put(iter.getName(), mapList);
                    } else {
                        // map.put(iter.getName(), iter.getText());
                        if (hasAttributes) {
                            attrMap.put("text", iter.getText());
                            map.put(iter.getName(), attrMap);
                        } else {
                            map.put(iter.getName(), iter.getText());
                        }
                    }
                }
            }
        } else {
            // 根节点的
            if (listAttr0.size() > 0) {
                map.put("text", element.getText());
            } else {
                map.put(element.getName(), element.getText());
            }
        }
        return map;
    }

    static List<Element> xmlSingel(String str) throws DocumentException{
        List<Element>  list=new ArrayList<Element>();
        Document document=DocumentHelper.parseText(str);
        Element root=document.getRootElement();
        Element elment=(Element) root.elements().get(0);
        list=elment.elements();
        return list;
    }

}
