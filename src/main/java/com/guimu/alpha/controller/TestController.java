package com.guimu.alpha.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 测试controller
 * @Author: Guimu
 * @Create: 2019/02/14 20:18:27
 **/

@RestController
public class TestController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "alpha")
    public String alHe(@RequestParam String name) {
        logger.info("alpha" + name);
//        Object object = null;
//        try {
//            String io = object.toString();
//        } catch (NullPointerException e) {
//            logger.error("空指针异常,A:{}B:{}",123,457, e);
//            return null;
//        }
//        for (int i = 0; i < 30; i++) {
//            logger.info(wrapLog(i));
//        }
        return "alpha" + name;
    }

}
