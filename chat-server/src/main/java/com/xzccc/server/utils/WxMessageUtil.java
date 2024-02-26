package com.xzccc.server.utils;

import com.xzccc.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.*;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信消息处理类（微信消息交互大部分就是xml格式交互）
 */
@Slf4j
public class WxMessageUtil {
    /*
     * xml转map
     */
    public static Map<String, String> xmlToMap(HttpServletRequest request) throws IOException, DocumentException {
        HashMap<String, String> map = new HashMap<String, String>();
        SAXReader reader = new SAXReader();

        InputStream ins = request.getInputStream();
        Document doc = reader.read(ins);

        Element root = doc.getRootElement();
        @SuppressWarnings("unchecked")
        List<Element> list = (List<Element>) root.elements();

        for (Element e : list) {
            map.put(e.getName(), e.getText());
        }
        ins.close();
        return map;
    }

    /**
     * 获取公众号回复信息（xml格式）
     */
    public static String getWxReturnMsg(Map<String, String> decryptMap, String content) throws UnsupportedEncodingException {
        log.info("---开始封装xml---decryptMap:" + decryptMap.toString());
        TextMessage textMessage = new TextMessage();
        textMessage.setToUserName(decryptMap.get("FromUserName"));
        textMessage.setFromUserName(decryptMap.get("ToUserName"));
        textMessage.setCreateTime(System.currentTimeMillis());
        textMessage.setMsgType("text"); // 设置回复消息类型
        textMessage.setContent(content); // 设置回复内容
        String xmlMsg = getXmlString(textMessage);
        // 设置返回信息编码,防止中文乱码
        String encodeXmlMsg = new String(xmlMsg.getBytes(), "UTF-8");
        return encodeXmlMsg;
    }

    /**
     * 设置回复消息xml格式
     */
    private static String getXmlString(TextMessage textMessage) {
        String xml = "";
        if (textMessage != null) {
            xml = "<xml>";
            xml += "<ToUserName><![CDATA[";
            xml += textMessage.getToUserName();
            xml += "]]></ToUserName>";
            xml += "<FromUserName><![CDATA[";
            xml += textMessage.getFromUserName();
            xml += "]]></FromUserName>";
            xml += "<CreateTime>";
            xml += textMessage.getCreateTime();
            xml += "</CreateTime>";
            xml += "<MsgType><![CDATA[";
            xml += textMessage.getMsgType();
            xml += "]]></MsgType>";
            xml += "<Content><![CDATA[";
            xml += textMessage.getContent();
            xml += "]]></Content>";
            xml += "</xml>";
        }
        log.info("xml封装结果=>" + xml);
        return xml;
    }

    /**
     * 读取 Request Body 内容作为字符串
     *
     * @param request HttpServletRequest
     * @return XmlString
     * @throws IOException XmlIO
     */
    public static String readRequest(HttpServletRequest request) throws IOException {
        InputStream inputStream;
        StringBuffer sb = new StringBuffer();
        inputStream = request.getInputStream();
        String str;
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        while ((str = in.readLine()) != null) {
            sb.append(str);
        }
        in.close();
        inputStream.close();
        return sb.toString();
    }

    /**
     * 将微信获取的XML结果转为Map
     *
     * @param xmlString xml
     * @return Map
     * @throws DocumentException DocumentException
     */
    public static Map<String, String> ResponseXmlToMap(String xmlString) throws DocumentException {
        // 解析 XML 字符串为 Document 对象
        Document document = DocumentHelper.parseText(xmlString);
        // 获取根元素
        Element rootElement = document.getRootElement();
        // 获取子元素
        List<Element> nodes = rootElement.elements();
        // 获取子元素的文本内容
        Map<String, String> resultMap = new HashMap<>();
        for (Node node : nodes) {
            Element element = (Element) node;
            String nodeName = element.getName();
            String nodeText = element.getTextTrim();
            resultMap.put(nodeName, nodeText);
        }
//        if (resultMap.containsKey("errcode")) {
//            throw new BusinessException("系统异常");
//        }
        return resultMap;
    }


}