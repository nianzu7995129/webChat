package org.wechat.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;
import org.json.JSONObject;

public class FileUtil {

	private static String fileName = "config.json";

	private static String url = null;

	/** 获得WEB-INF路径 */
	private static String getWebRoot() {
		URL url = FileUtil.class.getProtectionDomain().getCodeSource().getLocation();
		String path = url.toString();
		boolean isWin = isWindows();

		int index = path.indexOf("WEB-INF");
		if (index == -1) {
			index = path.indexOf("classes");
		}
		if (index == -1) {
			index = path.indexOf("bin");
		}
		if (index == -1) {
			index = path.length();
		}

		path = path.substring(0, index);
		if (path.startsWith("zip")) {// 当class文件在war中时，此时返回zip:D:/...这样的路径
			path = path.substring(4);
		} else if (path.startsWith("file")) {// 当class文件在class文件中时，此时返回file:/D:/...这样的路径
			path = path.substring(6);
		} else if (path.startsWith("jar")) {// 当class文件在jar文件里面时，此时返回jar:file:/D:/...这样的路径
			path = path.substring(10);
		}
		if (!isWin) {// 如果不是windows 路径以/开头
			if (path.indexOf("/") != 0) {
				path = "/" + path;
			}
		}
		try {
			path = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("path>>" + path);
		return path;
	}

	/**
	 * 判断操作系统是否为windows
	 * 
	 * @return
	 */
	private static boolean isWindows() {
		boolean isWin = false;
		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		if (os.indexOf("win") != -1 || os.indexOf("Win") != -1) {
			isWin = true;
		} else {
			isWin = false;
		}
		return isWin;
	}

	/**
	 * 以字符为单位读取文件，常用于读文本，数字等类型的文件
	 */
	public static String readFileByChars() {
		StringBuffer sb = new StringBuffer();
		String path = getWebRoot();
		File file = null;
		Reader reader = null;
		JSONObject jo = null;
		if (url == null) {
			file = new File(path + File.separator + "WEB-INF" + File.separator + fileName);
			try {
				// 一次读一个字符
				reader = new InputStreamReader(new FileInputStream(file));
				int tempchar;
				while ((tempchar = reader.read()) != -1) {
					// 对于windows下，\r\n这两个字符在一起时，表示一个换行。
					// 但如果这两个字符分开显示时，会换两次行。
					// 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
					if (((char) tempchar) != '\r') {
						sb.append((char) tempchar);
					}
				}
				reader.close();
				jo = new JSONObject(sb.toString());
				url = jo.optString("url");
				if (!url.endsWith("/")) {
					url += "/";
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return url;
	}
}
