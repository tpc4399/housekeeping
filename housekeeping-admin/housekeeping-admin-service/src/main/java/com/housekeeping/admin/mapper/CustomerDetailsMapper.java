package com.housekeeping.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.housekeeping.admin.entity.CustomerDetails;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author su
 * @create 2020/11/23 10:54
 */
public interface CustomerDetailsMapper extends BaseMapper<CustomerDetails> {

    void updateHeadUrlById(@Param("headUrl") String headUrl,
                           @Param("userId") Integer userId);

    List<Integer> getIdbyByCompanyId(Integer companyId);

    List<Integer> getUserIdByGId(Integer gid);

    void blacklist(@Param("customerId") Integer customerId,
                   @Param("action") Boolean action);

    void collection(@Param("cusId") Integer cusId,@Param("empId") Integer empId);

    List<Integer> getAllEmpId(Integer id);

    void cancelCollection(@Param("cusId") Integer cusId,@Param("empId") Integer empId);

    Integer checkCollection(@Param("customerId") Integer customerId,@Param("empId") Integer empId);

    void collectionCompany(@Param("id") Integer id,@Param("companyId") Integer companyId);

    List<Integer> getAllCompId(Integer id);

    void cancelCollectionCompany(@Param("id") Integer id,@Param("compId") Integer compId);

    Integer checkCollectionCompany(@Param("id") Integer id,@Param("companyId") Integer companyId);
}
