package com.cnblogs.hoojo.sensitivewords.filter.ttmp.executor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class BadWordsFilter {

	// 保存所有脏词
	private HashSet<String> hash = new HashSet<String>();
	
	// 是否是脏词中首字符
	private byte[] fastCheck = new byte[Character.MAX_VALUE];
	// 脏词首字符 长度
	private byte[] fastLength = new byte[Character.MAX_VALUE];
	
	// 判断是否是一个字的脏词
	private Map<Character, Boolean> charCheck = new HashMap<Character, Boolean>();
	// 记录所有脏字中的停止字符
	private Map<Character, Boolean> endCheck = new HashMap<Character, Boolean>();

	// 脏词中长度最大词的length
	private int maxWordLength = 0;
	// 脏词中长度最小词的length
	private int minWordLength = Integer.MAX_VALUE;

	public BadWordsFilter() {
	}

	public void put(String word) {
		maxWordLength = Math.max(maxWordLength, word.length());
		minWordLength = Math.min(minWordLength, word.length());
		
		for (int i = 0; i < 7 && i < word.length(); i++) {
			fastCheck[word.charAt(i)] |= (byte) (1 << i);
		}

		for (int i = 7; i < word.length(); i++) {
			fastCheck[word.charAt(i)] |= 0x80;
		}

		if (word.length() == 1) {
			charCheck.put(word.charAt(0), true);
		} else {
			endCheck.put(word.charAt(word.length() - 1), true);
			fastLength[word.charAt(0)] |= (byte) (1 << (Math.min(7, word.length() - 2)));

			hash.add(word);
		}
	}
	
	public void init(String[] badwords) {
		for (String word : badwords) {
			put(word);
		}
	}

	public boolean contains(String text) {
		int index = 0;

		while (index < text.length()) {
			@SuppressWarnings("unused")
			int count = 1;

			if (index > 0 || (fastCheck[text.charAt(index)] & 1) == 0) {
				// 匹配到下一个“可能是脏词”首字符的位置
				while (index < text.length() - 1 && (fastCheck[text.charAt(++index)] & 1) == 0);
			}
			
			// 取得下一个脏词文本的第一个字符
			char begin = text.charAt(index);

			// 表示是简单脏词，单个字脏词
			if (minWordLength == 1 && charCheck.containsKey(begin)) {
				return true;
			}
			
			// 比对的次数是 当前文本剩余比对长度 或者 脏词的最大长度
			for (int j = 1; j <= Math.min(maxWordLength, text.length() - index - 1); j++) {
				char current = text.charAt(index + j);

				if ((fastCheck[current] & 1) == 0) { // 非首字符
					++count;
				}

				if ((fastCheck[current] & (1 << Math.min(j, 7))) == 0) { // 当前字符在脏词中的位置超过7位
					break;
				}

				if (j + 1 >= minWordLength) { // 当前比对词长度小于等于最大脏词的长度
					System.out.println(begin + "####" + (fastLength[begin] & (1 << Math.min(j - 1, 7))));
					// 判断当前字符是否是脏词最后一个字符
					if ((fastLength[begin] & (1 << Math.min(j - 1, 7))) > 0 && endCheck.containsKey(current)) {
						String sub = text.substring(index, index + j + 1);

						if (hash.contains(sub)) { // 判断是否是脏词
							System.out.println(sub);
							//return true;
						}
					}
				}
			}
			index++;
			//index += count;
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		BadWordsFilter filter = new BadWordsFilter();
		filter.init(new String[] {"一举", "一举成名", "一举成名走四方", "成名", "走四方"/*, "什"*/, "东南西北", "东南西北风呼呼呼的吹"});
		
		String content = "一举成名走四方的是什么，东南西北风呼呼呼的吹";
		System.out.println("***************************************************");
		System.out.println(filter.contains(content));
		System.out.println("***************************************************");
		
		for (char s : content.toCharArray()) {
			System.out.println("check: " + s + "->" + filter.fastCheck[s]);
			System.out.println("length: " + s + "->" + filter.fastLength[s]);
			System.out.println((filter.fastCheck[s] & 1));
		}
	}
}