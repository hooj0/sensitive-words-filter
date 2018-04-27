package com.cnblogs.hoojo.sensitivewords.filter;

import com.cnblogs.hoojo.sensitivewords.cache.JvmWordsCache;
import com.cnblogs.hoojo.sensitivewords.cache.RedisWordsCache;
import com.cnblogs.hoojo.sensitivewords.log.ApplicationLogging;

/**
 * 敏感词库抽象接口实现
 * 
 * @author hoojo
 * @createDate 2018年2月2日 下午4:09:02
 * @file AbstractSensitiveWordsFilter.java
 * @package com.cnblogs.hoojo.sensitivewords
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class AbstractSensitiveWordsFilter extends ApplicationLogging implements SensitiveWordsFilter {

	private volatile static boolean HAS_INIT_WORDS_CACHE = false;
	
	public void initAll() throws Exception {
		
		if (!HAS_INIT_WORDS_CACHE) {
			debug("初始化所有缓存");
			RedisWordsCache.getInstance().init();
			JvmWordsCache.getInstance().init();
			
			this.init();
		} else {
			debug("缓存已被初始化，无需重复执行！");
		}
	}
	
	public void refreshAll() throws Exception {
		
		debug("刷新所有缓存");
		RedisWordsCache.getInstance().refresh();
		JvmWordsCache.getInstance().refresh();
		
		this.refresh();
	}
	
	public abstract void init() throws RuntimeException;
	
	public abstract void refresh() throws RuntimeException;
	
	public abstract void destroy() throws RuntimeException;
}
