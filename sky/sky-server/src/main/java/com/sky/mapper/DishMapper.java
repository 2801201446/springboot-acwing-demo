package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
     * 插入菜品数据
     * @return
     */
    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);

    /**
     * 分页查询菜品
     * @return
     */
    Page<DishVO> queryDish(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询菜品
     * @return
     */
    @Select("select * from dish where id=#{id}")
    Dish getById(Long id);
    /**
     * 根据id删除菜品
     * @return
     */
    @Delete("delete from dish where id=#{id}")
    void deleteById(Long id);
    /**
     * 根据id修改菜品
     * @return
     */
    @AutoFill(OperationType.UPDATE)
    void updatedish(Dish dish);

    /**
     * 动态条件查询菜品
     *
     * @return
     */
    List<Dish> list(Dish dish);

    @Update("update dish set status=#{status} where id=#{id}")
    void startOrStop(Integer status, Long id);
}
