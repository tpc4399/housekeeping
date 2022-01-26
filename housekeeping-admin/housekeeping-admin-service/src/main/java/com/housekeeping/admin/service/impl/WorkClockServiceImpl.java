package com.housekeeping.admin.service.impl;


import com.aliyun.oss.OSSClient;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.housekeeping.admin.dto.CustomerEvaluationDTO;
import com.housekeeping.admin.dto.WorkClockDTO;
import com.housekeeping.admin.entity.*;
import com.housekeeping.admin.mapper.OrderDetailsMapper;
import com.housekeeping.admin.mapper.WorkClockMapper;
import com.housekeeping.admin.service.*;
import com.housekeeping.admin.vo.UploadPhotoVO;
import com.housekeeping.admin.vo.WorkCheckVO;
import com.housekeeping.common.entity.Message;
import com.housekeeping.common.sms.SendMessage;
import com.housekeeping.common.utils.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * @Author su
 * @Date 2020/12/11 16:08
 */
@Service("workClockService")
public class WorkClockServiceImpl extends ServiceImpl<WorkClockMapper, WorkClock> implements WorkClockService {

    @Resource
    private EmployeesDetailsService employeesDetailsService;
    @Resource
    private OSSClient ossClient;

    @Value("${oss.bucketName}")
    private String bucketName;

    @Value("${oss.urlPrefix}")
    private String urlPrefix;

    @Resource
    private IWorkDetailsService workDetailsService;

    @Resource
    private IOrderDetailsService orderDetailsService;

    @Resource
    private OrderDetailsMapper orderDetailsMapper;

    @Resource
    private ISysConfigService sysConfigService;

    @Resource
    private DelayingQueueService delayingQueueService;
    @Resource
    private ICustomerDetailsService customerDetailsService;
    @Resource
    private InvitationService invitationService;
    @Resource
    private IUserService userService;
    @Resource
    private ManagerDetailsService managerDetailsService;
    @Resource
    private ICompanyDetailsService companyDetailsService;

    @Override
    public R workStart(Integer id,String phonePrefix,String phone) {

        //開始的工作是訂單的第一份工作
        WorkClock byId = this.getById(id);
        Long number = workDetailsService.getById(byId.getWorkId()).getNumber();
        QueryWrapper<WorkDetails> qw = new QueryWrapper<>();
        qw.eq("number",number);
        List<WorkDetails> list = workDetailsService.list(qw);
        if(CollectionUtils.isEmpty(list)){
            return R.failed("工作不存在");
        }
        WorkDetails workDetails = list.get(0);
        if(byId.getWorkId().equals(workDetails.getId())){
            QueryWrapper qw1 = new QueryWrapper();
            qw1.eq("number", number);
            OrderDetails od = orderDetailsService.getOne(qw1);
            if (CommonUtils.isEmpty(od)) return R.failed(null, "订单不存在");
            /* 开始修改数据 修改订单状态 */
            orderDetailsMapper.status(number.toString(), CommonConstants.ORDER_STATE_HAVE_IN_HAND);
        }

        byId.setToWorkStatus(1);
        byId.setToWorkTime(LocalDateTime.now());
        byId.setWorkStatus(1);
        this.updateById(byId);

        QueryWrapper<EmployeesDetails> qw2 = new QueryWrapper<>();
        qw2.eq("user_id", TokenUtils.getCurrentUserId());
        EmployeesDetails one = employeesDetailsService.getOne(qw2);
        //发送短信
        String[] params = new String[]{one.getName()};
        SendMessage.sendWorkStartMessage(phonePrefix, phone, params);
        return R.ok("成功發送短信");
    }

    @Override
    public R workEnd(Integer id,String phonePrefix,String phone) {

        WorkClock byId = this.getById(id);
        if(byId.getToWorkStatus().equals(0)){
            return R.failed("請先進行上班打卡！");
        }

        byId.setOffWorkStatus(1);
        byId.setOffWorkTime(LocalDateTime.now());
        /*byId.setWorkStatus(2);*/
        this.updateById(byId);

        QueryWrapper<EmployeesDetails> qw = new QueryWrapper<>();
        qw.eq("user_id",TokenUtils.getCurrentUserId());
        EmployeesDetails one = employeesDetailsService.getOne(qw);
        //发送短信
        String[] params = new String[]{one.getName()};
        SendMessage.sendWorkEndMessage(phonePrefix, phone, params);
        return R.ok("成功發送短信");
    }

    @Override
    public WorkClock getByWorkId(Integer id) {
        QueryWrapper<WorkClock> qw = new QueryWrapper<>();
        qw.eq("work_id",id);
        return this.getOne(qw);
    }

    @Override
    public R uploadSummary(WorkClockDTO workClockDTO) {
        WorkClock byId = this.getById(workClockDTO.getId());
        byId.setStaffSummary(workClockDTO.getStaffSummary());
        byId.setStaffPhoto(workClockDTO.getStaffPhoto());
        this.updateById(byId);
        return R.ok("提交成功");
    }

    @Override
    public R customerEvaluation(CustomerEvaluationDTO customerEvaluationDTO) {
        WorkClock byId = this.getById(customerEvaluationDTO.getId());
        byId.setCustomerStarRating(customerEvaluationDTO.getCustomerStarRating());
        byId.setCustomerPhoto(customerEvaluationDTO.getCustomerPhoto());
        byId.setCustomerEvaluation(customerEvaluationDTO.getCustomerEvaluation());
        this.updateById(byId);
        return R.ok("提交成功");
    }

    @Override
    public R uploadPhoto(MultipartFile file, Integer id, Integer sort) {
        String res = "";

        LocalDateTime now = LocalDateTime.now();
        String nowString = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String catalogue = CommonConstants.HK_IM_PHOTO_ABSTRACT_PATH_PREFIX_PROV;
        String type = file.getOriginalFilename().split("\\.")[1];
        String fileAbstractPath = catalogue + "/" + nowString+"."+ type;

        try {
            ossClient.putObject(bucketName, fileAbstractPath, new ByteArrayInputStream(file.getBytes()));
            res = urlPrefix + fileAbstractPath;
        } catch (Exception e) {
            e.printStackTrace();
            return R.failed("error upload");
        }
        WorkClock byId = this.getById(id);
        if(sort ==1){
            byId.setPhoto1(res);
        }
        if(sort ==2){
            byId.setPhoto2(res);
        }
        if(sort ==3){
            byId.setPhoto3(res);
        }
        if(sort ==4){
            byId.setPhoto4(res);
        }
        if(sort ==5){
            byId.setPhoto5(res);
        }
        this.updateById(byId);
        return R.ok("上傳成功");
    }

    @Override
    public R workCheck(WorkCheckVO workCheckVO) {
        WorkClock byId = this.getById(workCheckVO.getId());

        List<String> workCheckList = workCheckVO.getStaffCheck();
        if(CollectionUtils.isEmpty(workCheckList)){
            return R.ok(null,null);
        }
        StringBuilder sb = new StringBuilder();
        workCheckList.forEach(x -> {
            sb.append(x).append(" ");
        });
        byId.setStaffCheck(sb.toString().trim());
        this.updateById(byId);
        return R.ok(null,null);
    }

    @Override
    public R customerConfirm(Integer id) {
        WorkClock byId = this.getById(id);
        byId.setCustomerConfirm(true);
        byId.setWorkStatus(2);
        this.updateById(byId);

        //開始的工作是訂單的最後一天
        Long number = workDetailsService.getById(byId.getWorkId()).getNumber();
        QueryWrapper<WorkDetails> qw = new QueryWrapper<>();
        qw.eq("number",number);
        List<WorkDetails> list = workDetailsService.list(qw);
        if(CollectionUtils.isEmpty(list)){
            return R.failed("工作不存在");
        }
        WorkDetails workDetails = list.get(list.size() - 1);
        if(byId.getWorkId().equals(workDetails.getId())){
            QueryWrapper qw2 = new QueryWrapper();
            qw2.eq("number", number);
            OrderDetails od = orderDetailsService.getOne(qw2);

            //订单佣金分配
            EmployeesDetails employeesDetails = employeesDetailsService.getById(od.getEmployeesId());
            CustomerDetails customerDetails = customerDetailsService.getById(od.getCustomerId());

            //佣金
            BigDecimal bonus = od.getBonus();

            //员工被邀请
            QueryWrapper<Invitation> qw3 = new QueryWrapper<>();
            qw3.eq("invitees",employeesDetails.getUserId());
            Invitation emp = invitationService.getOne(qw3);
            if(emp!=null){
                User byId1 = userService.getById(emp.getInvitee());
                //记录被邀请人佣金
                emp.setBonus(emp.getBonus().add(bonus));
                invitationService.updateById(emp);
                //邀请人加佣金
                byId1.setBonus(byId1.getBonus().add(bonus));
                userService.updateById(byId1);
                //判断如果邀请人是员工或经理,给公司加佣金
                if(byId1.getDeptId().equals(4)){
                    ManagerDetails byUser = managerDetailsService.getByUser(byId1.getId());
                    CompanyDetails byId2 = companyDetailsService.getById(byUser.getCompanyId());
                    User byId3 = userService.getById(byId2.getUserId());
                    byId3.setBonus(byId3.getBonus().add(bonus));
                    userService.updateById(byId3);
                }
                if(byId1.getDeptId().equals(5)){
                    EmployeesDetails byUser = employeesDetailsService.getByUserId(byId1.getId());
                    CompanyDetails byId2 = companyDetailsService.getById(byUser.getCompanyId());
                    User byId3 = userService.getById(byId2.getUserId());
                    byId3.setBonus(byId3.getBonus().add(bonus));
                    userService.updateById(byId3);
                }
            }

            //客户被邀请
            QueryWrapper<Invitation> qw4 = new QueryWrapper<>();
            qw4.eq("invitees",customerDetails.getUserId());
            Invitation cus = invitationService.getOne(qw4);
            if(cus!=null){
                User byId1 = userService.getById(cus.getInvitee());
                //记录被邀请人佣金
                cus.setBonus(cus.getBonus().add(bonus));
                invitationService.updateById(cus);
                //邀请人加佣金
                byId1.setBonus(byId1.getBonus().add(bonus));
                userService.updateById(byId1);
                //判断如果邀请人是员工或经理,给公司加佣金
                if(byId1.getDeptId().equals(4)){
                    ManagerDetails byUser = managerDetailsService.getByUser(byId1.getId());
                    CompanyDetails byId2 = companyDetailsService.getById(byUser.getCompanyId());
                    User byId3 = userService.getById(byId2.getUserId());
                    byId3.setBonus(byId3.getBonus().add(bonus));
                    userService.updateById(byId3);
                }
                if(byId1.getDeptId().equals(5)){
                    EmployeesDetails byUser = employeesDetailsService.getByUserId(byId1.getId());
                    CompanyDetails byId2 = companyDetailsService.getById(byUser.getCompanyId());
                    User byId3 = userService.getById(byId2.getUserId());
                    byId3.setBonus(byId3.getBonus().add(bonus));
                    userService.updateById(byId3);
                }
            }


            /* 开始修改数据 修改订单状态和完成时间 */
            LocalDateTime now = LocalDateTime.now();
            orderDetailsMapper.statusAndTime(number.toString(), CommonConstants.ORDER_STATE_TO_BE_EVALUATED, now);
            /* 生成空的评价记录 */
            orderDetailsMapper.insertEvaluation(number.toString());
            /* 生成七天自动评价消息 */
            //TODO 生成七天自动评价消息
            String delaySeconds = sysConfigService.getAutomaticEvaluationTime();
            String seqId = UUID.randomUUID().toString();
            String channel1 = CommonConstants.MESSAGE_CHANNEL_CUSTOMER;
            String channel2 = CommonConstants.MESSAGE_CHANNEL_EMPLOYEES;
            String body = number.toString();
            Long delayTime = Long.valueOf(delaySeconds) * 1000 * 60; //延时时间，分钟
            Long nowMilliSecond = LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
            Long deadLine = nowMilliSecond + delayTime;   //一分钟后的时间戳
            Message message1 = new Message(seqId, channel1, body, deadLine, LocalDateTime.now()); //客户 自动评价订单的延时消息
            Message message2 = new Message(seqId, channel2, body, deadLine, LocalDateTime.now()); //保洁员 自动评价订单的延时消息

            delayingQueueService.push(message1);
            delayingQueueService.push(message2);
        }
        return R.ok(null,"确认成功");
    }

    @Override
    public R uploadPhotos(UploadPhotoVO uploadPhotoVO) {
        WorkClock byId = this.getById(uploadPhotoVO.getId());
        byId.setStaffPhoto(uploadPhotoVO.getStaffPhoto());
        this.updateById(byId);
        return R.ok("上傳成功");
    }
}
