package com.housekeeping.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.housekeeping.admin.dto.CustomerEvaluationDTO;
import com.housekeeping.admin.dto.WorkClockDTO;
import com.housekeeping.admin.entity.WorkClock;
import com.housekeeping.admin.vo.UploadPhotoVO;
import com.housekeeping.admin.vo.WorkCheckVO;
import com.housekeeping.common.utils.R;
import org.springframework.web.multipart.MultipartFile;


public interface WorkClockService extends IService<WorkClock> {

    R workStart(Integer id,String phonePrefix,String phone);

    R workEnd(Integer id,String phonePrefix,String phone);

    WorkClock getByWorkId(Integer id);

    R uploadSummary(WorkClockDTO workClockDTO);

    R customerEvaluation(CustomerEvaluationDTO customerEvaluationDTO);

    R uploadPhoto(MultipartFile file, Integer id, Integer sort);

    R workCheck(WorkCheckVO workCheckVO);

    R customerConfirm(Integer id);

    R uploadPhotos(UploadPhotoVO uploadPhotoVO);
}
