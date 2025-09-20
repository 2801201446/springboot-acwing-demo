package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    //新增菜品应该是事务（操作哦表dish和dish_flavor），需要保证原子性
    @Transactional
    public void savedish(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);

        //插入菜品记录
        dishMapper.insert(dish);
        //插入dish表后自动生成dishid
        //获取当前dish的主键
        Long dishId=dish.getId();
        //获取本餐品的flavor
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors !=null && flavors.size()>0)
        {
            //将所有flavor赋予当前dish的id
            for(DishFlavor flavor:flavors)
            {
                flavor.setDishId(dishId);
            }
            dishFlavorMapper.insert(flavors);
        }

    }


    public PageResult queryDish(DishPageQueryDTO dishPageQueryDTO)
    {
        PageHelper .startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());

        //Page中泛型类型是返回显示条目数据
        Page<DishVO> page=dishMapper.queryDish(dishPageQueryDTO);
        Long total=page.getTotal();
        List record=page.getResult();
        return new PageResult(total,record);
    }

    //菜品批量删除
    public void detetdish(List<Long> ids) {
        //判断菜品是否可以删除

        for (Long id : ids) {
            Dish dish=dishMapper.getById(id);
            //是否起售
            if(dish.getStatus()== StatusConstant.DISABLE)
            {
                //在售不可删除
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //是否被关联
        List<Long> setmealsids=setmealDishMapper.getsetmealdishbydishid(ids);
        if(setmealsids!=null && setmealsids.size()>0)
        {
            //dish被套餐关联不可删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除dish数据
        for (Long id : ids) {
            dishMapper.deleteById(id);
            //删除关联的flavor数据
            dishFlavorMapper.deleteflavorById(id);
        }


    }


    public DishVO getByIdandFlavor(Long id) {

        Dish dish=dishMapper.getById(id);
        List<DishFlavor> dishFlavors=dishFlavorMapper.getByDishId(id);

        DishVO dishVO=new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }


    public void updatedish(DishDTO dishDTO) {
        Dish dish =new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //修改菜品数据
        dishMapper.updatedish(dish);

        //删除菜品口味数据
        dishFlavorMapper.deleteflavorById(dish.getId());
        //插入菜品口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();

        if(flavors !=null && flavors.size()>0)
        {
            //将所有flavor赋予当前dish的id
            for(DishFlavor flavor:flavors)
            {
                flavor.setDishId(dish.getId());
            }
            dishFlavorMapper.insert(flavors);
        }
    }

    /**
     * 条件查询菜品和口味
     * @param dish
     * @return
     */
    public List<DishVO> listWithFlavor(Dish dish) {
        List<Dish> dishList = dishMapper.list(dish);

        List<DishVO> dishVOList = new ArrayList<>();

        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        return dishVOList;
    }


    public void startOrStop(Integer status, Long id) {
        dishMapper.startOrStop(status,id);
    }
}
