package org.cn.o2o.controller.shopadmin;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.cn.o2o.dto.ProductCategoryExecution;
import org.cn.o2o.dto.Result;
import org.cn.o2o.enums.ProductCategoryStateEnum;
import org.cn.o2o.model.ProductCategory;
import org.cn.o2o.model.Shop;
import org.cn.o2o.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/shop")
public class ProductCategoryManagementController {
	@Autowired 
	private ProductCategoryService productCategoryService;
	
	@RequestMapping(value="/listproductcategorys",method=RequestMethod.GET)
	private Result<List<ProductCategory>> getPoductCategoryList(HttpServletRequest request){
		Shop currentShop = (Shop)request.getSession().getAttribute("currentShop");
		List<ProductCategory> productCategoryList = null;
		if(currentShop!=null&&currentShop.getShopId()>0) {
			productCategoryList = productCategoryService.getProductCategoryList(currentShop.getShopId());
			return new Result<List<ProductCategory>>(true,productCategoryList);
		}else {
			ProductCategoryStateEnum ps =ProductCategoryStateEnum.INNER_ERROR;
			return new Result<List<ProductCategory>>(false,ps.getState(),ps.getStateInfo());
		}
	}
	
	@RequestMapping(value="/addproductcategorys",method=RequestMethod.POST)
	private Map<String,Object> addProductCategorys(@RequestBody List<ProductCategory> productCategoryList,
			HttpServletRequest request){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		Shop currentShop = (Shop)request.getSession().getAttribute("currentShop");
		for(ProductCategory pc:productCategoryList) {
			pc.setShopId(currentShop.getShopId());
			pc.setCreateTime(new Date());
		}
		if(productCategoryList!=null&&productCategoryList.size()>0) {
			try {
				ProductCategoryExecution pe = productCategoryService.batchAddProductCategory(productCategoryList);
				if(pe.getState()==ProductCategoryStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				}else {
					modelMap.put("success", false);
					modelMap.put("errorMsg", pe.getStateInfo());
				}
			}catch(RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}
		}else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "至少输入一个商品类别！");
		}
		return modelMap;
	}
	
	@RequestMapping(value="/removeproductcategory",method=RequestMethod.POST)
	private Map<String,Object> remove(Long productCategoryId,HttpServletRequest request){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		if(productCategoryId!=null&&productCategoryId>0) {
			try {
				Shop currentshop=(Shop)request.getSession().getAttribute("currentShop");
				ProductCategoryExecution pe = productCategoryService.deleteProductCategory(productCategoryId, currentshop.getShopId());
				if(pe.getState()==ProductCategoryStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				}else {
					modelMap.put("success", false);
					modelMap.put("errorMsg", pe.getStateInfo());
				}
			}catch(RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}
		}else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "至少选择一个商品类别！");
		}
		return modelMap;
	}
}
