package com.cnblogs.hoojo.sensitivewords.filter;

import java.util.Set;

import com.cnblogs.hoojo.sensitivewords.event.WordsCacheContext;

/**
 * 抽象过滤脱敏接口实现
 * 
 * @author hoojo
 * @createDate 2018年3月22日 上午9:30:25
 * @file AbstractFilter.java
 * @package com.cnblogs.hoojo.sensitivewords.filter
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class AbstractFilter extends AbstractSensitiveWordsFilter {

	private AbstractFilterExecutor<?> executor;
	
	public AbstractFilter(AbstractFilterExecutor<?> executor) {
		
		WordsCacheContext.getInstance().register(this.executor);
		
		this.executor = executor;
	}
	
	@Override
	public boolean contains(boolean partMatch, String content) throws RuntimeException {
		
		return executor.contains(partMatch, content);
	}

	@Override
	public Set<String> getWords(boolean partMatch, String content) throws RuntimeException {
		
		return executor.getWords(partMatch, content);
	}

	@Override
	public String highlight(boolean partMatch, String content) throws RuntimeException {
		
		return executor.highlight(partMatch, content);
	}

	@Override
	public String filter(boolean partMatch, String content, char replaceChar) throws RuntimeException {
		
		return executor.filter(partMatch, content, replaceChar);
	}

	@Override
	public void init() throws RuntimeException {
		
		executor.init();
	}

	@Override
	public void refresh() throws RuntimeException {

		executor.refresh();
	}

	@Override
	public void destroy() throws RuntimeException {

		executor.destroy();
	}
}
