package com.cnblogs.hoojo.sensitivewords.cache;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cnblogs.hoojo.sensitivewords.business.model.SensitiveWords;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

/**
 * Jvm 敏感词缓存
 * @author hoojo
 * @createDate 2018年2月2日 下午4:56:38
 * @file JvmWordsCache.java
 * @package com.cnblogs.hoojo.sensitivewords.cache
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public final class JvmWordsCache extends AbstractWordCache {

	private WordsCache wordsCache;
	public static List<SensitiveWords> cache = null;

	private static class SingleFactory {
		
		private static final JvmWordsCache INSTANCE = new JvmWordsCache();
	}

	public static final JvmWordsCache getInstance() {
		
		return SingleFactory.INSTANCE;
	}
	
	private JvmWordsCache() {
		super("JVM 脱敏词库缓存");
	}

	@Override
	public void setDataSource(Object dataSource) {
		super.setDataSource(dataSource);

		if (dataSource instanceof WordsCache) {
			this.wordsCache = (WordsCache) dataSource;
		} else {
			throw new IllegalArgumentException("未知数据源类型" + getListenerName());
		}
	}

	@Override
	public boolean init() throws Exception {
		super.init();

		if (cache == null || cache.isEmpty()) {
			debug("{}: jvm cache 首次初始化", getListenerName());
			cache = Lists.newArrayList();

			return refresh();
		} else {
			debug("{}: jvm cache 已被初始化，无需重复执行", getListenerName());
		}

		return true;
	}

	public boolean put(SensitiveWords words) throws Exception {
		super.put(words);

		cache.add(words);
		return true;
	}

	public boolean put(List<SensitiveWords> words) throws Exception {
		super.put(words);

		cache.addAll(words);
		return true;
	}

	public List<SensitiveWords> get() throws Exception {
		super.get();

		return cache;
	}

	public boolean update(SensitiveWords word) throws Exception {
		super.update(word);

		if (remove(word)) {
			return put(word);
		}

		return false;
	}

	public boolean remove(final SensitiveWords word) throws Exception {
		super.remove(word);

		if (word == null) {
			return false;
		}

		return Iterators.removeIf(cache.iterator(), new Predicate<SensitiveWords>() {
			@Override
			public boolean apply(SensitiveWords item) {
				if (word.getSensitiveWordsId() == item.getSensitiveWordsId()) {
					return true;
				}
				if (StringUtils.equals(word.getWord(), item.getWord())) {
					return true;
				}
				return false;
			}
		});
	}

	public boolean refresh() throws Exception {
		super.refresh();

		debug("{}: 从新刷新初始化JVM缓存", getListenerName());
		try {
			cache.clear();
			
			List<SensitiveWords> words = wordsCache.get();
			if (words != null) {
				cache.addAll(words);
			}
			debug("{}: JVM缓存敏感词数量：{}", getListenerName(), cache.size());
		} catch (Exception e) {
			throw e;
		}

		return true;
	}
}