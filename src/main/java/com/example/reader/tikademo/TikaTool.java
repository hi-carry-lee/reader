package com.example.reader.tikademo;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用tika来解析文件，输出文本内容。
 */
public class TikaTool {

    public static final int maxSize = 10 * 1024 * 1024;

    /**
     * 解析文件，返回map对象，包含文件内容及文件元数据
     * @param fileName, input
     * @return Map<String,Object>
     */
    public static Map<String, Object> parseFile(String fileName, InputStream input) {
        Map<String, Object> meta = new HashMap<String, Object>();
        Parser parser = null;
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (suffix.equals("xlsx")) {
            parser = new OOXMLParser();
        } else {
            parser = new AutoDetectParser();
         }

        try {
            Metadata metadata = new Metadata();
            metadata.set(Metadata.CONTENT_ENCODING, "utf-8");
            metadata.set(Metadata.RESOURCE_NAME_KEY, fileName);

            ContentHandler handler = new BodyContentHandler(maxSize);
            ParseContext context = new ParseContext();
            context.set(Parser.class, parser);

            parser.parse(input, handler, metadata, context);
            for (String name : metadata.names()) {
                meta.put(name, metadata.get(name));
            }
            meta.put("content", handler.toString());
            return meta;
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
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
     * Tika解析文件，返回字符串文件内容
     * @param fileName, input
     * @return String
     */
    public static String parseFileContent(String fileName, InputStream input) {
        Parser parser = null;
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (suffix.equals("xlsx")) {
            parser = new OOXMLParser();
        } else {
            parser = new AutoDetectParser();
        }

        Metadata metadata = null;
        try {
            metadata = new Metadata();
            metadata.set(Metadata.CONTENT_ENCODING, "utf-8");
            metadata.set(Metadata.RESOURCE_NAME_KEY, fileName);

            ContentHandler handler = new BodyContentHandler(maxSize);
            ParseContext context = new ParseContext();
            context.set(Parser.class, parser);
            parser.parse(input, handler, metadata, context);

            return handler.toString();
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


}