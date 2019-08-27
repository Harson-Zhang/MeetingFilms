package com.harson.uidprovider.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baidu.fsg.uid.UidGenerator;
import com.stylefeng.guns.api.uid.UidGenAPI;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Service(interfaceClass = UidGenAPI.class)
public class DefaultUidGenServiceImpl implements UidGenAPI{

    @Resource(name = "cachedUidGenerator")
    private UidGenerator uidGenerator;

    public long getUid() {
        return uidGenerator.getUID();
    }
}
