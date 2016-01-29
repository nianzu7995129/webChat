package org.wechat.service;

import java.util.Date;
import java.util.Map;
import org.wechat.message.request.ReqEventClick;
import org.wechat.message.request.ReqImageMessage;
import org.wechat.message.request.ReqTextMessage;
import org.wechat.message.response.RespNewsMessage;
import org.wechat.message.response.RespTextMessage;
import org.wechat.utils.MessageUtil;
import javax.servlet.http.HttpServletRequest;

public class CoreService {

	/**
	 * 处理微信发来的请求
	 * 
	 * @param request
	 * @return
	 */
	public static String processRequest(Map<String, Object> requestMap) {
		String respMessage = null;
		try {
			//  
			String respContent = "请求处理异常，请稍候尝试！";
			// 发送方帐号（open_id）
			String fromUserName = (String) requestMap.get("FromUserName");
			// 公众帐号
			String toUserName = (String) requestMap.get("ToUserName");
			// 消息类型
			String msgType = (String) requestMap.get("MsgType");
			System.out.println("--------------------------------------测试信息----------------------------------");
			// System.out.println("fromUserName>>" + fromUserName);
			// System.out.println("toUserName>>" + toUserName);
			System.out.println("msgType>>" + msgType);
			System.out.println("-------------------------------------------------------------------------------");

			// 回复文本消息
			RespTextMessage resptextMessage = new RespTextMessage();
			RespNewsMessage respnewsMessage = new RespNewsMessage();

			// 文本消息
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {

				System.out.println("text");

				// respContent = "文本消息！";
				ReqTextMessage reqtextMessage = new ReqTextMessage();
				// 发送方帐号（open_id）
				reqtextMessage.setFromUserName(fromUserName);
				// 公众帐号
				reqtextMessage.setToUserName(toUserName);
				// 消息类型
				reqtextMessage.setMsgType(msgType);
				// 消息创建时间
				reqtextMessage.setCreateTime(Long.parseLong((String) requestMap.get("CreateTime")));
				// 消息内容
				reqtextMessage.setContent((String) requestMap.get("Content"));

				resptextMessage = TextMessageService.TextMessageService(reqtextMessage, respContent);
				respMessage = MessageUtil.textMessageToXml(resptextMessage);
			}
			// 图片消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {

				System.out.println("image");

				ReqImageMessage reqimageMessage = new ReqImageMessage();
				// 发送方帐号（open_id）
				reqimageMessage.setFromUserName(fromUserName);
				// 公众帐号
				reqimageMessage.setToUserName(toUserName);
				// 消息id
				reqimageMessage.setMsgId(Long.parseLong((String) requestMap.get("MsgId")));
				// 消息类型
				reqimageMessage.setMsgType(msgType);
				// 消息创建时间
				reqimageMessage.setCreateTime(Long.parseLong((String) requestMap.get("CreateTime")));

				respnewsMessage = ImageMessageservice.ImageMessageService(reqimageMessage, respContent);
				respMessage = MessageUtil.newsMessageToXml(respnewsMessage);
			}
			// 地理位置消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
				// respContent = "地理位置消息！";
			}
			// 链接消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
				// respContent = "链接消息！";
			}
			// 音频消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
				// respContent = "音频消息！";
			}
			// 事件推送
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
				// 事件类型
				String eventType = (String) requestMap.get("Event");
				// 订阅
				if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
					// respContent = "谢谢您的关注！";
				}
				// 取消订阅
				else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
					// TODO 取消订阅后用户再收不到公众号发送的消息，因此不需要回复消息
				}
				// 自定义菜单click菜单
				else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
					// TODO 自定义菜单权没有开放，暂不处理该类消息

					System.out.println("click");

					String key = (String) requestMap.get("EventKey");
					System.out.println(key);

					// System.out.println("a");
					ReqEventClick reqeventClick = new ReqEventClick();
					// 发送方帐号（open_id）
					reqeventClick.setFromUserName(fromUserName);
					// 公众帐号
					reqeventClick.setToUserName(toUserName);
					// 消息类型
					reqeventClick.setMsgType(msgType);
					// 消息创建时间
					reqeventClick.setCreateTime(Long.parseLong((String) requestMap.get("CreateTime")));
					// 事件类型
					reqeventClick.setEvent((String) requestMap.get("Event"));
					// 事件KEY值
					reqeventClick.setEventKey((String) requestMap.get("EventKey"));
					/*
					 * //获取资源列表 WechatUtil weicht = new WechatUtil(); JSONObject resource = weicht.getResourceList(fromUserName,key); //JSONObject resource = MenuClickService.getResourceList(fromUserName,key); JSONArray re = resource.getJSONArray("resources"); if(re.length() == 0) { resptextMessage.setToUserName(fromUserName); resptextMessage.setFromUserName(toUserName); resptextMessage.setCreateTime(new Date().getTime()); resptextMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT); respContent = "目录中含有0个报表"; resptextMessage.setContent(respContent); respMessage = MessageUtil.textMessageToXml(resptextMessage); } else{ //点击处理（生成图文消息） respnewsMessage = MenuClickService.MenuClick(resource,reqeventClick,respContent); respMessage = MessageUtil.newsMessageToXml(respnewsMessage); }
					 */

				}
				// 自定义菜单view类型菜单
				else if (eventType.equals(MessageUtil.EVENT_TYPE_VIEW)) {
					System.out.print(fromUserName);
				} else if (eventType.equals(MessageUtil.EVENT_TYPE_SCANCODE_WAITMSG)) {
					// ReqEventSCAN reqeventSCAN = new ReqEventSCAN();
					// String scanresult = ;
					// System.out.println(requestMap.toString());
					String res = requestMap.get("ScanCodeInfo").toString();
					System.out.println(res);
					int i = res.indexOf("bind");
					int j = res.indexOf("---");

					resptextMessage.setToUserName(fromUserName);
					resptextMessage.setFromUserName(toUserName);
					resptextMessage.setCreateTime(new Date().getTime());
					resptextMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);

					if (i <= 0 || j <= 0) {
						respContent = "二维码错误！";
					} else {
						String username = res.substring(i + 4, j);
						String md5 = res.substring(j + 3, res.length() - 1);
						// System.out.println(res);
						// System.out.println(username);
						// System.out.println(md5);
						/*
						 * WechatUtil weicht = new WechatUtil(); Boolean resu = weicht.bindOpenId(fromUserName, username, md5); if(resu) { respContent = "绑定成功！"; } else { respContent = "绑定失败！"; }
						 */

					}
					resptextMessage.setContent(respContent);
					respMessage = MessageUtil.textMessageToXml(resptextMessage);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return respMessage;

	}

}
