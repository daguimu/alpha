package com.guimu.alpha.consts;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @Description: 需要放到线程变量中的keyStr
 * @Author: Guimu
 * @Create: 2019/02/18 17:25:39
 **/
public enum ThreadLocalConstEnum {
    RE_LOG_BATCH_NO(1, "re_batch_no_key");
    private int value;

    private String descStr;

    public boolean checkIn(int value) {
        return Arrays.stream(ThreadLocalConstEnum.values()).filter(el -> el.value == value).collect(
            Collectors.toList()).size() > 0;
    }

    ThreadLocalConstEnum(int value, String descStr) {
        this.value = value;
        this.descStr = descStr;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDescStr() {
        return descStr;
    }

    public void setDescStr(String descStr) {
        this.descStr = descStr;
    }}
