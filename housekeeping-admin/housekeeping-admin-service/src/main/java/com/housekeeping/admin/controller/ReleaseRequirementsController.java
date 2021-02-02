package com.housekeeping.admin.controller;

import com.housekeeping.admin.dto.ReleaseRequirementADTO;
import com.housekeeping.admin.dto.ReleaseRequirementBDTO;
import com.housekeeping.admin.dto.ReleaseRequirementCDTO;
import com.housekeeping.admin.service.IReleaseRequirementService;
import com.housekeeping.common.utils.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author su
 * @Date 2021/1/5 11:30
 */
@Api(tags={"【需求发布】相关接口"})
@RestController
@AllArgsConstructor
@RequestMapping("/releaseRequirements")
public class ReleaseRequirementsController {

    private final IReleaseRequirementService releaseRequirementService;

}
