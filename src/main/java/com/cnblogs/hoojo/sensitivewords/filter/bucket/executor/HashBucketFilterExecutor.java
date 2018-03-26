package com.cnblogs.hoojo.sensitivewords.filter.bucket.executor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cnblogs.hoojo.sensitivewords.filter.AbstractFilterExecutor;
import com.google.common.collect.Maps;

/**
 * hash bucket 脱敏过滤算法实现
 * @author hoojo
 * @createDate 2018年3月21日 下午4:59:33
 * @file HashBucketFilterExecutor.java
 * @package com.cnblogs.hoojo.sensitivewords.filter.bucket.executor
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public final class HashBucketFilterExecutor extends AbstractFilterExecutor<Map<Character, Map<Integer, Set<String>>>> {

	private HashBucketFilterExecutor() {
		super("二级hash(hash bucket)脱敏算法实现");
	}

	private static class SingleFactory {
		private static final HashBucketFilterExecutor INSTANCE = new HashBucketFilterExecutor();
	}

	public static final HashBucketFilterExecutor getInstance() {
		return SingleFactory.INSTANCE;
	}
	
	@Override
	protected Map<Character, Map<Integer, Set<String>>> getCacheNodes() {
		return new HashMap<Character, Map<Integer, Set<String>>>();
	}

	@Override
	protected boolean put(String word) throws RuntimeException {
		
		if (StringUtils.isBlank(word)) {
			return false;
		}
		
		word = StringUtils.trim(word);
		if (word.length() < 2) {
			return false;
		}
		
		
		char firstChar = word.charAt(0);
		
		Map<Integer, Set<String>> buckets = cacheNodes.get(firstChar);
		if (buckets == null) {
			buckets = Maps.newHashMap();
			cacheNodes.put(firstChar, buckets);
		}
		
		Set<String> words = buckets.get(word.length());
		if (words == null) {
			words = new HashSet<String>();
			buckets.put(word.length(), words);
		}
		words.add(word);
		
		return true;
	}

	@Override
	protected boolean processor(boolean partMatch, String content, Callback callback) throws RuntimeException {
		
		if (StringUtils.isBlank(content)) {
			return false;
		}
		
		content = StringUtils.trim(content);
		if (content.length() < 2) {
			return false;
		}
		
		for (int i = 0; i < content.length(); i++) {
            Character wordChar = content.charAt(i);
            
            // 判断是否属于脏字符
            if (!cacheNodes.containsKey(wordChar)) {
                continue;
            }
            
            Map<Integer, Set<String>> buckets = cacheNodes.get(wordChar);
            Set<Integer> sizes = buckets.keySet();
            for (int size : sizes) {
            	
            	if (i + size > content.length()) {
            		continue;
            	}
            	
            	String word = content.substring(i, i + size);
            	Set<String> words = buckets.get(size);
            	// 判断是否是脏词
                if (words.contains(word)) {
                	if (callback.call(word)) {
                		return true;
                	}

                	if (partMatch) {
                		i += word.length();
                	} 
                }
            }
        }
		
		return false;
	}
	
	public static void main(String[] args) {
		HashBucketFilterExecutor.getInstance().init();
		HashBucketFilterExecutor.getInstance().put("中国人");
		HashBucketFilterExecutor.getInstance().put("中国男人");
		HashBucketFilterExecutor.getInstance().put("中国人民");
		HashBucketFilterExecutor.getInstance().put("人民");
		HashBucketFilterExecutor.getInstance().put("中间");
		HashBucketFilterExecutor.getInstance().put("女人");

		HashBucketFilterExecutor.getInstance().put("一举");
		HashBucketFilterExecutor.getInstance().put("一举成名");
		HashBucketFilterExecutor.getInstance().put("一举成名走四方");
		HashBucketFilterExecutor.getInstance().put("成名");
		HashBucketFilterExecutor.getInstance().put("走四方");
		
		String content = "我们中国人都是好人，在他们中间有男人和女人。中国男人很惨，中国人民长期被压迫。";
		System.out.println(HashBucketFilterExecutor.getInstance().getWords(true, content));
		System.out.println(HashBucketFilterExecutor.getInstance().getWords(false, content));
		System.out.println(HashBucketFilterExecutor.getInstance().filter(false, content, '*'));
		System.out.println(HashBucketFilterExecutor.getInstance().highlight(false, content));
		
		content = "一举成名走四方大大的好";
		System.out.println(HashBucketFilterExecutor.getInstance().getWords(true, content));
		System.out.println(HashBucketFilterExecutor.getInstance().getWords(false, content));
		System.out.println(HashBucketFilterExecutor.getInstance().filter(false, content, '*'));
		System.out.println(HashBucketFilterExecutor.getInstance().highlight(false, content));
	}
}
