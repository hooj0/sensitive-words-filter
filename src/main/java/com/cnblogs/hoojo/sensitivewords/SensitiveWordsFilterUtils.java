package com.cnblogs.hoojo.sensitivewords;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cnblogs.hoojo.sensitivewords.factory.FilterType;
import com.cnblogs.hoojo.sensitivewords.factory.SensitiveWordsFactory;
import com.cnblogs.hoojo.sensitivewords.filter.AbstractSensitiveWordsFilter;
import com.cnblogs.hoojo.sensitivewords.log.ApplicationLogging;
import com.google.common.base.Optional;

/**
 * 分词执行器
 * 
 * @author hoojo
 * @createDate 2018年2月2日 下午4:25:38
 * @file SensitiveWordsFilterUtils.java
 * @package com.cnblogs.hoojo.sensitivewords
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public final class SensitiveWordsFilterUtils extends ApplicationLogging {

	private static class SingleFactory {
		private static final SensitiveWordsFilterUtils INSTANCE = new SensitiveWordsFilterUtils();
	}

	public static final SensitiveWordsFilterUtils getInstance() {
		return SingleFactory.INSTANCE;
	}

	private SensitiveWordsFilterUtils() {
	}

	/**
	 * 是否包含敏感字符
	 * @author hoojo
	 * @createDate 2018年2月9日 下午2:57:52
	 * @param partMatch 是否支持匹配词语的一部分
	 * @param content 被匹配内容
	 * @return 是否包含敏感字符
	 */
	public boolean contains(FilterType type, boolean partMatch, String content) throws Exception {
		type = checkFilterType(type);

		if (StringUtils.isBlank(content)) {
			throw new RuntimeException("必填参数content 为空");
		}
		debug("执行“过滤”敏感词接口：{}，算法：{}", type.getClazz().getSimpleName(), type.getDesc());
		
		AbstractSensitiveWordsFilter wordsFilter = SensitiveWordsFactory.create(type);

		wordsFilter.initAll();
		
		return wordsFilter.contains(partMatch, content);
	}
	
	/**
	 * 返回匹配到的敏感词语
	 * @author hoojo
	 * @createDate 2018年2月9日 下午4:00:06
	 * @param partMatch 是否部分匹配
	 * @param content 被匹配的语句
	 * @return 返回匹配的敏感词语集合
	 */
	public Set<String> getWords(FilterType type, boolean partMatch, String content) throws Exception {
		type = checkFilterType(type);

		if (StringUtils.isBlank(content)) {
			throw new RuntimeException("必填参数content 为空");
		}

		debug("执行“过滤”敏感词接口：{}，算法：{}", type.getClazz().getSimpleName(), type.getDesc());
		AbstractSensitiveWordsFilter wordsFilter = SensitiveWordsFactory.create(type);

		wordsFilter.initAll();
		Set<String> words = wordsFilter.getWords(partMatch, content);

		debug("包含敏感词：{}", words);
		return words;
	}
	
	/**
	 * html高亮敏感词
	 * @author hoojo
	 * @createDate 2018年2月9日 下午4:37:33
	 * @param partMatch 是否部分匹配
	 * @param content 被匹配的语句
	 * @return 返回html高亮敏感词
	 * @throws RuntimeException
	 */
	public String highlight(FilterType type, boolean partMatch, String content) throws Exception {
		type = checkFilterType(type);

		if (StringUtils.isBlank(content)) {
			throw new RuntimeException("必填参数content 为空");
		}

		debug("执行“过滤”敏感词接口：{}，算法：{}", type.getClazz().getSimpleName(), type.getDesc());
		AbstractSensitiveWordsFilter wordsFilter = SensitiveWordsFactory.create(type);

		wordsFilter.initAll();
		content = wordsFilter.highlight(partMatch, content);

		debug("高亮敏感词：{}", content);
		return content;
	}
	
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
	public String filter(FilterType type, boolean partMatch, String target, Character replaceChar) throws Exception {

		replaceChar = Optional.fromNullable(replaceChar).or('*');
		type = checkFilterType(type);

		if (StringUtils.isBlank(target)) {
			throw new RuntimeException("必填参数content 为空");
		}

		debug("执行“过滤”敏感词接口：{}，算法：{}", type.getClazz().getSimpleName(), type.getDesc());
		AbstractSensitiveWordsFilter wordsFilter = SensitiveWordsFactory.create(type);

		wordsFilter.initAll();
		String result = wordsFilter.filter(partMatch, target, replaceChar);

		debug("脱敏结果：{}", result);
		return result;
	}

	public void refresh(FilterType type) throws Exception {

		type = checkFilterType(type);

		debug("执行“刷新”敏感词库缓存接口：{}，算法：{}", type.getClazz().getSimpleName(), type.getDesc());
		AbstractSensitiveWordsFilter wordsFilter = SensitiveWordsFactory.create(type);

		wordsFilter.initAll();
		wordsFilter.refresh();
	}
	
	public void refreshAll(FilterType type) throws Exception {
		
		type = checkFilterType(type);

		debug("执行“刷新”敏感词库和所有缓存接口：{}，算法：{}", type.getClazz().getSimpleName(), type.getDesc());
		AbstractSensitiveWordsFilter wordsFilter = SensitiveWordsFactory.create(type);

		wordsFilter.initAll();
		wordsFilter.refreshAll();
	}
	
	public boolean contains(boolean firstPart, String content) throws Exception {
		
		return contains(null, firstPart, content);
	}
	
	public Set<String> getWords(boolean firstPart, String content) throws Exception {
		
		return getWords(null, firstPart, content);
	}
	
	public String highlight(boolean firstPart, String content) throws Exception {
		
		return highlight(null, firstPart, content);
	}
	
	public String filter(boolean firstPart, String target, Character replaceChar) throws Exception {
		
		return filter(null, firstPart, target, replaceChar);
	}
	
	public void refresh() throws Exception {
		
		this.refresh(null);
	}
	
	public void refreshAll() throws Exception {
		
		this.refreshAll(null);
	}
	
	private FilterType checkFilterType(FilterType type) {
		if (type == null) {
			type = FilterType.DFA;
		}
		return type;
	}
}
