package com.cnblogs.hoojo.sensitivewords.factory;

import com.cnblogs.hoojo.sensitivewords.filter.AbstractSensitiveWordsFilter;

/**
 * 敏感词库过滤实现工厂
 * @author hoojo
 * @createDate 2018年2月2日 下午4:05:29
 * @file SWFacotry.java
 * @package com.cnblogs.hoojo.sensitivewords.factory
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class SensitiveWordsFactory {

	public static final AbstractSensitiveWordsFilter create(FilterType filterType) throws Exception {
		
		return (AbstractSensitiveWordsFilter) Class.forName(filterType.getClazz().getName()).newInstance();
	}
}
