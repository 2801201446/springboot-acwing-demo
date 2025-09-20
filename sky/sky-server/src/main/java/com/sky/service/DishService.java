package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {


    //新增菜品
    void savedish(DishDTO dishDTO);

    PageResult queryDish(DishPageQueryDTO dishPageQueryDTO);

    void detetdish(List<Long> ids);

    DishVO getByIdandFlavor(Long id);

    void updatedish(DishDTO dishDTO);

    List<DishVO> listWithFlavor(Dish dish);

    void startOrStop(Integer status, Long id);
}
