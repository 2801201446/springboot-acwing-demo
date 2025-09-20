package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.*;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api("员工相关接口")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("员工登录")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    @PostMapping
    @ApiOperation("新增员工")
    public Result<String> addemployee(@RequestBody EmployeeDTO employeeDTO)
    {
        log.info("新增员工: {}" ,employeeDTO);
        employeeService.addemployee(employeeDTO);
        return Result.success();
    }


    @GetMapping("/page")
    @ApiOperation("分页查询员工")
    public Result<PageResult> queryemployee(EmployeePageQueryDTO employeePageQueryDTO)
    {
        log.info("员工分页查询: {}" ,employeePageQueryDTO);
        PageResult pageResult=employeeService.queryemployee(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用用户状态")
    public Result startorstop(@PathVariable Integer status, Long id) {
        log.info("启用或禁用员工状态：{}{}",status,id);
        employeeService.startorstop(status,id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("修改员工信息(先查询)")
    public Result<Employee> updateemployee(@PathVariable Long id)
    {
        log.info("查询要修改员工的信息");
        Employee employee=employeeService.updateemployee(id);
        return Result.success(employee);
    }
    @PutMapping()
    @ApiOperation("修改员工信息(后修改)")
    public Result updateemployee2(@RequestBody EmployeeDTO employeeDTO) {
        log.info("修改员工的信息");
        employeeService.updateemployee2(employeeDTO);
        return Result.success();
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("员工登出")
    public Result<String> logout() {
        return Result.success();
    }

}
