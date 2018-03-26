package com.cnblogs.hoojo.sensitivewords.filter.ttmp.executor;

import java.util.HashSet;

/**
 * 数据缓存节点
 * 
 * @author hoojo
 * @createDate 2018年3月20日 下午6:14:53
 * @file TtmpCacheNode.java
 * @package com.cnblogs.hoojo.sensitivewords.filter.ttmp.executor
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public class TtmpCacheNode {

	// 保存所有脏词
	private HashSet<String> hash = new HashSet<String>();

	// 是否是脏词中首字符
	private byte[] fastCheck = new byte[Character.MAX_VALUE];
	// 脏词首字符 长度
	private byte[] fastLength = new byte[Character.MAX_VALUE];

	// 判断是否是一个字的脏词
	private boolean[] charCheck = new boolean[Character.MAX_VALUE];
	// 记录所有脏字中的停止字符
	private boolean[] endCheck = new boolean[Character.MAX_VALUE];

	// 脏词中长度最大词的length
	private int maxWordLength = 0;
	// 脏词中长度最小词的length
	private int minWordLength = Integer.MAX_VALUE;

	public HashSet<String> getHash() {
		return hash;
	}

	public void setHash(HashSet<String> hash) {
		this.hash = hash;
	}

	public byte[] getFastCheck() {
		return fastCheck;
	}

	public void setFastCheck(byte[] fastCheck) {
		this.fastCheck = fastCheck;
	}

	public byte[] getFastLength() {
		return fastLength;
	}

	public void setFastLength(byte[] fastLength) {
		this.fastLength = fastLength;
	}

	public int getMaxWordLength() {
		return maxWordLength;
	}

	public void setMaxWordLength(int maxWordLength) {
		this.maxWordLength = maxWordLength;
	}

	public int getMinWordLength() {
		return minWordLength;
	}

	public void setMinWordLength(int minWordLength) {
		this.minWordLength = minWordLength;
	}
	
	public boolean[] getCharCheck() {
		return charCheck;
	}

	public void setCharCheck(boolean[] charCheck) {
		this.charCheck = charCheck;
	}

	public boolean[] getEndCheck() {
		return endCheck;
	}

	public void setEndCheck(boolean[] endCheck) {
		this.endCheck = endCheck;
	}
}
