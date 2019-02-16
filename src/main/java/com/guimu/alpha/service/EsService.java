package com.guimu.alpha.service;

import com.guimu.alpha.model.LogEsBase;
import java.util.List;

/**
 * @Description: es 操作接口
 * @Author: Guimu
 * @Create: 2019/02/16 09:50:23
 **/

public interface EsService {

    boolean addOne(LogEsBase source);

    boolean clearUserType(LogEsBase condition);

    List<LogEsBase> getAllByBase(LogEsBase condition);
}
