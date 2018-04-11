package com.cnblogs.hoojo.sensitivewords.business.model;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.cnblogs.hoojo.sensitivewords.business.enums.SensitiveWordsType;


/**
 * 敏感词库
 * @author hoojo
 * @createDate 2018-02-02 14:54:58
 * @file SensitiveWords.java
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public class SensitiveWords implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/** 主键id */
	private Long sensitiveWordsId;
	/** 敏感词 */
	private String word;
	/** 敏感词类型，1：色情，2：政治，3：暴恐，4：民生，5：反动，6：贪腐，7：其他 */
	private SensitiveWordsType type;
	/** 创建人 */
	private String creator;
	/** 创建时间 */
	private Date createTime;
	/** 更新人 */
	private String updater;
	/** 更新时间 */
	private Date updateTime;
	
	public SensitiveWords() {
	}
	
	public SensitiveWords(String word, String creator, String updater) {
		super();
		this.word = word;
		this.creator = creator;
		this.updater = updater;
	}

	public SensitiveWords(String word) {
		super();
	}

	/** 主键id */
	public void setSensitiveWordsId(Long sensitiveWordsId) {
		this.sensitiveWordsId = sensitiveWordsId;
	}
	
	/** 主键id*/
	public Long getSensitiveWordsId() {
		return this.sensitiveWordsId;
	}
	/** 敏感词 */
	public void setWord(String word) {
		this.word = word;
	}
	
	/** 敏感词*/
	public String getWord() {
		return this.word;
	}
	/** 敏感词类型，1：色情，2：政治，3：暴恐，4：民生，5：反动，6：贪腐，7：其他 */
	public void setType(SensitiveWordsType type) {
		this.type = type;
	}
	
	/** 敏感词类型，1：色情，2：政治，3：暴恐，4：民生，5：反动，6：贪腐，7：其他*/
	public SensitiveWordsType getType() {
		return this.type;
	}
	/** 创建人 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	
	/** 创建人*/
	public String getCreator() {
		return this.creator;
	}
	/** 创建时间 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	/** 创建时间*/
	public Date getCreateTime() {
		return this.createTime;
	}
	/** 更新人 */
	public void setUpdater(String updater) {
		this.updater = updater;
	}
	
	/** 更新人*/
	public String getUpdater() {
		return this.updater;
	}
	/** 更新时间 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	
	/** 更新时间*/
	public Date getUpdateTime() {
		return this.updateTime;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}