package com.example.reader.tikademo;

import com.google.common.io.Files;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ExpandedTitleContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用tika来解析文件，输出文本内容
 * 需要测试：pdf, word, excel, html, xml 已测试
 * excel提取的时间格式统一为：月份-日期-年（缩写） 12/21/21
 */
public class TikaTool {

    public static void main(String[] args) throws TikaException, IOException, SAXException {
        File file = new File("D:\\test\\Data Provider Questionnaire_Chinese.xlsx");
        Map<String, Object> map = parseFile(file);
        System.out.println(map.get("content"));
//        System.out.println(extractHtml(file));
//        parse1();
    }

    /**
     * 解析文件，如果是HTML，只输出页面内容，结构及样式不输出
     * @param file
     * @return
     */
    public static Map<String,Object> parseFile(File file) {
        Map<String,Object> meta = new HashMap<String,Object>();
        // 不确定文档格式，选择这个，否则可以指定具体的Parser，比如：PDFParser
        Parser parser = null;
//        OOXMLParser msofficeparser = new OOXMLParser ();
        String s = file.getName().substring(file.getName().lastIndexOf(".")+1);
        if(s.equals("xlsx")){
            parser = new OOXMLParser ();
        }else{
            parser = new AutoDetectParser();
        }
        InputStream input = null;
        try {
            /**
             * 参数1，文档元数据。既是输入参数，也是输出参数。作输入参数时，能够有助于解析器更好地理解文档的格式。
             */
            Metadata metadata = new Metadata();
            metadata.set(Metadata.CONTENT_ENCODING, "utf-8");
            metadata.set(Metadata.RESOURCE_NAME_KEY, file.getName());
            /**
             *  参数2 文档输入原始 字节流。
             */
            input = new FileInputStream(file);
            /**
             * 参数3：XHTML SAX事件处理器。将输入文档的结构化信息以XHTML格式写入到此Handler
             * 参数：最大数量的字符，超出会抛出SAXException异常
             *      SAX（Simple API for XML）是一个基于事件的xml文档的解析器
             *
             *      name:meta:word-count, metadata.get(name):132
             *      metadata.get("word-count")可以作为 BodyContentHandler的参数
             */
            ContentHandler handler = new BodyContentHandler(10 * 1024 * 1024);
            /**
             *  参数4 解析处理的上下文
             */
            ParseContext context = new ParseContext();
            // 设置解析器
            context.set(Parser.class, parser);
            // 核心方法 parse
            parser.parse(input, handler, metadata, context);
            for (String name : metadata.names()) {
                meta.put(name,metadata.get(name));
                System.out.println("name:"+name+", metadata.get(name):"+metadata.get(name));
                /**
                 * 这里的 metadata里面的信息，是解析文件之后得到的
                 */
            }
            meta.put("content",handler.toString());
            return meta;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 文档输入原始 字节流，需要手动关闭
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 原样输出HTML文本
     * @param file
     * @return
     * @throws IOException
     */
    public static String extractHtml(File file) throws IOException {
        byte[] bytes = Files.toByteArray(file);
        AutoDetectParser tikaParser = new AutoDetectParser();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SAXTransformerFactory factory = (SAXTransformerFactory) SAXTransformerFactory.newInstance();
        TransformerHandler handler;
        try {
            handler = factory.newTransformerHandler();
        } catch (TransformerConfigurationException ex) {
            throw new IOException(ex);
        }
        handler.getTransformer().setOutputProperty(OutputKeys.METHOD, "html");
        handler.getTransformer().setOutputProperty(OutputKeys.INDENT, "yes");
        handler.getTransformer().setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        handler.setResult(new StreamResult(out));
        ExpandedTitleContentHandler handler1 = new ExpandedTitleContentHandler(handler);
        try {
            tikaParser.parse(new ByteArrayInputStream(bytes), handler1, new Metadata());
        } catch (SAXException | TikaException ex) {
            throw new IOException(ex);
        }
        return new String(out.toByteArray(), "UTF-8");
    }

    /**
     * 针对后缀是 .xlsx的文件，如果使用上面的方式，会抛出异常：
     *  org.apache.tika.exception.TikaException: Unexpected RuntimeException from org.apache.tika.parser.microsoft.ooxml.OOXMLParser@2eee3069
     *  经过搜索，可能是java版本的问题，尝试切换java8
     *
     *  OOXML：全称是Office Open XML,OOXML是由微软公司为Office 2007产品开发的技术规范,现已成为国际文档格式标准,
     */
    public static void parse1() throws IOException, TikaException, SAXException {
        //detecting the file type
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(new File("D:\\test\\Data Provider Questionnaire_Chinese.xlsx"));
        ParseContext pcontext = new ParseContext();

        //OOXml parser
        OOXMLParser msofficeparser = new OOXMLParser ();
        msofficeparser.parse(inputstream, handler, metadata,pcontext);
        System.out.println("文件内容:" + handler.toString());
//        System.out.println("Metadata of the document:");
//        String[] metadataNames = metadata.names();
//
//        for(String name : metadataNames) {
//            System.out.println(name + ": " + metadata.get(name));
//        }
    }
}