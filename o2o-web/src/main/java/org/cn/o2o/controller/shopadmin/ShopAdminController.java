package org.cn.o2o.controller.shopadmin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value="shop",method= {RequestMethod.GET})
public class ShopAdminController {
	@RequestMapping(value="/shopoperation")
	public String shopOperation() {
		return "shop/shopoperation";
	}
	@RequestMapping(value="/shoplist")
	public String shopList() {
		return "shop/shoplist";
	}
	@RequestMapping(value="/shopmanage")
	public String shopManagement() {
		return "shop/shopmanage";
	}
	@RequestMapping(value="/productcategorymanage",method=RequestMethod.GET)
	public String productCategoryManagement() {
		return "shop/productcategorymanage";
	}
	@RequestMapping(value="/productoperation")
	public String productOperation() {
		return "shop/productoperation";
	}
	@RequestMapping(value="/productmanage")
	public String productmanagement() {
		return "shop/productmanage";
	}
	@RequestMapping(value = "/ownerlogin")
	public String ownerLogin(HttpServletRequest request) {
		return "shop/ownerlogin";
	}
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	private String register() {
		return "shop/register";
	}
	@RequestMapping(value = "/changepsw", method = RequestMethod.GET)
	private String changePsw() {
		return "shop/changepsw";
	}
	@RequestMapping(value = "/shopedit")
	private String shopedit(){return "shop/shopedit";}
}
