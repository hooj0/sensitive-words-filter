package com.cnblogs.hoojo.sensitivewords;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cnblogs.hoojo.sensitivewords.business.enums.SensitiveWordsType;
import com.cnblogs.hoojo.sensitivewords.business.model.SensitiveWords;
import com.cnblogs.hoojo.sensitivewords.cache.RedisWordsCache;
import com.cnblogs.hoojo.sensitivewords.factory.FilterType;
import com.cnblogs.hoojo.sensitivewords.filter.AbstractFilterExecutor;
import com.cnblogs.hoojo.sensitivewords.filter.bucket.executor.HashBucketFilterExecutor;
import com.cnblogs.hoojo.sensitivewords.filter.dat.exectuor.DatFilterExecutor;
import com.cnblogs.hoojo.sensitivewords.filter.dfa.executor.DfaFilterExecutor;
import com.cnblogs.hoojo.sensitivewords.filter.tire.executor.TireTreeFilterExecutor;
import com.cnblogs.hoojo.sensitivewords.filter.ttmp.executor.TtmpFilterExecutor;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import junit.framework.TestCase;

/**
 * 敏感词库测试
 * @author hoojo
 * @createDate 2018年2月2日 下午5:40:21
 * @file SensitiveWordsTest.java
 * @package com.hoojo.business.service.mgbase.sensitivewords
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public class SensitiveWordsServiceDBTest extends TestCase {

	private SensitiveWordsService<SensitiveWords> service;
	
	@SuppressWarnings("unchecked")
	public void setUp() {
	}
	
	public void testAdd() {
		SensitiveWords word = new SensitiveWords();
		word.setType(SensitiveWordsType.OTHERS);
		word.setWord("拿回扣");
		
		word.setCreator("1");
		word.setUpdater("2");
		
		try {
			service.add(word);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testBatchAdd() {
		
		try {
			for (SensitiveWordsType type : SensitiveWordsType.values()) {
				
				int i;
				Set<SensitiveWords> words;
				try {
					InputStream stream = SensitiveWordsServiceDBTest.class.getResourceAsStream(type.getName() + "词库.txt");
					BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "gbk"));
					
					i = 0;
					words = Sets.newHashSet();
					while (true) {
						String line = reader.readLine();
						if (line == null) {
							break;
						}
						
						i++;
						
						if (StringUtils.isNotBlank(line)) {
							SensitiveWords entity = new SensitiveWords(line, "admin", "admin");
							entity.setType(type);
							words.add(entity);
							
							try {
								service.add(entity);
							} catch (Exception e) {
								System.err.println(e.getMessage());
							}
						}
					}
					
					System.out.println("循环单词: " + i + ", 插入数据：" + words.size());
					Thread.sleep(1000 * 3);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
			}
			Thread.sleep(1000 * 3);
			
			//service.batch(new ArrayList<>(words), SensitiveWordsType.REACTION);
			//System.out.println("循环单词: " + i + ", 插入数据：" + words.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testBatchAdd2() {
		
		try {
			int i;
			Set<SensitiveWords> words;
			try {
				InputStream stream = SensitiveWordsServiceDBTest.class.getResourceAsStream("敏感词库大全.txt");
				BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "gbk"));
				
				i = 0;
				words = Sets.newHashSet();
				while (true) {
					String line = reader.readLine();
					if (line == null) {
						break;
					}
					
					if (StringUtils.isNotBlank(line)) {
						SensitiveWords entity = new SensitiveWords(line, "admin", "admin");
						entity.setType(SensitiveWordsType.OTHERS);
						words.add(entity);
						
						try {
							service.add(entity);
							i++;
						} catch (Exception e) {
							System.err.println(e.getMessage());
						}
					}
				}
				
				System.out.println("循环单词: " + i + ", 插入数据：" + words.size());
				Thread.sleep(1000 * 3);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testFilter() throws Exception {
		System.out.println(SensitiveWordsFilterUtils.getInstance().filter(FilterType.HASH_BUCKET, false, "尼玛啊,然后，市长仓井空在婚礼上唱春天在哪里。", '*'));
	}
	
	public void testPut() throws Exception {
		RedisWordsCache.getInstance().put(new SensitiveWords("ss", "11", "22"));
		System.out.println(RedisWordsCache.getInstance().get().size());
	}
	
	public void testPutList() throws Exception {
		
		RedisWordsCache.getInstance().put(Lists.newArrayList(new SensitiveWords("ss3", "11", "22"), new SensitiveWords("ss4", "11", "22")));
		System.out.println(RedisWordsCache.getInstance().get().size());
	}
	
	public void testGetCache() {
		try {
			System.out.println(RedisWordsCache.getInstance().get());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testRemove() {
		try {
			System.out.println(RedisWordsCache.getInstance().get());
			RedisWordsCache.getInstance().remove(new SensitiveWords("ss", "11", "22"));
			System.out.println(RedisWordsCache.getInstance().get());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testInit() {
		
		RedisWordsCache.getInstance().setDataSource(service);
		try {
			RedisWordsCache.getInstance().init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testSensitiveWordsFilterUtils() throws Exception {
		System.out.println(SensitiveWordsFilterUtils.getInstance().getWords(false, "我们中国人都是好人，在他们中间有男人和女人。中国男人很惨，中国人民长期被压迫。"));
	}
	
	@SuppressWarnings("rawtypes")
	public void testPerformance() {
		
		AbstractFilterExecutor[] executors = { 
				DfaFilterExecutor.getInstance(), 
				TireTreeFilterExecutor.getInstance(), HashBucketFilterExecutor.getInstance(),
				TtmpFilterExecutor.getInstance(), DatFilterExecutor.getInstance() 
			};
		
		String content = "";
		try {
			InputStreamReader reader = new InputStreamReader(ClassLoader.getSystemResourceAsStream("Talk.txt"), StandardCharsets.UTF_8);
			BufferedReader bufferedReader = new BufferedReader(reader);
			
			while (true) {
				String line = bufferedReader.readLine();
				if (line == null) {
					break;
				}
				
				content += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		for (int i = 0; i < 15; i++) {
			content = content + "，" + content;
		}

		System.out.println("过滤字符数：" + content.length());
		for (AbstractFilterExecutor exec : executors) {
			
			long m = Runtime.getRuntime().freeMemory();
			long start = System.currentTimeMillis();
			exec.init("BadWord.txt");
			System.out.println("初始化填词耗时：" + (System.currentTimeMillis() - start));
			
			start = System.currentTimeMillis();
			Set<String> words = exec.getWords(false, content);
			System.out.println(exec.getListenerName() + ": " + words + ", 数量：" + words.size());
			System.out.println("查找耗时：" + (System.currentTimeMillis() - start));
			//exec.filter(false, content, '*');
			//System.out.println("查找耗时：" + (System.currentTimeMillis() - start));
			System.out.println("内存消耗：" + ((m - Runtime.getRuntime().freeMemory()) / 1024));
		}
	}
}
