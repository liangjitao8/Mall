package org.cn.o2o.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.cn.o2o.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CacheService{
    @Autowired
    private RedisUtil redisUtil;

	public void removeFromCache(String keyPrefix) {
        Map<Object,Object> keyMap = new HashMap<Object,Object>();
        keyMap=redisUtil.hmget(keyPrefix+"*");
        Iterator iter = keyMap.entrySet().iterator();
        while (iter.hasNext()){
            Map.Entry entry = (Map.Entry) iter.next();
            redisUtil.del(entry.getKey().toString());
        }
	}
	
}
