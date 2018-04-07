package com.cnblogs.hoojo.sensitivewords.filter.simhash;

import com.cnblogs.hoojo.sensitivewords.filter.AbstractFilter;
import com.cnblogs.hoojo.sensitivewords.filter.simhash.executor.SimHashFilterExecutor;

/**
 * simhash 算法
 * 
 * @author hoojo
 * @createDate 2018年3月23日 下午5:55:49
 * @file SimHashFilter.java
 * @package com.cnblogs.hoojo.sensitivewords.filter.simhash.executor
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public class SimHashFilter extends AbstractFilter {

	public SimHashFilter() {
		super(SimHashFilterExecutor.getInstance());
	}
}
