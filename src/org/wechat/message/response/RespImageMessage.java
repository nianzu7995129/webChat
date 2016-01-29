package org.wechat.message.response;

/*响应图片消息*/

public class RespImageMessage extends RespBaseMessage {

	// 图片链接  
    private String PicUrl;  
  
    public String getPicUrl() {  
        return PicUrl;  
    }  
  
    public void setPicUrl(String picUrl) {  
        PicUrl = picUrl;  
    }  
	
}
