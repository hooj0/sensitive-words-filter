package com.cnblogs.hoojo.sensitivewords.filter.dfa.executor;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

import com.cnblogs.hoojo.sensitivewords.filter.AbstractFilterExecutor;
import com.google.common.collect.Maps;

/**
 * DFA 脱敏算法实现支持类
 * 
 * @author hoojo
 * @createDate 2018年2月9日 上午10:34:42
 * @file DfaFilterExecutor.java
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public final class DfaFilterExecutor extends AbstractFilterExecutor<HashMap<Character, DfaNode>> {

	private static class SingleFactory {
		private static final DfaFilterExecutor INSTANCE = new DfaFilterExecutor();
	}

	public static final DfaFilterExecutor getInstance() {
		return SingleFactory.INSTANCE;
	}
	
	private DfaFilterExecutor() {
		super("DFA 脱敏算法实现支持类");
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
		
		Character fisrtChar = word.charAt(0);
		DfaNode node = cacheNodes.get(fisrtChar);
		if (node == null) {
			node = new DfaNode(fisrtChar);
			cacheNodes.put(fisrtChar, node);
		}
		
		for (int i = 1; i < word.length(); i++) {
			Character nextChar = word.charAt(i); 
			
			DfaNode nextNode = null;
			if (!node.isLeaf()) {
				nextNode = node.getChilds().get(nextChar);
			} 
			if (nextNode == null) {
				nextNode = new DfaNode(nextChar);
			}
			
			node.addChild(nextNode);
			node = nextNode;
			
			if (i == word.length() - 1) {
				node.setWord(true);
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
		
		for (int index = 0; index < content.length(); index++) {
			char fisrtChar = content.charAt(index);
			
			DfaNode node = cacheNodes.get(fisrtChar);
			if (node == null || node.isLeaf()) {
				continue;
			} 
			
			int charCount = 1;
			for (int i = index + 1; i < content.length(); i++) {
				char wordChar = content.charAt(i);
				
				node = node.getChilds().get(wordChar);
				if (node != null) {
					charCount++;
				} else {
					break;
				}
				
				if (partMatch && node.isWord()) {
					if (callback.call(StringUtils.substring(content, index, index + charCount))) {
						return true;
					}
					break;
				} else if (node.isWord()) {
					if (callback.call(StringUtils.substring(content, index, index + charCount))) {
						return true;
					}
				}
				
				if (node.isLeaf()) {
					break;
				}
			}
			
			if (partMatch) {
				index += charCount;
			}
		}
		
		return false;
	}
	
	@Override
	protected HashMap<Character, DfaNode> getCacheNodes() {
		return Maps.newHashMap();
	}
	
	public static void main(String[] args) {
		DfaFilterExecutor.getInstance().init();
		DfaFilterExecutor.getInstance().put("中国人");
		DfaFilterExecutor.getInstance().put("中国男人");
		DfaFilterExecutor.getInstance().put("中国人民");
		DfaFilterExecutor.getInstance().put("人民");
		DfaFilterExecutor.getInstance().put("中间");
		DfaFilterExecutor.getInstance().put("女人");

		DfaFilterExecutor.getInstance().put("一举");
		DfaFilterExecutor.getInstance().put("一举成名");
		DfaFilterExecutor.getInstance().put("一举成名走四方");
		DfaFilterExecutor.getInstance().put("成名");
		DfaFilterExecutor.getInstance().put("走四方");
		
		String content = "我们中国人都是好人，在他们中间有男人和女人。中国男人很惨，中国人民长期被压迫。";
		System.out.println(DfaFilterExecutor.getInstance().contains(true, content));
		System.out.println(DfaFilterExecutor.getInstance().getWords(true, content));
		System.out.println(DfaFilterExecutor.getInstance().getWords(false, content));
		System.out.println(DfaFilterExecutor.getInstance().filter(false, content, '*'));
		System.out.println(DfaFilterExecutor.getInstance().highlight(false, content));
		
		content = "一举成名走四方的是什么";
		System.out.println(DfaFilterExecutor.getInstance().getWords(true, content));
		System.out.println(DfaFilterExecutor.getInstance().getWords(false, content));
		System.out.println(DfaFilterExecutor.getInstance().filter(false, content, '*'));
		System.out.println(DfaFilterExecutor.getInstance().highlight(false, content));
	}
}
