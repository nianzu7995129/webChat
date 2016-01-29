package org.wechat.message.request;


/*请求文本消息*/

public class ReqTextMessage extends ReqBaseMessage {
	
	// 消息内容  
    private String Content;  
  
    public String getContent() {  
        return Content;  
    }  
  
    public void setContent(String content) {  
        Content = content;  
    } 
    
}
