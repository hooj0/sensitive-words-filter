package com.cnblogs.hoojo.sensitivewords.cache;

import java.util.List;

import com.cnblogs.hoojo.sensitivewords.SensitiveWordsService;
import com.cnblogs.hoojo.sensitivewords.business.model.SensitiveWords;
import com.google.common.collect.Lists;

/**
 * Redis 分布式缓存
 * 
 * @author hoojo
 * @createDate 2018年2月2日 下午4:55:39
 * @file RedisWordsCache.java
 * @package com.cnblogs.hoojo.sensitivewords.cache
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public final class RedisWordsCache extends AbstractWordCache {

	private SensitiveWordsService<SensitiveWords> service;
	
	private RedisWordsCache() {
		super("redis 脱敏词库缓存");
		
		JvmWordsCache.getInstance().setDataSource(this);
	}

	private static class SingleFactory {
		
		private static final RedisWordsCache INSTANCE = new RedisWordsCache();
	}

	public static final RedisWordsCache getInstance() {
		
		return SingleFactory.INSTANCE;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void setDataSource(Object dataSource) {
		super.setDataSource(dataSource);
		
		if (dataSource instanceof SensitiveWordsService) {
			this.service = (SensitiveWordsService<SensitiveWords>) dataSource;
		} else {
			throw new IllegalArgumentException("未知数据源类型" + getListenerName());
		}
	}
	
	@Override
	public boolean init() throws Exception {
		super.init();
		
		/*
		if (RedisUtil.getCountQueue(RedisKeyUtil.FENGKONG_SW_FILTER_PREFIX) == 0) {
			debug("{}: redis cache 首次初始化", getListenerName());
			
			return refresh();
		} else {
			debug("{}: redis缓存已被初始化，无需再执行", getListenerName());
		}
		*/
		return true;
	}
	
	public boolean put(SensitiveWords words) throws Exception {
		super.put(words);
		
		//RedisUtil.push(RedisKeyUtil.FENGKONG_SW_FILTER_PREFIX, toJSON(words));
		return true;
	}

	public boolean put(List<SensitiveWords> words) throws Exception {
		super.put(words);
		
		List<String> rows = Lists.newArrayList();
		for (SensitiveWords word : words) {
			rows.add(toJSON(word));
		}
		
		//RedisUtil.push(RedisKeyUtil.FENGKONG_SW_FILTER_PREFIX, rows);
		return true;
	}

	public List<SensitiveWords> get() throws Exception {
		super.get();
		/*
		JSONObject json = RedisUtil.getQueue(RedisKeyUtil.FENGKONG_SW_FILTER_PREFIX, 0, -1);
		if (json == null) {
			return Lists.newArrayList();
		}
		
		JSONArray rows = json.getJSONArray("rows");
		List<SensitiveWords> words = Lists.newArrayList();
		for (int i = 0; i < rows.size(); i++) {
			words.add(JSONObject.parseObject(rows.get(i).toString(), SensitiveWords.class));
		}
		return words;
		*/
		return null;
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
		/*
		JSONObject json = RedisUtil.getQueue(RedisKeyUtil.FENGKONG_SW_FILTER_PREFIX, 0, -1);
		if (json == null) {
			return false;
		}
		
		List<String> newRows = Lists.newArrayList();

		JSONArray rows = json.getJSONArray("rows");
		for (int i = 0; i < rows.size(); i++) {
			SensitiveWords cacheWord = JSONObject.parseObject(rows.get(i).toString(), SensitiveWords.class);
			
			if (cacheWord.getSensitiveWordsId() == word.getSensitiveWordsId()) {
				continue;
			}
			if (StringUtils.equals(cacheWord.getWord(), word.getWord())) {
				continue;
			}
			
			newRows.add(rows.get(i).toString());
		}
		
		RedisUtil.removeRedisCache(RedisKeyUtil.FENGKONG_SW_FILTER_PREFIX);
		return RedisUtil.push(RedisKeyUtil.FENGKONG_SW_FILTER_PREFIX, rows);
		*/
		return false;
	}

	public boolean refresh() throws Exception {
		super.refresh();
		
		debug("{}: 从新刷新初始化redis缓存", getListenerName());
		/*
		try {
			RedisUtil.removeRedisCache(RedisKeyUtil.FENGKONG_SW_FILTER_PREFIX);
			
			SensitiveWords entity = new SensitiveWords();
			entity.setEnableFlag(EnableState.ENABLE);
			
			List<SensitiveWords> words = service.list(entity);
			
			List<String> jsonWords = Lists.newArrayList();
			for (SensitiveWords word : words) {
				jsonWords.add(toJSON(word));
			}
			
			RedisUtil.push(RedisKeyUtil.FENGKONG_SW_FILTER_PREFIX, jsonWords);
			
			debug("{}: redis 缓存敏感词数量：{}", getListenerName(), words.size());
		} catch(Exception e) {
			throw e;
		}
		*/
		return true;
	}
	
	private String toJSON(SensitiveWords word) {
		/*
		Map<String, Object> map = BeanMapUtils.transBean2Map(word);
		map.put("type", word.getType().name());
		
		return JSON.toJSONString(map);
		*/
		return null;
	}
}
