package com.lgh.pull2.utils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


/**
 * xml文件处理
 */
public class FileUtil {

    /**
     * xml to bean
     *
     * @param xml
     * @param cls
     * @param <T>
     * @return
     * @throws JAXBException
     */
    public static <T> T ConvertToBean(String xml, Class<T> cls) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(cls);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        T t = (T) unmarshaller.unmarshal(new StringReader(xml));
        return t;
    }

    /**
     * xml to bean
     *
     * @param file
     * @param cls
     * @param <T>
     * @return
     * @throws JAXBException
     */
    public static <T> T ConvertToBean(File file, Class<T> cls) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(cls);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        T t = (T) unmarshaller.unmarshal(file);

        return t;
    }

//    public static String read(String pathName) throws IOException {
//        //读取
////        String pathName = "D:\\1.txt";
////        File file = new File(pathName);
//        StringBuilder result = new StringBuilder();
//        BufferedReader br = new BufferedReader(new FileReader(pathName));
//        String line;
//        while ((line = br.readLine()) != null) {
//            result.append(line + "\r\n");
//        }
//        br.close();
//        return result.toString();
//    }
//
    public static void write(String pathName, String text) throws IOException {
        File file = new File(pathName);
        if(!file.exists()){
            file.createNewFile();
        }

        BufferedWriter br = new BufferedWriter(new FileWriter(pathName));
        br.write(text);
        br.flush();
        br.close();
    }


    public static List<File> getDirectory(File file) {
        List<File> result = new ArrayList<>();
        File flist[] = file.listFiles();
        if (flist == null || flist.length == 0) {
            return null;
        }
        for (File f : flist) {
            if (f.isDirectory()) {
                List<File> files = getDirectory(f);
                for (File file1 : files)
                {
                    result.add(file1);
                }
            } else {
                result.add(f);
            }
        }
        return result;
    }
}
