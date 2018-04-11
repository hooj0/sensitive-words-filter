package com.cnblogs.hoojo.sensitivewords.event;

import java.util.Enumeration;
import java.util.Vector;

import com.cnblogs.hoojo.sensitivewords.cache.JvmWordsCache;
import com.cnblogs.hoojo.sensitivewords.cache.RedisWordsCache;
import com.cnblogs.hoojo.sensitivewords.log.ApplicationLogging;

/**
 * 目标事件源对象上下文
 * @author hoojo
 * @createDate 2018年2月5日 下午5:22:47
 * @file TargetEventSource.java
 * @package com.cnblogs.hoojo.sensitivewords.event
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public final class WordsCacheContext extends ApplicationLogging {

	private Vector<CacheChangedListener> listeners = new Vector<CacheChangedListener>();

	private static class SingleFactory {
		
		private static final WordsCacheContext INSTANCE = new WordsCacheContext();
	}

	public static final WordsCacheContext getInstance() {
		
		return SingleFactory.INSTANCE;
	}
	
	private WordsCacheContext() {
		
		try {
			register(RedisWordsCache.getInstance());
			register(JvmWordsCache.getInstance());
		} catch (Exception e) {
			error(e);
			throw e;
		}
	}
	
	public void register(CacheChangedListener listener) {
		
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public void dispatchChanged(CacheChangedEvent event) throws Exception {
		
		event.doEvent();
		
		Enumeration<CacheChangedListener> enums = listeners.elements();
		while (enums.hasMoreElements()) {
			CacheChangedListener listener = enums.nextElement();

			try {
				info("触发事件：{}，执行监听业务：{}，数据：{}", event.getAction(), listener.getListenerName(), event.getSource());
				listener.handleChangedEvent(event);
			} catch (Exception e) {
				throw e;
			}
		}
	}

	public interface CacheChangedListener extends java.util.EventListener {

		public void handleChangedEvent(CacheChangedEvent event) throws Exception;
		
		public String getListenerName();
	}
}
