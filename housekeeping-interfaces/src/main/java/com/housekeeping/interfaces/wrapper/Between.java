package com.housekeeping.interfaces.wrapper;

import lombok.Data;

/**
 * @Author su
 * @Date 2020/12/4 17:09
 */

@Data
public class Between<T> {
    private T bLeft;
    private T bRight;

    public Between(T bLeft, T bRight) {
        this.bLeft = bLeft;
        this.bRight = bRight;
    }
}

