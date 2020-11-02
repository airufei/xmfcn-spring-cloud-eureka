package com.cn.xmf.job.admin.music.rpc;

import com.alibaba.fastjson.JSONObject;
import com.cn.xmf.base.model.Partion;
import com.cn.xmf.model.wx.Music;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Service(微信音乐)
 *
 * @author rufei.cn
 * @version 2019-10-21
 */
@SuppressWarnings("all")
// 需要根据调用服务来进行修改
@FeignClient(value = "${base-service.wechat-service}")// 配置远程服务名以及自定义权限验证配置
@RequestMapping("/server/music/")// 配置远程服务路径
public interface MusicService {

    /**
     * list:(查询微信音乐 带分页数据)
     *
     * @return
     * @Author rufei.cn
     */
    @RequestMapping(value = "getList", consumes = MediaType.APPLICATION_JSON_VALUE)
    Partion getList(@RequestBody JSONObject map);


    /**
     * getMusicList:(查询微信音乐 不带分页数据)
     *
     * @return
     * @Author rufei.cn
     */
    @RequestMapping(value = "getMusicList", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<Music> getMusicList(@RequestBody Music music);


    /**
     * getMusic:(查询微信音乐单个实体数据)
     *
     * @return
     * @Author rufei.cn
     */
    @RequestMapping(value = "getMusic", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Music getMusic(@RequestBody Music music);


    /**
     * save:(保存微信音乐数据)
     *
     * @return
     * @Author rufei.cn
     */
    @RequestMapping(value = "save", consumes = MediaType.APPLICATION_JSON_VALUE)
    Music save(@RequestBody Music music);


    /**
     * delete:(删除微信音乐数据)
     *
     * @return
     * @Author rufei.cn
     */
    @RequestMapping(value = "delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    boolean delete(@RequestParam("id") Long id);

}