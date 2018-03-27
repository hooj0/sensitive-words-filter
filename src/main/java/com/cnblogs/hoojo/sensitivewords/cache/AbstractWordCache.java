package com.cnblogs.hoojo.sensitivewords.cache;

import java.util.List;

import com.cnblogs.hoojo.sensitivewords.business.model.SensitiveWords;
import com.cnblogs.hoojo.sensitivewords.event.CacheChangedEvent;
import com.cnblogs.hoojo.sensitivewords.event.WordsCacheContext.CacheChangedListener;
import com.cnblogs.hoojo.sensitivewords.log.ApplicationLogging;

/**
 * abstract word cache template method
 * @author hoojo
 * @createDate 2018年2月6日 下午2:33:54
 * @file AbstractWordCache.java
 * @package com.cnblogs.hoojo.sensitivewords.cache
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class AbstractWordCache extends ApplicationLogging implements WordsCache, CacheChangedListener {

	private String listenerName;

	public AbstractWordCache(String listenerName) {
		this.listenerName = listenerName;
	}
	
	public String getListenerName() {
		return listenerName;
	}
	
	@Override
	public void setDataSource(Object dataSource) {
		debug("{}: bindDataSource: {}", listenerName, dataSource);
	}
	
	@Override
	public boolean init() throws Exception {
		debug("{}: init word cache", listenerName);
		
		return true;
	}

	@Override
	public boolean put(SensitiveWords words) throws Exception {
		debug("{}: put word: {}", listenerName, words);
		
		return true;
	}

	@Override
	public boolean put(List<SensitiveWords> words) throws Exception {
		debug("{}: put word list: {}", listenerName, words);
		
		return true;
	}

	@Override
	public List<SensitiveWords> get() throws Exception {
		debug("{}: get word list", listenerName);
		
		return null;
	}
	
	@Override
	public boolean update(SensitiveWords word) throws Exception {
		debug("{}: update word: {}", listenerName, word);
		
		return true;
	}

	@Override
	public boolean remove(SensitiveWords words) throws Exception {
		debug("{}: remove word: {}", listenerName, words);
		
		return false;
	}

	@Override
	public boolean refresh() throws Exception {
		debug("{}: refresh word cache", listenerName);
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void handleChangedEvent(CacheChangedEvent event) throws Exception {
		
		this.init();
		
		switch (event.getAction()) {
			
			case PUT:
				this.put((SensitiveWords) event.getSource());
				break;
				
			case PUT_LIST:
				this.put((List<SensitiveWords>) event.getSource());
				break;
				
			case REMOVE:
				this.remove((SensitiveWords) event.getSource());
				break;
				
			case UPDATE:
				this.update((SensitiveWords) event.getSource());
				break;
				
			case REFRESH:
				this.refresh();
				break;

			default:
				throw new UnsupportedOperationException();
		}
	}
}
