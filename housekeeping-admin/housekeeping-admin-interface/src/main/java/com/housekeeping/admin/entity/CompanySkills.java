package com.housekeeping.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("company_skills")
public class CompanySkills extends Model<CompanySkills> {

    @TableId(type= IdType.AUTO)
    private Integer id;

    private Integer companyId;

    private String presetJobIds;

    private String jobPrice;
}

