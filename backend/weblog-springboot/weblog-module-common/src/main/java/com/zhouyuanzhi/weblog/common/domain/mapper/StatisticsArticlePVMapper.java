package com.zhouyuanzhi.weblog.common.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.zhouyuanzhi.weblog.common.domain.dos.StatisticsArticlePVDO;

import java.time.LocalDate;

/**
 * @author: 周元智
 * @url: www.zhouyuanzhi.com
 * @date: 2025-08-22 17:06
 * @description: 每日文章 PV 访问量统计
 **/
public interface StatisticsArticlePVMapper extends BaseMapper<StatisticsArticlePVDO> {

    /**
     * 对指定日期的文章 PV 访问量进行 +1
     * @param date
     * @return
     */
    default int increasePVCount(LocalDate date) {
        return update(null, Wrappers.<StatisticsArticlePVDO>lambdaUpdate()
                .setSql("pv_count = pv_count + 1")
                .eq(StatisticsArticlePVDO::getPvDate, date));
    }
}
