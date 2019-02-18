package com.guimu.alpha.serviceimpl;

import com.guimu.alpha.controller.TestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: Guimu
 * @Create: 2019/02/18 09:49:57
 **/
@Service
public class SleuthService implements ReLogDtoStrConventor {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TestController controller;

    @Async
    public void asyncMethod() throws Exception {
        logger.info("Start Async Method");
        String result = controller.alHe("kili");
        System.out.println(result);
        logger.info("End Async Method");
    }
}
