package com.gba.client.bo;

import com.gba.common.util.RedisDataBase1Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: liuxudong
 * @Description:
 * @Date: Created in 2024/9/12
 */
@Component
public class StockBO {
    @Autowired
    RedisDataBase1Utils redisDataBase1Utils;

    /**
     * 获取标的明细
     *
     * @return
     */
    public List<String> getStockInfoList() {
        return redisDataBase1Utils.getList("STOCK_BACKUP");
    }

    /**
     * 获取股票信息的映射表。
     *
     * @return 返回一个并发映射表，其中键为股票代码，值为对应的股票名称。
     */
    public Map<String, String> getStockInfoMap() {
        // 将股票信息列表转化为并发映射表，其中每个元素通过逗号分割，第一部分作为键，第二部分作为值
        return getStockInfoList().stream()
                .map(stock -> stock.split(","))
                .collect(Collectors.toConcurrentMap(
                        arr -> arr[0],         // 键映射器：股票代码
                        arr -> arr[1],         // 值映射器：股票名称
                        (existing, replacement) -> existing // 合并函数：保留已存在的值
                ));
    }
}
