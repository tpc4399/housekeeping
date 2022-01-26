package com.housekeeping.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.housekeeping.admin.dto.CompanyDetailsPageDTO;
import com.housekeeping.admin.dto.PageOfEmployeesDTO;
import com.housekeeping.admin.service.ICustomerDetailsService;
import com.housekeeping.common.annotation.Access;
import com.housekeeping.common.annotation.RolesEnum;
import com.housekeeping.common.utils.R;
import com.housekeeping.common.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author su
 * @create 2020/11/23 11:28
 */
@Api(value="客戶controller",tags={"【客户详情】接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/customerDetails")
public class CustomerDetailsController {

    private final ICustomerDetailsService customerDetailsService;

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("【客户】设置为默认地址")
    @PutMapping("/toDefault")
    public R toDefault(Integer id){
        return customerDetailsService.toDefault(id);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("【客户】上传头像")
    @PostMapping("/uploadHead")
    public R uploadHead(@RequestParam("file") MultipartFile file) throws IOException {
        Integer reviserId = TokenUtils.getCurrentUserId();
        //服务器存储head
        String fileName = customerDetailsService.uploadHead(file, reviserId);
        //数据库存储headUrl
        customerDetailsService.updateHeadUrlByUserId(fileName, reviserId);
        return R.ok("頭像保存成功");
    }

    @Access({RolesEnum.USER_COMPANY})
    @ApiOperation("【公司】获取客户列表")
    @GetMapping("/getCustomerList")
    public R getCustomerList(Page page, Integer cid, String name){
        return customerDetailsService.getCustomerList(page,cid,name);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("客户】收藏员工")
    @GetMapping("/collection")
    public R collection(Integer empId){
        return customerDetailsService.collection(empId);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("客户】查看收藏員工列表")
    @GetMapping("/getCollectionList")
    public R getCollectionList(PageOfEmployeesDTO pageOfEmployeesDTO){
        return customerDetailsService.getCollectionList(pageOfEmployeesDTO);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("客户】取消收藏員工")
    @GetMapping("/cancelCollection")
    public R cancelCollection(String ids){
        return customerDetailsService.cancelCollection(ids);
    }

    @ApiOperation("客户】判斷是否有收藏")
    @GetMapping("/checkCollection")
    public R checkCollection(@RequestParam Integer empId){
        return customerDetailsService.checkCollection(empId);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("客户】收藏公司")
    @GetMapping("/collectionCompany")
    public R collectionCompany(Integer companyId){
        return customerDetailsService.collectionCompany(companyId);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("客户】查看收藏公司列表")
    @GetMapping("/getCollectionCompanyList")
    public R getCollectionCompanyList(CompanyDetailsPageDTO companyDetailsPageDTO){
        return customerDetailsService.getCollectionCompanyList(companyDetailsPageDTO);
    }

    @Access({RolesEnum.USER_CUSTOMER})
    @ApiOperation("客户】取消收藏公司")
    @GetMapping("/cancelCollectionCompany")
    public R cancelCollectionCompany(String ids){
        return customerDetailsService.cancelCollectionCompany(ids);
    }

    @ApiOperation("客户】判斷是否有收藏")
    @GetMapping("/checkCollectionCompany")
    public R checkCollectionCompany(@RequestParam Integer companyId){
        return customerDetailsService.checkCollectionCompany(companyId);
    }



}
