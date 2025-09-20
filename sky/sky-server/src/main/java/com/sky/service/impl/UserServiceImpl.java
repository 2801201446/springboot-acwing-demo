package com.sky.service.impl;


import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.sky.properties.WeChatProperties;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    //微信接口地址
    public static final String LOGIN="https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;
    //微信登录
    public User login(UserLoginDTO userLoginDTO) {
        String string = getOpenid(userLoginDTO.getCode());

        //判断openid是否为空
       if(string==null)
       {
           throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
       }

        //判断当前用户是否为新用户
        User user=userMapper.getByOpenId(string);

        //新用户自动完成注册
        if(user==null)
        {
            user=User.builder()
                    .openid(string)
                    .createTime(LocalDateTime.now())
                    .build();

            userMapper.insert(user);
        }
        //返回用户对象
        return user;
    }

    ////调用微信服务接口，获取当前openid
    private String getOpenid(String code)
    {
        //调用微信服务接口，获取当前openid
        Map<String,String> map=new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","token_code");

        //HttpClient发送请求
        String json = HttpClientUtil.doGet(LOGIN, map);

        JSONObject jsonObject = JSON.parseObject(json);
        String string = jsonObject.getString("openid");
        return string;
    }
}
