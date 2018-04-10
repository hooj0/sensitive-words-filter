package com.cnblogs.hoojo.sensitivewords.business.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.cnblogs.hoojo.sensitivewords.business.model.SensitiveWords;

/**
 * <b>function:</b> 敏感词库 MyBatis数据库操作
 * @author hoojo
 * @createDate 2018-02-02 14:54:58
 * @file SensitiveWordsDao.java
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
@Repository
public interface SensitiveWordsDao<T extends SensitiveWords> {

	public int add(T entity) throws Exception;
	
	public int batch(List<T> entity) throws Exception;
	
	public int edit(T entity) throws Exception;
	
	public int remove(T entity) throws Exception;
	
	public Long count(T entity) throws Exception;

	public List<T> query(T entity) throws Exception;
	
	public T get(T entity) throws Exception;
}
