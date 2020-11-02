package com.cn.xmf.service.common;

import com.cn.xmf.base.model.RetData;
import com.cn.xmf.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;


/**
 * 统一异常处理
 * 2018-09-09 15:46
 *
 * @auther airufei
 * @return
 */
@SuppressWarnings("all")
@ControllerAdvice
public class UnifiedException {
    private static Logger logger = LoggerFactory.getLogger(UnifiedException.class);
    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    private SysCommonService sysCommonService;

    @ExceptionHandler({Exception.class, Throwable.class, Error.class, IOException.class, RuntimeException.class})
    @ResponseBody
    RetData handleException(Throwable e, HttpServletRequest request, HttpServletResponse response){
        RetData mobileData  =null;
        dingTalkMessage(request, e);

        return mobileData;
    }

    /**
     * dingTalkMessage:(发送钉钉消息)
     * @author: airufei
     * @date:2018/1/3 18:08
     * @return:
     */
    private void dingTalkMessage(HttpServletRequest request, Throwable throwable) {
        Enumeration<String> enu = request.getParameterNames();
        String requestUrl = request.getRequestURI();
        StringBuilder sb = new StringBuilder();
        while (enu.hasMoreElements()) {
            String paraName = enu.nextElement();
            sb.append(" " + paraName + ":" + request.getParameter(paraName));
        }
        String stackMessage = StringUtil.getExceptionMsg(throwable);
        String url = StringUtil.getSystemUrl(request) + requestUrl;
        sysCommonService.sendDingTalkMessage(url,sb.toString(),stackMessage,null, UnifiedException.class);
        logger.error(stackMessage);
    }
}
