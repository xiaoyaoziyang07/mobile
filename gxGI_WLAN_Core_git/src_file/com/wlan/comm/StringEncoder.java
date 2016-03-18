package com.wlan.comm;

/**
 * 字符串编码器类，将字符串转换为指定格式.<br>
 * <br>
 * 参数字典:<br>
 * src - source 来源的简写<br>
 * dst - destnation 目的的简写<br>
 * fnd - find 查找的简写<br>
 * rep - replace 替换的简写<br>
 * idx - index 索引，下标的简写<br>
 * enc - encoding 编码的简写<br>
 * <br>
 * 例子:<br>
 * <%=ArticleFormat.htmlTextEncoder(yourString)%>
 */
public class StringEncoder {
	/**
	 * 将字符串src中的子字符串fnd全部替换为新子字符串rep.<br>
	 * 功能相当于java sdk 1.4的String.replaceAll方法.<br>
	 * 不同之处在于查找时不是使用正则表达式而是普通字符串.
	 */
	public static String replaceAll(String src, String fnd, String rep) throws Exception {
		if (src == null || src.equals("")) {
			return "";
		}

		String dst = src;

		int idx = dst.indexOf(fnd);

		while (idx >= 0) {
			dst = dst.substring(0, idx) + rep + dst.substring(idx + fnd.length(), dst.length());
			idx = dst.indexOf(fnd, idx + rep.length());
		}

		return dst;
	}

	/**
	 * 将字符串src中的子字符串fnd全部替换为新子字符串rep.<br>
	 * 功能相当于java sdk 1.4的String.replaceAll方法.<br>
	 * 不同之处在于查找时不是使用正则表达式而是普通字符串. 本函数、仅替换一次
	 */
	public static String replaceSign(String src, String fnd, String rep) throws Exception {
		if (src == null || src.equals("")) {
			return "";
		}

		String dst = src;

		int idx = dst.indexOf(fnd);

		while (idx >= 0) {
			dst = dst.substring(0, idx) + rep + dst.substring(idx + fnd.length(), dst.length());
			idx = dst.indexOf(fnd, idx + rep.length());
			break;
		}

		return dst;
	}

	/**
	 * 转换为HTML编码.<br>
	 */
	public static String htmlEncoder(String src) throws Exception {
		if (src == null || src.equals("")) {
			return "";
		}

		String dst = src;
		dst = replaceAll(dst, "<", "&lt;");
		dst = replaceAll(dst, ">", "&rt;");
		dst = replaceAll(dst, "\"", "&quot;");
		dst = replaceAll(dst, "'", "&#039;");

		return dst;
	}

	/**
	 * 转换为HTML文字编码.<br>
	 */
	public static String htmlTextEncoder(String src) throws Exception {
		if (src == null || src.equals("")) {
			return "";
		}

		String dst = src;
		dst = replaceAll(dst, "<", "&lt;");
		// dst = replaceAll(dst, ">", "&rt;");
		dst = replaceAll(dst, "\"", "&quot;");
		dst = replaceAll(dst, "'", "&#039;");
		dst = replaceAll(dst, " ", "&nbsp;");
		dst = replaceAll(dst, "\r\n", "<br>");
		dst = replaceAll(dst, "\r", "<br>");
		dst = replaceAll(dst, "\n", "<br>");

		return dst;
	}

	/**
	 * 转换为URL编码.<br>
	 */
	public static String urlEncoder(String src, String enc) throws Exception {
		return java.net.URLEncoder.encode(src, enc);
	}

	/**
	 * 转换为XML编码.<br>
	 */
	public static String xmlEncoder(String src) throws Exception {
		if (src == null || src.equals("")) {
			return "";
		}

		String dst = src;
		dst = replaceAll(dst, "&", "&amp;");
		dst = replaceAll(dst, "<", "&lt;");
		dst = replaceAll(dst, ">", "&gt;");
		dst = replaceAll(dst, "\"", "&quot;");
		dst = replaceAll(dst, "\'", "&acute;");

		return dst;
	}

	/**
	 * 转换为SQL编码.<br>
	 */
	public static String sqlEncoder(String src) throws Exception {
		if (src == null || src.equals("")) {
			return "";
		}

		return replaceAll(src, "'", "''");
	}

	/**
	 * 转换为javascript编码.<br>
	 */
	public static String jsEncoder(String src) throws Exception {
		if (src == null || src.equals("")) {
			return "";
		}

		String dst = src;
		dst = replaceAll(dst, "'", "\\'");
		dst = replaceAll(dst, "\"", "\\\"");
		// dst = replaceAll(dst, "\r\n", "\\\n"); // 和\n转换有冲突
		dst = replaceAll(dst, "\n", "\\\n");
		dst = replaceAll(dst, "\r", "\\\n");

		return dst;
	}
}
