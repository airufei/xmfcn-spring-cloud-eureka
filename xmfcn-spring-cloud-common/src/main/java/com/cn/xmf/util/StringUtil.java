package com.cn.xmf.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.xmf.model.ding.MarkdownMessage;
import com.cn.xmf.model.es.LogMessage;
import com.cn.xmf.model.log.EsLogMessage;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
public class StringUtil extends StringUtils {
    private static Logger logger = LoggerFactory.getLogger(StringUtil.class);
    private static final char SEPARATOR = '_';

    public static boolean isBlank(final CharSequence cs) {
        if ("null".equals(StringUtils.trim(String.valueOf(cs)))) {
            return true;
        }
        return StringUtils.isBlank(cs);
    }

    public static boolean isNotBlank(final CharSequence cs) {
        if ("null".equals(StringUtils.trim(String.valueOf(cs)))) {
            return false;
        }

        return StringUtils.isNotBlank(cs);
    }

    public static boolean isNotEmpty(final CharSequence cs) {
        if ("null".equals(StringUtils.trim(String.valueOf(cs)))) {
            return false;
        }
        return StringUtils.isNotEmpty(cs);
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase(" hello_world ") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * stringToFloat:(String转float 默认值0)
     *
     * @param
     * @return
     * @author rufei.cn
     * @Date 2017/11/23 15:19
     **/
    public static float stringToFloat(String s) {
        float result = 0L;
        if (isBlank(s)) {
            return result;
        }
        try {
            return Float.parseFloat(s);
        } catch (Exception e) {
            String exceptionMsg = StringUtil.getExceptionMsg(e);
            logger.error(exceptionMsg);
        }
        return result;
    }

    /**
     * stringToDouble:(String 转double 默认值0)
     *
     * @param
     * @return
     * @author rufei.cn
     * @Date 2017/11/23 15:20
     **/
    public static double stringToDouble(String s) {
        double result = 0L;
        try {
            return Double.parseDouble(s);
        } catch (Exception e) {
            String exceptionMsg = StringUtil.getExceptionMsg(e);
            logger.error(exceptionMsg);
        }
        return result;
    }

    /**
     * stringToInt:(String 转int 默认值0)
     *
     * @param
     * @return
     * @author rufei.cn
     * @Date 2017/11/23 15:21
     **/
    public static int stringToInt(String strId) {
        int id = 0;
        try {
            if (isBlank(strId)) {
                return id;
            }
            id = Integer.parseInt(strId);
        } catch (Exception e) {
            String exceptionMsg = StringUtil.getExceptionMsg(e);
            logger.error(exceptionMsg);
        }
        return id;
    }

    /**
     * stringToLong:(String转long，默认值0)
     *
     * @param
     * @return
     * @author rufei.cn
     * @Date 2017/11/23 15:21
     **/
    public static long stringToLong(String strId) {
        long id = 0;
        try {
            if (isBlank(strId)) {
                return id;
            }
            id = Long.parseLong(strId);
        } catch (Exception e) {
            String exceptionMsg = StringUtil.getExceptionMsg(e);
            logger.error(exceptionMsg);
        }
        return id;
    }

    /**
     * obectToString:(Object 转字符串 默认"")
     *
     * @param
     * @return
     * @author rufei.cn
     * @Date 2017/11/23 15:25
     **/
    public static String obectToString(Object obj) {
        String str = "";
        if (obj != null) {
            str = obj.toString();
        }
        return str;
    }
    /**
     * stringToBoolean:(String转boolean 默认false)
     *
     * @param
     * @return
     * @author rufei.cn
     * @Date 2017/11/23 15:30
     **/
    public static boolean stringToBoolean(String s) {
        boolean b = false;
        try {
            if (isBlank(s)) {
                return b;
            }
            b = Boolean.parseBoolean(s);
        } catch (Exception e) {
            String exceptionMsg = StringUtil.getExceptionMsg(e);
            logger.error(exceptionMsg);
        }
        return b;
    }

    /**
     * 字符串转数字
     *
     * @param str 数字字符串
     * @return
     */
    public static int objToInt(Object obj) {
        int result = 0;
        try {
            if (obj != null && obj.toString().length() > 0) {
                result = Integer.parseInt(obj.toString());
            }
        } catch (NumberFormatException e) {
            String exceptionMsg = StringUtil.getExceptionMsg(e);
            logger.error(exceptionMsg);
        }
        return result;

    }


    /**
     * getFileExt:(获取文件名后缀 默认值 "")
     *
     * @param
     * @return
     * @author rufei.cn
     * @Date 2017/11/23 15:26
     **/
    public static String getFileExt(String filePath) {
        String fileExt = "";
        if (isBlank(filePath)) {
            return fileExt;
        }
        String path = filePath.trim();
        if (isBlank(path)) {
            return fileExt;
        }
        return path.substring(path.lastIndexOf('.') + 1, path.length());
    }


    /**
     * isNumber:(判断字符是否为数字)
     *
     * @param
     * @return
     * @author rufei.cn
     * @Date 2017/11/23 15:31
     **/
    public static boolean isNumber(String str) {
        if (str.matches("\\d*")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * isMobilePhone :(判断字符是否为手机号)
     *
     * @param
     * @return
     * @author rufei.cn
     * @Date 2017/11/23 15:32
     **/
    public static boolean isMobilePhone(String s) {
        boolean result = false;
        if (StringUtil.isBlank(s)) {
            return result;
        }
        String mobile = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
        try {
            Pattern pattern = Pattern.compile(mobile);
            Matcher matcher = pattern.matcher(s);
            result = matcher.matches();
        } catch (Exception e) {
            result = false;
            logger.error(StringUtil.getExceptionMsg(e));
        }
        return result;
    }

    /**
     * 是否中文名称
     * @param name
     * @return
     */
    public static boolean isChinese(String str){
        boolean ret=false;
        if(isBlank(str))
        {
            return ret;
        }
        return Pattern.matches("[\\u4e00-\\u9fa5]+",str);
    }
    /**
     * 过滤特殊字符
     *
     * @param str 带特殊字符的字符串
     * @return 过滤之后的字符串（只剩字母、数字、下划线）
     */
    public static String stringFilter(String str) {
        // 只允许字母和数字
        // String regEx = "[^a-zA-Z0-9]";
        // 清除掉所有特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * getURLDecoderString:(URL解码)
     *
     * @param url
     * @param ENCODE
     * @return
     * @author rufei.cn
     */
    public static String getURLDecoderString(String url, String encode) {
        String result = "";
        if (null == url) {
            return "";
        }
        try {
            result = java.net.URLDecoder.decode(url, encode);
        } catch (UnsupportedEncodingException e) {
            String exceptionMsg = StringUtil.getExceptionMsg(e);
            logger.error(exceptionMsg);
        }
        return result;
    }

    /**
     * getSessionStr:(获取session中的字符串)
     *
     * @param session
     * @param key
     * @return
     * @author rufei.cn
     */
    public static String getSessionStr(HttpSession session, String key) {
        String str = "";
        Object obj = session.getAttribute(key);
        if (obj != null) {
            str = obj.toString();
        }
        return str;
    }

    /**
     * getSessionStr:(获取session中的对象)
     *
     * @param session
     * @param key
     * @return
     * @author rufei.cn
     */
    public static String getSessionObj(HttpSession session, String key) {
        String str = "";
        Object obj = session.getAttribute(key);
        if (obj != null) {
            str = obj.toString();
        }
        return str;
    }

    /**
     * getURLEncoderString:(URL编码)
     *
     * @param url
     * @param ENCODE
     * @return
     * @author rufei.cn
     */
    public static String getURLEncoderString(String url, String encode) {
        String result = "";
        if (null == url) {
            return "";
        }
        try {
            result = java.net.URLEncoder.encode(url, encode);
        } catch (UnsupportedEncodingException e) {
            logger.error(StringUtil.getExceptionMsg(e));
        }
        return result;
    }

    /**
     * getSystemUrl:(获取访问服务器的端口号IP)
     * http://localhost:8081/lcApp 或者http://localhost:8081/lcApp
     *
     * @param request
     * @param isHttps 是否https 链接
     * @return
     * @author rufei.cn
     */
    public static String getSystemUrl(HttpServletRequest request) {
        // 用于获取服务器IP 端口号 项目名
        int localPort = request.getServerPort();
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        logger.info("ip/domname:" + scheme + "://" + serverName + ":" + localPort + request.getContextPath());
        String url = "";
        if (localPort == 80) {
            url = scheme + "://" + serverName + request.getContextPath();
        } else {
            url = scheme + "://" + serverName + ":" + localPort
                    + request.getContextPath();
        }
        return url;
    }


    /**
     * getUuId:(获取唯一标识)
     *
     * @return
     * @author rufei.cn
     */
    public static String getUuId() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid;
    }


    /**
     * getQueryParms:(获取分页参数集合)
     *
     * @param page        当前页
     * @param pageSizeStr 每页记录
     * @return
     * @author rufei.cn
     */
    public static Map getQueryParms(String page, String pageSizeStr) {
        Map map = new HashMap();
        int currentCount = StringUtil.objToInt(page);
        int pageSize = StringUtil.objToInt(pageSizeStr);
        if (pageSize < 1) {
            pageSize = 10;
        }
        int startIndex = 0;
        if (currentCount <= 1) {
            currentCount = 1;
            startIndex = 0;
        } else {
            startIndex = (currentCount - 1) * pageSize;
        }
        map.put("startIndex", startIndex);
        map.put("pageSize", pageSize);
        map.put("currentCount", currentCount);
        map.put("sortWay", "desc");
        map.put("sortField", "update_time");
        map.put("flag", 1);
        return map;
    }

    /**
     * StrToDouble:(字符转Double)
     *
     * @param amount
     * @return
     * @author rufei.cn
     */
    public static double StrToDouble(String amount) {
        double result = 0.00;
        try {
            if (StringUtil.isBlank(amount)) {
                return result;
            }
            result = Double.parseDouble(amount);
        } catch (Exception e) {
            logger.error(StringUtil.getExceptionMsg(e));
        }
        return result;
    }

    /**
     * StrToBoolean:(字符转 boolean)
     *
     * @param amount
     * @return
     * @author rufei.cn
     */
    public static boolean strToBoolean(String str) {
        boolean result = false;
        try {
            if (isBlank(str)) {
                return result;
            }
            result = Boolean.parseBoolean(str);
        } catch (Exception e) {
            String exceptionMsg = StringUtil.getExceptionMsg(e);
            logger.error(exceptionMsg);
        }
        return result;
    }


    /**
     * redirect:(重定向)
     *
     * @param response
     * @param url
     * @author rufei.cn
     */
    public static void redirect(HttpServletResponse response, String url) {
        try {
            logger.info("重定向到：url={}", url);
            response.sendRedirect(url);
        } catch (IOException e) {
        }
    }

    /**
     * 创建一个随机数.
     *
     * @return 随机六位数+当前时间yyyyMMddHHmmss
     */
    public static String randomCodeUtil() {
        String cteateTime = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
        int num = RandomUtils.nextInt(100000, 1000000);
        String result = cteateTime + StringUtils.leftPad(num + "", 6, "0");
        return result;
    }


    /**
     * getPageMap:(当前页转化为MYSQL所需要的分页参数)
     *
     * @param pageStr
     * @return
     * @author rufei.cn
     */

    public static Map getPageMap(int currentCount, int pageSize) {
        Map map = new HashMap();
        if (pageSize < 1) {
            pageSize = 10;
        }
        int startIndex = 0;
        if (currentCount <= 1) {
            currentCount = 1;
            startIndex = 0;
        } else {
            startIndex = (currentCount - 1) * pageSize;
        }
        map.put("startIndex", startIndex);
        map.put("pageSize", pageSize);
        map.put("currentCount", currentCount);
        map.put("sortWay", "desc");
        map.put("flag", 1);
        return map;
    }

    /**
     * getPageJSONObject:(当前页转化为MYSQL所需要的分页参数)
     *
     * @param pageStr
     * @return
     * @author rufei.cn
     */

    public static JSONObject getPageJSONObject(int currentCount, int pageSize) {
        JSONObject map = new JSONObject();
        if (pageSize < 1) {
            pageSize = 10;
        }
        int startIndex = 0;
        if (currentCount <= 1) {
            currentCount = 1;
            startIndex = 0;
        } else {
            startIndex = (currentCount - 1) * pageSize;
        }
        map.put("startIndex", startIndex);
        map.put("pageSize", pageSize);
        map.put("currentCount", currentCount);
        map.put("sortWay", "desc");
        map.put("flag", 1);
        return map;
    }

    /**
     * getPageMap:(当前页转化为MYSQL所需要的分页参数--默认每页10条)
     *
     * @param pageStr
     * @return
     * @author rufei.cn
     */

    public static Map getPageMap(int currentCount) {
        Map map = new HashMap();
        int pageSize = 10;
        int startIndex = 0;
        if (currentCount <= 1) {
            currentCount = 1;
            startIndex = 0;
        } else {
            startIndex = (currentCount - 1) * pageSize;
        }
        map.put("startIndex", startIndex);
        map.put("pageSize", pageSize);
        map.put("currentCount", currentCount);
        map.put("sortWay", "desc");
        map.put("flag", 0);
        return map;
    }

    /**
     * 输入流转化成字符串
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String inputStream2String(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i = -1;
        while ((i = is.read()) != -1) {
            baos.write(i);
        }
        return baos.toString();
    }

    /**
     * getExceptionMsg:(根据异常获取关键异常信息字符串)
     *
     * @param throwable
     * @return
     * @author rufei.cn
     */
    public static String getExceptionMsg(Throwable throwable) {
        StringBuilder stackMessage = new StringBuilder();
        StackTraceElement[] stackTrace = throwable.getStackTrace();
        if (stackTrace == null || stackTrace.length < 1) {
            return null;
        }
        int len = stackTrace.length;
        for (int i = 0; i < len; i++) {
            if (i > 6) {
                break;
            }
            String moduleName = moduleName = stackTrace[i].getClassName();
            String methodName = stackTrace[i].getMethodName();
            Class<?> aClass = null;
            try {
                aClass = Class.forName(moduleName);
            } catch (ClassNotFoundException e) {

            }
            int lineNumber = stackTrace[i].getLineNumber();
            String simpleName = null;
            if (aClass != null) {
                simpleName = MarkdownMessage.getLinkText("(" + aClass.getSimpleName() + ".java " + lineNumber + ")", null);
            }
            stackMessage.append(moduleName).append(" ").append(methodName).append(simpleName).append("\n\n");
            if (i == 0) {
                stackMessage.append(MarkdownMessage.getBoldText("错误信息:" + throwable.getMessage())).append("\n\n");
            }
        }
        return stackMessage.toString();
    }

    /**
     * getEsLogData(组织日志信息)
     * @param loggingEvent 日志信息
     * @param subSysName 子系统名称
     * @author rufei.cn
     * @date 2018/2/27 11:21
     */
    public static String getEsLogData(LoggingEvent loggingEvent, String subSysName) {
        String message = loggingEvent.getMessage();
        Level level = loggingEvent.getLevel();
        String loggerName = loggingEvent.getLoggerName();
        StackTraceElement[] callerData = loggingEvent.getCallerData();
        StringBuilder stackMessage = null;//堆栈信息
        String methodName = "";
        if (callerData != null && callerData.length > 0) {
            methodName = callerData[0].getMethodName();
            if (level != null && level == Level.ERROR) {
                stackMessage = new StringBuilder();//堆栈信息
                for (int i = 0; i < callerData.length; i++) {
                    if (i > 10) {
                        break;
                    }
                    stackMessage.append(callerData[i] + "\n");
                }
            }
        }
        EsLogMessage esLog = new EsLogMessage();
        esLog.setSubSysName(subSysName);
        esLog.setModuleName(loggerName);
        esLog.setLevel(level.toString());
        esLog.setMethodName(methodName);
        esLog.setMessage(message);
        if (stackMessage != null && stackMessage.toString().length() > 0) {
            esLog.setStackMessage(stackMessage.toString());
        }
        String jsonString = JSON.toJSONString(esLog);
        return jsonString;
    }

    /**
     * 密码加密
     *
     * @param password
     * @return
     * @throws Exception
     */
    public static String getEncryptPassword(String password) {
        String ret = null;
        try {
            password = M5Util.getMd5(password);
            ret = new StringBuilder(password).reverse().toString();//反转
        } catch (Exception e) {
            ret = null;
            String exceptionMsg = StringUtil.getExceptionMsg(e);
            logger.error(exceptionMsg);
        }
        return ret;
    }

    /**
     * getLogData(组织日志信息)
     * @param loggingEvent 日志信息
     * @param subSysName 子系统名称
     * @author rufei.cn
     * @date 2018/2/27 11:21
     */
    public static String getLogData(LoggingEvent loggingEvent, String subSysName) {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {

        }
        Map<String, String> mdcPropertyMap = loggingEvent.getMDCPropertyMap();
        String ip = addr.getHostAddress().toString(); //获取本机ip
        String hostName = addr.getHostName().toString(); //获取本机计算机名称
        String formattedMessage = loggingEvent.getFormattedMessage();
        Level level = loggingEvent.getLevel();
        String loggerName = loggingEvent.getLoggerName();
        String threadName = loggingEvent.getThreadName();
        Map<String, String> mdcMap = loggingEvent.getMDCPropertyMap();
        StackTraceElement[] callerData = loggingEvent.getCallerData();
        if (isBlank(loggerName)) {
            return null;
        }
        String message = loggerName.replace("\\\\", "\\").replace("\\\\\\", "\\");
        StringBuilder stackMessage = null;//堆栈信息
        String methodName = "";
        String traceId = "";
        String tranceMessage = "";
        if (mdcMap != null) {
            traceId = mdcMap.get("traceId");
            tranceMessage = JSON.toJSONString(mdcMap);//trace 详细数据
        }
        int len = 0;
        if (callerData != null && callerData.length > 0) {
            methodName = callerData[0].getMethodName();
            len = callerData.length;
        }
        stackMessage = new StringBuilder();//堆栈信息
        for (int i = 0; i < len; i++) {
            if (level != null && level != Level.ERROR) {
                continue;
            }
            if (i > 50) {
                break;
            }
            stackMessage.append(callerData[i] + "\n\n");
        }
        if (StringUtil.isBlank(traceId) && mdcMap != null) {
            traceId = mdcMap.get("X-B3-TraceId");
        }
        if (StringUtil.isBlank(traceId)) {
            traceId = "";
        }
        if (StringUtil.isBlank(tranceMessage)) {
            tranceMessage = "";
        }
        if (StringUtil.isBlank(methodName)) {
            methodName = "";
        }
        LogMessage logMessage = new LogMessage();
        logMessage.setSubSysName(subSysName);
        logMessage.setModuleName(loggerName);
        logMessage.setLevel(level.toString());
        logMessage.setMethodName(methodName);
        logMessage.setMessage(message);
        logMessage.setThreadName(threadName);
        logMessage.setSysIp(ip);
        logMessage.setTraceId(traceId);
        logMessage.setTraceMap(tranceMessage);
        if (stackMessage != null && stackMessage.toString().length() > 0) {
            logMessage.setStackMessage(stackMessage.toString());
        }
        String jsonString = JSON.toJSONString(logMessage);
        jsonString = jsonString.replace("\\\\", "\\").replace("\\\\\\", "\\");
        return jsonString;
    }

    /**
     * getRandNum:(生成随机数)
     *
     * @param min 最小值
     * @param max 最大值
     * @return
     * @Author rufei.cn
     */
    public static int getRandNum(int min, int max) {
        Random random = new Random();
        int num = random.nextInt(max);
        if (min > num) {
            num = max - min;
        }
        return num;
    }

    /**
     * 当前线程sleep
     *
     * @param randNum
     */
    public static void threadSleep(long randNum) {
        try {
            Thread.sleep(randNum);
        } catch (InterruptedException e) {
        }
    }

    /**
     * 获取访问者IP
     * <p>
     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
     * <p>
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
     * 如果还不存在则调用Request .getRemoteAddr()。
     *
     * @param request
     * @return
     */
    public String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        } else {
            return request.getRemoteAddr();
        }
    }

    /**
     * 获取系统名称
     *
     * @return
     */
    public static String getSubSysName() {
        String sysSubSysName = "";
        Environment environment = null;
        try {
            environment = (Environment) SpringUtil.getBean("environment");
        } catch (Exception e) {
        }
        if (environment == null) {
            return sysSubSysName;
        }
        sysSubSysName = environment.getProperty("spring.application.name");
        return sysSubSysName;
    }

    /**
     * 微信解密，获取手机号
     * @param key
     * @param iv
     * @param encData
     * @return
     * @throws Exception
     */
    public static String decrypt(byte[] key, byte[] iv, byte[] encData) throws Exception {
    AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
    cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
    //解析解密后的字符串
    return new String(cipher.doFinal(encData),"UTF-8");
  }

    public static void main(String[] args) {
        String password = StringUtil.getEncryptPassword("abc123");
        System.out.println(isChinese(password));
        System.out.println(isChinese("66666"));
        System.out.println(isChinese("张三"));
        System.out.println(isChinese("三奥术大师大所多无"));
    }

}