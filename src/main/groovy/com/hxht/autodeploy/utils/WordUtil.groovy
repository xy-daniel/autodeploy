package com.hxht.autodeploy.utils

import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLConverter
import fr.opensagres.poi.xwpf.converter.xhtml.XHTMLOptions
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.converter.WordToHtmlConverter
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.ooxml.POIXMLDocument
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument

import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/**
 * Word操作工具类
 */
class WordUtil {
    /**
     * 读取word文档，返回Html字符串。
     * @param file 文档文件
     */
    static readWordToHtml(File file) {
        def extractor
        try {
            def filePath = file.getAbsolutePath()
            if (filePath.endsWith(".docx")) {//docx
                return word2007ToHtml(file)
            } else if (filePath.endsWith(".doc")) {//doc
                return docToHtml(file)
            } else if (filePath.endsWith("pdf")) {//pdf
                PdfUtil.generateHTMLFromPDF(file)

            }
        } catch (IOException e) {
            e.printStackTrace()
        } finally {
            if (null != extractor) try {
                extractor.close()
            } catch (IOException e) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 获取word文档文本内容
     * @param file
     */
    static readWordContent(File file) {
        def extractor
        try {
            def filePath = file.getAbsolutePath()
            if (filePath.endsWith(".docx")) {//docx
                return word2007Content(file)
            } else if (filePath.endsWith(".doc")) {//doc
                return wordContent(file)
            }

        } catch (IOException e) {
            e.printStackTrace()
        } finally {
            if (extractor) {
                try {
                    extractor.close()
                } catch (IOException e) {
                    e.printStackTrace()
                }
            }
        }
        return null
    }

    /**
     * 获取2007以上版本word文档文本内容
     * @param file
     */
    static word2007Content(File file) {
        //读取文件路径
        def content = ""
        try {
            def opcPackage = POIXMLDocument.openPackage(file.getAbsolutePath())
            XWPFDocument xwpf = new XWPFDocument(opcPackage)
            POIXMLTextExtractor poiText = new XWPFWordExtractor(xwpf)
            content = poiText.getText()
        } catch (IOException e) {
            e.printStackTrace()
        }
        return content
    }
    /**
     * 获取2007以下版本word文档文本内容
     * @param file
     */
    static wordContent(File file) {
        def content = ""
        //读取字节流，读取文件路径
        def input = null
        try {
            input = new FileInputStream(file)
            def wex = new WordExtractor(input)
            content = wex.getText()
        } catch (Exception e) {
            e.printStackTrace()
        }finally {
            if (input != null) {
                input.close()
            }
        }
        if(content.endsWith("\r\n\r\n\r\n3\r\n\r\n\r\n\r\n1\r\n\r\n\r\n\r\n")){
            return content.substring(0,content.length()-26)
        }else{
            return content
        }
    }

    static word2007ToHtml(File file) {
        def outPutStreamWriter = null
        try {
            def doc = new XWPFDocument(file.newDataInputStream())
            def options = XHTMLOptions.create()
            def outPutStream = new ByteArrayOutputStream()
            outPutStreamWriter = new OutputStreamWriter(outPutStream, "utf-8")
            def xhtmlConverter = (XHTMLConverter) XHTMLConverter.getInstance()
            xhtmlConverter.convert(doc, outPutStreamWriter, options)
            outPutStream.toString()
        } finally {
            if (outPutStreamWriter != null) {
                outPutStreamWriter.close()
            }
        }
    }

    /**
     * doc文件转换成html
     */
    static docToHtml(File file) {
        def out = null
        try {
            def wordDocument = new HWPFDocument(new FileInputStream(file.getAbsolutePath()))
            def wordToHtmlConverter = new WordToHtmlConverter(
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument())
            wordToHtmlConverter.processDocument(wordDocument)
            def htmlDocument = wordToHtmlConverter.getDocument()
            out = new ByteArrayOutputStream()
            def domSource = new DOMSource(htmlDocument)
            def streamResult = new StreamResult(out)
            def tf = TransformerFactory.newInstance()
            def serializer = tf.newTransformer()
            serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8")
            serializer.setOutputProperty(OutputKeys.INDENT, "yes")
            serializer.setOutputProperty(OutputKeys.METHOD, "html")
            serializer.transform(domSource, streamResult)
            out.toString()
        } finally {
            if (out != null) {
                out.close()
            }
        }
    }

}
