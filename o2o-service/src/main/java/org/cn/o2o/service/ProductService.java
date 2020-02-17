package org.cn.o2o.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cn.o2o.dao.ProductDao;
import org.cn.o2o.dao.ProductImgDao;
import org.cn.o2o.exception.ProductOperationException;
import org.cn.o2o.dto.ImageHolder;
import org.cn.o2o.dto.ProductExecution;
import org.cn.o2o.enums.ProductStateEnum;
import org.cn.o2o.model.Product;
import org.cn.o2o.model.ProductImg;
import org.cn.o2o.utils.ImageUtil;
import org.cn.o2o.utils.PageCalculator;
import org.cn.o2o.utils.PathUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService{
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductImgDao productImgDao;

	@Transactional
	// 1处理略缩图，获取略缩图相对路径并赋值给product
	// 2往tb_product写入商品信息，获取productId
	// 3结合productId批量处理商品详情图
	// 4将商品详情图列表批量插入tb_product_img中
	public ProductExecution addProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImgHolderList)
			throws ProductOperationException {
		// 1空值判断2给商品设置默认属性3默认上架状态
		if (product != null && product.getShop() != null && product.getShop().getShopId() != null) {
			product.setCreateTime(new Date());
			product.setLastEditTime(new Date());
			product.setEnableStatus(1);
			if (thumbnail != null) {// 若商品略缩图不为空则添加
				addThumbnail(product, thumbnail);
			}
			try {
				int effectedNum = productDao.insertProduct(product);
				if (effectedNum <= 0)
					throw new ProductOperationException("创建商品失败！");
			} catch (Exception e) {
				throw new ProductOperationException("创建商品失败！" + e.toString());
			}
			// 若商品详情图不为空则添加
			if (productImgHolderList != null && productImgHolderList.size() > 0) {
				addProductImgList(product, productImgHolderList);
			}
			return new ProductExecution(ProductStateEnum.SUCCESS, product);
		} else {
			// 传参为空则返回空值错误信息
			return new ProductExecution(ProductStateEnum.EMPTY);
		}
	}

	// 添加详情图
	private void addProductImgList(Product product, List<ImageHolder> productImgHolderList)
			throws ProductOperationException {
		// 获取图片存储路径，在这里直接保存到相应店铺的文件底下
		String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
		List<ProductImg> productImgList = new ArrayList<ProductImg>();
		// 遍历图片一次去处理，并添加进productImg实体类里
		for (ImageHolder productImgHolder : productImgHolderList) {
			String imgAddr = ImageUtil.generateNormalImg(productImgHolder, dest);
			ProductImg productImg = new ProductImg();
			productImg.setImgAddr(imgAddr);
			productImg.setProductId(product.getProductId());
			productImg.setCreateTime(new Date());
			productImgList.add(productImg);
		}
		if (productImgList.size() > 0) {
			try {
				int effectedNum = productImgDao.batchInsertProductImg(productImgList);
				if (effectedNum <= 0) {
					throw new ProductOperationException("创建商品详情图失败");
				}
			} catch (Exception e) {
				throw new ProductOperationException("创建商品详情图失败:" + e.toString());
			}
		}

	}

	/**
	 * 添加缩略图
	 * 
	 * @param product
	 * @param thumbnail
	 */
	private void addThumbnail(Product product, ImageHolder thumbnail) {
		String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
		String thumbnailAddr = ImageUtil.generateThumbnail(thumbnail, dest);
		product.setImgAddr(thumbnailAddr);
	}

	public Product getProductById(long productId) {
		return productDao.queryProductById(productId);
	}

	@Transactional
	public ProductExecution modifyProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImgHolderList)
			throws ProductOperationException {
		// 1空值判断2给商品设置默认属性3默认上架状态
		if (product != null && product.getShop() != null && product.getShop().getShopId() != null) {
			product.setLastEditTime(new Date());
			if (thumbnail != null) {// 若商品略缩图不为空则查询原有的信息
				Product tempProduct = productDao.queryProductById(product.getProductId());
				if(tempProduct.getImgAddr()!=null) {
					ImageUtil.deleteFileOrPath(tempProduct.getImgAddr());
				}
				addThumbnail(product, thumbnail);
			}
			if(productImgHolderList!=null&&productImgHolderList.size()>0) {
				deleteProductImgList(product.getProductId());
				addProductImgList(product, productImgHolderList);
			}
			try {
				int effectedNum = productDao.updateProduct(product);
				if (effectedNum <= 0) {
					throw new ProductOperationException("更新商品失败！");
				}
				return new ProductExecution(ProductStateEnum.SUCCESS,product);
			} catch (Exception e) {
				throw new ProductOperationException("更新商品失败！" + e.toString());
			}
		} else {
			// 传参为空则返回空值错误信息
			return new ProductExecution(ProductStateEnum.EMPTY);
		}
	}

	private void deleteProductImgList(Long productId) {
		List<ProductImg> productImgList = productImgDao.queryProductImgList(productId);
		for(ProductImg productImg:productImgList)
			ImageUtil.deleteFileOrPath(productImg.getImgAddr());
		productImgDao.deleteProductImgByProductId(productId);
	}

	public ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize) {
		//页码转换成对应条数
		int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
		List<Product> productList = productDao.queryProductList(productCondition, rowIndex, pageSize);
		//返回同样查询条件的商品总数
		int count = productDao.queryProductCount(productCondition);
		ProductExecution pe =new ProductExecution();
		pe.setProductList(productList);
		pe.setCount(count);
		return pe;
	}
}
