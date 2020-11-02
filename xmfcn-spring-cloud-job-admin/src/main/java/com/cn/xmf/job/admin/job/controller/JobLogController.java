package com.cn.xmf.job.admin.job.controller;

import com.cn.xmf.base.model.ResultCodeMessage;
import com.cn.xmf.job.admin.core.model.XxlJobGroup;
import com.cn.xmf.job.admin.core.model.XxlJobInfo;
import com.cn.xmf.job.admin.core.model.XxlJobLog;
import com.cn.xmf.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.cn.xmf.job.admin.core.util.I18nUtil;
import com.cn.xmf.job.admin.job.dao.XxlJobGroupDao;
import com.cn.xmf.job.admin.job.dao.XxlJobInfoDao;
import com.cn.xmf.job.admin.job.dao.XxlJobLogDao;
import com.cn.xmf.job.core.biz.ExecutorBiz;
import com.cn.xmf.job.core.biz.model.LogResult;
import com.cn.xmf.job.core.biz.model.ReturnT;
import com.cn.xmf.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * index controller
 *
 * @author xuxueli 2015-12-19 16:13:16
 */
@Controller
@RequestMapping("/joblog")
public class JobLogController {
    private static Logger logger = LoggerFactory.getLogger(JobLogController.class);

    @Resource
    private XxlJobGroupDao xxlJobGroupDao;
    @Resource
    public XxlJobInfoDao xxlJobInfoDao;
    @Resource
    public XxlJobLogDao xxlJobLogDao;

    @RequestMapping
    public String index(Model model, @RequestParam(required = false, defaultValue = "0") Integer jobId) {

        // 执行器列表
        List<XxlJobGroup> jobGroupList = xxlJobGroupDao.findAll();
        model.addAttribute("JobGroupList", jobGroupList);

        // 任务
        if (jobId > 0) {
            XxlJobInfo jobInfo = xxlJobInfoDao.loadById(jobId);
            model.addAttribute("jobInfo", jobInfo);
        }

        return "joblog/joblog.index";
    }

    @RequestMapping("/getJobsByGroup")
    @ResponseBody
    public ReturnT<List<XxlJobInfo>> getJobsByGroup(int jobGroup) {
        List<XxlJobInfo> list = xxlJobInfoDao.getJobsByGroup(jobGroup);
        return new ReturnT<List<XxlJobInfo>>(list);
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        int logStatus, String filterTime, String executorHandler) {

        // parse param
        Date triggerTimeStart = null;
        Date triggerTimeEnd = null;
        if (StringUtils.isNotBlank(filterTime)) {
            String[] temp = filterTime.split(" - ");
            if (temp.length == 2) {
                try {
                    triggerTimeStart = DateUtils.parseDate(temp[0], "yyyy-MM-dd HH:mm:ss");
                    triggerTimeEnd = DateUtils.parseDate(temp[1], new String[]{"yyyy-MM-dd HH:mm:ss"});
                } catch (ParseException e) {
                }
            }
        }
        int jobGroup = -1;
        int jobId = -1;
        // page query
        List<XxlJobLog> list = xxlJobLogDao.pageList(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, executorHandler, logStatus);
        int listCount = xxlJobLogDao.pageListCount(start, length, jobGroup, jobId, triggerTimeStart, triggerTimeEnd, executorHandler, logStatus);

        // package result
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", listCount);        // 总记录数
        maps.put("recordsFiltered", listCount);    // 过滤后的总记录数
        maps.put("data", list);                    // 分页列表
        return maps;
    }

    @RequestMapping("/logDetailPage")
    public String logDetailPage(Integer id, Model model) {

        // base check
        ReturnT<String> logStatue = ReturnT.SUCCESS;
        XxlJobLog jobLog = xxlJobLogDao.load(id);
        if (jobLog == null) {
            throw new RuntimeException(I18nUtil.getString("joblog_logid_unvalid"));
        }

        model.addAttribute("triggerCode", jobLog.getTriggerCode());
        model.addAttribute("handleCode", jobLog.getHandleCode());
        model.addAttribute("executorAddress", jobLog.getExecutorAddress());
        model.addAttribute("triggerTime", jobLog.getTriggerTime().getTime());
        model.addAttribute("logId", jobLog.getId());
        return "joblog/joblog.detail";
    }

    @RequestMapping("/logDetailCat")
    @ResponseBody
    public ReturnT<LogResult> logDetailCat(String executorAddress, String triggerTime, String logId, Integer fromLineNum) {
        try {
            long triggerTimeLong = 0;
            if (StringUtil.isNotBlank(triggerTime)) {
                triggerTime = triggerTime.replace(",", "");
                triggerTimeLong = StringUtil.stringToLong(triggerTime);
            }
            if (StringUtil.isNotBlank(logId)) {
                logId = logId.trim().replace(",", "");
            }
            int ld = StringUtil.stringToInt(logId);
            ExecutorBiz executorBiz = XxlJobDynamicScheduler.getExecutorBiz(executorAddress);
            ReturnT<LogResult> logResult = executorBiz.log(triggerTimeLong, ld, fromLineNum);

            // is end
            if (logResult.getContent() != null && logResult.getContent().getFromLineNum() > logResult.getContent().getToLineNum()) {
                XxlJobLog jobLog = xxlJobLogDao.load(ld);
                if (jobLog.getHandleCode() > 0) {
                    logResult.getContent().setEnd(true);
                }
            }

            return logResult;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new ReturnT<LogResult>(ResultCodeMessage.FAILURE, e.getMessage());
        }
    }

    @RequestMapping("/logKill")
    @ResponseBody
    public ReturnT<String> logKill(Integer id) {
        // base check
        XxlJobLog log = xxlJobLogDao.load(id);
        XxlJobInfo jobInfo = xxlJobInfoDao.loadById(log.getJobId());
        if (jobInfo == null) {
            return new ReturnT<String>(500, I18nUtil.getString("jobinfo_glue_jobid_unvalid"));
        }
        if (ResultCodeMessage.SUCCESS != log.getTriggerCode()) {
            return new ReturnT<String>(500, I18nUtil.getString("joblog_kill_log_limit"));
        }

        // request of kill
        ReturnT<String> runResult = null;
        try {
            ExecutorBiz executorBiz = XxlJobDynamicScheduler.getExecutorBiz(log.getExecutorAddress());
            runResult = executorBiz.kill(jobInfo.getId());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            runResult = new ReturnT<String>(500, e.getMessage());
        }

        if (ResultCodeMessage.SUCCESS == runResult.getCode()) {
            log.setHandleCode(ResultCodeMessage.FAILURE);
            log.setHandleMsg(I18nUtil.getString("joblog_kill_log_byman") + ":" + (runResult.getMsg() != null ? runResult.getMsg() : ""));
            log.setHandleTime(new Date());
            xxlJobLogDao.updateHandleInfo(log);
            return new ReturnT<String>(runResult.getMsg());
        } else {
            return new ReturnT<String>(500, runResult.getMsg());
        }
    }

    @RequestMapping("/clearLog")
    @ResponseBody
    public ReturnT<String> clearLog(Integer jobGroup, Integer jobId, Integer type) {

        Date clearBeforeTime = null;
        int clearBeforeNum = 0;
        if (type == 1) {
            clearBeforeTime = DateUtils.addMonths(new Date(), -1);    // 清理一个月之前日志数据
        } else if (type == 2) {
            clearBeforeTime = DateUtils.addMonths(new Date(), -3);    // 清理三个月之前日志数据
        } else if (type == 3) {
            clearBeforeTime = DateUtils.addMonths(new Date(), -6);    // 清理六个月之前日志数据
        } else if (type == 4) {
            clearBeforeTime = DateUtils.addYears(new Date(), -1);    // 清理一年之前日志数据
        } else if (type == 5) {
            clearBeforeNum = 1000;        // 清理一千条以前日志数据
        } else if (type == 6) {
            clearBeforeNum = 10000;        // 清理一万条以前日志数据
        } else if (type == 7) {
            clearBeforeNum = 30000;        // 清理三万条以前日志数据
        } else if (type == 8) {
            clearBeforeNum = 100000;    // 清理十万条以前日志数据
        } else if (type == 9) {
            clearBeforeNum = 0;            // 清理所有日志数据
        } else {
            return new ReturnT<String>(ResultCodeMessage.FAILURE, I18nUtil.getString("joblog_clean_type_unvalid"));
        }
        xxlJobLogDao.clearLog(jobGroup, jobId, clearBeforeTime, clearBeforeNum);
        return ReturnT.SUCCESS;
    }

}
