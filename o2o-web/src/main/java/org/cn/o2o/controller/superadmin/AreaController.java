package org.cn.o2o.controller.superadmin;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cn.o2o.dto.AreaExecution;
import org.cn.o2o.enums.AreaStateEnum;
import org.cn.o2o.model.Area;
import org.cn.o2o.service.AreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/superadmin")
public class AreaController {
	@Autowired
	private AreaService areaService;
	@RequestMapping(value="/listareas",method=RequestMethod.GET)
	private Map<String,Object> listArea(){
		Map<String,Object> modelMap = new HashMap<String,Object>();
		List<Area> list = new ArrayList<Area>();
		try {
			list=areaService.getAreaList();
			modelMap.put("rows", list);
			modelMap.put("total", list.size());
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return modelMap;
	}
	@RequestMapping(value = "/addarea", method = RequestMethod.POST)
	private Map<String, Object> addArea(String areaStr,
										HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		Area area = null;
		try {
			area = mapper.readValue(areaStr, Area.class);
			// decode可能有中文的地方
			area.setAreaName((area.getAreaName() == null) ? null : URLDecoder
					.decode(area.getAreaName(), "UTF-8"));
			area.setAreaDesc((area.getAreaDesc() == null) ? null : (URLDecoder
					.decode(area.getAreaDesc(), "UTF-8")));
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		if (area != null && area.getAreaName() != null) {
			try {
				AreaExecution ae = areaService.addArea(area);
				if (ae.getState() == AreaStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", ae.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入区域信息");
		}
		return modelMap;
	}

	@RequestMapping(value = "/modifyarea", method = RequestMethod.POST)
	private Map<String, Object> modifyArea(String areaStr,
										   HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		Area area = null;
		try {
			area = mapper.readValue(areaStr, Area.class);
			area.setAreaName((area.getAreaName() == null) ? null : URLDecoder
					.decode(area.getAreaName(), "UTF-8"));
			area.setAreaDesc((area.getAreaDesc() == null) ? null : URLDecoder
					.decode(area.getAreaDesc(), "UTF-8"));
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		if (area != null && area.getAreaId() != null) {
			try {
				AreaExecution ae = areaService.modifyArea(area);
				if (ae.getState() == AreaStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", ae.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入区域信息");
		}
		return modelMap;
	}

	@RequestMapping(value = "/removearea", method = RequestMethod.POST)
	private Map<String, Object> removeArea(Long areaId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (areaId != null && areaId > 0) {
			try {
				AreaExecution ae = areaService.removeArea(areaId);
				if (ae.getState() == AreaStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", ae.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入区域信息");
		}
		return modelMap;
	}

	@RequestMapping(value = "/removeareas", method = RequestMethod.POST)
	private Map<String, Object> removeAreas(String areaIdListStr) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		JavaType javaType = mapper.getTypeFactory().constructParametricType(
				ArrayList.class, Long.class);
		List<Long> areaIdList = null;
		try {
			areaIdList = mapper.readValue(areaIdListStr, javaType);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
		}
		if (areaIdList != null && areaIdList.size() > 0) {
			try {
				AreaExecution ae = areaService.removeAreaList(areaIdList);
				if (ae.getState() == AreaStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", ae.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入区域信息");
		}
		return modelMap;
	}
}
