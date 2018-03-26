package com.cnblogs.hoojo.sensitivewords.filter;

import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;

/**
 * 各算法支持类抽象接口
 * @author hoojo
 * @createDate 2018年2月7日 下午6:35:02
 * @file AbstractSensitiveWordsFilterSupport.java
 * @package com.cnblogs.hoojo.sensitivewords.filter
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class AbstractSensitiveWordsFilterSupport extends AbstractSensitiveWordsFilter {

	private static final String HTML_HIGHLIGHT = "<font color='red'>%s</font>";

	/**
	 * 匹配到敏感词的回调接口
	 * @author hoojo
	 * @createDate 2018年3月21日 上午11:46:15
	 * @param 敏感词对象类型
	 */
	protected interface Callback {
		
		/**
		 * 匹配掉敏感词回调
		 * @author hoojo
		 * @createDate 2018年3月21日 上午11:48:11
		 * @param word 敏感词
		 * @return true 立即停止后续任务并返回，false 继续执行
		 */
		boolean call(String word);
	}
	
	/**
	 * 判断一段文字包含敏感词语，支持敏感词结果回调
	 * @author hoojo
	 * @createDate 2018年2月9日 下午2:54:59
	 * @param partMatch 是否支持匹配词语的一部分
	 * @param content 被匹配内容
	 * @param callback 回调接口
	 * @return 是否匹配到的词语
	 */
	protected abstract boolean processor(boolean partMatch, String content, Callback callback) throws RuntimeException;
	
	@Override
	public boolean contains(boolean partMatch, String content) throws RuntimeException {
		
		return processor(partMatch, content, new Callback() {
			@Override
			public boolean call(String word) {
				return true; // 有敏感词立即返回
			}
		});
	}

	@Override
	public Set<String> getWords(boolean partMatch, String content) throws RuntimeException {
		final Set<String> words = Sets.newHashSet();
		
		processor(partMatch, content, new Callback() {
			@Override
			public boolean call(String word) {
				words.add(word);
				return false; // 继续匹配后面的敏感词
			}
		});
		
		return words;
	}
	
	@Override
	public String highlight(boolean partMatch, String content) throws RuntimeException {
		Set<String> words = this.getWords(false, content);
		
		Iterator<String> iter = words.iterator();
		while (iter.hasNext()) {
			String word = iter.next();
			content = content.replaceAll(word, String.format(HTML_HIGHLIGHT, word));
		}
		
		return content;
	}

	@Override
	public String filter(boolean partMatch, String content, char replaceChar) throws RuntimeException {
		Set<String> words = this.getWords(partMatch, content);
		
		Iterator<String> iter = words.iterator();
		while (iter.hasNext()) {
			String word = iter.next();
			content = content.replaceAll(word, Strings.repeat(String.valueOf(replaceChar), word.length()));
		}
		
		return content;
	}
}
