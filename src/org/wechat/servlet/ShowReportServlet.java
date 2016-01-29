package org.wechat.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.wechat.utils.DES3;
import org.wechat.utils.FileUtil;

/**
 * 原微信菜单为click类型，根据KEY值进行跳转，现改为使用view方式
 */
public class ShowReportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String jspName = request.getParameter("yx");
		try {
			jspName = DES3.decode(jspName);
			String host = FileUtil.readFileByChars();
			StringBuffer jspUrl = new StringBuffer(host).append("demo/report/").append(jspName).append(".jsp");
			//StringBuffer jspUrl = new StringBuffer("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx979468582d2cc19d&redirect_uri=http://115.28.43.22/demo/report/report_cgdd.jsp&response_type=code#wechat_redirect");
			System.out.println("jspUrl>>>" + jspUrl);
			response.sendRedirect(jspUrl.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws Exception {
		String aa = "report_spxsfxb";
		String des3encode = DES3.encode(aa);
		System.out.println(des3encode);
		String des3decode = DES3.decode("g1lUF6KrRQr8xmtIZJA7_w,,");
		System.out.println(des3decode);
	}
}
