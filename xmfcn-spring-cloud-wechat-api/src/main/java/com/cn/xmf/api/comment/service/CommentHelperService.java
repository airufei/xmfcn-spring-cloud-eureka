package com.cn.xmf.api.comment.service;

import com.alibaba.fastjson.JSONObject;
import com.cn.xmf.model.wx.Comment;
import com.cn.xmf.model.wx.CommentDomm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 辅助类
 */
@Service
@SuppressWarnings("all")
public class CommentHelperService {

    private static Logger logger = LoggerFactory.getLogger(CommentHelperService.class);

    /**
     * 数据转换
     *
     * @param list
     * @return
     */
    public List<CommentDomm> getCommentDommList(List<Comment> list, JSONObject param) {
        List<CommentDomm> newList = new ArrayList<>();
        if (list == null || list.size() <= 0) {
            return newList;
        }
        if (param == null || param.size() <= 0) {
            return newList;
        }
        for (Comment item : list) {
            CommentDomm domm = new CommentDomm();
            domm.setId(item.getId());
            domm.setBizId(item.getBizId());
            domm.setOpenId(item.getOpenId());
            domm.setContent(item.getContent());
            domm.setPhotoUrl(item.getPhotoUrl());
            domm.setNickName(item.getNickName());
            domm.setCreatetimestr(item.getCreatetimestr());
            newList.add(domm);
        }
        return newList;
    }
}
