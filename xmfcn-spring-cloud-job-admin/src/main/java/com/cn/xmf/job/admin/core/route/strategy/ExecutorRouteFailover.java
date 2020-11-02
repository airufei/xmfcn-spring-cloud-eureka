package com.cn.xmf.job.admin.core.route.strategy;

import com.cn.xmf.base.model.ResultCodeMessage;
import com.cn.xmf.job.admin.core.route.ExecutorRouter;
import com.cn.xmf.job.admin.core.schedule.XxlJobDynamicScheduler;
import com.cn.xmf.job.admin.core.util.I18nUtil;
import com.cn.xmf.job.core.biz.ExecutorBiz;
import com.cn.xmf.job.core.biz.model.ReturnT;
import com.cn.xmf.job.core.biz.model.TriggerParam;

import java.util.List;

/**
 * Created by xuxueli on 17/3/10.
 */
public class ExecutorRouteFailover extends ExecutorRouter {

    @Override
    public ReturnT<String> route(TriggerParam triggerParam, List<String> addressList) {

        StringBuffer beatResultSB = new StringBuffer();
        for (String address : addressList) {
            // beat
            ReturnT<String> beatResult = null;
            try {
                ExecutorBiz executorBiz = XxlJobDynamicScheduler.getExecutorBiz(address);
                beatResult = executorBiz.beat();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                beatResult = new ReturnT<String>(ResultCodeMessage.FAILURE, "" + e);
            }
            beatResultSB.append((beatResultSB.length() > 0) ? "<br><br>" : "")
                    .append(I18nUtil.getString("jobconf_beat") + "：")
                    .append("<br>address：").append(address)
                    .append("<br>code：").append(beatResult.getCode())
                    .append("<br>msg：").append(beatResult.getMsg());

            // beat success
            if (beatResult.getCode() == ResultCodeMessage.SUCCESS) {

                beatResult.setMsg(beatResultSB.toString());
                beatResult.setContent(address);
                return beatResult;
            }
        }
        return new ReturnT<String>(ResultCodeMessage.FAILURE, beatResultSB.toString());

    }
}
