package com.guimu.alpha.controller;

import com.guimu.alpha.model.LogEsBase;
import com.guimu.alpha.service.EsService;
import com.guimu.alpha.serviceimpl.SleuthService;
import com.guimu.alpha.utils.RedisUtils;
import com.guimu.alpha.utils.ThreadUtils;
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
import org.springframework.web.client.RestTemplate;

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
    private RestTemplate restTemplate;
    @Autowired
    private EsService esService;
    @Autowired
    private SleuthService sleuthService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "alpha")
    public String alHe(@RequestParam String name) {
        logger.info("alpha" + name);
        String url = "http://localhost:9900/hello";
        String restlt = restTemplate
            .getForObject(url + "?msg={1}", String.class, "msg param1");
        System.out.println(restlt);
        return "alpha" + name;
    }


    @RequestMapping(value = "/feginc")
    public String feign(@RequestParam String name) {
        return "from alpha feign api:" + name;
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
    public String red(@RequestParam String batchNo, @RequestParam String userId) {
        try {
            Thread.currentThread().sleep(1000);//毫秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogEsBase condition = new LogEsBase();
        condition.setType("LogEsBase");
        condition.setUserId(userId);
        condition.setBatchNo(batchNo);
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

    @RequestMapping(value = "test")
    public String test() throws Exception {
        logger.info("调用异步方法开始");
        sleuthService.asyncMethod();
        logger.info("调用异步方法结束");
        return "it's  ok";
    }

    @RequestMapping(value = "down")
    public void downloadNet(HttpServletResponse response) {
        String text = this.red(ThreadUtils.threadLocal.get().getBatchNo(),
            ThreadUtils.threadLocal.get().getUserId());
        if (StringUtils.isEmpty(text)) {
            return;
        }
        // 下载网络文件
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
