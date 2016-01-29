package org.wechat.utils;

import java.io.InputStream;  
import java.io.Writer;  
import java.util.ArrayList;
import java.util.HashMap;  
import java.util.Iterator;
import java.util.List;  
import java.util.Map;  
import javax.servlet.http.HttpServletRequest;  
import org.dom4j.Document;  
import org.dom4j.Element;  
import org.dom4j.io.SAXReader;  
import org.wechat.message.response.Article;
import org.wechat.message.response.RespMusicMessage;
import org.wechat.message.response.RespNewsMessage;
import org.wechat.message.response.RespTextMessage;
  



import com.thoughtworks.xstream.XStream;  
import com.thoughtworks.xstream.core.util.QuickWriter;  
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;  
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;  
import com.thoughtworks.xstream.io.xml.XppDriver;  
/** 
 * 消息工具类 
 */  
public class MessageUtil {  
  
    /** 
     * 返回消息类型：文本 
     */  
    public static final String RESP_MESSAGE_TYPE_TEXT = "text";  
  
    /** 
     * 返回消息类型：音乐 
     */  
    public static final String RESP_MESSAGE_TYPE_MUSIC = "music";  
  
    /** 
     * 返回消息类型：图文 
     */  
    public static final String RESP_MESSAGE_TYPE_NEWS = "news";  
  
    /** 
     * 请求消息类型：文本 
     */  
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";  
  
    /** 
     * 请求消息类型：图片 
     */  
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";  
  
    /** 
     * 请求消息类型：链接 
     */  
    public static final String REQ_MESSAGE_TYPE_LINK = "link";  
  
    /** 
     * 请求消息类型：地理位置 
     */  
    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";  
  
    /** 
     * 请求消息类型：音频 
     */  
    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";  
  
    /** 
     * 请求消息类型：推送 
     */  
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";  
  
    /** 
     * 事件类型：subscribe(订阅) 
     */  
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";  
  
    /** 
     * 事件类型：unsubscribe(取消订阅) 
     */  
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";  
    
    /** 
     * 事件类型：scancode_push(扫二维码) 
     */  
    public static final String EVENT_TYPE_SCANCODE_PUSH = "scancode_push";  
    
    /** 
     * 事件类型：scancode_push(扫二维码) 
     */  
    public static final String EVENT_TYPE_SCANCODE_WAITMSG = "scancode_waitmsg";  
  
    /** 
     * 事件类型：CLICK(自定义菜单点击事件) 
     */  
    public static final String EVENT_TYPE_CLICK = "CLICK";  
    
    /** 
     * 事件类型：VIEW(自定义菜单点击跳转链接时的事件) 
     */  
    public static final String EVENT_TYPE_VIEW  = "VIEW"; 
    
    /** 
     * 解析微信发来的请求（XML） 
     *  
     */  
    public static Map<String, Object> parseXml(HttpServletRequest request) throws Exception {  
        // 将解析结果存储在HashMap中  
        Map<String, Object> map = new HashMap<String, Object>();  
  
        // 从request中取得输入流  
        InputStream inputStream = request.getInputStream();  
        // 读取输入流  
        SAXReader reader = new SAXReader();  
        Document document = reader.read(inputStream); 
        if(document == null)  
            return map;  
        
        // 得到xml根元素  
        Element root = document.getRootElement();  
        for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {  
            Element e = (Element) iterator.next();  
            //System.out.println(e.getName());  
            List list = e.elements();  
            if(list.size() > 0){  
                map.put(e.getName(), Dom2Map(e));  
            }else  
                map.put(e.getName(), e.getText());  
        }  
        // 释放资源  
        inputStream.close();  
        inputStream = null;  
  
        return map;  
    }  
    
    public static Map Dom2Map(Element e){  
        Map map = new HashMap();  
        List list = e.elements();  
        if(list.size() > 0){  
            for (int i = 0;i < list.size(); i++) {  
                Element iter = (Element) list.get(i);  
                List mapList = new ArrayList();  
                  
                if(iter.elements().size() > 0){  
                    Map m = Dom2Map(iter);  
                    if(map.get(iter.getName()) != null){  
                        Object obj = map.get(iter.getName());  
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){  
                            mapList = new ArrayList();  
                            mapList.add(obj);  
                            mapList.add(m);  
                        }  
                        if(obj.getClass().getName().equals("java.util.ArrayList")){  
                            mapList = (List) obj;  
                            mapList.add(m);  
                        }  
                        map.put(iter.getName(), mapList);  
                    }else  
                        map.put(iter.getName(), m);  
                }  
                else{  
                    if(map.get(iter.getName()) != null){  
                        Object obj = map.get(iter.getName());  
                        if(!obj.getClass().getName().equals("java.util.ArrayList")){  
                            mapList = new ArrayList();  
                            mapList.add(obj);  
                            mapList.add(iter.getText());  
                        }  
                        if(obj.getClass().getName().equals("java.util.ArrayList")){  
                            mapList = (List) obj;  
                            mapList.add(iter.getText());  
                        }  
                        map.put(iter.getName(), mapList);  
                    }else  
                        map.put(iter.getName(), iter.getText());  
                }  
            }  
        }else  
            map.put(e.getName(), e.getText());  
        return map;  
    }  
    
  
    /** 
     * 文本消息对象转换成xml 
     *  
     */  
    public static String textMessageToXml(RespTextMessage textMessage) {  
        xstream.alias("xml", textMessage.getClass());  
        return xstream.toXML(textMessage);  
    }  
  
    /** 
     * 音乐消息对象转换成xml 
     *  
     */  
    public static String musicMessageToXml(RespMusicMessage musicMessage) {  
        xstream.alias("xml", musicMessage.getClass());  
        return xstream.toXML(musicMessage);  
    }  
  
    /** 
     * 图文消息对象转换成xml 
     *  
     */  
    public static String newsMessageToXml(RespNewsMessage newsMessage) {  
        xstream.alias("xml", newsMessage.getClass());  
        xstream.alias("item", new Article().getClass());  
        return xstream.toXML(newsMessage);  
    }  
  
    /** 
     * 扩展xstream，使其支持CDATA块 
     *  
     */  
    private static XStream xstream = new XStream(new XppDriver() {  
        public HierarchicalStreamWriter createWriter(Writer out) {  
            return new PrettyPrintWriter(out) {  
                // 对所有xml节点的转换都增加CDATA标记  
                boolean cdata = true;  
  
                public void startNode(String name, Class clazz) {  
                    super.startNode(name, clazz);  
                }  
  
                protected void writeText(QuickWriter writer, String text) {  
                    if (cdata) {  
                        writer.write("<![CDATA[");  
                        writer.write(text);  
                        writer.write("]]>");  
                    } else {  
                        writer.write(text);  
                    }  
                }  
            };  
        }  
    });  
}