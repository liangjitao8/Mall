package org.cn.o2o.controller.superadmin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/superadmin", method = { RequestMethod.GET,
        RequestMethod.POST })
public class SuperAdminController {
    /*@RequestMapping(value = "/top", method = RequestMethod.GET)
    private String top() {
        return "superadmin/top";
    }*/
    @RequestMapping(value = "/main", method = RequestMethod.GET)
    private String main() {
        return "superadmin/main";
    }
    @RequestMapping(value = "/areamanage", method = RequestMethod.GET)
    private String areaManagement() {
        return "superadmin/areamanage";
    }
    @RequestMapping(value = "/personinfomanage", method = RequestMethod.GET)
    private String personInfoManage() {
        return "superadmin/personinfomanage";
    }
    @RequestMapping(value = "/shopmanage", method = RequestMethod.GET)
    private String shopManage() {
        return "superadmin/shopmanage";
    }
    @RequestMapping(value = "/shopcategorymanage", method = RequestMethod.GET)
    private String shopCategoryManage() {
        return "superadmin/shopcategorymanage";
    }
    @RequestMapping(value = "/headlinemanage", method = RequestMethod.GET)
    private String headLineManagement() {
        return "superadmin/headlinemanage";
    }
}
