package com.hxht.autodeploy.service.sync.huigu;

import org.apache.cxf.staxutils.DelegatingXMLStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author Nano on 2018/8/23.
 * @version 1.0
 * @since 1.0
 */
public class CDataXMLStreamWriter extends DelegatingXMLStreamWriter {
    // All elements with these names will be turned into CDATA
    private static final Logger log = LoggerFactory.getLogger(CDataXMLStreamWriter.class);
    private static final Logger wslog = LoggerFactory.getLogger("wsAppender");

    public CDataXMLStreamWriter(XMLStreamWriter del) {
        super(del);
    }


    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        log.info("打印参数：{}", text);
        wslog.info("打印参数：{}", text);
        super.writeCData(text);
    }

    public void writeStartElement(String prefix, String local, String uri) throws XMLStreamException {
        super.writeStartElement(prefix, local, uri);
    }
}
