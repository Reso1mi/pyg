package com.pyg.pojogroup;

import java.io.Serializable;
import java.util.List;

import com.pyg.pojo.TbSpecification;
import com.pyg.pojo.TbSpecificationOption;

public class Specification implements Serializable{

	/**
	 * 商品规格和细节的合体pojo 
	 */
	private static final long serialVersionUID = 1L;

	//这里的名字要和前台html里面的一致
	private TbSpecification specification;

	public TbSpecification getSpecification() {
		return specification;
	}

	public void setSpecification(TbSpecification specification) {
		this.specification = specification;
	}

	public List<TbSpecificationOption> getSpecificationOptionList() {
		return specificationOptionList;
	}

	public void setSpecificationOptionList(List<TbSpecificationOption> specificationOptionList) {
		this.specificationOptionList = specificationOptionList;
	}

	private List<TbSpecificationOption> specificationOptionList;
}
