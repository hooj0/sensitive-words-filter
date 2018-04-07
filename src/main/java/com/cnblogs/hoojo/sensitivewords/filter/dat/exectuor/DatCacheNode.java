package com.cnblogs.hoojo.sensitivewords.filter.dat.exectuor;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * 双数组脏词缓存节点
 * 
 * @author hoojo
 * @createDate 2018年3月21日 下午3:29:03
 * @file DatCacheNode.java
 * @package com.cnblogs.hoojo.sensitivewords.filter.dat.exectuor
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public class DatCacheNode {

	//脏字库
    private Set<Character> chars = Sets.newHashSet();
    
    //敏感词库
    private Set<String> words = Sets.newHashSet();

	public Set<Character> getChars() {
		return chars;
	}

	public void setChars(Set<Character> chars) {
		this.chars = chars;
	}

	public Set<String> getWords() {
		return words;
	}

	public void setWords(Set<String> words) {
		this.words = words;
	}
}
