package com.guimu.alpha.model;

import lombok.Data;

/**
 * @Description: es 存储的基类
 * @Author: Guimu
 * @Create: 2019/02/16 09:49:18
 **/
@Data
public class LogEsBase {

    //    private String id;    //es 物理主键
    private String index;//属于哪个索引 类似于库
    private String type;//属于索引中的哪个type 类似于表

    private String userId;//用户id
    private String logMsg;//日志详情
    private String batchNo;//生产批号，表示某一次录制的时间批号
}
