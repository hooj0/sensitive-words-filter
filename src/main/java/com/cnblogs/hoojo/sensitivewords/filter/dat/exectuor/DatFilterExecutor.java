package com.cnblogs.hoojo.sensitivewords.filter.dat.exectuor;

import org.apache.commons.lang.StringUtils;

import com.cnblogs.hoojo.sensitivewords.filter.AbstractFilterExecutor;

/**
 * 双数组算法过滤敏感词
 * @author hoojo
 * @createDate 2018年3月21日 下午3:28:21
 * @package com.cnblogs.hoojo.sensitivewords.filter.dat.exectuor
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public final class DatFilterExecutor extends AbstractFilterExecutor<DatCacheNode> {

	private DatFilterExecutor() {
		super("dat 双数组算法脱敏实现");
	}
	
	private static class SingleFactory {
		private static final DatFilterExecutor INSTANCE = new DatFilterExecutor();
	}

	public static final DatFilterExecutor getInstance() {
		return SingleFactory.INSTANCE;
	}

	@Override
	protected DatCacheNode getCacheNodes() {
		return new DatCacheNode();
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
		
		cacheNodes.getWords().add(word);
		
        for (Character character : word.toCharArray()) {
        	cacheNodes.getChars().add(character);
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
            if (!cacheNodes.getChars().contains(wordChar)) {
                continue;
            }
            
            int j = i + 1;
            while (j < content.length()) {
            	
            	// 判断下一个字符是否属于脏字符
            	wordChar = content.charAt(j);
                if (!cacheNodes.getChars().contains(wordChar)) {
                    break;
                }
                
                String word = content.substring(i, j + 1);
                // 判断是否是脏词
                if (cacheNodes.getWords().contains(word)) {
                    
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
		DatFilterExecutor.getInstance().init();
		DatFilterExecutor.getInstance().put("中国人");
		DatFilterExecutor.getInstance().put("中国男人");
		DatFilterExecutor.getInstance().put("中国人民");
		DatFilterExecutor.getInstance().put("人民");
		DatFilterExecutor.getInstance().put("中间");
		DatFilterExecutor.getInstance().put("女人");

		DatFilterExecutor.getInstance().put("一举");
		DatFilterExecutor.getInstance().put("一举成名");
		DatFilterExecutor.getInstance().put("一举成名走四方");
		DatFilterExecutor.getInstance().put("成名");
		DatFilterExecutor.getInstance().put("走四方");
		
		String content = "我们中国人都是好人，在他们中间有男人和女人。中国男人很惨，中国人民长期被压迫。";
		System.out.println(DatFilterExecutor.getInstance().getWords(true, content));
		System.out.println(DatFilterExecutor.getInstance().getWords(false, content));
		System.out.println(DatFilterExecutor.getInstance().filter(false, content, '*'));
		System.out.println(DatFilterExecutor.getInstance().highlight(false, content));
		
		content = "一举成名走四方的是什么";
		System.out.println(DatFilterExecutor.getInstance().getWords(true, content));
		System.out.println(DatFilterExecutor.getInstance().getWords(false, content));
		System.out.println(DatFilterExecutor.getInstance().filter(false, content, '*'));
		System.out.println(DatFilterExecutor.getInstance().highlight(false, content));
	}
}
