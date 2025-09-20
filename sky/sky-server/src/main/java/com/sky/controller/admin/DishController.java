package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/admin/dish")
@Api(tags = "菜品管理")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result savedish(@RequestBody DishDTO dishDTO)
    {
        log.info("新增菜品：{}",dishDTO);
        dishService.savedish(dishDTO);

        //管理端新增菜品，需要确保与用户端缓存redis数据一致性
        String key="dish_"+dishDTO.getCategoryId();
        //删除用户端当前dish_id的缓存
        redisTemplate.delete(key);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult> queryDish(DishPageQueryDTO dishPageQueryDTO)
    {
        log.info("分页查询菜品：{}",dishPageQueryDTO);
        PageResult pageResult=dishService.queryDish(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用或停用菜品
     *
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> startOrStop(@PathVariable Integer status, Long id) {
        // 1. 启用 0. 停用
        log.info("启用或停用菜品：{}", id);
        dishService.startOrStop(status, id);
        clearRedis("dish_*");
        return Result.success();
    }

    @DeleteMapping()
    @ApiOperation("菜品批量删除")
    public Result deletedish(@RequestParam List<Long> ids )
    {
        dishService.detetdish(ids);

        //保证与用户端数据一致性，直接删除所有dish_开头菜品缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查菜品")
    public Result<DishVO> getById(@PathVariable Long id) {
        DishVO dishVO=dishService.getByIdandFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping()
    @ApiOperation("修改菜品")
    public Result updatedish(@RequestBody DishDTO dishDTO)
    {
        log.info("修改菜品：{}",dishDTO);
        dishService.updatedish(dishDTO);

        //保证与用户端数据一致性，直接删除所有dish_开头菜品缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        return Result.success();
    }

    //清理缓存数据私有方法
    private void clearRedis(String keys) {
        Set<String> cacheKeys = redisTemplate.keys(keys);
        redisTemplate.delete(cacheKeys);
    }

}
