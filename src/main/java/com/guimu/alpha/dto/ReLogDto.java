package com.guimu.alpha.dto;

import lombok.Data;

/**
 * @Description: 录取日志需要传的参数dto
 * @Author: Guimu
 * @Create: 2019/02/16 18:57:07
 **/
@Data
public class ReLogDto {


    private String batchNo;//日志录制批号,可根据时间戳转换得到
    private String userId;//用户id
    private String levelStr;//字符串表示的日志级别,不区分大小写
}
