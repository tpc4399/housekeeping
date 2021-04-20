package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.AddEmployeesContractDTO;
import com.housekeeping.admin.dto.GetCalendarByDateSlotDTO;
import com.housekeeping.admin.dto.TimeSlotDTO;
import com.housekeeping.admin.service.IEmployeesContractService;
import com.housekeeping.admin.vo.TimeSlot;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.CommonUtils;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * @Author su
 * @Date 2021/2/1 10:06
 */
@Api(tags={"【包工服务】相关接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/employeesContract")
public class EmployeesContractController {

    private final IEmployeesContractService employeesContractService;

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_EMPLOYEES, RolesEnum.USER_COMPANY})
    @ApiOperation("【管理员】給員工添加一个包工服务")
    @PostMapping
    public R add(@RequestBody AddEmployeesContractDTO dto){
        return employeesContractService.add(dto);
    }

    @ApiOperation("获取员工的所有包工服务")
    @GetMapping("/getAllById")
    public R getByEmployeesId(@RequestParam Integer employeesId){
        return employeesContractService.getByEmployeesId(employeesId);
    }

    @ApiOperation("根据id获取包工服务")
    @GetMapping("/getById")
    public R getById(@RequestParam Integer id){
        return R.ok(employeesContractService.getById(id));
    }

    @Access({RolesEnum.SYSTEM_ADMIN})
    @ApiOperation("【管理员】获取所有包工服务")
    @GetMapping
    public R getAll(){
        return R.ok(employeesContractService.list());
    }

    @ApiOperation("根据时间段和包工_id 獲取包工時間表，就是这个包工的服务时间的一个详细列举")
    @PostMapping("/getCalendarByDateSlot")
    public R getCalendarByDateSlot(@RequestBody GetCalendarByDateSlotDTO dto){
        Map<LocalDate, List<TimeSlot>> res = employeesContractService.getCalendarByContractId(dto.getDateSlot(), dto.getId());
        if (CommonUtils.isEmpty(res)){
            return R.failed("該包工沒有設置時間表");
        }else {
            return R.ok(res, "獲取成功");
        }
    }

    @ApiOperation("根据时间段和包工_id 獲取員工包工時間表的闲置时间表，就是这段时间内这个员工可以做这个包工的时间。以详细列举形式返回")
    @PostMapping("/getFreeTimeByDateSlot")
    public R getFreeTimeByDateSlot(@RequestBody GetCalendarByDateSlotDTO dto){
        Map<LocalDate, List<TimeSlot>> res = employeesContractService.getFreeTimeByContractId(dto.getDateSlot(), dto.getId());
        if (CommonUtils.isEmpty(res)){
            return R.failed("該包工沒有闲置时间或包工没设置时间表");
        }else {
            return R.ok(res, "獲取成功");
        }
    }

    @Access(RolesEnum.USER_CUSTOMER)
    @ApiOperation("【客户】预约包工服务")
    @PostMapping("/makeAnAppointment")
    public R makeAnAppointment(){
        return R.ok();
    }

    @Access({RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【保洁员】添加一个包工服务，盡量用postman測試這個接口，swagger會出問題(圖片數據為空，程序不會報錯)")
    @PostMapping(value = "/add2_1", headers = "content-type=multipart/form-data")
    public R add2(@RequestParam("name") String name,
                  @RequestParam("image") MultipartFile[] image,
                  @RequestParam("days") Integer dateLength,
                  @RequestParam("times") Float timeLength,
                  @RequestParam("totalPrice") BigDecimal totalPrice,
                  @RequestParam("jobs") Integer[] jobs,
                  @RequestParam("description") String description,
                  @RequestParam("actives") Integer[] actives){

        employeesContractService.add2(null, name, image, dateLength, timeLength, totalPrice, jobs, description, actives);

        return R.ok();
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER})
    @ApiOperation("【管理员】【公司】【经理】給員工添加一个包工服务，盡量用postman測試這個接口，swagger會出問題(圖片數據為空，程序不會報錯)")
    @PostMapping(value = "/add2_2", headers = "content-type=multipart/form-data")
    public R add2(@RequestParam("employeesId") Integer employeesId,
                  @RequestParam("name") String name,
                  @RequestParam("image") MultipartFile[] image,
                  @RequestParam("days") Integer dateLength,
                  @RequestParam("times") Float timeLength,
                  @RequestParam("totalPrice") BigDecimal totalPrice,
                  @RequestParam("jobs") Integer[] jobs,
                  @RequestParam("description") String description,
                  @RequestParam("actives") Integer[] actives){

        employeesContractService.add2(employeesId, name, image, dateLength, timeLength, totalPrice, jobs, description, actives);

        return R.ok();
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【管理员】【公司】【经理】【保洁员】修改包工服务，1、盡量用postman測試這個接口，swagger會出問題(圖片數據為空，程序不會報錯) 2、image只在数据有改变的时候传入完整数据，数据不改变就传空数组即可")
    @PutMapping(value = "/update", headers = "content-type=multipart/form-data")
    public R update(@RequestParam("id") Integer id,
                    @RequestParam("name") String name,
                    @RequestParam("image") MultipartFile[] image,
                    @RequestParam("days") Integer dateLength,
                    @RequestParam("times") Float timeLength,
                    @RequestParam("totalPrice") BigDecimal totalPrice,
                    @RequestParam("jobs") Integer[] jobs,
                    @RequestParam("description") String description,
                    @RequestParam("actives") Integer[] actives){

        employeesContractService.update(id, name, image, dateLength, timeLength, totalPrice, jobs, description, actives);

        return R.ok();
    }

    @Access({RolesEnum.SYSTEM_ADMIN, RolesEnum.USER_COMPANY, RolesEnum.USER_MANAGER, RolesEnum.USER_EMPLOYEES})
    @ApiOperation("【管理员】【公司】【经理】【保洁员】删除包工服务")
    @DeleteMapping("/{id}")
    public R del(@PathVariable Integer id){
        employeesContractService.removeById(id);
        return R.ok(null, "删除成功");
    }

}
