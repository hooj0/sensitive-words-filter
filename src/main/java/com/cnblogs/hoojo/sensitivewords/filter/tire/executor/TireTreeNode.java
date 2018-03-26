package com.cnblogs.hoojo.sensitivewords.filter.tire.executor;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * 多叉树模型
 * 
 * @author hoojo
 * @createDate 2018年2月8日 下午8:23:27
 * @file TireTreeNode.java
 * @project fengkong-service-provider
 * @blog http://hoojo.cnblogs.com
 * @email hoojo_@126.com
 * @version 1.0
 */
public class TireTreeNode {

	private char _char;
	private boolean word;
	private List<TireTreeNode> childs;
	
	public TireTreeNode() {
	}

	public TireTreeNode(char _char) {
		this._char = _char;
	}
	
	public boolean isLeaf() {
		return (childs == null || childs.isEmpty());
	}
	
	public char getChar() {
		return _char;
	}

	public void setChar(char _char) {
		this._char = _char;
	}

	public boolean isWord() {
		return word;
	}

	public void setWord(boolean word) {
		this.word = word;
	}
	
	public List<TireTreeNode> getChilds() {
		return childs;
	}

	public void setChilds(List<TireTreeNode> childs) {
		this.childs = childs;
	}
	
	public void addChild(TireTreeNode child) {
		if (this.childs == null) {
			childs = Lists.newArrayList();
		}
		
		this.childs.add(child);
	}

	public void removeChild(TireTreeNode child) {
		if (this.childs != null) {
			this.childs.remove(child);
		}
	}
	
	public TireTreeNode find(char _char) {
		if (this.childs != null) {
			for (TireTreeNode item : this.childs) {
				if (item.getChar() == _char) {
					return item;
				}
			}
		}
		return null;
	}

	public void print(TireTreeNode node) {
		System.out.println(node.getChar());
		if (node.getChilds() != null) {
			for (TireTreeNode childNode : node.getChilds()) {
				//System.out.println(childNode.getWord());
				print(childNode);
			}
		}
	}
	
	public static void main(String[] args) {
		TireTreeNode node = new TireTreeNode('中');
		
		TireTreeNode g = new TireTreeNode('国');
		g.addChild(new TireTreeNode('人'));
		
		TireTreeNode n = new TireTreeNode('男');
		n.addChild(new TireTreeNode('人'));
		g.addChild(n);
		
		node.addChild(g);
		node.addChild(new TireTreeNode('间'));
		
		node.print(node);
	}
}
