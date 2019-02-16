package com.guimu.alpha.controller;

import com.guimu.alpha.model.LogEsBase;
import com.guimu.alpha.service.EsService;
import com.guimu.alpha.utils.RedisUtils;
import com.guimu.alpha.utils.ThreUtils;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.StringJoiner;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private EsService esService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "alpha")
    public String alHe(@RequestParam String name) {
        logger.info("alpha" + name);
        return "alpha" + name;
    }

    @RequestMapping(value = "es")
    public String gel(@RequestParam String msg, @RequestParam String userId) {
        LogEsBase logEsBase = new LogEsBase();
        logEsBase.setLogMsg(msg);
        logEsBase.setUserId(userId);
        boolean flag = esService.addOne(logEsBase);
        return flag + "";
    }

    @RequestMapping(value = "query")
    public String red(@RequestParam String userId) {
        LogEsBase condition = new LogEsBase();
        condition.setType("LogEsBase");
        condition.setUserId(userId);
        List<LogEsBase> list = esService.getAllByBase(condition);
        StringJoiner joiner = new StringJoiner("<br/>", "", "");
        list.forEach(el -> joiner.add(el.getLogMsg()));
        return joiner.toString();
    }

    @RequestMapping(value = "clear")
    public String clear(@RequestParam String userId) {
        LogEsBase logEsBase = new LogEsBase();
        logEsBase.setType("LogEsBase");
        logEsBase.setUserId(userId);
        esService.clearUserType(logEsBase);
        return "ok";
    }

    @RequestMapping(value = "down")
    public void downloadNet(HttpServletResponse response) {
        String text = this.red(ThreUtils.threadLocal.get());
        if (StringUtils.isEmpty(text)) {
            return;
        }
        // 下载网络文件
        int bytesum = 0;
        int byteread;
        try {
            InputStream inStream = new ByteArrayInputStream(text.getBytes());
            response.setHeader("Content-Disposition",
                "attachment;filename=" + System.currentTimeMillis() + ".log");

            BufferedInputStream bis = new BufferedInputStream(inStream); //创建文件缓冲输入流
            byte[] buffer = new byte[bis.available()];//从输入流中读取不受阻塞
            bis.read(buffer);//读取数据文件
            bis.close();
            OutputStream out = response.getOutputStream();
            out.write(buffer);//输出数据文件
            out.flush();//释放缓存
            out.close();//关闭输出流
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
