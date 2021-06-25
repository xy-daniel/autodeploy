package com.hxht.autodeploy.service.sync.huigu;

import org.apache.cxf.interceptor.AttachmentOutInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.staxutils.StaxUtils;

import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;

/**
 * @author Nano on 2018/8/23.
 * @version 1.0
 * @since 1.0
 */
public class CDataWriterInterceptor extends AbstractPhaseInterceptor<Message> {

    public CDataWriterInterceptor() {
        super(Phase.PRE_STREAM);
        addAfter(AttachmentOutInterceptor.class.getName());
    }

    @Override
    public void handleMessage(Message message) throws Fault {
// Required for CDATA to working
        message.put("disable.outputstream.optimization", Boolean.TRUE);
        XMLStreamWriter writer = StaxUtils.createXMLStreamWriter(message.getContent(OutputStream.class));
        message.setContent(XMLStreamWriter.class, new CDataXMLStreamWriter(writer));
    }
}
