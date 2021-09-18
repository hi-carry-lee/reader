package com.example.reader.mail;

import java.io.*;

/**
 * 将邮件文件，分割成一封邮件一个文件的形式
 */
public class SplitFile {

    public static String split(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        int size = inputStream.available();
        byte[] bytes = new byte[size];
        inputStream.read(bytes);
        // 将文件读取成字符串
        String fileString = new String(bytes, "utf8");
        /**
         * 这种方式截取了之后，末尾少一个 --
         */
        String[] parts = fileString.split("--\\r\\n");
        int num = 0;
        Writer w = null;
        for (String str: parts) {
            num ++ ;
            // 非空的字符串才写入文件
            if(str.trim().length()!=0){
                w = new FileWriter("D:\\test\\temp\\"+num);
                w.write(str.trim()+"--");
                w.flush();
            }
        }
        // 关闭流
        w.close();
        inputStream.close();

        // TODO 待处理
        return "D:\\test\\temp";
    }

    public static void main(String[] args) throws IOException {
        File mailFile = new File("D:\\mail");
        split(mailFile);
    }
}
