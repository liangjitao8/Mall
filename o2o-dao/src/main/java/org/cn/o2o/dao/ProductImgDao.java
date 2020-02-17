package org.cn.o2o.dao;

import org.cn.o2o.model.ProductImg;

import java.util.List;



public interface ProductImgDao {
	/**
	 * 批量增加商品详情图片
	 * @param productImgList
	 * @return
	 */
	int batchInsertProductImg(List<ProductImg> productImgList);
	
	int deleteProductImgByProductId(long productId);

	List<ProductImg> queryProductImgList(Long productId);
}
