package com.pyg.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class PageResult implements Serializable{
	/**
	 * 封装分页结果集对像 
	 */
	private static final long serialVersionUID = 1L;

	private long total; //总记录数
	
	private List rows; //记录

	public PageResult(long total, List rows) {
		super();
		this.total = total;
		this.rows = rows;
	}
}
