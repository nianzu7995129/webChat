package org.wechat.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.wechat.message.request.ReqImageMessage;
import org.wechat.message.request.ReqTextMessage;
import org.wechat.message.response.Article;
import org.wechat.message.response.RespNewsMessage;
import org.wechat.utils.MessageUtil;

public class ImageMessageservice {
	public static RespNewsMessage ImageMessageService(
			ReqImageMessage reqimageMessage, String respContent) {
		
		System.out.println("imageService");
		
		// 返回消息类
		RespNewsMessage respnewsMessage = new RespNewsMessage();
		respnewsMessage.setToUserName(reqimageMessage.getFromUserName());
		respnewsMessage.setFromUserName(reqimageMessage.getToUserName());
		respnewsMessage.setCreateTime(new Date().getTime());
		respnewsMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_NEWS);
		List<Article> articleList = new ArrayList<Article>();
		Article article1 = new Article();
		article1.setTitle("马来西亚：基本确定飞机残骸属于波音777");
		article1.setDescription("据法新社消息，法国航空官员已开始调查疑似的MH370客机残骸。残骸大约有2米长，看起来像是飞机机翼的一部分，上面还有一个编码BB670。残骸与波音777飞机零件有惊人的相似。");
		article1.setPicUrl("http://www.shujubaosong.com/reportmis//mis2/images/M1.jpg");
		article1.setUrl("http://news.sina.com.cn/w/2015-07-30/142332160665.shtml");

		Article article2 = new Article();
		article2.setTitle("少林寺30名弟子：举报人报复诽谤释永信");
		article2.setDescription("7月28日，刚刚举报过少林寺方丈释永信的举报人释正义又发布了新的“物证”，公布了释永信涉嫌双重户籍的身份信息，同时被指与释永信有情人关系的释延洁的双重户籍信息也被公布。");
		article2.setPicUrl("http://www.shujubaosong.com/weichat/images/S1.jpg");
		article2.setUrl("http://news.sina.com.cn/c/2015-07-30/122532160493.shtml");

		Article article3 = new Article();
		article3.setTitle("少林寺30名弟子：举报人报复诽谤释永信");
		article3.setDescription("7月28日，刚刚举报过少林寺方丈释永信的举报人释正义又发布了新的“物证”，公布了释永信涉嫌双重户籍的身份信息，同时被指与释永信有情人关系的释延洁的双重户籍信息也被公布。");
		article3.setPicUrl("http://www.shujubaosong.com/weichat/images/S1.jpg");
		article3.setUrl("http://news.sina.com.cn/c/2015-07-30/122532160493.shtml");

		articleList.add(article1);
		articleList.add(article2);
		articleList.add(article3);
		// 设置图文消息个数
		respnewsMessage.setArticleCount(articleList.size());
		// 设置图文消息包含的图文集合
		respnewsMessage.setArticles(articleList);

		return respnewsMessage;
	}
}
