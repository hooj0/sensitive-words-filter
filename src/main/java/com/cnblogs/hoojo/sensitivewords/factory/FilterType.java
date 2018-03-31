package com.cnblogs.hoojo.sensitivewords.factory;

import com.cnblogs.hoojo.sensitivewords.filter.AbstractSensitiveWordsFilter;
import com.cnblogs.hoojo.sensitivewords.filter.bucket.HashBucketFilter;
import com.cnblogs.hoojo.sensitivewords.filter.dat.DatFilter;
import com.cnblogs.hoojo.sensitivewords.filter.dfa.DfaFilter;
import com.cnblogs.hoojo.sensitivewords.filter.simhash.SimHashFilter;
import com.cnblogs.hoojo.sensitivewords.filter.tire.TireTreeFilter;
import com.cnblogs.hoojo.sensitivewords.filter.ttmp.TtmpFilter;

/**
 * 敏感词算法实现类型
 * @author hoojo
 * @createDate 2018年2月2日 下午4:28:11
 * @file FilterType.java
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public enum FilterType {

	DFA("dfa算法", DfaFilter.class),
	TIRE("tire树算法", TireTreeFilter.class),
	HASH_BUCKET("二级hash算法", HashBucketFilter.class),
	DAT("双数组算法", DatFilter.class),
	TTMP("ttmp算法", TtmpFilter.class),
	SIMHASH("simhash算法", SimHashFilter.class);
	
	private String desc;
	private Class<? extends AbstractSensitiveWordsFilter> clazz;
	
	FilterType(String desc, Class<? extends AbstractSensitiveWordsFilter> clazz) {
		this.desc = desc;
		this.clazz = clazz;
	}
	
	public String getDesc() {
		return desc;
	}

	public Class<? extends AbstractSensitiveWordsFilter> getClazz() {
		return clazz;
	}
}
