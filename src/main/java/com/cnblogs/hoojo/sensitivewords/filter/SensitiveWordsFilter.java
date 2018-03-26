package com.cnblogs.hoojo.sensitivewords.filter;

import java.util.Set;

/**
 * 敏感词库接口
 * @author hoojo
 * @createDate 2018年2月2日 下午4:03:46
 * @file SensitiveWordsFilter.java
 * @package com.cnblogs.hoojo.sensitivewords
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public interface SensitiveWordsFilter {

	/**
	 * 是否包含敏感字符
	 * @author hoojo
	 * @createDate 2018年2月9日 下午2:57:52
	 * @param partMatch 是否支持匹配词语的一部分
	 * @param content 被匹配内容
	 * @return 是否包含敏感字符
	 */
	public boolean contains(boolean partMatch, String content) throws RuntimeException;
	
	/**
	 * 返回匹配到的敏感词语
	 * @author hoojo
	 * @createDate 2018年2月9日 下午4:00:06
	 * @param partMatch 是否部分匹配
	 * @param content 被匹配的语句
	 * @return 返回匹配的敏感词语集合
	 */
	public Set<String> getWords(boolean partMatch, String content) throws RuntimeException;
	
	/**
	 * html高亮敏感词
	 * @author hoojo
	 * @createDate 2018年2月9日 下午4:37:33
	 * @param partMatch 是否部分匹配
	 * @param content 被匹配的语句
	 * @return 返回html高亮敏感词
	 * @throws RuntimeException
	 */
	public String highlight(boolean partMatch, String content) throws RuntimeException;
	
	/**
	 * 过滤敏感词，并把敏感词替换为指定字符
	 * @author hoojo
	 * @createDate 2018年2月9日 下午4:38:12
	 * @param partMatch 是否部分匹配
	 * @param content 被匹配的语句
	 * @param replaceChar 替换字符
	 * @return 过滤后的字符串
	 * @throws RuntimeException
	 */
	public String filter(boolean partMatch, String content, char replaceChar) throws RuntimeException;
	
}
