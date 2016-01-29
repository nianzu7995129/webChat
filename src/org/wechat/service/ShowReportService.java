package org.wechat.service;

import org.wechat.message.request.ReqTextMessage;
import org.wechat.utils.DES3;
import org.wechat.utils.FileUtil;

public class ShowReportService {

	public static ReqTextMessage getReqTextMessage(String fromUserName, String toUserName, String msgType, String createTime, String key) throws Exception {
		System.out.println("ReqTextMessage>>>------------------");
		String jspName = DES3.encode(key);
		String host = FileUtil.readFileByChars();
		String content = host + "demo/ShowReportServlet?yx=" + jspName;
		//content = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx3a70c3685a49a6a8&redirect_uri=http://115.28.43.22/demo/report/index.jsp&response_type=code&scope=snsapi_base#wechat_redirect";
		System.out.println("jspcontent>>>" + content);
		ReqTextMessage reqtextMessage = new ReqTextMessage();
		// 发送方帐号（open_id）
		reqtextMessage.setFromUserName(fromUserName);
		// 公众帐号
		reqtextMessage.setToUserName(toUserName);
		// 消息id
		reqtextMessage.setMsgId(0);
		// 消息类型
		reqtextMessage.setMsgType(msgType);
		// 消息创建时间
		reqtextMessage.setCreateTime(Long.parseLong(createTime));
		// 消息内容
		reqtextMessage.setContent(content);
		return reqtextMessage;
	}
}
