package com.cnblogs.hoojo.sensitivewords.filter.ttmp;

import com.cnblogs.hoojo.sensitivewords.filter.AbstractFilter;
import com.cnblogs.hoojo.sensitivewords.filter.ttmp.executor.TtmpFilterExecutor;

/**
 * ttmp 算法过滤
 * 
 * @author hoojo
 * @createDate 2018年3月20日 下午6:06:35
 * @file TtmpSWFilter.java
 * @package com.cnblogs.hoojo.sensitivewords.filter.ttmp
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public class TtmpFilter extends AbstractFilter {

	public TtmpFilter() {
		super(TtmpFilterExecutor.getInstance());
	}
}
