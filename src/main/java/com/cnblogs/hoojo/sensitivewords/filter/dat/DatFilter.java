package com.cnblogs.hoojo.sensitivewords.filter.dat;

import com.cnblogs.hoojo.sensitivewords.filter.AbstractFilter;
import com.cnblogs.hoojo.sensitivewords.filter.dat.exectuor.DatFilterExecutor;

/**
 * DAT 算法实现敏感词脱敏过滤
 * @author hoojo
 * @createDate 2018年2月2日 下午4:23:20
 * @file DFASWFilter.java
 * @package com.cnblogs.hoojo.sensitivewords.support.dfa
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public class DatFilter extends AbstractFilter {

	public DatFilter() {
		super(DatFilterExecutor.getInstance());
	}
}
