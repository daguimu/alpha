package com.guimu.alpha.utils;

import com.guimu.alpha.dto.ReLogDto;
import lombok.Data;

/**
 * @Description:
 * @Author: Guimu
 * @Create: 2019/02/16 14:33:14
 **/
@Data
public class ThreadUtils {

    public static ThreadLocal<ReLogDto> threadLocal = ThreadLocal.withInitial(ReLogDto::new);
}
