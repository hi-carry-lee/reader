package com.example.reader;


import com.example.reader.tikademo.TikaTool;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;

public class TikaTest {

    @Test
    public void getObject() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(new File("D:\\test\\Data Provider Questionnaire_Chinese.xlsx"));
//        Map<String, Object> map = TikaTool.parseFile(inputStream);
//        System.out.println(map.get("content"));
    }

    @Test
    public void getString() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream(new File("D:\\test\\Data Provider Questionnaire_Chinese.xlsx"));
//        String content = TikaTool.parseFileContent(inputStream);
//        System.out.println(content);
    }
}
