package com.hxht.autodeploy.utils

import org.apache.pdfbox.pdmodel.PDDocument
import org.fit.pdfdom.PDFDomTree

import javax.xml.parsers.ParserConfigurationException

class PdfUtil {
    static generateHTMLFromPDF(File file) throws IOException, ParserConfigurationException {
        def outPutStreamWriter = null
        try {
            def pdf = PDDocument.load(file)
            def outPutStream = new ByteArrayOutputStream()
            outPutStreamWriter = new OutputStreamWriter(outPutStream, "utf-8")
            new PDFDomTree().writeText(pdf, outPutStreamWriter)
            outPutStream.toString()
        }finally {
            if (outPutStreamWriter != null) {
                outPutStreamWriter.close()
            }
        }


    }
}
