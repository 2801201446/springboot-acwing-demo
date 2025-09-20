package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    @Insert("insert into shopping_cart (name, user_id, dish_id, setmeal_id, dish_flavor, number, amount, image, create_time) " +
            " values (#{name},#{userId},#{dishId},#{setmealId},#{dishFlavor},#{number},#{amount},#{image},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    @Update("update shop_cart set number=#{number} where id=#{id}")
    void updateNumberById(ShoppingCart cart);

    List<ShoppingCart> list(ShoppingCart shoppingCart);

    @Delete("delete from shop_cart where user_id=#{userId}")
    void deleteByUserId(Long currentId);

    @Delete("delete from shop_cart where id=#{id}")
    void deleteById(Long id);

}
