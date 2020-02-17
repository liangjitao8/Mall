package org.cn.o2o.service;

import java.util.List;

import org.cn.o2o.dao.ProductCategoryDao;
import org.cn.o2o.dao.ProductDao;
import org.cn.o2o.exception.ProductCategoryOperationException;
import org.cn.o2o.dto.ProductCategoryExecution;
import org.cn.o2o.enums.ProductCategoryStateEnum;
import org.cn.o2o.model.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductCategoryService{
	@Autowired
	private ProductCategoryDao productCategoryDao;
	@Autowired
	private ProductDao productDao;

	public List<ProductCategory> getProductCategoryList(long shopId) {
		return productCategoryDao.queryProductCategoryList(shopId);
	}

	@Transactional
	public ProductCategoryExecution batchAddProductCategory(List<ProductCategory> productCategoryList)
			throws ProductCategoryOperationException {
		if (productCategoryList != null && productCategoryList.size() > 0) {
			try {
				int effectedNum = productCategoryDao.batchInsertProductCategory(productCategoryList);
				if (effectedNum <= 0) {
					throw new ProductCategoryOperationException("店铺类别创建失败！");
				} else {
					return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
				}
			} catch (Exception e) {
				throw new ProductCategoryOperationException("batchAdd Error");
			}
		} else {
			return new ProductCategoryExecution(ProductCategoryStateEnum.EMPTY_LIST);
		}
	}

	@Transactional
	public ProductCategoryExecution deleteProductCategory(long productCategoryId, long shopId)
			throws ProductCategoryOperationException {
		try {
			int effectedNum=productDao.updateProductCategoryToNull(productCategoryId);
			if(effectedNum<0) {
				throw new ProductCategoryOperationException("商品类别更新失败");
			}
		}catch(Exception e) {
			throw new ProductCategoryOperationException("deleteProductCategory error"+e.getMessage());
		}
		try {
			int effectedNum = productCategoryDao.deleteProductCategory(productCategoryId, shopId);
			if (effectedNum <= 0) {
				throw new ProductCategoryOperationException("店铺类别删除失败！");
			} else {
				return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
			}
		} catch (Exception e) {
			throw new ProductCategoryOperationException("delete Error:" + e.getMessage());
		}
	}
}
