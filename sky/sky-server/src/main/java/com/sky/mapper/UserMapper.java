package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    //根据openid查询user
    @Select("select * from user where openid=#{string}")
    User getByOpenId(String string);


    //插入user
    void insert(User user);

    @Select("select from user where id=#{userId}")
    User getById(Long userId);
}
