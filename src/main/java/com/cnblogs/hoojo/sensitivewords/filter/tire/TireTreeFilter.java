package com.cnblogs.hoojo.sensitivewords.filter.tire;

import com.cnblogs.hoojo.sensitivewords.filter.AbstractFilter;
import com.cnblogs.hoojo.sensitivewords.filter.tire.executor.TireTreeFilterExecutor;

/**
 * trie 树算法实现敏感词脱敏过滤
 * @author hoojo
 * @createDate 2018年2月2日 下午4:22:23
 * @file TrieSWFilter.java
 * @package com.cnblogs.hoojo.sensitivewords.support.trie
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public class TireTreeFilter extends AbstractFilter {

	public TireTreeFilter() {
		super(TireTreeFilterExecutor.getInstance());
	}
}
