package com.cnblogs.hoojo.sensitivewords.filter.simhash.executor;

import java.math.BigInteger;

/**
 * http://blog.csdn.net/hustfoxy/article/details/23687239
 * https://en.wikipedia.org/wiki/Fowler%E2%80%93Noll%E2%80%93Vo_hash_function
 * 
 * @author hoojo
 * @createDate 2018年3月22日 下午6:48:41
 * @file FNVHashUtils.java
 * @package com.cnblogs.hoojo.sensitivewords.filter.bucket.executor
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class FNVHashUtils {

	public static int HASH_BITS = 64;
	/** 初始的哈希值 */
	public static final BigInteger FNV_64_OFFSET_BASIS = new BigInteger("14695981039346656037");
	/** FNV用于散列的质数 */
	public static final BigInteger FNV_64_PRIME = new BigInteger("1099511628211");
	/** 8位数据（即一个字节） */
	public static final BigInteger FNV_64_OCTET_OF_DATA = BigInteger.ONE.shiftLeft(HASH_BITS).subtract(BigInteger.ONE);

	/**
	 * fnv-1 hash算法，将字符串转换为64位hash值
	 */
	public static BigInteger hash64(String text) {
		BigInteger hash = FNV_64_OFFSET_BASIS;
		
		int len = text.length();
		for (int i = 0; i < len; i++) {
			hash = hash.multiply(FNV_64_PRIME);
			hash = hash.xor(BigInteger.valueOf(text.charAt(i)));
		}
		hash = hash.and(FNV_64_OCTET_OF_DATA);
		
		return hash;
	}

	/**
	 * fnv-1a hash算法，将字符串转换为64位hash值
	 */
	public static BigInteger hash64_(String text) {
		BigInteger hash = FNV_64_OFFSET_BASIS;
		
		int len = text.length();
		for (int i = 0; i < len; i++) {
			hash = hash.xor(BigInteger.valueOf(text.charAt(i)));
			hash = hash.multiply(FNV_64_PRIME);
		}
		hash = hash.and(FNV_64_OCTET_OF_DATA);
		
		return hash;
	}
}
