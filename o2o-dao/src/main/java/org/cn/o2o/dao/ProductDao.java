package org.cn.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.cn.o2o.model.Product;

public interface ProductDao {
	Product queryProductByProductId(long productId);
	
	/**
	 * 插入商品
	 * @param product
	 * @return
	 */
	int insertProduct(Product product);
	
	Product queryProductById(long productId);
	
	int updateProduct(Product product);
	
	List<Product> queryProductList(@Param("productCondition") Product productCondition, @Param("rowIndex") int rowIndex,
                                   @Param("pageSize") int pageSize);
	
	int queryProductCount(@Param("productCondition") Product productCondition);
	
	int updateProductCategoryToNull(long productCategoryId);
}
