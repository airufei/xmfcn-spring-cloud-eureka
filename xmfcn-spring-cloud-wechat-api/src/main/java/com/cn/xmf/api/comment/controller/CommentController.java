package com.cn.xmf.api.comment.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.xmf.api.comment.rpc.CommentService;
import com.cn.xmf.api.comment.service.CommentHelperService;
import com.cn.xmf.api.common.SysCommonService;
import com.cn.xmf.base.model.Partion;
import com.cn.xmf.base.model.ResultCodeMessage;
import com.cn.xmf.base.model.RetData;
import com.cn.xmf.model.wx.Comment;
import com.cn.xmf.model.wx.CommentDomm;
import com.cn.xmf.util.ConstantUtil;
import com.cn.xmf.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * WxUserMessageController(微信留言)
 * Controller 层的异常应该统一捕获进行处理，这样业务代码更加清晰
 *
 * @author rufei.cn
 * @version 2019-10-15
 */
@RestController
@RequestMapping(value = "/msg")
@SuppressWarnings("all")
public class CommentController {

    private static Logger logger = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentHelperService commentHelperService;
    @Autowired
    private SysCommonService sysCommonService;


    /**
     * getList:(获取微信留言分页查询接口)
     *
     * @param request
     * @param parms
     * @return
     * @Author rufei.cn
     */
    @RequestMapping("isShow")
    public RetData isShow(HttpServletRequest request) {
        RetData retData = new RetData();
        String isShow = sysCommonService.getDictValue(ConstantUtil.DICT_TYPE_BASE_CONFIG, "btn_comment_is_show");
        boolean toBoolean = StringUtil.stringToBoolean(isShow);
        if (toBoolean) {
            retData.setCode(ResultCodeMessage.SUCCESS);
            retData.setMessage(ResultCodeMessage.SUCCESS_MESSAGE);
        }
        return retData;
    }

    /**
     * getList:(获取微信留言分页查询接口)
     *
     * @param request
     * @param parms
     * @return
     * @Author rufei.cn
     */
    @RequestMapping("getList")
    public RetData getList(HttpServletRequest request) {
        RetData retData = new RetData();
        String pageNoStr = request.getParameter("pageNo");
        String openId = request.getParameter("openId");
        String type = request.getParameter("type");
        String bizId = request.getParameter("bizId");
        int pageSize = 10;
        int pageNo = 1;
        if (pageNo > 50) {
            pageNo = 50;
        }
        if (StringUtil.isNotBlank(pageNoStr)) {
            pageNo = StringUtil.stringToInt(pageNoStr);
        }
        JSONObject param = StringUtil.getPageJSONObject(pageNo, pageSize);
        param.put("openId", openId);
        param.put("type", type);
        param.put("bizId", bizId);
        logger.info("getList:(获取微信留言分页查询接口) 开始  param={}", param);
        String key = "getCommentDommList_" + bizId + pageNo + pageSize + type;
        String cache = sysCommonService.getCache(key);
        if (StringUtil.isNotBlank(cache)) {
            JSONObject jsonObject = JSONObject.parseObject(cache);
            retData.setData(jsonObject);
            retData.setCode(ResultCodeMessage.SUCCESS);
            retData.setMessage(ResultCodeMessage.SUCCESS_MESSAGE);
            return retData;
        }
        Partion pt = commentService.getList(param);
        List<Comment> list = null;
        long totalCount = 0;
        if (pt != null) {
            list = (List<Comment>) pt.getList();
            totalCount = pt.getTotalCount();
        }
        String dommIsOpen = sysCommonService.getDictValue(ConstantUtil.DICT_TYPE_BASE_CONFIG, "domm_is_open");
        boolean domm_is_open = StringUtil.stringToBoolean(dommIsOpen);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("totalCount", totalCount);
        jsonObject.put("open", domm_is_open);
        retData.setData(jsonObject);
        String listStr = JSON.toJSONString(list);
        list = JSONObject.parseArray(listStr, Comment.class);
        if (list == null || list.size() <= 0) {
            retData.setCode(ResultCodeMessage.NO_DATA);
            retData.setMessage(ResultCodeMessage.NO_DATA_MESSAGE);
            return retData;
        }
        List<CommentDomm> newList = null;
        if (domm_is_open) {
            newList = commentHelperService.getCommentDommList(list, param);
            jsonObject.put("list", newList);
        } else {
            jsonObject.put("list", list);
        }
        if (jsonObject.size() > 0) {
            sysCommonService.save(key, jsonObject.toString(), 60 * 5);
        }
        retData.setData(jsonObject);
        retData.setCode(ResultCodeMessage.SUCCESS);
        retData.setMessage(ResultCodeMessage.SUCCESS_MESSAGE);
        logger.info("getList:(获取微信留言分页查询接口) 结束");
        return retData;
    }

    /**
     * save:(保存微信留言数据接口)
     *
     * @param request
     * @param parms
     * @return
     * @Author rufei.cn
     */
    @RequestMapping(value = "save")
    public RetData save(HttpServletRequest request) {
        RetData retData = new RetData();
        Comment comment = new Comment();
        String openId = request.getParameter("openId");
        String type = request.getParameter("type");
        String content = request.getParameter("content");
        String photoUrl = request.getParameter("photoUrl");
        String remark = request.getParameter("remark");
        String nickName = request.getParameter("nickName");
        String bizId = request.getParameter("bizId");
        String key = ConstantUtil.CACHE_SYS_BASE_DATA_ + "comment_limit" + openId;
        String cache = sysCommonService.getCache(key);
        if (StringUtil.isNotBlank(cache)) {
            retData.setCode(ResultCodeMessage.PARMS_ERROR);
            retData.setMessage("提交过于频繁，请稍等再试");
            return retData;
        }
        if (StringUtil.isBlank(bizId)) {
            bizId = StringUtil.getUuId();
        }
        if (StringUtil.isBlank(type)) {
            type = "common_comment";
        }
        if (StringUtil.isBlank(content)) {
            retData.setCode(ResultCodeMessage.PARMS_ERROR);
            retData.setMessage("不好意思，留言信息不能为空");
            return retData;
        }
        if (StringUtil.isBlank(openId) || "undefined".equals(openId)) {
            retData.setCode(ResultCodeMessage.PARMS_ERROR);
            retData.setMessage("不好意思，请先登录");
            return retData;
        }
        if (content.length() > 120) {
            retData.setCode(ResultCodeMessage.PARMS_ERROR);
            retData.setMessage("太长了,可以简短一点，谢谢。");
            return retData;
        }
        content = StringUtil.stringFilter(content);
        boolean checkContent = sysCommonService.checkContent(content, openId);
        if (!checkContent) {
            retData.setCode(ResultCodeMessage.PARMS_ERROR);
            retData.setMessage("评论内容含有违法违规内容");
            return retData;
        }
        comment.setOpenId(openId);
        comment.setType(type);
        comment.setContent(content);
        comment.setPhotoUrl(photoUrl);
        comment.setRemark(remark);
        comment.setNickName(nickName);
        comment.setBizId(bizId);
        logger.info("save:(保存微信留言数据接口) 开始  comment={}", comment);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        // 保存数据库
        Comment ret = commentService.save(comment);
        if (ret != null) {
            sysCommonService.save(key, "has_save", 5);
            retData.setCode(ResultCodeMessage.SUCCESS);
            retData.setMessage(ResultCodeMessage.SUCCESS_MESSAGE);
        }
        logger.info("save:(保存微信留言数据接口) 结束");
        return retData;
    }
}