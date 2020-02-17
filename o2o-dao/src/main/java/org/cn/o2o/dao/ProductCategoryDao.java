package org.cn.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.cn.o2o.model.ProductCategory;


public interface ProductCategoryDao {
	/**
	 * 通过shopId查询
	 * @param shopId
	 * @return
	 */
	List<ProductCategory> queryProductCategoryList(long shopId);
	/**
	 * 批量新增商品类别
	 * @param productCategoryList
	 * @return
	 */
	int batchInsertProductCategory(List<ProductCategory> productCategoryList);
	
	/**
	 * 删除指定productCategory
	 * @param productCategoryId
	 * @param shopId
	 * @return
	 */
	int deleteProductCategory(@Param("productCategoryId") long productCategoryId, @Param("shopId") long shopId);
}
