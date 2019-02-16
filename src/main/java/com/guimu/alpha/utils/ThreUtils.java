package com.guimu.alpha.utils;

import lombok.Data;

/**
 * @Description:
 * @Author: Guimu
 * @Create: 2019/02/16 14:33:14
 **/
@Data
public class ThreUtils {

   public static ThreadLocal<String> threadLocal = new ThreadLocal<>();
}
