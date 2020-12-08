package com.housekeeping.im.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ImChatGroupUser extends Model<ImChatGroupUser> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 群id
     */
    private Integer chatGroupId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 入群时间
     */
    private LocalDateTime createDate;


}
