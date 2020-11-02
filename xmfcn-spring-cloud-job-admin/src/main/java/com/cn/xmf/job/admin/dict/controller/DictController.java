package com.cn.xmf.job.admin.dict.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.xmf.base.model.Partion;
import com.cn.xmf.base.model.ResultCodeMessage;
import com.cn.xmf.job.admin.sys.DictService;
import com.cn.xmf.job.core.biz.model.ReturnT;
import com.cn.xmf.model.dict.Dict;
import com.cn.xmf.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
@RequestMapping("/dict")
public class DictController {
    @Autowired
    private DictService dictService;
    private static Logger logger = LoggerFactory.getLogger(DictController.class);

    @RequestMapping
    public String index() {
        return "dict/dict.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(HttpServletRequest request) {
        String dictKey = request.getParameter("dictKey");
        String type = request.getParameter("type");
        String value = request.getParameter("value");
        String remark = request.getParameter("remark");
        String startStr = request.getParameter("start");
        String length = request.getParameter("length");
        int pageSize = 10;
        int pageNo = 1;
        int start = 0;
        if (StringUtil.isNotBlank(startStr)) {
            start = StringUtil.stringToInt(startStr);
        }
        if (StringUtil.isNotBlank(length)) {
            pageSize = StringUtil.stringToInt(length);
        }
        if (start > 0) {
            pageNo = (start / pageSize) + 1;
        }
        if (StringUtil.isBlank(dictKey)) {
            dictKey = "";
        }
        if (StringUtil.isBlank(type)) {
            type = "";
        }
        if (StringUtil.isBlank(remark)) {
            remark = "";
        }
        JSONObject object = StringUtil.getPageJSONObject(pageNo, pageSize);
        object.put("dictKey", dictKey);
        object.put("type", type);
        object.put("dictValue", value);
        object.put("remark", remark);
        Partion partion = dictService.list(object);
        List<Dict> list = null;
        int totalCount = 0;
        if (partion != null) {
            list = (List<Dict>) partion.getList();
            totalCount = partion.getTotalCount();
        }
        Map<String, Object> maps = new HashMap<String, Object>();
        maps.put("recordsTotal", totalCount);        // 总记录数
        maps.put("recordsFiltered", totalCount);    // 过滤后的总记录数
        maps.put("data", list);                    // 分页列表
        return maps;
    }

    @RequestMapping("/save")
    @ResponseBody
    public ReturnT<String> save(Dict dict) {
        logger.info("save 开始============>" + JSON.toJSONString(dict));
        ReturnT<String> retData = new ReturnT<>(ResultCodeMessage.FAILURE, "保存数据失败");
        if (dict == null) {
            retData.setMsg(ResultCodeMessage.PARMS_ERROR_MESSAGE);
            return retData;
        }
        String dictValue = dict.getDictValue();
        String dictKey = dict.getDictKey();
        String type = dict.getType();
        String remark = dict.getRemark();
        if (StringUtil.isBlank(dictValue)) {
            retData.setMsg("值不能为空");
            return retData;
        }
        if (StringUtil.isBlank(dictKey)) {
            retData.setMsg("键不能为空");
            return retData;
        }
        if (StringUtil.isBlank(type)) {
            retData.setMsg("类型不能为空");
            return retData;
        }
        if (StringUtil.isBlank(remark)) {
            retData.setMsg("备注不能为空");
            return retData;
        }
        dict.setCreateTime(new Date());
        dict.setUpdateTime(new Date());
        Dict data = dictService.save(dict);
        if (data == null) {
            retData.setMsg(ResultCodeMessage.PARMS_ERROR_MESSAGE);
            return retData;
        }
        retData.setCode(ResultCodeMessage.SUCCESS);
        retData.setMsg(ResultCodeMessage.SUCCESS_MESSAGE);
        logger.info("save 结束============>" + JSON.toJSONString(retData));
        return retData;
    }

    @RequestMapping("/delete")
    @ResponseBody
    public ReturnT<String> delete(Integer id) {
        logger.info("delete 开始============>" + id);
        ReturnT<String> retData = new ReturnT<>(ResultCodeMessage.FAILURE, "删除失败");
        if (id == null || id <= 0) {
            retData.setMsg(ResultCodeMessage.PARMS_ERROR_MESSAGE);
            return retData;
        }
        long newId = id;
        boolean delete = dictService.delete(newId);
        if (delete) {
            retData.setCode(ResultCodeMessage.SUCCESS);
            retData.setMsg(ResultCodeMessage.SUCCESS_MESSAGE);
        }
        logger.info("delete 结束============>" + JSON.toJSONString(retData));
        return retData;
    }
}
