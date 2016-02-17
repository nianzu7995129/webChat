package org.wechat.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.wechat.message.request.ReqTextMessage;
import org.wechat.message.response.RespTextMessage;
import org.wechat.service.TextMessageService;
import org.wechat.utils.SignUtil;
import org.wechat.utils.MessageUtil;
import org.wechat.service.ShowReportService;

/**
 * Servlet implementation class CoreServlet
 */
public class CoreServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//System.out.println("coreServlet>>>>>>>doGet>>>>>>");
		// 微信加密签名
		String signature = request.getParameter("signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		// 随机字符串
		String echostr = request.getParameter("echostr");

		// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
		if (SignUtil.checkSignature(signature, timestamp, nonce)) {
			response.getWriter().print(echostr);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");  
        response.setCharacterEncoding("UTF-8");
		String respMessage = "";
		RespTextMessage respTextMessage = new RespTextMessage();
		try {
			System.out.println("coreServlet>>>>>>>doPost>>>>>>");
			Map<String, Object> requestMap = MessageUtil.parseXml(request);
			String fromUserName = (String) requestMap.get("FromUserName");//发送方帐号（open_id）
			String toUserName = (String) requestMap.get("ToUserName");//公众帐号
			String createTime = (String) requestMap.get("CreateTime");//创建时间
			String msgType = (String) requestMap.get("MsgType");//消息类型
		
			System.out.println("--------------------------------------测试信息----------------------------------");
			System.out.println("fromUserName>>" + fromUserName);
			System.out.println("toUserName>>" + toUserName);
			System.out.println("createTime>>" + createTime);
			System.out.println("msgType>>" + msgType);
			System.out.println("-------------------------------------------------------------------------------");
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
				String eventType = (String) requestMap.get("Event");
				if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
					String key = (String) requestMap.get("EventKey");
					ReqTextMessage reqTextMessage = ShowReportService.getReqTextMessage(fromUserName,toUserName,msgType,createTime,key);
					respTextMessage = TextMessageService.TextMessageService(reqTextMessage, "请点击：");
					respMessage = MessageUtil.textMessageToXml(respTextMessage);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 响应消息
		PrintWriter out = response.getWriter();
		out.print(respMessage);
		out.close();
	}
}
