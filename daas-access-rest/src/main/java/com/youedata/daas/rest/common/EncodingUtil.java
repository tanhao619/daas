//package com.youedata.daas.rest.common;
//
//
//import info.monitorenter.cpdetector.io.*;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.nio.charset.Charset;
//
///**
// * Created by chengtao on 2017/11/13 0013.
// */
//public class EncodingUtil {
//
//    /**
//     * 获取hdfs文件编码格式
//     * @param filePath 文件路径
//     * @return
//     */
//    public static String getHfdsFileEcode(String filePath){
//        InputStream inputStream = HdfsUtil.getInstance().openFile(filePath);
//        HdfsUtil.getInstance().disconnect();
//        String eCode = getInputStreamEncode(inputStream);
//        if(null != eCode){
//            return eCode;
//        }
//        return "UTF-8";
//    }
//
//    /**
//     * 获得文件流的编码格式
//     *
//     * @param inputStream 文件流
//     */
//    public static String getInputStreamEncode(InputStream inputStream) {
//
//        /*
//         * cpDetector是探测器，它把探测任务交给具体的探测实现类的实例完成。
//         * cpDetector内置了一些常用的探测实现类，这些探测实现类的实例可以通过add方法 加进来，如ParsingDetector、ByteOrderMarkDetector、JChardetFacade、ASCIIDetector、UnicodeDetector。
//         * cpDetector按照“谁最先返回非空的探测结果，就以该结果为准”的原则返回探测到的字符集编码。cpDetector是基于统计学原理的，不保证完全正确。
//         */
//        CodepageDetectorProxy codepageDetector = CodepageDetectorProxy.getInstance();
//        codepageDetector.add(new ParsingDetector(false));//ParsingDetector可用于检查HTML、XML等文件或字符流的编码,构造方法中的参数用于指示是否显示探测过程的详细信息，为false不显示。
//        codepageDetector.add(JChardetFacade.getInstance());//JChardetFacade封装了由Mozilla组织提供的JChardet，它可以完成大多数文件的编码 测定。所以，一般有了这个探测器就可满足大多数项目的要求，如果你还不放心，可以再多加几个探测器，比如下面的ASCIIDetector、UnicodeDetector等。
//        codepageDetector.add(ASCIIDetector.getInstance());//ASCIIDetector用于ASCII编码测定
//        codepageDetector.add(UnicodeDetector.getInstance());//UnicodeDetector用于Unicode家族编码的测定
//        Charset charset = null;
//        try {
//            InputStream in = new ByteArrayInputStream(HdfsUtil.readBytesFromInputStream(inputStream,256L));//添加HdfsDataInputStream支持mark
//            charset = codepageDetector.detectCodepage(in, 256);
//            if (charset != null){
//                return charset.name();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
//}
