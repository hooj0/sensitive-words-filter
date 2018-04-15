package com.cnblogs.hoojo.sensitivewords.filter.bucket;

import com.cnblogs.hoojo.sensitivewords.filter.AbstractFilter;
import com.cnblogs.hoojo.sensitivewords.filter.bucket.executor.HashBucketFilterExecutor;

/**
 * hash bucket 脱敏过滤算法实现
 * 
 * @author hoojo
 * @createDate 2018年3月22日 上午9:25:16
 * @file HashBucketFilter.java
 * @package com.cnblogs.hoojo.sensitivewords.filter.bucket
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public class HashBucketFilter extends AbstractFilter {

	public HashBucketFilter() {
		super(HashBucketFilterExecutor.getInstance());
	}
}
