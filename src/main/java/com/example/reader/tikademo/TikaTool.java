package com.example.reader.tikademo;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
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
     * @param  input
     * @return Map<String,Object>
     */
    public static Map<String, Object> parseFile(InputStream input) {
        Map<String, Object> meta = new HashMap<String, Object>();
        Parser parser = new AutoDetectParser();;

        try {
            Metadata metadata = new Metadata();
            metadata.set(Metadata.CONTENT_ENCODING, "utf-8");

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
     * @param  input
     * @return String
     */
    public static String parseFileContent(InputStream input) {
        Parser parser = new AutoDetectParser();
        Metadata metadata = null;
        try {
            metadata = new Metadata();
            metadata.set(Metadata.CONTENT_ENCODING, "utf-8");

            ContentHandler handler = new BodyContentHandler(maxSize);
            ParseContext context = new ParseContext();
            context.set(Parser.class, parser);
            parser.parse(input, handler, metadata, context);

            return handler.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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