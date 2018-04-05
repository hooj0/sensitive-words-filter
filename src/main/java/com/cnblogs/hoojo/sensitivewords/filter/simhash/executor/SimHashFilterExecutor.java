package com.cnblogs.hoojo.sensitivewords.filter.simhash.executor;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cnblogs.hoojo.sensitivewords.filter.AbstractFilterExecutor;
import com.google.common.collect.Maps;

/**
 * google simhash 算法实现脱敏过滤
 * 
 * 由于simhash是对大文本进行比较，并且比较的是在支持分词的基础上对分词对象进行比较，进而确定相识度。
 * 故 在脱敏方面支持不是很友好，在大文本情况下，效率低下。
 * 改变情况，需要分词库支持。
 * 
 * @author hoojo
 * @createDate 2018年3月22日 上午11:07:47
 * @file SimHashFilterExecutor.java
 * @package com.cnblogs.hoojo.sensitivewords.filter.simhash.executor
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public final class SimHashFilterExecutor extends AbstractFilterExecutor<Map<Character, Map<String, Set<String>>>> {

	private SimHashFilterExecutor() {
		super("simhash 算法脱敏实现");
	}
	
	private static class SingleFactory {
		private static final SimHashFilterExecutor INSTANCE = new SimHashFilterExecutor();
	}

	public static final SimHashFilterExecutor getInstance() {
		return SingleFactory.INSTANCE;
	}

	@Override
	protected Map<Character, Map<String, Set<String>>> getCacheNodes() {
		return Maps.newHashMap();
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
		
		Character firstChar = word.charAt(0);
		
		Map<String, Set<String>> hashs = cacheNodes.get(firstChar);
		if (hashs == null) {
			hashs = Maps.newHashMap();
			cacheNodes.put(firstChar, hashs);
		}
		
		String hash = SimHashUtils._simhash(word);
		String[] chunks = SimHashUtils.chunk(hash);
		
		Map<String, Set<String>> map = SimHashUtils.cartesianProduct(chunks);
		Set<String> keys = map.keySet();
		for (String chunk : keys) {
			if (!hashs.containsKey(chunk)) {
				hashs.put(chunk, map.get(chunk));
			}
		}
		
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
			
            Map<String, Set<String>> hashs = cacheNodes.get(wordChar);
            int j = i + 1;
            while (j < content.length()) {
                String word = content.substring(i, j + 1);
                // 判断是否是脏词
                if (SimHashUtils.contains(word, hashs)) {
                    
                	if (callback.call(word)) {
                		return true;
                	}

                	if (partMatch) {
                		i += word.length();
                	} 
                }
                
                j++;
            }
        }
		
		return false;
	}

	public static void main(String[] args) {
		
		SimHashFilterExecutor.getInstance().init();
		SimHashFilterExecutor.getInstance().put("中国人");
		SimHashFilterExecutor.getInstance().put("中国男人");
		SimHashFilterExecutor.getInstance().put("中国人民");
		SimHashFilterExecutor.getInstance().put("人民");
		SimHashFilterExecutor.getInstance().put("中间");
		SimHashFilterExecutor.getInstance().put("女人");

		SimHashFilterExecutor.getInstance().put("一举");
		SimHashFilterExecutor.getInstance().put("一举成名");
		SimHashFilterExecutor.getInstance().put("一举成名走四方");
		SimHashFilterExecutor.getInstance().put("成名");
		SimHashFilterExecutor.getInstance().put("走四方");
		
		String content = "我们中国人都是好人，在他们中间有男人和女人。中国男人很惨，中国人民长期被压迫。";
		System.out.println(SimHashFilterExecutor.getInstance().getWords(true, content));
		System.out.println(SimHashFilterExecutor.getInstance().getWords(false, content));
		System.out.println(SimHashFilterExecutor.getInstance().filter(false, content, '*'));
		System.out.println(SimHashFilterExecutor.getInstance().highlight(false, content));
		
		content = "一举成名走四方大大的好";
		System.out.println(SimHashFilterExecutor.getInstance().getWords(true, content));
		System.out.println(SimHashFilterExecutor.getInstance().getWords(false, content));
		System.out.println(SimHashFilterExecutor.getInstance().filter(false, content, '*'));
		System.out.println(SimHashFilterExecutor.getInstance().highlight(false, content));
	}
}
