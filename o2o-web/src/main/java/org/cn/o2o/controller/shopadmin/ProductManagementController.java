package org.cn.o2o.controller.shopadmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.cn.o2o.dto.ImageHolder;
import org.cn.o2o.dto.ProductExecution;
import org.cn.o2o.enums.ProductStateEnum;
import org.cn.o2o.model.Product;
import org.cn.o2o.model.ProductCategory;
import org.cn.o2o.model.Shop;
import org.cn.o2o.service.ProductCategoryService;
import org.cn.o2o.service.ProductService;
import org.cn.o2o.utils.CodeUtil;
import org.cn.o2o.utils.HttpServletRequestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/shop")
public class ProductManagementController {
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductCategoryService productCategoryService;
	private static final int IMAGEMAXCOUNT =6;//最大上传详情图
	
	@RequestMapping(value="/getproductlistbyshop",method=RequestMethod.GET)
	private Map<String,Object> getProductListByShop(HttpServletRequest request){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		//从前台传过来的页码
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		//要求返回的商品数上限
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		//session中获取shopId
		Shop currentShop = (Shop)request.getSession().getAttribute("currentShop");
		System.out.println(currentShop.getShopId());
		if((pageIndex>-1)&&(pageSize>-1)&&(currentShop!=null)&&(currentShop.getShopId()!=null)) {
			//获取传入的需要检索的条件
			long productCategoryId = HttpServletRequestUtil.getInt(request, "productCategoryId");
			String productName = HttpServletRequestUtil.getString(request, "productName");
			Product productCondition = compactProductCondition(currentShop.getShopId(),productCategoryId,productName);
			//传入查询条件以及分页信息
			ProductExecution pe = productService.getProductList(productCondition, pageIndex, pageSize);
			modelMap.put("productList", pe.getProductList());
			modelMap.put("count", pe.getCount());
			modelMap.put("success", true);
		}else {
			modelMap.put("errMsg", "empty pageSize or pageIndex orshopId");
			modelMap.put("success", false);
		}
		return modelMap;
	}
	
	private Product compactProductCondition(Long shopId, long productCategoryId, String productName) {
		Product productCondition = new Product();
		Shop shop = new Shop();
		shop.setShopId(shopId);
		productCondition.setShop(shop);
		//若有指定类别要求需要添加进去
		if(productCategoryId!=-1L) {
			ProductCategory productCategory=new ProductCategory();
			productCategory.setProductCategoryId(productCategoryId);
			productCondition.setProductCategory(productCategory);
			
		}
		//若有商品名模糊查询的要求则添加进去
		if(productName!=null) {
			productCondition.setProductName(productName);
		}
		return productCondition;
	}

	@RequestMapping(value="/getproductbyid",method=RequestMethod.GET)
	private Map<String,Object> getProductById(@RequestParam Long productId){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		if(productId>-1) {
			Product product=productService.getProductById(productId);
			List<ProductCategory> productCategoryList = 
					productCategoryService.getProductCategoryList(product.getShop().getShopId());
			modelMap.put("product", product);
			modelMap.put("productCategoryList", productCategoryList);
			modelMap.put("success", true);
		}
		else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty productId");
		}
		return modelMap;
	}
	
	@RequestMapping(value="/modifyproduct",method = RequestMethod.POST)
	private Map<String,Object> modifyproduct(HttpServletRequest request){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		//调用上下架操作
		boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");
		if(!statusChange&&!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		ObjectMapper mapper = new ObjectMapper();
		Product product = null;
		ImageHolder thumbnail =null;
		List<ImageHolder> productImgList = new ArrayList<ImageHolder>();
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		try {
			if(multipartResolver.isMultipart(request)) {
				thumbnail = handleImage(request, productImgList);
			}
		}catch(Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg",e.toString());
			return modelMap;
		}
		try {
			String productStr = HttpServletRequestUtil.getString(request, "productStr");
			//尝试获取前端传过来的表单String流并将其转换成Product实体类
			product = mapper.readValue(productStr, Product.class);
		}catch(Exception e) {
			modelMap.put("success",false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		//若Product信息，缩略图以及详情图列表为非空，则开始进行商品添加操作
		if(product!=null) {
			try {
				//从session中获取当前店铺的Id并赋值给product,减少对前端数据的依赖
				Shop currentShop = (Shop)request.getSession().getAttribute("currentShop");
				Shop shop = new Shop();
				shop.setShopId(currentShop.getShopId());
				product.setShop(shop);
				//执行添加操作
				ProductExecution pe = productService.modifyProduct(product, thumbnail, productImgList);
				if(pe.getState() == ProductStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				}else {
					modelMap.put("success", false);
					modelMap.put("errMsg", pe.getStateInfo());
				}
			}catch(RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}
		}
		return modelMap;
	}

	private ImageHolder handleImage(HttpServletRequest request, List<ImageHolder> productImgList) throws IOException {
		ImageHolder thumbnail;
		MultipartHttpServletRequest multipartRequest=(MultipartHttpServletRequest) request;
		//取出缩略图构建ImageHolder
		CommonsMultipartFile thumbnailFile = 
				(CommonsMultipartFile)multipartRequest.getFile("thumbnail");
		thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(),thumbnailFile.getInputStream());
		//取出详情图列表并构建List<ImageHolder>列表对象，最多支持六张图片上传
		for(int i =0;i<IMAGEMAXCOUNT;i++) {
			CommonsMultipartFile productImgFile = 
					(CommonsMultipartFile)multipartRequest.getFile("productImg"+i);
			if(productImgFile!=null) {
				//若取出的第I个图片的文件流不为空，则将其加入详情图列表
				ImageHolder productImg = new ImageHolder(productImgFile.getOriginalFilename(),
						productImgFile.getInputStream());
				productImgList.add(productImg);
			}else {
				break;
			}
		}
		return thumbnail;
	}
	
	@RequestMapping(value="/addproduct",method = RequestMethod.POST)
	private Map<String,Object> addproduct(HttpServletRequest request){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		//验证码校验
		if(!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "验证码错误");
			return modelMap;
		}
		//接受前端参数的变量的初始化，包括商品，缩略图，列表实体类
		ObjectMapper mapper = new ObjectMapper();
		Product product =null;
		String productStr = HttpServletRequestUtil.getString(request, "productStr");
		ImageHolder thumbnail = null;
		List<ImageHolder> productImgList = new ArrayList<ImageHolder>();
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		try {
			//若请求中存在文件流，则取出相关的文件（包括缩略图和详情图)
			if(multipartResolver.isMultipart(request)) {
				thumbnail = handleImage(request, productImgList);
			}else {
				modelMap.put("success", false);
				modelMap.put("errMsg","上传图片不能为空");
				return modelMap;
			}
		}catch(Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		try {
			//尝试获取前端传过来的表单String流并将其转换成Product实体类
			product = mapper.readValue(productStr, Product.class);
		}catch(Exception e) {
			modelMap.put("success",false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		//若Product信息，缩略图以及详情图列表为非空，则开始进行商品添加操作
		if(product!=null&&thumbnail!=null&&productImgList.size()>0) {
			try {
				//从session中获取当前店铺的Id并赋值给product,减少对前端数据的依赖
				Shop currentShop = (Shop)request.getSession().getAttribute("currentShop");
				Shop shop = new Shop();
				shop.setShopId(currentShop.getShopId());
				product.setShop(shop);
				//执行添加操作
				ProductExecution pe = productService.addProduct(product, thumbnail, productImgList);
				if(pe.getState() == ProductStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				}else {
					modelMap.put("success", false);
					modelMap.put("errMsg", pe.getStateInfo());
				}
			}catch(RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}
		}else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入商品信息");
			return modelMap;
		}
		return modelMap;
	}
}
