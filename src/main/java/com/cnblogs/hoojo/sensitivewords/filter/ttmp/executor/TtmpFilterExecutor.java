package com.cnblogs.hoojo.sensitivewords.filter.ttmp.executor;

import org.apache.commons.lang.StringUtils;

import com.cnblogs.hoojo.sensitivewords.filter.AbstractFilterExecutor;

/**
 * ttmp 过滤明干成实现
 * 
 * @author hoojo
 * @createDate 2018年3月20日 下午6:09:01
 * @file TtmpFilterExecutor.java
 * @package com.cnblogs.hoojo.sensitivewords.filter.ttmp
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public final class TtmpFilterExecutor extends AbstractFilterExecutor<TtmpCacheNode> {

	private TtmpFilterExecutor() {
		super("ttmp 算法脱敏支持类");
	}
	
	private static class SingleFactory {
		private static final TtmpFilterExecutor INSTANCE = new TtmpFilterExecutor();
	}

	public static final TtmpFilterExecutor getInstance() {
		return SingleFactory.INSTANCE;
	}
	
	@Override
	protected TtmpCacheNode getCacheNodes() {
		return new TtmpCacheNode();
	}
	
	protected boolean processor(boolean partMatch, String content, Callback callback) {
		if (StringUtils.isBlank(content)) {
			return false;
		}
		content = StringUtils.trim(content);
		
		int index = 0;
		while (index < content.length()) {
			int count = 1;

			if (partMatch) {
				if (index > 0 || (cacheNodes.getFastCheck()[content.charAt(index)] & 1) == 0) {
					// 匹配到下一个“可能是脏词”首字符的位置
					while (index < content.length() - 1 && (cacheNodes.getFastCheck()[content.charAt(++index)] & 1) == 0);
				}
			}
			
			// 取得下一个脏词文本的第一个字符
			char begin = content.charAt(index);

			// 表示是简单脏词，单个字脏词
			if (cacheNodes.getMinWordLength() == 1 && cacheNodes.getCharCheck()[begin]) {
				
				if (callback.call(String.valueOf(begin))) {
					return true;
				}
			}
			
			// 比对的次数是 当前文本剩余比对长度 或者 脏词的最大长度
			for (int j = 1; j <= Math.min(cacheNodes.getMaxWordLength(), content.length() - index - 1); j++) {
				char current = content.charAt(index + j);

				if ((cacheNodes.getFastCheck()[current] & 1) == 0) { // 非首字符
					++count;
				}

				if ((cacheNodes.getFastCheck()[current] & (1 << Math.min(j, 7))) == 0) { // 当前字符在脏词中的位置超过7位
					break;
				}

				if (j + 1 >= cacheNodes.getMinWordLength()) { // 当前比对词长度小于等于最大脏词的长度
					// 判断当前字符是否是脏词最后一个字符
					if ((cacheNodes.getFastLength()[begin] & (1 << Math.min(j - 1, 7))) > 0 && cacheNodes.getEndCheck()[current]) {
						String sub = content.substring(index, index + j + 1);
						
						if (cacheNodes.getHash().contains(sub)) { // 判断是否是脏词
							if (callback.call(String.valueOf(sub))) {
								return true;
							}
						}
					}
				}
			}
			
			if (partMatch) {
				index++;
			} else {
				index += count;
			}
		}
		
		return false;
	}

	@Override
	protected boolean put(String word) throws RuntimeException {
		this.cacheNodes.setMaxWordLength(Math.max(this.cacheNodes.getMaxWordLength(), word.length()));
		this.cacheNodes.setMinWordLength(Math.min(this.cacheNodes.getMinWordLength(), word.length()));
		
		for (int i = 0; i < 7 && i < word.length(); i++) {
			byte[] fastCheck = this.cacheNodes.getFastCheck();
			fastCheck[word.charAt(i)] |= (byte) (1 << i);
			
			this.cacheNodes.setFastCheck(fastCheck);
		}

		for (int i = 7; i < word.length(); i++) {
			byte[] fastCheck = this.cacheNodes.getFastCheck();
			fastCheck[word.charAt(i)] |= 0x80;
			
			this.cacheNodes.setFastCheck(fastCheck);
		}

		if (word.length() == 1) {
			cacheNodes.getCharCheck()[word.charAt(0)] = true;
		} else {
			cacheNodes.getEndCheck()[word.charAt(word.length() - 1)] = true;
			
			byte[] fastLength = cacheNodes.getFastLength();
			fastLength[word.charAt(0)] |= (byte) (1 << (Math.min(7, word.length() - 2)));
			
			cacheNodes.setFastLength(fastLength);

			cacheNodes.getHash().add(word);
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		TtmpFilterExecutor.getInstance().init();
		
		TtmpFilterExecutor.getInstance().put("中国人");
		TtmpFilterExecutor.getInstance().put("中国男人");
		TtmpFilterExecutor.getInstance().put("中国人民");
		TtmpFilterExecutor.getInstance().put("人民");
		TtmpFilterExecutor.getInstance().put("中间");
		TtmpFilterExecutor.getInstance().put("女人");
		
		TtmpFilterExecutor.getInstance().put("一");
		TtmpFilterExecutor.getInstance().put("一举成名");
		TtmpFilterExecutor.getInstance().put("一举成名走四方");
		TtmpFilterExecutor.getInstance().put("成名");
		TtmpFilterExecutor.getInstance().put("走四方");
		TtmpFilterExecutor.getInstance().put("是");
		
		String content = "我们中国人都是好人，在他们中间有男人和女人。中国男人很惨，中国人民长期被压迫。";
		System.out.println(TtmpFilterExecutor.getInstance().getWords(true, content));
		System.out.println(TtmpFilterExecutor.getInstance().getWords(false, content));
		System.out.println(TtmpFilterExecutor.getInstance().filter(false, content, '*'));
		System.out.println(TtmpFilterExecutor.getInstance().highlight(false, content));
		
		content = "一举成名走四方的是什么";
		System.out.println(TtmpFilterExecutor.getInstance().getWords(true, content));
		System.out.println(TtmpFilterExecutor.getInstance().getWords(false, content));
		System.out.println(TtmpFilterExecutor.getInstance().filter(true, content, '*'));
		System.out.println(TtmpFilterExecutor.getInstance().highlight(false, content));
	}
}
