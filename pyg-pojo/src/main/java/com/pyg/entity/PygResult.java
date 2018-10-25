package com.pyg.entity;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class PygResult implements Serializable{

	/**
	 *	自定义的返回响应结果类型 
	 */
	private static final long serialVersionUID = 1L;

	public PygResult(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}
	//是否成功
	private boolean success;
	//信息
	private String message;
}
