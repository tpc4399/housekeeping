package com.housekeeping.admin.dto;

import com.housekeeping.admin.entity.User;
import com.housekeeping.admin.entity.UserWithdrawal;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserWithdrawalDTO {

    private Integer id;

    private Integer userId;

    private BigDecimal amount;      //提现金额

    private String bank;

    private String collectionAccount;

    private LocalDateTime createTime;

    private Integer status;         //0提现申请中 1提现拒绝 2提现同意 3提现已发放

    private User user;

    public UserWithdrawalDTO() {
    }

    public UserWithdrawalDTO(UserWithdrawal userWithdrawal) {
        this.id = userWithdrawal.getId();
        this.userId = userWithdrawal.getUserId();
        this.amount = userWithdrawal.getAmount();
        this.bank = userWithdrawal.getBank();
        this.collectionAccount = userWithdrawal.getCollectionAccount();
        this.createTime = userWithdrawal.getCreateTime();
        this.status = userWithdrawal.getStatus();
    }
}
