package com.cnblogs.hoojo.sensitivewords.cache;

import java.util.List;

import com.cnblogs.hoojo.sensitivewords.business.model.SensitiveWords;

/**
 * 敏感词库缓存
 * 
 * @author hoojo
 * @createDate 2018年2月2日 下午4:57:07
 * @file WordsCache.java
 * @package com.cnblogs.hoojo.sensitivewords.cache
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public interface WordsCache {
	
	public void setDataSource(Object dataSource);

	public boolean init() throws Exception;
	
	public boolean put(SensitiveWords words) throws Exception;

	public boolean put(List<SensitiveWords> words) throws Exception;

	public List<SensitiveWords> get() throws Exception;
	
	public boolean remove(SensitiveWords words) throws Exception;
	
	public boolean refresh() throws Exception;

	boolean update(SensitiveWords word) throws Exception;
}
