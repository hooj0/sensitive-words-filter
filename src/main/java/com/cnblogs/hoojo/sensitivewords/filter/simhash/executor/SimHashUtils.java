package com.cnblogs.hoojo.sensitivewords.filter.simhash.executor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Maps;

/**
 * simhash 算法工具类 
 * 
 * 介绍下这个算法主要原理，为了便于理解尽量不使用数学公式，分为这几步： 
 * 
 * 1、分词，把需要判断文本分词形成这个文章的特征单词。
 * 最后形成去掉噪音词的单词序列并为每个词加上权重，我们假设权重分为5个级别（1~5）。
 * 
 * 比如：“ 美国“51区”雇员称内部有9架飞碟，曾看见灰色外星人 ” ==> 分词后为 “ 美国（4） 51区（5） 雇员（3） 称（1） 内部（2） 有（1） 9架（3） 飞碟（5） 曾（1） 看见（3） 灰色（4） 外星人（5）”，
 * 括号里是代表单词在整个句子里重要程度，数字越大越重要。 
 * 
 * 2、hash，通过hash算法把每个词变成hash值， 
 * 比如“美国”通过hash算法计算为 100101,“51区”通过hash算法计算为 101011。
 * 这样我们的字符串就变成了一串串数字， 还记得文章开头说过的吗，要把文章变为数字计算才能提高相似度计算性能，现在是降维过程进行时。 
 * 
 * 3、加权，通过 2步骤的hash生成结果，需要按照单词的权重形成加权数字串，
 * 比如“美国”的hash值为“100101”，通过加权计算为“4 -4 -4 4 -4 4”； 
 * “51区”的hash值为“101011”，通过加权计算为 “ 5 -5 5 -5 5 5”。
 * 
 * 4、合并，把上面各个单词算出来的序列值累加，变成只有一个序列串。
 *  比如 “美国”的 “4 -4 -4 4 -4 4”，“51区”的 “ 5 -5 5 -5 5 5”，
 *  把每一位进行累加， “4+5 -4+-5 -4+5 4+-5-4+5 4+5” ==》 “9 -9 1 -1 1 9”。 
 *  这里作为示例只算了两个单词的，真实计算需要把所有单词的序列串累加。 
 * 
 * 5、降维，把4步算出来的 “9 -9 1 -1 1 9” 变成 0 1串，形成我们最终的simhash签名。 
 * 	如果每一位大于0 记为 1，小于0 记为 0。
 * 	最后算出结果为：“1 0 1 0 1 1”。 
 * 
 * http://www.lanceyan.com/tag/simhash
 * 
 * 
 * 让我们来总结一下上述算法的实质：<br/>
 *  1、将64位的二进制串等分成四块 <br/>
 *  2、调整上述64位二进制，将任意一块作为前16位，总共有四种组合，生成四份table <br/>
 *  3、采用精确匹配的方式查找前16位<br/>
 *  4、如果样本库中存有2^34（差不多10亿）的哈希指纹，则每个table返回2^(34-16)=262144个候选结果，大大减少了海明距离的计算成本<br/>
 * 
 * 具体simhash步骤如下：
	（1）将文档分词，取一个文章的TF-IDF权重最高的前20个词（feature）和权重（weight）。
		即一篇文档得到一个长度为20的（feature：weight）的集合。
	（2）对其中的词（feature），进行普通的哈希之后得到一个64为的二进制，得到长度为20的（hash : weight）的集合。
	（3）根据（2）中得到一串二进制数（hash）中相应位置是1是0，对相应位置取正值weight和负值weight。
			例如一个词进过（2）得到（010111：5）进过步骤（3）之后可以得到列表[-5,5,-5,5,5,5]，
			即对一个文档，我们可以得到20个长度为64的列表[weight，-weight...weight]。
	（4）对（3）中20个列表进行列向累加得到一个列表。如[-5,5,-5,5,5,5]、[-3,-3,-3,3,-3,3]、[1,-1,-1,1,1,1]进行列向累加得到[-7，1，-9，9，3，9]，这样，我们对一个文档得到，一个长度为64的列表。
	（5）对（4）中得到的列表中每个值进行判断，当为负值的时候去0，正值取1。例如，[-7，1，-9，9，3，9]得到010111，这样，我们就得到一个文档的simhash值了。
	（6）计算相似性。连个simhash取异或，看其中1的个数是否超过3。超过3则判定为不相似，小于等于3则判定为相似。
 * @author hoojo
 * @createDate 2018年3月22日 下午4:50:23
 * @file SimHashUtils.java
 * @package com.cnblogs.hoojo.sensitivewords.filter.bucket.executor
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public abstract class SimHashUtils {

	private static final int HASH_BITS = FNVHashUtils.HASH_BITS;

	/** 默认按照4段进行simhash存储 */
	private static final int CHUNK_COUNT = 4;
	/** 汉明距离的衡量标准 */
	private static final int HAMMING_THRESH = 3;

	public static Long simhash(Map<String, Integer> words) {
		//1、分词：直接加入单词、脏词，所以不存在分词

		// 按照词语的hash值，计算simHashWeight(低位对齐)
		List<Integer[]> mergeWeights = new ArrayList<Integer[]>(words.size());
		
		Set<String> wordSet = words.keySet();
		for (String word : wordSet) {

			//2、hash: 计算分词的hash
			long hash = hash(word);
			
			// 按照词语的hash值，计算simHashWeight(低位对齐)
			Integer[] weights = new Integer[HASH_BITS];
			Arrays.fill(weights, 0);
			
			//3、加权：即W = Hash * weight，且遇到1则hash值和权值正相乘，遇到0则hash值和权值负相乘
			for (int i = 0; i < HASH_BITS; i++) {
				if (((hash >> i) & 1) == 1) {
					// 3.1 正负值weight
					weights[i] += 1;
				} else {
					weights[i] -= 1;
				}
				
				// 3.2 增加权重： W = Hash * weight
				weights[i] *= words.get(word);
			}
			
			mergeWeights.add(weights);
		}
		
		//4、合并：把上面各个单词算出来的序列值累加
		Integer[] weights = new Integer[HASH_BITS];
		Arrays.fill(weights, 0);
		for (Integer[] weight : mergeWeights) {

			for (int i = 0; i < HASH_BITS; i++) {
				weights[i] += weight[i];
			}
		}
		
		//5、降维：大于0 记为 1，小于0 记为 0
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < HASH_BITS; i++) {
			if (weights[i] > 0) {
				sb.append(1);
			} else {
				sb.append(0);
			}
		}
		
		System.out.println(sb);
		return new BigInteger(sb.toString(), 2).longValue();
	}
	
	public static Long simhash_(Map<String, Integer> words) {
		//1、分词：直接加入单词、脏词，所以不存在分词

		// 按照词语的hash值，计算simHashWeight(低位对齐)
		Integer[] weights = new Integer[HASH_BITS];
		Arrays.fill(weights, 0);		
		
		Set<String> wordSet = words.keySet();
		for (String word : wordSet) {

			//2、hash: 计算分词的hash
			long hash = hash(word);
			
			//3、加权：即W = Hash * weight，且遇到1则hash值和权值正相乘，遇到0则hash值和权值负相乘
			for (int i = 0; i < HASH_BITS; i++) {
				Integer weight = 0;
				if (((hash >> i) & 1) == 1) {
					// 3.1 正负值weight
					weight += 1;
				} else {
					weight -= 1;
				}
				
				// 3.2 增加权重： W = Hash * weight
				weight *= words.get(word);
				
				//4、合并：把上面各个单词算出来的序列值累加
				weights[i] += weight;
			}
		}
		
		//5、降维：大于0 记为 1，小于0 记为 0
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < HASH_BITS; i++) {
			if (weights[i] > 0) {
				sb.append(1);
			} else {
				sb.append(0);
			}
		}
		
		System.out.println(sb);
		return new BigInteger(sb.toString(), 2).longValue();
	}
	
	public static Long simhash(String word, int weight) {
		//1、分词：直接加入单词、脏词，所以不存在分词
		
		//2、hash: 计算分词的hash
		long hash = hash(word);
		
		//3、加权：即W = Hash * weight，且遇到1则hash值和权值正相乘，遇到0则hash值和权值负相乘
		Integer[] weights = new Integer[HASH_BITS];
		Arrays.fill(weights, 0);
		// 按照词语的hash值，计算simHashWeight(低位对齐)
		for (int i = 0; i < HASH_BITS; i++) {
			if (((hash >> i) & 1) == 1) {
				// 3.1 正负值weight
				weights[i] += 1;
			} else {
				weights[i] -= 1;
			}
			
			// 3.2 增加权重： W = Hash * weight
			weights[i] *= weight;
		}
		
		//4、合并：把上面各个单词算出来的序列值累加
		// 由于单个词，所以不存在合并
		
		//5、降维：大于0 记为 1，小于0 记为 0
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < HASH_BITS; i++) {
			if (weights[i] > 0) {
				sb.append(1);
			} else {
				sb.append(0);
			}
		}
		
		System.out.println(sb);
		return new BigInteger(sb.toString(), 2).longValue();
	}
	
	public static Long simhash(String word) {
		//1、分词：直接加入单词、脏词，所以不存在分词
		
		//2、hash: 计算分词的hash
		long hash = hash(word);
		
		//3、加权：即W = Hash * weight，且遇到1则hash值和权值正相乘，遇到0则hash值和权值负相乘
		Integer[] weights = new Integer[HASH_BITS];
		Arrays.fill(weights, 0);
		// 按照词语的hash值，计算simHashWeight(低位对齐)
		for (int i = 0; i < HASH_BITS; i++) {
			if (((hash >> i) & 1) == 1) {
				// 3.1 正负值weight
				weights[i] += 1;
			} else {
				weights[i] -= 1;
			}
			
			// 3.2 增加权重： W = Hash * weight
			// 权重都一样，所以不存在权重
		}
		
		//4、合并：把上面各个单词算出来的序列值累加
		// 由于单个词，所以不存在合并
		
		//5、降维：大于0 记为 1，小于0 记为 0
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < HASH_BITS; i++) {
			if (weights[i] > 0) {
				sb.append(1);
			} else {
				sb.append(0);
			}
		}

		return new BigInteger(sb.toString(), 2).longValue();
	}
	
	public static String _simhash(String word) {
		//1、分词：直接加入单词、脏词，所以不存在分词
		
		//2、hash: 计算分词的hash
		long hash = hash(word);
		
		//3、加权：即W = Hash * weight，且遇到1则hash值和权值正相乘，遇到0则hash值和权值负相乘
		Integer[] weights = new Integer[HASH_BITS];
		Arrays.fill(weights, 0);
		// 按照词语的hash值，计算simHashWeight(低位对齐)
		for (int i = 0; i < HASH_BITS; i++) {
			if (((hash >> i) & 1) == 1) {
				// 3.1 正负值weight
				weights[i] += 1;
			} else {
				weights[i] -= 1;
			}
			
			// 3.2 增加权重： W = Hash * weight
			// 权重都一样，所以不存在权重
		}
		
		//4、合并：把上面各个单词算出来的序列值累加
		// 由于单个词，所以不存在合并
		
		//5、降维：大于0 记为 1，小于0 记为 0
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < HASH_BITS; i++) {
			if (weights[i] > 0) {
				sb.append(1);
			} else {
				sb.append(0);
			}
		}

		return sb.toString();
	}
	
	private static long hash(String target) {
		
		//long hash = FNVHashUtils.hash64(target).longValue();
		//long hash = FNVHashUtils.hash64_(target).longValue();
		long hash = Murmur3.hash64(target.getBytes());
		
		return hash;
	}
	
	public static String[] _chunk(Long simhash) {
		String[] chunk = new String[CHUNK_COUNT];
		
		int chunkIndex = 0;
		int offset = HASH_BITS / CHUNK_COUNT;

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < HASH_BITS; i++) {
			sb.append(simhash >> i & 1);
			if ((i + 1) % offset == 0) {
				chunk[chunkIndex++] = sb.toString();
				sb.setLength(0);
			}
		}
		return chunk;
	}
	
	public static String[] chunk(String simhash) {
		
		String[] chunk = new String[CHUNK_COUNT];
		
		int offset = HASH_BITS / CHUNK_COUNT;
		for (int i = 0; i < CHUNK_COUNT; i++) {
			chunk[i] = simhash.substring(i * offset, i * offset + offset);
		}
		
		return chunk;
	}
	
	public static Map<String, Set<String>> cartesianProduct(String[] chunks) {
		Map<String, Set<String>> result = Maps.newHashMap();
		for (int i = 0; i < chunks.length; i++) {
			Set<String> set = new HashSet<String>();
			for (int j = 0; j < chunks.length; j++) {
				if (j != i) {
					set.add(chunks[j]);
				}
			}
			
			result.put(chunks[i], set);
		}
		return result;
	}
	
	public static Map<String, String> _cartesianProduct(String[] chunks, String simhash) {
		Map<String, String> result = Maps.newHashMap();
		for (int i = 0; i < chunks.length; i++) {
			result.put(chunks[i], simhash);
		}
		return result;
	}
	
	public static Map<String, Long> _cartesianProduct(String[] chunks, Long simhash) {
		Map<String, Long> result = Maps.newHashMap();
		for (int i = 0; i < chunks.length; i++) {
			result.put(chunks[i], simhash);
		}
		return result;
	}
	
	public static boolean contains(String word, Map<String, Set<String>> store) {
		
		String simhash = _simhash(word);
		String[] chunks = chunk(simhash);
		for (int i = 0; i < chunks.length; i++) {
			if (!store.containsKey(chunks[i])) {
				continue;
			}
			
			Set<String> set = store.get(chunks[i]);
			for (String hash : set) {
				for (int j = 0; j < chunks.length; j++) {
					if (i != j && hammingDistance(hash, chunks[j]) < HAMMING_THRESH) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public static boolean _contains(String word, Map<String, String> store) {
		
		String simhash = _simhash(word);
		String[] chunks = chunk(simhash);
		for (int i = 0; i < chunks.length; i++) {
			if (!store.containsKey(chunks[i])) {
				continue;
			}
			
			if (hammingDistance(simhash, store.get(chunks[i])) < HAMMING_THRESH) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean __contains(String word, Map<String, Long> store) {
		
		Long simhash = simhash(word);
		String[] chunks = _chunk(simhash);
		for (int i = 0; i < chunks.length; i++) {
			if (!store.containsKey(chunks[i])) {
				continue;
			}
			
			if (hammingDistance(simhash, store.get(chunks[i])) < HAMMING_THRESH) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * 求海明距离 二进制串A 和 二进制串B 的海明距离 就是 A xor B 后二进制中1的个数
	 * @author hoojo
	 * @createDate 2018年3月22日 下午4:53:07
	 */
	public static int hammingDistance(int a, int b) {
		BigInteger _a = BigInteger.valueOf(a);
		BigInteger _b = BigInteger.valueOf(b);

		int distance = 0;

		char[] bit2s = _a.xor(_b).toString(2).toCharArray();
		for (char bit : bit2s) {
			if (bit == '1') {
				distance++;
			}
		}

		return distance;
	}

	public static int hammingDistance(Long a, Long b) {
		int distance = 0;
		for (int i = 0; i < HASH_BITS; i++) {
			if ((a >> i & 1) != (b >> i & 1)) {
				distance++;
			}
		}
		return distance;
	}

	/**
	 * 二进制字符串汉明距离，a=10001 b=01000，distance=3
	 * 
	 * @author hoojo
	 * @createDate 2018年3月22日 下午5:06:42
	 */
	public static int hammingDistance(String a, String b) {
		int distance = 0;

		if (a.length() != b.length()) {
			distance = -1;
		} else {
			for (int i = 0; i < a.length(); i++) {
				if (a.charAt(i) != b.charAt(i)) {
					distance++;
				}
			}
		}
		return distance;
	}

	public static void main(String[] args) {

		Map<String, Integer> words = new HashMap<String, Integer>();
		words.put("CSDN", 5);
		words.put("ABCD", 1);
		words.put("中国", 4);
		
		System.out.println("---------------------");
		System.out.println(simhash(words));
		System.out.println("----------------------");
		System.out.println(simhash_(words));
		System.out.println("----------------------");
		System.out.println(simhash("中国", 5));
		System.out.println("----------------------");
		System.out.println(simhash("中国"));
		System.out.println("----------------------");
		System.out.println("0" + BigInteger.valueOf(simhash("中国")).toString(2));
		System.out.println("----------------------");
		
		Long b = simhash(words);

		words = new HashMap<String, Integer>();
		words.put("CSDN", 5);
		words.put("ABCE", 1);
		words.put("中国", 4);
		Long a = simhash(words);
		
		System.out.println(a);
		System.out.println(b);
		System.out.println(hammingDistance(b, a));
		System.out.println(hammingDistance(simhash("中国"), simhash("中国")));
		
		System.out.println("----------------------");
		System.out.println(StringUtils.join(_chunk(b), ";"));
		System.out.println(StringUtils.join(chunk(_simhash("中国")), ";"));
		
		System.out.println(cartesianProduct(chunk(_simhash("中国"))));
		
		System.out.println("----------------------");
		System.out.println(contains("中间", cartesianProduct(chunk(_simhash("中国")))));
		
	}
}
