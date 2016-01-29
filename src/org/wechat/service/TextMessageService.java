package org.wechat.service;

import java.util.Date;
import org.wechat.message.request.ReqTextMessage;
import org.wechat.message.response.RespTextMessage;
import org.wechat.utils.MessageUtil;

public class TextMessageService {
	public static RespTextMessage TextMessageService(ReqTextMessage reqtextMessage, String respContent) {
		System.out.println("textService");
		// 返回消息类
		RespTextMessage resptextMessage = new RespTextMessage();
		resptextMessage.setToUserName(reqtextMessage.getFromUserName());
		resptextMessage.setFromUserName(reqtextMessage.getToUserName());
		resptextMessage.setCreateTime(new Date().getTime());
		resptextMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);
		String message = reqtextMessage.getContent();
		respContent = respContent + message;
		resptextMessage.setContent(respContent);
		return resptextMessage;
	}
}
