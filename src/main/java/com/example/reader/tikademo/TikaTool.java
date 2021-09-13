package com.example.reader.tikademo;

import com.google.common.io.Files;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
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
 * 需要测试：pdf, word, excel, cvs, html, xml
 */
public class TikaTool {

    public static void main(String[] args) {
        File file = new File("D:\\tika-test.pdf");
        Map<String, Object> map = parseFile(file);
        System.out.println(map.get("content"));
    }

    /**
     * 解析文件
     * @param file
     * @return
     */
    public static Map<String,Object> parseFile(File file) {
        Map<String,Object> meta = new HashMap<String,Object>();
        // 不确定文档格式，选择这个，否则可以指定具体的Parser，比如：PDFParser
        Parser parser = new AutoDetectParser();
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


}